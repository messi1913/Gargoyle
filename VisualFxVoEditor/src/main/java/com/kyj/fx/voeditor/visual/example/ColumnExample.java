/**
 * package : com.kyj.fx.voeditor.visual.component
 *	fileName : ColumnExample.java
 *	date      : 2015. 11. 11.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.example;

import java.util.Arrays;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import com.kyj.fx.voeditor.visual.component.ComboBoxTableColumn;
import com.kyj.fx.voeditor.visual.component.dock.pane.DockPane;
import com.kyj.fx.voeditor.visual.main.model.vo.ClassTypeCodeDVO;

/**
 * @author KYJ
 *
 */
public class ColumnExample extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("DockFX");

		TableView<ClassTypeCodeDVO> tableView = new TableView<ClassTypeCodeDVO>();
		tableView.setEditable(true);
		{
			ClassTypeCodeDVO e = new ClassTypeCodeDVO();
			e.setCommCode("코드1");
			e.setCommCodeNm("데이터 1");
			tableView.getItems().add(e);
		}

		{
			ClassTypeCodeDVO e = new ClassTypeCodeDVO();
			e.setCommCode("코드1");
			e.setCommCodeNm("데이터 2123123123123123123123123112312312331232\n\n\n\n\n\n\n\34534958345093443850934583409583405934850349534095830534098");
			tableView.getItems().add(e);
		}

		{
			ClassTypeCodeDVO e = new ClassTypeCodeDVO();
			e.setCommCode("코드3");
			e.setCommCodeNm("데이터 3");
			tableView.getItems().add(e);
		}

		{
			ClassTypeCodeDVO e = new ClassTypeCodeDVO();
			e.setCommCode("코드4");
			e.setCommCodeNm("데이터 4");
			tableView.getItems().add(e);
		}

		ObservableList<CodeDVO> observableArrayList = FXCollections.observableArrayList(Arrays.asList(new CodeDVO("출력용 코드명1", "코드1"),
				new CodeDVO("출력용코드명2", "코드2"), new CodeDVO("출력용코드명3", "코드3")));
		tableView.getColumns().add(new ComboBoxTableColumn<>("commCode", observableArrayList, "code", "codeNm"));
		TableColumn<ClassTypeCodeDVO, Object> e = new TableColumn<>("SAMPLE");
		e.setCellValueFactory(new PropertyValueFactory<>("commCodeNm"));
		tableView.getColumns().add(e);

		tableView.getColumns().add(new TableColumn<>("emptyCol1"));
		tableView.getColumns().add(new TableColumn<>("emptyCol2"));
		tableView.getColumns().add(new TableColumn<>("emptyCol3"));
		tableView.getColumns().add(new TableColumn<>("emptyCol4"));
		tableView.getColumns().add(new TableColumn<>("emptyCol5"));
		tableView.getColumns().add(new TableColumn<>("emptyCol6"));


		tableView.setOnMouseClicked(event -> {
			ClassTypeCodeDVO selectedItem = tableView.getSelectionModel().getSelectedItem();

			System.out.println("유효성 체크입니다.");
			System.out.println("코드 : 데이터 형태로 출력되야합니다.");
			System.out.println(selectedItem.getCommCode() + " : " + selectedItem.getCommCodeNm());

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
