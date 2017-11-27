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

import com.kyj.fx.voeditor.visual.component.sql.dbtree.DatabaseTreeNode;
import com.kyj.fx.voeditor.visual.util.DbUtil;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @author KYJ
 *
 */
public abstract class ColumnItemTree<T> extends TableItemTree<T> {

	private TableItemTree<T> parent;
	public Supplier<Connection> conSupplier;
	private boolean isPrimaryKey;

	public ColumnItemTree(TableItemTree<T> parent, String name) throws Exception {
		this.parent = parent;
		this.conSupplier = parent.conSupplier;
		setName(name);

	}

	@Override
	public void read() throws Exception {
		String tableName = parent.getName();
		String schemaName = parent.getParent().getName();
		String childrenSQL = getChildrenSQL(schemaName, tableName);
		Connection connection = getConnection();
		try {
			if (childrenSQL != null && !childrenSQL.isEmpty()) {
				List<Map<String, Object>> select = DbUtil.select(connection, childrenSQL);
				childrens = applyChildren(select);

				if (childrens == null)
					childrens = FXCollections.observableArrayList();
			}
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

	public void setParent(TableItemTree<T> parent) {
		this.parent = parent;
	}

	/**
	 * @return the isPrimaryKey
	 */
	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}

	/**
	 * @param isPrimaryKey
	 *            the isPrimaryKey to set
	 */
	public void setPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}

	@Override
	public Node createGraphics() {
		Image fxImage = new Image(getClass().getResourceAsStream("../column.png"), 15d, 15d, false, false);
		return new ImageView(fxImage);
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
