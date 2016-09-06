/**
 * package : com.kyj.fx.voeditor.visual.component.sql.nodes
 *	fileName : TableItemTree.java
 *	date      : 2015. 11. 8.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.dbtree.H2;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.DatabaseItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.SchemaItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.TableItemTree;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 * @author KYJ
 *
 */
public class H2TableItemTree extends TableItemTree<String> {

	public H2TableItemTree(SchemaItemTree<String> parent, String name) throws Exception {
		super(parent, name);
	}

	@Override
	public String getChildrenSQL(String... conditions) {
//
		String driver = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.DBMS_H2);
		String velocitySQL = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.SQL_TABLE_COLUMNS_WRAPPER, driver);

		HashMap<String, Object> hashMap = new HashMap<String,Object>();
		hashMap.put("tableName", conditions[1] );
		hashMap.put("databaseName",conditions[0] );
		return ValueUtil.getVelocityToText(velocitySQL, hashMap, true);

		// return "";
//		StringBuffer sb = new StringBuffer();
//		sb.append("SELECT COLUMN_NAME,  \n");
//		sb.append("CASE WHEN COLUMN_KEY = 'PRI' THEN 'Y' ELSE 'N' END PRIMARY_YN  \n");
//		sb.append("FROM INFORMATION_SCHEMA.`COLUMNS`\n");
//		sb.append("WHERE 1=1\n");
//		sb.append("AND TABLE_SCHEMA = '" + conditions[0] + "'\n");
//		sb.append("AND TABLE_NAME = '" + conditions[1] + "'\n");
//		return sb.toString();
	}

	@Override
	public ObservableList<TreeItem<DatabaseItemTree<String>>> applyChildren(List<Map<String, Object>> items) throws Exception {

		ObservableList<TreeItem<DatabaseItemTree<String>>> observableArrayList = FXCollections.observableArrayList();
		for (Map<String, Object> map : items) {
			Object columnName = map.get("COLUMN_NAME");
			Object isPrimarykey = map.get("COLUMN_KEY");
			if (columnName == null)
				continue;

			H2ColumnItemTree mysqlSchemaItemTree = new H2ColumnItemTree(this, columnName.toString());
			if (isPrimarykey != null)
				mysqlSchemaItemTree.setPrimaryKey("PRI".equals(isPrimarykey.toString()));

			TreeItem<DatabaseItemTree<String>> treeItem = new TreeItem<>(mysqlSchemaItemTree);
			observableArrayList.add(treeItem);
		}

		return observableArrayList;
	}

}
