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
import com.kyj.fx.voeditor.visual.component.sql.dbtree.oracle.OracleDatabaseItemTree;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.DialogUtil;

import javafx.event.ActionEvent;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

/**
 * 오라클 조회 패널
 * @author KYJ
 *
 */
public class OracleSqlPane extends CommonsSqllPan {

	public OracleSqlPane() {
		super(ResourceLoader.DBMS_SUPPORT_ORACLE);
	}

	public OracleSqlPane(String dbms) {
		super(dbms);
	}

	@Override
	public TreeItem<DatabaseItemTree<String>> apply(String t, Supplier<Connection> conSupplier) {
		try {
			DatabaseItemTree<String> databaseItemTree = new OracleDatabaseItemTree("databases", conSupplier);
			TreeItem<DatabaseItemTree<String>> createNode = new DatabaseTreeNode().createNode(databaseItemTree);
			return createNode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.sql.view.SqlPane#menuExportMergeScriptOnAction(javafx.event.ActionEvent)
	 */
	@Override
	public void menuExportMergeScriptOnAction(ActionEvent e) {
		DialogUtil.showMessageDialog((Stage) this.getScene().getWindow(), "Not yet support");

	}

}
