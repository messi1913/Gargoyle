/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2016. 2. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.net.URISyntaxException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.voeditor.visual.momory.SkinManager;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Skin목록을 보여주는 뷰
 *
 * @author KYJ
 *
 */
@FXMLController(value = "SkinConfigView.fxml", isSelfController = true)
public class SkinConfigView extends BorderPane {

	private static final Logger LOGGER = LoggerFactory.getLogger(SkinConfigView.class);

	/**
	 * 생성자
	 *
	 * @throws URISyntaxException
	 */
	public SkinConfigView() {
		FxUtil.loadRoot(SkinConfigView.class, this, err -> LOGGER.error(ValueUtil.toString(err)));
	}

	@FXML
	public void initialize() {
		ListView<String> lvItems = getSkinView();
		lvItems.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
				String selectedItem = lvItems.getSelectionModel().getSelectedItem();
				if (selectedItem != null && !selectedItem.isEmpty()) {
					applyStyling(selectedItem);
				}
			}
		});
		setCenter(lvItems);

		Button button = new Button("적용");
		button.getStyleClass().add(SkinManager.BUTTON_STYLE_CLASS_NAME);
		button.setPrefWidth(150);
		button.setOnMouseClicked(event -> {

			String selectedItem = lvItems.getSelectionModel().getSelectedItem();
			if (selectedItem != null && !selectedItem.isEmpty()) {
				applyStyling(selectedItem);
			}

		});
		HBox btnBox = new HBox(button);
		btnBox.setPadding(new Insets(5, 5, 5, 5));
		btnBox.setAlignment(Pos.CENTER);
		setBottom(btnBox);
	}

	private void applyStyling(String selectedItem) {
		SkinManager instance = SkinManager.getInstance();
		if (instance.existSkinFullPath(selectedItem)) {
			Stage stage = (Stage) this.getScene().getWindow();
			ObservableList<String> stylesheets = stage.getScene().getStylesheets();
			stylesheets.clear();

			instance.registSkinFullPathn(selectedItem);
			instance.resetSkin();
		}
	}

	/**
	 * 스킨뷰리턴
	 *
	 * @return
	 */
	private ListView<String> getSkinView() {
		ListView<String> value = new ListView<>(getSkinList());
		return value;
	}

	/**
	 * 스킨데이터리턴
	 *
	 * @return
	 */
	private ObservableList<String> getSkinList() {
		return FXCollections.observableArrayList(SkinManager.getInstance().getSkinList());
	}

}
