/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 1. 25.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.math.BigInteger;

import com.kyj.fx.voeditor.visual.component.DiffAppController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class TextCompareExam extends Application {

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 1. 20.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(DiffAppController.class.getResource("TextBaseDiffApp.fxml"));
		BorderPane load = loader.load();
		loader.getController();

		// ReadOnlyConsole center = ReadOnlySingletonConsole.getInstance();
		//
		// BorderPane borderPane = new BorderPane(center);
		// borderPane.getStylesheets().add(SkinManager.getInstance().getSkin());
		// Button button = new Button("테스트");
		// TextField textField = new TextField();
		//
		// button.setOnMouseClicked(event -> {
		// center.appendText(textField.getText());
		// });

		Scene scene = new Scene(load);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
