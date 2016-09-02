/********************************
  * 프로젝트 : VisualFxVoEditor
  * 패키지   : com.kyj.fx.voeditor.visual.example
  * 작성일   : 2016. 8. 23.
  * 작성자   : KYJ
  *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.kyj.fx.voeditor.visual.component.grid.EditableTableView;
import com.kyj.fx.voeditor.visual.component.grid.EditableTableView.ColumnExpression;
import com.kyj.fx.voeditor.visual.component.grid.EditableTableView.ValueExpression;
import com.kyj.fx.voeditor.visual.util.DbUtil;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/***************************
 *
 * @author KYJ
 *
 ***************************/
public class EditableTableViewExam extends Application {

	/********************************
	 * 작성일 : 2016. 8. 23. 작성자 : KYJ
	 *
	 *
	 * @param args
	 ********************************/
	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		Button btnExec = new Button("Exec.");
		Button btnAdd = new Button("Add ");
		Button btnRemove = new Button("Remove");
		Button btnSave = new Button("Save");
		btnAdd.setDisable(true);

		TextField textField = new TextField();
		HBox hBox = new HBox(5, textField, btnExec, btnAdd, btnRemove, btnSave);
		EditableTableView editableTableView = new EditableTableView(new Supplier<Connection>() {

			@Override
			public Connection get() {
				try {
					return DbUtil.getConnection();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		});

		editableTableView.tableNameProperty().addListener((oba, oldval, newval) -> {
			btnAdd.setDisable(false);
		});

		//  editableTableView.execute("tbm_sm_realtime_search");
		btnExec.setOnAction(e -> {
			String tableName = textField.getText();
			try {
				editableTableView.readByTableName("select * from " + tableName, tableName);

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		btnAdd.setOnAction(ev -> {
			editableTableView.getItems().add(new HashMap<>());
			editableTableView.getSelectionModel().selectLast();
			editableTableView.scrollTo(editableTableView.getItems().size() - 1);
		});

		btnRemove.setOnAction(ev -> {
			editableTableView.getItems().removeAll(editableTableView.getSelectionModel().getSelectedItems());
		});

		btnSave.setOnAction(ev -> {
			try {
				editableTableView.save();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		editableTableView.setOnMouseClicked(ev -> {
			Map<ColumnExpression, ObjectProperty<ValueExpression>> selectedItem = editableTableView.getSelectionModel().getSelectedItem();
			System.out.println(selectedItem);
		});

		BorderPane root = new BorderPane(editableTableView);
		root.setTop(hBox);
		Scene value = new Scene(root);

		//  ".table-row{ -fx-background-color: red; }"

		//  value.getStylesheets().add(EditableTableViewExam.class.getResource("EditableTableViewExam.css").toExternalForm());
		primaryStage.setScene(value);
		primaryStage.show();
	}

}
