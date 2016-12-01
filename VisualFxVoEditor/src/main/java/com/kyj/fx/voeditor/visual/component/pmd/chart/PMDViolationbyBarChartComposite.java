/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.pmd
 *	작성일   : 2016. 10. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.pmd.chart;

import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;

import org.controlsfx.control.CheckComboBox;

import com.kyj.fx.voeditor.visual.component.pmd.PMDCheckedListComposite;
import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.paint.Color;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleViolation;

/**
 * @author KYJ
 *
 */
@SuppressWarnings("rawtypes")
public class PMDViolationbyBarChartComposite extends AbstractPMDViolationChartComposite
		implements ChartCustomAction<javafx.scene.chart.BarChart.Data> {

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
	private ObservableMap<RulePriority, Integer> observableHashMap;

	/**
	 * RangeChart를 표현해주는 뷰
	 * @최초생성일 2016. 10. 18.
	 */
	private ObjectProperty<BarChart> barChart = new SimpleObjectProperty<>();
	//	private BarChart<String, Number> barChart = null;
	private CategoryAxis xAxis;
	private NumberAxis yAxis;
	private PMDCheckedListComposite checkedListComposite;
	private ObservableList<javafx.scene.chart.XYChart.Series<String, Number>> series;

	public PMDViolationbyBarChartComposite(PMDCheckedListComposite checkedListComposite) {
		super();
		this.checkedListComposite = checkedListComposite;
		observableHashMap = FXCollections.observableMap(new TreeMap<>((o1, o2) -> Integer.compare(o1.getPriority(), o2.getPriority())));
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationChartVisualable#createNode()
	 */
	@Override
	public Node createNode() {
		xAxis = new CategoryAxis();
		xAxis.setAutoRanging(false);
		xAxis.setAnimated(false);

		yAxis = new NumberAxis();
		yAxis.setAnimated(false);
		yAxis.setPrefWidth(60d);
		yAxis.setAutoRanging(true);
		yAxis.setMaxWidth(60d);
		yAxis.setLabel("Violation Count");

		Series<String, Number> dataList = new Series<>(FXCollections.observableArrayList());
		dataList.setName("Priority");

		BarChart<String, Number> c = new BarChart<>(xAxis, yAxis);

		barChart.set(c);

		series = c.getData();

		series.add(0, dataList);
		this.dataList = dataList.getData();

		return c;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 24.
	 * @param barChart2
	 */
	private void style(BarChart<String, Number> chart) {
		Color lineColor = Color.web("#58AD58");
		//		chart-bar series0 data2 default-color0
		String style = "-fx-bar-fill: ".concat(FxUtil.toWebString(lineColor)).concat(";");
		Set<Node> lookupAll = chart.lookupAll(".default-color0");
		for (Node n : lookupAll) {
			//				anotherStyleAction.accept(style);
			n.setStyle(style);
		}

	}

	/**
	 * Summary
	 * @최초생성일 2016. 10. 19.
	 */
	private PMDVioationAdapter pmdVioationAdapter = ruleViolation -> {
		RulePriority priority = ruleViolation.getRule().getPriority();

		if (observableHashMap.containsKey(priority)) {

			Integer integer = observableHashMap.get(priority);
			observableHashMap.put(priority, Integer.sum(integer.intValue(), 1));
		} else {
			observableHashMap.put(priority, Integer.valueOf(1));
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
		style(barChart.get());

	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationChartVisualable#clean()
	 */
	@Override
	public void clean() {
		dataList.clear();
		//		if (barChart.get() != null)
		//			barChart.get().getData().clear();
		//		barChart.getData().clear();
		observableHashMap.clear();

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
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.ChartCustomAction#chartGraphicsCustomAction(java.lang.Object, javafx.scene.Node)
	 */
	@Override
	public void chartGraphicsCustomAction(Data data, Node node) {
	}

}
