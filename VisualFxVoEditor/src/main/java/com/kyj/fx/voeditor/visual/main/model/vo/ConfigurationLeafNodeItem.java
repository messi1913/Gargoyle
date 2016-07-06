/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.layout
 *	작성일   : 2015. 11. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main.model.vo;


/**
 * @author KYJ
 *
 */
public class ConfigurationLeafNodeItem extends ConfigurationTreeItem {

	private Class<?> contentNode;

	/**
	 * @return the contentNode
	 */
	public Class<?> getContentNode() {
		return contentNode;
	}

	/**
	 * @param contentNode
	 *            the contentNode to set
	 */
	public void setContentNode(Class<?> contentNode) {
		this.contentNode = contentNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return super.toString();
	}

}
