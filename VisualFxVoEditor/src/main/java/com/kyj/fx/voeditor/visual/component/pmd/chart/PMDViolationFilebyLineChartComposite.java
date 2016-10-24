/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.pmd
 *	작성일   : 2016. 10. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.pmd.chart;

import java.util.function.Predicate;

import org.controlsfx.control.CheckComboBox;

import com.kyj.fx.voeditor.visual.component.chart.RangeSliderLineChartComposite;
import com.kyj.fx.voeditor.visual.component.pmd.PMDCheckedListComposite;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleViolation;

/**
 * @author KYJ
 *
 */
@SuppressWarnings("rawtypes")
public class PMDViolationFilebyLineChartComposite extends AbstractPMDViolationChartComposite
		implements ChartCustomAction<javafx.scene.chart.XYChart.Data> {

	/**
	 *  ObservableMap에 집계된 데이터를 기반으로 차트로 표현하기 위한 dataList
	 * @최초생성일 2016. 10. 18.
	 */
	private ObservableList<Data<String, Number>> dataList;

	/**
	 * 중복되는 데이터를 제거하기위한 데이터셋
	 * summary
	 * @최초생성일 2016. 10. 18.
	 */
	private ObservableMap<ReuleViolationWrapper, Integer> observableHashMap;

	/**
	 * RangeChart를 표현해주는 뷰
	 * @최초생성일 2016. 10. 18.
	 */
	private RangeSliderLineChartComposite<Data<String, Number>> sliderLineChartComposite = null;
	private PMDCheckedListComposite checkedListComposite;

	public PMDViolationFilebyLineChartComposite(PMDCheckedListComposite checkedListComposite) {
		observableHashMap = FXCollections.observableHashMap();
		this.checkedListComposite = checkedListComposite;
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationChartVisualable#createNode()
	 */
	@Override
	public Node createNode() {

		try {
			sliderLineChartComposite = new RangeSliderLineChartComposite<Data<String, Number>>() {
				@Override
				public Data<String, Number> converter(Data<String, Number> t) {
					return t;
				}

				/* (non-Javadoc)
				 * @see com.kyj.fx.voeditor.visual.component.chart.RangeSliderLineChartComposite#createNewSeries(javafx.collections.ObservableList)
				 */
				@Override
				public Series<String, Number> createNewSeries(String seriesName, ObservableList<Data<String, Number>> subList) {
					return super.createNewSeries("File Names", subList);
				}

				/* (non-Javadoc)
				 * @see com.kyj.fx.voeditor.visual.component.chart.RangeSliderLineChartComposite#createNumberAxis()
				 */
				@Override
				protected NumberAxis createNumberAxis() {

					NumberAxis createNumberAxis = super.createNumberAxis();
					createNumberAxis.setLabel("Violation Count");
					return createNumberAxis;
				}

			};
			dataList = sliderLineChartComposite.getItems();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sliderLineChartComposite;
	}

	/**
	 * Summary
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
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationChartVisualable#violationAdapter()
	 */
	@Override
	public PMDVioationAdapter violationAdapter() {
		return pmdVioationAdapter;
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationChartVisualable#build()
	 */
	@Override
	public void build() {

		//Summary
		observableHashMap.entrySet().forEach(v -> {
			String name = v.getKey().toString();

			Integer value = v.getValue();
			dataList.add(new Data<String, Number>(name, value));
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

//		u.setOnMouseEntered(ev -> {
//			u.setCursor(Cursor.HAND);
//		});
//
//		u.setOnMouseExited(ev -> {
//			u.setCursor(Cursor.DEFAULT);
//		});
//
//		u.setOnMouseClicked(ev -> {
//
//			ListView<RuleViolation> lvViolation = this.checkedListComposite.getLvViolation();
//			ObservableList<RuleViolation> items = lvViolation.getItems();
//			ObservableList<RuleViolation> violationList = this.checkedListComposite.getViolationList();
//			List<RuleViolation> collect = violationList.stream().filter(ruleViolationFilter())
//					.filter(v -> ValueUtil.equals(t.getName(), ValueUtil.getSimpleFileName(v.getFilename()))).collect(Collectors.toList());
//			items.setAll(collect);
//
//		});

	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.ChartCustomAction#chartGraphicsCustomAction(java.lang.Object, javafx.scene.Node)
	 */
	@Override
	public void chartGraphicsCustomAction(Data data, Node node) {
//		node.setOnMouseClicked(ev -> {
//
//			ListView<RuleViolation> lvViolation = this.checkedListComposite.getLvViolation();
//			ObservableList<RuleViolation> violationList = this.checkedListComposite.getViolationList();
//			ObservableList<RuleViolation> items = lvViolation.getItems();
//			List<RuleViolation> collect = violationList.stream().filter(ruleViolationFilter())
//					.filter(v -> ValueUtil.equals(data.getName(), ValueUtil.getSimpleFileName(v.getFilename())))
//					.collect(Collectors.toList());
//			items.setAll(collect);
//		});

	}

}
