/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 2. 11.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import com.kyj.fx.voeditor.visual.component.popup.VariableMappingView;

/**
 * @author KYJ
 *
 */
public class MapValueFactoryExam extends Application {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		VariableMappingView controller = new VariableMappingView();

		controller.extractVariableFromSql("select * from tbm_sm_user where user_id = :userId");
		//controller.setVariableItems(extractVariableFromSql);

		primaryStage.setScene(new Scene(new BorderPane(controller)));
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

}
