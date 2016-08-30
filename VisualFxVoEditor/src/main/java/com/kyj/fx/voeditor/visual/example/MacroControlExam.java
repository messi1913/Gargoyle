/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 8. 30.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.macro.MacroControl;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class MacroControlExam extends Application{

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 30.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setScene(new Scene(new MacroControl()));
		primaryStage.show();

	}

}
