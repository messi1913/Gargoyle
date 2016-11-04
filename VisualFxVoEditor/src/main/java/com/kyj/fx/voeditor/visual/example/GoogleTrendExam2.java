/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 11. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.google.trend.GoogleTrendComposite;
import com.kyj.fx.voeditor.visual.main.initalize.ProxyInitializable;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class GoogleTrendExam2 extends Application {
	private static final Logger LOGGER = LoggerFactory.getLogger(GoogleTrendExam2.class);

	public static void main(String[] args) throws Exception {
		//프록시 관련 설정을  세팅함.
		new ProxyInitializable().initialize();
		launch(args);
	}

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		GoogleTrendComposite borRoot = new GoogleTrendComposite();
		primaryStage.setScene(new Scene(borRoot));
		primaryStage.setMaximized(true);
		primaryStage.show();
	}

}
