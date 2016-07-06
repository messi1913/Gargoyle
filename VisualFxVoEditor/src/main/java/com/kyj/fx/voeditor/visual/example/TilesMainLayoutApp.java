/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   :  com.kyj.fx.voeditor.visual.example
 *	작성일   :  2016. 2. 23.
 *	작성자   :  KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.momory.SkinManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class TilesMainLayoutApp extends Application
{

	/********************************
	 * 패키지 : 작성일 : 2016. 2. 23. 작성자 : KYJ
	 *
	 * @param args
	 *******************************/
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(TilesMainLayoutApp.class.getResource("TilesMainLayoutApp.fxml"));
		BorderPane root = loader.load();

		Scene sc = new Scene(root);
		root.getStylesheets().add(getClass().getResource("TilesMainLayoutApp.css").toExternalForm());
		primaryStage.setScene(sc);
		primaryStage.show();

	}

}
