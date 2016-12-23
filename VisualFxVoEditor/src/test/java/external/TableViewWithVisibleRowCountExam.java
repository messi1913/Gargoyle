/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : external
 *	작성일   : 2016. 12. 1.
 *	작성자   : KYJ
 *******************************/
package external;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class TableViewWithVisibleRowCountExam extends Application {

	public class Person {
		StringProperty name = new SimpleStringProperty();;

		public Person(String name) {
			super();
			this.name.set(name);
		}

		public StringProperty nameProperty() {
			return this.name;
		}

		public java.lang.String getName() {
			return this.nameProperty().get();
		}

		public void setName(final java.lang.String name) {
			this.nameProperty().set(name);
		}

	}

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		TableViewWithVisibleRowCount<Object> center = new TableViewWithVisibleRowCount<>();

		TableColumn<Object, Object> e = new TableColumn<>();
		e.setCellValueFactory(new PropertyValueFactory<>("name"));
		center.getColumns().add(e);
		center.getItems().add(new Person("ssss"));
		center.getItems().add(new Person("ssss"));
		center.getItems().add(new Person("ssss"));
		center.getItems().add(new Person("ssss"));
		center.getItems().add(new Person("ssss"));
		center.getItems().add(new Person("ssss"));

		primaryStage.setScene(new Scene(new BorderPane(center), 300, 400));
		primaryStage.show();

		Platform.runLater(() -> {
			System.out.println(center.getVisibleRowCount());
		});

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 1.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

}
