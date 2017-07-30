/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.capture
 *	작성일   : 2017. 7. 28.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.capture;

import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * @author KYJ
 *
 */
public class DrawItemSkin<T extends DrawItem> extends BehaviorSkinBase<DrawItem, DrawItemBehavior> {

	private VBox container;

	protected DrawItemSkin(DrawItem control) {
		super(control, new DrawItemBehavior(control));
		container = new VBox();
		container.setPickOnBounds(true);
		container.getChildren().add(new Label("Table-Name"));
		container.getChildren().add(new Label("Column-Name"));
		container.getChildren().add(new Label("Column-Name"));

		getSkinnable().focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					container.requestFocus();
				}
			}
		});

		container.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				System.out.println(newValue);

				if (newValue) {
					BorderStroke bs = new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN);
					Border border = new Border(bs);//Border.EMPTY;
					container.setPadding(new Insets(5));
					container.setBorder(border);
				} else {
					container.setBorder(Border.EMPTY);
				}
			}
		});

		getChildren().add(container);

		//		getChildren().forEach(n ->{
		//			n.setMouseTransparent(true);
		//		});
	}

	@Override
	protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
		super.layoutChildren(contentX, contentY, contentWidth, contentHeight);
	}

}
