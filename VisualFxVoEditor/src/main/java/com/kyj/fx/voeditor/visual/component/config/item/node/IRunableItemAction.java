/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.config.node
 *	작성일   : 2016. 10. 5.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.config.item.node;

/**
 * @author KYJ
 *
 */
public interface IRunableItemAction {

	/**
	 *
	 * 실행처리
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 5.
	 */
	public abstract void run();

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 5.
	 */
	public abstract void revert();

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 5.
	 */
	public abstract void apply();

}
