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

	public static final String KEY_LAST_SELECTED_PATH = "last.selected.path";

	public static final String KEY_USER_SKIN_NAME = "user.skin.name";

	/**
	 *
	 */
	private PreferencesUtil() {
		// TODO Auto-generated constructor stub
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
