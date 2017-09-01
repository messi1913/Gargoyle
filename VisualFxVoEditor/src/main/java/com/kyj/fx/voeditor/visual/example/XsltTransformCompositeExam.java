/**
 * 
 */
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.text.XsltTransformComposite;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class XsltTransformCompositeExam extends Application {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(new XsltTransformComposite());
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

}
