/**
 * 
 */
package com.kyj.fx.voeditor.visual.main.layout;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

import com.kyj.fx.voeditor.visual.component.spreadsheets.GagoyleSpreadSheetView;

/**
 * @author KYJ
 *
 */
public class SchoolMgrerSpreadSheetView extends BorderPane {

	private GagoyleSpreadSheetView gagoyleSpreadSheetView;

	public SchoolMgrerSpreadSheetView() {

		GridBase gridBase = new GridBase(100, 100);
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

		this.setOnKeyPressed(this::spreadSheetKeyPress);
	}

	/**
	 * @param e
	 */
	public void spreadSheetKeyPress(KeyEvent e) {
		gagoyleSpreadSheetView.spreadSheetKeyPress(e);
	}

	public void paste() {
		gagoyleSpreadSheetView.paste();
	}

	public void paste(final String pastString, final int startRowIndex, final int startColumnIndex) {
		gagoyleSpreadSheetView.paste(pastString, startRowIndex, startColumnIndex);
	}
}
