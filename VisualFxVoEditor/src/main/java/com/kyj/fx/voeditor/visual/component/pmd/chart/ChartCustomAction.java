/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.pmd.chart
 *	작성일   : 2016. 10. 24.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.pmd.chart;

import javafx.scene.Node;

/**
 *
 * 차트 노드들에 대한 커스텀 인터페이스 제공
 * @author KYJ
 *
 */
public interface ChartCustomAction<T> {

	/**
	 * PI차트의 그래픽 아이템 노드를 선택한 경우 발생됨.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 24.
	 * @param t
	 * @param u
	 */
	public void chartGraphicsCustomAction(T t, Node u);

	/**
	 * 시리즈 라벨 action 처리.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 24.
	 * @param data
	 * @param node
	 */
	public void seriesLegendLabelCustomAction(T data, Node node);
}
