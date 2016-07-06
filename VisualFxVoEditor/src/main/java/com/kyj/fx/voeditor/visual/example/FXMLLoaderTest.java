/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 5. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/***************************
 *
 * @author KYJ
 *
 ***************************/
public class FXMLLoaderTest extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		TableView tb = FxUtil.load(SampleController.class, (TableView tv) -> {
			System.out.println("load complete...");
		});
		Scene scene = new Scene(tb);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
