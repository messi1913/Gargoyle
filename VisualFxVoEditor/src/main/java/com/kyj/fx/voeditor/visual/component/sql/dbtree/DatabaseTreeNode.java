/**
 * package : com.kyj.fx.voeditor.visual.component.sql.nodes
 *	fileName : DatabaseTreeItem.java
 *	date      : 2015. 11. 8.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.dbtree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.ColumnItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.DatabaseItemTree;
import com.kyj.fx.voeditor.visual.exceptions.GargoyleConnectionFailException;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @author KYJ
 *
 */
public class DatabaseTreeNode {

	private static Logger LOGGER = LoggerFactory.getLogger(DatabaseTreeNode.class);

	/**
	 * 파일로부터 이미지를 그리기 위한 뷰를 반환한다.
	 *
	 * @Date 2015. 10. 14.
	 * @param file
	 * @return
	 * @User KYJ
	 * @deprecated 각 클래스별 createGraphics함수로 별도 구현할 수 있도록 수정.
	 */
	@Deprecated
	public static ImageView getDatabaseImage() {
		Image fxImage = new Image(DatabaseTreeNode.class.getResourceAsStream("database.png"), 15d, 15d, false, false);
		return new ImageView(fxImage);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 27.
	 * @return
	 * @deprecated 각 클래스별 createGraphics함수로 별도 구현할 수 있도록 수정.
	 */
	@Deprecated
	public static Node getTableImage() {
		Image fxImage = new Image(DatabaseTreeNode.class.getResourceAsStream("table.png"), 15d, 15d, false, false);
		return new ImageView(fxImage);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 27.
	 * @return
	 * @deprecated 각 클래스별 createGraphics함수로 별도 구현할 수 있도록 수정.
	 */
	@Deprecated
	public static Node getColumnImage() {
		Image fxImage = new Image(DatabaseTreeNode.class.getResourceAsStream("column.png"), 15d, 15d, false, false);
		return new ImageView(fxImage);
	}

	/**
	 * 파일 트리를 생성하기 위한 노드를 반환한다.
	 *
	 * @Date 2015. 10. 14.
	 * @param f
	 * @return
	 * @User KYJ
	 */
	public TreeItem<DatabaseItemTree<String>> createNode(final DatabaseItemTree<String> f) {
		TreeItem<DatabaseItemTree<String>> treeItem = new TreeItem<DatabaseItemTree<String>>(f) {
			private boolean isLeaf;
			private boolean isFirstTimeChildren = true;
			private boolean isFirstTimeLeaf = true;

			@Override
			public ObservableList<TreeItem<DatabaseItemTree<String>>> getChildren() {
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
					DatabaseItemTree<String> f = getValue();
					isLeaf = (f instanceof ColumnItemTree);
				}
				return isLeaf;
			}

			private ObservableList<TreeItem<DatabaseItemTree<String>>> buildChildren(TreeItem<DatabaseItemTree<String>> treeItem) {

				DatabaseItemTree<String> f = treeItem.getValue();

				if (f == null) {
					return FXCollections.emptyObservableList();
				} else {
					try {
						f.read();
					} catch (GargoyleConnectionFailException e) {
						LOGGER.error(ValueUtil.toString(e));
						DialogUtil.showExceptionDailog(e);
					} catch (Exception e) {
						LOGGER.error(ValueUtil.toString(e));
						return FXCollections.emptyObservableList();
					}
				}

				if (f instanceof ColumnItemTree) {
					return FXCollections.emptyObservableList();
				}

				ObservableList<TreeItem<DatabaseItemTree<String>>> files = f.getChildrens();
				if (files != null) {
					ObservableList<TreeItem<DatabaseItemTree<String>>> children = FXCollections.observableArrayList();
					for (TreeItem<DatabaseItemTree<String>> childFile : files) {
						DatabaseItemTree<String> value = childFile.getValue();
						TreeItem<DatabaseItemTree<String>> createNode = createNode(value);
						children.add(createNode);
					}
					return children;
				}
				return FXCollections.emptyObservableList();
			}

		};

		Node imageNode = getGraphics(f);
		treeItem.setGraphic(imageNode);

		return treeItem;
	}

	public static Node getGraphics(final DatabaseItemTree<String> f) {
		if (f != null) {
			return f.createGraphics();
			
			//각 클래스별 별도 구현. 더상의 캐스팅은 없다.
			
			// if (f instanceof ProcedureItemTree) {
			// ProcedureItemTree<String> tmp = (ProcedureItemTree<String>) f;
			// return tmp.createGraphics();
			// } else if (f instanceof ColumnItemTree) {
			// return getColumnImage();
			// } else if (f instanceof TableItemTree) {
			// return getTableImage();
			// } else if (f instanceof SchemaItemTree) {
			// return getDatabaseImage();
			// }
			
		}

		return null; // .new ImageView();
	}

}
