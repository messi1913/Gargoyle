/**
 * package : com.kyj.fx.voeditor.visual.component.sql.nodes
 *	fileName : TableItemTree.java
 *	date      : 2015. 11. 8.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.dbtree.sqlite;

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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 * 
 * 테이블 컬럼에 대한 메타데이터 처리.
 * 
 * @author KYJ
 *
 */
public class SqliteTableItemTree extends TableItemTree<String> {

	/* 
	 * references 
	 * http://docs.oracle.com/javase/6/docs/api/java/sql/ DatabaseMetaData.html#getTables%28java.lang.String,%20java.lang. String,%20java.lang.String,%20java.lang.String%5b%5d%29 
	 */

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

	public SqliteTableItemTree(SchemaItemTree<String> parent, String name) throws Exception {
		super(parent, name);
	}

	/**
	 * 커넥션으로부터 스키마 정보 출력
	 */
	@Override
	public ObservableList<TreeItem<DatabaseItemTree<String>>> applyChildren(Connection con, String... args) throws Exception {

		DatabaseMetaData metaData = con.getMetaData();
		ResultSet tables = metaData.getColumns(null, null, args[1], "%");

		Set<String> primaryKeySet = toSet(metaData.getPrimaryKeys(null, null, args[1]), COLUMN_NAME);

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
	public String getChildrenSQL(String... conditions) {
		return "";
	}

	@Override
	public ObservableList<TreeItem<DatabaseItemTree<String>>> applyChildren(List<Map<String, Object>> items) throws Exception {
		return FXCollections.observableArrayList();
	}

	/**
	 * SQLite에서는 스키마명을 적용하여 조회하지않는다.
	 * 
	 * @inheritDoc
	 */
	@Override
	public boolean isApplySchemaName(String schemaName) {
		return false;
	}
}
