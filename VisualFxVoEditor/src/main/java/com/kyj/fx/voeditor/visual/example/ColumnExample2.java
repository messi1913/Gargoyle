/**
 * package : com.kyj.fx.voeditor.visual.component
 *	fileName : ColumnExample.java
 *	date      : 2015. 11. 11.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.example;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import com.kyj.fx.voeditor.visual.component.ComboBoxTableColumn;
import com.kyj.fx.voeditor.visual.component.sql.dock.DockPane;

/**
 * @author KYJ
 *
 */
public class ColumnExample2 extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("DockFX");

		TableView<Map<String, String>> tableView = new TableView<Map<String, String>>();
		tableView.setEditable(true);
		{
			Map<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("commCode", "코드1");
			hashMap.put("commCodeNm", "데이터 1");
			tableView.getItems().add(hashMap);
		}

		{
			Map<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("commCode", "코드1");
			hashMap.put("commCodeNm", "데이터 2");
			tableView.getItems().add(hashMap);
		}

		{
			Map<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("commCode", "코드3");
			hashMap.put("commCodeNm", "데이터 3");
			tableView.getItems().add(hashMap);
		}

		{
			Map<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("commCode", "코드4");
			hashMap.put("commCodeNm", "데이터 4");
			tableView.getItems().add(hashMap);

		}

		ObservableList<CodeDVO> observableArrayList = FXCollections.observableArrayList(Arrays.asList(new CodeDVO("출력용 코드명1", "코드1"),
				new CodeDVO("출력용코드명2", "코드2"), new CodeDVO("출력용코드명3", "코드3")));
		tableView.getColumns().add(new ComboBoxTableColumn<>("commCode", observableArrayList, "code", "codeNm"));
		TableColumn<Map<String, String>, Object> e = new TableColumn<>("SAMPLE");
		e.setCellValueFactory(new MapValueFactory("commCodeNm"));
		tableView.getColumns().add(e);

		tableView.setOnMouseClicked(event -> {
			Map<String, String> selectedItem = tableView.getSelectionModel().getSelectedItem();

			System.out.println("유효성 체크입니다.");
			System.out.println("코드 : 데이터 형태로 출력되야합니다.");
			System.out.println(selectedItem.get("commCode") + " : " + selectedItem.get("commCodeNm"));

		});

		primaryStage.setScene(new Scene(new BorderPane(tableView), 1100, 700));
		// primaryStage.setScene(new Scene(new BorderPane(new
		// MysqlPane("sample")), 1100, 700));
		primaryStage.sizeToScene();

		primaryStage.show();

		Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

		DockPane.initializeDefaultUserAgentStylesheet();

	}
}
