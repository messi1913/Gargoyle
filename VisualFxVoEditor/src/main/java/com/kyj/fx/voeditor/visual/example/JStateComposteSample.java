/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.monitor.jstat.view
 *	작성일   : 2017. 7. 17.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.monitor.jstat.view.JStateCompositeWrapper;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class JStateComposteSample extends Application {

	/**
	 * 
	 */
	public JStateComposteSample() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 7. 17. 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		//input your process id;

		int pid = 8632;
		JStateCompositeWrapper jStateComposite = new JStateCompositeWrapper(pid);
		primaryStage.setScene(new Scene(jStateComposite.getParent()));
		primaryStage.show();
	}

}
