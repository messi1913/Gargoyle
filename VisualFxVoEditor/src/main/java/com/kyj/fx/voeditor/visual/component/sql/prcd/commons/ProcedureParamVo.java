/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.prcd
 *	작성일   : 2017. 11. 29.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.prcd.commons;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 프로시저 파라미터 관리
 * 
 * @author KYJ
 *
 */
public class ProcedureParamVo {

	// index = new SimpleObjectProperty<Integer>();
	private ObjectProperty<Integer> index;
	private StringProperty name;
	private StringProperty type;
	private ObjectProperty<Object> value;
	private BooleanProperty nullable;

	public ProcedureParamVo() {
		index = new SimpleObjectProperty<Integer>();
		name = new SimpleStringProperty();
		type = new SimpleStringProperty();
		value = new SimpleObjectProperty<Object>();
		nullable = new SimpleBooleanProperty();
	}

	public final StringProperty nameProperty() {
		return this.name;
	}

	public final String getName() {
		return this.nameProperty().get();
	}

	public final void setName(final String name) {
		this.nameProperty().set(name);
	}

	public final StringProperty typeProperty() {
		return this.type;
	}

	public final String getType() {
		return this.typeProperty().get();
	}

	public final void setType(final String type) {
		this.typeProperty().set(type);
	}

	public final ObjectProperty<Object> valueProperty() {
		return this.value;
	}

	public final Object getValue() {
		return this.valueProperty().get();
	}

	public final void setValue(final Object value) {
		this.valueProperty().setValue(value);
	}

	public final BooleanProperty nullableProperty() {
		return this.nullable;
	}

	public final boolean isNullable() {
		return this.nullableProperty().get();
	}

	public final void setNullable(final boolean nullable) {
		this.nullableProperty().set(nullable);
	}

	public final ObjectProperty<Integer> indexProperty() {
		return this.index;
	}
	

	public final Integer getIndex() {
		return this.indexProperty().get();
	}
	
	public final void setIndex(final Integer index) {
		this.indexProperty().set(index);
	}
	

	

}
