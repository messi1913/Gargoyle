/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.macro
 *	작성일   : 2016. 9. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.macro;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.framework.annotation.FxPostInitialize;
import com.kyj.fx.voeditor.visual.framework.thread.ExecutorDemons;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(MacroSqlComposite.class);

	@FXML
	private BorderPane borTree;

	@FXML
	private MacroFavorTreeView tvFavorite;

	@FXML
	private BorderPane borContent;

	private String initText;
	private Supplier<Connection> connectionSupplier;
	private MacroTableGeneratorable createScript;

	public MacroSqlComposite(MacroTableGeneratorable createScript, String initText) {
		this.initText = initText;
		this.createScript = createScript;
		this.connectionSupplier = createScript.connectionSupplier();
		try {
			FxUtil.loadRoot(MacroSqlComposite.class, this);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@FxPostInitialize
	public void post() {

		// 스크립트 실행
		ExecutorDemons.getGargoyleSystemExecutorSerivce().execute(() -> {
			if (createScript != null) {
				try {
					createScript.execute();
				} catch (Exception e1) {
					LOGGER.error(ValueUtil.toString(e1));
				}
			}
		});

		MenuItem menuAddItem = new MenuItem("Add");
		menuAddItem.setAccelerator(new KeyCodeCombination(KeyCode.INSERT, KeyCharacterCombination.CONTROL_DOWN));
		menuAddItem.setOnAction(e -> {
			addOnAction();
		});

		MenuItem menuDeleteItem = new MenuItem("Delete");
		menuDeleteItem.setAccelerator(new KeyCodeCombination(KeyCode.DELETE, KeyCharacterCombination.CONTROL_DOWN));
		menuDeleteItem.setOnAction(e -> {
			addOnAction();
		});

		tvFavorite.setContextMenu(new ContextMenu(menuAddItem, menuDeleteItem));

		borContent.setCenter(new MacroControl(connectionSupplier, initText));

		MacroFavorTreeItemCreator macroFavorTreeItem = new MacroFavorTreeItemCreator(connectionSupplier);

		MacroItemVO f = new MacroItemVO();
		tvFavorite.setRoot(macroFavorTreeItem.createRoot(f));
		tvFavorite.setShowRoot(true);
	}

	@FXML
	public void addOnAction() {
		LOGGER.debug("add item");
	}

	@FXML
	public void modifyOnAction() {

	}

	public void removeOnAction() {

	}
}
