/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.grid
 *	작성일   : 2016. 2. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.grid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * @author KYJ
 *
 */
public final class DynamicImageViewItem extends DynamicItem<ImageView> {

	public DynamicImageViewItem(String filePathName) throws FileNotFoundException {
		this(new Image(new FileInputStream(new File(filePathName))));
	}

	public DynamicImageViewItem(File file) throws FileNotFoundException {
		this(new Image(new FileInputStream(file)));
	}

	public DynamicImageViewItem(Image item) {
		this(new ImageView(item));
	}

	public DynamicImageViewItem(ImageView item) {
		super(item);
		validating(item);
	}

	private void validating(ImageView item) {
		item.setFitWidth(100);
		item.setFitHeight(100);
	}

	@Override
	public void setOnMouseClicked(MouseEvent event) {
		System.out.println("이미지 선택.");

	}

}
