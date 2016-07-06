/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.dbtree
 *	작성일   : 2016. 1. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.dbtree;

import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.DatabaseItemTree;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

/**
 * @author KYJ
 *
 */
public class DatabaseTreeCallback<K extends DatabaseItemTree<String>> implements Callback<TreeView<K>, TreeCell<K>> {

	@Override
	public TreeCell<K> call(TreeView<K> param) {
		return new DatabaseTreeCell<>();
	}

}
