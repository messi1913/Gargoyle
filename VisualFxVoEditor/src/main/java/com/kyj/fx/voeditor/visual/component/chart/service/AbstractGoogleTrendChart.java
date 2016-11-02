/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 11. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.chart.service;

import com.kyj.fx.voeditor.visual.framework.adapter.IGargoyleChartAdapter;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;

/**
 * GoogleTrend를 구현하기 위한 추상 클래스
 * @author KYJ
 *
 */
public abstract class AbstractGoogleTrendChart<T, C> extends LineChart<String, Number> {

	private StringProperty source = new SimpleStringProperty();
	private ObjectProperty<T> json = new SimpleObjectProperty<>();
	private IGargoyleChartAdapter<T, C> adapter;

	public AbstractGoogleTrendChart(String source) {

		super(new CategoryAxis(), new NumberAxis());

		this.source.set(source);
		this.json.set(convert(source));
		this.adapter = adapter();


		Platform.runLater(() -> {
			createChart();
		});
	}

	/**
	 * source를 JSONObject로 변환하는 로직 구현
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 2.
	 * @param source
	 * @return
	 */
	public abstract T convert(String source);

	/**
	 * 데이터와 차트를 그리기위한 중간역할을 하는 어댑터 구현
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 2.
	 * @return
	 */
	public abstract IGargoyleChartAdapter<T, C> adapter();

	/**
	 * 차트 그리는 작업 처리
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 2.
	 */
	protected void createChart() {
		IGargoyleChartAdapter<T, C> adapter = this.adapter;

		String title = adapter.getTitle(this.getJson());
		setTitle(title);

		int columnCount = adapter.getColumnCount(this.getJson());

		for (int i = 0; i <= columnCount; i++) {

			String columnName = adapter.getColumnName(this.getJson(), i);

			ObservableList<Data<String, Number>> data = FXCollections.observableArrayList();
			javafx.scene.chart.XYChart.Series<String, Number> series = new Series<>(columnName, data);
			getData().add(series);

			int valueCount = adapter.getValueCount(this.getJson(), columnName);

			for (int v = 0; v < valueCount; v++) {
				Data<String, Number> value = adapter.getValue(this.getJson(), i, columnName, v);
				if(value == null)
					continue;
				
				data.add(value);
			}

		}

	}

	/**********************************************************************************************************************************************************/
	//property define.

	public final StringProperty sourceProperty() {
		return this.source;
	}

	public final java.lang.String getSource() {
		return this.sourceProperty().get();
	}

	public final void setSource(final java.lang.String source) {
		this.sourceProperty().set(source);
	}

	public final ObjectProperty<T> jsonProperty() {
		return this.json;
	}

	public final T getJson() {
		return this.jsonProperty().get();
	}

	public final void setJson(final T json) {
		this.jsonProperty().set(json);
	}

	/**
	 * @return the adapter
	 */
	public final IGargoyleChartAdapter<T, C> getAdapter() {
		return adapter;
	}

}
