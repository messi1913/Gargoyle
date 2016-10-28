/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 10. 27.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class CommboBoxExam extends Application {
	@FXML
	private TableView<SampleVO> tbSample;

	@FXML
	private TableColumn<SampleVO, String> colCommbo;

	public static void main(String[] args) {
		launch(args);
	}

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("CommboBoxExam.fxml"));
		fxmlLoader.setController(this);
		Parent load = fxmlLoader.load();
		Scene value = new Scene(load);

		value.getStylesheets().add(getClass().getResource("CommboBoxExam.css").toExternalForm());
		primaryStage.setScene(value);
		primaryStage.show();

	}

	@FXML
	public void initialize() {
		colCommbo.setCellFactory(param -> {
			ComboBoxTableCell<SampleVO, String> comboBoxTableCell = new ComboBoxTableCell<>("Hi", "My", "Name", "Is", "Kim");
			comboBoxTableCell.setPickOnBounds(true);
			comboBoxTableCell.updateSelected(true);
			return comboBoxTableCell;
		});
		colCommbo.setCellValueFactory(v -> v.getValue().nameProperty());

		tbSample.getItems().add(new SampleVO("Hi"));
		tbSample.getItems().add(new SampleVO("My"));
		tbSample.getItems().add(new SampleVO("Name"));
		tbSample.getItems().add(new SampleVO("Is"));

	}

	static class SampleVO {
		StringProperty name;

		public SampleVO(String string) {
			name = new SimpleStringProperty(string);
		}

		public final StringProperty nameProperty() {
			return this.name;
		}

		public final java.lang.String getName() {
			return this.nameProperty().get();
		}

		public final void setName(final java.lang.String name) {
			this.nameProperty().set(name);
		}

	}
}
