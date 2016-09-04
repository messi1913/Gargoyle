/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 8. 30.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.macro.MacroSqlComposite;
import com.kyj.fx.voeditor.visual.util.DbUtil;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 스케줄링 컴포넌트 테스튼
 * 
 * @author KYJ
 *
 */
public class MacroControlExam extends Application {

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 30.
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

		MacroSqlComposite macroSqlComposite = new MacroSqlComposite(() -> {
			try {
				return DbUtil.getConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}, "");

		primaryStage.setScene(new Scene(macroSqlComposite));
		primaryStage.show();

	}

}
