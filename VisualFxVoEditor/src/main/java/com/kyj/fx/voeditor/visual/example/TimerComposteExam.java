/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2017. 6. 16.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.timer.TimerComposite;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class TimerComposteExam extends Application {

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		TimerComposite root = new TimerComposite();
		BorderPane root2 = new BorderPane(root);
		Button button = new Button("start");
		button.setOnAction(ev -> root.start());
		Button button2 = new Button("stop");
		button2.setOnAction(ev -> root.stop());
		Button button3 = new Button("pause");
		button3.setOnAction(ev -> root.pause());
		
		Button button4 = new Button("resume");
		button4.setOnAction(ev -> root.resume());
		
		root2.setTop(new HBox(button, button2, button3, button4));
		primaryStage.setScene(new Scene(root2));
		primaryStage.show();

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 16. 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

}
