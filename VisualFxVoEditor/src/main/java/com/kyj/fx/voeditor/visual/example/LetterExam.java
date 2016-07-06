/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 4. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.LettersPane;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class LetterExam extends Application {

	/**
	 */
	public LetterExam() {

		// TODO Auto-generated constructor stub

	}
	/**********************************************************************************************/
	/* 이벤트 처리항목 기술 */
	// TODO Auto-generated constructor stub
	/**********************************************************************************************/

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 4.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		LettersPane lettersPane = new LettersPane();
		primaryStage.setScene(new Scene(lettersPane));
		primaryStage.show();
	}

	/**********************************************************************************************/
	/* 그 밖의 API 기술 */
	// TODO Auto-generated constructor stub
	/**********************************************************************************************/
}
