/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 4. 13.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.controlsfx.control.table.TableFilter;

import com.kyj.fx.voeditor.visual.util.FxUtil;

import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

/***************************
 *
 * @author KYJ
 *
 ***************************/
public class FilterTableExample extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {

		TableView<DataItem> tableView = new TableView<>();

		FxUtil.installClipboardKeyEvent(tableView);
		
		tableView.setItems(FXCollections.observableArrayList());
		IntStream.range(0, 20000).mapToObj(i -> new DataItem()).forEach(d -> tableView.getItems().add(d));

		TableColumn<DataItem, Integer> smallInt = new TableColumn<>("Small Int");
		smallInt.setCellValueFactory(cb -> new ReadOnlyObjectWrapper<>(cb.getValue().getSmallIntValue()));

		TableColumn<DataItem, Integer> largeInt = new TableColumn<>("Large Int");
		largeInt.setCellValueFactory(cb -> new ReadOnlyObjectWrapper<>(cb.getValue().getLargeIntValue()));

		TableColumn<DataItem, String> randomLetter = new TableColumn<>("Letter");
		randomLetter.setCellValueFactory(cb -> new ReadOnlyObjectWrapper<>(cb.getValue().getRandomLetter()));

		TableColumn<DataItem, String> randomString1 = new TableColumn<>("AlphaNum 1");
		randomString1.setCellValueFactory(cb -> new ReadOnlyObjectWrapper<>(cb.getValue().getRandomStr1()));

		TableColumn<DataItem, String> randomString2 = new TableColumn<>("AlphaNum 2");
		randomString1.setCellValueFactory(cb -> new ReadOnlyObjectWrapper<>(cb.getValue().getRandomStr2()));

		tableView.getColumns().addAll(smallInt, largeInt, randomLetter, randomString1, randomString2);

		Platform.runLater(() -> new TableFilter<>(tableView));

		GridPane grp = new GridPane();

		GridPane.setFillHeight(tableView, true);
		GridPane.setFillWidth(tableView, true);
		GridPane.setHgrow(tableView, Priority.ALWAYS);
		GridPane.setVgrow(tableView, Priority.ALWAYS);
		grp.getChildren().add(tableView);

		Scene scene = new Scene(grp);

		primaryStage.setScene(scene);

		primaryStage.show();

	}

	private static final class DataItem {

		private final int smallIntValue = new Random().nextInt(100);
		private final int largeIntValue = new Random().nextInt(10000);
		private final String randomLetter = String.valueOf((char) (new Random().nextInt(26) + 'a'));

		private final String randomStr1 = UUID.randomUUID().toString().replaceAll("-", "");
		private final String randomStr2 = UUID.randomUUID().toString().replaceAll("-", "");

		public int getLargeIntValue() {
			return largeIntValue;
		}

		public int getSmallIntValue() {
			return smallIntValue;
		}

		public String getRandomLetter() {
			return randomLetter;
		}

		public String getRandomStr1() {
			return randomStr1;
		}

		public String getRandomStr2() {
			return randomStr2;
		}
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

}
