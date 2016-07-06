package com.kyj.fx.voeditor.visual.excels.models;


public interface IDExcelData<Property> {

	public Property toProperty(int row, int col, Object data);
}
