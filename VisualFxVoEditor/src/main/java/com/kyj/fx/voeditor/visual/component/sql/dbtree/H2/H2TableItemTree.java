/**
 * package : com.kyj.fx.voeditor.visual.component.sql.nodes
 *	fileName : TableItemTree.java
 *	date      : 2015. 11. 8.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.dbtree.H2;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.DatabaseItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.SchemaItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.TableItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.sqlite.SqliteColumnItemTree;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 * @author KYJ
 *
 */
public class H2TableItemTree extends TableItemTree<String> {

	public H2TableItemTree(SchemaItemTree<String> parent, String name) throws Exception {
		super(parent, name);
	}

	@Override
	public String getChildrenSQL(String... conditions) {
//		String driver = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.DBMS_H2);
//		String velocitySQL = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.SQL_TABLE_COLUMNS_WRAPPER, driver);
//
//		HashMap<String, Object> hashMap = new HashMap<String,Object>();
//		hashMap.put("tableName", conditions[1] );
//		hashMap.put("databaseName",conditions[0] );
//		return ValueUtil.getVelocityToText(velocitySQL, hashMap, true);
		return "";
	}
	
	

//	@Override
//	public ObservableList<TreeItem<DatabaseItemTree<String>>> applyChildren(List<Map<String, Object>> items) throws Exception {
//
//		ObservableList<TreeItem<DatabaseItemTree<String>>> observableArrayList = FXCollections.observableArrayList();
//		for (Map<String, Object> map : items) {
//			Object columnName = map.get("COLUMN_NAME");
//			Object isPrimarykey = map.get("COLUMN_KEY");
//			if (columnName == null)
//				continue;
//
//			H2ColumnItemTree mysqlSchemaItemTree = new H2ColumnItemTree(this, columnName.toString());
//			if (isPrimarykey != null)
//				mysqlSchemaItemTree.setPrimaryKey("PRI".equals(isPrimarykey.toString()));
//
//			TreeItem<DatabaseItemTree<String>> treeItem = new TreeItem<>(mysqlSchemaItemTree);
//			observableArrayList.add(treeItem);
//		}
//
//		return observableArrayList;
//	}
	

	/*
	 * 
	 * # function getPrimaryKeys # 
	 * TABLE_CAT String => table catalog (may be null)
	 * 
	 * TABLE_SCHEM String => table schema (may be null)
	 * 
	 * TABLE_NAME String => table name
	 * 
	 * COLUMN_NAME String => column name
	 * 
	 * KEY_SEQ short => sequence number within primary key( a value of 1 represents the first column of the primary key, 
	 * a value of 2 would represent the second column within the primary key).
	 * 
	 * PK_NAME String => primary key name (may be null)
	 */
	//	private static final int GETPRIMARY_TABLE_CAT = 1;
	//	private static final int GETPRIMARY_TABLE_SCHEM = 2;
	//	private static final int GETPRIMARY_TABLE_NAME = 3;
	private static final int COLUMN_NAME = 4;
	//	private static final int GETPRIMARY_KEY_SEQ = 5;
	//	private static final int GETPRIMARY_PRIMARYKEY_NAME = 6;
	
	@Override
	public ObservableList<TreeItem<DatabaseItemTree<String>>> applyChildren(Connection con, String... args) throws Exception {

		DatabaseMetaData metaData = con.getMetaData();
		ResultSet tables = metaData.getColumns(null, args[0], args[1], "%");

		Set<String> primaryKeySet = toSet(metaData.getPrimaryKeys(null, args[0], args[1]), COLUMN_NAME);

		ObservableList<TreeItem<DatabaseItemTree<String>>> observableArrayList = FXCollections.observableArrayList();
		while (tables.next()) {

			/* 
			 * references 
			 * http://docs.oracle.com/javase/6/docs/api/java/sql/ DatabaseMetaData.html#getTables%28java.lang.String,%20java.lang. String,%20java.lang.String,%20java.lang.String%5b%5d%29 
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

	@Override
	public ObservableList<TreeItem<DatabaseItemTree<String>>> applyChildren(List<Map<String, Object>> items) throws Exception {
		return FXCollections.observableArrayList();
	}

}
