package com.kyj.fx.voeditor.visual.excels.models;

import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import com.kyj.fx.voeditor.visual.excels.models.AbstractDExcelModel;
import com.kyj.fx.voeditor.visual.excels.models.IDExcelData;
import com.kyj.fx.voeditor.visual.excels.models.IdDataMapping;

public class DXlsxModel extends AbstractDExcelModel {

	public DXlsxModel(Workbook excel) {
		super(excel);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * RottoAnalysis.rotto.analysis.model.dexcel.AbstractDExcelModel#work(java
	 * .lang.Class, int, RottoAnalysis.rotto.analysis.model.dexcel.IDExcelData,
	 * RottoAnalysis.rotto.analysis.model.dexcel.IDExcelDataMapping)
	 */
	@Override
	public <T> List<T> work(Class<T> type, int SheetIndex, IDExcelData<ExtracterProperty> bindCellModel, IdDataMapping<T> mapping)
			throws Exception {
		return super.work(type, SheetIndex, bindCellModel, mapping);
	}

}
