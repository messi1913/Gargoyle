/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2016. 7. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.scm;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNLogEntry;

import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "ScmChagnedCodeView.fxml", isSelfController = true)
public class SvnChagnedCodeComposite extends BorderPane {

	private static Logger LOGGER = LoggerFactory.getLogger(SvnChagnedCodeComposite.class);

	private FxSVNHistoryDataSupplier supplier;
	@FXML
	private PieChart piChartChagendCode;

	private List<Data> dataList;

	private ContextMenu contextMenu;

	public SvnChagnedCodeComposite(FxSVNHistoryDataSupplier supplier)
			throws Exception {
		this.supplier = supplier;
		FxUtil.loadRoot(SvnChagnedCodeComposite.class, this);

	}

	@FXML
	public void initialize() {
		createContextMenu();

		Collection<SVNLogEntry> allLogs = supplier.getAllLogs();
		LOGGER.debug("Log Count : {}", allLogs.size());
		String dateString = supplier.getStart().toDateString();
		String dateString2 = supplier.getEnd().toDateString();

		piChartChagendCode
				.setTitle(String.format("Chaned Code Count ( Rank %d )\n%s ~ %s", supplier.getRankSize(), dateString, dateString2));

		//		collectedTable = supplier.createStream(allLogs).collect(Collectors.groupingBy(v -> v.getPath()));
		Map<String, Long> collect = supplier.createStream(allLogs)
				.collect(Collectors.groupingBy(v -> v.getPath(), LinkedHashMap::new, Collectors.counting()));

		dataList = collect.entrySet().stream().sorted((o1, o2) -> {
			return -Long.compare(o1.getValue(), o2.getValue());
		}).map(v -> new Data(v.getKey(), v.getValue())).limit(supplier.getRankSize()).collect(Collectors.toList());

		piChartChagendCode.getData().addAll(dataList);

		Set<Node> lookupAll = piChartChagendCode.lookupAll(".chart-pie-label");

		for (Data d : piChartChagendCode.getData()) {
			Node node = d.getNode();
			Tooltip.install(node, new Tooltip(String.format("Source Code : %s\nModify Count:%d", d.getName(), (int) d.getPieValue())));

			node.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
				dataOnMouseClick(e, d);
				e.consume();
			});

			/*animation effect. */
			node.addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, ev -> {

				Platform.runLater(() -> node.setStyle("-fx-background-color:derive(-fx-color,-5%);"));

			});
			/*animation effect. */
			node.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, ev -> {

				Platform.runLater(() -> node.setStyle(null));

			});

		}

		lookupAll.stream().map(v -> (Text) v).forEach(v -> {

			String text = v.getText();
			String displayText = text;
			int count = supplier.getCollectedTable().get(displayText).size();
			int textLength = displayText.length();

			if (textLength > 15) {
				displayText = "... "+ displayText.substring(textLength-15);

				Tooltip.install(v, new Tooltip(text));

//				text = text.substring(lastIndexOf, textLength);
			}

			displayText = displayText.concat("   [").concat(String.valueOf(count)).concat("]");
			v.setText(displayText);
		});

//		Platform.runLater(() -> {
//			this.getScene().addEventFilter(KeyEvent.KEY_PRESSED, this::sceneOnKeyPressed);
//		});

	}

	/**
	 * 차트 위치를 복구하는 기능을 처리.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 20.
	 * @param e
	 */
//	public void sceneOnKeyPressed(KeyEvent e) {
//		if (e.getCode() == KeyCode.ESCAPE) {
//
//			setRight(null);
//			e.consume();
//		}
//	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 20.
	 */
	private void createContextMenu() {

		MenuItem miViewHistory = new MenuItem("View History");
		miViewHistory.setOnAction(ev -> {

			//			Object source = ev.getSource();
			//			System.out.println(source);

			Node ownerNode = contextMenu.getOwnerNode();
			System.out.println(ownerNode);
			Node center = getCenter();
			if ((ownerNode != null && ownerNode instanceof Region) && (center != null && center == piChartChagendCode)) {

				Data userData = (Data) contextMenu.getUserData();

				//				Node[] nodes = {};
				//				GridPane gridPane = new GridPane();
				//
				if (userData != null) {
					String relativePath = userData.getName();

					ListView<GargoyleSVNLogEntryPath> createHistoryListView = supplier.createHistoryListView(relativePath);
//					createHistoryListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

					FxUtil.showPopOver(ownerNode, createHistoryListView);
				}

			}

		});

		contextMenu = new ContextMenu(miViewHistory);

	}

	public void dataOnMouseClick(MouseEvent e, Data d) {

		if (e.getClickCount() == 1 && e.getButton() == MouseButton.PRIMARY) {
			contextMenu.hide();
			//			Long long1 = collectedTable.get(d.getName());

			ObservableList<GargoyleSVNLogEntryPath> list = FXCollections.observableArrayList(supplier.getCollectedTable().get(d.getName()));
			if (!list.isEmpty()) {
				GargoyleSVNLogEntryPath gargoyleSVNLogEntryPath = list.get(0);
				BorderPane borderPane = new BorderPane();
				borderPane.setTop(new Label(gargoyleSVNLogEntryPath.getPath()));
				ListView<GargoyleSVNLogEntryPath> createHistoryListView = supplier.createHistoryListView(list);
				borderPane.setCenter(createHistoryListView);
				borderPane.setBottom(new Label(String.valueOf(list.size()) + " ea"));
				FxUtil.showPopOver(d.getNode(), borderPane);
			}

		} else if (e.getClickCount() == 1 && e.getButton() == MouseButton.SECONDARY) {
			Node owner = d.getNode();
			if (!contextMenu.isShowing()) {
				contextMenu.setUserData(d);
				contextMenu.show(owner, e.getScreenX(), e.getScreenY());
			}
		}
	}

}
