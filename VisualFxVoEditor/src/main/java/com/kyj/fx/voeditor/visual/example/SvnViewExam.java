/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 4. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.util.Properties;

import com.kyj.fx.voeditor.visual.component.scm.SVNTreeView;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * TODO 클래스 역할
 *
 * @author KYJ
 *
 */
public class SvnViewExam extends Application {

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 2.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

	/***********************************************************************************/
	/* 이벤트 구현 */

	@Override
	public void start(Stage primaryStage) throws Exception {
		Properties prop = new Properties();
		prop.put(SVNTreeView.SVN_USER_ID, "callakrsos");
		prop.put(SVNTreeView.SVN_USER_PASS, "mFn+QPl+TW8=");
		prop.put(SVNTreeView.SVN_URL, "https://dev.naver.com/svn/javafxvoeditor/trunk");
		prop.put(SVNTreeView.SVN_PATH, ConfigResourceLoader.getInstance().get(SVNTreeView.SVN_PATH_WINDOW));
		BorderPane borderPane = new BorderPane(new SVNTreeView());
		primaryStage.setScene(new Scene(borderPane));
		primaryStage.show();

	}

	// 

	/***********************************************************************************/

	/***********************************************************************************/
	/* 일반API 구현 */

	// 
	/***********************************************************************************/
}
