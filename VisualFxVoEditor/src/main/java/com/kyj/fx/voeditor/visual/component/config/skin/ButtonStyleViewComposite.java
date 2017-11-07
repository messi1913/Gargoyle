/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.config.skin
 *	작성일   : 2016. 12. 5.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.config.skin;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 * unused.
 */
@FXMLController(value = "ButtonStyleView.fxml", isSelfController = true)
@Deprecated
public class ButtonStyleViewComposite extends BorderPane {

	/**
	 * @최초생성일 2016. 12. 5.
	 */
	private static final String PATH_TEMPLATE_CSS_BUTTON = "template/css/button";

	private static final Logger LOGGER = LoggerFactory.getLogger(ButtonStyleViewComposite.class);

	@FXML
	private TableView<File> tbSkins;
	@FXML
	private TableColumn<File, String> colSkinName;
	@FXML
	private TableColumn<File, String> colPreview;
	@FXML
	private BorderPane borPreview;
	@FXML
	private TextArea txtStyle;

	public ButtonStyleViewComposite() {
		FxUtil.loadRoot(ButtonStyleViewComposite.class, this, e -> LOGGER.error(ValueUtil.toString(e)));
	}

	@FXML
	public void initialize() {
		loadButtonStyles();
		colSkinName.setCellValueFactory(param -> {
			return new SimpleStringProperty(param.getValue().getName());
		});

		tbSkins.getSelectionModel().selectedItemProperty().addListener((oba, o, n) -> {
			File selectedItem = n;
			if (selectedItem != null && selectedItem.exists()) {

				String readFile = FileUtil.readFile(selectedItem, true, null);
				txtStyle.setText(readFile);

				try {
					List<Node> findAllByNodes = FxUtil.findAllByNodes(borPreview, node -> node instanceof Button);
					String className = String.format("%s", selectedItem.getName().replaceAll(".css", ""));
					LOGGER.debug("{}", className);
					findAllByNodes.forEach(btn -> {
						btn.getStyleClass().add("button");
						btn.getStyleClass().add(className);
					});

					borPreview.getStylesheets().clear();
					borPreview.getStylesheets().add(selectedItem.toURI().toURL().toExternalForm());
					borPreview.applyCss();
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				txtStyle.setText("");
			}

		});

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 5.
	 */
	private void loadButtonStyles() {
		File file = new File(PATH_TEMPLATE_CSS_BUTTON);
		if (file.exists()) {
			File[] listFiles = file.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".css");
				}
			});

			tbSkins.getItems().addAll(listFiles);
		}
	}

}
