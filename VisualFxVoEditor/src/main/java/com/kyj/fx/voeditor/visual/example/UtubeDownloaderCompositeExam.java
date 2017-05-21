/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2017. 5. 12.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.utube.UtubeDownloaderComposite;
import com.kyj.fx.voeditor.visual.main.initalize.SSLInitializable;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class UtubeDownloaderCompositeExam extends Application {
	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		primaryStage.setScene(new Scene(new UtubeDownloaderComposite()));
		primaryStage.show();

	}

	public static void main(String[] args) throws Exception {
		new SSLInitializable().initialize();
		launch(args);
	}

}
