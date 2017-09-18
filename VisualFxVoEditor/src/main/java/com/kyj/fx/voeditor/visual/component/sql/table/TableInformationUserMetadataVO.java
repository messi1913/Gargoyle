/**
 * package : com.kyj.fx.voeditor.visual.component.table
 *	fileName : TableInformationMetadataVO.java
 *	date      : 2016. 1. 1.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.table;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author KYJ
 *
 */
public class TableInformationUserMetadataVO {

	private StringProperty catalog;
	/**
	 * dbms
	 */
	private StringProperty databaseName;
	/**
	 * table Name
	 */
	private StringProperty tableName;

	public TableInformationUserMetadataVO() {
		catalog = new SimpleStringProperty();
		tableName = new SimpleStringProperty();
		databaseName = new SimpleStringProperty();
	}

	public final StringProperty tableNameProperty() {
		return this.tableName;
	}

	public final java.lang.String getTableName() {
		return this.tableNameProperty().get();
	}

	public final void setTableName(final java.lang.String tableName) {
		this.tableNameProperty().set(tableName);
	}

	public final StringProperty databaseNameProperty() {
		return this.databaseName;
	}

	public final java.lang.String getDatabaseName() {
		return this.databaseNameProperty().get();
	}

	public final void setDatabaseName(final java.lang.String databaseName) {
		this.databaseNameProperty().set(databaseName);
	}

	public final StringProperty catalogProperty() {
		return this.catalog;
	}

	public final String getCatalog() {
		return this.catalogProperty().get();
	}

	public final void setCatalog(final String catalog) {
		this.catalogProperty().set(catalog);
	}

}
