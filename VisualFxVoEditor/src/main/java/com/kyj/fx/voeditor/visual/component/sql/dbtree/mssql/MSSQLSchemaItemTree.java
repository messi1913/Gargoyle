/**
 * package : com.kyj.fx.voeditor.visual.component.sql.nodes
 *	fileName : TableItemTree.java
 *	date      : 2015. 11. 8.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.dbtree.mssql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.DatabaseItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.SchemaItemTree;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 * 데이터베이스 스키마 정보 출력
 *
 * @author KYJ
 *
 */
public class MSSQLSchemaItemTree extends SchemaItemTree<String> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MSSQLSchemaItemTree.class);

	public MSSQLSchemaItemTree(DatabaseItemTree<String> parent, String name) throws Exception {
		super(parent, name);
	}

	@Override
	public String getChildrenSQL(String... conditions) {
		// return "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.`TABLES` WHERE
		// TABLE_SCHEMA
		// = '" + conditions[0] + "' ORDER BY TABLE_NAME";

		return "";
		// return "SELECT TABLE_SCHEMA + '.' + TABLE_NAME AS TABLE_NAME FROM
		// INFORMATION_SCHEMA.TABLES WHERE TABLE_CATALOG = '"+conditions[0]+"'";
	}

	/**
	 * 커넥션으로부터 스키마 정보 출력
	 */
	@Override
	public ObservableList<TreeItem<DatabaseItemTree<String>>> applyChildren(Connection con, String... args) throws Exception {
		DatabaseMetaData metaData = con.getMetaData();
		ResultSet tables = metaData.getTables(args[0], null, "%", null);

		ObservableList<TreeItem<DatabaseItemTree<String>>> observableArrayList = FXCollections.observableArrayList();
		while (tables.next()) {

			String tableType = tables.getString(4);

			if ("TABLE".equals(tableType)) {
				LOGGER.debug("TABLE_CAT: {} TABLE_SCHEM:  {}  TABLE_NAME : {} TABLE_TYPE : {} ", tables.getString(1), tables.getString(2),
						tables.getString(3), tableType);
				MSSQLTableItemTree mssqlTableItemTree = new MSSQLTableItemTree(this, tables.getString(2) + "." + tables.getString(3));
				TreeItem<DatabaseItemTree<String>> treeItem = new TreeItem<>(mssqlTableItemTree);
				observableArrayList.add(treeItem);
			}

		}
		return observableArrayList;
	}

	@Override
	public ObservableList<TreeItem<DatabaseItemTree<String>>> applyChildren(List<Map<String, Object>> items) throws Exception {
		return FXCollections.observableArrayList();
	}

	@Override
	protected DatabaseItemTree<String> createProcedureItemTree(String cat, String schem, String _name, String type, String remark)
			throws Exception {
		String name = _name;
		if (_name.endsWith(";1")) {
			name = _name.substring(0, _name.length() - 2);
		}

		if (procedureFilter(cat, schem, name, type)) {
			
			DatabaseItemTree<String> createProcedureItemTree = new MSSQLProcedureItemTree(this, cat, schem, name, remark);
			createProcedureItemTree.setName(toProcedureName(cat, schem, name, type));

			return createProcedureItemTree;
		}
		return null;
	}

	protected boolean procedureFilter(String cat, String schem, String name, String type) {
		return !"sys".equals(schem);
	}

	protected String toProcedureName(String cat, String schem, String name, String type) {
		return String.format("%s.%s", schem, name);
	}
	
	

}
