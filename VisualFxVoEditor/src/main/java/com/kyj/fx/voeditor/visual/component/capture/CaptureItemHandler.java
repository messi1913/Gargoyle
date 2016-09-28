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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;

/**
 * @author KYJ
 *
 */
public class CaptureItemHandler {

	private CaptureScreenController controller;

	private ObservableList<Node> items;

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
			Label e2 = new Label("텍스트");
			e2.getTransforms().add(new Scale(5, 5));

			e2.setOnMouseClicked(ev -> {
				if (ev.getClickCount() == 2) {
					DialogUtil.showInputDialog(btn, "Text", "Input Text").ifPresent(v -> {
						//						v.getValue()
						if (ValueUtil.isNotEmpty(v.getValue())) {
							e2.setText(v.getValue());
						}
					});
				}
			});

			controller.addItemEvent(e2);
			controller.addChildren(e2);
		});

		items.add(btn);
	}

	/**
	 * 도형 객체 생성.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 9. 28.
	 */
	protected void createFigure() {

		Button btn = new Button("사각형");
		btn.setOnAction(e -> {
			Rectangle rectangle = new Rectangle(200d, 200d);
			rectangle.setStyle("-fx-background-color: red;");
			//			rectangle.setFill(Color.RED);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setStroke(Color.RED);
			//			rectangle.getTransforms().add(new Scale(5, 2));
			controller.addItemEvent(rectangle);
			controller.addChildren(rectangle);
		});

		items.add(btn);
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
