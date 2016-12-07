/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 12. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.util.HashMap;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class VelocityDocumentExam extends Application {

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		BorderPane pane = new BorderPane();

		TextArea root = new TextArea();
		TextArea btm = new TextArea();

		SplitPane splitPane = new SplitPane( root, btm);
		splitPane.setDividerPosition(0, 0.6d);
		splitPane.setOrientation(Orientation.VERTICAL);
		pane.setCenter(splitPane);
		Button value = new Button("Excute");
		pane.setBottom(value);

		value.setOnAction(ev -> {

			String velocityToText = ValueUtil.getVelocityToText(root.getText(), new HashMap<String, Object>(), false);
			btm.setText(velocityToText);
		});
		primaryStage.setScene(new Scene(pane));
		primaryStage.show();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 6.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

}
