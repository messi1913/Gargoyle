/**
 * 
 */
package com.kyj.fx.voeditor.visual.excels.models;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.excels.base.ExcelColDVO;
import com.kyj.fx.voeditor.visual.excels.base.ExcelDataDVO;
import com.kyj.fx.voeditor.visual.excels.base.ExcelSVO;
import com.kyj.fx.voeditor.visual.main.Main;

/**
 * @author KYJ
 */
public abstract class AbstractExcelModel implements IExcelModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractExcelModel.class);

	private Workbook excel;

	/**
     * 
     */
	public AbstractExcelModel(Workbook excel) {

		this.excel = excel;

	}

	/**
	 * 2014. 11. 4. KYJ
	 * 
	 * @param
	 * @return
	 * @throws Exception
	 * @처리내용 : 입력된 엑셀파일로부터 ExcelSVO객체를 생성하여 반환한다.
	 */
	public ExcelSVO work() throws Exception {

		// 결과반환용 SVO
		ExcelSVO excelSVO = new ExcelSVO();
		// 컬럼부

		/* 시작 엑셀관련 메타정보 처리객체 */
		FormulaEvaluator evaluator = excel.getCreationHelper().createFormulaEvaluator();
		DecimalFormat df = new DecimalFormat();
		/* 끝 엑셀관련 메타정보 처리객체 */
		for (int sheetIndex = 0; sheetIndex < excel.getNumberOfSheets(); sheetIndex++) {
			Sheet sheetAt = excel.getSheetAt(sheetIndex);
			String sheetName = sheetAt.getSheetName();

			List<ExcelColDVO> columnDVOList = new ArrayList<ExcelColDVO>();
			excelSVO.setColDvoList(sheetName, columnDVOList);
			int maxColumIndex = 0;
			// 시작 데이터부 처리
			List<ExcelDataDVO> arrayList = new ArrayList<ExcelDataDVO>();
			// 컬럼부에 정의되어야하는데 없음. 데이터부에는 존재할경우 컬럼부를 추가하기 위한 플래그
			boolean existsOutOfColumn = false;
			// while (rowIterator.hasNext())
			for (int row = 0; row < sheetAt.getLastRowNum(); row++) {
				Row next = sheetAt.getRow(row);

				if (next != null) {
					short lastCellNum = next.getLastCellNum();
					for (int col = 0; col < lastCellNum; col++) {
						Cell cell = next.getCell(col);
						if (cell != null) {
							// 엑셀 셀
							// Cell cell = cellIterator.next();
							CellStyle cellStyle = cell.getCellStyle();
							Color fillBackgroundColorColor = cellStyle.getFillBackgroundColorColor();
							String backColorRgb = "";
							if (fillBackgroundColorColor instanceof HSSFColor) {
								HSSFColor backColor = (HSSFColor) fillBackgroundColorColor;
								backColorRgb = backColor.getHexString();
							} else if (fillBackgroundColorColor instanceof XSSFColor) {
								XSSFColor backColor = (XSSFColor) fillBackgroundColorColor;
								backColorRgb = backColor.getARGBHex();
							}

							int cellType = cell.getCellType();
							String stringCellValue = "";

							switch (cellType) {
								case Cell.CELL_TYPE_FORMULA :
									if (!(cell.toString() == "")) {
										if (evaluator.evaluateFormulaCell(cell) == 0) {
											double fddata = cell.getNumericCellValue();
											stringCellValue = String.valueOf(fddata);
										} else if (evaluator.evaluateFormulaCell(cell) == 1) {
											stringCellValue = cell.getStringCellValue();
										} else if (evaluator.evaluateFormulaCell(cell) == 4) {
											boolean fbdata = cell.getBooleanCellValue();
											stringCellValue = String.valueOf(fbdata);
										}
										break;
									}
									stringCellValue = cell.getCellFormula();

									break;

								case Cell.CELL_TYPE_BLANK :
									/* N/A */

									break;
								case Cell.CELL_TYPE_BOOLEAN :
									stringCellValue = String.valueOf(cell.getBooleanCellValue());
									break;
								case Cell.CELL_TYPE_ERROR :

									/* N/A */
									break;
								case Cell.CELL_TYPE_NUMERIC :

									// double numericCellValue =
									// cell.getNumericCellValue();

									String dateFormatString = cellStyle.getDataFormatString();

									short dataFormat = cellStyle.getDataFormat();
									boolean internalDateFormat = HSSFDateUtil.isInternalDateFormat(dataFormat);
									double numericCellValue = cell.getNumericCellValue();
									boolean validExcelDate = HSSFDateUtil.isValidExcelDate(numericCellValue);
									boolean cellDateFormatted = HSSFDateUtil.isCellDateFormatted(cell);
									boolean cellInternalDateFormatted = HSSFDateUtil.isCellInternalDateFormatted(cell);

									LOGGER.debug("sheet : [ " + sheetName + " ] dateFormatString : [ " + dateFormatString
											+ " ] dataFormat : [ " + dataFormat + " ] isInternalDateFormat : [ " + internalDateFormat
											+ " ] validExcelDate : [ " + validExcelDate + " ] cellDateFormatted : [ " + cellDateFormatted
											+ " ]  cellInternalDateFormatted : [" + cellInternalDateFormatted + " ] numericCellValue : [ "
											+ numericCellValue + " ] rowIndex : [ " + row + " ] columnIndex : [ " + col + " ]");

									if (cellDateFormatted || cellInternalDateFormatted
											|| (!"GENERAL".equals(dateFormatString.toUpperCase()))) {
										Date date = cell.getDateCellValue();
										LOGGER.debug("dateFmt : %s", dateFormatString);

										stringCellValue = new CellDateFormatter(dateFormatString).format(date);

									} else {
										double ddata = cell.getNumericCellValue();
										stringCellValue = df.format(ddata);
									}

									break;
								case Cell.CELL_TYPE_STRING :
									stringCellValue = cell.getStringCellValue();
									break;

								default :

									/* N/A */
									break;

							}
							// 시작 컬럼부 처리
							if (row == 0) {
								short alignment = cellStyle.getAlignment();
								int columnWidth = sheetAt.getColumnWidth(col);
								columnDVOList.add(new ExcelColDVO(col, stringCellValue, columnWidth, alignment));
							}
							// 끝 컬럼부 처리
							ExcelDataDVO excelDataDVO = new ExcelDataDVO(row, col, stringCellValue, backColorRgb);
							arrayList.add(excelDataDVO);
						} else {
							ExcelDataDVO excelDataDVO = new ExcelDataDVO(row, col, "");
							arrayList.add(excelDataDVO);
						}
					}// end for

				} else {
					ExcelDataDVO excelDataDVO = new ExcelDataDVO(row, 0, "");
					arrayList.add(excelDataDVO);
				}

			} // end for

			// 끝 데이터부 처리
			excelSVO.addSheetExcelDVO(sheetName, arrayList);

		}
		return excelSVO;

	}
}
