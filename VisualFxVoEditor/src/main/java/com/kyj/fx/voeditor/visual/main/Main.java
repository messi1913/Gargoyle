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

	/********************************
	 * 작성일 : 2016. 7. 26. 작성자 : KYJ
	 *
	 * 프로그램 종료시 처리할 내용을 구현할 이벤트를 등록하는 함수.
	 * 
	 * @param listener
	 ********************************/
	public static void addPrimaryStageCloseListener(PrimaryStageCloseable listener) {
		listeners.add(listener);
	}

	/**
	 * 어플리케이션이 종료될때 처리할 이벤트를 구현한다.
	 * 
	 * PrimaryStageCloseable을 구현하고 addPrimaryStageCloseListener(PrimaryStageCloseable) 함수에 리스너를 등록한 함수는 프로그램 종료시 처리할 이벤트가 실행되게 된다.
	 * 
	 * 주로 화면에 대한 Resource 해제등의 로직이 대상이됨.
	 * 
	 * @최초생성일 2016. 7. 26.
	 */
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
	}

	@Override
	public void start(Stage primaryStage) throws IOException {

		// setApplicationUncaughtExceptionHandler();

		//어플리케이션 타이틀 지정
		primaryStage.setTitle(APPLICATION_TITLE);

		//화면 중앙 위치.
		primaryStage.centerOnScreen();

		//메인 스테이지 클로즈 이벤트 구현.
		primaryStage.setOnCloseRequest(onPrimaryStageCloseRequest);

		/*[시작 ]초기 워크스페이스 선택 지정. */
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
		/*[끝 ]초기 워크스페이스 선택 지정. */

		try {

			//예상치 못한 에외에 대한 대비 로직구현. 
			setApplicationUncaughtExceptionHandler();

			//클래스 로딩같은 어플리케이션이 메모리에 로딩됨과 동기에 무거운 처리를 비동기로 로딩하는 로직이 구현되있음.
			SharedMemory.init();

			//PrimaryStage를 공유변수로 지정하기 위한 로직 처리. 
			SharedMemory.setPrimaryStage(primaryStage);

			//Main Application을 로드
			BorderPane mainParent = setNewRootView();

			Scene scene = new Scene(mainParent, 1280, 900);

			scene.getStylesheets().add(SkinManager.getInstance().getSkin());
			primaryStage.setScene(scene);

			primaryStage.show();

		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}
	}

	/********************************
	 * 작성일 : 2016. 7. 26. 작성자 : KYJ
	 *
	 * 예측되지 못한 예외처리에 대한 로직처리가 입력된다.
	 * 
	 * ex) 정규식 무한루프등(StackOverflow)
	 ********************************/
	private void setApplicationUncaughtExceptionHandler() {
		if (Thread.getDefaultUncaughtExceptionHandler() == null) {
			// Register a Default Uncaught Exception Handler for the application
			Thread.setDefaultUncaughtExceptionHandler(new GargoyleUnCaughtExceptionHandler());
		}
	}

	/***************************
	 *
	 * 예측하지 못한 예외에 대한 실제 로직이 구현됨.
	 * 
	 * @author KYJ
	 *
	 ***************************/
	private static class GargoyleUnCaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
		@Override
		public void uncaughtException(Thread t, Throwable e) {
			// Print the details of the exception in SceneBuilder log file
			LOGGER.error("####################");
			LOGGER.error("uncaughtException");
			LOGGER.error(ValueUtil.toString(e));
			LOGGER.error("####################");

			StackTraceElement[] sts = t.getStackTrace();
			if (sts != null) {
				for (StackTraceElement s : sts) {

					LOGGER.error("className : {} method : {}  line : {} ", s.getClassName(), s.getMethodName(), s.getLineNumber());
				}
			}

		}
	}

	/********************************
	 * 작성일 : 2016. 7. 26. 작성자 : KYJ
	 *
	 * Appplication 메인 로직
	 * 
	 * @return
	 * @throws IOException
	 ********************************/
	private BorderPane setNewRootView() throws IOException {
		BorderPane root = new BorderPane();
		root.setCenter(loadMainLayout());
		return root;
	}

	/********************************
	 * 작성일 : 2016. 7. 26. 작성자 : KYJ
	 *
	 * Appliucation 메인로직.
	 * 
	 * @return
	 * @throws IOException
	 ********************************/
	BorderPane loadMainLayout() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("layout/SystemLayoutView.fxml"));
		BorderPane borderpane = loader.load();
		return borderpane;
	}
}
