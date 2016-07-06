/**
 * 
 */
package com.kyj.fx.voeditor.visual.excels.models;

import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author KYJ
 */
public class XlsModel extends AbstractExcelModel {

	public XlsModel(Workbook excelFile) throws IOException {
		super(excelFile);
	}

}
