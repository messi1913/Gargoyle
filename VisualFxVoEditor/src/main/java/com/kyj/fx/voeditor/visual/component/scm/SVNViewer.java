/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2016. 6. 8.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.scm;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.voeditor.visual.component.TextBaseDiffAppController;
import com.kyj.fx.voeditor.visual.component.text.JavaTextArea;
import com.kyj.fx.voeditor.visual.util.DateUtil;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.scm.manager.svn.java.JavaSVNManager;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * SVN에 대한 각종 메타정보를 조회 처리하기 위한 처리를한다.
 *
 * @author KYJ
 *
 */
@FXMLController(value = "SVNViewerView.fxml", isSelfController = true)
public class SVNViewer extends BorderPane {

	/**
	 * @최초생성일 2016. 7. 21.
	 */
	private static final int TAB_INDEX_SVN_GRAPH = 2;
	@FXML
	private AnchorPane anTreePane;
	@FXML
	private TableView<SVNLogEntry> tbRevision;
	@FXML
	private TableColumn<SVNLogEntry, Long> colRevision;
	@FXML
	private TableColumn<SVNLogEntry, String> colUser;
	@FXML
	private TableColumn<SVNLogEntry, String> colComment;
	@FXML
	private TableColumn<SVNLogEntry, String> colDate;

	private SVNTreeView tvSvnView;

	@FXML
	private BorderPane borChart;
	@FXML
	private Tab tabHistChart;
	/**
	 * TabPane.
	 * @최초생성일 2016. 7. 21.
	 */
	@FXML
	private TabPane tabPaneSVN;

	@FXML
	private BorderPane borSource;
	@FXML
	private Tab tabSource;
	@FXML
	private Label lblSourceTitle, /*SVN 형상의 최근 리비전 정보를 보여줌*/ txtLastRevision;
	private JavaTextArea javaTextAre;
	private LineChart<String, String> lineHist;
	@FXML
	private TextField txtRevisionFilter;

	public SVNViewer() throws Exception {
		FxUtil.loadRoot(SVNViewer.class, this);
	}

	@FXML
	public void initialize() {
		tvSvnView = new SVNTreeView();
		tvSvnView.setOnAction(this::svnTreeViewOnAction);
		tvSvnView.setOnKeyPressed(this::svnTreeVoewOnKeyPressed);
		tvSvnView.load();

		anTreePane.getChildren().set(0, tvSvnView);
		AnchorPane.setLeftAnchor(tvSvnView, 0.0);
		AnchorPane.setRightAnchor(tvSvnView, 0.0);
		AnchorPane.setBottomAnchor(tvSvnView, 0.0);
		AnchorPane.setTopAnchor(tvSvnView, 0.0);

		colRevision.setCellValueFactory(v -> new SimpleObjectProperty<Long>(v.getValue().getRevision()));
		colUser.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getAuthor()));
		colComment.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getMessage()));
		colDate.setCellValueFactory(v -> new SimpleStringProperty(DateUtil.getDateString(v.getValue().getDate().getTime(), "yyyy-MM-dd")));

		javaTextAre = new JavaTextArea();

		borSource.setCenter(javaTextAre);
		lineHist = new LineChart<>(new CategoryAxis(), new CategoryAxis());
		borChart.setCenter(lineHist);

		tbRevision.getSelectionModel().setCellSelectionEnabled(true);
		tbRevision.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		//		FxClipboardUtil.installCopyPasteHandler(tbRevision);
		//		FxTableViewUtil.
		FxUtil.installClipboardKeyEvent(tbRevision);

		MenuItem menuDiff = new MenuItem("Diff");
		menuDiff.setOnAction(this::menuDiffOnAction);
		tbRevision.setContextMenu(new ContextMenu(menuDiff));

		tvSvnView.svnGraphPropertyProperty().addListener((oba, oldView, newView) -> {

			if (newView != null) {
				Tab tabSvnGraph = new Tab("SVN Graph", newView);
				if (tabPaneSVN.getTabs().size() <= TAB_INDEX_SVN_GRAPH) {
					tabPaneSVN.getTabs().add(tabSvnGraph);
				} else {
					tabPaneSVN.getTabs().add(TAB_INDEX_SVN_GRAPH, tabSvnGraph);
				}
			}
		});

		displayLatestRevision();
	}

	@FXML
	public void btnRefleshOnAction() {
		displayLatestRevision();
	}

	/**
	 * SVN형상의 최신 리비전 번호를 UI에 표시한다.
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 13.
	 */
	public void displayLatestRevision() {
		try {
			long latestRevision = tvSvnView.getLatestRevision();
			this.txtLastRevision.setText(String.format("Latest Revision : %d", latestRevision));
		} catch (SVNException e) {
		}
	}

	/**********************************************************************************************/
	/* 이벤트 처리항목 기술 */
	// TODO Auto-generated constructor stub
	/**********************************************************************************************/

	private Comparator<SVNLogEntry> sortUpper = (o1, o2) -> ~o1.getDate().compareTo(o2.getDate());
	private Comparator<SVNLogEntry> sortLower = (o1, o2) -> o1.getDate().compareTo(o2.getDate());

	/********************************
	 * 작성일 : 2016. 6. 14. 작성자 : KYJ
	 *
	 * Diff 처리 이벤트
	 *
	 * @param e
	 ********************************/
	public void menuDiffOnAction(ActionEvent e) {
		ObservableList<SVNLogEntry> selectedItems = tbRevision.getSelectionModel().getSelectedItems();
		if (selectedItems != null && selectedItems.size() == 2) {

			SVNLogEntry svnLogEntry = selectedItems.get(0);
			SVNLogEntry svnLogEntry2 = selectedItems.get(1);
			if (svnLogEntry == null || svnLogEntry2 == null)
				return;

			long revision = svnLogEntry.getRevision();
			long revision2 = svnLogEntry2.getRevision();
			SVNItem svnItem = lastSelectedSVNItem.get();
			
			String rootUrl = svnItem.getManager().getRootUrl();
			
			
			String cat = svnItem.getManager().cat(JavaSVNManager.relativePath(rootUrl, svnItem.getPath(), true), String.valueOf(revision));
			String cat2 = svnItem.getManager().cat(JavaSVNManager.relativePath(rootUrl, svnItem.getPath(), true), String.valueOf(revision2));

			try {

				//				FXMLLoader loader = new FXMLLoader();
				//				loader.setLocation(TextBaseDiffAppController.class.getResource("TextBaseDiffApp.fxml"));
				//				Parent parent = loader.load();
				//				TextBaseDiffAppController controller = loader.getController();
				//				controller.setDiff(cat, cat2);
				Parent parent = FxUtil.loadAndControllerAction(TextBaseDiffAppController.class, controller -> {
					try {
						controller.setDiff(cat, cat2);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				});

				Stage stage = new Stage();
				stage.setScene(new Scene(parent));
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.initOwner(this.getScene().getWindow());
				stage.show();

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			DialogUtil.showMessageDialog("두개의 아이템을 선택하십시요.");

		}

	}

	/**
	 * 마지막에 트리에서 선택된 SVNItem이 임시저장된다.
	 *
	 * @최초생성일 2016. 6. 13.
	 */
	private ObjectProperty<SVNItem> lastSelectedSVNItem = new SimpleObjectProperty<>();

	public void svnTreeViewOnAction(SVNItem item) {

		if (!item.isDir()) {
			lastSelectedSVNItem.set(item);
			tbRevision.getItems().clear();
			LineChart<String, String> lineHist = new LineChart<>(new CategoryAxis(), new CategoryAxis());
			//			lineHist.setRotate(90d);
			//			lineHist.scaleXProperty().set(0.7);
			//			lineHist.scaleYProperty().set(0.7);
			lineHist.autosize();
			lineHist.setLegendVisible(false);
			
			
			String relativePath = JavaSVNManager.relativePath(item.getManager().getRootUrl(), item.getPath(), true);
			
			List<SVNLogEntry> logs = item.getManager().log(relativePath);
//			List<SVNLogEntry> logs = item.getManager().log(item.getPath());

			tbRevision.getItems().addAll(logs.stream().sorted(sortUpper).collect(Collectors.toList()));

			// 시리즈 생성

			ObservableList<Data<String, String>> observableArrayList = FXCollections.observableArrayList();
			logs.stream().sorted(sortLower).forEach(entry -> {
				Date date = entry.getDate();
				String dateString = DateUtil.getDateString(date.getTime(), "yy-MM-dd HH:mm");
				Data<String, String> data = new Data<>(dateString, entry.getAuthor());

				setDataNode(entry, data);

				data.getNode().setOnMouseClicked(e -> {

					if (e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY) {
						String path = item.path;
						long revision = entry.getRevision();

						String content = item.getManager().cat(path, String.valueOf(revision));

						BorderPane pane = new BorderPane(FxUtil.createJavaTextArea(content));
						pane.setTop(new Label(item.getManager().fromPrettySVNLogConverter().apply(entry)));
						FxUtil.showPopOver(data.getNode(), pane);
					}
				});

				data.getNode().setOnMouseEntered(ev -> {
					data.getNode().setBlendMode(BlendMode.GREEN);
				});

				data.getNode().setOnMouseExited(ev -> {
					data.getNode().setBlendMode(null);
				});

				//				e.setNode(new Label(String.valueOf(v.getRevision())));

				observableArrayList.add(data);
			});

			Series<String, String> series = new Series<>("Commitors.", observableArrayList);
			lineHist.getData().add(series);

			borChart.setCenter(lineHist);

			String cat = item.getManager().cat(relativePath);
			//			String simpleName = item.getSimpleName();
			javaTextAre.setContent(cat);

			tabPaneSVN.getSelectionModel().select(tabHistChart);
		}

	}

	private void setDataNode(SVNLogEntry entry, Data<String, String> data) {

		Group group = new Group();
		group.setManaged(false);
		Text value = new Text(entry.getRevision() + "");
		value.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
		value.translateYProperty().set(-15);
		Circle circle = new Circle(4, Color.WHITE);
		circle.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
		circle.setStroke(Color.web("#f3622d"));
		StackPane stackPane = new StackPane(value, circle);
		stackPane.setPrefSize(30, 60);
		group.getChildren().add(stackPane);
		data.setNode(group);

	}

	public void svnTreeVoewOnKeyPressed(KeyEvent e) {
		if (e.getCode() == KeyCode.DELETE) {

			TreeItem<SVNItem> selectedItem = tvSvnView.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				tvSvnView.menuDiscardLocationOnAction(new ActionEvent());
			}
		}
	}

	/**
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 13.
	 * @param e
	 */
	@FXML
	public void tbRevisionOnMouseClick(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY) {

			SVNItem svnItem = lastSelectedSVNItem.get();
			if (svnItem != null) {

				SVNLogEntry selectedItem = tbRevision.getSelectionModel().getSelectedItem();
				if (selectedItem != null) {

					long revision = selectedItem.getRevision();
					String path = svnItem.getPath();
					path = JavaSVNManager.relativePath(svnItem.getManager().getRootUrl(), path, true);
					String cat = svnItem.getManager().cat(path, String.valueOf(revision));
					javaTextAre.setContent(cat);
					tabPaneSVN.getSelectionModel().select(tabHistChart);
				}

			}

		}

	}

	@FXML
	public void reloadOnAction() {
		String revisionFilter = txtRevisionFilter.getText();
		if (ValueUtil.isNotEmpty(revisionFilter)) {
			try {
				int revision = Integer.parseInt(revisionFilter);
				tvSvnView.getRoot().getChildren().clear();
				tvSvnView.load(r -> r >= revision);
			} catch (NumberFormatException e) {
			}
		} else {
			tvSvnView.load();
		}
	}
	/**********************************************************************************************/
	/* 그 밖의 API 기술 */
	// TODO Auto-generated constructor stub
	/**********************************************************************************************/
}
