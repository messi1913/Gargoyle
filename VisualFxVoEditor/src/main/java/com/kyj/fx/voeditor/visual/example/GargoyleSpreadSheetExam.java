/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 10. 28.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.util.List;

import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

import com.kyj.fx.voeditor.visual.component.spreadsheets.ExcelTemplateControl;
import com.kyj.fx.voeditor.visual.component.spreadsheets.GagoyleSpreadSheetView;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class GargoyleSpreadSheetExam extends Application {

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 28.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

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

		ExcelTemplateControl excelTemplateControl = new ExcelTemplateControl();
		primaryStage.setScene(new Scene(new GagoyleSpreadSheetView(gridBase)));
		primaryStage.show();

	}

}
