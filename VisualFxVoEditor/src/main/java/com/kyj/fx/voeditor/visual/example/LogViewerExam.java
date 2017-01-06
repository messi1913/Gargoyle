/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2017. 1. 5.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.io.File;

import com.kyj.fx.voeditor.visual.component.text.LogViewComposite;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class LogViewerExam extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		File watchTargetFile = new File("C:\\Users\\KYJ\\AppData\\Local\\O-PREA-Batch\\app\\log\\batch-scheduler.log");

		LogViewComposite root = new LogViewComposite(watchTargetFile);
		primaryStage.setScene(new Scene(root.getParent()));
		primaryStage.show();

		//감지 하려는 대상 디렉토리

	}

}
