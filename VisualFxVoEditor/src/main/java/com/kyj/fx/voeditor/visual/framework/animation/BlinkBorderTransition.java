/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.animation
 *	작성일   : 2016. 12. 15.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.animation;

import com.fxexperience.javafx.animation.CachedTimelineTransition;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.CacheHint;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * @author KYJ
 *
 */
public class BlinkBorderTransition extends CachedTimelineTransition {

	/**
	 * @최초생성일 2016. 12. 15.
	 */
	private static final BorderWidths WIDTHS = new BorderWidths(1d);

	private Color blinkColor;

	public BlinkBorderTransition(final Region node, Border def, Color blinkColor) {
		super(node, new Timeline(create(node, blinkColor)));

		node.setCache(true);
		node.setCacheHint(CacheHint.SPEED);

		setCycleCount(3);
		setCycleDuration(Duration.millis(1000));
		setDelay(Duration.millis(500));

		setOnFinished(ev -> {
			node.setBorder(def);
		});
	}

	public static KeyFrame[] create(final Region node, Color blinkColor) {

		KeyFrame[] frames = new KeyFrame[] {

				new KeyFrame(Duration.millis(50),
						keyval(node, new Border(new BorderStroke(blinkColor, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, WIDTHS)))),

				new KeyFrame(Duration.millis(250),
						keyval(node, new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, WIDTHS)))),

				new KeyFrame(Duration.millis(500),
						keyval(node, new Border(new BorderStroke(blinkColor, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, WIDTHS)))),

				new KeyFrame(Duration.millis(750),
						keyval(node, new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, WIDTHS)))),

		};

		return frames;
	}

	public static KeyValue keyval(final Region node, Border b) {
		return new KeyValue(node.borderProperty(), b);
	}

	//	public static KeyValue end(final Region node, Border start) {
	//		return new KeyValue(node.borderProperty(), start);
	//	}

	public static Border start() {
		return new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
	}

	public static Border end() {
		return new Border(new BorderStroke(Color.YELLOW, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
	}

}
