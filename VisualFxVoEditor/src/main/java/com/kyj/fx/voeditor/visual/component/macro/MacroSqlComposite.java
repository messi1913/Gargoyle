/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.macro
 *	작성일   : 2016. 9. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.macro;

import java.sql.Connection;
import java.util.function.Supplier;

import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.framework.annotation.FxPostInitialize;
import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;

/***************************
 * 
 * 즐겨찾기 기능이 적용된 매크로뷰
 * 
 * @author KYJ
 *
 ***************************/
@FXMLController(value = "MacroSqlView.fxml", isSelfController = true)
public class MacroSqlComposite extends BorderPane {

	@FXML
	private MacroFavorTreeView tvFavorite;
	@FXML
	private BorderPane borContent;

	private Supplier<Connection> connectionSupplier;
	private String initText;

	public MacroSqlComposite(Supplier<Connection> connectionSupplier, String initText) {
		this.connectionSupplier = connectionSupplier;
		this.initText = initText;
		try {
			FxUtil.loadRoot(MacroSqlComposite.class, this);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@FxPostInitialize
	public void post() {

		MenuItem menuAddItem = new MenuItem("Add");
		MenuItem menuDeleteItem = new MenuItem("Delete");
		tvFavorite.setContextMenu(new ContextMenu(menuAddItem, menuDeleteItem));

		borContent.setCenter(new MacroControl(connectionSupplier, initText));

		MacroFavorTreeItemCreator macroFavorTreeItem = new MacroFavorTreeItemCreator(connectionSupplier);

		MacroItemVO f = new MacroItemVO();
		tvFavorite.setRoot(macroFavorTreeItem.createRoot(f));
		tvFavorite.setShowRoot(false);
	}

}
