/**
 * package : com.kyj.fx.voeditor.visual.component.sql.nodes
 *	fileName : DbsItemTree.java
 *	date      : 2015. 11. 8.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.dbtree.H2;

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
public class H2DatabaseItemTree extends DatabaseItemTree<String> {

	public H2DatabaseItemTree(String name, ConnectionSupplier conSupplier) throws Exception {
		super(name, conSupplier);
	}

	@Override
	public String getChildrenSQL(String... conditions) {
		return "SHOW DATABASES";
	}

	@Override
	public ObservableList<TreeItem<DatabaseItemTree<String>>> applyChildren(List<Map<String, Object>> items)
			throws Exception {

		ObservableList<TreeItem<DatabaseItemTree<String>>> observableArrayList = FXCollections.observableArrayList();
		for (Map<String, Object> map : items) {
			Object databaseName = map.get("SCHEMA_NAME");
			if (databaseName == null)
				continue;

			H2SchemaItemTree mysqlSchemaItemTree = new H2SchemaItemTree(this, databaseName.toString());
			TreeItem<DatabaseItemTree<String>> treeItem = new TreeItem<>(mysqlSchemaItemTree);
			// treeItem.setGraphic(graphicsNode(databaseName.toString()));

			observableArrayList.add(treeItem);
		}

		return observableArrayList;
	}

	// public Node graphicsNode(String item) {
	// return new Label(item);
	// }

}
