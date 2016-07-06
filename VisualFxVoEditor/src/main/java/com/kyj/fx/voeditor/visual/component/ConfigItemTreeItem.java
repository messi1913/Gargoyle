/**
 * KYJ
 * 2015. 10. 12.
 */
package com.kyj.fx.voeditor.visual.component;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import com.kyj.fx.voeditor.visual.main.model.vo.ConfigurationLeafNodeItem;
import com.kyj.fx.voeditor.visual.main.model.vo.ConfigurationTreeItem;

/**
 * @author KYJ
 *
 */
public class ConfigItemTreeItem {

	/**
	 * 파일 트리를 생성하기 위한 노드를 반환한다.
	 *
	 * @Date 2015. 10. 14.
	 * @param f
	 * @return
	 * @User KYJ
	 */
	public TreeItem<ConfigurationTreeItem> createNode(final ConfigurationTreeItem f) {
		TreeItem<ConfigurationTreeItem> treeItem = new TreeItem<ConfigurationTreeItem>(f) {
			private boolean isLeaf;
			private boolean isFirstTimeChildren = true;
			private boolean isFirstTimeLeaf = true;

			@Override
			public ObservableList<TreeItem<ConfigurationTreeItem>> getChildren() {
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
					ConfigurationTreeItem f = getValue();
					if (f instanceof ConfigurationLeafNodeItem)
						isLeaf = true;
					else
						isLeaf = false;
				}
				return isLeaf;
			}

			private ObservableList<TreeItem<ConfigurationTreeItem>> buildChildren(TreeItem<ConfigurationTreeItem> treeItem) {

				ConfigurationTreeItem f = treeItem.getValue();
				if (f == null) {
					return FXCollections.emptyObservableList();
				}

				List<ConfigurationTreeItem> childrens = f.getChildrens();
				if (childrens.isEmpty() || f instanceof ConfigurationLeafNodeItem) {
					return FXCollections.emptyObservableList();
				}

				else {
					ObservableList<TreeItem<ConfigurationTreeItem>> children = FXCollections.observableArrayList();
					for (ConfigurationTreeItem child : childrens) {
						TreeItem<ConfigurationTreeItem> createNode = createNode(child);
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