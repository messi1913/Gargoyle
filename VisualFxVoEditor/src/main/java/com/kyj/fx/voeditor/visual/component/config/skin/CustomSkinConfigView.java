/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2016. 2. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.config.skin;

import java.net.URISyntaxException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.ConfigItemTreeItem;
import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.main.model.vo.ConfigurationLeafNodeItem;
import com.kyj.fx.voeditor.visual.main.model.vo.ConfigurationTreeItem;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

/**
 * Skin목록을 보여주는 뷰
 *
 * @author KYJ
 *
 */
@FXMLController(value = "CustomSkinConfigView.fxml", isSelfController = true)
public class CustomSkinConfigView extends BorderPane {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomSkinConfigView.class);

	@FXML
	private TreeTableView<ConfigurationTreeItem> ttvIConfig;

	@FXML
	private TreeTableColumn<ConfigurationTreeItem, String> ttcConfig;

	/**
	 * 생성자O
	 *
	 * @throws URISyntaxException
	 */
	public CustomSkinConfigView() {
		FxUtil.loadRoot(CustomSkinConfigView.class, this, ex -> LOGGER.error(ValueUtil.toString(ex)));
	}

	@FXML
	public void initialize() {

		ttvIConfig.setRowFactory(new Callback<TreeTableView<ConfigurationTreeItem>, TreeTableRow<ConfigurationTreeItem>>() {

			@Override
			public TreeTableRow<ConfigurationTreeItem> call(TreeTableView<ConfigurationTreeItem> param) {
				return new TreeTableRow<>();
			}
		});

		ttcConfig.setCellValueFactory(param -> param.getValue().getValue().itemNameProperty());

		ConfigurationTreeItem root = new ConfigurationTreeItem();
		root.setItemName("Basic");
		{
			ConfigurationLeafNodeItem fontConfigItem = new ConfigurationLeafNodeItem();
			fontConfigItem.setItemName("Font");

			ConfigurationLeafNodeItem backgroundConfigItem = new ConfigurationLeafNodeItem();
			backgroundConfigItem.setItemName("Background");

			root.setChildrens(Arrays.asList(fontConfigItem, backgroundConfigItem));
		}

		TreeItem<ConfigurationTreeItem> createNode = new ConfigItemTreeItem().createNode(root);
		ttvIConfig.setRoot(createNode);

	}

}
