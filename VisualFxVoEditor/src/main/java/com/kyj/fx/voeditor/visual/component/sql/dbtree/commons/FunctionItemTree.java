
package com.kyj.fx.voeditor.visual.component.sql.dbtree.commons;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 * 
 * 데이터베이스 function을 조회하려는 목적으로 만들어짐.
 * 
 * @author KYJ
 *
 */
public abstract class FunctionItemTree<T> extends SchemaItemTree<T> {

	private SchemaItemTree<T> parent;

	public Supplier<Connection> conSupplier;

	public FunctionItemTree() throws Exception {
		super();
	}

	public FunctionItemTree(SchemaItemTree<T> parent, String name) throws Exception {
		this.parent = parent;
		this.conSupplier = parent.conSupplier;
		setName(name);

	}

	@Deprecated
	@Override
	public String getChildrenSQL(String... conditions) {
		return null;
	}

	@Deprecated
	@Override
	public ObservableList<TreeItem<DatabaseItemTree<T>>> applyChildren(List<Map<String, Object>> items) throws Exception {
		return null;
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
