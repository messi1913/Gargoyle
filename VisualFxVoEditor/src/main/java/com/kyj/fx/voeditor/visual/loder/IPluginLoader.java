/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.loder
 *	작성일   : 2015. 12. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.loder;

import java.util.List;

/**
 * @author KYJ
 *
 */
public interface IPluginLoader {

	/**
	 * 플러그인이에 대한 정보
	 * @최초생성일 2016. 4. 13.
	 */
	public static final String PLUGIN_INI = "plugin.ini";
	/**
	 * 플러그인에 대한 파일 형태
	 * @최초생성일 2016. 4. 13.
	 */
	public static final String PLUGIN_EXTENSION = ".jar";
	/**
	 * 플러그인이 존재해야하는 디렉토리명
	 * @최초생성일 2016. 4. 13.
	 */
	public static final String PLUGIN_DIR = "plugins";
	// 메인UI 위치정보
	public static final String KEY_DISPLAY_JAVAFX_NODE_CLASS = "display.javafx.node.class";
	// 설정화면 위치정보
	public static final String KEY_DISPLAY_CONFIG_JAVAFX_NODE_CLASS = "display.javafx.config.node.class";
	// UI에 보여주기 위한 메뉴명
	public static final String KEY_DISPLAY_MENU_NAME = "display.menu.name";
	// 플러그인을 띄우기 위해 메뉴에 위치시킬 패스정보
	public static final String KEY_DISPLAY_MENU_PATH = "display.menu.path";

	// 설정정보가 존재한다면 설정정보에서 위치될떄 보여줄 이름
	public static final String KEY_DISPLAY_CONFIG_NAME = "display.config.name";

	/**
	 * 메인탭에 Parent가 로드될때 처리할 리스너를 등록할 클래스를 정의.
	 * @최초생성일 2016. 6. 16.
	 */
	public static final String ADD_ON_PARENT_LOADED_LISTENER = "add.on.parent.loaded.listener";


	public static final String SET_ON_PARENT_BEFORE_LOADED_LISTENER = "add.on.parent.before.loaded.listener";

	public static final String PLUGIN_DESC = "plugin.desc";


	/**
	 * 플러그인 정보를 로드함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 18.
	 * @return
	 */
	List<JarWrapper> load();
}
