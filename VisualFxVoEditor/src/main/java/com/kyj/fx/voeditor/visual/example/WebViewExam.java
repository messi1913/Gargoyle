/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 3. 20.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.io.File;

import com.kyj.fx.voeditor.visual.framework.webview.TinymceDeligator;

import javafx.application.Application;
import javafx.application.Platform;
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

		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Platform.runLater(() -> {

					// System.out.println("ACTION READONLY");
					createInstance.setText("<a href='http://www.naver.com'>LINK</a>");
					// createInstance.setReadOnly(true);

				});

			}
		}).start();

		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		//
		// try {
		// Thread.sleep(10000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		//
		// Platform.runLater(() -> {
		//
		// System.out.println("ACTION Editable");
		// createInstance.setReadOnly(false);
		//
		// });
		//
		// }
		// }).start();

	}

	//

	/***********************************************************************************/

	/***********************************************************************************/
	/* 일반API 구현 */

	//
	/***********************************************************************************/
}
