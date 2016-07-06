/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2015. 10. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.util.HashMap;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import com.kyj.fx.voeditor.visual.component.popup.SimpleSQLResultView;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;

/**
 * @author KYJ
 *
 */
public class SimpleSQLResultViewExam extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		try {
			SharedMemory.setPrimaryStage(primaryStage);

			BorderPane mainParent = new BorderPane();
			Scene scene = new Scene(mainParent, 1280, 900);
			primaryStage.setScene(scene);

			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("userId", new String[]{"kyjun.kim","zz"});
			String sql = "SELECT * FROM sos.tbm_sm_user WHERE 1=1 " + " #if($userId) " + " AND  USER_ID = :userId " + " #end ";

			SimpleSQLResultView simpleSQLResultView = new SimpleSQLResultView(sql, param);
			simpleSQLResultView.show();
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
