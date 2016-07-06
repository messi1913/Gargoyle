/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2016. 1. 20.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import com.sun.javafx.scene.control.skin.TreeViewSkin;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;

/**
 * @author KYJ
 *
 */
public class FxControlTreeViewSkin extends TreeViewSkin<Node> {

	public FxControlTreeViewSkin(FxControlTreeView treeView) {
		super(treeView);
		
		//트리 아이템 구현
		FxControlsTreeItem fxControlsTreeItem = new FxControlsTreeItem(treeView);
		TreeItem<Node> createNode = fxControlsTreeItem.createNode(treeView.getItem());
		treeView.setRoot(createNode);
	}

}
