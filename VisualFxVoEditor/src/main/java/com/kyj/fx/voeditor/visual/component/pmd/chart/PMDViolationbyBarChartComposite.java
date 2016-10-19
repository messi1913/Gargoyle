/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.pmd
 *	작성일   : 2016. 10. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.pmd.chart;

import java.util.function.BiConsumer;

import com.kyj.fx.voeditor.visual.component.chart.AttachedTextValuePieChart;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.util.StringConverter;

/**
 * @author KYJ
 *
 */
public class PMDViolationbyBarChartComposite extends AbstractPMDViolationBarChartComposite {

	private AttachedTextValuePieChart picChart;

	/**
	 * 중복되는 데이터를 제거하기위한 데이터셋 summary
	 * 
	 * @최초생성일 2016. 10. 18.
	 */
	private ObservableMap<ReuleViolationWrapper, Integer> observableHashMap;

	private ObservableList<javafx.scene.chart.PieChart.Data> dataList;

	/**
	 * 집계된 총 갯수
	 * 
	 * @최초생성일 2016. 10. 19.
	 */
	private IntegerProperty total = new SimpleIntegerProperty();

	public PMDViolationbyBarChartComposite() {
		observableHashMap = FXCollections.observableHashMap();
	}

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
		
		//		picChart.setOnMouseClicked(this::picChartOnMouseClick);
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

	/*
	 *  PI차트의 그래픽 아이템 노드를 선택한 경우 발생됨.
	 */

	public void chartGraphicsCustomAction(Data d, Node n) {

	}

	/********************************
	 * 작성일 : 2016. 10. 20. 작성자 : KYJ
	 *
	 * 시리즈 라벨 action 처리.
	 * 
	 * @param node
	 ********************************/
	public void seriesLegendLabelCustomAction(Data d, Node node) {

	}

}
