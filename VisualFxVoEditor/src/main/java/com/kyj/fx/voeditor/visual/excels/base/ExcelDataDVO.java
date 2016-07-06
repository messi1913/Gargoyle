package com.kyj.fx.voeditor.visual.excels.base;

import java.awt.Color;

public class ExcelDataDVO {

	private int row;

	private int col;

	private Object data;

	private Color backgroundColor;

	@Deprecated
	private String backColorRgb;

	public ExcelDataDVO(int row, int col, Object data) {

		this.row = row;
		this.col = col;
		this.data = data;
	}

	public ExcelDataDVO(int rowIndex, int columnIndex, String stringCellValue, String backColorRgb) {
		this(rowIndex, columnIndex, stringCellValue);
		this.backColorRgb = backColorRgb;
	}

	public ExcelDataDVO(int rowIndex, int columnIndex, String stringCellValue, Color backgroundColor) {
		this(rowIndex, columnIndex, stringCellValue);
		this.backgroundColor = backgroundColor;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * @return the backgroundColor
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * @param backgroundColor
	 *            the backgroundColor to set
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	/**
	 * @return the backColorRgb
	 */
	public String getBackColorRgb() {
		return backColorRgb;
	}

	/**
	 * @param backColorRgb
	 *            the backColorRgb to set
	 */
	public void setBackColorRgb(String backColorRgb) {
		this.backColorRgb = backColorRgb;
	}

	@Override
	public String toString() {
		return "ExcelDataDVO [row=" + row + ", col=" + col + ", data=" + data + "]";
	}

}
