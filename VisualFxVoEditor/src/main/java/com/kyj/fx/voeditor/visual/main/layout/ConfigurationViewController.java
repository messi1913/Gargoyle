/**
 * KYJ
 * 2015. 10. 15.
 */
package com.kyj.fx.voeditor.visual.main.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.kyj.fx.voeditor.visual.component.ConfigItemTreeItem;
import com.kyj.fx.voeditor.visual.component.ResourcesConfigView;
import com.kyj.fx.voeditor.visual.component.SceneBuilderLocationComposite;
import com.kyj.fx.voeditor.visual.component.SkinConfigView;
import com.kyj.fx.voeditor.visual.component.popup.DatabaseConfigView;
import com.kyj.fx.voeditor.visual.component.popup.DatabaseUrlManagementView;
import com.kyj.fx.voeditor.visual.component.scm.SVNConfigView;
import com.kyj.fx.voeditor.visual.main.model.vo.ConfigurationLeafNodeItem;
import com.kyj.fx.voeditor.visual.main.model.vo.ConfigurationTreeItem;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 *
 */
public class ConfigurationViewController {

	@FXML
	private TreeView<ConfigurationTreeItem> tvItems;

	/**
	 * 설정 콤포넌트가 로드될 위치
	 *
	 * @최초생성일 2015. 11. 4.
	 */
	@FXML
	private BorderPane borContent;

	/**
	 *
	 * @최초생성일 2015. 11. 4.
	 */
	private ConfigurationTreeItem root;

	/**
	 * 생성자
	 */
	public ConfigurationViewController() {

		// TODO 설정파일로 트리에 보여줄 내용이 동적으로 불러들일 수 있게 처리하도록 하면 좋을것같다.
		ConfigurationTreeItem databases = new ConfigurationTreeItem();
		databases.setItemName("Database");

		ConfigurationTreeItem resources = new ConfigurationTreeItem();
		resources.setItemName("Resources");

		ConfigurationTreeItem javafx = new ConfigurationTreeItem();
		javafx.setItemName("Java FX");

		root = new ConfigurationTreeItem(Arrays.asList(databases, resources, javafx));
		root.setItemName("Configurations");
		{
			ConfigurationLeafNodeItem children1 = new ConfigurationLeafNodeItem();
			children1.setItemName("Database Settings");
			children1.setContentNode(DatabaseConfigView.class);

			ConfigurationLeafNodeItem children2 = new ConfigurationLeafNodeItem();
			children2.setItemName("Database Url Management");
			children2.setContentNode(DatabaseUrlManagementView.class);

			databases.setChildrens(Arrays.asList(children1, children2));
		}

		List<ConfigurationTreeItem> resourcesChildrens = new ArrayList<>();
		{
			{
				ConfigurationLeafNodeItem children3 = new ConfigurationLeafNodeItem();
				children3.setItemName("Resources Configuration");
				children3.setContentNode(ResourcesConfigView.class);
				resourcesChildrens.add(children3);
			}

			{
				ConfigurationLeafNodeItem children4 = new ConfigurationLeafNodeItem();
				children4.setItemName("SVN Configuration");
				children4.setContentNode(SVNConfigView.class);
				resourcesChildrens.add(children4);
			}

			{
				ConfigurationLeafNodeItem children3 = new ConfigurationLeafNodeItem();
				children3.setItemName("Skin Configuration");
				children3.setContentNode(SkinConfigView.class);
				resourcesChildrens.add(children3);
			}
		}
		resources.setChildrens(resourcesChildrens);

		List<ConfigurationTreeItem> javafxChildrens = new ArrayList<>();
		{
			{
				ConfigurationLeafNodeItem children = new ConfigurationLeafNodeItem();
				children.setItemName("SceneBuilder Location");
				children.setContentNode(SceneBuilderLocationComposite.class);
				javafxChildrens.add(children);
			}
		}
		javafx.setChildrens(javafxChildrens);

	}

	@FXML
	public void initialize() {
		ConfigItemTreeItem configItemTreeItem = new ConfigItemTreeItem();
		TreeItem<ConfigurationTreeItem> createNode = configItemTreeItem.createNode(root);
		tvItems.setRoot(createNode);
		createNode.setExpanded(true);
		tvItems.setShowRoot(true);

	}

	/**
	 * 트리를 더블클릭할때 처리되는 이벤트 함수이다.
	 *
	 * ConfigurationLeafNodeItem 타입의 트리노드인 경우에 ConfigurationLeafNodeItem 클래스안의
	 * ContentNode에 존재하는 Class를 읽은후 객체를 생성하며, 객체생성에 문제가 없는 경우 화면에 불러들인다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 11. 4.
	 * @param e
	 */
	@FXML
	public void tvItemsMouseClick(MouseEvent e) {
		if (e.getClickCount() == 2) {
			TreeItem<ConfigurationTreeItem> selectedItem = tvItems.getSelectionModel().getSelectedItem();
			if (selectedItem == null) {
				return;
			}

			ConfigurationTreeItem value = selectedItem.getValue();
			if (value == null)
				return;

			if (value instanceof ConfigurationLeafNodeItem) {
				ConfigurationLeafNodeItem node = (ConfigurationLeafNodeItem) value;
				Class<?> contentNode = node.getContentNode();
				if (contentNode == null)
					return;

				try {
					Node newInstance = (Node) contentNode.newInstance();
					borContent.setCenter(newInstance);
				} catch (InstantiationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// if (contentNode == DatabaseConfigView.class) {
				//
				// try {
				// DatabaseConfigView newInstance = (DatabaseConfigView)
				// contentNode.newInstance();
				// borContent.setCenter(newInstance);
				// } catch (Exception e1) {
				// e1.printStackTrace();
				// }
				//
				// } else if (contentNode == DatabaseUrlManagementView.class) {
				//
				// try {
				// DatabaseUrlManagementView newInstance =
				// (DatabaseUrlManagementView) contentNode.newInstance();
				// borContent.setCenter(newInstance);
				// } catch (Exception e1) {
				// e1.printStackTrace();
				// }
				//
				// }

			}
		}
	}

}
