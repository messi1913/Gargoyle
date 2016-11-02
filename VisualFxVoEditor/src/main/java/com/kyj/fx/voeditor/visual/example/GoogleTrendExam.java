/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 11. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.chart.service.BaseGoogleTrendChart;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class GoogleTrendExam extends Application{

	public static void main(String[] args) {
		launch(args);
	}
	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		String source = ValueUtil.toString(GoogleTrendExam.class.getResourceAsStream("GoogleTrendSample.json"));
		primaryStage.setScene(new Scene(new BaseGoogleTrendChart(source)));
		primaryStage.show();
	}

}
