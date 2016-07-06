/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 1. 20.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

import com.kyj.fx.voeditor.visual.component.CheckBoxFxControlTreeView;
import com.kyj.fx.voeditor.visual.component.FxControlTreeView;

/**
 * @author KYJ
 *
 */
public class FxControlsTreeViewExam extends Application {

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 1. 20.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

	Node getTestNod() {
		/* [시작] 분석하고자하는 UI구조 */
		BorderPane borderPane = new BorderPane();
		ScrollPane scrollPane2 = new ScrollPane();
		scrollPane2.setContent(new TextArea());
		borderPane.setTop(new HBox(new Button(), new Button(), new HTMLEditor()));
		borderPane.setCenter(new BorderPane(scrollPane2));
		/* [끝] 분석하고자하는 UI구조 */
		return borderPane;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		CheckBoxFxControlTreeView checkedNodeTreeView = new CheckBoxFxControlTreeView(getTestNod());
		checkedNodeTreeView.setOnMouseClicked(event -> {
			System.out.println(checkedNodeTreeView.getCheckModel().getCheckedItems());
		});

		FxControlTreeView simpleNodeTreeView = new FxControlTreeView(getTestNod());
		simpleNodeTreeView.setOnMouseClicked(event -> {
			System.out.println(simpleNodeTreeView.getSelectionModel().getSelectedItems());
		});

		Scene scene = new Scene(new SplitPane(simpleNodeTreeView, checkedNodeTreeView));
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
