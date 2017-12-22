/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 2. 13.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.fxloader;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.prefs.Preferences;

/**
 * Gagoyle 레지스트리 관련 처리 유틸리티
 *
 * @author KYJ
 *
 */
public class FxPreferencesUtil {

	/**
	 * 스킨이 존재하는 위치
	 * 
	 * @최초생성일 2016. 12. 3.
	 */
	public static final String KEY_LAST_SELECTED_PATH = "last.selected.path";

	/**
	 * 사용자가 선택한 스킨명
	 * 
	 * @최초생성일 2016. 12. 3.
	 */
	public static final String KEY_USER_SKIN_NAME = "user.skin.name";

	/**
	 * 사용자가 선택한 버튼 스타일 클래스 파일 명
	 * 
	 * @최초생성일 2016. 12. 3.
	 */
	public static final String KEY_USER_BUTTON_STYLECLASS_FILE_NAME = "user.button.syleclass.file.name";

	/**
	 *
	 */
	protected FxPreferencesUtil() {
	}

	/**
	 * Gagoyle에서 사용하는 기본 경로를 리턴
	 *
	 * @return
	 */
	public static Preferences getDefault() {
		Preferences userRoot = userRoot();
		return userRoot.node("com").node("kyj").node("gagoyle");
	}

	public static Preferences userRoot() {
		return Preferences.userRoot();
	}

	public static Preferences systemRoot() {
		return Preferences.systemRoot();
	}

	/**
	 * MsOffice 경로 관련된 정보를 확인하고 리턴받음.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 21.
	 * @return
	 */
	public static Preferences getMsOffice() {
		return getRegidit(userRoot -> {

			Preferences officeNode = userRoot.node("Software").node("Microsoft").node("Office");

			try {
				String[] childrenNames = new String[] { /* "11.0", "12.0", "13.0", */ "14.0", /* "15.0", "16.0", "17.0", "18.0" */ };

				// if (null != childrenNames) {

				List<Preferences> nodeList = new ArrayList<>();
				for (String n : childrenNames) {

					// ,MsOffcie 버젼 관련 노드.
					// if (n.matches("1[1-9].0")) {
					if (!officeNode.nodeExists(n))
						continue;

					Preferences versionNode = officeNode.node(n);
//					Preferences word = versionNode.node("Word");
//					Preferences installRoot = word.node("InstallRoot");

//					String string3 = installRoot.get("Path", "");
//					Preferences node2 = word.node("Options");
//					String string = node2.get("PROGRAMDIR", "");
//					boolean userNode = node2.isUserNode();

//					String string2 = installRoot.get("Path", "");

					if (!versionNode.nodeExists("Word")) {
						continue;
					}

					Preferences wordNode = versionNode.node("Options");
					if (!versionNode.nodeExists("Options")) {
						continue;
					}

					Preferences optionNode = wordNode.node("Options");
					nodeList.add(optionNode);
				}

				// 최신버젼부터 체크하기 위함.
				for (int i = nodeList.size() - 1; i >= 0; i--) {
					Preferences _node = nodeList.get(i);
					String dir = _node.get("PROGRAMDIR", "");
					if (ValueUtil.isNotEmpty(dir))
						return _node;

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		});
	}

	private static Preferences getRegidit(Function<Preferences, Preferences> findVal) {
		return findVal.apply(userRoot());
	}

}
