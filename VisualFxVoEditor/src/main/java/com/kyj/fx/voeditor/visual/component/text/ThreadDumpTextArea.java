package com.kyj.fx.voeditor.visual.component.text;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.NavigationActions.SelectionPolicy;
import org.fxmisc.richtext.Paragraph;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.thread.ExecutorDemons;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 *
 */
public class ThreadDumpTextArea extends BorderPane {

	/**
	 * @최초생성일 2016. 10. 6.
	 */
	private static final String POSISION_FORMAT = "line : %d selectionStart : %d selectionEnd : %d column : %d  anchor : %d caret : %d";

	@SuppressWarnings("unused")
	private static Logger LOGGER = LoggerFactory.getLogger(SqlKeywords.class);

	//	private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
	private static final String METHODPAREN = "\\([a-zA-Z0-9:. ]+\\)";
	private static final String BRACE_PATTERN = "\\{|\\}";
	private static final String BRACKET_PATTERN = "\\[|\\]";
	private static final String SEMICOLON_PATTERN = "\\;";
	private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
	private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

	private static final Pattern PATTERN = Pattern.compile("(?<METHODPAREN>" + METHODPAREN + ")" + "|(?<BRACE>" + BRACE_PATTERN + ")"
			+ "|(?<BRACKET>" + BRACKET_PATTERN + ")" + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")" + "|(?<STRING>" + STRING_PATTERN + ")"
			+ "|(?<COMMENT>" + COMMENT_PATTERN + ")");

	private CodeArea codeArea;
	private CodeAreaHelper<CodeArea> codeHelperDeligator;
	private Label lblLineInfo = new Label();

	/**
	 * 인스턴스의 수와 상관없이 무조건 1개의 서비스로 사용.
	 * 처음에 생성자에 생성했는데, 스레드수가 너무 많이 늘어남. 사실 이 클래스는 어플리케이션당 하나만 사용하여
	 * 관리해도 상관없다고 판단.
	 * @최초생성일 2016. 10. 18.
	 */
	private static final ExecutorService executor = ExecutorDemons.newSingleThreadExecutor(" Thread-Dump-Executors - Gargoyle ");

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

	public ThreadDumpTextArea() {

		//
		//		RuntimeClassUtil.addShutdownHook(() -> {
		//
		//			if (!executor.isTerminated())
		//				executor.shutdownNow();
		//
		//		});

		codeArea = new CodeArea();
		codeHelperDeligator = new CodeAreaHelper<>(codeArea);
		//codeArea.setParagraphGraphicFactory(getLineFactory());

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

			String format = String.format(POSISION_FORMAT, codeArea.getCurrentParagraph() + 1, start + 1, end + 1, caretColumn + 1,
					codeArea.getAnchor(), codeArea.getCaretPosition());

			lblLineInfo.setText(format);
		});

		lblLineInfo.setPrefHeight(USE_COMPUTED_SIZE);
		lblLineInfo.setMinHeight(USE_COMPUTED_SIZE);
		lblLineInfo.setMaxHeight(USE_COMPUTED_SIZE);

		this.setCenter(codeArea);
		this.setBottom(lblLineInfo);
		// this.getChildren().add(codeArea);

		//		this.getStylesheets().add(JavaKeywordsAsync.class.getResource("java-keywords.css").toExternalForm());
		this.getStylesheets().add(ThreadDumpTextArea.class.getResource("thread-dump-keywords.css").toExternalForm());

	}

	public IntFunction<Node> getLineFactory() {
		return LineNumberFactory.get(codeArea);
	}

	//	public void setContent(String content) {
	//		codeArea.clear();
	//		codeArea.replaceText(0, 0, content);
	//	}

	public String getContent() {
		return codeHelperDeligator.getText();
	}

	//	public void appendContent(String content) {
	//		codeArea.replaceText(0, 0, content);
	//	}

	protected void setContent(List<String> readLines) {
		setContent(readLines.stream().collect(Collectors.joining("\n")));
	}

	private void setContent(int start, int end, String text) {
		codeHelperDeligator.setContent(start, end, text);
	}

	public void setContent(String content) {
		codeHelperDeligator.setContent(content);
	}

	public void setEditable(boolean editable) {
		this.codeArea.setEditable(editable);
	}

	public void selectRange(int anchor, int caretPosition) {
		codeArea.selectRange(anchor, caretPosition);
	}

	public final String getText() {
		return codeHelperDeligator.getText();
	}

	public final String getText(int start, int end) {
		return codeArea.getText(start, end);

	}

	public String getText(int paragraph) {
		return codeArea.getText(paragraph);
	}

	public Paragraph<Collection<String>, Collection<String>> getParagraph(int index) {
		return codeArea.getParagraph(index);
	}

	public ObservableList<Paragraph<Collection<String>, Collection<String>>> getParagraphs() {
		return codeArea.getParagraphs();
	}

	public IndexRange getParagraphSelection(int paragraph) {
		return codeArea.getParagraphSelection(paragraph);
	}

	private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
		codeArea.setStyleSpans(0, highlighting);
	}

	private static StyleSpans<Collection<String>> computeHighlighting(String text) {
		Matcher matcher = PATTERN.matcher(text);
		int lastKwEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		while (matcher.find()) {
			String styleClass = matcher.group("METHODPAREN") != null ? "method-paren"
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

	/**
	 * 키클릭 이벤트 처리
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 14.
	 * @param e
	 */
	public void codeAreaKeyClick(KeyEvent e) {
		codeHelperDeligator.codeAreaKeyClick(e);
	}

	public void replaceSelection(String selection) {
		codeHelperDeligator.replaceSelection(selection);
		//		codeArea.getUndoManager().mark();
		//		codeArea.replaceSelection(selection);
		//		codeArea.getUndoManager().mark();
	}

	public void appendContent(String content) {
		codeHelperDeligator.appendContent(content);
		//		codeArea.getUndoManager().mark();
		//		codeArea.appendText(content);
		//		codeArea.getUndoManager().mark();
	}

	/**
	 * 특정라인으로 이동처리하는 메소드
	 *
	 * 특정라인블록 전체를 선택처리함.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 4.
	 * @param moveToLine
	 */
	public void moveToLine(int moveToLine) {
		codeHelperDeligator.moveToLine(moveToLine);
	}

	public void moveToLine(int moveToLine, int startCol) {
		codeHelperDeligator.moveToLine(moveToLine, startCol);
	}

	public void moveToLine(int moveToLine, int startCol, int endCol) {
		codeHelperDeligator.moveToLine(moveToLine, startCol, endCol);
	}

	/**
	 * @return the codeArea
	 */
	public final CodeArea getCodeArea() {
		return codeArea;
	}

	public Integer getCurrentLine() {
		return codeHelperDeligator.getCurrentLine();
	}

	public void lineStart(SelectionPolicy pol) {
		codeArea.lineStart(pol);
	}

	public void clearSelection() {
		codeArea.deselect();
	}


}
