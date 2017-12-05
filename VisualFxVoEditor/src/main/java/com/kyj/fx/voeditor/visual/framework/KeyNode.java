/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2015. 10. 22.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;

/**
 * @author KYJ
 *
 */
public class KeyNode {
	private StringProperty key;
	private ObjectProperty<Node> value;

	public KeyNode(String key, Node value) {
		this();
		this.setKey(key);
		this.value.set(value);
	}

	public KeyNode() {
		this.key = new SimpleStringProperty();
		this.value = new SimpleObjectProperty<Node>();
	}

	public void setKey(String key) {
		this.key.set(key);
	}

	public String getKey() {
		return key.get();
	}

	public StringProperty keyProperty() {
		return key;
	}

	@Override
	public String toString() {
		return "[" + key.get() + "] , [" + value.get() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key.get() == null) ? 0 : key.get().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyNode other = (KeyNode) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.get().equals(other.key.get()))
			return false;
		return true;
	}

	public final ObjectProperty<Node> valueProperty() {
		return this.value;
	}

	public final Node getValue() {
		return this.valueProperty().get();
	}

	public final void setValue(final Node value) {
		this.valueProperty().set(value);
	}

}
