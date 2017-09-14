/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.view
 *	작성일   : 2015. 11. 9.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.view;

import com.kyj.fx.voeditor.visual.component.sql.dbtree.DatabaseTreeNode;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.H2.H2DatabaseItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.DatabaseItemTree;
import com.kyj.fx.voeditor.visual.component.sql.functions.ConnectionSupplier;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.DialogUtil;

import javafx.event.ActionEvent;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

/**
 *
 * H2 데이터베이스 SQL 패널
 *
 * @author KYJ
 *
 */

public class H2Pane extends CommonsSqllPan {
	// private static Logger LOGGER = LoggerFactory.getLogger(H2Pane.class);

	public H2Pane() {
		super(ResourceLoader.DBMS_SUPPORT_H2);
	}

	public H2Pane(String dbms) {
		super(dbms);
	}

	@Override
	public TreeItem<DatabaseItemTree<String>> apply(String t, ConnectionSupplier conSupplier) {
		try {
			DatabaseItemTree<String> databaseItemTree = new H2DatabaseItemTree("databases", conSupplier);
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
