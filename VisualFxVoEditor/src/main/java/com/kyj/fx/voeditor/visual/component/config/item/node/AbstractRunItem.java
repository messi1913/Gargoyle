/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.config.item.node
 *	작성일   : 2016. 10. 5.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.config.item.node;

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

	public String getDisplayName() {
		return getName();
	}

}
