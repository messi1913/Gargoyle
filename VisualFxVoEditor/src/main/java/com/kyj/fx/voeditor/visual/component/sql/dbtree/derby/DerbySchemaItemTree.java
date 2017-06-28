/**
 * package : com.kyj.fx.voeditor.visual.component.sql.nodes
 *	fileName : TableItemTree.java
 *	date      : 2015. 11. 8.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.dbtree.derby;

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
public class DerbySchemaItemTree extends SchemaItemTree<String> {

	public DerbySchemaItemTree(DatabaseItemTree<String> parent, String name) throws Exception {
		super(parent, name);
	}

	private String schemaId;

	@Override
	public String getChildrenSQL(String... conditions) {
		return "select * from sys.systables where schemaid = '" + this.schemaId + "' order by tablename";
		//		return "show tables in " + conditions[0] + " order by table_name";

		//		return "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.`TABLES` WHERE TABLE_SCHEMA = '" + conditions[0] + "' ORDER BY TABLE_NAME";
	}

	@Override
	public ObservableList<TreeItem<DatabaseItemTree<String>>> applyChildren(List<Map<String, Object>> items) throws Exception {
		ObservableList<TreeItem<DatabaseItemTree<String>>> observableArrayList = FXCollections.observableArrayList();
		for (Map<String, Object> map : items) {
			Object tableName = map.get("TABLENAME");
			if (tableName == null)
				continue;

			String tableId = map.get("TABLEID").toString();
			DerbyTableItemTree mysqlSchemaItemTree = new DerbyTableItemTree(this, tableName.toString());
			mysqlSchemaItemTree.setTableId(tableId);

			TreeItem<DatabaseItemTree<String>> treeItem = new TreeItem<>(mysqlSchemaItemTree);
			//treeItem.setGraphic(graphicsNode(tableName.toString()));
			observableArrayList.add(treeItem);
		}

		return observableArrayList;
	}

	public void setSchemaId(String schemaId) {
		this.schemaId = schemaId;
	}

	public String getSchemaId() {
		return schemaId;
	}

	//	public Node graphicsNode(String item) {
	//		return new Label(item);
	//	}

}
