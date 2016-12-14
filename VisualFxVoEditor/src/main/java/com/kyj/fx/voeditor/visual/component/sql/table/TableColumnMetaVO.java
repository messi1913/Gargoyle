/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.table
 *	작성일   : 2016. 1. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.table;

import java.util.List;

import com.kyj.fx.voeditor.visual.component.grid.AbstractDVO;
import com.kyj.fx.voeditor.visual.component.sql.table.IKeyType.KEY_TYPE;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author KYJ
 *
 */
public class TableColumnMetaVO extends AbstractDVO{

	private StringProperty columnName;

	private StringProperty sortOrder;

	private StringProperty isNullable;

	private ObjectProperty<KEY_TYPE> keyType;

	private BooleanProperty isPrimaryKey;

	private StringProperty dataLength;

	private StringProperty dataType;

	private StringProperty defaultValue;

	private StringProperty remark;

	private ObjectProperty<List<ReferenceKey>> refs = new SimpleObjectProperty<>();

	public TableColumnMetaVO() {
		columnName = new SimpleStringProperty();
		sortOrder = new SimpleStringProperty();
		isNullable = new SimpleStringProperty();
		keyType = new SimpleObjectProperty<>(KEY_TYPE.NOMAL);
		isPrimaryKey = new SimpleBooleanProperty();
		dataLength = new SimpleStringProperty();
		dataType = new SimpleStringProperty();
		defaultValue = new SimpleStringProperty();
		remark = new SimpleStringProperty();
		refs = new SimpleObjectProperty<>();
	}

	public final StringProperty columnNameProperty() {
		return this.columnName;
	}

	public final java.lang.String getColumnName() {
		return this.columnNameProperty().get();
	}

	public final void setColumnName(final java.lang.String columnName) {
		this.columnNameProperty().set(columnName);
	}

	public final StringProperty sortOrderProperty() {
		return this.sortOrder;
	}

	public final java.lang.String getSortOrder() {
		return this.sortOrderProperty().get();
	}

	public final void setSortOrder(final java.lang.String sortOrder) {
		this.sortOrderProperty().set(sortOrder);
	}

	public final StringProperty isNullableProperty() {
		return this.isNullable;
	}

	public final java.lang.String getIsNullable() {
		return this.isNullableProperty().get();
	}

	public final void setIsNullable(final java.lang.String isNullable) {
		this.isNullableProperty().set(isNullable);
	}

	public final BooleanProperty isPrimaryKeyProperty() {
		return this.isPrimaryKey;
	}

	public final boolean isIsPrimaryKey() {
		return this.isPrimaryKeyProperty().get();
	}

	public final void setIsPrimaryKey(final boolean isPrimaryKey) {
		this.isPrimaryKeyProperty().set(isPrimaryKey);
	}

	public final ObjectProperty<KEY_TYPE> keyTypeProperty() {
		return this.keyType;
	}

	public final com.kyj.fx.voeditor.visual.component.sql.table.IKeyType.KEY_TYPE getKeyType() {
		return this.keyTypeProperty().get();
	}

	public final void setKeyType(final com.kyj.fx.voeditor.visual.component.sql.table.IKeyType.KEY_TYPE keyType) {
		this.keyTypeProperty().set(keyType);
	}

	public final StringProperty dataLengthProperty() {
		return this.dataLength;
	}

	public final java.lang.String getDataLength() {
		return this.dataLengthProperty().get();
	}

	public final void setDataLength(final java.lang.String dataLength) {
		this.dataLengthProperty().set(dataLength);
	}

	public final StringProperty dataTypeProperty() {
		return this.dataType;
	}

	public final java.lang.String getDataType() {
		return this.dataTypeProperty().get();
	}

	public final void setDataType(final java.lang.String dataType) {
		this.dataTypeProperty().set(dataType);
	}

	public final StringProperty defaultValueProperty() {
		return this.defaultValue;
	}

	public final String getDefaultValue() {
		return this.defaultValueProperty().get();
	}

	public final void setDefaultValue(final String defaultValue) {
		this.defaultValueProperty().set(defaultValue);
	}

	public final StringProperty remarkProperty() {
		return this.remark;
	}

	public final String getRemark() {
		return this.remarkProperty().get();
	}

	public final void setRemark(final String remark) {
		this.remarkProperty().set(remark);
	}

	public final ObjectProperty<List<ReferenceKey>> refsProperty() {
		return this.refs;
	}

	public final java.util.List<com.kyj.fx.voeditor.visual.component.sql.table.ReferenceKey> getRefs() {
		return this.refsProperty().get();
	}

	public final void setRefs(final java.util.List<com.kyj.fx.voeditor.visual.component.sql.table.ReferenceKey> refs) {
		this.refsProperty().set(refs);
	}

}
