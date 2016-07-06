/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.view
 *	작성일   : 2015. 11. 9.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.view;

import java.sql.Connection;
import java.util.function.Supplier;

import com.kyj.fx.voeditor.visual.component.sql.dbtree.DatabaseTreeNode;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.DatabaseItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.mysql.MySQLDatabaseItemTree;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;

import javafx.scene.control.TreeItem;

/**
 * MYSQL 조회 패널
 *
 * @author KYJ
 *
 */

public class MysqlPane extends CommonsSqllPan {
	// private static Logger LOGGER = LoggerFactory.getLogger(MysqlPane.class);

	public MysqlPane() {
		super(ResourceLoader.DBMS_SUPPORT_MY_SQL);
	}

	public MysqlPane(String dbms) {
		super(dbms);
	}

	@Override
	public TreeItem<DatabaseItemTree<String>> apply(String t, Supplier<Connection> conSupplier) {
		try {
			DatabaseItemTree<String> databaseItemTree = new MySQLDatabaseItemTree("databases", conSupplier);
			TreeItem<DatabaseItemTree<String>> createNode = new DatabaseTreeNode().createNode(databaseItemTree);
			return createNode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

}
