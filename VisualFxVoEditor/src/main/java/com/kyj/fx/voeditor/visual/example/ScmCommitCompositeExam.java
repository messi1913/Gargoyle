/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 7. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.util.Properties;

import com.kyj.fx.voeditor.visual.component.scm.FxSVNHistoryDataSupplier;
import com.kyj.fx.voeditor.visual.component.scm.ScmCommitComposite;
import com.kyj.fx.voeditor.visual.component.scm.SvnChagnedCodeComposite;
import com.kyj.scm.manager.svn.java.JavaSVNManager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
		properties.put(JavaSVNManager.SVN_URL, "svn://10.40.41.49/");
		properties.put(JavaSVNManager.SVN_USER_ID, "kyjun.kim");
		properties.put(JavaSVNManager.SVN_USER_PASS, "kyjun.kim");

		FxSVNHistoryDataSupplier svnDataSupplier = new FxSVNHistoryDataSupplier(new JavaSVNManager(properties));

		SvnChagnedCodeComposite svnChagnedCodeComposite = new SvnChagnedCodeComposite(svnDataSupplier);
		ScmCommitComposite scmCommitComposite = new ScmCommitComposite(svnDataSupplier);
		TabPane tabPane = new TabPane();
		tabPane.getTabs().addAll(new Tab("Chagned Codes.", svnChagnedCodeComposite), new Tab("Commit Hist.", scmCommitComposite));
		primaryStage.setScene(new Scene(tabPane));
		primaryStage.show();
	}

}
