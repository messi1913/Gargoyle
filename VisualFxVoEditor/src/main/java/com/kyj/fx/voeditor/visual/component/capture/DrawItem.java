/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.capture
 *	작성일   : 2017. 7. 28.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.capture;

import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * @author KYJ
 *
 */
public abstract class DrawItem extends Control implements ColorChange {

	/**
	 * 
	 */
//	public DrawItem() {

		// setMouseTransparent(false);
		// setFocusTraversable(false);
//	}

	@Override
	protected abstract Skin<?> createDefaultSkin();

}
