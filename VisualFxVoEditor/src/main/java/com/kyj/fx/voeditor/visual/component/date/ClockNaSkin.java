/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.date
 *	작성일   : 2016. 4. 12.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.date;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

/**
 * 애니메이션이 없는 Clock 스킨
 *
 * Clock Not Animation Skin.
 *
 * @author KYJ
 *
 */
public class ClockNaSkin implements Skin<TimeClocker> {

	private static Logger LOGGER = LoggerFactory.getLogger(ClockNaSkin.class);

	/**
	 * 혹시나 시계 중간위치에 글자를 새기고 싶다면 수정.
	 *
	 * @최초생성일 2016. 4. 12.
	 */
	private String brandText = "";

	private static int MINIMUM_LENGTH = 3;
	private static int MAXIMUM_LENGTH = 8;

	private TimeClocker clock;

	private final Group analogueClock;

	private final TextField digitalClock;

	private final VBox layout;

	/* 컨트롤의 위치 좌표를 표현. */
	private DoubleProperty locationX = new SimpleDoubleProperty();
	private DoubleProperty locationY = new SimpleDoubleProperty();

	private final Line hourHand;
	private final Line minuteHand;
	private final Line secondHand;

	/**
	 * format을 바꾸지말것 포멧을 바꾸는 경우
	 *
	 * MINIMUM_LENGTH와 MAXIMUM_LENGTH 속성들의 관련된 로직도 수정이 필요함.
	 *
	 * @최초생성일 2016. 4. 12.
	 */
	private final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(
			/* not allow modify format */"hh:mm:ss");

//	private DoubleProperty initX = new SimpleDoubleProperty(0);
//	private DoubleProperty initY = new SimpleDoubleProperty(0);
//	private ObjectProperty<Line> movingTarget = new SimpleObjectProperty<>();

	/**
	 * @param clock
	 */
	public ClockNaSkin(TimeClocker clock) {
		this.clock = clock;

		// construct the analogueClock pieces.
		final Circle face = new Circle(100, 100, 100);
		face.setId("face");
//		face.setMouseTransparent(false);
//		face.setPickOnBounds(false);
//		face.setFocusTraversable(false);
		final Label brand = new Label(brandText);
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
//		secondHand.setMouseTransparent(true);
//		secondHand.setFocusTraversable(true);
//		secondHand.setPickOnBounds(true);
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
		digitalClock = new TextField(getTimeText());
		digitalClock.setText(getTimeText());
		digitalClock.setId("digitalClock");
		digitalClock.setAlignment(Pos.CENTER);

		// 초기값.
		// digitalClock.setText(getTimeText());

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

		// 시간을 조정.
		updateClockAngle();

		digitalClock.setTextFormatter(new TextFormatter<>(c -> {
			if (c.getControlNewText().isEmpty()) {
				return c;
			}

			/* 날짜형식 validator 추가. */
			ParsePosition parsePosition = new ParsePosition(0);
			try {
				Date object = dateTimeFormatter.parse(c.getControlNewText(), parsePosition);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

			// 따옴이 2개 들어가야돼. 여기서 리턴되는 null의 의미는 값의 수정을 허용하지않겠다는거야.
			long count = c.getControlNewText().chars().filter(ch -> ch == ':').count();
			if (count != 2)
				return null;

			/* 정규식 validator 추가. */
			if (ValueUtil.regexMatch("^(0[0-9]|1[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$", c.getControlNewText()) == null) {
				digitalClock.setStyle("-fx-background-color:red");
			} else {
				digitalClock.setStyle(brandText);
			}
			return c;

		}));

		digitalClock.textProperty().addListener(digitalClockTextChangeListener);

		// layout the scene.
		layout = new VBox();
		layout.getStylesheets().add(getResource("clock.css"));
		layout.getChildren().addAll(analogueClock, digitalClock);
		layout.setAlignment(Pos.CENTER);
//		layout.setFocusTraversable(false);

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


		Rectangle rectangle = new Rectangle(6d, 6d, Color.RED);
		analogueClock.getChildren().add(rectangle);

		layout.setOnMouseEntered(event->{
//			double x = event.getX();
//			double y = event.getY();
//			rectangle.setX(x - 3);
//			rectangle.setY(y - 3);

		});
		layout.setOnMouseClicked(event -> {
//			double sceneX = event.getSceneX();
//			double sceneY = event.getSceneY();
//			double x = event.getX();
//			double y = event.getY();

//			BoundingBox boundingBox = new BoundingBox(x, y, 0, 6, 6, 1);
//			Rectangle rectangle = new Rectangle(6d, 6d, Color.RED);
//			rectangle.setX(x - 3);
//			rectangle.setY(y - 3);
//			analogueClock.getChildren().add(rectangle);
//			KeyFrame keyFrame = new KeyFrame(javafx.util.Duration.seconds(0.5), new KeyValue(rectangle.opacityProperty(), 0d));
//			Timeline timeline = new Timeline(keyFrame);
//			timeline.setOnFinished(ee -> {
//				analogueClock.getChildren().remove(analogueClock);
//			});
//			timeline.play();
//
//			System.out.println(secondHand.intersects(boundingBox));

		});

	}

	private final ChangeListener<? super String> digitalClockTextChangeListener = (oba, old, n) -> {

		try {
			int length = n.length();

			if (MINIMUM_LENGTH <= length && length <= MAXIMUM_LENGTH) {

				// replaceDate(n);
				Date da = dateTimeFormatter.parse(n);
				updateClockAngle(da);
			}

		} catch (Exception e) {
		}

	};

	/**
	 * 시간 UI를 갱신시킨다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 12.
	 */
	private void updateClockAngle() {
		updateClockAngle(null);
	}

	/**
	 * 시간 UI를 갱신시킨다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 12.
	 * @param date
	 */
	private void updateClockAngle(Date date) {
		Calendar calendar = GregorianCalendar.getInstance();

		if (date != null)
			calendar.setTime(date);

		final double seedSecondDegrees = calendar.get(Calendar.SECOND) * (360 / 60);
		final double seedMinuteDegrees = (calendar.get(Calendar.MINUTE) + seedSecondDegrees / 360.0) * (360 / 60);
		final double seedHourDegrees = (calendar.get(Calendar.HOUR) + seedMinuteDegrees / 360.0) * (360 / 12);

		// define rotations to map the analogueClock to the current time.
		final Rotate hourRotate = new Rotate(seedHourDegrees);
		final Rotate minuteRotate = new Rotate(seedMinuteDegrees);
		final Rotate secondRotate = new Rotate(seedSecondDegrees);

		hourHand.getTransforms().clear();
		minuteHand.getTransforms().clear();
		secondHand.getTransforms().clear();

		hourHand.getTransforms().add(hourRotate);
		minuteHand.getTransforms().add(minuteRotate);
		secondHand.getTransforms().add(secondRotate);
	}

	/**
	 * 시간 텍스트 필드의 값을 리턴받는다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 12.
	 * @return
	 */
	public String getText() {
		return digitalClock.getText();
	}

	/**
	 * 시간 데이터를 리턴받는다. 단 날짜가 유효하지않는경우는 null을 리턴함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 12.
	 * @return
	 */
	public Date getDate() {
		try {
			return dateTimeFormatter.parse(getText());
		} catch (ParseException e) {
			return null;
		}
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
		// String ampmString = calendar.get(Calendar.AM_PM) == Calendar.AM ?
		// "AM" : "PM";
		return String.format("%s:%s:%s", hourString, minuteString, secondString);

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

	public final DoubleProperty locationXProperty() {
		return this.locationX;
	}

	public final double getLocationX() {
		return this.locationXProperty().get();
	}

	public final void setLocationX(final double locationX) {
		this.locationXProperty().set(locationX);
	}

	public final DoubleProperty locationYProperty() {
		return this.locationY;
	}

	public final double getLocationY() {
		return this.locationYProperty().get();
	}

	public final void setLocationY(final double locationY) {
		this.locationYProperty().set(locationY);
	}

}
