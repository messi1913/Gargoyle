/**
 * package : com.kyj.fx.voeditor.visual.component
 *	fileName : ColumnExample.java
 *	date      : 2015. 11. 11.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.example;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class ColumnExample3 extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException {

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("ColumnExam3.fxml"));
		primaryStage.setTitle("Column Resize Test");
		primaryStage.setScene(new Scene(new BorderPane(loader.load()), 1100, 700));
		primaryStage.sizeToScene();
		primaryStage.show();

	}
}
