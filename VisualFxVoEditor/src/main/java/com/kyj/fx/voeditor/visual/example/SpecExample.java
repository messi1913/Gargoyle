/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 2. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.io.File;

import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.words.spec.ui.tabs.SpecTabPane;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class SpecExample extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		SpecTabPane center = new SpecTabPane();
		Button btnCapture = new Button("SnapShot");
		ImageView ivOrigin = new ImageView();

		btnCapture.setOnAction(ev -> {

			FxUtil.snapShot(center, new File("example.png"));

			FxUtil.printJob(primaryStage, center);
		});

		Text text1 = new Text("Big italic red text");
		text1.setFill(Color.RED);
		text1.setFont(Font.font("Helvetica", FontPosture.ITALIC, 40));
		Text text2 = new Text(" little bold blue text");
		text2.setFill(Color.BLUE);
		text2.setFont(Font.font("Helvetica", FontWeight.BOLD, 10));
		TextFlow textFlow = new TextFlow(text1, text2);

		BorderPane root = new BorderPane(center);
		root.setTop(new HBox(textFlow, btnCapture));
		root.setBottom(ivOrigin);
		root.setPrefSize(800, 600);
		Scene value = new Scene(root, 800, 600);

		primaryStage.setScene(value);
		primaryStage.show();
	}

}
