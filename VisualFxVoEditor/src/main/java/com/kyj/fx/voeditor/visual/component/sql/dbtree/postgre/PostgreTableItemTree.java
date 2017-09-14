/**
 * package : com.kyj.fx.voeditor.visual.component.sql.nodes
 *	fileName : TableItemTree.java
 *	date      : 2015. 11. 8.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.dbtree.postgre;

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
public class PostgreTableItemTree extends TableItemTree<String> {

	/**
	 * dirty..
	 * @throws Exception
	 */
	public PostgreTableItemTree() throws Exception {

	}

	public PostgreTableItemTree(SchemaItemTree<String> parent, String name) throws Exception {
		super(parent, name);
	}

	@Override
	public String getChildrenSQL(String... conditions) {

		// StringBuffer sb = new StringBuffer();
		// sb.append("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS\n");
		// sb.append("WHERE 1=1\n");
		// sb.append("AND TABLE_SCHEMA = '" + conditions[0] + "'\n");
		// sb.append("AND TABLE_NAME = '" + conditions[1] + "'\n");

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT  B. COLUMN_NAME, A.PK AS PRIMARY_YN FROM (\n");
		sb.append("	SELECT C.COLUMN_NAME, 'Y' AS PK\n");
		sb.append("	FROM INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE C, INFORMATION_SCHEMA.TABLE_CONSTRAINTS S  \n");
		sb.append("	WHERE C.CONSTRAINT_NAME = S.CONSTRAINT_NAME  \n");
		sb.append("	AND S.CONSTRAINT_TYPE = 'PRIMARY KEY' AND C.TABLE_NAME = '" + conditions[1] + "') AS A RIGHT OUTER JOIN \n");
		sb.append("	INFORMATION_SCHEMA.COLUMNS AS B \n");
		sb.append("	ON A.COLUMN_NAME = B.COLUMN_NAME\n");
		sb.append("	WHERE B.TABLE_NAME = '" + conditions[1] + "' and B.TABLE_SCHEMA = '" + conditions[0] + "'\n");
		sb.append("	ORDER BY B.ORDINAL_POSITION\n");
		sb.toString();

		return sb.toString();
	}

	@Override
	public ObservableList<TreeItem<DatabaseItemTree<String>>> applyChildren(List<Map<String, Object>> items) throws Exception {

		ObservableList<TreeItem<DatabaseItemTree<String>>> observableArrayList = FXCollections.observableArrayList();
		for (Map<String, Object> map : items) {
			Object columnName = map.get("column_name");
			Object primaryKeyYn = map.get("primary_yn");
			if (columnName == null)
				continue;

			PostgreColumnItemTree mysqlSchemaItemTree = new PostgreColumnItemTree(this, columnName.toString());
			mysqlSchemaItemTree.setPrimaryKey("Y".equals(primaryKeyYn));
			TreeItem<DatabaseItemTree<String>> treeItem = new TreeItem<>(mysqlSchemaItemTree);
			observableArrayList.add(treeItem);
		}

		return observableArrayList;
	}

}
