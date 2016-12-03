/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.config.skin
 *	작성일   : 2016. 12. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.config.skin;

import com.kyj.fx.voeditor.visual.momory.SkinManager;

/**
 *
 * Custom Skin을 생성하기 위한 템플릿.
 * 
 * @author KYJ
 *
 */
final public class SkinTemplate {

//	private static String skinTemplate;

	/*********************************************************************/
	// 스킨템플릿이 사용되는 키를 리턴.
	/*********************************************************************/
	public static String KEY_TAB = "tab-gargoyle";
	public static String KEY_TAB_OPACITY = "tab-opacity-gargoyle";
	
	public static String KEY_HBOX = "hbox-gargoyle";
	public static String KEY_HBOX_OPACITY = "hbox-opacity-gargoyle";
	
	public static String MENU_BAR = "menubar-gargoyle";
	public static String MENU_BAR_OPACITY = "menubar-opacity-gargoyle";
	
	public static final String MENU_BAR_LABEL = "menubar-label-gargoyle";
	public static final String MENU_BAR_LABEL_OPACITY = "menubar-label-opacity-gargoyle";
	
	public static String MENU_TAB_SELECTED = "tab-selected-gargoyle";
	public static String MENU_TAB_SELECTED_OPACITY = "tab-selected-opacity-gargoyle";
	
	
	public static String MENU_TAB_UNSELECTED = "tab-unselected-gargoyle";
	public static String MENU_TAB_UNSELECTED_OPACITY = "tab-unselected-opacity-gargoyle";

	/**
	 * 스킨 템플릿정보를 리턴.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 3.
	 * @return
	 */
	public static final String getSkinTemplate() {
		return SkinManager.getInstance().getSkinTemplate();
	}

//	static {
//		skinTemplate = SkinManager.getInstance().getSkinTemplate();
//	}

}
