/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 1. 25.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.net.URL;

import com.kyj.fx.fxloader.FxLoader;
import com.kyj.fx.voeditor.visual.component.XmlDiffAppController;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class XmlFileCompareExam extends Application {

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 1. 20.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		XmlDiffAppController c = new XmlDiffAppController();

		BorderPane load = FxLoader.load(c);
		
		URL resource1 = new URL("http://biomesdev/PDF/888C9D2D-6932-4508-B2B1-AF23DEA4AD94.XML");
		URL resource2 = new URL("http://biomesdev/PDF/D7252CD7-6ABF-492D-A0B1-DC1BD6226321.XML");

//		try {
//			c.setDiff(resource1, resource2);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		Scene scene = new Scene(load);
		primaryStage.setScene(scene);
		primaryStage.show();

	}
}
