/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2016. 6. 8.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.scm;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.tmatesoft.svn.core.SVNLogEntry;

import com.kyj.fx.voeditor.visual.component.TextBaseDiffAppController;
import com.kyj.fx.voeditor.visual.component.text.JavaTextArea;
import com.kyj.fx.voeditor.visual.exceptions.GagoyleException;
import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.util.DateUtil;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
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

	private SVNTreeView element;

	@FXML
	private BorderPane borChart;

	@FXML
	private BorderPane borSource;
	private JavaTextArea javaTextAre;

	public SVNViewer() throws NullPointerException, GagoyleException, IOException {
		FxUtil.loadRoot(SVNViewer.class, this);
	}

	@FXML
	public void initialize() {
		element = new SVNTreeView();
		element.setOnAction(this::svnTreeViewOnAction);
		anTreePane.getChildren().set(0, element);
		AnchorPane.setLeftAnchor(element, 0.0);
		AnchorPane.setRightAnchor(element, 0.0);
		AnchorPane.setBottomAnchor(element, 0.0);
		AnchorPane.setTopAnchor(element, 0.0);

		colRevision.setCellValueFactory(v -> new SimpleObjectProperty<Long>(v.getValue().getRevision()));
		colUser.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getAuthor()));
		colComment.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getMessage()));
		colDate.setCellValueFactory(v -> new SimpleStringProperty(DateUtil.getDateString(v.getValue().getDate().getTime(), "yyyy-MM-dd")));

		javaTextAre = new JavaTextArea();

		borSource.setCenter(javaTextAre);
		LineChart<String, String> lineHist = new LineChart<>(new CategoryAxis(), new CategoryAxis());
		borChart.setCenter(lineHist);

		tbRevision.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		MenuItem menuDiff = new MenuItem("Diff");
		menuDiff.setOnAction(this::menuDiffOnAction);
		tbRevision.setContextMenu(new ContextMenu(menuDiff));
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

			long revision = svnLogEntry.getRevision();
			long revision2 = svnLogEntry2.getRevision();
			SVNItem svnItem = lastSelectedSVNItem.get();

			String cat = svnItem.getManager().cat(svnItem.getPath(), String.valueOf(revision));
			String cat2 = svnItem.getManager().cat(svnItem.getPath(), String.valueOf(revision2));

			try {

				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(TextBaseDiffAppController.class.getResource("TextBaseDiffApp.fxml"));
				Parent parent = loader.load();

				TextBaseDiffAppController controller = loader.getController();
				controller.setDiff(cat, cat2);

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
			List<SVNLogEntry> logs = item.getManager().log(item.getPath());

			tbRevision.getItems().addAll(logs.stream().sorted(sortUpper).collect(Collectors.toList()));

			// 시리즈 생성

			ObservableList<Data<String, String>> observableArrayList = FXCollections.observableArrayList();
			logs.stream().sorted(sortLower).forEach(v -> {
				Date date = v.getDate();
				String dateString = DateUtil.getDateString(date.getTime(), "yyyy/MM/dd");
				observableArrayList.add(new Data<>(dateString, v.getAuthor()));
			});

			Series<String, String> series = new Series<>("Commitors.", observableArrayList);
			lineHist.getData().add(series);

			borChart.setCenter(lineHist);

			String cat = item.getManager().cat(item.getPath());
			String simpleName = item.getSimpleName();
			javaTextAre.setContent(cat);

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
					String cat = svnItem.getManager().cat(path, String.valueOf(revision));
					javaTextAre.setContent(cat);
				}

			}

		}

	}
	/**********************************************************************************************/
	/* 그 밖의 API 기술 */
	// TODO Auto-generated constructor stub
	/**********************************************************************************************/
}
