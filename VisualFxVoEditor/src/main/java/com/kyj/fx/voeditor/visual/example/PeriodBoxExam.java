/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 12. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.jfoenix.controls.JFXDateTimePicker;
import com.kyj.fx.voeditor.visual.component.date.PeriodBoxComposite;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class PeriodBoxExam extends Application {
	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		BorderPane pane = new BorderPane();
		PeriodBoxComposite value = new PeriodBoxComposite();

		JFXDateTimePicker timePicker = new JFXDateTimePicker();
		Button btnGet = new Button("Get");
		pane.setBottom(btnGet);
		pane.setCenter(value);
		pane.setTop(timePicker);
		primaryStage.setScene(new Scene(pane));
		primaryStage.show();

		btnGet.setOnAction(ev -> {
			System.out.println(value.getStartDay());
			System.out.println(value.getStartDate());
			System.out.println(value.getStartTime());
		});
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
