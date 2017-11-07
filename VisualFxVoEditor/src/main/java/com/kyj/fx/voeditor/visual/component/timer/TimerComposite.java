/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.timer
 *	작성일   : 2017. 6. 16.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.timer;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.fxloader.FxPostInitialize;
import com.kyj.fx.voeditor.visual.framework.thread.DemonTimerFactory;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "TimerView.fxml", isSelfController = true, css = "TimerView.css")
public class TimerComposite extends BorderPane {
	private static Logger LOGGER = LoggerFactory.getLogger(TimerComposite.class);

	@FXML
	private Label lblHour, lblMin, lblSec;

	public TimerComposite() {
		FxUtil.loadRoot(TimerComposite.class, this, err -> {
			LOGGER.error(ValueUtil.toString(err));
		});
	}

	private LongProperty startTimeMills = new SimpleLongProperty(-1);
	private LongProperty currentTimeMills = new SimpleLongProperty(-1);

	private AtomicBoolean start = new AtomicBoolean();
	private AtomicBoolean pause = new AtomicBoolean();

	private Timer timerInstance;

	private Calendar instance = Calendar.getInstance();

	@FXML
	public void initialize() {

		currentTimeMills.addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (start.get()) {

					Platform.runLater(() -> {
						long value = newValue.longValue() - startTimeMills.get();
						instance.setTimeInMillis(value);
						int hour = instance.get(Calendar.MINUTE);
						int min = instance.get(Calendar.SECOND);
						int sec = instance.get(Calendar.MILLISECOND);

						lblHour.setText(String.format("%02d", hour));
						lblMin.setText(String.format("%02d", min));
						lblSec.setText(String.format("%03d", sec));
					});

				}
			}

		});
	}

	@FxPostInitialize
	public void postInit() {

	}

	public void reset() {
		stop();
		lblHour.setText("00");
		lblMin.setText("00");
		lblSec.setText("00");
		currentTimeMills.set(-1);
	}

	public void start() {

		if (timerInstance != null)
			timerInstance.cancel();

		start.set(true);
		pause.set(false);
		timerInstance = DemonTimerFactory.newInsance("Timer-Thread");
		startTimeMills.set(System.currentTimeMillis());
		timerInstance.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {

				while (pause.get()) {
					///////
				}
				currentTimeMills.set(System.currentTimeMillis());

			}
		}, 1, 1);

	}

	public void stop() {
		timerInstance.cancel();
		start.set(false);
	}

	public void pause() {
		start.set(false);
		pause.set(true);
	}

	public void resume() {
		start.set(true);
		pause.set(false);
	}
}
