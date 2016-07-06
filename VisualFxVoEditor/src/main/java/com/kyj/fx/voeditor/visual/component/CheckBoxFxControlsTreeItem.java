/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2016. 1. 20.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Control;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Shape;
import javafx.util.StringConverter;

import org.controlsfx.control.CheckTreeView;

/**
 * 자바FX컨트롤요소의 구조를 트리형태로 보여주기 위한 기반
 *
 * @author KYJ
 *
 */
public class CheckBoxFxControlsTreeItem {
	private CheckTreeView<Node> tv;

	public CheckBoxFxControlsTreeItem(CheckTreeView<Node> tv) {
		this.tv = tv;

		tv.setCellFactory(param -> {
			CheckBoxTreeCell<Node> treeCell = new CheckBoxTreeCell<Node>();
			treeCell.setConverter(new StringConverter<TreeItem<Node>>() {

				@Override
				public String toString(TreeItem<Node> n) {
					return getName(n.getValue());
				}

				@Override
				public TreeItem<Node> fromString(String string) {
					// TODO Auto-generated method stub
					return null;
				}
			});
			return treeCell;
		});

	}

	/**
	 * 파일 트리를 생성하기 위한 노드를 반환한다.
	 *
	 * @Date 2015. 10. 14.
	 * @param f
	 * @return
	 * @User KYJ
	 */
	public TreeItem<Node> createNode(final Node f) {

		TreeItem<Node> treeItem = new CheckBoxTreeItem<Node>(f) {
			private boolean isLeaf;
			private boolean isFirstTimeChildren = true;
			private boolean isFirstTimeLeaf = true;

			@Override
			public ObservableList<TreeItem<Node>> getChildren() {
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

					Node f = getValue();
					if (f == null) {
						isLeaf = true;
					} else if (f instanceof Control) {
						isLeaf = ((Control) f).getChildrenUnmodifiable().isEmpty();
					} else if (f instanceof Parent) {
						isLeaf = ((Parent) f).getChildrenUnmodifiable().isEmpty();
					} else if (f instanceof Shape)
						isLeaf = true;
					else
						isLeaf = false;
				}
				return isLeaf;
			}

			private ObservableList<TreeItem<Node>> buildChildren(TreeItem<Node> treeItem) {

				Node f = treeItem.getValue();
				if (f == null) {
					return FXCollections.emptyObservableList();
				}
				treeItem.setGraphic(getImage(getName(f)));
				List<Node> childrens = getChildrens(f);
				if (childrens == null || childrens.isEmpty()) {
					return FXCollections.emptyObservableList();
				}

				else {
					ObservableList<TreeItem<Node>> children = FXCollections.observableArrayList();
					for (Node child : childrens) {
						children.add(createNode(child));
					}
					return children;
				}
			}

		};

		treeItem.setGraphic(getImage(getName(f)));

		return treeItem;
	}

	/**
	 * resources패키지로부터 image를 가져오는 처리
	 * 
	 * @param name
	 * @return
	 */
	ImageView getImage(String name) {
		try {
			String name2 = "images/nodeicons/" + name + ".png";
			URL resource = this.getClass().getClassLoader().getResource(name2);
			if (resource != null) {
				File file = new File(resource.toURI());
				if (file.exists())
					return new ImageView(new Image(resource.openStream()));
			}
		} catch (Exception e) {
			// not important...
		}

		return new ImageView();
	}

	/**
	 * 클래스 패키지 +이름으로부터 UI에 표시할 텍스트를 가져옴.
	 * 
	 * @param n
	 * @return
	 */
	String getName(Node n) {
		String name = n.getClass().getName();
		int startIndex = name.lastIndexOf('.');
		if (startIndex <= -1)
			return name;
		if (startIndex != 0)
			startIndex++;
		int endIndex = name.lastIndexOf('$');
		if (endIndex <= -1)
			endIndex = name.length();
		return name.substring(startIndex, endIndex);
	}

	Object getMethod(Object o, String methodName) {
		try {
			Method declaredMethod = o.getClass().getMethod(methodName);
			declaredMethod.setAccessible(true);
			return declaredMethod.invoke(o);
		} catch (Exception e) {

		}
		return null;
	}

	Object getDeclaredMethod(Object o, String methodName) throws Exception {
		try {
			Method declaredMethod = o.getClass().getDeclaredMethod(methodName);
			declaredMethod.setAccessible(true);
			return declaredMethod.invoke(o);
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * node로부터 구성된 하위 노드들을 반환받음.
	 * 
	 * @param node
	 * @return
	 */
	public List<Node> getChildrens(Node node) {
		List<Node> controls = new ArrayList<>();
		if (node instanceof Control) {
			Control c = (Control) node;
			controls.add(c);
			controls = c.getChildrenUnmodifiable();
		} else if (node instanceof Parent) {
			Parent p = (Parent) node;
			controls = p.getChildrenUnmodifiable();
		} else {
			controls.add(node);
		}
		return controls;
	}

}
