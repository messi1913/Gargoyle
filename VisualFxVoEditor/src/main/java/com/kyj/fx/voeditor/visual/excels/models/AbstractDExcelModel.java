/**
 *
 */
package com.kyj.fx.voeditor.visual.excels.models;

import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.kyj.fx.voeditor.visual.util.ExcelUtil;

import javafx.collections.FXCollections;

/**
 * @author KYJ
 */
public abstract class AbstractDExcelModel implements IDExcelModel {

	private Workbook excel;

	/**
	 *
	 */
	public AbstractDExcelModel(Workbook excel) {
		this.excel = excel;
	}

	public final Workbook getWorkbook() {
		return this.excel;
	}

	/**
	 * @param classType
	 *            반환받고하자는 클래스 타입
	 * @param sheetIndex
	 *            엑셀 시트번호
	 * @param bindCellModel
	 *            셀데이터를 이용해서 처리할 내용
	 * @param mapping
	 *            데이터 실제 맵핑
	 * @return 결과 List
	 * @throws Exception
	 */
	public <T> List<T> work(Class<T> type, int sheetIndex, IDExcelData<ExtracterProperty> bindCellModel, IdDataMapping<T> mapping)
			throws Exception {
		return work(type, sheetIndex, 0, bindCellModel, mapping);
	}

	/**
	 * @param type
	 *            반환받고하자는 클래스 타입
	 * @param sheetIndex
	 *            엑셀 시트번호
	 * @param startRow
	 *            시작로우
	 * @param bindCellModel
	 *            셀데이터를 이용해서 처리할 내용
	 * @param mapping
	 *            데이터 실제 맵핑
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> work(Class<T> type, int sheetIndex, int startRow, IDExcelData<ExtracterProperty> bindCellModel,
			IdDataMapping<T> mapping) throws Exception {

		List<T> resultList = FXCollections.observableArrayList(); // new
																	// ArrayList<T>();
		Sheet sheetAt = excel.getSheetAt(sheetIndex);

		// while (rowIterator.hasNext())
		for (int row = startRow; row <= sheetAt.getLastRowNum(); row++) {
			Row next = sheetAt.getRow(row);
			T newInstance = type.newInstance();
			short lastCellNum = next.getLastCellNum();

			for (int col = 0; col < lastCellNum; col++) {
				Cell cell = next.getCell(col);

				ExtracterProperty property = bindCellModel.toProperty(row, col, cell);
				mapping.doMapping(newInstance, property);

			} // end for

			resultList.add(newInstance);

		} // end for

		return resultList;

	}

	/**
	 * @param type
	 *            반환받고하자는 클래스 타입
	 * @param sheetIndex
	 *            엑셀 시트번호
	 * @param startRow
	 *            시작로우
	 * @param bindCellModel
	 *            셀데이터를 이용해서 처리할 내용
	 * @param mapping
	 *            데이터 실제 맵핑
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> work(Class<T> type, int sheetIndex, int startRow, DirectDataMapping<T> mapping) throws Exception {

		List<T> resultList = FXCollections.observableArrayList(); // new
																	// ArrayList<T>();
		Sheet sheetAt = excel.getSheetAt(sheetIndex);

		// while (rowIterator.hasNext())
		for (int row = startRow; row <= sheetAt.getLastRowNum(); row++) {
			Row next = sheetAt.getRow(row);
			T newInstance = type.newInstance();
			short lastCellNum = next.getLastCellNum();

			for (int col = 0; col < lastCellNum; col++) {
				Cell cell = next.getCell(col);
				mapping.doMapping(newInstance, row, col, ExcelUtil.getValue(cell));
			} // end for

			resultList.add(newInstance);

		} // end for

		return resultList;

	}
}
