/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 5. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.chart.SliderLineChartComposite;
import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

/***************************
 *
 * @author KYJ
 *
 ***************************/
public class SliderLineChartExam extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent loadRoot1 = null;
		Parent loadRoot2 = null;
		try {
			loadRoot1 = FxUtil.loadRoot(SliderLineChartComposite.class);
			loadRoot2 = FxUtil.loadRoot(SliderLineChartComposite.class);

			primaryStage.setScene(new Scene(new SplitPane(loadRoot1, loadRoot2)));
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(loadRoot1 == loadRoot2);

	}

}