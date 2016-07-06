/**
 * package : com.kyj.fx.voeditor.visual.functions
 *	fileName : ToExcelFunction.java
 *	date      : 2015. 11. 22.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.functions;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.kyj.fx.voeditor.visual.excels.base.ExcelColDVO;
import com.kyj.fx.voeditor.visual.excels.base.ExcelDataDVO;
import com.kyj.fx.voeditor.visual.excels.base.ExcelSVO;
import com.kyj.fx.voeditor.visual.util.ExcelUtil;

import javafx.scene.control.TableColumn;

/**
 * Excel로 변환
 *
 * @author KYJ
 *
 */
public class ToExcelFileFunction {

	public <T> boolean generate0(File saveFile, List<String> columns, List<Map<String, Object>> param) {
		List<ExcelColDVO> cols = new ArrayList<>();
		if (columns != null && !columns.isEmpty()) {
			for (int i = 0; i < columns.size(); i++) {
				String column = columns.get(i);
				cols.add(new ExcelColDVO(i, column));
			}
		}
		return generate(saveFile, cols, param);
	}

	public boolean generate2(File saveFile, @SuppressWarnings("rawtypes") List<TableColumn<?, ?>> columns,
			List<Map<String, Object>> param) {

		List<ExcelColDVO> cols = new ArrayList<>();

		if (columns != null && !columns.isEmpty()) {

			for (int i = 0; i < columns.size(); i++) {
				String column = columns.get(i).getText();
				cols.add(new ExcelColDVO(i, column));
			}
		}
		return generate(saveFile, cols, param);
	}

	public boolean generate(File saveFile, List<ExcelColDVO> columns, List<Map<String, Object>> param) {

		ExcelSVO excelSVO = new ExcelSVO();
		String sheetName = "sheet1";

		if (columns != null && !columns.isEmpty()) {
			excelSVO.setColDvoList(sheetName, columns);
		}
		List<ExcelDataDVO> dataList = new ArrayList<>();
		for (int i = 0; i < param.size(); i++) {
			Map<String, Object> map = param.get(i);

			Iterator<String> iterator = map.keySet().iterator();
			int col = 0;
			while (iterator.hasNext()) {
				String key = iterator.next();
				Object value = map.get(key);
				dataList.add(new ExcelDataDVO(i, col, value));
				col++;
			}
		}

		excelSVO.addSheetExcelDVO(sheetName, dataList);

		try {
			ExcelUtil.createExcel(saveFile.getAbsolutePath(), excelSVO, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

}
