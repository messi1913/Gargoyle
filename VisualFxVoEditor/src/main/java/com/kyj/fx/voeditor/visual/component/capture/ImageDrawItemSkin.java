/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.capture
 *	작성일   : 2017. 10. 12.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.capture;

import java.io.InputStream;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * @author KYJ
 *
 */
public class ImageDrawItemSkin extends DrawItemSkin<ImageDrawItem> {

	public ImageDrawItemSkin(ImageDrawItem control) {
		super(control);
	}

	@Override
	public void draw(VBox container) {
		InputStream is = getControl().getImage();
		ImageView e = new ImageView(new Image(is));
		
		// double fitHeight = getControl().getFitHeight();

		container.getChildren().add(e);
		VBox.setVgrow(e, Priority.ALWAYS);
	}

}
