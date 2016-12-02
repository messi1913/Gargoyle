/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2016. 2. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.config.skin;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.ConfigItemTreeItem;
import com.kyj.fx.voeditor.visual.component.font.FontViewComposite;
import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.main.model.vo.ConfigurationGraphicsNodeItem;
import com.kyj.fx.voeditor.visual.main.model.vo.ConfigurationLeafNodeItem;
import com.kyj.fx.voeditor.visual.main.model.vo.ConfigurationTreeItem;
import com.kyj.fx.voeditor.visual.momory.SkinManager;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
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
	@FXML
	private Button btnEdit;

	private ObjectProperty<ConfigurationTreeItem> selectedItem = new SimpleObjectProperty<ConfigurationTreeItem>();

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
				TreeTableRow<ConfigurationTreeItem> treeTableRow = new TreeTableRow<>();

				treeTableRow.setOnMouseClicked(ev -> {
					ConfigurationTreeItem item = treeTableRow.getItem();
					selectedItem.set(item);
				});
				return treeTableRow;
			}
		});

		ttcConfig.setCellValueFactory(param -> param.getValue().getValue().itemNameProperty());

		ConfigurationTreeItem root = new ConfigurationTreeItem();
		root.setItemName("Basic");
		{
			ConfigurationLeafNodeItem fontConfigItem = new ConfigurationLeafNodeItem();
			fontConfigItem.setItemName("Font");
			fontConfigItem.setContentNode(FontViewComposite.class);

			ConfigurationLeafNodeItem backgroundConfigItem = new ConfigurationLeafNodeItem();
			backgroundConfigItem.setItemName("Background");
			backgroundConfigItem.setContentNode(SkinPreviewViewComposite.class);
			root.setChildrens(Arrays.asList(fontConfigItem, backgroundConfigItem));
		}

		TreeItem<ConfigurationTreeItem> createNode = new ConfigItemTreeItem().createNode(root);
		createNode.setExpanded(true);
		ttvIConfig.setRoot(createNode);

	}

	/**
	 * Load TreeItem
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 2.
	 * @param item
	 */
	private void load(ConfigurationTreeItem item) {
		if (item != null) {
			if (item instanceof ConfigurationGraphicsNodeItem) {
				ConfigurationGraphicsNodeItem node = (ConfigurationGraphicsNodeItem) item;
				if (node.getCustomOpenStyle() != null) {
					Consumer<Class<?>> customOpenStyle = node.getCustomOpenStyle();
					customOpenStyle.accept(node.getContentNode());
				} else if (node.getContentNode() != null) {

					Class<?> cont = node.getContentNode();
					try {
						Object newInstance = cont.newInstance();
						if (newInstance instanceof Parent) {
							FxUtil.createStageAndShow((Parent) newInstance, stage -> {
								stage.initOwner(CustomSkinConfigView.this.getScene().getWindow());
								stage.setTitle(node.getItemName());
							});
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}
	}

	@FXML
	public void btnEditOnAction() {

		ConfigurationTreeItem value = selectedItem.getValue();
		if (value != null) {
			if (value instanceof ConfigurationGraphicsNodeItem) {
				load((ConfigurationGraphicsNodeItem) value);
			}
		}

	}

	/**
	 *  스킨 초기화
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 1.
	 */
	@FXML
	public void btnResetOnAction() {
		SkinManager.getInstance().resetSkin();
	}

}
