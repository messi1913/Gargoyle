/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 10. 25.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.dock.pane.DockNode;
import com.kyj.fx.voeditor.visual.component.dock.pane.DockPane;
import com.kyj.fx.voeditor.visual.component.dock.pane.DockPos;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class DockPaneExam extends Application {

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		DockNode dockNode = new DockNode(new BorderPane(new Label("Master")), "hello m");
		DockNode dockNode2 = new DockNode(new BorderPane(new Label("Slave")), "hello s");
		//		SplitPane root = new SplitPane();

		DockPane dockPane = new DockPane();


		dockNode2.dock(dockPane, DockPos.CENTER);
		dockNode.dock(dockPane, DockPos.LEFT);

		//		root.getItems().add(dockPane);
		Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
		DockPane.initializeDefaultUserAgentStylesheet();
		primaryStage.setScene(new Scene(dockPane));
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

}
