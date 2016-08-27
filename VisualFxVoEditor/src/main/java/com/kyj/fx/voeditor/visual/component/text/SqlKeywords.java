package com.kyj.fx.voeditor.visual.component.text;

import java.io.UnsupportedEncodingException;
import java.lang.Character.UnicodeBlock;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.popup.TextSearchAndReplaceView;
import com.kyj.fx.voeditor.visual.util.SqlFormatter;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

public class SqlKeywords extends BorderPane {

	private static Logger LOGGER = LoggerFactory.getLogger(SqlKeywords.class);
	// private static final String[] KEYWORDS = new String[] {
	// /* 대문자 */
	// "SELECT", "FROM", "GROUP", "BY", "WHERE", "JOIN", "AND", "UPDATE",
	//
	// "DELETE", "CREATE", "DROP", "SET", "NOT NULL", "INSERT into", "ALTER",
	// "ORDER",
	// /* 소문자 */
	// "select", "from", "group", "by", "where", "join", "and", "update",
	// "delete", "create", "drop", "set", "not null", "insert into",
	// "alter", "order" };
	
	//2016-08-27 정규식 (?i) 추가옵션을 주는경오 대소문자 구분을 무시한다.
	private static final String KEYWORD_PATTERN = "(?i)\\b(" + String.join("|", SQLKeywordFactory.getKeywords()) + ")\\b";
	private static final String PAREN_PATTERN = "\\(|\\)";
	private static final String BRACE_PATTERN = "\\{|\\}";
	private static final String BRACKET_PATTERN = "\\[|\\]";
	private static final String SEMICOLON_PATTERN = "\\;";
	private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
	// private static final String COMMENT_PATTERN = "//[^\n]*" + "|" +
	// "/\\*(.|\\R)*?\\*/";
	private static final String COMMENT_PATTERN = "(?:/\\*[^;]*?\\*/)|(?:--[^\\n]*)";

	private static final String CHARACTERS_MATCH = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";

	private static final Pattern PATTERN = Pattern.compile("(?<KEYWORD>" + KEYWORD_PATTERN + ")" + "|(?<PAREN>" + PAREN_PATTERN + ")"
			+ "|(?<BRACE>" + BRACE_PATTERN + ")" + "|(?<BRACKET>" + BRACKET_PATTERN + ")" + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
			+ "|(?<STRING>" + STRING_PATTERN + ")" + "|(?<COMMENT>" + COMMENT_PATTERN + ")");

	private CodeArea codeArea;

	private Label lblLineInfo = new Label();

	private SqlFormatter sqlFormatter = new SqlFormatter();

	public CodeArea getCodeArea() {
		return codeArea;
	}

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

	public SqlKeywords() {

		codeArea = new CodeArea();
		codeArea.appendText("");
		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

		codeArea.richChanges().subscribe(change -> {
			Platform.runLater(() -> {

				codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));

			});
		});

		// 마우스 클릭이벤트 정의
		codeArea.addEventHandler(KeyEvent.KEY_PRESSED, this::codeAreaKeyClick);
		// codeArea.setOnKeyPressed(this::codeAreaKeyClick);
		//
		codeArea.setOnMouseClicked(defaultSelectionHandler);

		// 선택라인정보 보여주는 기능 추가.
		codeArea.selectionProperty().addListener((oba, oldval, newval) -> {
			int start = newval.getStart();
			int end = newval.getEnd();
			int caretColumn = codeArea.getCaretColumn();

			String format = String.format("line : %d selectionStart : %d selectionEnd : %d column : %d ",
					codeArea.getCurrentParagraph() + 1, start + 1, end + 1, caretColumn + 1);

			lblLineInfo.setText(format);
		});

		lblLineInfo.setPrefHeight(USE_COMPUTED_SIZE);
		this.setCenter(codeArea);
		this.setBottom(lblLineInfo);
		// this.getChildren().add();
		this.getStylesheets().add(SqlKeywords.class.getResource("java-keywords.css").toExternalForm());

	}

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
		else if (e.getCode() == KeyCode.F && (e.isControlDown() && e.isShiftDown())) {

			String selectedText = codeArea.getSelectedText();
			if (ValueUtil.isNotEmpty(selectedText)) {
				// codeArea.replaceSelection(sqlFormatter.format(selectedText));
				replaceSelection(sqlFormatter.format(selectedText));
			} else {
				String format = sqlFormatter.format(codeArea.getText());
				// 2016.2.15 undo,redo처리를 위해 setContent로 변경
				// codeArea.clear();
				// codeArea.appendText(format);
				setContent(format);
			}
			e.consume();
		}
		// Ctr + U 선택된 문자 또는 전체 문자를 대문자로 치환
		else if (e.getCode() == KeyCode.U && (e.isControlDown() && !e.isAltDown() && !e.isShiftDown())) {
			String selectedText = codeArea.getSelectedText();
			if (ValueUtil.isNotEmpty(selectedText)) {
				// codeArea.replaceSelection(sqlFormatter.toUpperCase(selectedText));
				replaceSelection(sqlFormatter.toUpperCase(selectedText));
			} else {
				String text = codeArea.getText();
				//// 2016.2.15 undo,redo처리를 위해 setContent로 변경
				// codeArea.clear();
				// codeArea.appendText(sqlFormatter.toUpperCase(text));
				setContent(sqlFormatter.toUpperCase(text));
			}
			e.consume();
		}
		// Ctr + L 선택된 문자 또는 전체 문자를 소문자로 치환
		else if (e.getCode() == KeyCode.L && (e.isControlDown() && !e.isAltDown() && !e.isShiftDown())) {
			String selectedText = codeArea.getSelectedText();
			if (ValueUtil.isNotEmpty(selectedText)) {
				// codeArea.replaceSelection(sqlFormatter.toLowerCase(selectedText));
				replaceSelection(sqlFormatter.toLowerCase(selectedText));
			} else {
				String text = codeArea.getText();
				//// 2016.2.15 undo,redo처리를 위해 setContent로 변경
				// codeArea.clear();
				// codeArea.appendText(sqlFormatter.toLowerCase(text));
				setContent(sqlFormatter.toUpperCase(text));
			}
			e.consume();
		}
	}

	private void setContent(int start, int end, String text) {
		codeArea.getUndoManager().mark();
		codeArea.replaceText(start, end, text);
		codeArea.getUndoManager().mark();
	}

	public void replaceSelection(String selection) {
		codeArea.getUndoManager().mark();
		codeArea.replaceSelection(selection);
		codeArea.getUndoManager().mark();
	}

	public void setContent(String content) {
		codeArea.getUndoManager().mark();
		codeArea.clear();
		codeArea.replaceText(0, 0, content);
		codeArea.getUndoManager().mark();
	}

	public void appendContent(String content) {
		codeArea.getUndoManager().mark();
		// codeArea.replaceText(0, 0, content);
		codeArea.appendText(content);
		codeArea.getUndoManager().mark();
	}

	public String getSelectedText() {
		String selectedText = codeArea.getSelectedText();
		if (ValueUtil.isEmpty(selectedText)) {
			String fullText = codeArea.getText();
			int caretPosition = codeArea.getCaretPosition();
			return sqlFormatter.split(fullText, caretPosition);
		}
		return selectedText;
	}

	public String getText() {
		return codeArea.getText();
	}

	private static StyleSpans<Collection<String>> computeHighlighting(String _text) {
		String text = _text;
		text = StringUtils.replace(text, "\"", "'");

		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		try {
			Matcher matcher = PATTERN.matcher(text);
			int lastKwEnd = 0;

			while (matcher.find(lastKwEnd)) {

				String styleClass = matcher.group("KEYWORD") != null ? "keyword"
						: matcher.group("PAREN") != null ? "paren"
								: matcher.group("BRACE") != null ? "brace"
										: matcher.group("BRACKET") != null ? "bracket"
												: matcher.group("SEMICOLON") != null ? "semicolon"
														: matcher.group("STRING") != null ? "string"
																: matcher.group("COMMENT") != null ? "comment" : null; /*
																														 * never
																														 * happens
																														 */
				assert styleClass != null;
				spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
				spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
				lastKwEnd = matcher.end();
			}
			spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
			return spansBuilder.create();
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}
		return spansBuilder.create();
		// Matcher matcher = PATTERN.matcher(text);
		// int lastKwEnd = 0;
		// StyleSpansBuilder<Collection<String>> spansBuilder = new
		// StyleSpansBuilder<>();
		// while (matcher.find()) {
		// String styleClass = matcher.group("KEYWORD") != null ? "keyword"
		// : matcher.group("PAREN") != null ? "paren"
		// : matcher.group("BRACE") != null ? "brace"
		// : matcher.group("BRACKET") != null ? "bracket"
		// : matcher.group("SEMICOLON") != null ? "semicolon"
		// : matcher.group("STRING") != null ? "string"
		// : matcher.group("COMMENT") != null ? "comment" : null; /*
		// * never
		// * happens
		// */
		// assert styleClass != null;
		// spansBuilder.add(Collections.emptyList(), matcher.start() -
		// lastKwEnd);
		// spansBuilder.add(Collections.singleton(styleClass), matcher.end() -
		// matcher.start());
		// lastKwEnd = matcher.end();
		// }
		// spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
		// return spansBuilder.create();
	}

	// TODO :: () 안에 문자들을 가식성이 좋도록 표시... StyleSpans이 중복되어 적용이 안됨..
	// computeHighlighting 메소드와 합쳐야함.
	// css 파일에서 -fx-background-color 기능이 동작 하지 않음..

	private static StyleSpans<Collection<String>> groupBackgroundColor(String text, int point) {

		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

		Pattern pattern = Pattern.compile("\\([^)]+\\)");
		Matcher matcher = pattern.matcher(text);
		int lastKwEnd = 0;

		while (matcher.find()) {
			String styleClass = "unGroup";
			if (matcher.start() < point && matcher.end() > point) {
				styleClass = "inGroup";
			}
			spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
			spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
			lastKwEnd = matcher.end();
		}

		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
		return spansBuilder.create();
	}

	public void setEditable(boolean editable) {
		codeArea.editableProperty().set(editable);
	}

}
