/**
 * package : com.kyj.fx.voeditor.visual.component.sql.nodes
 *	fileName : DbsItemTree.java
 *	date      : 2015. 11. 8.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.dbtree.derby;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.DatabaseItemTree;

/**
 * 데이터베이스 세션정보 출력
 *
 * @author KYJ
 *
 */
public class DerbyDatabaseItemTree extends DatabaseItemTree<String> {

	public DerbyDatabaseItemTree(String name, Supplier<Connection> conSupplier) throws Exception {
		super(name, conSupplier);
	}

	@Override
	public String getChildrenSQL(String... conditions) {
		return "select * from sys.sysschemas";
	}

	@Override
	public ObservableList<TreeItem<DatabaseItemTree<String>>> applyChildren(List<Map<String, Object>> items)
			throws Exception {

		ObservableList<TreeItem<DatabaseItemTree<String>>> observableArrayList = FXCollections.observableArrayList();
		for (Map<String, Object> map : items) {
			Object databaseName = map.get("SCHEMANAME");
			if (databaseName == null)
				continue;

			String schemaId = map.get("SCHEMAID").toString();
			DerbySchemaItemTree derbyItemTree = new DerbySchemaItemTree(this, databaseName.toString());
			derbyItemTree.setSchemaId(schemaId);
			TreeItem<DatabaseItemTree<String>> treeItem = new TreeItem<>(derbyItemTree);
			// treeItem.setGraphic(graphicsNode(databaseName.toString()));

			observableArrayList.add(treeItem);
		}

		return observableArrayList;
	}

	// public Node graphicsNode(String item) {
	// return new Label(item);
	// }

}
