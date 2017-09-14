/**
 * package : com.kyj.fx.voeditor.visual.component.sql.tab
 *	fileName : SqlTabPane.java
 *	date      : 2015. 11. 6.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.tab;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * @author KYJ
 *
 */
public class SqlTabPane extends TabPane {

	/**
	 * 
	 */
	public SqlTabPane() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	@SafeVarargs
	public SqlTabPane(SqlTab... items) {
		super(items);
	}

	@Override
	protected ObservableList<Node> getChildren() {
		return super.getChildren();
	}

	public SqlTab getSelectedTab() {
		Tab selectedItem = getSelectionModel().getSelectedItem();
		return (SqlTab) selectedItem;
	}
}
