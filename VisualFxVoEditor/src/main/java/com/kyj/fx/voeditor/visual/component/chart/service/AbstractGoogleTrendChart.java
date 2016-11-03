/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 11. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.chart.service;

import java.util.Optional;

import com.kyj.fx.voeditor.visual.framework.adapter.IGargoyleChartAdapter;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

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
		this(source, new CategoryAxis(), new NumberAxis());
	}

	public AbstractGoogleTrendChart(String source, CategoryAxis x, NumberAxis y) {
		super(x, y);

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

		for (int i = 0; i < columnCount; i++) {
			String columnName = adapter.getColumnName(this.getJson(), i);

			ObservableList<Data<String, Number>> data = FXCollections.observableArrayList();
			javafx.scene.chart.XYChart.Series<String, Number> series = new Series<>(columnName, data);
			getData().add(series);

			int valueCount = adapter.getValueCount(this.getJson(), columnName);

			for (int v = 0; v < valueCount; v++) {
				Data<String, Number> value = adapter.getValue(this.getJson(), i, columnName, v);

				if (value == null)
					continue;
				StackPane s = new StackPane(new Rectangle());
				value.setNode(s);
				s.setUserData(String.format("%s %s %s", value.getXValue(), columnName, value.getYValue().intValue()));

				data.add(value);
			}

		}

		Line e = new Line();
		e.getStyleClass().add("google-chart-flow-line");
		e.setStyle("-fx-fill:gray");
		e.setOpacity(0.3d);
		//		getChildren().add(e);
		getPlotChildren().add(e);

		Text label = new Text("               \n\n\n\n\n\n\n\n\n\n     ");
		//		label.setStyle("-fx-fill:red");
		//		StackPane s = new StackPane(label);
		VBox s = new VBox(label);
		//		s.getStyleClass().add("google-chart-guide-box");
		//		s.setStyle("-fx-background-color : green;");
		s.setPrefSize(VBox.USE_COMPUTED_SIZE, VBox.USE_COMPUTED_SIZE);
		s.setPadding(new Insets(10));
		s.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.DASHED, CornerRadii.EMPTY, new BorderWidths(3d))));
		s.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, new Insets(5))));
		getPlotChildren().add(s);

		this.addEventHandler(MouseEvent.MOUSE_MOVED, ev -> {

			double sceneX = Math.abs(getYAxis().getLayoutX() + getYAxis().getWidth() - ev.getX() - getYAxis().getPadding().getLeft()
					- getYAxis().getPadding().getRight() - getYAxis().getInsets().getLeft() - getYAxis().getInsets().getRight());//ev.getScreenX();//ev.getX();

			e.setStartX(sceneX);
			e.setStartY(0d);

			e.setEndX(sceneX);
			e.setEndY(this.getHeight());

			Optional<String> reduce = lookupAll(".chart-line-symbol").stream() /*.peek(System.out::println)*/.filter(v -> v != null)
					.filter(n -> {
				return e.intersects(n.getBoundsInParent());
			}).filter(v -> v.getUserData() != null).map(v -> v.getUserData().toString()).reduce((a, b) -> {
				return a + "\n" + b;
			});

			if (reduce.isPresent()) {
				label.setText(reduce.get());
				s.setOpacity(1d);
				if ((label.getBoundsInParent().getWidth() + label.getBoundsInParent().getMaxX() + sceneX) > this.getWidth()) {
					s.setLayoutX(sceneX - label.getBoundsInParent().getWidth() - label.getBoundsInParent().getMinX());
				} else {
					s.setLayoutX(sceneX);
				}

				s.setLayoutY(ev.getSceneY());
			} else
				s.setOpacity(0.3d);

		});

	}

	/* (non-Javadoc)
	 * @see javafx.scene.chart.LineChart#layoutPlotChildren()
	 */
	@Override
	protected void layoutPlotChildren() {
		// TODO Auto-generated method stub
		super.layoutPlotChildren();

	}

	/* (non-Javadoc)
	 * @see javafx.scene.chart.Chart#layoutChildren()
	 */
	@Override
	protected void layoutChildren() {
		super.layoutChildren();

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
