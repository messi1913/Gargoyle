/**
 * 
 */
package com.kyj.fx.voeditor.visual.component;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * 테이블셀을 이미지로 처리하기 위한 클래스
 * 
 * @author KYJ
 *
 */
public class ImageCellFactory<S> implements Callback<TableColumn<S, String>, TableCell<S, String>> {

	private String imageLocation;

	public ImageCellFactory(String imageLocation) {
		this.imageLocation = imageLocation;
	}

	@Override
	public TableCell<S, String> call(TableColumn<S, String> param) {
		return new DaoWizardImageCell<S>(imageLocation);
	}

}
