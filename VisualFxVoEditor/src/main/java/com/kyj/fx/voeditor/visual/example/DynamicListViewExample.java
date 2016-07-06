/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 2. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.grid.DynamicImageViewItem;
import com.kyj.fx.voeditor.visual.component.grid.DynamicLabelItem;
import com.kyj.fx.voeditor.visual.component.grid.DynamicItemListView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class DynamicListViewExample extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		DynamicItemListView dynamicListView = new DynamicItemListView();
		dynamicListView.getItems().add(new DynamicLabelItem("테스트"));
		dynamicListView.getItems().add(new DynamicImageViewItem("C:\\Users\\KYJ\\Pictures\\98.jpg"));
		BorderPane root = new BorderPane(dynamicListView);
		root.setPrefSize(800, 600);
		Scene value = new Scene(root, 800, 600);

		primaryStage.setScene(value);
		primaryStage.show();
	}

}
