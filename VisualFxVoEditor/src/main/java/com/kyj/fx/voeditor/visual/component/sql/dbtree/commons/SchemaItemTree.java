/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.dbtree.commons
 *	작성일   : 2017. 9. 12.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.dbtree.commons;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.kyj.fx.voeditor.visual.exceptions.GargoyleConnectionFailException;
import com.kyj.fx.voeditor.visual.util.DbUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 * 
 * 프로시저 정보를 처리하기 위한 트리
 * 
 * @author KYJ
 *
 */
public abstract class SchemaItemTree<T> extends DatabaseItemTree<T> {

//	private static final Logger LOGGER = LoggerFactory.getLogger(SchemaItemTree.class);

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
			ObservableList<TreeItem<DatabaseItemTree<T>>> second = applyChildren(connection, getName());
			if (second != null)
				childrens.addAll(second);

			String catalog = this.getName();
			// try (Connection con = getConnection()) {
			ResultSet rs = connection.getMetaData().getProcedures(catalog, null, "%");

			while (rs.next()) {
				String cat = rs.getString(1);
				String schem = rs.getString(2);
				String name = rs.getString(3);

				String remark = rs.getString(7);
				String type = rs.getString(8);

				// LOGGER.debug("cat {} schem {} name {} {} ", cat, schem, name,
				// type);

				DatabaseItemTree<T> procd = createProcedureItemTree(cat, schem, name, type, remark);
				if (procd != null)
					childrens.add(new TreeItem<>(procd));

			}

			// Functions
			childrens.addAll(getFunctions());

		} finally {
			if (connection != null)
				connection.close();
		}
	}

	public Connection getConnection() {
		return this.conSupplier.get();
	}

	/**
	 * 펑션 리턴.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 12.
	 * @return
	 */
	protected ObservableList<TreeItem<DatabaseItemTree<T>>> getFunctions() {
		return FXCollections.emptyObservableList();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 12.
	 * @param cat
	 * @param schem
	 * @param na
	 * @param type
	 * @param remark
	 * @return
	 * @throws Exception
	 */
	protected DatabaseItemTree<T> createProcedureItemTree(String cat, String schem, String name, String type, String remark)
			throws Exception {
		return new ProcedureItemTree<T>(this, cat, schem, name, remark);
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
