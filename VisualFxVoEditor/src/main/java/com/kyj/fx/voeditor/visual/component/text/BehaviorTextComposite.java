/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2017. 12. 8.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kyj.fx.voeditor.visual.component.dock.tab.DockTab;
import com.kyj.fx.voeditor.visual.component.dock.tab.DockTabPane;
import com.kyj.fx.voeditor.visual.framework.GargoyleTabPanable;
import com.kyj.fx.voeditor.visual.framework.thread.ExecutorDemons;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.fx.voeditor.visual.util.XMLUtils;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class BehaviorTextComposite extends BorderPane implements GargoyleTabPanable {

	private ObjectProperty<File> wib = new SimpleObjectProperty<>();
	private XMLEditor xmlEditor;
	private BehaviorTextArea txtScript;
	private SplitPane splitPane;
	private Button btnSearch, btnLoad;
	private TextField txtLoacation;
	private SplitMenuButton smbReferences;
	private TabPane tabPane;
	private Tab mainTab;
	private Stage xmlStage;

	public BehaviorTextComposite() {

		// Top
		smbReferences = new SplitMenuButton();
		this.smbReferences.setText("Reference");

		btnSearch = new Button("Search");
		btnLoad = new Button("Load");
		txtLoacation = new TextField();
		// txtLoacation.setPrefWidth(Double.MAX_VALUE);
		txtLoacation.setMaxWidth(Double.MAX_VALUE);
		txtLoacation.setMinWidth(200d);
		txtLoacation.setPrefWidth(400d);
		smbReferences.setPrefWidth(200d);

		HBox hBox = new HBox(smbReferences, txtLoacation, btnSearch, btnLoad);
		hBox.setPrefWidth(Double.MAX_VALUE);
		hBox.setMaxWidth(Double.MAX_VALUE);
		HBox.getHgrow(txtLoacation);
		hBox.setStyle("-fx-padding : 5px; -fx-spacing : 5px;");

		Menu menuWindow = new Menu("Window");
		MenuItem cmiShowFullText = new MenuItem("Show Full Text");
		cmiShowFullText.setOnAction(ev -> {
			if (this.wib != null) {
				if (xmlStage != null) {
					xmlStage.show();
					return;
				}

				xmlStage = FxUtil.createStageAndShow(xmlEditor, stage -> {
					stage.setTitle("XML - " + this.wib.getName());
					stage.setWidth(800d);
					stage.setHeight(600d);
				});
			}
		});

		menuWindow.getItems().add(cmiShowFullText);
		MenuBar mbMaster = new MenuBar(menuWindow);
		VBox top = new VBox(mbMaster, hBox);

		setTop(top);

		// Center
		xmlEditor = new XMLEditor();

		txtScript = new BehaviorTextArea();

		splitPane = new SplitPane(txtScript);

		splitPane.setOrientation(Orientation.HORIZONTAL);

		tabPane = new TabPane();
		mainTab = new Tab("main", splitPane);
		mainTab.setClosable(false);
		tabPane.getTabs().add(mainTab);
		setCenter(tabPane);

		// Events
		this.btnSearch.setOnAction(this::btnSearchOnAction);
		this.btnLoad.setOnAction(this::btnLoadOnAction);

		// DragDrop
		txtScript.setOnDragOver(this::txtScriptOnDagOver);
		txtScript.setOnDragDropped(this::txtScriptOnDragDropped);

		this.wib.addListener((oba, old, newval) -> {
			if (newval != null && newval.exists()) {
				tab.setText(newval.getName());
			}
		});
		init();
	}

	public BehaviorTextComposite(File wib) {
		this();
		this.wib.set(wib);
		txtLoacation.setText(this.wib == null ? "" : this.wib.get().getAbsolutePath());
	}

	protected void init() {

		readAsync();
	}

	public void smbReferencesOnAction(BehaviorReferenceMenuItem e) {
		BehaviorReferenceVO v = e.getValue();
		addNewTab(v);
	}

	public void txtScriptOnDagOver(DragEvent ev) {
		if (ev.isConsumed())
			return;

		if (ev.getDragboard().hasFiles()) {
			ev.acceptTransferModes(TransferMode.LINK);
			ev.consume();
		}
	}

	public void txtScriptOnDragDropped(DragEvent ev) {

		if (ev.isConsumed())
			return;

		if (ev.getDragboard().hasFiles()) {

			List<File> files = ev.getDragboard().getFiles();

			// tbDatabase.getItems().add(e)
			files.stream().filter(f -> f.getName().endsWith(".wib")).findFirst().ifPresent(f -> {

				this.wib.set(f);
				this.readAsync();
			});

			ev.setDropCompleted(true);
			ev.consume();
		}

	}

	/**
	 * 탭추가
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 8.
	 * @param v
	 */
	protected void addNewTab(BehaviorReferenceVO v) {

		Optional<BehaviorReferenceTab> findAny = tabPane.getTabs().stream() // stream
				.filter(t -> t instanceof BehaviorReferenceTab).map(t -> (BehaviorReferenceTab) t).filter(t -> t.getValue().equals(v))
				.findAny();

		boolean present = findAny.isPresent();
		if (!present) {
			BehaviorReferenceTab newTab = new BehaviorReferenceTab(v);
			tabPane.getTabs().add(newTab);
			tabPane.getSelectionModel().select(newTab);
		} else
			tabPane.getSelectionModel().select(findAny.get());
	}

	public void btnSearchOnAction(ActionEvent e) {
		File wib = DialogUtil.showFileDialog(chooser -> {
			chooser.getExtensionFilters().add(new ExtensionFilter("WIB files (*.wib)", "*.wib"));
		});

		if (wib != null) {
			this.wib.set(wib);
			this.txtLoacation.setText(wib.getAbsolutePath());
		}

		readAsync();
	}

	public void btnLoadOnAction(ActionEvent e) {
		String text = this.txtLoacation.getText();
		if (ValueUtil.isNotEmpty(text)) {
			this.wib.set(new File(text));
			// Ref 초기화
			smbReferences.getItems().clear();
			readAsync();
		}
	}

	private void readAsync() {
		if (this.wib.get() != null && this.wib.get().exists()) {
			ExecutorDemons.getGargoyleSystemExecutorSerivce().execute(() -> {
				String readBehavior = readBehavior();

				// set Full Text
				Platform.runLater(() -> {
					xmlEditor.setText(readBehavior);
				});

				// read script
				Optional<String> xpathText = XMLUtils.toXpathText(readBehavior, "//Script/text()");
				xpathText.ifPresent(str -> {
					Platform.runLater(() -> {
						txtScript.replaceText(str);
						tabPane.getSelectionModel().select(mainTab);
					});
				});

				Optional<NodeList> xpathNodes = XMLUtils.toXpathNodes(readBehavior, "//Reference");
				xpathNodes.ifPresent(nodes -> {

					int length = nodes.getLength();

					for (int i = 0; i < length; i++) {
						Node item = nodes.item(i);
						NamedNodeMap attributes = item.getAttributes();
						if (attributes == null)
							continue;

						Node namedItem = attributes.getNamedItem("FileName");
						String txtFileName = namedItem.getTextContent();

						if (ValueUtil.isNotEmpty(txtFileName)) {

							File refFile = new File(this.wib.get().getParentFile(), txtFileName);
							if (refFile.exists()) {
								BehaviorReferenceMenuItem e = new BehaviorReferenceMenuItem(new BehaviorReferenceVO(refFile, txtFileName));
								e.setOnAction(new BehaviorReferenceMenuItemActionEvent(BehaviorTextComposite.this, e));
								smbReferences.getItems().add(e);
							}
						}
					}
				});

			});
		}
	}

	private String readBehavior() {
		BehaviorReader reader = new BehaviorReader(this.wib.get());
		return reader.readBehavior();
	}

	/**
	 * Component Name <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 8.
	 * @return
	 */
	public static String getName() {
		return "Behavior-Viewer";
	}

	private DockTabPane tabpane;
	private DockTab tab;

	@Override
	public void setTabPane(DockTabPane tabpane) {
		this.tabpane = tabpane;
	}

	@Override
	public void setTab(DockTab tab) {
		this.tab = tab;
	}

}
