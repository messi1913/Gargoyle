/**
 * KYJ
 * 2015. 10. 12.
 */
package com.kyj.fx.voeditor.visual.component.sql.table;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 *
 * 데이터베이스 인덱스를 조회하기 위한 트리노드 생성
 *
 * @author KYJ
 *
 */
public class IndexTreeItem {

	/**
	 * 파일 트리를 생성하기 위한 노드를 반환한다.
	 *
	 * @Date 2015. 10. 14.
	 * @param f
	 * @return
	 * @User KYJ
	 */
	public TreeItem<TableIndexNode> createNode(final TableIndexNode f) {
		TreeItem<TableIndexNode> treeItem = new TreeItem<TableIndexNode>(f) {
			private boolean isLeaf;
			private boolean isFirstTimeChildren = true;
			private boolean isFirstTimeLeaf = true;

			@Override
			public ObservableList<TreeItem<TableIndexNode>> getChildren() {
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
					TableIndexNode f = getValue();
					if (f instanceof TableIndexLeaf)
						isLeaf = true;
					else
						isLeaf = false;
				}
				return isLeaf;
			}

			private ObservableList<TreeItem<TableIndexNode>> buildChildren(TreeItem<TableIndexNode> treeItem) {

				TableIndexNode f = treeItem.getValue();
				if (f == null) {
					return FXCollections.emptyObservableList();
				}

				List<TableIndexNode> childrens = f.getChildrens();
				if (childrens.isEmpty() || f instanceof TableIndexLeaf) {
					return FXCollections.emptyObservableList();
				}

				else {
					ObservableList<TreeItem<TableIndexNode>> children = FXCollections.observableArrayList();
					for (TableIndexNode child : childrens) {
						children.add(createNode(child));
					}
					return children;
				}
			}

		};

		return treeItem;
	}
}