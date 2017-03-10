/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 2. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.io.File;

import com.kyj.fx.voeditor.visual.component.config.view.AntRunConfigView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class AntBuildExam extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		String buildFile = "C:\\Users\\KYJ\\.git\\Gargoyle\\VisualFxVoEditor\\build\\build.xml";
		File file = new File(buildFile);
		AntRunConfigView antRunConfigView = new AntRunConfigView(primaryStage, file);

		Scene value = new Scene(antRunConfigView, 800, 600);
		
		primaryStage.setScene(value);
		primaryStage.show();
	}

}
