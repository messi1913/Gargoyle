/**
 * package : com.kyj.fx.voeditor.visual.component.sql.nodes
 *	fileName : TableItemTree.java
 *	date      : 2015. 11. 8.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.dbtree.mysql;

import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.DatabaseItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.SchemaItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.TableItemTree;

/**
 * @author KYJ
 *
 */
public class MYSQLTableItemTree extends TableItemTree<String> {

	public MYSQLTableItemTree(SchemaItemTree<String> parent, String name) throws Exception {
		super(parent, name);
	}

	@Override
	public String getChildrenSQL(String... conditions) {

		// return "";
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COLUMN_NAME,  \n");
		sb.append("CASE WHEN COLUMN_KEY = 'PRI' THEN 'Y' ELSE 'N' END PRIMARY_YN  \n");
		sb.append("FROM INFORMATION_SCHEMA.`COLUMNS`\n");
		sb.append("WHERE 1=1\n");
		sb.append("AND TABLE_SCHEMA = '" + conditions[0] + "'\n");
		sb.append("AND TABLE_NAME = '" + conditions[1] + "'\n");
		return sb.toString();
	}

	@Override
	public ObservableList<TreeItem<DatabaseItemTree<String>>> applyChildren(List<Map<String, Object>> items) throws Exception {

		ObservableList<TreeItem<DatabaseItemTree<String>>> observableArrayList = FXCollections.observableArrayList();
		for (Map<String, Object> map : items) {
			Object columnName = map.get("COLUMN_NAME");
			Object isPrimarykey = map.get("PRIMARY_YN");
			if (columnName == null)
				continue;

			MYSQLColumnItemTree mysqlSchemaItemTree = new MYSQLColumnItemTree(this, columnName.toString());
			if (isPrimarykey != null)
				mysqlSchemaItemTree.setPrimaryKey("Y".equals(isPrimarykey.toString()));

			TreeItem<DatabaseItemTree<String>> treeItem = new TreeItem<>(mysqlSchemaItemTree);
			observableArrayList.add(treeItem);
		}

		return observableArrayList;
	}

}
