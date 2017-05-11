/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.events
 *	작성일   : 2016. 9. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/***************************
 * 
 * 키 이벤트에 이벤트 리스너를 등록처리해주는 유틸리티
 * 
 * @author KYJ
 *
 ***************************/
class ClipboardKeyEventInstaller {

	/********************************
	 * 작성일 :  2016. 9. 3. 작성자 : KYJ
	 *
	 *
	 * @param tb
	 ********************************/
	public static void install(TableView<?> tb) {

		tb.addEventHandler(KeyEvent.KEY_PRESSED, e -> {

			int type = -1;
			if (e.isControlDown() && e.getCode() == KeyCode.C) {
				if (e.isShiftDown()) {
					type = 2;
				} else {
					type = 1;
				}
			}

			if (type == -1)
				return;

			ObservableList<TablePosition> selectedCells = tb.getSelectionModel().getSelectedCells();

			TablePosition tablePosition = selectedCells.get(0);
			TableColumn tableColumn = tablePosition.getTableColumn();
			int row = tablePosition.getRow();
			int col = tb.getColumns().indexOf(tableColumn);

			switch (type) {
			case 1:
				StringBuilder sb = new StringBuilder();
				for (TablePosition cell : selectedCells) {
					// TODO :: 첫번째 컬럼(행 선택 기능)도 빈값으로 복사됨..
					// 행변경시
					if (row != cell.getRow()) {
						sb.append("\n");
						row++;
					}
					// 열 변경시
					else if (col != tb.getColumns().indexOf(cell.getTableColumn())) {
						sb.append("\t");
					}
					Object cellData = getDisplayText(/*tableColumn*/ cell, row);//cell.getTableColumn().getCellData(cell.getRow());
					sb.append(ValueUtil.decode(cellData, cellData, "").toString());
				}
				FxClipboardUtil.putString(sb.toString());

				// Map<String, Object> map = tbResult.getItems().get(row);
				// FxClipboardUtil.putString(ValueUtil.toCVSString(map));
				break;
			case 2:
				Object cellData = getDisplayText(tableColumn, row); //tableColumn.getCellData(row);
				FxClipboardUtil.putString(ValueUtil.decode(cellData, cellData, "").toString());
				break;
			}

			e.consume();
		});

	}

	private static Object getDisplayText(TablePosition cell, int row) {
		return getDisplayText(cell.getTableColumn(), row);
	}

	private static Object getDisplayText(TableColumn<?, ?> tc, int row) {
		return FxUtil.getDisplayText(tc, row);
	}
}
