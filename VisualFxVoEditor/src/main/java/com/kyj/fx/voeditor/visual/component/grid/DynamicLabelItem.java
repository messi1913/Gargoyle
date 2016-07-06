/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.grid
 *	작성일   : 2016. 2. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.grid;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

/**
 * @author KYJ
 *
 */
public final class DynamicLabelItem extends DynamicItem<Label> {

	public DynamicLabelItem(String item) {
		super(new Label(item));
	}

	public DynamicLabelItem(Label item) {
		super(item);
	}

	@Override
	public void setOnMouseClicked(MouseEvent event) {
		// TODO Auto-generated method stub

	}

}
