/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2016. 3. 31.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.util.Random;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * @author KYJ
 *
 */
public class LettersPane extends Region {

	static Font defaultFont = Font.font("굴림");
	static String family = defaultFont.getFamily();

	private static final Font FONT_DEFAULT = new Font(defaultFont.getFamily(), 40);
	private static final Random RANDOM = new Random();
	/**
	 * if you use this, you will be see changed control's speed moving.
	 *
	 * @최초생성일 2016. 3. 31.
	 */
	private static final Interpolator INTERPOLATOR = Interpolator.SPLINE(0.295, 0.800, 0.305, 1.000);
	private Text pressText;

	public LettersPane() {

		System.out.println("font ::: " + defaultFont);
		System.out.println("family ::: " + family);

		setId("LettersPane");
		setPrefSize(480, 480);
		setFocusTraversable(true);
		setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent me) {
				requestFocus();
				me.consume();
			}
		});

		// new Thread(() -> {
		// Scanner scan = new Scanner(System.in);
		// while (scan.hasNext()) {
		// createLetter(scan.nextLine());
		// }
		// }).start();

		// setOnKeyReleased(ke -> {
		//
		// // System.out.println(ke.getCharacter());
		// System.out.println(ke.getText());
		// createLetter(ke.getText());
		// ke.consume();
		// });
		setOnKeyPressed(ke -> {
			System.out.println(ke.getCharacter());
			System.out.println(ke.getText());
			createLetter(ke.getText());
			ke.consume();
		});
		// create press keys text
		pressText = new Text("Press Keys");
		pressText.setTextOrigin(VPos.TOP);
		pressText.setFont(new Font(family, 40));
		pressText.setLayoutY(5);
		pressText.setFill(Color.rgb(80, 80, 80));

		DropShadow effect = new DropShadow();
		effect.setRadius(0);
		effect.setOffsetY(1);
		effect.setColor(Color.WHITE);
		pressText.setEffect(effect);
		getChildren().add(pressText);
	}

	@Override
	protected void layoutChildren() {
		// center press keys text
		pressText.setLayoutX((getWidth() - pressText.getLayoutBounds().getWidth()) / 2);
	}

	public void createLetter(String c) {
		final Text letter = new Text(c);
		letter.setFill(Color.BLACK);
		letter.setFont(FONT_DEFAULT);
		letter.setTextOrigin(VPos.TOP);
		letter.setTranslateX((getWidth() - letter.getBoundsInLocal().getWidth()) / 2);
		letter.setTranslateY((getHeight() - letter.getBoundsInLocal().getHeight()) / 2);
		getChildren().add(letter);

		createRandomTimeLine(letter).play();
	}

	private Timeline createRandomTimeLine(Node node) {
		// over 3 seconds move letter to random position and fade it out
		final Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(3), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// we are done remove us from scene
				getChildren().remove(node);
			}
		}, new KeyValue(node.translateXProperty(), getRandom(0.0f, getWidth() - node.getBoundsInLocal().getWidth()), INTERPOLATOR),
				new KeyValue(node.translateYProperty(), getRandom(0.0f, getHeight() - node.getBoundsInLocal().getHeight()), INTERPOLATOR),
				new KeyValue(node.opacityProperty(), 0f)));
		return timeline;
	}

	//	private Timeline createCurveTimeLine(Node node) {
	//		// over 3 seconds move letter to random position and fade it out
	//		final Timeline timeline = new Timeline();
	//		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(3), new EventHandler<ActionEvent>() {
	//			@Override
	//			public void handle(ActionEvent event) {
	//				// we are done remove us from scene
	//				getChildren().remove(node);
	//			}
	//		}, new KeyValue(node.translateXProperty(), node.translateXProperty().get(), INTERPOLATOR),
	//			new KeyValue(node.translateYProperty(), 100f, INTERPOLATOR), new KeyValue(node.opacityProperty(), 0f)));
	//
	//		new KeyFrame(Duration.millis(0.5), new KeyFrame(time, values))
	//
	//
	//
	//		return timeline;
	//
	//
	//	}

	private Timeline createUpperTimeLine(Node node) {
		// over 3 seconds move letter to random position and fade it out
		final Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(3), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// we are done remove us from scene
				getChildren().remove(node);
			}
		}, new KeyValue(node.translateXProperty(), node.translateXProperty().get(), INTERPOLATOR),
				new KeyValue(node.translateYProperty(), 100f, INTERPOLATOR), new KeyValue(node.opacityProperty(), 0f)));
		return timeline;
	}

	private static float getRandom(double min, double max) {
		return (float) (RANDOM.nextFloat() * (max - min) + min);
	}
}
