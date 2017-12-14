/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2017. 12. 14.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import java.io.File;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
	private RadioMenuItem miKorean, miEnglish;
	@FXML
	private MenuItem miEtc;
	@FXML
	private TableView<LangLabel> tvItems;

	@FXML
	private TableColumn<LangLabel, String> tvLang;

	/**
	 * 생성자
	 */
	public SyncadeLanguageExtractor() {

		FxUtil.loadRoot(SyncadeLanguageExtractor.class, this, err -> {
			LOGGER.error(ValueUtil.toString(err));
		});

	}

	@FXML
	public void initialize() {
		tvLang.setCellFactory(TextFieldTableCell.forTableColumn());
		tvLang.setCellValueFactory(call -> {
			LangLabel value = call.getValue();
			LangVersion defaultLangVersion = value.getDefaultLangVersion();
			return defaultLangVersion.value;
		});
	}

	@FXML
	public void miImportOnAction() {
		File f = DialogUtil.showFileDialog(SharedMemory.getPrimaryStage(), stage -> {
			stage.getExtensionFilters().add(GargoyleExtensionFilters.XML_FILTER);
		});
		if (f != null) {
			FileUtil.asynchRead(f, xml -> {

				String defaultLang = "English";
				Optional<NodeList> applicationNode = XMLUtils.toXpathNodes(xml, "/Application");
				if (applicationNode.isPresent()) {
					NodeList nodeList = applicationNode.get();
					Node item = nodeList.item(0);
					NamedNodeMap attributes = item.getAttributes();
					if (attributes != null) {

						Node namedItem = attributes.getNamedItem("Default");
						if (namedItem != null) {
							defaultLang = namedItem.getTextContent();
						}
					}
				}

				Optional<NodeList> xpathNodes = XMLUtils.toXpathNodes(xml, "/Application/Label");
				if (xpathNodes.isPresent()) {
					NodeList nodeList = xpathNodes.get();

					ObservableList<Object> items = FXCollections.observableArrayList();
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

							Node versionNode = versionNodes.item(j);
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

							langLabel.getVersions().add(version);
						}

						items.add(langLabel);

					} // end for

					Platform.runLater(() -> {
						tvItems.getItems().addAll(items);
					});
				}

			});
		}
	}

	@FXML
	public void miExportOnAction() {

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

	}
}
