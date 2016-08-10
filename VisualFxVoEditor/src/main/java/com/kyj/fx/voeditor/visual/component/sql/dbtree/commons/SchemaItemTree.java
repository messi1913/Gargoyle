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

import com.kyj.fx.voeditor.visual.exceptions.GargoyleConnectionFailException;
import com.kyj.fx.voeditor.visual.util.DbUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 * @author KYJ
 *
 */
public abstract class SchemaItemTree<T> extends DatabaseItemTree<T> {

	private DatabaseItemTree<T> parent;

	public Supplier<Connection> conSupplier;

	public SchemaItemTree() throws Exception {
		super();
	}

	public SchemaItemTree(DatabaseItemTree<T> parent, String name) throws Exception {
		this.parent = parent;
		this.conSupplier = parent.conSupplier;
		setName(name);

	}

	public DatabaseItemTree<T> getParent() {
		return parent;
	}

	public void setParent(DatabaseItemTree<T> parent) {
		this.parent = parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.DatabaseItemTree
	 * #read()
	 */
	@Override
	public void read() throws Exception {
		String childrenSQL = getChildrenSQL(getName());

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
		// connection.close();
		// }

		Connection connection = getConnection();
		if (connection == null)
			throw new GargoyleConnectionFailException("connect fail...");

		try {
			if (childrenSQL != null && !childrenSQL.isEmpty()) {
				List<Map<String, Object>> select = DbUtil.select(connection, childrenSQL);
				childrens.addAll(applyChildren(select));
			}

//			if (childrens == null)
//				childrens = FXCollections.observableArrayList();

			// SQL로 불가능한 처리는 Connection을 받아 처리하도록한다.
			ObservableList<TreeItem<DatabaseItemTree<T>>> second = applyChildren(connection, getName());
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
