/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2016. 7. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.scm;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.kyj.fx.voeditor.visual.component.text.JavaTextArea;
import com.kyj.fx.voeditor.visual.util.FxCollectors;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.scm.manager.svn.java.JavaSVNManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * JAVA FX와 연계된 API를 제공.
 *
 * SVN에서 가져온 데이터에 대한 공유변수 역할 처리함.
 *
 * 스레드에 안정적이지 않으므로 싱글톤 사용은 금지.
 *
 * @author KYJ
 *
 */
public class FxSVNHistoryDataSupplier extends SimpleSVNHistoryDataSupplier {

	private static Logger LOGGER = LoggerFactory.getLogger(FxSVNHistoryDataSupplier.class);

	public FxSVNHistoryDataSupplier(JavaSVNManager manager, int weekSize, int rankSize) throws Exception {
		super(manager, weekSize, rankSize);
	}

	public FxSVNHistoryDataSupplier(JavaSVNManager manager) throws Exception {
		this(manager, 3, 25);
	}

	public ListView<GargoyleSVNLogEntryPath> createHistoryListView(String relativePath) {

		List<GargoyleSVNLogEntryPath> list = getCollectedTable().get(relativePath);
		ListView<GargoyleSVNLogEntryPath> createHistoryListView = createHistoryListView(FXCollections.observableArrayList(list));
		createHistoryListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		return createHistoryListView;
	}

	public ListView<GargoyleSVNLogEntryPath> createHistoryListView(ObservableList<GargoyleSVNLogEntryPath> list) {
		ListView<GargoyleSVNLogEntryPath> listView = new ListView<GargoyleSVNLogEntryPath>(list);
		listView.setCellFactory(new Callback<ListView<GargoyleSVNLogEntryPath>, ListCell<GargoyleSVNLogEntryPath>>() {

			@Override
			public ListCell<GargoyleSVNLogEntryPath> call(ListView<GargoyleSVNLogEntryPath> param) {

				ListCell<GargoyleSVNLogEntryPath> listCell = new ListCell<GargoyleSVNLogEntryPath>() {

					/* (non-Javadoc)
					 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
					 */
					@Override
					protected void updateItem(GargoyleSVNLogEntryPath item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setText(null);
						} else {
							String type = "Modify";
							switch (item.getType()) {
							case GargoyleSVNLogEntryPath.TYPE_ADDED:
								type = "Add";
								break;
							case GargoyleSVNLogEntryPath.TYPE_MODIFIED:
								type = "Modify";
								break;
							case GargoyleSVNLogEntryPath.TYPE_REPLACED:
								type = "Replace";
								break;
							case GargoyleSVNLogEntryPath.TYPE_DELETED:
								type = "Delete";
								break;
							}
							String path = item.getPath();
							setText(String.format("Resivion :%d File : %s Date : %s Type : %s ", item.getCopyRevision(),
									path.substring(path.lastIndexOf("/")), YYYY_MM_DD_HH_MM_SS_PATTERN.format(item.getDate()), type));
						}

					}

				};

				return listCell;
			}
		});
		listView.setPrefSize(600, ListView.USE_COMPUTED_SIZE);
		listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		listView.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {

			ContextMenu contextMenu = null;

			ObservableList<GargoyleSVNLogEntryPath> selectedItems = listView.getSelectionModel().getSelectedItems();
			GargoyleSVNLogEntryPath selectedItem = null;
			if (selectedItems != null && !selectedItems.isEmpty()) {
				selectedItem = selectedItems.get(0);
			}

			if (ev.getClickCount() == 2 && ev.getButton() == MouseButton.PRIMARY) {

				if (selectedItem != null) {
					String path = selectedItem.getPath();

					long copyRevision = selectedItem.getCopyRevision();
					String rootUrl = getRootUrl();
					LOGGER.debug("{}", rootUrl);
					LOGGER.debug("Cat Command, Path : {} Revision {}", path, copyRevision);

					try {
						String content = "";
						VBox vBox = new VBox(5);
						if (isExists(path)) {
							content = cat(path, String.valueOf(copyRevision));

							List<SVNLogEntry> log = log(path, String.valueOf(copyRevision), ex -> {
								LOGGER.error(ValueUtil.toString(ex));
							});

							if (ValueUtil.isNotEmpty(log)) {
								SVNLogEntry svnLogEntry = log.get(0);
								String apply = getManager().fromPrettySVNLogConverter().apply(svnLogEntry);
								Label e = new Label(apply);
								e.setStyle("-fx-text-fill:black");
								vBox.getChildren().add(e);
							}
							vBox.getChildren().add(createJavaTextArea(content));

						} else {
							content = "Does not exists. Repository. [Removed]";
							Label e = new Label(content);
							e.setStyle("-fx-text-fill:black");
							vBox.getChildren().add(e);
						}

						FxUtil.showPopOver(ev.getPickResult().getIntersectedNode(), vBox);
					} catch (SVNException e) {
						LOGGER.error(ValueUtil.toString(e));
					}

				}
				ev.consume();

			} else if (ev.getClickCount() == 1 && ev.getButton() == MouseButton.SECONDARY) {

				ObservableList<MenuItem> menus = FXCollections.observableArrayList();

				MenuItem miHist = new MenuItem("History");
				miHist.setUserData(selectedItem);
				MenuItem miDiff = new MenuItem("Diff");
				miDiff.setDisable(true);
				menus.add(miHist);
				menus.add(miDiff);

				if (selectedItems.size() == 2) {
					miDiff.setUserData(selectedItems);
					miDiff.setDisable(false);
				}

				miHist.setOnAction(event -> {
					GargoyleSVNLogEntryPath userData = (GargoyleSVNLogEntryPath) miHist.getUserData();
					if (userData == null)
						return;

					FxUtil.showPopOver(ev.getPickResult().getIntersectedNode(), createHistoryListView(userData.getPath()));
				});

				miDiff.setOnAction(event -> {

					List<GargoyleSVNLogEntryPath> userData = (List<GargoyleSVNLogEntryPath>) miDiff.getUserData();
					if (userData == null && userData.size() != 2)
						return;

					GargoyleSVNLogEntryPath gargoyleSVNLogEntryPath1 = userData.get(0);
					GargoyleSVNLogEntryPath gargoyleSVNLogEntryPath2 = userData.get(1);

					try {
						String diff = diff(gargoyleSVNLogEntryPath1.getPath(), gargoyleSVNLogEntryPath1.getCopyRevision(),
								gargoyleSVNLogEntryPath2.getCopyRevision());

						TextArea showingNode = new TextArea(diff);
						showingNode.setPrefSize(600d, 800d);

						FxUtil.showPopOver(ev.getPickResult().getIntersectedNode(), showingNode);
					} catch (Exception e) {
						e.printStackTrace();
					}

				});

				contextMenu = new ContextMenu(menus.stream().toArray(MenuItem[]::new));

				contextMenu.show(ev.getPickResult().getIntersectedNode(), ev.getScreenX(), ev.getScreenY());
			}

		});

		return listView;
	}

//	MenuItem createDiffMenu(){
//
//	}

	ListView<SVNLogEntry> createEntryListView(ObservableList<SVNLogEntry> list) {
		ListView<SVNLogEntry> listView = new ListView<SVNLogEntry>(list);
		listView.setCellFactory(new Callback<ListView<SVNLogEntry>, ListCell<SVNLogEntry>>() {

			@Override
			public ListCell<SVNLogEntry> call(ListView<SVNLogEntry> param) {

				ListCell<SVNLogEntry> listCell = new ListCell<SVNLogEntry>() {

					/* (non-Javadoc)
					 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
					 */
					@Override
					protected void updateItem(SVNLogEntry item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setText(null);
						} else {
							long revision = item.getRevision();
							String author = item.getAuthor();
							String dateString = YYYY_MM_DD_HH_MM_SS_PATTERN.format(item.getDate());
							String message = item.getMessage();
							setText(String.format("Resivion :%d author %s date :%s message :%s ", revision, author, dateString, message));
						}

					}

				};

				return listCell;
			}
		});
		listView.setPrefSize(600, ListView.USE_COMPUTED_SIZE);

		listView.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {

			if (ev.getClickCount() == 2 && ev.getButton() == MouseButton.PRIMARY) {
				SVNLogEntry selectedItem = listView.getSelectionModel().getSelectedItem();
				if (selectedItem != null) {

					Map<String, SVNLogEntryPath> changedPaths = selectedItem.getChangedPaths();
					if (ValueUtil.isNotEmpty(changedPaths)) {

						ObservableList<GargoyleSVNLogEntryPath> collect = createStream(Arrays.asList(selectedItem))
								.collect(FxCollectors.toObservableList());

						Node showing = null;
						if (collect.isEmpty()) {
							showing = new Label("Empty.");
							showing.setStyle("-fx-text-fill:black");
						} else {
							showing = createHistoryListView(collect);
						}

						FxUtil.showPopOver(ev.getPickResult().getIntersectedNode(), showing);
					}

				}
			}
		});

		return listView;
	}

	public JavaTextArea createJavaTextArea(String content) {

//		return new TextArea(content);
		return FxUtil.createJavaTextArea(content);


	}

	public String diff(String path, long revision1, long revision2) throws Exception {
		return getManager().diff(path, SVNRevision.create(revision1), path, SVNRevision.create(revision2));
	}

}
