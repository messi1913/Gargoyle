/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework
 *	작성일   : 2016. 11. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.adapter;

import javafx.scene.chart.XYChart.Data;

/**
 *
 * 데이터 변환으로 사용되는 어댑터 인터페이스
 *
 * @author KYJ
 *
 */
public interface IGargoyleChartAdapter<T, C> {

	/**
	 * 차트 타이틀
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 2.
	 * @param t
	 * @return
	 */
	public String getTitle(T t);

	/**
	 * 컬럼명리턴
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 2.
	 * @param t
	 * @param seq
	 * @return
	 */
	public String getColumnName(T t, int seq);

	/**
	 * 컬럼부의 갯수를 리턴함.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 2.
	 * @param t
	 * @return
	 */
	public int getColumnCount(T t);

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 2.
	 * @param t
	 * @param columnName
	 * @param row
	 * @return
	 */
	public Data<String, Number> getValue(T t, int columnIndex, String columnName, int row);

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 2.
	 * @param t
	 * @param columnName
	 * @return
	 */
	public int getValueCount(T t, String columnName);
}
