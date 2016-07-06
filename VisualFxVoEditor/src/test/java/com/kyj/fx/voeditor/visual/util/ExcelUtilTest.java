/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 2. 11.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import kyj.Fx.dao.wizard.core.model.vo.TableMasterDVO;
import kyj.Fx.dao.wizard.core.model.vo.TableModelDVO;

import org.apache.commons.lang.SystemUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.excels.base.ExcelColDVO;
import com.kyj.fx.voeditor.visual.excels.base.ExcelDataDVO;
import com.kyj.fx.voeditor.visual.excels.base.ExcelSVO;

/**
 * @author KYJ
 *
 */
public class ExcelUtilTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtilTest.class);
	private static File createExcelFile;

	@org.junit.Before
	public void makeExcelFile() throws Exception {

		List<TableMasterDVO> listTable = VoWizardUtil.listTable("tbm_sm_user");
		List<TableModelDVO> listColumns = VoWizardUtil.listColumns(listTable.get(0));

		File userDir = SystemUtils.getUserDir();
		createExcelFile = VoWizardUtil.createExcelFile(userDir, "TbmSmUserDVO.xlsx", listColumns, true);
	}

	/**
	 * Test method for
	 * {@link com.kyj.fx.voeditor.visual.util.ExcelUtil#toXlsxExcelSVO(java.io.File)}
	 * .
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testToXlsxExcelSVO() throws Exception {

		ExcelSVO xlsxExcelSVO = ExcelUtil.toK(createExcelFile, new BiFunction<File, Workbook, ExcelSVO>() {

			@Override
			public ExcelSVO apply(File file, Workbook xlsx) {
				ExcelSVO svo = new ExcelSVO();
				svo.setFile(file);
				int numberOfSheets = xlsx.getNumberOfSheets();
				for (int i = 0; i < numberOfSheets; i++) {
					Sheet sheetAt = xlsx.getSheetAt(i);
					String sheetName = xlsx.getSheetName(i);

					// 헤더부 처리
					{
						Row columnRow = sheetAt.getRow(2);
						short lastCellNum = columnRow.getLastCellNum();
						ArrayList<ExcelColDVO> colList = new ArrayList<>();
						for (int _cell = 0; _cell < lastCellNum; _cell++) {
							Cell cell = columnRow.getCell(_cell);
							String stringCellValue = cell.getStringCellValue();
							ExcelColDVO excelColDVO = new ExcelColDVO();
							excelColDVO.setColSeq(_cell);
							excelColDVO.setColName(stringCellValue);
							colList.add(excelColDVO);
						}
						svo.setColDvoList(sheetName, colList);
					}

					// 데이터부 처리
					for (int _row = 3; _row < sheetAt.getLastRowNum(); _row++) {
						Row row = sheetAt.getRow(_row);
						short lastCellNum = row.getLastCellNum();

						for (int _cell = 0; _cell < lastCellNum; _cell++) {
							Cell cell = row.getCell(_cell);
							String value = cell.getStringCellValue();
							svo.addSheetExcelDVO(sheetName, new ExcelDataDVO(_row, _cell, value));
						}
					}

				}
				return svo;
			}
		});

		List<TableModelDVO> list = ExcelUtil.toK(createExcelFile, new BiFunction<File, Workbook, List<TableModelDVO>>() {

			@Override
			public List<TableModelDVO> apply(File file, Workbook xlsx) {
				List<TableModelDVO> llist = new ArrayList<>();

				Sheet sheetAt = xlsx.getSheetAt(0);

				// 헤더부 처리
				Row columnRow = sheetAt.getRow(2);
				ArrayList<ExcelColDVO> colList = new ArrayList<>();
				Cell _column = columnRow.getCell(0);
				Cell _type = columnRow.getCell(1);
				Cell _size = columnRow.getCell(2);
				Cell _comment = columnRow.getCell(3);

				// 데이터부 처리
				for (int _row = 3; _row < sheetAt.getLastRowNum(); _row++) {
					Row row = sheetAt.getRow(_row);
					Cell column = row.getCell(0);
					Cell type = row.getCell(1);
					Cell size = row.getCell(2);
					Cell comment = row.getCell(3);

					TableModelDVO modelDVO = new TableModelDVO();
					modelDVO.setName(column.getStringCellValue());
					modelDVO.setDabaseTypeName(type.getStringCellValue());
					modelDVO.setSize(size.getStringCellValue());
					modelDVO.setDesc(comment.getStringCellValue());
					llist.add(modelDVO);
				}

				return llist;
			}
		});

		System.out.println(list);
	}
}
