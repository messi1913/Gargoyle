/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 2. 11.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.example.CheckBoxTableCellExam.SampleVO;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * @author KYJ
 *
 */
public class CheckBoxTableCellExam extends Application {

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		TableView<SampleVO> center = new TableView<>();
		center.setEditable(true);
		TableColumn<SampleVO, Boolean> e = new TableColumn<SampleVO, Boolean>();
		e.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SampleVO, Boolean>, ObservableValue<Boolean>>() {

			@Override
			public ObservableValue<Boolean> call(CellDataFeatures<SampleVO, Boolean> param) {
				return param.getValue().checked;
			}
		});
		e.setCellFactory(new Callback<TableColumn<SampleVO, Boolean>, TableCell<SampleVO, Boolean>>() {

			@Override
			public TableCell<SampleVO, Boolean> call(TableColumn<SampleVO, Boolean> param) {
				return new CheckBoxTableCell<SampleVO, Boolean>() {

					/*
					 * (non-Javadoc)
					 *
					 * @see
					 * javafx.scene.control.cell.CheckBoxTableCell#updateItem(
					 * java.lang.Object, boolean)
					 */
					@Override
					public void updateItem(Boolean item, boolean empty) {
						super.updateItem(item, empty);

					}

					/*
					 * (non-Javadoc)
					 *
					 * @see
					 * javafx.scene.control.Cell#isItemChanged(java.lang.Object,
					 * java.lang.Object)
					 */
					@Override
					protected boolean isItemChanged(Boolean oldItem, Boolean newItem) {

						System.out.println(oldItem + " : " + newItem);
						return super.isItemChanged(oldItem, newItem);
					}

				};
			}
		});
		e.setEditable(true);

		TableColumn<SampleVO, String> e2 = new TableColumn<SampleVO, String>();
		e2.setCellValueFactory(new PropertyValueFactory<SampleVO, String>("name"));
		center.getColumns().add(e);
		center.getColumns().add(e2);

		center.getItems().add(new SampleVO(true, "kim"));
		center.getItems().add(new SampleVO(true, "young"));
		BorderPane borderPane = new BorderPane(center);
		primaryStage.setScene(new Scene(borderPane));
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

	public static class SampleVO {
		private BooleanProperty checked;
		private StringProperty name;

		public SampleVO() {
			checked = new SimpleBooleanProperty();
			name = new SimpleStringProperty();
		}

		public SampleVO(boolean checked, String name) {
			this();
			this.checked.set(checked);
			this.name.set(name);
		}

		public BooleanProperty checkedProperty() {
			return this.checked;
		}

		public boolean isChecked() {
			return this.checkedProperty().get();
		}

		public void setChecked(boolean checked) {
			this.checkedProperty().set(checked);
		}

		public StringProperty nameProperty() {
			return this.name;
		}

		public java.lang.String getName() {
			return this.nameProperty().get();
		}

		public void setName(java.lang.String name) {
			this.nameProperty().set(name);
		}

	}

}
