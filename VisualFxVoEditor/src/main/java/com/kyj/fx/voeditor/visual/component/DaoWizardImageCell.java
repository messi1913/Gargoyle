/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2015. 10. 21.
 *	프로젝트 : SOS 미어캣 프로젝트
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @author KYJ
 * @param <S>
 * @param <T>
 *
 */
public class DaoWizardImageCell<S> extends TableCell<S, String> {

	private static final double WIDTH = 32;
	private static final double HEIGHT = 32;

	private ImageView imageView;

	private Image image;

	private String src;

	public DaoWizardImageCell(String src) {
		this.src = src;
		if (src != null)
			createImageField();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.scene.control.TableCell#commitEdit(java.lang.Object)
	 */
	@Override
	public void commitEdit(String newValue) {
		// TODO Auto-generated method stub
		super.commitEdit(newValue);
	}

	@Override
	public void updateItem(String _item, boolean empty) {
		super.updateItem(_item, empty);

		if (!empty) {
			setText(_item);

			if (_item == null || _item.isEmpty()) {
				setGraphic(null);
				return;
			}

			if (imageView == null)
				createImageField();

			if (imageView != null)
				setGraphic(imageView);

			else
				setGraphic(imageView);

		} else {
			setGraphic(null);
		}
	}

	private void createImageField() {

		try {
			if (src != null && !src.isEmpty()) {
				String file = getClass().getResource(src).getFile();
				image = new Image(new FileInputStream(file), WIDTH, HEIGHT, true, true);
				imageView = new ImageView(image);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

}
