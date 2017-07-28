/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.capture
 *	작성일   : 2017. 7. 28.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.capture;

import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * @author KYJ
 *
 */
public class DrawItemSkin<T extends DrawItem> extends BehaviorSkinBase<DrawItem, DrawItemBehavior> {

	private VBox container;

	protected DrawItemSkin(DrawItem control) {
		super(control, new DrawItemBehavior(control));
		container = new VBox();
		
		container.getChildren().add(new Label("Table-Name"));
		container.getChildren().add(new Label("Column-Name"));
		container.getChildren().add(new Label("Column-Name"));
		
		
		getChildren().add(container);
	}

	@Override
	protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
		super.layoutChildren(contentX, contentY, contentWidth, contentHeight);
		
	}

}
