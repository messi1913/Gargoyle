/**
 * package : com.kyj.fx.voeditor.visual.component.sql.nodes
 *	fileName : TableItemTree.java
 *	date      : 2015. 11. 8.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.dbtree.commons;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import com.kyj.fx.voeditor.visual.exceptions.ConnectionFailException;
import com.kyj.fx.voeditor.visual.util.DbUtil;

/**
 * @author KYJ
 *
 */
public abstract class TableItemTree<T> extends SchemaItemTree<T> {

	private SchemaItemTree<T> parent;

	public Supplier<Connection> conSupplier;

	public TableItemTree() throws Exception {
		super();
	}

	public TableItemTree(SchemaItemTree<T> parent, String name) throws Exception {
		this.parent = parent;
		this.conSupplier = parent.conSupplier;
		setName(name);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.SchemaItemTree
	 * #read()
	 */
	@Override
	public void read() throws Exception {
		if (parent == null)
			return;
		String childrenSQL = getChildrenSQL(parent.getName(), getName());

		// Connection connection = getConnection();
		// try {
		// if (childrenSQL != null && !childrenSQL.isEmpty()) {
		// List<Map<String, Object>> select = DbUtil.select(connection,
		// childrenSQL);
		// childrens = applyChildren(select);
		//
		// if (childrens == null)
		// childrens = FXCollections.observableArrayList();
		// }
		// } finally {
		// if (connection != null)
		// connection.close();
		// }

		Connection connection = getConnection();
		if (connection == null)
			throw new ConnectionFailException("connect fail...");

		try {
			if (childrenSQL != null && !childrenSQL.isEmpty()) {
				List<Map<String, Object>> select = DbUtil.select(connection, childrenSQL);
				childrens.addAll(applyChildren(select));
			}

//			if (childrens == null)
//				childrens = FXCollections.observableArrayList();

			// SQL로 불가능한 처리는 Connection을 받아 처리하도록한다.
			ObservableList<TreeItem<DatabaseItemTree<T>>> second = applyChildren(connection, parent.getName(), getName());
			if (second != null)
				childrens.addAll(second);

		} finally {
			if (connection != null)
				connection.close();
		}

	}

	public Connection getConnection() {
		return this.conSupplier.get();
	}

	public SchemaItemTree<T> getParent() {
		return parent;
	}

	public void setParent(SchemaItemTree<T> parent) {
		this.parent = parent;
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
