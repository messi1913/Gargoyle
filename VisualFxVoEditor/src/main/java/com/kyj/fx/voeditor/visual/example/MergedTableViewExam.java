/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 9. 9.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.util.Random;

import com.kyj.fx.voeditor.visual.component.grid.MergedTableView;
import com.kyj.fx.voeditor.visual.component.grid.MergedTextFieldTableCell;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;

/***************************
 * 
 * @author KYJ
 *
 ***************************/
public class MergedTableViewExam extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		MergedTableView<String> root = new MergedTableView<>();
		TableColumn<String,String> e = new TableColumn<>("sample");
		e.setCellValueFactory(param -> {
			String value = param.getValue();
			return new SimpleStringProperty(value);
		});
		e.setCellFactory(MergedTextFieldTableCell.forTableColumn());
		
		root.getColumns().add(e);

		Random random = new Random(10);
		for (int i = 0; i < 100; i++) {
			root.getItems().add(String.valueOf(random.nextInt(10)));
		}
		//		
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}

}
