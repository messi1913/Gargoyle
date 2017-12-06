/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 12. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.kyj.fx.voeditor.visual.component.velocity.DefaultVelocityBinderComposite;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;

import javafx.application.Application;
import javafx.scene.Scene;
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

		DefaultVelocityBinderComposite pane = new DefaultVelocityBinderComposite();
		ArrayList<Map<String, Object>> data = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			Map<String, Object> e = new LinkedHashMap<String, Object>();
			e.put("v3", i);
			e.put("v4", i);
			e.put("v1", i);
			e.put("v2", i);
			data.add(e);
		}

		pane.setContext(ConfigResourceLoader.getInstance().get("default.database.view.template"));
		pane.setData(data);

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
