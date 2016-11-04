/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.chart.service
 *	작성일   : 2016. 11. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.chart.service;

import javafx.scene.chart.XYChart.Data;

/**
 * 차트 마우스 오버시 보여주는 툴팁 데이터에 대한
 * 구조를 정의함.
 * @author KYJ
 *
 */
public class ChartOverTooltip {

	private int columnIndex;

	private String columnName;

	private Data<String, Number> data;

	public ChartOverTooltip(int columnIndex, String columnName, Data<String, Number> data) {
		this.columnName = columnName;
		this.columnIndex = columnIndex;
		this.data = data;
	}

	/**
	 * @return the columnName
	 */
	public final String getColumnName() {
		return columnName;
	}

	/**
	 * @return the columnIndex
	 */
	public final int getColumnIndex() {
		return columnIndex;
	}

	/**
	 * @return the data
	 */
	public final Data<String, Number> getData() {
		return data;
	}

	/**
	 * @param columnName the columnName to set
	 */
	public final void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	/**
	 * @param columnIndex the columnIndex to set
	 */
	public final void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	/**
	 * @param data the data to set
	 */
	public final void setData(Data<String, Number> data) {
		this.data = data;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s %s %s", data.getXValue(), columnName, data.getYValue().intValue());
	}

}
