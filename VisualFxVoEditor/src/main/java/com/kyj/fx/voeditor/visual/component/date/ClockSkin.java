/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.date
 *	작성일   : 2016. 4. 12.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.date;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * @author KYJ
 *
 */
public class ClockSkin implements Skin<TimeClocker> {

	private static Logger LOGGER = LoggerFactory.getLogger(ClockSkin.class);

	private TimeClocker clock;

	private final Group analogueClock;

	private final Label digitalClock;

	private final VBox layout;

	/**
	 * 애니메이션 처리 속성을 지정함.
	 *
	 * @최초생성일 2016. 4. 12.
	 */
	private BooleanProperty autoAnimation = new SimpleBooleanProperty(false);

	DoubleProperty locationX = new SimpleDoubleProperty();
	DoubleProperty locationY = new SimpleDoubleProperty();

	private final Line hourHand;
	private final Line minuteHand;
	private final Line secondHand;

	/**
	 * @param clock
	 */
	public ClockSkin(TimeClocker clock) {
		this.clock = clock;

		// construct the analogueClock pieces.
		final Circle face = new Circle(100, 100, 100);
		face.setId("face");
		final Label brand = new Label("");
		brand.setId("brand");
		brand.layoutXProperty().bind(face.centerXProperty().subtract(brand.widthProperty().divide(2)));
		brand.layoutYProperty().bind(face.centerYProperty().add(face.radiusProperty().divide(2)));
		hourHand = new Line(0, 0, 0, -50);
		hourHand.setTranslateX(100);
		hourHand.setTranslateY(100);
		hourHand.setId("hourHand");
		minuteHand = new Line(0, 0, 0, -75);
		minuteHand.setTranslateX(100);
		minuteHand.setTranslateY(100);
		minuteHand.setId("minuteHand");
		secondHand = new Line(0, 15, 0, -88);
		secondHand.setTranslateX(100);
		secondHand.setTranslateY(100);
		secondHand.setId("secondHand");
		final Circle spindle = new Circle(100, 100, 5);
		spindle.setId("spindle");
		Group ticks = new Group();
		for (int i = 0; i < 12; i++) {
			Line tick = new Line(0, -83, 0, -93);
			tick.setTranslateX(100);
			tick.setTranslateY(100);
			tick.getStyleClass().add("tick");
			tick.getTransforms().add(new Rotate(i * (360 / 12)));
			ticks.getChildren().add(tick);
		}
		analogueClock = new Group(face, brand, ticks, spindle, hourHand, minuteHand, secondHand);

		// construct the digitalClock pieces.
		digitalClock = new Label();
		digitalClock.setId("digitalClock");

		// determine the starting time.
		Calendar calendar = GregorianCalendar.getInstance();
		final double seedSecondDegrees = calendar.get(Calendar.SECOND) * (360 / 60);
		final double seedMinuteDegrees = (calendar.get(Calendar.MINUTE) + seedSecondDegrees / 360.0) * (360 / 60);
		final double seedHourDegrees = (calendar.get(Calendar.HOUR) + seedMinuteDegrees / 360.0) * (360 / 12);

		// define rotations to map the analogueClock to the current time.
		final Rotate hourRotate = new Rotate(seedHourDegrees);
		final Rotate minuteRotate = new Rotate(seedMinuteDegrees);
		final Rotate secondRotate = new Rotate(seedSecondDegrees);
		hourHand.getTransforms().add(hourRotate);
		minuteHand.getTransforms().add(minuteRotate);
		secondHand.getTransforms().add(secondRotate);

		// the hour hand rotates twice a day.
		final Timeline hourTime = new Timeline(
				new KeyFrame(Duration.hours(12), new KeyValue(hourRotate.angleProperty(), 360 + seedHourDegrees, Interpolator.LINEAR)));

		// the minute hand rotates once an hour.
		final Timeline minuteTime = new Timeline(new KeyFrame(Duration.minutes(60),
				new KeyValue(minuteRotate.angleProperty(), 360 + seedMinuteDegrees, Interpolator.LINEAR)));

		// move second hand rotates once a minute.
		final Timeline secondTime = new Timeline(new KeyFrame(Duration.seconds(60),
				new KeyValue(secondRotate.angleProperty(), 360 + seedSecondDegrees, Interpolator.LINEAR)));

		// the digital clock updates once a second.
		final Timeline digitalTime = new Timeline(new KeyFrame(Duration.seconds(0), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				digitalClock.setText(getTimeText());
			}
		}), new KeyFrame(Duration.seconds(1)));

		// 초기값.
		digitalClock.setText(getTimeText());

		// time never ends.
		// hourTime.setCycleCount(Animation.INDEFINITE);
		// minuteTime.setCycleCount(Animation.INDEFINITE);
		// secondTime.setCycleCount(Animation.INDEFINITE);
		// digitalTime.setCycleCount(Animation.INDEFINITE);

		// hourTime.setOnFinished(event -> {
		// if (autoAnimation.get())
		// hourTime.play();
		// });
		//
		// minuteTime.setOnFinished(event -> {
		// if (autoAnimation.get())
		// minuteTime.play();
		// });
		secondTime.setOnFinished(event -> {
			if (autoAnimation.get())
				secondTime.play();
		});

		// digitalTime.setOnFinished(event -> {
		// if (autoAnimation.get())
		// digitalTime.play();
		// });

		// start the analogueClock.
		// digitalTime.play();
		secondTime.play();
		// minuteTime.play();
		// hourTime.play();

		// stage.initStyle(StageStyle.TRANSPARENT);

		// add a glow effect whenever the mouse is positioned over the clock.
		final Glow glow = new Glow();
		analogueClock.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				analogueClock.setEffect(glow);
			}
		});
		analogueClock.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				analogueClock.setEffect(null);
			}
		});

		// layout the scene.
		layout = new VBox();
		layout.getStylesheets().add(getResource("clock.css"));
		layout.getChildren().addAll(analogueClock, digitalClock);
		layout.setAlignment(Pos.CENTER);

		// 화면 좌표 위치를 조절함.
		Scene scene = clock.getScene();
		scene.widthProperty().addListener((oba, oldval, newval) -> {
			double _width = newval.doubleValue() / 2;
			double _centerX = scene.getWidth();
			double locationX = _width + _centerX;
			layout.setLayoutX(locationX);
		});

		scene.heightProperty().addListener((oba, oldval, newval) -> {
			double _height = newval.doubleValue() / 2;
			double _centerY = scene.getHeight();
			double locationY = _height + _centerY;
			layout.setLayoutY(locationY);
		});

	}

	/**
	 * 현재 시간 텍스트 리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 12.
	 * @return
	 */
	private String getTimeText() {
		Calendar calendar = GregorianCalendar.getInstance();
		String hourString = pad(2, '0', calendar.get(Calendar.HOUR) == 0 ? "12" : calendar.get(Calendar.HOUR) + "");
		String minuteString = pad(2, '0', calendar.get(Calendar.MINUTE) + "");
		String secondString = pad(2, '0', calendar.get(Calendar.SECOND) + "");
		String ampmString = calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
		return hourString + ":" + minuteString + ":" + secondString + " " + ampmString;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public TimeClocker getSkinnable() {
		return clock;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Node getNode() {
		return layout;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void dispose() {

	}

	private String pad(int fieldWidth, char padChar, String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = s.length(); i < fieldWidth; i++) {
			sb.append(padChar);
		}
		sb.append(s);

		return sb.toString();
	}

	static String getResource(String path) {
		return TimeClocker.class.getResource(path).toExternalForm();
	}

	// records relative x and y co-ordinates.
	class Delta {
		double x, y;
	}

}
