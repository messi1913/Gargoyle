/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 3. 20.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.io.File;

import com.kyj.fx.voeditor.visual.component.file.FilesAnalysisComposite;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * TODO 클래스 역할
 *
 * @author KYJ
 *
 */
public class FileAnalysisExam extends Application {

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 20.
	 * @param args
	 */
	public static void main(String[] args) {

		launch(args);

	}

	/***********************************************************************************/
	/* 이벤트 구현 */

	@Override
	public void start(Stage primaryStage) throws Exception {

		File f = new File("C:\\ph4net0m\\ph4net0m");
		FilesAnalysisComposite view = new FilesAnalysisComposite(f);

		primaryStage.setScene(new Scene(view, 1200, 700));
		primaryStage.show();

	}

	//

	/***********************************************************************************/

	/***********************************************************************************/
	/* 일반API 구현 */

	//
	/***********************************************************************************/
}
