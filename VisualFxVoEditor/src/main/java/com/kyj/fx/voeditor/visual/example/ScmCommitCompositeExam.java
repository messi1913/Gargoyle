/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 7. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.util.Properties;

import com.kyj.fx.voeditor.visual.component.scm.ScmCommitComposite;
import com.kyj.scm.manager.svn.java.JavaSVNManager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class ScmCommitCompositeExam extends Application {

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 18.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		Properties properties = new Properties();
		properties.put(JavaSVNManager.SVN_URL, "svn://localhost/svn");
		properties.put(JavaSVNManager.SVN_USER_ID, "kyjun.kim");
		properties.put(JavaSVNManager.SVN_USER_PASS, "kyjun.kim");

		primaryStage.setScene(new Scene(new ScmCommitComposite(properties)));
		primaryStage.show();
	}

}
