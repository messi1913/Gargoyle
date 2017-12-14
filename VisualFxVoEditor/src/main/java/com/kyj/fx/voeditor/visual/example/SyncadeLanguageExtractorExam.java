/********************************
 *	프로젝트 : JFoenix
 *	패키지   : demos
 *	작성일   : 2016. 12. 9.
 *	프로젝트 : OPERA
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.text.SyncadeLanguageExtractor;
import com.kyj.fx.voeditor.visual.exceptions.GargoyleConnectionFailException;
import com.kyj.fx.voeditor.visual.exceptions.NotYetSupportException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class SyncadeLanguageExtractorExam extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws NotYetSupportException, GargoyleConnectionFailException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		primaryStage.setTitle("Database Exam");

		SyncadeLanguageExtractor view = new SyncadeLanguageExtractor();

		primaryStage.setScene(new Scene(view, 1100, 700));

		primaryStage.show();

		// Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

		// DockPane.initializeDefaultUserAgentStylesheet();

	}

}
