/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 11. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/***************************
 *
 * @author KYJ
 *
 ***************************/
class FxTreeViewClipboardUtil {

	/**
	 * ctrl + c copy 처리
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 16.
	 * @param table
	 */
	public static void installCopyPasteHandler(TreeView<?> table) {
		table.addEventHandler(KeyEvent.KEY_PRESSED, e -> {

			if (e.isControlDown() && e.getCode() == KeyCode.C) {

				ObservableList<?> selectedItems = table.getSelectionModel().getSelectedItems();
				StringBuilder sb = new StringBuilder();
				for (Object cell : selectedItems) {
					// TODO :: 첫번째 컬럼(행 선택 기능)도 빈값으로 복사됨..
					// 행변경시
					if (cell != null) {
						sb.append(cell.toString());
						sb.append("\n");
					}
				}
				FxClipboardUtil.putString(sb.toString());
			}

		});
	}
}
