/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.capture
 *	작성일   : 2016. 9. 28.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.capture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;

import com.jfoenix.controls.JFXColorPicker;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.GargoyleExtensionFilters;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Scale;

/**
 * @author KYJ
 *
 */
public class ErdItemHandler {

	private ErdScreenController controller;

	protected ObservableList<Control> items;

	/**
	 * @param captureScreenController
	 */
	public ErdItemHandler(ErdScreenController controller) {
		this.controller = controller;
		items = FXCollections.observableArrayList();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 9. 28.
	 */
	protected void createLaledItems() {

		Button btn = new Button("텍스트");
		btn.setOnAction(e -> {
			Label label = new Label("텍스트");
			label.getTransforms().add(new Scale(5, 5));

			label.setOnMouseClicked(ev -> {

				if (ev.getClickCount() == 2) {
					DialogUtil.showInputDialog(btn, "Text", "Input Text").ifPresent(v -> {
						// v.getValue()
						if (ValueUtil.isNotEmpty(v.getValue())) {
							label.setText(v.getValue());
						}
					});
					ev.consume();
				}
			});

			controller.addItemEvent(label);
			controller.addChildren(label);
		});

		items.add(btn);
	}

	/**
	 * 도형 객체 생성.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 9. 28.
	 */
	protected void createFigure() {

		{
			Button btn = new Button("사각형");
			btn.setOnAction(e -> {
				Rectangle rectangle = new Rectangle(200d, 200d);
				rectangle.setStyle("-fx-background-color: red;");
				// rectangle.setFill(Color.RED);
				rectangle.setFill(Color.TRANSPARENT);
				rectangle.setStroke(Color.RED);
				rectangle.setArcHeight(15);
				rectangle.setArcWidth(15);

				// rectangle.getTransforms().add(new Scale(5, 2));
				controller.addItemEvent(rectangle);
				controller.addChildren(rectangle);

				// rectangle.getlay
			});

			items.add(btn);
		}

		{
			Button btn = new Button("라인");
			btn.setOnAction(e -> {

				// Rectangle rectangle = new Rectangle(200d, 200d);
				// rectangle.setStrokeLineJoin(StrokeLineJoin.ROUND);

				Line line = new Line();
				line.setStartX(0d);
				line.setStartY(0d);
				line.setEndX(200d);
				line.setEndY(200d);

				line.setStrokeWidth(10);
				line.setStrokeLineCap(StrokeLineCap.BUTT);
				line.getStrokeDashArray().addAll(15d, 5d, 15d, 15d, 20d);
				line.setStrokeDashOffset(10);

				controller.addItemEvent(line);
				controller.addChildren(line);
			});
			items.add(btn);
		}

		{
			Button btn = new Button("둥근타원형");
			btn.setOnAction(e -> {

				// Rectangle rectangle = new Rectangle(200d, 200d);
				// rectangle.setStrokeLineJoin(StrokeLineJoin.ROUND);
				Group g = new Group();
				DropShadow ds = new DropShadow();
				ds.setOffsetY(3.0);
				ds.setColor(Color.color(0.4, 0.4, 0.4));

				Ellipse ellipse = new Ellipse();
				ellipse.setCenterX(50.0f);
				ellipse.setCenterY(50.0f);
				ellipse.setRadiusX(50.0f);
				ellipse.setRadiusY(25.0f);
				ellipse.setFill(Color.RED);
				ellipse.setStroke(Color.BLACK);
				ellipse.setEffect(ds);
				g.getChildren().add(ellipse);

				g.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {

					if (ev.getClickCount() == 2 && MouseButton.PRIMARY == ev.getButton()) {
						if (ev.isConsumed())
							return;

						if (ellipse.getUserData() != null)
							return;

						JFXColorPicker picker = new JFXColorPicker();
						picker.setValue((Color) ellipse.getFill());
						picker.valueProperty().addListener(new ChangeListener<Color>() {

							@Override
							public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
								ellipse.setFill(newValue);
							}
						});

						FxUtil.createStageAndShow(" select color ", picker, stage -> {

							ellipse.setUserData(new Object());

							stage.setAlwaysOnTop(true);

							stage.setOnCloseRequest(se -> {
								ellipse.setUserData(null);
							});
						});

						ev.consume();
					}
				});

				controller.addItemEvent(g);
				controller.addChildren(g);
			});
			items.add(btn);
		}

		{
			Button btn = new Button("테이블 샘플");
			btn.setOnAction(e -> {

				DrawItem d = new TableDrawItem("Table-Name", Arrays.asList("User-Name", "User-Age"));
				d.setController(this.controller);

				controller.addItemEvent(d);
				controller.addChildren(d);
			});
			items.add(btn);
		}

		{
			Button btn = new Button("이미지");
			btn.setOnAction(e -> {

				File showFileDialog = DialogUtil.showFileDialog(chooser -> {
					chooser.getExtensionFilters().add(GargoyleExtensionFilters.PNG_EXTENSION_FILTER);
				});
				if (showFileDialog != null) {
					try {
//						Image image = new Image(new FileInputStream(showFileDialog));
						DrawItem d = new ImageDrawItem(showFileDialog.toURI().toURL().openStream());
						d.setController(this.controller);

						controller.addItemEvent(d);
						controller.addChildren(d);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

			});
			items.add(btn);
		}
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 9. 28.
	 * @return
	 */
	public ObservableList<Control> getItems() {

		createLaledItems();

		createFigure();

		return items;
	}

}
