/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2017. 11. 30.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.sql.functions.ConnectionSupplier;
import com.kyj.fx.voeditor.visual.component.sql.prcd.mssql.MssqlProcedureCallComposite;
import com.kyj.fx.voeditor.visual.util.DbUtil;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class ProcedureCallExam extends Application {

	/**
	 */
	public ProcedureCallExam() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		ConnectionSupplier connectionSupplier = new ConnectionSupplier() {

			@Override
			public String getUrl() {
				return "";
			}

			@Override
			public String getUsername() {
				return "sa";
			}

			@Override
			public String getPassword() {
				return "";
			}

			@Override
			public String getDriver() {
				return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			}
			
		};

		MssqlProcedureCallComposite<Void> root = new MssqlProcedureCallComposite<Void>(connectionSupplier);
		root.setProcedure("master", "sys", "sp_add_data_file_recover_suspect_db");
		root.prepare();
		primaryStage.setScene(new Scene(root));
		primaryStage.show();

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 30.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

}
