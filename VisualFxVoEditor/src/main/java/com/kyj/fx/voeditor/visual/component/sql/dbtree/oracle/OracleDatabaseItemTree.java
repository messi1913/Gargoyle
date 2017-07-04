/**
 * package : com.kyj.fx.voeditor.visual.component.sql.nodes
 *	fileName : DbsItemTree.java
 *	date      : 2015. 11. 8.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.dbtree.oracle;

import java.util.List;
import java.util.Map;

import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.DatabaseItemTree;
import com.kyj.fx.voeditor.visual.component.sql.functions.ConnectionSupplier;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 * 데이터베이스 세션정보 출력
 * 
 * @author KYJ
 *
 */
public class OracleDatabaseItemTree extends DatabaseItemTree<String> {

	public OracleDatabaseItemTree() throws Exception {
		super();
	}

	public OracleDatabaseItemTree(String name, ConnectionSupplier conSupplier) throws Exception {
		super(name, conSupplier);
	}

	@Override
	public String getChildrenSQL(String... conditions) {
		return "SELECT USERNAME AS DATABASE FROM ALL_USERS A WHERE EXISTS (SELECT '1' FROM ALL_TABLES  B WHERE A.USERNAME = B.OWNER)";
	}

	@Override
	public ObservableList<TreeItem<DatabaseItemTree<String>>> applyChildren(List<Map<String, Object>> items)
			throws Exception {

		ObservableList<TreeItem<DatabaseItemTree<String>>> observableArrayList = FXCollections.observableArrayList();
		for (Map<String, Object> map : items) {
			Object databaseName = map.get("DATABASE");
			if (databaseName == null)
				continue;

			OracleSchemaItemTree schemaItemTree = new OracleSchemaItemTree(this, databaseName.toString());
			TreeItem<DatabaseItemTree<String>> treeItem = new TreeItem<>(schemaItemTree);
			observableArrayList.add(treeItem);
		}

		return observableArrayList;
	}
}
