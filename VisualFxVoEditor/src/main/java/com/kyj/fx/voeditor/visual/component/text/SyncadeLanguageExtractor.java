/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2017. 12. 14.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.GargoyleExtensionFilters;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.fx.voeditor.visual.util.XMLUtils;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;

/**
 * Syncade 다국어 유틸리티
 * 
 * @author KYJ
 *
 */
@FXMLController(value = "SyncadeLanguageExtractorView.fxml", css = "SyncadeLanguageExtractorView.css", isSelfController = true)
public class SyncadeLanguageExtractor extends BorderPane {

	private static final Logger LOGGER = LoggerFactory.getLogger(SyncadeLanguageExtractor.class);

	@FXML
	private RadioMenuItem miKorean, miEnglish, miEncodingEuckr, miEncodingUtf8;
	@FXML
	private MenuItem miEtc;
	@FXML
	private TableView<LangLabel> tvItems;

	@FXML
	private TableColumn<LangLabel, String> tvLang;
	@FXML
	private ToggleGroup lang;

	@FXML
	private TableView<LangVersion> tbLang;
	@FXML
	private TableColumn<LangVersion, String> colLang, colLangCont;

	private ToggleGroup tgEncoding;

	/**
	 * 생성자
	 */
	public SyncadeLanguageExtractor() {

		FxUtil.loadRoot(SyncadeLanguageExtractor.class, this, err -> {
			LOGGER.error(ValueUtil.toString(err));
		});

	}

	private ObjectProperty<File> file = new SimpleObjectProperty<File>();

	@FXML
	public void initialize() {
		tvLang.setCellFactory(TextFieldTableCell.forTableColumn());
		tvLang.setCellValueFactory(call -> {
			LangLabel value = call.getValue();
			LangVersion defaultLangVersion = value.getDefaultLangVersion();
			return defaultLangVersion.value;
		});
		tgEncoding = miEncodingUtf8.getToggleGroup();

		file.addListener(fileChanageListener);
		lang.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if (newValue == null)
					return;
				RadioMenuItem r = (RadioMenuItem) newValue;
				String lang = r.getText();
				File f = file.get();
				if (f != null)
					readAynch(f);
			}
		});

		tvItems.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<LangLabel>() {

			@Override
			public void changed(ObservableValue<? extends LangLabel> observable, LangLabel oldValue, LangLabel newValue) {
				if (newValue == null)
					return;

				tbLang.getItems().clear();
				// vboxLangProp.getChildren().clear();

				ObservableList<LangVersion> versions = newValue.getVersions();
				for (LangVersion version : versions) {
					tbLang.getItems().add(version);
				}
			}
		});

		colLang.setEditable(true);
		colLangCont.setEditable(true);

		colLang.setCellFactory(TextFieldTableCell.forTableColumn());
		colLangCont.setCellFactory(TextFieldTableCell.forTableColumn());

		colLang.setCellValueFactory(param -> param.getValue().languageProperty());
		colLangCont.setCellValueFactory(param -> param.getValue().valueProperty());
	}

	@FXML
	public void miImportOnAction() {
		File f = DialogUtil.showFileDialog(SharedMemory.getPrimaryStage(), stage -> {
			stage.getExtensionFilters().add(GargoyleExtensionFilters.XML_FILTER);
		});
		if (f != null) {
			file.set(f);
		}
	}

	@FXML
	public void miExportOnAction() {

	}

	@FXML
	public void btnAddOnAction() {

		LangVersion e = new LangVersion();

		tbLang.getItems().add(e);
	}

	public static class LangLabel {
		private StringProperty id;

		private ObservableList<LangVersion> versions;

		private ObjectProperty<LangVersion> defaultLangVersion;

		public LangLabel() {
			id = new SimpleStringProperty();
			defaultLangVersion = new SimpleObjectProperty<>();
			versions = FXCollections.observableArrayList();
		}

		public final StringProperty idProperty() {
			return this.id;
		}

		public final String getId() {
			return this.idProperty().get();
		}

		public final void setId(final String id) {
			this.idProperty().set(id);
		}

		public ObservableList<LangVersion> getVersions() {
			return versions;
		}

		public final ObjectProperty<LangVersion> defaultLangVersionProperty() {
			return this.defaultLangVersion;
		}

		public final LangVersion getDefaultLangVersion() {
			return this.defaultLangVersionProperty().get();
		}

		public final void setDefaultLangVersion(final LangVersion defaultLangVersion) {
			this.defaultLangVersionProperty().set(defaultLangVersion);
		}

	}

	public static class LangVersion {
		private StringProperty language;
		private StringProperty value;
		private Node versionNode;
		private LangLabel langLabel;

		public LangVersion() {
			language = new SimpleStringProperty();
			value = new SimpleStringProperty();
		}

		public final StringProperty languageProperty() {
			return this.language;
		}

		public final String getLanguage() {
			return this.languageProperty().get();
		}

		public final void setLanguage(final String language) {
			this.languageProperty().set(language);
		}

		public final StringProperty valueProperty() {
			return this.value;
		}

		public final String getValue() {
			return this.valueProperty().get();
		}

		public final void setValue(final String value) {
			this.valueProperty().set(value);
		}

		@Override
		public String toString() {
			return value.get();
		}

		public void setVersionNode(Node versionNode) {
			this.versionNode = versionNode;
		}

		public Node getVersionNode() {
			return versionNode;
		}

		public void setParent(LangLabel langLabel) {
			this.langLabel = langLabel;
		}

		public LangLabel getParent() {
			return langLabel;
		}

	}

	private ChangeListener<File> fileChanageListener = new ChangeListener<File>() {

		@Override
		public void changed(ObservableValue<? extends File> observable, File oldValue, File newValue) {
			if (newValue != null && newValue.exists()) {
				readAynch(newValue);
			}

		}
	};

	private StringProperty defaultLang = new SimpleStringProperty("English");

	private synchronized String getDefaultLang() {
		return defaultLang.get();
	}

	private synchronized void setDefaultLang(String lang) {
		defaultLang.set(lang);
	}

	private void readAynch(File newValue) {
		FileUtil.asynchRead(newValue, xml -> {

			// String defaultLang = getDefaultLang();
			RadioMenuItem selection = (RadioMenuItem) tgEncoding.getSelectedToggle();
			Charset encoding = Charset.forName(selection.getText());

			Optional<NodeList> applicationNode = XMLUtils.toXpathNodes(xml, "//Application", encoding);
			if (applicationNode.isPresent()) {
				NodeList nodeList = applicationNode.get();
				Node item = nodeList.item(0);
				NamedNodeMap attributes = item.getAttributes();
				if (attributes != null) {

					Node namedItem = attributes.getNamedItem("Default");
					if (namedItem != null) {
						setDefaultLang(namedItem.getTextContent());
					}
				}
			}

			Optional<NodeList> xpathNodes = XMLUtils.toXpathNodes(xml, "/Application/Label", encoding);
			if (xpathNodes.isPresent()) {
				NodeList nodeList = xpathNodes.get();

				ObservableList<LangLabel> items = FXCollections.observableArrayList();
				for (int i = 0, size = nodeList.getLength(); i < size; i++) {

					LangLabel langLabel = new LangLabel();

					Node item = nodeList.item(i);
					NamedNodeMap attributes = item.getAttributes();
					if (attributes == null) {
						// Invalide..
						LOGGER.info("Invalide Label. ");
						continue;
					}

					Node namedItem = attributes.getNamedItem("id");
					String labelId = namedItem.getNodeValue();

					// set Label ID
					langLabel.setId(labelId);

					// version.
					NodeList versionNodes = item.getChildNodes();
					for (int j = 0, max = versionNodes.getLength(); j < max; j++) {

						LangVersion version = new LangVersion();

						version.setParent(langLabel);
						Node versionNode = versionNodes.item(j);
						short nodeType = versionNode.getNodeType();

						if (Node.ELEMENT_NODE != nodeType)
							continue;

						NamedNodeMap versionAttr = versionNode.getAttributes();
						if (versionAttr != null) {
							Node langNode = versionAttr.getNamedItem("language");
							String langName = langNode.getNodeValue();
							// set lang name
							version.setLanguage(langName);

							if (defaultLang.equals(langName)) {

								langLabel.setDefaultLangVersion(version);
							}
						}
						String content = versionNode.getTextContent();
						version.setValue(content);

						version.setVersionNode(versionNode);
						langLabel.getVersions().add(version);
					}

					items.add(langLabel);

				} // end for

				Platform.runLater(() -> {
					tvItems.getItems().clear();
					tvItems.getItems().addAll(items);
				});
			}

		});
	}
}
