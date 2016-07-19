/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2016. 7. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.scm;

import java.util.Date;

import org.controlsfx.control.PopOver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

import com.kyj.fx.voeditor.visual.component.text.JavaTextArea;
import com.kyj.scm.manager.svn.java.JavaSVNManager;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

/**
 * JAVA FX와 연계된 API를 제공.
 *
 * @author KYJ
 *
 */
public class FxSVNHistoryDataSupplier extends SimpleSVNHistoryDataSupplier {

	private static Logger LOGGER = LoggerFactory.getLogger(FxSVNHistoryDataSupplier.class);

	public FxSVNHistoryDataSupplier(JavaSVNManager manager, int weekSize, int rankSize) throws SVNException {
		super(manager, weekSize, rankSize);
	}

	public FxSVNHistoryDataSupplier(JavaSVNManager manager) throws SVNException {
		this(manager, 5, 25);
	}

	ListView<GargoyleSVNLogEntryPath> createHistoryListView(ObservableList<GargoyleSVNLogEntryPath> list) {
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

		listView.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {

			if (ev.getClickCount() == 2 && ev.getButton() == MouseButton.PRIMARY) {
				GargoyleSVNLogEntryPath selectedItem = listView.getSelectionModel().getSelectedItem();
				if (selectedItem != null) {
					String path = selectedItem.getPath();
					long copyRevision = selectedItem.getCopyRevision();
					String rootUrl = getRootUrl();
					LOGGER.debug("{}", rootUrl);
					LOGGER.debug("Cat Command, Path : {} Revision {}", path, copyRevision);

					String content = cat(path, String.valueOf(copyRevision));

					PopOver popOver = new PopOver(createJavaTextArea(content));
					popOver.show(ev.getPickResult().getIntersectedNode());
				}
			}
		});

		return listView;
	}

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
							setText(String.format("Resivion :%d author %s date :%s message :%s ", item.getRevision(), author, dateString,
									message));
						}

					}

				};

				return listCell;
			}
		});
		listView.setPrefSize(600, ListView.USE_COMPUTED_SIZE);

//		listView.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
//
//			if (ev.getClickCount() == 2 && ev.getButton() == MouseButton.PRIMARY) {
//				SVNLogEntry selectedItem = listView.getSelectionModel().getSelectedItem();
//				if (selectedItem != null) {
//					String path = selectedItem.getPath();
//					long copyRevision = selectedItem.getCopyRevision();
//					String rootUrl = getRootUrl();
//					LOGGER.debug("{}", rootUrl);
//					LOGGER.debug("Cat Command, Path : {} Revision {}", path, copyRevision);
//
//					String content = cat(path, String.valueOf(copyRevision));
//
//					PopOver popOver = new PopOver(createJavaTextArea(content));
//					popOver.show(ev.getPickResult().getIntersectedNode());
//				}
//			}
//		});

		return listView;
	}

	public JavaTextArea createJavaTextArea(String content) {
		JavaTextArea javaTextArea = new JavaTextArea();
		javaTextArea.setPrefSize(1200, 800);
		javaTextArea.setContent(content);
		return javaTextArea;
	}

}
