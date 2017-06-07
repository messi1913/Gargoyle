/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2017. 6. 7.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.kyj.fx.voeditor.visual.framework.listview.ListViewFindHelper;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * @author KYJ
 *
 */
public class ListViewExam extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		ListView<String> center = new ListView<String>();

		List<String> collect = Stream.iterate(0, a -> a + 1).limit(100).map(v -> String.valueOf(v)).collect(Collectors.toList());
		center.getItems().addAll(collect);
		primaryStage.setScene(new Scene(new BorderPane(center)));
		primaryStage.show();

		ListViewFindHelper.install(center, new StringConverter<String>() {

			@Override
			public String toString(String object) {
				return object;
			}

			@Override
			public String fromString(String string) {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}

}
