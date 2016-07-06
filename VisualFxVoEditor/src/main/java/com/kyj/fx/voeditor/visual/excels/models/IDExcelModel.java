package com.kyj.fx.voeditor.visual.excels.models;

import java.util.List;

/**
 * 엑셀 파일 읽기 관련 인터페이스
 *
 * @author KYJ
 *
 */
public interface IDExcelModel {
	public <T> List<T> work(Class<T> classType, int SheetIndex, IDExcelData<ExtracterProperty> bindCellModel, IdDataMapping<T> mapping)
			throws Exception;

}
