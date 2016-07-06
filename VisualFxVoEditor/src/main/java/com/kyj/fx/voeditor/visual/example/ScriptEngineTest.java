/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 5. 11.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */

public class ScriptEngineTest extends Application {

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		primaryStage.setScene(new Scene(FxUtil.load(ExController.class)));
		primaryStage.show();
	}

}
