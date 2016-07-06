/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2015. 10. 15.
 *	프로젝트 : VisualFxVoEditor
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.ResultDialog;
import com.kyj.fx.voeditor.visual.component.popup.SelectWorkspaceView;
import com.kyj.fx.voeditor.visual.framework.PrimaryStageCloseable;
import com.kyj.fx.voeditor.visual.main.initalize.Initializable;
import com.kyj.fx.voeditor.visual.main.scanning.ResourceScanner;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;
import com.kyj.fx.voeditor.visual.momory.SkinManager;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;

/**
 * 메인클래스
 *
 * @author KYJ
 *
 */
public class Main extends Application {

	private static final String APPLICATION_TITLE = "Gargoyle";
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	private static List<PrimaryStageCloseable> listeners = new ArrayList<PrimaryStageCloseable>();

	public static void addPrimaryStageCloseListener(PrimaryStageCloseable listener) {
		listeners.add(listener);
	}

	private static EventHandler<WindowEvent> onPrimaryStageCloseRequest = event -> {

		Optional<Pair<String, String>> showYesOrNoDialog = DialogUtil.showYesOrNoDialog("Exit?", "Exit???");

		if (showYesOrNoDialog.isPresent()) {
			Pair<String, String> pair = showYesOrNoDialog.get();
			if ("Y".equals(pair.getValue())) {

				for (PrimaryStageCloseable c : listeners) {
					c.closeRequest();
				}

			} else {
				event.consume();
			}
		}

	};

	public static void main(String[] args) {
		launch(args);
	}

	public Main() {

		/* 2016.2.6 프록시 설정 내용을 ProxyInitializable 구현. */
		try {

			// Initialable databaseInitializer = new DatabaseInitializer();
			// databaseInitializer.initialize();
			// 2016.2.5 위 코드가 아래로 바뀜. 어노테이션 기반 스캔처리. 어노테이션이 붙은항목은 초기화 처리를 한다.
			ResourceScanner.getInstance().initialize(str -> {
				try {
					Initializable newInstance = (Initializable) str.newInstance();
					LOGGER.debug("initialize!!!!");
					if (newInstance != null) {
						newInstance.initialize();
					}
				} catch (Exception e) {
					LOGGER.error(ValueUtil.toString(e));
				}
			});

		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}

		// LOGGER.debug("Resources Info");
		// Map loadingResourceInfo =
		// ResourceInfo.getInstance().getLoadingResourceInfo("log4j.xml");
		// LOGGER.debug(loadingResourceInfo.toString());
	}

	@Override
	public void start(Stage primaryStage) throws IOException {

		// setApplicationUncaughtExceptionHandler();

		primaryStage.setTitle(APPLICATION_TITLE);
		primaryStage.centerOnScreen();
		primaryStage.setOnCloseRequest(onPrimaryStageCloseRequest);
		String baseDir = ResourceLoader.getInstance().get(ResourceLoader.BASE_DIR);

		if (baseDir == null || baseDir.isEmpty() || !new File(baseDir).exists()) {
			SelectWorkspaceView selectWorkspaceView = new SelectWorkspaceView();
			@SuppressWarnings("rawtypes")
			ResultDialog show = selectWorkspaceView.show();

			if (ResultDialog.CANCEL == show.getStatus()) {
				Platform.exit();
				return;
			}
		}

		try {
			setApplicationUncaughtExceptionHandler();
			SharedMemory.init();
			SharedMemory.setPrimaryStage(primaryStage);

			BorderPane mainParent = setNewRootView();

			Scene scene = new Scene(mainParent, 1280, 900);

			scene.getStylesheets().add(SkinManager.getInstance().getSkin());
			primaryStage.setScene(scene);

			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setApplicationUncaughtExceptionHandler() {
		if (Thread.getDefaultUncaughtExceptionHandler() == null) {
			// Register a Default Uncaught Exception Handler for the application
			Thread.setDefaultUncaughtExceptionHandler(new GargoyleUnCaughtExceptionHandler());
		}
	}

	private static class GargoyleUnCaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
		@Override
		public void uncaughtException(Thread t, Throwable e) {
			// Print the details of the exception in SceneBuilder log file
			LOGGER.error("####################");
			LOGGER.error("uncaughtException");
			LOGGER.error(ValueUtil.toString(e));
			LOGGER.error("####################");
		}
	}

	private BorderPane setNewRootView() throws IOException {
		BorderPane root = new BorderPane();
		root.setCenter(loadMainLayout());
		return root;
	}

	BorderPane loadMainLayout() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("layout/SystemLayoutView.fxml"));
		BorderPane borderpane = loader.load();
		return borderpane;
	}
}
