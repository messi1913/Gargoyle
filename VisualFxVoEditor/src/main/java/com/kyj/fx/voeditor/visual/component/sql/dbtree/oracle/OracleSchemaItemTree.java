/**
 * package : com.kyj.fx.voeditor.visual.component.sql.nodes
 *	fileName : TableItemTree.java
 *	date      : 2015. 11. 8.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.dbtree.oracle;

import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.DatabaseItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.SchemaItemTree;

/**
 * 데이터베이스 스키마 정보 출력
 * 
 * @author KYJ
 *
 */
public class OracleSchemaItemTree extends SchemaItemTree<String> {

	public OracleSchemaItemTree(DatabaseItemTree<String> parent, String name) throws Exception {
		super(parent, name);
	}

	@Override
	public String getChildrenSQL(String... conditions) {
		return "SELECT TABLE_NAME FROM ALL_TABLES WHERE  OWNER = '" + conditions[0] + "' ORDER BY TABLE_NAME";
	}

	@Override
	public ObservableList<TreeItem<DatabaseItemTree<String>>> applyChildren(List<Map<String, Object>> items)
			throws Exception {
		ObservableList<TreeItem<DatabaseItemTree<String>>> observableArrayList = FXCollections.observableArrayList();
		for (Map<String, Object> map : items) {
			Object tableName = map.get("TABLE_NAME");
			if (tableName == null)
				continue;

			OracleTableItemTree mysqlSchemaItemTree = new OracleTableItemTree(this, tableName.toString());

			TreeItem<DatabaseItemTree<String>> treeItem = new TreeItem<>(mysqlSchemaItemTree);
			observableArrayList.add(treeItem);
		}

		return observableArrayList;
	}
}
