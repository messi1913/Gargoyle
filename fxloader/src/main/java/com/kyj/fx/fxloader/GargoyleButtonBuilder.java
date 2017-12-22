/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 12. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.fxloader;

import javafx.scene.control.Button;
import javafx.util.Builder;

/**
 *
 * 전체 버튼에 대한 공통기능을 적용하기 위한 처리
 *
 * @author KYJ
 *
 */
public class GargoyleButtonBuilder extends Button implements Builder<Button> {

	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(GargoyleButtonBuilder.class);

	@Override
	public Button build() {
		return this;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 3.
	 * @param btn
	 * @param styleClassName
	 * @return
	 */
	public static boolean applyStyleClass(Button btn, String styleClassName) {
		if (ValueUtil.isNotEmpty(styleClassName)) {
			btn.getStyleClass().add(styleClassName);
			return true;
		}
		return false;

	}

}
