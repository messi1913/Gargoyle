/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2017. 12. 8.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import java.io.File;
import java.util.Optional;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kyj.fx.voeditor.visual.framework.thread.ExecutorDemons;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.fx.voeditor.visual.util.XMLUtils;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * @author KYJ
 *
 */
public class BehaviorTextComposite extends BorderPane {

	private File wib;
	private XMLEditor xmlEditor;
	private BehaviorTextArea txtScript;
	private SplitPane splitPane;
	private Button btnSearch, btnLoad;
	private TextField txtLoacation;
	private SplitMenuButton smbReferences;
	private TabPane tabPane;
	private Tab mainTab;

	public BehaviorTextComposite() {

		// Top
		smbReferences = new SplitMenuButton();
		btnSearch = new Button("Search");
		btnLoad = new Button("Load");
		txtLoacation = new TextField(this.wib == null ? "" : this.wib.getAbsolutePath());
		// txtLoacation.setPrefWidth(Double.MAX_VALUE);
		txtLoacation.setMaxWidth(Double.MAX_VALUE);
		txtLoacation.setMinWidth(200d);
		smbReferences.setPrefWidth(200d);

		HBox hBox = new HBox(smbReferences, txtLoacation, btnSearch, btnLoad);
		hBox.setPrefWidth(Double.MAX_VALUE);
		hBox.setMaxWidth(Double.MAX_VALUE);
		HBox.getHgrow(txtLoacation);
		hBox.setStyle("-fx-padding : 5px; -fx-spacing : 5px;");

		setTop(hBox);

		// Center
		xmlEditor = new XMLEditor();
		txtScript = new BehaviorTextArea();
		
//		SplitPane sub = new SplitPane(xmlEditor, customValuesXml);
		
		splitPane = new SplitPane(txtScript, xmlEditor);
		
		splitPane.setDividerPositions(0.7, 0.3);
		splitPane.setOrientation(Orientation.HORIZONTAL);

		tabPane = new TabPane();
		mainTab = new Tab("main", splitPane);
		mainTab.setClosable(false);
		tabPane.getTabs().add(mainTab);
		setCenter(tabPane);

		init();
	}

	public BehaviorTextComposite(File wib) {
		this();
	}

	protected void init() {
		this.btnSearch.setOnAction(this::btnSearchOnAction);
		this.btnLoad.setOnAction(this::btnLoadOnAction);
		smbReferences.setText("Reference");
		readAsync();
	}

	public void smbReferencesOnAction(BehaviorReferenceMenuItem e) {
		BehaviorReferenceVO v = e.getValue();
		addNewTab(v);
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
		this.wib = DialogUtil.showFileDialog(chooser -> {
			chooser.getExtensionFilters().add(new ExtensionFilter("WIB files (*.wib)", "*.wib"));
		});
		
		if (this.wib != null)
			this.txtLoacation.setText(this.wib.getAbsolutePath());

		readAsync();
	}

	public void btnLoadOnAction(ActionEvent e) {
		String text = this.txtLoacation.getText();
		if (ValueUtil.isNotEmpty(text)) {
			this.wib = new File(text);
			// Ref 초기화
			smbReferences.getItems().clear();
			readAsync();
		}
	}

	private void readAsync() {
		if (this.wib != null && this.wib.exists()) {
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

							File refFile = new File(this.wib.getParentFile(), txtFileName);
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
		BehaviorReader reader = new BehaviorReader(this.wib);
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

}
