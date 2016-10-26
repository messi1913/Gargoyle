/**
 * package : com.kyj.fx.voeditor.visual.component
 *	fileName : ColumnExample.java
 *	date      : 2015. 11. 11.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.example;

import java.util.HashMap;
import java.util.Map;

import com.kyj.fx.voeditor.visual.component.ColorPickerTableColumn;
import com.kyj.fx.voeditor.visual.component.dock.pane.DockPane;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class ColorPickerExample extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("DockFX");

		TableView<Map<String, Object>> tableView = new TableView<Map<String, Object>>();
		tableView.setEditable(true);
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("color", Color.RED);
			tableView.getItems().add(map);
		}
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("color", Color.GREEN);
			tableView.getItems().add(map);
		}

		tableView.setEditable(true);
		tableView.getColumns().add(new ColorPickerTableColumn<>(new TableColumn<>(), "color"));

		tableView.setOnMouseClicked(event -> {
			System.out.println("mouse click event");
		});

		Button btnAdd = new Button("추가");
		btnAdd.setOnMouseClicked(event -> {
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("color", Color.RED);
			tableView.getItems().add(hashMap);
		});
		BorderPane root = new BorderPane(tableView);

		root.setTop(new BorderPane(btnAdd));
		primaryStage.setScene(new Scene(root, 1100, 700));
		// primaryStage.setScene(new Scene(new BorderPane(new
		// MysqlPane("sample")), 1100, 700));
		primaryStage.sizeToScene();

		primaryStage.show();

		Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

		DockPane.initializeDefaultUserAgentStylesheet();

	}
}
