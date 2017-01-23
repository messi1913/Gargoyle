/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 2. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.bci.view.JavaProcessMonitor;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class ProcessMonitorExample extends Application {

	private JavaProcessMonitor javaProcessMonitor;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		javaProcessMonitor = new JavaProcessMonitor();
		Scene value = new Scene(javaProcessMonitor.getParent(), 800, 600);

		primaryStage.setScene(value);
		primaryStage.show();
	}

}
