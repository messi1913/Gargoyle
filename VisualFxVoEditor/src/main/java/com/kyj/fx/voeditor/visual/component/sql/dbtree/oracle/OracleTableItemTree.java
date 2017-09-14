/**
 * package : com.kyj.fx.voeditor.visual.component.sql.nodes
 *	fileName : TableItemTree.java
 *	date      : 2015. 11. 8.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.dbtree.oracle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.DatabaseItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.SchemaItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.TableItemTree;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 * @author KYJ
 *
 */
public class OracleTableItemTree extends TableItemTree<String> {

	private static Logger LOGGER = LoggerFactory.getLogger(OracleTableItemTree.class);

	public OracleTableItemTree(SchemaItemTree<String> parent, String name) throws Exception {
		super(parent, name);

	}

	@Override
	public String getChildrenSQL(String... conditions) {
		String driver = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.DBMS_ORACLE);
		String sql = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.SQL_TABLE_COLUMNS_WRAPPER, driver);

//		sql = ValueUtil.regexReplaceMatchs(":databaseName", sql, conditions[0]);
		// 테이블에 $가 있는 특수문자에서는 에러발생하므로 fix
		// sql = sql.replaceAll(":tableName", conditions[1]);
//		sql = ValueUtil.regexReplaceMatchs(":tableName", sql, conditions[1]);

		HashMap<String, Object> map = new HashMap<String,Object>();
		map.put("databaseName", conditions[0]);
		map.put("tableName", conditions[1]);
		sql = ValueUtil.getVelocityToText(sql, map, true);
		LOGGER.debug(sql);
		return sql;
	}

	@Override
	public ObservableList<TreeItem<DatabaseItemTree<String>>> applyChildren(List<Map<String, Object>> items) throws Exception {

		ObservableList<TreeItem<DatabaseItemTree<String>>> observableArrayList = FXCollections.observableArrayList();
		for (Map<String, Object> map : items) {
			Object columnName = map.get("COLUMN_NAME");
			Object primaryYn = map.get("COLUMN_KEY");

			if (columnName == null)
				continue;

			OracleColumnItemTree mysqlSchemaItemTree = new OracleColumnItemTree(this, columnName.toString());
			mysqlSchemaItemTree.setPrimaryKey("PRI".equals(primaryYn));

			TreeItem<DatabaseItemTree<String>> treeItem = new TreeItem<>(mysqlSchemaItemTree);
			observableArrayList.add(treeItem);
		}

		return observableArrayList;
	}

}
