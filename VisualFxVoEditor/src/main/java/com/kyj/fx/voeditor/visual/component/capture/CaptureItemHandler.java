/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.capture
 *	작성일   : 2016. 9. 28.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.capture;

import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
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
public class CaptureItemHandler {

	private CaptureScreenController controller;

	protected ObservableList<Node> items;

	/**
	 * @param captureScreenController
	 */
	public CaptureItemHandler(CaptureScreenController controller) {
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
						//						v.getValue()
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
	 * @작성자 : KYJ
	 * @작성일 : 2016. 9. 28.
	 */
	protected void createFigure() {

		{
			Button btn = new Button("사각형");
			btn.setOnAction(e -> {
				Rectangle rectangle = new Rectangle(200d, 200d);
				rectangle.setStyle("-fx-background-color: red;");
				//			rectangle.setFill(Color.RED);
				rectangle.setFill(Color.TRANSPARENT);
				rectangle.setStroke(Color.RED);
				rectangle.setArcHeight(15);
				rectangle.setArcWidth(15);

				//			rectangle.getTransforms().add(new Scale(5, 2));
				controller.addItemEvent(rectangle);
				controller.addChildren(rectangle);

				//			rectangle.getlay
			});

			items.add(btn);
		}

		{
			Button btn = new Button("라인");
			btn.setOnAction(e -> {

				//				Rectangle rectangle = new Rectangle(200d, 200d);
				//				rectangle.setStrokeLineJoin(StrokeLineJoin.ROUND);
				
				
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

				//				Rectangle rectangle = new Rectangle(200d, 200d);
				//				rectangle.setStrokeLineJoin(StrokeLineJoin.ROUND);
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

				controller.addItemEvent(g);
				controller.addChildren(g);
			});
			items.add(btn);
		}

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 9. 28.
	 * @return
	 */
	public ObservableList<Node> getItems() {

		createLaledItems();

		createFigure();

		return items;
	}

}
