/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.menu
 *	작성일   : 2015. 10. 17.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.menu;

/**
 * 메뉴 설정관리 인터페이스
 * @author KYJ
 *
 */
public interface IMenuConfiguration {
	/**
	 * KYJ
	 * 
	 * @return the menuId
	 */
	public String getMenuId();

	/**
	 * KYJ
	 * 
	 * @return the menuPath
	 */
	public String getMenuPath();

	/**
	 * KYJ
	 * 
	 * @return the menuName
	 */
	public String getMenuName();

}
