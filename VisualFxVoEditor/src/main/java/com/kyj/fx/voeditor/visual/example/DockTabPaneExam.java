/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 10. 25.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.dock.tab.DockTab;
import com.kyj.fx.voeditor.visual.component.dock.tab.DockTabPane;
import com.kyj.fx.voeditor.visual.component.sql.dock.DockPane;

import javafx.application.Application;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class DockTabPaneExam extends Application {

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		DockTabPane dockTabPane = new DockTabPane();
		dockTabPane.setSide(Side.LEFT);

		{
			DockTab e = new DockTab("Hello1");
			e.setContent(new Label("Hello world!1"));
			dockTabPane.getTabs().add(e);
		}
		{
			DockTab e = new DockTab("Hello2");
			e.setContent(new Label("Hello world!2"));
			dockTabPane.getTabs().add(e);
		}
		{
			DockTab e = new DockTab("Hello3");
			e.setContent(new Label("Hello world!3"));
			dockTabPane.getTabs().add(e);
		}

		DockPane.initializeDefaultUserAgentStylesheet();

		primaryStage.setScene(new Scene(dockTabPane));
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

}
