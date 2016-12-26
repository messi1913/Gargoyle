/**
 *
 */
package com.kyj.fx.voeditor.visual.main.layout;

import java.util.List;
import java.util.Map;

import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

import com.kyj.fx.voeditor.visual.component.spreadsheets.GagoyleSpreadSheetView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 *
 */
public class SchoolMgrerSpreadSheetView extends BorderPane {

	private GagoyleSpreadSheetView gagoyleSpreadSheetView;

	/**
	 * @param colSize
	 */
	public SchoolMgrerSpreadSheetView(int colSize) {
		GridBase gridBase = new GridBase(100, colSize);
		List<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();

		for (int row = 0; row < gridBase.getRowCount(); ++row) {
			ObservableList<SpreadsheetCell> currentRow = FXCollections.observableArrayList();
			for (int column = 0; column < gridBase.getColumnCount(); ++column) {
				SpreadsheetCell createCell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1, "");
				currentRow.add(createCell);
			}
			rows.add(currentRow);
		}
		gridBase.setRows(rows);

		gagoyleSpreadSheetView = new GagoyleSpreadSheetView(gridBase);

		setCenter(gagoyleSpreadSheetView);
	}

	public SchoolMgrerSpreadSheetView() {
		this(27);
	}



	/**
	 * @param e
	 */
//	public void spreadSheetKeyPress(KeyEvent e) {
//		gagoyleSpreadSheetView.spreadSheetKeyPress(e);
//	}

	public void paste() {
		gagoyleSpreadSheetView.paste();
	}

	public void paste(final String pastString, final int startRowIndex, final int startColumnIndex) {
		gagoyleSpreadSheetView.paste(pastString, startRowIndex, startColumnIndex);
	}

	/**
	 * 특수문자에대한 문자열 paste에 대한 버그를 수정하기 위한 함수.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 23.
	 * @param items
	 * @param startRowIndex
	 * @param startColumnIndex
	 */
	public void paste(List<Map<String, Object>> items, int startRowIndex, int startColumnIndex) {
		gagoyleSpreadSheetView.paste(items, startRowIndex, startColumnIndex);
	}
}
