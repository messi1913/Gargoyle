/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2016. 1. 20.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeView;

/**
 * Node에 대한 UI 구성구조를 조회후 트리로 보여줌.
 * 
 * @author KYJ
 *
 */
@DefaultProperty("item")
public class FxControlTreeView extends TreeView<Node> {

	private ObjectProperty<Node> item;

	/**
	 * @param item
	 *            분석하고자하는 UI노드
	 */
	public FxControlTreeView(Node item) {

		this.item = new SimpleObjectProperty<>();
		this.item.set(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.scene.control.Control#createDefaultSkin()
	 */
	@Override
	protected Skin<?> createDefaultSkin() {
		return new FxControlTreeViewSkin(this);
	}

	public final ObjectProperty<Node> itemProperty() {
		return this.item;
	}

	public final javafx.scene.Node getItem() {
		return this.itemProperty().get();
	}

	public final void setItem(final javafx.scene.Node item) {
		this.itemProperty().set(item);
	}

}
