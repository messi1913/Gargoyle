package com.kyj.fx.voeditor.visual.component.text;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
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

import com.kyj.fx.voeditor.visual.component.popup.TextSearchAndReplaceView;
import com.kyj.fx.voeditor.visual.framework.thread.ExecutorDemons;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Pair;

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
	private ExecutorService executor;

	private static final String CHARACTERS_MATCH = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";

	// 선택 범위 지정
	private EventHandler<? super MouseEvent> defaultSelectionHandler = event -> {
		if (event.getClickCount() == 1) {
			// codeArea.setStyleSpans(0,
			// groupBackgroundColor(codeArea.getText(),
			// codeArea.getCaretPosition()));
		} else if (event.getClickCount() == 2) {
			String selectedText = codeArea.getSelectedText();
			if (ValueUtil.isNotEmpty(selectedText)) {
				IndexRange selection = codeArea.getSelection();
				String ltrimText = selectedText.replaceAll("^\\s+", "");
				String firstStr = ltrimText.substring(0, 1).replaceAll(CHARACTERS_MATCH, "");
				int start = selection.getStart();
				int end = selection.getEnd();
				codeArea.selectRange(start + (selectedText.length() - ltrimText.length() + 1 - firstStr.length()), end);
			}
		}
	};

	public JavaTextArea() {
		executor = ExecutorDemons.newSingleThreadExecutor();
		//
		//		RuntimeClassUtil.addShutdownHook(() -> {
		//
		//			if (!executor.isTerminated())
		//				executor.shutdownNow();
		//
		//		});

		codeArea = new CodeArea();
		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

		//		codeArea.richChanges().subscribe(change -> {
		//			codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
		//		});

		//		codeArea.richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
		//				.subscribe(change -> {
		//					codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
		//				});

		codeArea.richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
				.successionEnds(Duration.ofMillis(500)).supplyTask(this::computeHighlightingAsync).awaitLatest(codeArea.richChanges())
				.filterMap(t -> {
					if (t.isSuccess()) {
						return Optional.of(t.get());
					} else {
						t.getFailure().printStackTrace();
						return Optional.empty();
					}
				}).subscribe(this::applyHighlighting);

		// 마우스 클릭이벤트 정의
		codeArea.addEventHandler(KeyEvent.KEY_PRESSED, this::codeAreaKeyClick);
		// codeArea.setOnKeyPressed(this::codeAreaKeyClick);
		//
		codeArea.setOnMouseClicked(defaultSelectionHandler);

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

	//	public void setContent(String content) {
	//		codeArea.clear();
	//		codeArea.replaceText(0, 0, content);
	//	}

	public String getContent() {
		return codeArea.getText();
	}

	//	public void appendContent(String content) {
	//		codeArea.replaceText(0, 0, content);
	//	}

	protected void setContent(List<String> readLines) {
		setContent(readLines.stream().collect(Collectors.joining("\n")));
	}

	private void setContent(int start, int end, String text) {
		codeArea.getUndoManager().mark();
		codeArea.replaceText(start, end, text);
		codeArea.getUndoManager().mark();
	}

	public void setContent(String content) {
		codeArea.getUndoManager().mark();
		codeArea.clear();
		codeArea.replaceText(content);
		codeArea.getUndoManager().mark();
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

	//	private static StyleSpans<Collection<String>> computeHighlighting(String text) {
	//		Matcher matcher = PATTERN.matcher(text);
	//		int lastKwEnd = 0;
	//		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
	//		while (matcher.find()) {
	//			String styleClass = matcher.group("KEYWORD") != null ? "keyword"
	//					: matcher.group("PAREN") != null ? "paren"
	//							: matcher.group("BRACE") != null ? "brace"
	//									: matcher.group("BRACKET") != null ? "bracket"
	//											: matcher.group("SEMICOLON") != null ? "semicolon"
	//													: matcher.group("STRING") != null ? "string"
	//															: matcher.group("COMMENT") != null ? "comment" : null;
	//			/* never happens */ assert styleClass != null;
	//			spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
	//			spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
	//			lastKwEnd = matcher.end();
	//		}
	//		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
	//		return spansBuilder.create();
	//	}

	private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
		codeArea.setStyleSpans(0, highlighting);
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

	private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
		String text = codeArea.getText();
		Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
			@Override
			protected StyleSpans<Collection<String>> call() throws Exception {
				return computeHighlighting(text);
			}
		};

		executor.execute(task);

		return task;
	}

	//	private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
	//		codeArea.setStyleSpans(0, highlighting);
	//	}

	/**
	 * 키클릭 이벤트 처리
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 14.
	 * @param e
	 */
	public void codeAreaKeyClick(KeyEvent e) {
		// System.out.println("sqlKeywords");
		// System.out.println(e.getCode());
		// CTRL + F 찾기
		if ((e.getCode() == KeyCode.F) && (e.isControlDown() && !e.isShiftDown())) {

			ObservableValue<String> textProperty = codeArea.textProperty();
			TextSearchAndReplaceView textSearchView = new TextSearchAndReplaceView(this, textProperty);

			textSearchView.setOnSearchResultListener((vo) -> {

				switch (vo.getSearchType()) {
				case SEARCH_SIMPLE: {
					int startIndex = vo.getStartIndex();
					int endIndex = vo.getEndIndex();
					codeArea.selectRange(startIndex, endIndex);
					LOGGER.debug(String.format("find text : %s startIdx :%d endIdx :%d", vo.getSearchText(), startIndex, endIndex));
					break;
				}
				case SEARCH_ALL: {
					int startIndex = vo.getStartIndex();
					String searchText = vo.getSearchText();
					String replaceText = vo.getReplaceText();
					// codeArea.replaceText(startIndex, startIndex +
					// searchText.length(), replaceText);
					setContent(startIndex, startIndex + searchText.length(), replaceText);
					break;
				}
				}

			});

			textSearchView.setOnReplaceResultListener(vo -> {
				switch (vo.getReaplceType()) {
				case SIMPLE: {
					String reaplceResult = vo.getReaplceResult();
					setContent(reaplceResult);
					break;
				}
				case ALL: {
					String reaplceResult = vo.getReaplceResult();
					setContent(reaplceResult);
					break;
				}
				}
			});

			textSearchView.isSelectScopePropertyProperty().addListener((oba, oldval, newval) -> {
				if (newval)
					LOGGER.debug("User Select Locale Scope..");
				else
					LOGGER.debug("User Select Gloval Scope..");
			});

			codeArea.setOnMouseClicked(event -> {

				IndexRange selection = codeArea.getSelection();
				int start = selection.getStart();
				textSearchView.setSlidingStartIndexProperty(start);

			});

			textSearchView.show();

			codeArea.setOnMouseClicked(defaultSelectionHandler);

			e.consume();
		}
		// CTRL + SHIFT + F 포멧팅
		//		else if (e.getCode() == KeyCode.F && (e.isControlDown() && e.isShiftDown())) {
		//
		//			doSqlFormat();
		//			e.consume();
		//		}
		// Ctr + ALT +  U 선택된 문자 또는 전체 문자를 대문자로 치환
		else if (e.getCode() == KeyCode.U && (e.isControlDown() && e.isAltDown() && !e.isShiftDown())) {
			String selectedText = codeArea.getSelectedText();
			if (ValueUtil.isNotEmpty(selectedText)) {
				// codeArea.replaceSelection(sqlFormatter.toUpperCase(selectedText));
				replaceSelection(ValueUtil.toUpperCase(selectedText));
			} else {
				String text = codeArea.getText();
				//// 2016.2.15 undo,redo처리를 위해 setContent로 변경
				// codeArea.clear();
				// codeArea.appendText(sqlFormatter.toUpperCase(text));
				setContent(ValueUtil.toUpperCase(text));
			}
			e.consume();
		}
		// Ctr + ALT + L 선택된 문자 또는 전체 문자를 소문자로 치환
		else if (e.getCode() == KeyCode.L && (e.isControlDown() && e.isAltDown() && !e.isShiftDown())) {
			String selectedText = codeArea.getSelectedText();
			if (ValueUtil.isNotEmpty(selectedText)) {
				// codeArea.replaceSelection(sqlFormatter.toLowerCase(selectedText));
				replaceSelection(ValueUtil.toLowerCase(selectedText));
			} else {
				String text = codeArea.getText();
				//// 2016.2.15 undo,redo처리를 위해 setContent로 변경
				// codeArea.clear();
				// codeArea.appendText(sqlFormatter.toLowerCase(text));
				setContent(ValueUtil.toUpperCase(text));
			}
			e.consume();
		}
		// CTRL + L 특정 라인위치로 이동
		else if (e.getCode() == KeyCode.L && (e.isControlDown() && !e.isAltDown() && !e.isShiftDown())) {
			Optional<Pair<String, String>> showInputDialog = DialogUtil.showInputDialog("Go to Line", "Enter line number");
			showInputDialog.ifPresent(v -> {

				ValueUtil.ifNumberPresent(v.getValue(), num -> {

					moveToLine(num.intValue());

				});
			});

		}
	}

	public void replaceSelection(String selection) {
		codeArea.getUndoManager().mark();
		codeArea.replaceSelection(selection);
		codeArea.getUndoManager().mark();
	}

	public void appendContent(String content) {
		codeArea.getUndoManager().mark();
		// codeArea.replaceText(0, 0, content);
		codeArea.appendText(content);
		codeArea.getUndoManager().mark();
	}

	/**
	 * 특정라인으로 이동처리하는 메소드
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 4.
	 * @param moveToLine
	 */
	@SuppressWarnings("rawtypes")
	private void moveToLine(int moveToLine) {
		int position = 0;
		int row = moveToLine;
		int length = 0;
		for (Paragraph par : codeArea.getParagraphs().subList(0, row)) {
			position += par.length() + 1; // account for line terminators
			length = par.length() + 1;
		}
		codeArea.positionCaret(position - length);
	}

}
