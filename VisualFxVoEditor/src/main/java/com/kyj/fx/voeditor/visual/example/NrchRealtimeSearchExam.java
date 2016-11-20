/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 11. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.NrchRealtimeSrchFlowComposite;
import com.kyj.fx.voeditor.visual.main.initalize.ProxyInitializable;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class NrchRealtimeSearchExam extends Application {

	public static void main(String[] args) throws Exception {
		new ProxyInitializable().initialize();
		launch(args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		NrchRealtimeSrchFlowComposite root = new NrchRealtimeSrchFlowComposite();
		primaryStage.setScene(new Scene(root, 1200, 800));

		primaryStage.show();
		root.reflesh();
	}

}
