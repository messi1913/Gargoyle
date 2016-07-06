/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2015. 10. 22.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.popup;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author KYJ
 *
 */
public class KeyValue {
	private StringProperty key;
	private ObjectProperty<Object> value;

	public KeyValue(String key, Object value) {
		this();
		this.setKey(key);
		this.setValue(value);
	}

	public KeyValue() {
		this.key = new SimpleStringProperty();
		this.value = new SimpleObjectProperty<Object>();
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

	public void setValue(Object value) {
		this.value.set(value);
	}

	public Object getValue() {
		return value.get();
	}

	public ObjectProperty<Object> valueProperty() {
		return value;
	}

}
