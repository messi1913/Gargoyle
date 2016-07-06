package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.sql.view.CommonsSqllPan;
import com.kyj.fx.voeditor.visual.exceptions.ConnectionFailException;
import com.kyj.fx.voeditor.visual.exceptions.NotYetSupportException;
import com.kyj.fx.voeditor.visual.momory.SkinManager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SqlTabPanExample extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws NotYetSupportException, ConnectionFailException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		primaryStage.setTitle("Database Exam");

		CommonsSqllPan sqlPane = CommonsSqllPan.getSqlPane();
		sqlPane.getStylesheets().add(SkinManager.getInstance().getSkin());
		BorderPane root = new BorderPane(sqlPane);
		Menu menu = new Menu("Exam");
		MenuItem e = new MenuItem("exam");
		e.setOnAction(System.out::println);
		e.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
		menu.getItems().add(e);
		root.setTop(new MenuBar(menu));

		primaryStage.setScene(new Scene(root, 1100, 700));

		primaryStage.show();

		// Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

		// DockPane.initializeDefaultUserAgentStylesheet();

	}

}
