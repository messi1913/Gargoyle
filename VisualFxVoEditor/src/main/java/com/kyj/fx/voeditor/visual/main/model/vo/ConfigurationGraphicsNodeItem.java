/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.model.vo
 *	작성일   : 2016. 12. 1.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main.model.vo;

/**
 *
 * Leaf노드가 아니면서 그래픽 처리를 담당하는 트리 중간노드
 * @author KYJ
 *
 */
public class ConfigurationGraphicsNodeItem extends ConfigurationTreeItem {

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
}
