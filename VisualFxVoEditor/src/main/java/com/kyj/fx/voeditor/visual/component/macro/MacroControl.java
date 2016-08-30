/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.macro
 *	작성일   : 2016. 8. 30.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.macro;

import java.util.List;
import java.util.Map;

import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * @author KYJ
 *
 */
public class MacroControl extends Control {

	/* (non-Javadoc)
	 * @see javafx.scene.control.Control#createDefaultSkin()
	 */
	@Override
	protected Skin<?> createDefaultSkin() {
		return new MacroBaseSkin(this);
	}

	/**
	 * Start 버튼을 클릭하면 결과가 리턴된다.
	 * param으로 입력받은 데이터는 textArea에서 적혀져있는 텍스트문자열.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 30.
	 * @param param
	 * @return
	 */
	public <K, V> List<Map<K, V>> start(String param) {
		return null;
	}

	/**
	 * 매크로 동작을 멈춘다.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 30.
	 * @return
	 */
	public boolean stop() {
		return false;
	}
}
