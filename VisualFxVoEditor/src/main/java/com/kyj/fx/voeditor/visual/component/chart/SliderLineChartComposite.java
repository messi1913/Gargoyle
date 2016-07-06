/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.chart
 *	작성일   : 2016. 5. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.chart;

import java.util.List;

import com.kyj.fx.voeditor.visual.framework.InstanceTypes;
import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;

/***************************
 *
 * @author KYJ
 *
 ***************************/
@FXMLController(value = "SliderLineChartView.fxml", isSelfController = true, instanceType = InstanceTypes.RequireNew)
public class SliderLineChartComposite<T> extends BorderPane implements ListChangeListener<T> {

	@FXML
	public LineChart<String, Number> chart;

	@FXML
	public Slider slider;

	private ObservableList<T> items = FXCollections.observableArrayList();

	public SliderLineChartComposite(List<T> items) {
		items.addAll(items);
	}

	@FXML
	public void initialize() {
		items.addListener(this);
	}

	@Override
	public void onChanged(javafx.collections.ListChangeListener.Change<? extends T> c) {

	}
}
