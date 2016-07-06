/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 4. 12.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.date.TimeClocker;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class ClockExam extends Application {

	/**
	 */
	public ClockExam() {

	}

	public static void main(String[] args) {

		launch(args);

	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		TimeClocker clock = new TimeClocker();
		primaryStage.setScene(new Scene(new BorderPane(clock)));
		primaryStage.show();


		System.err.println(clock.getTimeString());
	}

	/**********************************************************************************************/
	/* 이벤트 처리항목 기술 */
	// TODO Auto-generated constructor stub
	/**********************************************************************************************/

	/**********************************************************************************************/
	/* 그 밖의 API 기술 */
	// TODO Auto-generated constructor stub
	/**********************************************************************************************/
}
