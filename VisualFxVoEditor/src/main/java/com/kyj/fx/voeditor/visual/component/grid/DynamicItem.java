/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.grid
 *	작성일   : 2016. 2. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.grid;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * @author KYJ
 *
 */
public abstract class DynamicItem<T extends Node> {

	private ObjectProperty<T> item = new SimpleObjectProperty<>(null, "item");

	public DynamicItem(T item) {
		this.item.set(item);
	}

	public final ObjectProperty<T> itemProperty() {
		return this.item;
	}

	public final T getItem() {
		return this.itemProperty().get();
	}

	public final void setItem(final T item) {
		this.itemProperty().set(item);
	}

	public abstract void setOnMouseClicked(MouseEvent event);

}
