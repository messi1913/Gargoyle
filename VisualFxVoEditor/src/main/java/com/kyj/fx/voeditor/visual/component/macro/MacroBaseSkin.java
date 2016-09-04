/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.macro
 *	작성일   : 2016. 8. 30.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.macro;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.NumberTextField;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import kyj.Fx.dao.wizard.core.util.ValueUtil;

/**
 * SQL 매크로 기능을 지우너하기 위한 베이스 스킨
 *
 * @author KYJ
 *
 */
@SuppressWarnings("restriction")
public class MacroBaseSkin extends BehaviorSkinBase<MacroControl, BehaviorBase<MacroControl>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MacroBaseSkin.class);

	private BorderPane rootLayout;
	private TextArea textArea;
	private TableView<Map<String, String>> tbResult;

	/**
	 * 스레드가 실행중인지 여부에 따라 값이 TRUE/FALSE로 나뉘며
	 * 이 값에 따라 버튼활성화 비활성화 및 스레드의 실행상태가 결정된다.
	 * @최초생성일 2016. 8. 31.
	 */
	private BooleanProperty isStarted = new SimpleBooleanProperty();
	/**
	 * 종료 요청이 성공적으로 진행된경우 호출되는 이벤트 내용이 기술된다.
	 * @최초생성일 2016. 8. 31.
	 */
	public Consumer<Void> onStopSuccessed;
	/**
	 * 처리되는 코드블록에서 에러가 발생되 자동으로 멈춰야되는경우에 호출되는 이벤트 내용이 기술된다.
	 * @최초생성일 2016. 8. 31.
	 */
	public Consumer<Void> onStopErrored;
	/**
	 * 스케줄링 대기 시간 기본값 5초
	 *
	 * @최초생성일 2016. 8. 30.
	 */
	private AtomicInteger sleepSecond = new AtomicInteger(5);

	/**
	 * 키 이벤트 정의
	 * @최초생성일 2016. 8. 31.
	 */
	protected static final List<KeyBinding> DATE_CELL_BINDINGS = new ArrayList<KeyBinding>();

	static {
		DATE_CELL_BINDINGS.add(new KeyBinding(KeyCode.C, "Clipboard").ctrl());
	}

	/**
	 * @param control
	 */
	public MacroBaseSkin(MacroControl control) {
		this(control, new BehaviorBase<MacroControl>(control, DATE_CELL_BINDINGS) {

			/*
			 * 이벤트 함수정의
			 *
			 * (non-Javadoc)
			 * @see com.sun.javafx.scene.control.behavior.BehaviorBase#callAction(java.lang.String)
			 */
			@Override
			protected void callAction(String name) {
				LOGGER.debug("callAction : {} ", name);
				switch (name) {
				case "Clipboard":
					getControl().tbResultOnKeyReleased();
					break;
				}

			}

		});
	}

	/**
	 * @param control
	 * @param behavior
	 */
	protected MacroBaseSkin(MacroControl control, BehaviorBase<MacroControl> behavior) {
		super(control, behavior);

		rootLayout = new BorderPane();
		tbResult = new TableView<>();
		tbResult.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tbResult.getSelectionModel().setCellSelectionEnabled(true);
		//		tbResult.addEventFilter(KeyEvent.KEY_RELEASED, this::tbResultOnKeyReleased);
		textArea = new TextArea();
		SplitPane splitPane = new SplitPane(textArea, tbResult);
		splitPane.setOrientation(Orientation.VERTICAL);
		rootLayout.setCenter(splitPane);

		Button btnStart = createStartButton();
		Button btnStop = createStopButton();
		btnStop.setDisable(true);
		btnStart.addEventHandler(ActionEvent.ACTION, this::btnStartOnAction);
		btnStop.addEventHandler(ActionEvent.ACTION, this::btnStopOnAction);

		Label label = new Label(" Delay Sec : ");
		NumberTextField numberTextField = new NumberTextField(String.valueOf(sleepSecond.get()));
		numberTextField.textProperty().addListener((oba, o, n) -> {
			int parseInt = 5;
			try {
				parseInt = Integer.parseInt(n);
			} catch (NumberFormatException e) {
			}

			if (0 <= parseInt && parseInt < 1001) {
				sleepSecond.getAndSet(parseInt);
			} else {
				DialogUtil.showMessageDialog("입력 제한 [1 ~ 1000]");
				numberTextField.setText(o);
				return;
			}

		});

		HBox buttonBox = new HBox(5, label, numberTextField, btnStart, btnStop);
		buttonBox.setId("btn-box");
		buttonBox.setPadding(new Insets(5));
		buttonBox.setAlignment(Pos.CENTER_RIGHT);
		rootLayout.setBottom(buttonBox);
		getChildren().add(this.rootLayout);

		isStarted.addListener((oba, o, n) -> {
			if (n) {
				btnStart.setDisable(true);
				btnStop.setDisable(false);
			} else {
				btnStart.setDisable(false);
				btnStop.setDisable(true);
			}
		});

		isStarted.set(false);

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 30.
	 * @return
	 */
	public Button createStartButton() {
		return new Button("Start");
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 30.
	 * @return
	 */
	public Button createStopButton() {
		return new Button("Stop");
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 30.
	 * @param e
	 */
	public void btnStartOnAction(ActionEvent e) {
		isStarted.set(true);
		new Thread(target).start();
	}

	/**
	 * Stop 버튼을 크릭한경우 처리할 이벤트
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 30.
	 * @param e
	 */
	public void btnStopOnAction(ActionEvent e) {
		stop();
	}

	/**
	 * 스케줄링 정지 처리.
	 *
	 * 스케줄링 작업이 끝난 이후에 호출되기때문에
	 * 리턴값이 바로출력되지않을 수 있다.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 31.
	 */
	public boolean stop() {
		isStarted.set(false);
		return isStarted.get();
	}

	/********************************
	 * 작성일 : 2016. 8. 30. 작성자 : KYJ
	 *
	 * 스케줄 대기시간을 정한다. 기본단위 초 기본값 5초
	 *
	 * @param second
	 ********************************/
	public void setSleepSecond(int second) {
		this.sleepSecond.getAndSet(second);
	}

	/********************************
	 * 작성일 : 2016. 8. 30. 작성자 : KYJ
	 *
	 * 스케줄 대기시간을 리턴 기본값 5초
	 *
	 * @return
	 ********************************/
	public int getSleepSecond() {
		return this.sleepSecond.get();
	}

	public TableView<Map<String, String>> getTbResult() {
		return tbResult;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 31.
	 * @param initText
	 */
	public void setInitText(String initText) {
		this.textArea.setText(initText);

	}

	/**
	 * Stop요청이 들어온경우 성공적으로 정지되면 호출되는 이벤트 정의
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 31.
	 * @param onStopSuccessed
	 */
	public void setOnStopSuccessed(Consumer<Void> onStopSuccessed) {
		this.onStopSuccessed = onStopSuccessed;
	}

	/**
	 * 에러가 발생되서 정지되는경우 호출되는 이벤트 정의
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 31.
	 * @param onStopErrored
	 */
	public void setOnStopErrored(Consumer<Void> onStopErrored) {
		this.onStopErrored = onStopErrored;
	}

	/**
	 * 스케줄링 클래스 . 인스턴스당 하나
	 *
	 * @최초생성일 2016. 8. 30.
	 */
	private ThreadScheduler target = new ThreadScheduler();

	/***************************
	 *
	 * 스케줄링 처리하기위한 Runnble 클래스 정의
	 *
	 * @author KYJ
	 *
	 ***************************/
	class ThreadScheduler implements Runnable {

		private Consumer<Void> onFinished;

		@Override
		public void run() {

			onFinished = new Consumer<Void>() {

				@Override
				public void accept(Void t) {
					try {
						int second = sleepSecond.get() * 1000;
						Thread.currentThread().sleep(second);
					} catch (InterruptedException e) {
						LOGGER.error(ValueUtil.toString(e));
					}
					LOGGER.debug("Is Restart {} ", isStarted.get());

					//				if (!updateStop.get()) {
					synchronized (isStarted) {

						if (isStarted.get()) {
							LOGGER.debug("ReSchedule");
							new Thread(target).start();
						} else {
							LOGGER.debug("End Schedule.");
							if (onStopSuccessed != null) {
								onStopSuccessed.accept(null);
							}
						}

					}

				}
			};

			//Javafx 스레드로 처리실행.
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					try {
						if (isStarted.get())
							MacroBaseSkin.this.getSkinnable().start(tbResult, textArea.getText());
					} catch (Exception e) {

						//에러가 발생하면 중단함.

						onFinished = null;
						isStarted.set(false);
						DialogUtil.showExceptionDailog(e);
					}

				}
			});

			if (onFinished != null)
				onFinished.accept(null);
			else {
				if (onStopErrored != null)
					onStopErrored.accept(null);
			}

		}

	}

}
