/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.capture
 *	작성일   : 2017. 10. 12.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.capture;

import java.io.InputStream;

import javafx.scene.control.Skin;
import javafx.scene.paint.Color;

/**
 * @author KYJ
 *
 */
public class ImageDrawItem extends DrawItem {

	private InputStream image;
	private double fitHeight = -1;
	private double fitWidth = -1;

	public ImageDrawItem(InputStream stream) {
		this.image = stream;
	}

	// public ImageDrawItem(InputStream stream, double fitHeight) {
	// this(stream, -1, fitHeight);
	// }
	//
	// public ImageDrawItem(InputStream stream, double fitWidth, double fitHeight) {
	// this.image = stream;
	// this.fitWidth = fitWidth;
	// this.fitHeight = fitHeight;
	// }

	public InputStream getImage() {
		return image;
	}

	public final double getFitWidth() {
		return fitWidth;
	}

	public final double getFitHeight() {
		return fitHeight;
	}

	@Override
	public void requestBackgroundColorChange(Color newColor) {
		// Nothing.

	}

	@Override
	public void requestTextFillChange(Color newColor) {
		// Nothing.
	}

	@Override
	public void requestBorderColorChange(Color newColor) {

	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new ImageDrawItemSkin(this);
	}

}
