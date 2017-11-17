
package com.kyj.fx.voeditor.visual.component.sql.dbtree.commons;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.FxCollectors;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 * 
 * 데이터베이스 function을 조회하려는 목적으로 만들어짐.
 * 
 * @author KYJ
 *
 */
public class ProcedureItemTree<T> extends SchemaItemTree<T> {

	private SchemaItemTree<T> parent;

	public Supplier<Connection> conSupplier;

	public ProcedureItemTree() throws Exception {
		super();
	}

	private String cat;
	private String schem;
	private String remark;
	private String procedureName;

	public ProcedureItemTree(SchemaItemTree<T> parent, String cat, String schem, String procedureName, String remark) throws Exception {
		this.parent = parent;
		this.conSupplier = parent.conSupplier;
		this.procedureName = procedureName;
		this.cat = cat;
		this.schem = schem;
		this.remark = remark;
		setName(procedureName);
	}

	public String getCat() {
		return cat;
	}

	public String getSchem() {
		return schem;
	}

	public String getRemark() {
		return remark;
	}

	public String getProcedureName() {
		return procedureName;
	}

	@Deprecated
	@Override
	public String getChildrenSQL(String... conditions) {
		return null;
	}

	@Override
	public ObservableList<TreeItem<DatabaseItemTree<T>>> applyChildren(List<Map<String, Object>> items) throws Exception {
		ObservableList<TreeItem<DatabaseItemTree<T>>> collect = FXCollections.emptyObservableList();

		if (ValueUtil.isNotEmpty(items)) {
			collect = items.stream().map(m -> {

				try {
					ProcedureColumnsTree<T> tree = new ProcedureColumnsTree<>(this, m);
					TreeItem<DatabaseItemTree<T>> treeItem = new TreeItem<DatabaseItemTree<T>>();
					treeItem.setValue(tree);
					treeItem.setGraphic(tree.createGraphics());
					return treeItem;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}).collect(FxCollectors.toObservableList());
		}

		return collect;
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

		Connection con = getConnection();
		List<Map<String, Object>> procedureColumns = DbUtil.getProcedureColumns(con, cat, schem, getProcedureName());
		getChildrens().addAll(applyChildren(procedureColumns));

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
