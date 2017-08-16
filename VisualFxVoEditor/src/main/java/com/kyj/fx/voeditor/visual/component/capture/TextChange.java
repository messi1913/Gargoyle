/**
 * 
 */
package com.kyj.fx.voeditor.visual.component.capture;

import javafx.scene.text.Font;

/**
 * @author KYJ
 *
 */
public interface TextChange {

	/**
	 * 폰트에 대한 변경 요청
	 * 
	 * @param font
	 */
	public void fontChange(Font font);

	/**
	 * 텍스트 내용에 대한 변경 요청
	 * 
	 * @param text
	 */
	public void textChange(String text);
}
