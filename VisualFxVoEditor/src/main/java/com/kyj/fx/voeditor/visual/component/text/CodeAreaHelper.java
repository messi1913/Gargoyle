/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2016. 10. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import org.fxmisc.richtext.CodeArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.popup.TextSearchAndReplaceView;
import com.kyj.fx.voeditor.visual.util.SqlFormatter;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.IndexRange;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 *
 *  코드 처리 관련 Helper 클래스
 *
 *   CodeArea클래스와 연관된 모든 공통처리내용이 구현된다.
 *
 * @author KYJ
 *
 */
public class CodeAreaHelper {

	private static Logger LOGGER = LoggerFactory.getLogger(SqlKeywords.class);

	private static final String CHARACTERS_MATCH = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";

	protected CodeArea codeArea;
	protected SqlFormatter sqlFormatter = new SqlFormatter();
	protected CodeAreaMoveLineHelper codeMoveDeligator;

	public CodeAreaHelper(CodeArea codeArea) {
		this.codeArea = codeArea;
		this.codeArea.setOnMouseClicked(defaultSelectionHandler);
		codeMoveDeligator = new CodeAreaMoveLineHelper(codeArea);
	}

	public void setContent(String content) {
		codeArea.getUndoManager().mark();
		codeArea.clear();
		codeArea.replaceText(0, 0, content);
		codeArea.getUndoManager().mark();
	}

	public void setContent(int start, int end, String text) {
		codeArea.getUndoManager().mark();
		codeArea.replaceText(start, end, text);
		codeArea.getUndoManager().mark();
	}

	public String getSqlFormat(String sql) {
		return sqlFormatter.format(sql);
	}

	public void replaceSelection(String selection) {
		codeArea.getUndoManager().mark();
		codeArea.replaceSelection(selection);
		codeArea.getUndoManager().mark();
	}

	public void appendContent(String content) {
		codeArea.getUndoManager().mark();
		codeArea.appendText(content);
		codeArea.getUndoManager().mark();
	}

	public String getSelectedText() {
		String selectedText = codeArea.getSelectedText();
		if (ValueUtil.isEmpty(selectedText)) {
			String fullText = codeArea.getText();
			int caretPosition = codeArea.getCaretPosition();

			//bugfix
			String split = sqlFormatter.split(fullText, caretPosition);
			return split;
		}
		return selectedText;
	}

	public String getText() {
		return codeArea.getText();
	}

	/**
	 * Sql 포멧처리.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 9. 23.
	 */
	public void doSqlFormat() {
		String selectedText = codeArea.getSelectedText();
		if (ValueUtil.isNotEmpty(selectedText)) {
			// codeArea.replaceSelection(sqlFormatter.format(selectedText));
			replaceSelection(getSqlFormat(selectedText));
		} else {
			String format = getSqlFormat(codeArea.getText());
			// 2016.2.15 undo,redo처리를 위해 setContent로 변경
			// codeArea.clear();
			// codeArea.appendText(format);
			setContent(format);
		}
	}

	// 선택 범위 지정
	protected EventHandler<? super MouseEvent> defaultSelectionHandler = event -> {
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

			if(e.isConsumed())
				return;

			ObservableValue<String> textProperty = codeArea.textProperty();
			TextSearchAndReplaceView textSearchView = new TextSearchAndReplaceView(codeArea, textProperty);

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

					//					codeArea.getUndoManager().mark();
					//					codeArea.replaceText(startIndex, (startIndex + searchText.length()), replaceText);
					setContent(startIndex, startIndex + searchText.length(), replaceText);
					//					codeArea.getUndoManager().mark();

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
		// Ctr + U 선택된 문자 또는 전체 문자를 대문자로 치환
		else if (e.getCode() == KeyCode.U && (e.isControlDown() && !e.isAltDown() && !e.isShiftDown())) {
			if(e.isConsumed())
				return;

			String selectedText = codeArea.getSelectedText();
			if (ValueUtil.isNotEmpty(selectedText)) {
				// codeArea.replaceSelection(sqlFormatter.toUpperCase(selectedText));
				replaceSelection(sqlFormatter.toUpperCase(selectedText));
			}
			//2016.10.13 선택된 문자가 없는경우 치환안함.
//			else {
//				String text = codeArea.getText();
								//// 2016.2.15 undo,redo처리를 위해 setContent로 변경
								// codeArea.clear();
								// codeArea.appendText(sqlFormatter.toUpperCase(text));
//				setContent(sqlFormatter.toUpperCase(text));
//			}
			e.consume();
		}
		// Ctr + L 선택된 문자 또는 전체 문자를 소문자로 치환
		else if (e.getCode() == KeyCode.L && (e.isControlDown() && !e.isAltDown() && !e.isShiftDown())) {

			if(e.isConsumed())
				return;

			String selectedText = codeArea.getSelectedText();
			if (ValueUtil.isNotEmpty(selectedText)) {
				// codeArea.replaceSelection(sqlFormatter.toLowerCase(selectedText));
				replaceSelection(sqlFormatter.toLowerCase(selectedText));
			}

			//2016.10.13 선택된 문자가 없는경우 치환안함.
//			else {
//				String text = codeArea.getText();
										//// 2016.2.15 undo,redo처리를 위해 setContent로 변경
										// codeArea.clear();
										// codeArea.appendText(sqlFormatter.toLowerCase(text));
//				setContent(sqlFormatter.toUpperCase(text));
//			}
			e.consume();
		}
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
		codeMoveDeligator.moveToLine(moveToLine);
	}

	public void moveToLine(int moveToLine, int startCol) {
		codeMoveDeligator.moveToLine(moveToLine, startCol);
	}

	public void moveToLine(int moveToLine, int startCol, int endCol) {
		codeMoveDeligator.moveToLine(moveToLine, startCol, endCol);
	}

	public Integer getCurrentLine() {
		return codeMoveDeligator.getCurrentLine();
	}

}
