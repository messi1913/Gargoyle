/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.macro
 *	작성일   : 2016. 9. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.macro;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/***************************
 * 
 * @author KYJ
 *
 ***************************/
public class MacroFavorTreeItem extends TreeItem<MacroItemVO> {

	/**
	 * 파일 트리를 생성하기 위한 노드를 반환한다.
	 *
	 * @Date 2015. 10. 14.
	 * @param f
	 * @return
	 * @User KYJ
	 */
	public static TreeItem<MacroItemVO> createNode(final MacroItemVO f) {
		TreeItem<MacroItemVO> treeItem = new TreeItem<MacroItemVO>(f) {
			private boolean isLeaf;
			private boolean isFirstTimeChildren = true;
			private boolean isFirstTimeLeaf = true;

			@Override
			public ObservableList<TreeItem<MacroItemVO>> getChildren() {
				if (isFirstTimeChildren) {
					isFirstTimeChildren = false;
					super.getChildren().setAll(buildChildren(this));
				}
				return super.getChildren();
			}

			@Override
			public boolean isLeaf() {
				if (isFirstTimeLeaf) {
					isFirstTimeLeaf = false;
					MacroItemVO f = getValue();

					if (f.getType() == MACRO_ITEM_TYPE.DIR || f.getChildrens().isEmpty())
						isLeaf = true;
					else
						isLeaf = false;
				}
				return isLeaf;
			}

			private ObservableList<TreeItem<MacroItemVO>> buildChildren(TreeItem<MacroItemVO> treeItem) {

				MacroItemVO f = treeItem.getValue();
				if (f == null) {
					return FXCollections.emptyObservableList();
				}

				List<MacroItemVO> childrens = f.getChildrens();
				if (f.getType() == MACRO_ITEM_TYPE.FILE || childrens.isEmpty()) {
					return FXCollections.emptyObservableList();
				}

				else {
					ObservableList<TreeItem<MacroItemVO>> children = FXCollections.observableArrayList();
					for (MacroItemVO child : childrens) {
						TreeItem<MacroItemVO> createNode = createNode(child);
						createNode.setExpanded(true);
						children.add(createNode);
					}

					return children;
				}
			}

		};
		
		
		return treeItem;
	}

}
