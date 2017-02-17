/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 11. 28.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.lang.reflect.Method;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * @author KYJ
 *
 */
class FxTableViewUtil {

	private FxTableViewUtil() {
	}

	/**
	 * 테이블컬럼의 row에 해당하는 데이터가 무엇인지 정의한 값을 리턴.
	 *
	 * StringConverter를 이용한 TableCell인경우 정의된 StringConvert를 이용한 데이터를 Excel의
	 * Cell에 쓰고, StringConverter를 이용하지않는 UI의 TableCell의 경우 데이터셋에 바인드된 값을 사용하게됨.
	 *
	 * 작성된 API내에서 적절한 값이 아니라고 판단되는경우 Ovrride해서 재정의하도록한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 9. 9.
	 * @param table
	 *            사용자 화면에 정의된 tableView
	 * @param column
	 *            사용자 화면에 정의된 tableColumn
	 * @param columnIndex
	 *            사용자 화면에 정의된 tableColumn의 인덱스
	 * @param rowIndex
	 *            사용자 화면에 정의된 tableCell의 인덱스
	 * @return Object 테이블셀에 정의된 데이터를 리턴할 값으로, 리턴해주는 값이 엑셀에 write된다.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static  Object getValue(TableView<?> table, TableColumn<?, ?> column, int rowIndex) {

		Callback cellFactory = column.getCellFactory();
		if (cellFactory != null) {
			TableCell cell = (TableCell) cellFactory.call(column);

			if (cell != null) {
				StringConverter converter = null;
				if (cell instanceof TextFieldTableCell) {
					TextFieldTableCell txtCell = (TextFieldTableCell) cell;
					converter = txtCell.getConverter();
				} else if (cell instanceof ComboBoxTableCell) {
					ComboBoxTableCell txtCell = (ComboBoxTableCell) cell;
					converter = txtCell.getConverter();
				}
				/* else 기본값. */
				else {
					try {
						Method m = cell.getClass().getMethod("converterProperty");
						if (m != null) {
							Object object = m.invoke(cell);
							if (object != null && object instanceof ObjectProperty) {
								ObjectProperty<StringConverter> convert = (ObjectProperty<StringConverter>) object;
								converter = convert.get();
							}
						}
					} catch (Exception e) {
						// Nothing...
					}
				}

				if (converter != null) {
					Object cellData = column.getCellData(rowIndex);
					return converter.toString(cellData);
				}
			}
		}

		return column.getCellData(rowIndex);
	}

	/**
	 *
	 * reference - getValueByConverter(TableView<?> table, TableColumn<?, ?>
	 * column, int rowIndex) <br/>
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 28.
	 * @param table
	 * @param columnIndex
	 * @param rowIndex
	 * @return
	 */
	public static <T> Object getValue(TableView<?> table, int columnIndex, int rowIndex) {
		return getValue(table, table.getColumns().get(columnIndex), rowIndex);
	}

	/********************************
	 * 작성일 : 2016. 5. 12. 작성자 : KYJ
	 *
	 * 테이블뷰 클립보드 기능.
	 *
	 * @param table
	 ********************************/
	@SuppressWarnings("rawtypes")
	public static void installCopyPasteHandler(TableView<?> table) {

		table.addEventHandler(KeyEvent.KEY_PRESSED, e -> {

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

			// TableView<Map<String, Object>> tbResult = getTbResult();

			ObservableList<TablePosition> selectedCells = table.getSelectionModel().getSelectedCells();

			TablePosition tablePosition = selectedCells.get(0);
			TableColumn tableColumn = tablePosition.getTableColumn();
			int row = tablePosition.getRow();
			int col = table.getColumns().indexOf(tableColumn);

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
					else if (col != table.getColumns().indexOf(cell.getTableColumn())) {
						sb.append("\t");
					}
					Object cellData = cell.getTableColumn().getCellData(cell.getRow());
					sb.append(ValueUtil.decode(cellData, cellData, "").toString());
				}
				FxClipboardUtil.putString(sb.toString());

				// Map<String, Object> map = tbResult.getItems().get(row);
				// FxClipboardUtil.putString(ValueUtil.toCVSString(map));
				break;
			case 2:
				Object cellData = tableColumn.getCellData(row);
				FxClipboardUtil.putString(ValueUtil.decode(cellData, cellData, "").toString());
				break;
			}

		});
	}

}
