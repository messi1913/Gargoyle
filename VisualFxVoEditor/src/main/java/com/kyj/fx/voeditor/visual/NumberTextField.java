/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual
 *	작성일   : 2016. 8. 30.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual;

import javafx.scene.control.TextField;

/***************************
 * 
 * 숫자만 입력가능한 텍스트 필드 정의
 * 
 * @author KYJ
 *
 ***************************/
public class NumberTextField extends TextField {

	public NumberTextField(String text) {

		//값의 유효성을 판단해서 결과 처리
		this.textProperty().addListener((oba, o, n) -> {
			if (!isValid(n)) {
				setText(o);
			}
		});

		this.setText(text);

	}

	/********************************
	 * 작성일 : 2016. 8. 30. 작성자 : KYJ
	 *
	 * 입력값이 유효한지 판단.
	 * 
	 * @param value
	 * @return
	 ********************************/
	private boolean isValid(final String value) {
		// 음수
		if (value == null || value.length() == 0 || value.equals("-") || value.equals(".")) {
			return true;
		}

		try {
			new Double(value);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
}
