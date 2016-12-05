/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 12. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.config.skin.ButtonStyleViewComposite;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class ButtonStylingExam extends Application {

	public static void main(String[] args) {
		launch(args);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new ButtonStyleViewComposite();
		Scene scene = new Scene(root);

//		HBox hboxSample = new HBox(new Button("Button"));
//		hboxSample.setAlignment(Pos.CENTER);
//		root.setTop(hboxSample);
//
//		TextArea textArea = new TextArea();
//		root.setCenter(textArea);
//		Button btnApply = new Button("Apply");
//
//		root.setBottom(btnApply);
//		btnApply.setOnAction(ev -> {
//			String text = textArea.getText();
//			scene.getStylesheets().clear();
//			try {
//				File createUserCustomSkin = SkinManager.getInstance().createUserCustomSkin(text, false);
//				scene.getStylesheets().add(createUserCustomSkin.toURI().toURL().toExternalForm());
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		});

		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
