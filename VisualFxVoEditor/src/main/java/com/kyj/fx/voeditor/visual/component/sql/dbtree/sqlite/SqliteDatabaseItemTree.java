/**
 * package : com.kyj.fx.voeditor.visual.component.sql.nodes
 *	fileName : DbsItemTree.java
 *	date      : 2015. 11. 8.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.dbtree.sqlite;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.DatabaseItemTree;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 * 데이터베이스 세션정보 출력
 *
 * @author KYJ
 *
 */
public class SqliteDatabaseItemTree extends DatabaseItemTree<String> {

	public SqliteDatabaseItemTree(String name, Supplier<Connection> conSupplier) throws Exception {
		super(name, conSupplier);
	}

	/**
	 * 커넥션으로부터 데이터베이스 정보 출력
	 */
	@Override
	public ObservableList<TreeItem<DatabaseItemTree<String>>> applyChildren(Connection con, String... args) throws Exception {
		ObservableList<TreeItem<DatabaseItemTree<String>>> observableArrayList = FXCollections.observableArrayList();
		SqliteSchemaItemTree mysqlSchemaItemTree = new SqliteSchemaItemTree(this, "-");
		TreeItem<DatabaseItemTree<String>> treeItem = new TreeItem<>(mysqlSchemaItemTree);
		observableArrayList.add(treeItem);

		return observableArrayList;
	}

	/**
	 * 기본값처리
	 * 
	 * @inheritDoc
	 */
	@Override
	public String getChildrenSQL(String... conditions) {
		return "";
	}

	/**
	 * 기본값처리
	 * 
	 * @inheritDoc
	 */
	@Override
	public ObservableList<TreeItem<DatabaseItemTree<String>>> applyChildren(List<Map<String, Object>> items) throws Exception {
		return FXCollections.observableArrayList();
	}

}
