/**
 * package : com.kyj.fx.voeditor.visual.component.sql.nodes
 *	fileName : DbsItemTree.java
 *	date      : 2015. 11. 8.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.dbtree.commons;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.kyj.fx.voeditor.visual.component.sql.functions.ConnectionSupplier;
import com.kyj.fx.voeditor.visual.exceptions.GargoyleConnectionFailException;
import com.kyj.fx.voeditor.visual.util.DbUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;

/**
 * @author KYJ
 *
 */
public abstract class DatabaseItemTree<T> implements IConnectionByChildrens<T> {

	private String name;
	public ConnectionSupplier conSupplier;
	public ObservableList<TreeItem<DatabaseItemTree<T>>> childrens = FXCollections.observableArrayList();

	public DatabaseItemTree() throws Exception {
	}

	public DatabaseItemTree(String name, ConnectionSupplier conSupplier) throws Exception {
		this.name = name;
		this.conSupplier = conSupplier;
	}

	public void read() throws Exception {

		String childrenSQL = getChildrenSQL("");
		Connection connection = getConnection();
		if (connection == null)
			throw new GargoyleConnectionFailException("connect fail...");

		try {
			if (childrenSQL != null && !childrenSQL.isEmpty()) {
				List<Map<String, Object>> select = DbUtil.select(connection, childrenSQL);
				childrens.addAll(applyChildren(select));
			}

			// if (childrens == null)
			// childrens = FXCollections.observableArrayList();

			// SQL로 불가능한 처리는 Connection을 받아 처리하도록한다.
			ObservableList<TreeItem<DatabaseItemTree<T>>> second = applyChildren(connection);
			if (second != null)
				childrens.addAll(second);

		} finally {
			if (connection != null)
				connection.close();
		}

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Connection getConnection() {
		return this.conSupplier.get();
	}

	public ObservableList<TreeItem<DatabaseItemTree<T>>> getChildrens() {
		return childrens;
	}

	public abstract String getChildrenSQL(String... conditions);

	/********************************
	 * 작성일 : 2016. 7. 12. 작성자 : KYJ
	 *
	 * check is Valide Schema Name
	 *
	 * if is not, The Method(' show100RowAction() ') in CommonsSqllPan.java
	 * 
	 * not apply schema Name SQL
	 * 
	 * @param schemaName
	 * @return
	 ********************************/
	public boolean isApplySchemaName(String schemaName) {
		return true;
	}

	// public abstract TreeItem<DatabaseItemTree<T>> applyChildren(Map<String,
	// Object> item)
	// throws Exception;

	public abstract ObservableList<TreeItem<DatabaseItemTree<T>>> applyChildren(List<Map<String, Object>> items) throws Exception;

	/**
	 * 트리 이미지
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 17.
	 * @return
	 */
	public Node createGraphics() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}
}
