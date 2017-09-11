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
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.DatabaseItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.SchemaItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.TableItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.sqlite.SqliteColumnItemTree;

/**
 * @author KYJ
 *
 */
public class MSSQLTableItemTree extends TableItemTree<String> {

	private static final int COLUMN_NAME = 4;

	public MSSQLTableItemTree(SchemaItemTree<String> parent, String name) throws Exception {
		super(parent, name);
	}

	@Override
	public String getChildrenSQL(String... conditions) {
		return "";
	}

	@Override
	public ObservableList<TreeItem<DatabaseItemTree<String>>> applyChildren(List<Map<String, Object>> items) throws Exception {
		return FXCollections.observableArrayList();
	}

	/**
	 * 커넥션으로부터 스키마 정보 출력
	 */
	@Override
	public ObservableList<TreeItem<DatabaseItemTree<String>>> applyChildren(Connection con, String... args) throws Exception {

		DatabaseMetaData metaData = con.getMetaData();
		ResultSet tables = metaData.getColumns(args[0], null, args[1], "%");

		Set<String> primaryKeySet = toSet(metaData.getPrimaryKeys(args[0], null, args[1]), COLUMN_NAME);

		ObservableList<TreeItem<DatabaseItemTree<String>>> observableArrayList = FXCollections.observableArrayList();
		while (tables.next()) {

			/*
			 * references http://docs.oracle.com/javase/6/docs/api/java/sql/
			 * DatabaseMetaData.html#getTables%28java.lang.String,%20java.lang.
			 * String,%20java.lang.String,%20java.lang.String%5b%5d%29
			 */
			String columnName = tables.getString(COLUMN_NAME);
			SqliteColumnItemTree coumnItem = new SqliteColumnItemTree(this, columnName);
			coumnItem.setPrimaryKey(primaryKeySet.contains(columnName));
			TreeItem<DatabaseItemTree<String>> treeItem = new TreeItem<>(coumnItem);
			observableArrayList.add(treeItem);
		}
		return observableArrayList;
	}

	/********************************
	 * 작성일 : 2016. 6. 27. 작성자 : KYJ
	 *
	 * ResultSet으로부터 특정 컬럼인덱스만 조회처리.
	 * 
	 * @param rs
	 * @param columnIdx
	 * @return
	 * @throws SQLException
	 ********************************/
	public Set<String> toSet(ResultSet rs, int columnIdx) throws SQLException {
		Set<String> hashSet = new HashSet<String>();
		while (rs.next()) {
			hashSet.add(rs.getString(columnIdx));
		}
		return hashSet;
	}

	// @Override
	// public String getChildrenSQL(String... conditions) {
	//
	// // return "";
	// StringBuffer sb = new StringBuffer();
	// sb.append("SELECT COLUMN_NAME, \n");
	// sb.append("CASE WHEN COLUMN_KEY = 'PRI' THEN 'Y' ELSE 'N' END PRIMARY_YN
	// \n");
	// sb.append("FROM INFORMATION_SCHEMA.`COLUMNS`\n");
	// sb.append("WHERE 1=1\n");
	// sb.append("AND TABLE_SCHEMA = '" + conditions[0] + "'\n");
	// sb.append("AND TABLE_NAME = '" + conditions[1] + "'\n");
	// return sb.toString();
	// }
	//
	// @Override
	// public ObservableList<TreeItem<DatabaseItemTree<String>>>
	// applyChildren(List<Map<String, Object>> items) throws Exception {
	//
	// ObservableList<TreeItem<DatabaseItemTree<String>>> observableArrayList =
	// FXCollections.observableArrayList();
	// for (Map<String, Object> map : items) {
	// Object columnName = map.get("COLUMN_NAME");
	// Object isPrimarykey = map.get("PRIMARY_YN");
	// if (columnName == null)
	// continue;
	//
	// MSSQLColumnItemTree mysqlSchemaItemTree = new MSSQLColumnItemTree(this,
	// columnName.toString());
	// if (isPrimarykey != null)
	// mysqlSchemaItemTree.setPrimaryKey("Y".equals(isPrimarykey.toString()));
	//
	// TreeItem<DatabaseItemTree<String>> treeItem = new
	// TreeItem<>(mysqlSchemaItemTree);
	// observableArrayList.add(treeItem);
	// }
	//
	// return observableArrayList;
	// }

}
