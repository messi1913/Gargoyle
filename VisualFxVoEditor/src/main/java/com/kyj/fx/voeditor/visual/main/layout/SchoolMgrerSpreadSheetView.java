/**
 *
 */
package com.kyj.fx.voeditor.visual.main.layout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

import com.kyj.fx.voeditor.visual.component.spreadsheets.GagoyleSpreadSheetView;
import com.kyj.fx.voeditor.visual.excels.base.ExcelDataDVO;
import com.kyj.fx.voeditor.visual.excels.base.ExcelSVO;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.ExcelUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.GargoyleExtensionFilters;
import com.kyj.fx.voeditor.visual.util.ThreadUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * @author KYJ
 *
 */
public class SchoolMgrerSpreadSheetView extends BorderPane {

	private GagoyleSpreadSheetView gagoyleSpreadSheetView;
	private MenuBar mbRoot = new MenuBar();

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

		setTop(mbRoot);
		setCenter(gagoyleSpreadSheetView);

		createMenus();
	}

	public SchoolMgrerSpreadSheetView() {
		this(27);
	}

	private void createMenus() {
		//[Start] Menu File
		{
			Menu menuFile = new Menu("File");
			MenuItem miSaveAs = new MenuItem("Save As");
			miSaveAs.setOnAction(this::miSaveAsOnAction);
			menuFile.getItems().add(miSaveAs);

			mbRoot.getMenus().add(menuFile);
		}

	}

	/**
	 * 파일 저장 처리.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 27.
	 * @param event
	 */
	public void miSaveAsOnAction(ActionEvent event) {

		File saveFile = DialogUtil.showFileSaveCheckDialog(getScene().getWindow(), chooser -> {
//			chooser.setSelectedExtensionFilter(new ExtensionFilter(GargoyleExtensionFilters.XLSX_NAME, GargoyleExtensionFilters.XLSX));
			chooser.getExtensionFilters().add(new ExtensionFilter(GargoyleExtensionFilters.XLSX_NAME, GargoyleExtensionFilters.XLSX));
//			chooser.getSelectedExtensionFilter().getExtensions().add(GargoyleExtensionFilters.XLSX_NAME);
		});
		if (saveFile != null) {

			ThreadUtil.createNewThreadAndRun(() -> {

				try (Workbook workBookXlsx = ExcelUtil.createNewWorkBookXlsx()) {

					Sheet createSheet = workBookXlsx.createSheet("Sheet1");
					ObservableList<ObservableList<SpreadsheetCell>> rows = gagoyleSpreadSheetView.getRows();

					IntStream.iterate(0, r -> r + 1).limit(rows.size()).forEach(rIndex -> {

						ObservableList<SpreadsheetCell> cellList = rows.get(rIndex);

						IntStream.iterate(0, a -> a + 1).limit(cellList.size()).forEach(cidx -> {

							SpreadsheetCell spreadsheetCell = cellList.get(cidx);
							String text = spreadsheetCell.getText();

							try {
								ExcelUtil.createCell(createSheet, text, rIndex, cidx);
							} catch (Exception e1) {
								e1.printStackTrace();
							}

						});

					});

					workBookXlsx.write(new FileOutputStream(saveFile));
				} catch (IOException e) {
					e.printStackTrace();
				}

			});

		}
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
