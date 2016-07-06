/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2016. 1. 27.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.io.InputStream;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

/**
 * 이미지 처리를 위한 이미지패널
 *
 * 이미지뷰는 기본적으로 리사이징처리가 어렵기때문에 이를 극복하기 위한 패널
 *
 */
public class ImageViewPane extends Region {

	private ObjectProperty<ImageView> imageViewProperty = new SimpleObjectProperty<>();

	public ImageViewPane(InputStream image) {
		this(new Image(image));
	}

	public ImageViewPane(InputStream image, double reqWidth, double reqHeight) {
		this(new Image(image, reqWidth, reqHeight, true, true));
	}

	public ImageViewPane(Image image) {
		this(new ImageView(image));
	}

	public ImageViewPane(ImageView imageView) {
		imageViewProperty.addListener(new ChangeListener<ImageView>() {

			@Override
			public void changed(ObservableValue<? extends ImageView> arg0, ImageView oldIV, ImageView newIV) {
				if (oldIV != null) {
					getChildren().remove(oldIV);
				}
				if (newIV != null) {
					getChildren().add(newIV);
				}
			}
		});
		this.imageViewProperty.set(imageView);
	}

	public ImageViewPane() {
		this(new ImageView());
	}

	public ObjectProperty<ImageView> imageViewProperty() {
		return imageViewProperty;
	}

	public ImageView getImageView() {
		return imageViewProperty.get();
	}

	public void setImageView(ImageView imageView) {
		this.imageViewProperty.set(imageView);
	}

	@Override
	protected void layoutChildren() {
		ImageView imageView = imageViewProperty.get();
		if (imageView != null) {
			double width = getWidth();
			double height = getHeight();
			if (width > height) {
				width = 5 / 3 * height;
			}
			imageView.setFitWidth(width);
			imageView.setFitHeight(height);
			layoutInArea(imageView, 0, 0, getWidth(), getHeight(), 0, HPos.CENTER, VPos.CENTER);
		}
		// super.layoutChildren();
	}

}
