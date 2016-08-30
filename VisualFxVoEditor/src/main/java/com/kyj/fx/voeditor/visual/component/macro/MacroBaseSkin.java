/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.macro
 *	작성일   : 2016. 8. 30.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.macro;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

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
	private BooleanProperty isStarted = new SimpleBooleanProperty();

	/**
	 * 스케줄링 대기 시간 기본값 5초
	 * 
	 * @최초생성일 2016. 8. 30.
	 */
	private AtomicInteger sleepSecond = new AtomicInteger(5);
	/**
	 * @param control
	 */
	public MacroBaseSkin(MacroControl control) {

		this(control, new BehaviorBase<>(control, Collections.<KeyBinding> emptyList()));
	}

	/**
	 * @param control
	 * @param behavior
	 */
	protected MacroBaseSkin(MacroControl control, BehaviorBase<MacroControl> behavior) {
		super(control, behavior);

		rootLayout = new BorderPane();
		tbResult = new TableView<>();
		textArea = new TextArea();
		SplitPane splitPane = new SplitPane(textArea, tbResult);
		splitPane.setOrientation(Orientation.VERTICAL);
		rootLayout.setCenter(splitPane);

		Button btnStart = createStartButton();
		Button btnStop = createStopButton();
		btnStop.setDisable(true);
		btnStart.addEventHandler(ActionEvent.ACTION, this::btnStartOnAction);
		btnStop.addEventHandler(ActionEvent.ACTION, this::btnStopOnAction);

		Label label = new Label(" Wait Sec : 5 ");
//		NumberTextField numberTextField = new NumberTextField(String.valueOf(sleepSecond.get()));

		HBox buttonBox = new HBox(5,  label,/* numberTextField,*/ btnStart, btnStop);
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
		isStarted.set(false);
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
						e.printStackTrace();
					}
					LOGGER.debug("Is Restart {} ", isStarted.get());

					//				if (!updateStop.get()) {

					if (isStarted.get()) {
						LOGGER.debug("ReSchedule");
						ThreadScheduler newTarget = ThreadScheduler.this;
						new Thread(newTarget).start();
					} else {
						LOGGER.debug("End Schedule.");
					}
				}
			};
			
			
			//Javafx 처리실행.
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					try {
						MacroBaseSkin.this.getSkinnable().start(tbResult, textArea.getText());
					} catch (Exception e) {

						//에러가 발생하면 로그를 남기고 중단함.
						//						LOGGER.error(ValueUtil.toString(e));
						onFinished = null;
						isStarted.set(false);

						DialogUtil.showExceptionDailog(e);
					}

				}
			});

			if (onFinished != null)
				onFinished.accept(null);
		}

	}
}
