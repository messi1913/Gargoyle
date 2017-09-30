/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2017. 9. 29.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.http.HttpActionComposite;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class HttpActionCompositeExam extends Application {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(new HttpActionComposite());
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 29.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

}
