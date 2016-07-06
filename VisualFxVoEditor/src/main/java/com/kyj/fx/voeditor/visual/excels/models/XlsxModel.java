package com.kyj.fx.voeditor.visual.excels.models;

import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;

public class XlsxModel extends AbstractExcelModel {

	public XlsxModel(Workbook excelFile) throws IOException {
		super(excelFile);
	}

}
