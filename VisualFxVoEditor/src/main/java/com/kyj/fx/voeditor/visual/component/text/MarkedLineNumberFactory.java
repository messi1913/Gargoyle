/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2016. 10. 13.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import java.util.Optional;
import java.util.function.IntFunction;

import org.fxmisc.richtext.Paragraph;
import org.fxmisc.richtext.StyledTextArea;
import org.reactfx.collection.LiveList;
import org.reactfx.value.Val;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;

/**
 * @author KYJ
 *
 */
public class MarkedLineNumberFactory implements IntFunction<Node> {

	/**
	 * @최초생성일 2016. 10. 12.
	 */
	private static final String ADDED_COMMNET_STYLE = "-fx-fill:olivedrab";
	private static final String ERROR_LINE_COMMNET_STYLE = "-fx-fill:red";

	private static final Insets DEFAULT_INSETS = new Insets(0.0, 5.0, 0.0, 5.0);
	private static final Paint DEFAULT_TEXT_FILL = Color.web("#666");
	private static final Font DEFAULT_FONT = Font.font("monospace", FontPosture.ITALIC, 13);
	private static final Background DEFAULT_BACKGROUND = new Background(new BackgroundFill(Color.web("#ddd"), null, null));

	public static IntFunction<Node> get(StyledTextArea<?> area) {
		return get(area, digits -> "%0" + digits + "d");
	}

	public static IntFunction<Node> get(StyledTextArea<?> area, IntFunction<String> format) {
		return new MarkedLineNumberFactory(area, format);
	}

	private final Val<Integer> nParagraphs;
	private final IntFunction<String> format;
	private StyledTextArea<?> area;
	private int appendLineKeywordLength = JavaCodeAreaHelper.appendLineKeyword.length();
	private ObjectProperty<LineMapper<Boolean>> lineMarkFactory = new SimpleObjectProperty<>();

	private MarkedLineNumberFactory(StyledTextArea<?> area, IntFunction<String> format) {
		this.area = area;
		nParagraphs = LiveList.sizeOf(area.getParagraphs());
		this.format = format;
		lineMarkFactory.set(lineMarkFactory());
	}

	public static interface LineMapper<T> {

		public T map(int row, Paragraph<?> pra);

	}

	protected LineMapper<Boolean> lineMarkFactory() {

		return new LineMapper<Boolean>() {

			@Override
			public Boolean map(int row, Paragraph<?> pra) {

				String substring = pra.substring(0, appendLineKeywordLength);
				if (JavaCodeAreaHelper.appendLineKeyword.equals(substring)) {
					return true;
				}
				return false;
			}
		};
	}

	@Override
	public Node apply(int idx) {
		Val<String> formatted = nParagraphs.map(n -> {
			return format(idx + 1, n);
		});

		Paragraph<?> paragraph = this.area.getParagraph(idx);

		//		Optional<Boolean> findAny = paragraph.chars().limit(1L).mapToObj(v -> {
		//
		//			String substring = paragraph.substring(0, appendLineKeywordLength);
		//			if (JavaCodeAreaHelper.appendLineKeyword.equals(substring)) {
		//				return true;
		//			}
		//
		//			return false;
		//		}).findAny();

		Label lineNo = new Label();
		lineNo.setFont(DEFAULT_FONT);
		lineNo.setBackground(DEFAULT_BACKGROUND);
		lineNo.setTextFill(DEFAULT_TEXT_FILL);
		lineNo.setPadding(DEFAULT_INSETS);
		lineNo.getStyleClass().add("lineno");

		Rectangle rectangle = new Rectangle();
		rectangle.setWidth(10d);
		rectangle.setHeight(10d);
		if ( /*findAny.isPresent() && findAny.get()*/ lineMarkFactory.get().map(idx, paragraph))
			rectangle.setStyle(ADDED_COMMNET_STYLE);
		else
			rectangle.setStyle("-fx-fill:transparent");
		lineNo.setGraphic(rectangle);

		// bind label's text to a Val that stops observing area's paragraphs
		// when lineNo is removed from scene
		lineNo.textProperty().bind(formatted.conditionOnShowing(lineNo));

		return lineNo;
	}

	private String format(int x, int max) {
		int digits = (int) Math.floor(Math.log10(max)) + 1;
		return String.format(format.apply(digits), x);
	}
}
