/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 11. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

/***************************
 *
 * @author KYJ
 *
 ***************************/
class FxTreeViewClipboardUtil {

	/**
	 * ctrl + c copy 처리 <br/>
	 * 17.12.06 <br/>
	 * BUG FIX <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 16.
	 * @param table
	 */
	public static void installCopyPasteHandler(TreeView<?> table) {
		table.addEventHandler(KeyEvent.KEY_PRESSED, e -> {

			if (e.isConsumed())
				return;
			if (e.isControlDown() && e.getCode() == KeyCode.C) {

				ObservableList<?> selectedItems = table.getSelectionModel().getSelectedItems();
				StringBuilder sb = new StringBuilder();

				Callback cellFactory = table.getCellFactory();
				for (Object v : selectedItems) {
					if (v instanceof TreeItem) {
						TreeItem ti = (TreeItem) v;
						Object value = ti.getValue();
						TreeCell cell = (TreeCell) cellFactory.call(value);
						sb.append(cell.getText());
					} else {
						// TODO :: 첫번째 컬럼(행 선택 기능)도 빈값으로 복사됨..
						// 행변경시
						if (v != null) {
							sb.append(v.toString());
							sb.append("\n");
						}
					}

				}
				FxClipboardUtil.putString(sb.toString());
				e.consume();
			}

		});
	}
}
