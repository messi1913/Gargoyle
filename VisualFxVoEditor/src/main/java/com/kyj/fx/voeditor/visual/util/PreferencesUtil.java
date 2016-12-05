/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 2. 13.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.util.prefs.Preferences;

/**
 * Gagoyle 레지스트리 관련 처리 유틸리티
 *
 * @author KYJ
 *
 */
public class PreferencesUtil {

	/**
	 * 스킨이 존재하는 위치
	 * @최초생성일 2016. 12. 3.
	 */
	public static final String KEY_LAST_SELECTED_PATH = "last.selected.path";

	/**
	 * 사용자가 선택한 스킨명
	 * @최초생성일 2016. 12. 3.
	 */
	public static final String KEY_USER_SKIN_NAME = "user.skin.name";

	/**
	 * 사용자가 선택한 버튼 스타일 클래스 파일 명
	 * @최초생성일 2016. 12. 3.
	 */
	public static final String KEY_USER_BUTTON_STYLECLASS_FILE_NAME = "user.button.syleclass.file.name";

	/**
	 *
	 */
	private PreferencesUtil() {
	}

	/**
	 * Gagoyle에서 사용하는 기본 경로를 리턴
	 *
	 * @return
	 */
	public static Preferences getDefault() {
		Preferences userRoot = Preferences.userRoot();
		return userRoot.node("com").node("kyj").node("gagoyle");
	}
}
