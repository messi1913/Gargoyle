/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 2. 11.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import com.kyj.fx.voeditor.visual.component.popup.VariableMappingView;

/**
 * @author KYJ
 *
 */
public class DialogExam extends Application {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		String sql = "select * from tbm_sm_user where user_id = :userId";
		Button center = new Button("클릭");
		center.setOnMouseClicked(event -> {
			VariableMappingView variableMappingView = new VariableMappingView();
			variableMappingView.extractVariableFromSql(sql);
			variableMappingView.showAndWait(item -> {
				System.out.println(item);
			});
		});
		primaryStage.setScene(new Scene(new BorderPane(center)));
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

}
