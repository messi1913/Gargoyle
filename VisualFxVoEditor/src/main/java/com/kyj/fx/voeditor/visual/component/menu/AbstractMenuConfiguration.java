/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.menu
 *	작성일   : 2015. 10. 17.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.menu;

/**
 * 메뉴설정관리 추상클래스
 *
 * @author KYJ
 *
 */
public class AbstractMenuConfiguration implements IMenuConfiguration {
	private String menuId;
	private String menuPath;
	private String menuName;

	/**
	 * KYJ
	 *
	 * @param menuId
	 *            메뉴 ID
	 * @param menuPath
	 *            메뉴 경로
	 * @param menuName
	 *            메뉴명
	 */
	public AbstractMenuConfiguration(String menuId, String menuPath, String menuName) {
		super();
		this.menuId = menuId;
		this.menuPath = menuPath;
		this.menuName = menuName;
	}

	/**
	 * KYJ
	 *
	 * @return the menuId
	 */
	public String getMenuId() {
		return menuId;
	}

	/**
	 * KYJ
	 *
	 * @return the menuPath
	 */
	public String getMenuPath() {
		return menuPath;
	}

	/**
	 * KYJ
	 *
	 * @return the menuName
	 */
	public String getMenuName() {
		return menuName;
	}

}
