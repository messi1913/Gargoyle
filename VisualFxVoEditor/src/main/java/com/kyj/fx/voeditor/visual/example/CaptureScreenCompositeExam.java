/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2017. 7. 28.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.capture.ErdScreenController;
import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class CaptureScreenCompositeExam extends Application {

	/**
	 * 
	 */
	public CaptureScreenCompositeExam() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		Parent load = FxUtil.load(ErdScreenController.class);
//		CaptureScreenAdapter c = new CaptureScreenAdapter();
		primaryStage.setScene(new Scene(load));
		primaryStage.show();

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 7. 28. 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

}
