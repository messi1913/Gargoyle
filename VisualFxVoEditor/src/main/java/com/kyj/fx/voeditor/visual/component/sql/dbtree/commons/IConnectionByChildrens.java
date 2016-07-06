/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.dbtree.commons
 *	작성일   : 2016. 6. 27.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.dbtree.commons;

import java.sql.Connection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/***************************
 * 
 * 커텍션으로부터 Childrens 요소를 리턴.
 * 
 * @author KYJ
 *
 ***************************/
public interface IConnectionByChildrens<T> {

	public default ObservableList<TreeItem<DatabaseItemTree<T>>> applyChildren(Connection con, String... args) throws Exception {
		return FXCollections.observableArrayList();
	}

}
