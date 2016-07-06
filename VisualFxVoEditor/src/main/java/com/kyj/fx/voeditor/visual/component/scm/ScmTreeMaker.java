/********************************
 *	프로젝트 : dim.mk.tree
 *	패키지   : com.kyj.dim.mk.tree.fx.component
 *	작성일   : 2015. 12. 7.
 *	프로젝트 : Gagoyle
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.scm;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @author KYJ
 *
 */
public class ScmTreeMaker {
	private static Logger LOGGER = LoggerFactory.getLogger(ScmTreeMaker.class);

	/**
	 * 파일 트리를 생성하기 위한 노드를 반환한다.
	 *
	 * @Date 2015. 10. 14.
	 * @param f
	 * @return
	 * @User KYJ
	 */
	public TreeItem<SVNItem> createNode(final SVNItem f) {
		TreeItem<SVNItem> treeItem = new TreeItem<SVNItem>(f) {
			private boolean isLeaf;
			private boolean isFirstTimeChildren = true;
			private boolean isFirstTimeLeaf = true;

			@Override
			public ObservableList<TreeItem<SVNItem>> getChildren() {
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
					SVNItem f = getValue();

					if (f.isDir() || f instanceof SVNDirItem)
						isLeaf = false;
					else
						isLeaf = true;

				}
				return isLeaf;
			}

			private ObservableList<TreeItem<SVNItem>> buildChildren(TreeItem<SVNItem> treeItem) {

				SVNItem f = treeItem.getValue();
				if (f == null) {
					return FXCollections.emptyObservableList();
				}

				if (f.isDir() && f instanceof SVNDirItem)
					treeItem.setGraphic(getFolderImage());
				else {
					treeItem.setGraphic(getFileImage());
				}

				if (!f.isDir()) {
					return FXCollections.emptyObservableList();
				} else {
					List<SVNItem> childrens = f.getChildrens();
					ObservableList<TreeItem<SVNItem>> children = FXCollections.observableArrayList();
					for (SVNItem child : childrens) {
						TreeItem<SVNItem> createNode = createNode(child);
						children.add(createNode);
					}
					return children;
				}
			}

		};

		if (f.isDir() && f instanceof SVNDirItem)
			treeItem.setGraphic(getFolderImage());
		else {
			treeItem.setGraphic(getFileImage());
		}
		return treeItem;
	}

	/**
	 * 파일로부터 이미지를 그리기 위한 뷰를 반환한다.
	 *
	 * @Date 2015. 10. 14.
	 * @param file
	 * @return
	 * @User KYJ
	 */
	private static ImageView getFolderImage() {

		ImageView iv = new ImageView();
		try {
			Image fxImage = new Image(ClassLoader.getSystemClassLoader().getResource("META-INF/images/nodeicons/foler.png").openStream(),
					15d, 15d, false, false);
			iv.setImage(fxImage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return iv;
	}

	private static ImageView getFileImage() {
		ImageView iv = new ImageView();
		try {
			Image fxImage = new Image(ClassLoader.getSystemClassLoader().getResource("META-INF/images/nodeicons/file.png").openStream(),
					15d, 15d, false, false);
			iv.setImage(fxImage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return iv;

	}

}