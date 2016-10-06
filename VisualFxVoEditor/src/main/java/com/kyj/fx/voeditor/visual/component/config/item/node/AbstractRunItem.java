/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.config.item.node
 *	작성일   : 2016. 10. 5.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.config.item.node;

import javafx.scene.image.Image;

/**
 * @author KYJ
 *
 */
public abstract class AbstractRunItem implements IRunableItem, IRunableItemAction {

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.config.item.node.IRunableItem#getName()
	 */
	@Override
	public String getName() {
		return getRunableType().name();
	}

	/**
	 * ListView에 보여지게 될 DisplayName
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 6.
	 * @return
	 */
	public String getDisplayName() {
		return getClassPackageName();
	}

	/**
	 * class package name
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 6.
	 * @return
	 */
	public abstract String getClassPackageName();

	/**
	 * 대표 이미지
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 6.
	 * @return
	 */
	public abstract Image getImage();

}
