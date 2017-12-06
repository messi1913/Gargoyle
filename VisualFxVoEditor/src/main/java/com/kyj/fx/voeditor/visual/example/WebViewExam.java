/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 3. 20.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.framework.webview.TinymceDeligator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

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

		TinymceDeligator createInstance = TinymceDeligator.createInstance();
		// createInstance.setReadOnly(true);
		WebView view = createInstance.getWebView();
		WebEngine engine = view.getEngine();

		engine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue ov, State oldState, State newState) {
				if (newState == Worker.State.SUCCEEDED) {
					primaryStage.setTitle(engine.getLocation());
				}
			}
		});

		primaryStage.setScene(new Scene(new BorderPane(view), 1200, 700));
		primaryStage.show();

		Platform.runLater(() -> {
			createInstance.setText("hi");
		});
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Platform.runLater(() -> {
					createInstance.setText("<a href='http://www.naver.com'>LINK</a>");
				});

			}
		}).start();

	}

}
