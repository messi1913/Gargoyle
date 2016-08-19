package com.kyj.fx.voeditor.visual.component.text;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.Paragraph;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 *
 */
public class JavaTextArea extends BorderPane {

	@SuppressWarnings("unused")
	private static Logger LOGGER = LoggerFactory.getLogger(SqlKeywords.class);

	private static String[] KEYWORDS = new String[] { "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
			"const", "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if",
			"implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public",
			"return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try",
			"void", "volatile", "while" };

	private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
	private static final String PAREN_PATTERN = "\\(|\\)";
	private static final String BRACE_PATTERN = "\\{|\\}";
	private static final String BRACKET_PATTERN = "\\[|\\]";
	private static final String SEMICOLON_PATTERN = "\\;";
	private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
	private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

	private static final Pattern PATTERN = Pattern.compile("(?<KEYWORD>" + KEYWORD_PATTERN + ")" + "|(?<PAREN>" + PAREN_PATTERN + ")"
			+ "|(?<BRACE>" + BRACE_PATTERN + ")" + "|(?<BRACKET>" + BRACKET_PATTERN + ")" + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
			+ "|(?<STRING>" + STRING_PATTERN + ")" + "|(?<COMMENT>" + COMMENT_PATTERN + ")");

	private CodeArea codeArea;
	private Label lblLineInfo = new Label();

	public JavaTextArea() {

		codeArea = new CodeArea();
		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

		//		codeArea.richChanges().subscribe(change -> {
		//			codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
		//		});

		codeArea.richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
				.subscribe(change -> {
					codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
				});

		//		codeArea.richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
		//				.successionEnds(Duration.millis(500)).supplyTask(this::computeHighlightingAsync).awaitLatest(codeArea.richChanges())
		//				.filterMap(t -> {
		//					if (t.isSuccess()) {
		//						return Optional.of(t.get());
		//					} else {
		//						t.getFailure().printStackTrace();
		//						return Optional.empty();
		//					}
		//				}).subscribe(this::applyHighlighting);

		codeArea.selectionProperty().addListener((oba, oldval, newval) -> {
			int start = newval.getStart();
			int end = newval.getEnd();
			int caretColumn = codeArea.getCaretColumn();

			String format = String.format("line : %d selectionStart : %d selectionEnd : %d column : %d  anchor : %d",
					codeArea.getCurrentParagraph() + 1, start + 1, end + 1, caretColumn + 1, codeArea.getAnchor());

			lblLineInfo.setText(format);
		});

		lblLineInfo.setPrefHeight(USE_COMPUTED_SIZE);
		lblLineInfo.setMinHeight(USE_COMPUTED_SIZE);
		lblLineInfo.setMaxHeight(USE_COMPUTED_SIZE);

		this.setCenter(codeArea);
		this.setBottom(lblLineInfo);
		// this.getChildren().add(codeArea);

		//		this.getStylesheets().add(JavaKeywordsAsync.class.getResource("java-keywords.css").toExternalForm());
		this.getStylesheets().add(JavaTextArea.class.getResource("java-keywords.css").toExternalForm());

	}

	public void setKeywords(String[] keywords) {
		KEYWORDS = keywords;
		codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
	}

	public void setContent(String content) {
		codeArea.clear();
		codeArea.replaceText(0, 0, content);
	}

	public void appendContent(String content) {
		codeArea.replaceText(0, 0, content);
	}

	protected void setContent(List<String> readLines) {
		codeArea.replaceText(readLines.stream().collect(Collectors.joining("\n")));
	}

	public void setEditable(boolean editable) {
		this.codeArea.setEditable(editable);
	}

	public void selectRange(int anchor, int caretPosition) {
		codeArea.selectRange(anchor, caretPosition);
	}

	public final String getText() {
		return codeArea.getText();
	}

	public final String getText(int start, int end) {
		return codeArea.getText(start, end);
	}

	public String getText(int paragraph) {
		return codeArea.getText(paragraph);
	}

	public Paragraph<Collection<String>> getParagraph(int index) {
		return codeArea.getParagraph(index);
	}

	private static StyleSpans<Collection<String>> computeHighlighting(String text) {
		Matcher matcher = PATTERN.matcher(text);
		int lastKwEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		while (matcher.find()) {
			String styleClass = matcher.group("KEYWORD") != null ? "keyword"
					: matcher.group("PAREN") != null ? "paren"
							: matcher.group("BRACE") != null ? "brace"
									: matcher.group("BRACKET") != null ? "bracket"
											: matcher.group("SEMICOLON") != null ? "semicolon"
													: matcher.group("STRING") != null ? "string"
															: matcher.group("COMMENT") != null ? "comment" : null;
			/* never happens */ assert styleClass != null;
			spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
			spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
			lastKwEnd = matcher.end();
		}
		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
		return spansBuilder.create();
	}

	//	private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
	//		codeArea.setStyleSpans(0, highlighting);
	//	}

}
