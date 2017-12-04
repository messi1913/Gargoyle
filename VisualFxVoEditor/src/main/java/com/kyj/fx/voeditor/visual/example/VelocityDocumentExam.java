/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 12. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.velocity.DefaultVelocityBinderComposite;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class VelocityDocumentExam extends Application {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		BorderPane pane = new DefaultVelocityBinderComposite();

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
