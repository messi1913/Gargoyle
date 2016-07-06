/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 1. 25.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.io.File;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import com.kyj.fx.voeditor.visual.component.FileBaseDiffAppController;
import com.kyj.fx.voeditor.visual.component.DiffAppController;

/**
 * @author KYJ
 *
 */
public class FileCompareExam extends Application {

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
		loader.setLocation(DiffAppController.class.getResource("FileBaseDiffApp.fxml"));
		BorderPane load = loader.load();
		FileBaseDiffAppController controller = loader.getController();

		URL resource1 = FileCompareExam.class.getResource("Test1");
		URL resource2 = FileCompareExam.class.getResource("Test2");

		File ordinalFile = new File(resource1.toURI());
		controller.setDiffFile(ordinalFile, new File(resource2.toURI()));
		Scene scene = new Scene(load);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
