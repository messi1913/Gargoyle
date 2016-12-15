/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 4. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.jfoenix.transitions.JFXFillTransition;
import com.kyj.fx.voeditor.visual.component.LettersPane;
import com.kyj.fx.voeditor.visual.framework.animation.BlinkBorderTransition;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import jidefx.animation.AnimationType;
import jidefx.animation.AnimationUtils;

/**
 * @author KYJ
 *
 */
public class LetterExam extends Application {

	/**
	 */
	public LetterExam() {

		// TODO Auto-generated constructor stub

	}
	/**********************************************************************************************/
	/* 이벤트 처리항목 기술 */
	// TODO Auto-generated constructor stub
	/**********************************************************************************************/

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 4.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane borderPane = new BorderPane();
		Button button = new Button("Play");
		LettersPane lettersPane = new LettersPane();
		borderPane.setTop(new StackPane(button));
		borderPane.setCenter(lettersPane);

		primaryStage.setScene(new Scene(borderPane));
		primaryStage.show();

		//		lettersPane.setStyle("-fx-border-color : blue; -fx-border-width : 1px");

		//		button.setCache(true);
		//		button.setCacheHint(CacheHint.SPEED);

		//		Border fromBorder = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.DASHED, CornerRadii.EMPTY, BorderWidths.DEFAULT));
		//		Border toBorder = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.DASHED, CornerRadii.EMPTY, BorderWidths.DEFAULT));

		//		StringBinding createStringBinding = Bindings.createStringBinding(new Callable<String>() {
		//
		//			@Override
		//			public String call() throws Exception {
		//				return "-fx-border-color : red ; -fx-border-width : 1px";
		//			}
		//		}, button.styleProperty());

		Border border = button.getBorder();
		button.setOnAction(ev -> {

//						AnimationUtils.createTransition(button, AnimationType.BUBBLE).play();

			new BlinkBorderTransition(lettersPane, border, Color.RED).play();

//			new JFXFillTransition(Duration.seconds(1), button, Color.RED, Color.BLUE).play();

			//			Timeline timeline = new Timeline();
			//			timeline.setCycleCount(5);
			//			timeline.setAutoReverse(true);
			//			timeline.setDelay(Duration.seconds(0.2d));

			//
			//						KeyFrame keyFrame = new KeyFrame(Duration.millis(500),
			//								new KeyValue(button.styleProperty(), "-fx-border-color : red ; -fx-border-width : 1px"));
			//						KeyFrame keyFrame2 = new KeyFrame(Duration.millis(500), new KeyValue(button.styleProperty(), ""));
			//
			//						KeyValue from = new KeyValue(button.styleProperty(), "-fx-border-color : red ; -fx-border-width : 1px");
			//						KeyValue to = new KeyValue(button.styleProperty(), "");

			//			AnimationUtils.createTransition(button, AnimationType.FLIP_IN_X).play();;

			//			KeyValue from = new KeyValue(button.borderProperty(), fromBorder);
			//			KeyValue from2 = new KeyValue(button.opacityProperty(), 0);
			//			KeyValue from3 = new KeyValue(button.styleProperty(), "");
			//
			//			KeyFrame fromKeyFrame = new KeyFrame(Duration.millis(500), /*from,  from2,*/ from3);
			//
			//			KeyValue to = new KeyValue(button.borderProperty(), toBorder);
			//			KeyValue to2 = new KeyValue(button.opacityProperty(), 1);
			//			KeyValue to3 = new KeyValue(button.styleProperty(), "-fx-border-color : red ; -fx-border-width : 1px");
			//			KeyFrame toKeyFrame = new KeyFrame(Duration.millis(500), /*to , to2, */to3);
			//
			//			timeline.getKeyFrames().addAll(fromKeyFrame, toKeyFrame);
			//
			//			SequentialTransition transition = new SequentialTransition(button, timeline);
			//			transition.play();
		});

	}

	/**********************************************************************************************/
	/* 그 밖의 API 기술 */
	// TODO Auto-generated constructor stub
	/**********************************************************************************************/
}
