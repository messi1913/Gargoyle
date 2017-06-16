/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 2. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.io.File;

import com.kyj.fx.voeditor.visual.component.popup.ZipFileViewerComposite;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class ZipViewExample extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		String location = "C:\\Users\\KYJ\\Music\\멜론1710614.zip";
		
		ZipFileViewerComposite root = new ZipFileViewerComposite(new File(location));
		Scene value = new Scene(root, 800, 600);

		root.addReadFileFilter(".jar");
		primaryStage.setScene(value);
		primaryStage.show();
	}

}
