/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.chart
 *	작성일   : 2016. 5. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.chart;

import java.util.function.Function;
import java.util.stream.Collectors;

import org.controlsfx.control.RangeSlider;

import com.kyj.fx.voeditor.visual.util.FxCollectors;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.BorderPane;

/***************************
 *
 * @author KYJ
 *
 ***************************/

public abstract class RangeSliderLineChartComposite<T> extends BorderPane implements ListChangeListener<T> {

	public LineChart<String, Number> chart = new LineChart<>(new CategoryAxis(), new NumberAxis());

	public RangeSlider slider = new RangeSlider();

	//전체 데이터.
	private ObservableList<T> items = FXCollections.observableArrayList();

	private DoubleProperty displaySize = new SimpleDoubleProperty(15d);

	public RangeSliderLineChartComposite() throws Exception {
		this.setCenter(chart);
		this.setBottom(slider);
		initialize();
	}

	//	@FXML
	public void initialize() {
		chart.setAnimated(false);

		items.addListener(this);

		slider.setMajorTickUnit(5d);

		slider.setMajorTickUnit(5d);

		slider.setMin(0D);
		slider.setMax(0D);
		slider.setLowValue(0D);
		slider.setHighValue(0D);

		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.setBlockIncrement(10);
		
		
		this.displaySize.bind(slider.maxProperty());

		slider.lowValueProperty().addListener((oba, oldval, newval) -> {
			int start = newval.intValue();
			ObservableList<Data<String, Number>> subList = items.stream().skip(start).limit(displaySize.intValue())
					.map(v -> converter.apply(v)).collect(FxCollectors.toObservableList());
			Series<String, Number> chartSeries = new Series<>("", subList);
			chart.getData().clear();
			chart.getData().add(chartSeries);
		});

		slider.highValueProperty().addListener((oba, oldval, newval) -> {
			int start = (int) slider.getLowValue();
			int size = Math.abs(start - newval.intValue() - 2);
			ObservableList<Data<String, Number>> subList = items.stream().skip(start).limit(size).map(v -> converter.apply(v))
					.collect(FxCollectors.toObservableList());
			Series<String, Number> chartSeries = new Series<>("", subList);
			chart.getData().clear();
			chart.getData().add(chartSeries);
		});

		

	}

	@Override
	public void onChanged(javafx.collections.ListChangeListener.Change<? extends T> c) {
		if (c.next()) {

			if (c.wasAdded()) {
				int start = 0;
				int end = 5;
				Series<String, Number> chartSeries = new Series<>();
				chartSeries.getData().addAll(items.subList(start, end).stream().map(v -> converter.apply(v)).collect(Collectors.toList()));
				chart.getData().add(chartSeries);
				
				this.slider.setMax(this.items.size());
				this.slider.setLowValue(5D);
				this.slider.setHighValue(displaySize.doubleValue());
				
			}

			else if (c.wasRemoved()) {

			}

			
			
		}

	}

	private Function<T, Data<String, Number>> converter = new Function<T, XYChart.Data<String, Number>>() {

		@Override
		public Data<String, Number> apply(T t) {
			return converter(t);
		}
	};

	public abstract Data<String, Number> converter(T t);

	public ObservableList<T> getItems() {
		return this.items;
	}

	public void setSliderSize(Double size) {
		displaySize.set(size);
	}
}
