/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.grid
 *	작성일   : 2016. 9. 20.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.grid;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/***************************
 * 
 * @author KYJ
 *
 ***************************/
public class MergedTableColumn<S, T> extends TableColumn<S, T> {

	public MergedTableColumn() {
		super();
	}

	public MergedTableColumn(String text) {
		super(text);
	}

	protected void init() {
		this.setCellFactory(new Callback<TableColumn<S, T>, TableCell<S, T>>() {

			@Override
			public TableCell<S, T> call(TableColumn<S, T> param) {
				return null;
			}
		});
	}
}
