/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2017. 10. 29.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.popup.TableViewSearchComposite;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class TableViewSearchExam extends Application {

	/********************************
	 * 패키지 : 작성일 : 2016. 2. 23. 작성자 : KYJ
	 *
	 * @param args
	 *******************************/
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		TableView<Value> t = new TableView<>();
		TableColumn<Value, String> e = new TableColumn<>();
		e.setCellValueFactory(c -> c.getValue().nameProperty());

		t.getColumns().add(e);
		for (int i = 0; i < 100; i++)
			t.getItems().add(new Value(i));
		BorderPane root = new BorderPane(t);

		Scene sc = new Scene(root);
		primaryStage.setScene(sc);
		primaryStage.show();

		TableViewSearchComposite<Value> composite = new TableViewSearchComposite<Value>(primaryStage, t);
		composite.show();
	}

	public static class Value {
		public StringProperty name;

		public Value() {
			name = new SimpleStringProperty();
		}

		public Value(String v) {
			name = new SimpleStringProperty(v);
		}

		public Value(int v) {
			name = new SimpleStringProperty(String.valueOf(v));
		}

		public final StringProperty nameProperty() {
			return this.name;
		}

		public final String getName() {
			return this.nameProperty().get();
		}

		public final void setName(final String name) {
			this.nameProperty().set(name);
		}

	}
}
