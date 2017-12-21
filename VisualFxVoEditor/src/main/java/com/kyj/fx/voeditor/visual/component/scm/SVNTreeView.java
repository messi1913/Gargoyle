/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2016. 4. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.scm;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;

import com.google.common.base.Predicate;
import com.kyj.fx.voeditor.visual.functions.SVNDiscardLocationFunction;
import com.kyj.fx.voeditor.visual.loder.SVNInitLoader;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.NullExpresion;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.scm.manager.core.commons.SCMKeywords;
import com.kyj.scm.manager.svn.java.JavaSVNManager;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Pair;

/**
 * SVN TreeView
 *
 * @author KYJ
 *
 */
public class SVNTreeView extends TreeView<SVNItem> implements SCMKeywords {
	private static Logger LOGGER = LoggerFactory.getLogger(SVNTreeView.class);

	// private Properties prop;

	private ScmTreeMaker scmTreeMaker;

	private SVNInitLoader loader;

	/* Context Menu 정의. */
	private ContextMenu contextMenuDir;

	private Menu menuNew;
	private MenuItem menuAddNewLocation;
	private MenuItem menuSvnGraph;
	private MenuItem menuDiscardLocation;
	private MenuItem menuReflesh;
	private MenuItem menuCheckout;
	private MenuItem menuCopy;
	private MenuItem menuProperties;
	/**
	 * SVN분석 Graph객체를 보관함.
	 *
	 * @최초생성일 2016. 7. 21.
	 */
	private ObjectProperty<TabPane> svnGraphProperty = new SimpleObjectProperty<>();

	/**
	 * repository를 삭제하는 기능을 구현.
	 *
	 * @최초생성일 2016. 4. 4.
	 */
	private SVNDiscardLocationFunction discardFunction;

	public SVNTreeView() {

		scmTreeMaker = new ScmTreeMaker();
		loader = new SVNInitLoader();
		discardFunction = new SVNDiscardLocationFunction();

		addContextMenus();
		this.setOnMouseClicked(this::svnTreeOnMouseClick);
	}

	/**
	 * revision 번호를 필터링하면서 초기 데이터 로드
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 13.
	 * @param revisionHandler
	 */
	public void load(Predicate<Long> revisionHandler) {
		SVNRootItem svnRootItem = new SVNRootItem();
		List<TreeItem<SVNItem>> loadRepository = loadRepository(revisionHandler);
		TreeItem<SVNItem> value = new TreeItem<>(svnRootItem);
		value.getChildren().addAll(loadRepository);
		setRoot(value);
		getRoot().setExpanded(true);
		setShowRoot(false);
	}

	/**
	 * 기본 데이터 로드
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 13.
	 */
	public void load() {
		this.load(null);
	}

	public long getLatestRevision() throws SVNException {
		ObservableList<TreeItem<SVNItem>> children = getRoot().getChildren();
		if (children != null && !children.isEmpty()) {
			TreeItem<SVNItem> treeItem = children.get(0);
			if (treeItem != null && treeItem.getValue() != null)
				return treeItem.getValue().getLatestRevision();
		}
		return -1;
	}

	/***********************************************************************************/
	/* 이벤트 구현 */

	private SVNTreeViewOnAction svnTreeViewOnAction;

	public SVNTreeViewOnAction getOnAction() {
		return svnTreeViewOnAction;
	}

	public void setOnAction(SVNTreeViewOnAction svnTreeViewOnAction) {
		this.svnTreeViewOnAction = svnTreeViewOnAction;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 8.
	 * @param e
	 */
	public void svnTreeOnMouseClick(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
			TreeItem<SVNItem> selectedItem = this.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				SVNItem value = selectedItem.getValue();
				if (svnTreeViewOnAction != null)
					svnTreeViewOnAction.onAction(value);
			}
		}

	}

	/**
	 * 레포지토리를 삭제한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 4.
	 * @param e
	 */
	public void menuDiscardLocationOnAction(ActionEvent e) {

		// final int selectedIndex = getSelectionModel().getSelectedIndex();

		// ObservableList<TreeItem<SVNItem>> children = getRoot().getChildren();

		TreeItem<SVNItem> selectedItem = getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			SVNItem value = selectedItem.getValue();

			getSVNUrl(selectedItem);
			if (value != null && value instanceof SVNRepository) {
				SVNRepository repo = (SVNRepository) value;
				String url = getSVNUrl(repo);

				NullExpresion.ifNotNullDo(url, svnUrl -> {

					Optional<Pair<String, String>> showYesOrNoDialog = DialogUtil.showYesOrNoDialog("Discard Repository",
							String.format("Do you want Discard Repository %s ???", url));

					showYesOrNoDialog.ifPresent(v -> {

						if ("Y".equals(v.getValue())) {
							Boolean apply = discardFunction.apply(repo);
							if (apply == true) {

								boolean remove = getRoot().getChildren().remove(selectedItem);

								if (remove)
									DialogUtil.showMessageDialog("Discard Success!");
								else
									DialogUtil.showMessageDialog("Discard Fail...");
							} else {
								DialogUtil.showMessageDialog("Discard Fail...");
							}
						}

					});

				});

			}
		}

	}

	private static String getSVNUrl(TreeItem<SVNItem> selectedItem) {
		SVNItem value = selectedItem.getValue();
		if (value != null && value instanceof SVNRepository) {
			SVNRepository repo = (SVNRepository) value;
			if (repo != null)
				return getSVNUrl(repo);
		}
		return null;
	}

	private static String getSVNUrl(SVNRepository repo) {
		if (repo != null)
			return repo.getURL();
		return null;
	}

	/**
	 * Checkout..
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 4.
	 * @param e
	 */
	public void menuCheckoutOnAction(ActionEvent e) {

		TreeItem<SVNItem> selectedItem = getSelectionModel().getSelectedItem();
		SVNItem value = selectedItem.getValue();
		if (value instanceof SVNRepository) {
			DialogUtil.showMessageDialog(SharedMemory.getPrimaryStage(), "Root can't checout ");
			return;
		}

		String path = value.getPath();
		String url = value.getManager().getUrl();
		LOGGER.debug(url + path);

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("CheckoutView.fxml"));
			Stage stage = new Stage();
			stage.setScene(new Scene(loader.load()));
			CheckoutController controller = loader.getController();
			controller.setFileName(value.getSimpleName());
			controller.setSVNItem(value);
			stage.initOwner(SharedMemory.getPrimaryStage());
			stage.show();
		} catch (IOException e1) {
			LOGGER.error(ValueUtil.toString(e1));
		}
	}

	/**
	 * SVN Graph
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 21.
	 * @param e
	 * @throws Exception
	 */
	public void menuSVNGraphOnAction(ActionEvent e) {

		final int selectedIndex = getSelectionModel().getSelectedIndex();
		ObservableList<TreeItem<SVNItem>> children = getRoot().getChildren();
		TreeItem<SVNItem> selectedItem = children.get(selectedIndex);
		if (selectedItem != null) {
			SVNItem value = selectedItem.getValue();
			if (value != null && value instanceof SVNRepository) {
				SVNRepository repo = (SVNRepository) value;
				TabPane createSVNGraph = null;
				try {
					createSVNGraph = FxUtil.createSVNGraph(repo.getManager());
				} catch (Exception e1) {
					LOGGER.error(ValueUtil.toString(e1));
				}

				if (createSVNGraph != null) {
					setSvnGraphProperty(createSVNGraph);
				}
			}
		}

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 8.
	 * @param e
	 * @throws IOException
	 */
	public void menuPropertiesOnAction(ActionEvent e) {
		TreeItem<SVNItem> selectedItem = getSelectionModel().getSelectedItem();
		try {
			if (selectedItem != null) {
				SVNItem value = selectedItem.getValue();
				Properties properties = value.getManager().getProperties();

				FXMLLoader fxmlLoader = new FXMLLoader(SVNTreeView.class.getResource("AddNewSVNRepositoryView.fxml"));
				BorderPane n = fxmlLoader.load();

				Stage window = (Stage) getParent().getScene().getWindow();
				Stage parent = (Stage) com.kyj.fx.voeditor.visual.util.ValueUtil.decode(window, window, SharedMemory.getPrimaryStage());

				Stage stage = new Stage();
				AddNewSVNRepositoryController controller = fxmlLoader.getController();
				controller.setStage(stage);
				controller.setProperties(properties);

				stage.setScene(new Scene(n));
				stage.setResizable(false);
				stage.initOwner(parent);
				stage.setTitle("Modify Location.");
				stage.centerOnScreen();
				stage.showAndWait();

				Properties result = controller.getResult();

				if (result != null) {

					SVNItem newSVNItem = new SVNRepository(new JavaSVNManager(result));
					TreeItem<SVNItem> createNode = scmTreeMaker.createNode(newSVNItem, null);
					getRoot().getChildren().remove(selectedItem);
					getRoot().getChildren().add(createNode);
				}

			}
		} catch (Exception ex) {
			LOGGER.error(ValueUtil.toString(ex));
		}

	}

	/**
	 * 새로운 레포지토리를 추가한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 3.
	 * @param e
	 */
	public void menuAddNewLocationOnAction(ActionEvent e) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(SVNTreeView.class.getResource("AddNewSVNRepositoryView.fxml"));
			BorderPane n = fxmlLoader.load();

			Stage window = (Stage) getParent().getScene().getWindow();
			Stage parent = (Stage) com.kyj.fx.voeditor.visual.util.ValueUtil.decode(window, window, SharedMemory.getPrimaryStage());

			Stage stage = new Stage();
			AddNewSVNRepositoryController controller = fxmlLoader.getController();
			controller.setStage(stage);

			stage.setScene(new Scene(n));
			stage.setResizable(false);
			stage.initOwner(parent);
			stage.setTitle("Add New Location.");
			stage.centerOnScreen();
			stage.showAndWait();

			Properties result = controller.getResult();

			if (result != null) {
				SVNItem newSVNItem = new SVNRepository(new JavaSVNManager(result));
				TreeItem<SVNItem> createNode = scmTreeMaker.createNode(newSVNItem, null);
				getRoot().getChildren().add(createNode);
			}

		} catch (IOException e1) {
			LOGGER.error(ValueUtil.toString(e1));
		}
	}

	/**
	 * ContextMenu 보임여부에 따른 이벤트 처리를 구현한다.
	 *
	 * @최초생성일 2016. 4. 4.
	 */
	private EventHandler<WindowEvent> contextMenuVisibleEvent = event -> {
		TreeItem<SVNItem> selectedItem = getSelectionModel().getSelectedItem();
		if (selectedItem != null) {

			menuCopy.setVisible(false);
			menuCheckout.setVisible(false);
			menuDiscardLocation.setVisible(false);
			menuSvnGraph.setVisible(false);

			SVNItem value = selectedItem.getValue();
			if (value instanceof SVNRepository) {
				menuDiscardLocation.setVisible(true);
				menuSvnGraph.setVisible(true);
				menuCheckout.setVisible(true);
			} else if (value instanceof SVNDirItem) {

			} else if (value instanceof SVNFileItem) {
				menuCopy.setVisible(true);
			}

		}
	};

	/***********************************************************************************/

	/***********************************************************************************/
	/* 일반API 구현 */

	/**
	 * 컨텍스트 메뉴 등록
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 4.
	 */
	void addContextMenus() {
		menuNew = new Menu("New");
		menuAddNewLocation = new MenuItem("Repository Location");
		menuDiscardLocation = new MenuItem("Discard Location");
		menuReflesh = new MenuItem("Reflesh");
		menuCheckout = new MenuItem("Checkout");
		menuCopy = new MenuItem("Copy");

		menuSvnGraph = new MenuItem("SVN Graph");
		menuProperties = new MenuItem("Properties");

		menuNew.getItems().add(menuAddNewLocation);

		menuAddNewLocation.setOnAction(this::menuAddNewLocationOnAction);
		menuDiscardLocation.setOnAction(this::menuDiscardLocationOnAction);
		menuCheckout.setOnAction(this::menuCheckoutOnAction);
		menuSvnGraph.setOnAction(this::menuSVNGraphOnAction);
		menuProperties.setOnAction(this::menuPropertiesOnAction);

		contextMenuDir = new ContextMenu(menuNew, new SeparatorMenuItem(), menuCheckout, menuCopy, new SeparatorMenuItem(), menuSvnGraph,
				new SeparatorMenuItem(), menuDiscardLocation, menuReflesh, new SeparatorMenuItem(), menuProperties);

		// contextMenuFile = new ContextMenu(menuProperties);
		// setContextMenu(contextMenu);

		// setOnContextMenuRequested(ev -> {
		//
		// Node intersectedNode = ev.getPickResult().getIntersectedNode();
		// if (intersectedNode instanceof TreeCell) {
		// contextMenuDir.show(FxUtil.getWindow(this));
		// }
		//
		// });
		setCellFactory(new Callback<TreeView<SVNItem>, TreeCell<SVNItem>>() {
			@Override
			public TreeCell<SVNItem> call(TreeView<SVNItem> tv) {
				TextFieldTreeCell<SVNItem> textFieldTreeCell = new TextFieldTreeCell<SVNItem>();
				textFieldTreeCell.setContextMenu(contextMenuDir);
				return textFieldTreeCell;
			}
		});

		// 특정 조건에 따른 메뉴 VISIBLE 처리를 정의함.
		contextMenuDir.setOnShown(contextMenuVisibleEvent);
	}

	/**
	 * 저장된 SCM 저장소를 로드한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 3.
	 * @return
	 */
	public List<TreeItem<SVNItem>> loadRepository() {
		return this.loadRepository(null);
	}

	public List<TreeItem<SVNItem>> loadRepository(Predicate<Long> revisionHandler) {
		List<SVNItem> load = loader.load();
		if (load == null)
			return Collections.emptyList();
		return load.stream().map(v -> scmTreeMaker.createNode(v, revisionHandler)).collect(Collectors.toList());
	}

	/***********************************************************************************/

	public final ObjectProperty<TabPane> svnGraphPropertyProperty() {
		return this.svnGraphProperty;
	}

	public final javafx.scene.control.TabPane getSvnGraphProperty() {
		return this.svnGraphPropertyProperty().get();
	}

	public final void setSvnGraphProperty(final javafx.scene.control.TabPane svnGraphProperty) {
		this.svnGraphPropertyProperty().set(svnGraphProperty);
	}

}
