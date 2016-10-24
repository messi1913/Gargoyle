/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.pmd
 *	작성일   : 2016. 10. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.pmd.chart;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.controlsfx.control.CheckComboBox;

import com.kyj.fx.voeditor.visual.component.chart.AttachedTextValuePieChart;
import com.kyj.fx.voeditor.visual.component.pmd.PMDCheckedListComposite;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.ListView;
import javafx.util.StringConverter;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleViolation;

/**
 * @author KYJ
 *
 */
public class PMDViolationbyPieChartComposite extends AbstractPMDViolationChartComposite implements ChartCustomAction<PieChart.Data> {

	private AttachedTextValuePieChart picChart;
	private PMDCheckedListComposite checkedListComposite;
	/**
	 * 중복되는 데이터를 제거하기위한 데이터셋 summary
	 *
	 * @최초생성일 2016. 10. 18.
	 */
	private ObservableMap<ReuleViolationWrapper, Integer> observableHashMap;

	private ObservableList<javafx.scene.chart.PieChart.Data> dataList;

	public PMDViolationbyPieChartComposite(PMDCheckedListComposite checkedListComposite) {
		this.checkedListComposite = checkedListComposite;
		observableHashMap = FXCollections.observableHashMap();
	}

	/**
	 * 집계된 총 갯수
	 *
	 * @최초생성일 2016. 10. 19.
	 */
	private IntegerProperty total = new SimpleIntegerProperty();

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationChartVisualable#createNode()
	 */
	@Override
	public Node createNode() {
		picChart = new AttachedTextValuePieChart();
		picChart.setLabelConverter(new StringConverter<PieChart.Data>() {

			@Override
			public String toString(PieChart.Data object) {
				int value = (int) object.getPieValue();
				double percent = (value * 100) / total.get();
				return String.format("%s\ncount : %d\n%.2f%%", object.getName(), value, percent);
			}

			@Override
			public PieChart.Data fromString(String string) {
				// TODO Auto-generated method stub
				return null;
			}

		});

		picChart.setChartGraphicsCustomAction((data, node) -> {
			chartGraphicsCustomAction(data, node);

		});

		dataList = picChart.getData();

		return picChart;
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationChartVisualable#violationAdapter()
	 */
	@Override
	public PMDVioationAdapter violationAdapter() {
		return pmdVioationAdapter;
	}

	/**
	 * Summary
	 *
	 * @최초생성일 2016. 10. 19.
	 */
	private PMDVioationAdapter pmdVioationAdapter = ruleViolation -> {
		ReuleViolationWrapper wrapper = new ReuleViolationWrapper(ruleViolation);

		if (observableHashMap.containsKey(wrapper)) {
			Integer integer = observableHashMap.get(wrapper);
			observableHashMap.put(wrapper, Integer.sum(integer.intValue(), 1));
		} else {
			observableHashMap.put(wrapper, Integer.valueOf(1));
		}
	};

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationChartVisualable#build()
	 */
	@Override
	public void build() {
		Integer size = observableHashMap.values().stream().reduce((a, b) -> a + b).get();
		total.set(size);
		//Summary
		observableHashMap.entrySet().forEach(v -> {
			String name = v.getKey().toString();

			Integer value = v.getValue();
			dataList.add(new Data(name, value));
		});

	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationChartVisualable#clean()
	 */
	@Override
	public void clean() {
		observableHashMap.clear();
		dataList.clear();

	}

	@Override
	public Predicate<RuleViolation> ruleViolationFilter() {

		return ruleViolation -> {
			CheckComboBox<RulePriority> checkComboBox = this.checkedListComposite.getViolationCheckComboBox();
			ObservableList<RulePriority> checkedItems = checkComboBox.getCheckModel().getCheckedItems();
			Rule rule = ruleViolation.getRule();
			RulePriority priority = rule.getPriority();
			return checkedItems.stream().filter(c -> c == priority).findFirst().isPresent();

		};
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.ChartCustomAction#seriesLegendLabelCustomAction(javafx.scene.chart.PieChart.Data, javafx.scene.Node)
	 */
	@Override
	public void seriesLegendLabelCustomAction(Data t, Node u) {
		u.setOnMouseEntered(ev -> {
			u.setCursor(Cursor.HAND);
		});

		u.setOnMouseExited(ev -> {
			u.setCursor(Cursor.DEFAULT);
		});

		u.setOnMouseClicked(ev -> {

			ListView<RuleViolation> lvViolation = this.checkedListComposite.getLvViolation();
			ObservableList<RuleViolation> items = lvViolation.getItems();
			ObservableList<RuleViolation> violationList = this.checkedListComposite.getViolationList();
			List<RuleViolation> collect = violationList.stream().filter(ruleViolationFilter())
					.filter(v -> ValueUtil.equals(t.getName(), ValueUtil.getSimpleFileName(v.getFilename()))).collect(Collectors.toList());
			items.setAll(collect);

		});

	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.ChartCustomAction#chartGraphicsCustomAction(java.lang.Object, javafx.scene.Node)
	 */
	@Override
	public void chartGraphicsCustomAction(Data data, Node node) {
		node.setOnMouseClicked(ev -> {

			ListView<RuleViolation> lvViolation = this.checkedListComposite.getLvViolation();
			ObservableList<RuleViolation> violationList = this.checkedListComposite.getViolationList();
			ObservableList<RuleViolation> items = lvViolation.getItems();
			List<RuleViolation> collect = violationList.stream().filter(ruleViolationFilter())
					.filter(v -> ValueUtil.equals(data.getName(), ValueUtil.getSimpleFileName(v.getFilename())))
					.collect(Collectors.toList());
			items.setAll(collect);
		});

	}

}
