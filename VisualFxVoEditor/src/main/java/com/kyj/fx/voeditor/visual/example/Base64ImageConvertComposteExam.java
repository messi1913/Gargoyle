/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2017. 9. 14.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.image.Base64ImageConvertComposte;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class Base64ImageConvertComposteExam extends Application {

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 14.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(new Base64ImageConvertComposte());
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
