/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2016. 2. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.net.URISyntaxException;
import java.net.URL;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import com.kyj.fx.voeditor.visual.momory.SkinManager;

/**
 * Skin목록을 보여주는 뷰
 * 
 * @author KYJ
 *
 */
public class SkinConfigView extends BorderPane {

	/**
	 * 생성자
	 * 
	 * @throws URISyntaxException
	 */
	public SkinConfigView() {

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

			URL skinPahURL = instance.toURL(selectedItem);
			stylesheets.add(skinPahURL.toExternalForm());
			this.getStylesheets().clear();
			this.getStylesheets().add(skinPahURL.toExternalForm());

			instance.registSkinFullPathn(selectedItem);
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
