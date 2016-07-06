/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.dbtree
 *	작성일   : 2016. 1. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.dbtree;

import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.ColumnItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.DatabaseItemTree;

import javafx.scene.control.cell.TextFieldTreeCell;

/**
 *
 * 2016-05-18
 * setTextFill 함수 -> setStyle로 변경.
 *
 * 글로벌 css에 따라 setTextFill의경우 색상이 입혀지지않는 버그가 있다.
 * setStyle의 케이스에서는 항시 적용됨.
 * @author KYJ
 *
 */
public class DatabaseTreeCell<T extends DatabaseItemTree<String>> extends TextFieldTreeCell<T> {

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * javafx.scene.control.cell.TextFieldTreeCell#updateItem(java.lang.Object,
	 * boolean)
	 */

	@SuppressWarnings("rawtypes")
	@Override
	public void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setGraphic(null);
			setStyle(null);
		} else {

			if (item instanceof ColumnItemTree) {

				ColumnItemTree columnItemTree = (ColumnItemTree) item;
				if (columnItemTree.isPrimaryKey()) {
//					setTextFill(Color.RED);
					setStyle("-fx-text-fill:RED");
				} else {
//					setTextFill(Color.BLACK);
					setStyle("-fx-text-fill:BLACK");
				}
			} else {
//				setTextFill(Color.BLACK);
				setStyle("-fx-text-fill:BLACK");
			}

			setText(item.toString());

		}

	}
}
