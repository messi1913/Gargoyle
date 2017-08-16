/**
 * 
 */
package com.kyj.fx.voeditor.visual.component.capture;

import javafx.scene.paint.Color;

/**
 * 색상변경 요청이 들어옴
 * 
 * @author KYJ
 *
 */
public interface ColorChange {

	/**
	 * 백그라운드 변경 요청
	 * 
	 * @param newColor
	 */
	public void requestBackgroundColorChange(Color newColor);

	/**
	 * 텍스트 색상 변경 요청
	 * 
	 * @param newColor
	 */
	public void requestTextFillChange(Color newColor);

	/**
	 * 테두리 색상 변경 요청
	 * 
	 * @param newColor
	 */
	public void requestBorderColorChange(Color newColor);

}
