/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 3. 20.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

/**
 * TODO 클래스 역할
 *
 * @author KYJ
 *
 */
public class WebViewExam extends Application {

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

		WebView view = new WebView();
		WebEngine engine = view.getEngine();

		engine.setJavaScriptEnabled(true);
		engine.setCreatePopupHandler(new Callback<PopupFeatures, WebEngine>() {

			@Override
			public WebEngine call(PopupFeatures p) {

				Stage stage = new Stage(StageStyle.UTILITY);
				WebView wv2 = new WebView();
				VBox vBox = new VBox(5);
				vBox.getChildren().add(wv2);
				vBox.getChildren().add(new Button("업로딩"));
				wv2.getEngine().setJavaScriptEnabled(true);
				stage.setScene(new Scene(vBox));
				stage.show();
				return wv2.getEngine();
			}
		});

		engine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue ov, State oldState, State newState) {
				if (newState == Worker.State.SUCCEEDED) {
					primaryStage.setTitle(engine.getLocation());
				}
			}
		});

		engine.setConfirmHandler(new Callback<String, Boolean>() {

			@Override
			public Boolean call(String param) {
				System.out.println("confirm handler : " + param);
				return true;
			}
		});

		engine.setOnAlert((WebEvent<String> wEvent) -> {
			System.out.println("Alert Event  -  Message:  " + wEvent.getData());
		});
		engine.load("http://blog.daum.net/photoon/7920766");
		primaryStage.setScene(new Scene(new BorderPane(view), 1200, 700));
		primaryStage.show();

	}

	// 

	/***********************************************************************************/

	/***********************************************************************************/
	/* 일반API 구현 */

	// 
	/***********************************************************************************/
}
