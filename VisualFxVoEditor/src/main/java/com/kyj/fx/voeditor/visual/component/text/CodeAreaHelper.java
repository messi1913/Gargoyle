/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2016. 10. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.fxmisc.richtext.CodeArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.popup.TextSearchAndReplaceView;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.SqlFormatter;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Pair;

/**
 *
 * 코드 처리 관련 Helper 클래스
 *
 * CodeArea클래스와 연관된 모든 공통처리내용이 구현된다.
 *
 * @author KYJ
 *
 */
public class CodeAreaHelper<T extends CodeArea> {

	private static Logger LOGGER = LoggerFactory.getLogger(SqlKeywords.class);

	private static final String CHARACTERS_MATCH = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";

	protected T codeArea;
	protected SqlFormatter sqlFormatter = new SqlFormatter();
	protected CodeAreaMoveLineHelper codeMoveDeligator;
	private CodeAreaDragDropHelper dragDropHelper;

	protected ContextMenu contextMenu;
	protected Menu menuSearch;
	protected MenuItem miFindReplace;
	protected MenuItem menuMoveToLine;
	protected MenuItem miToUppercase;
	protected MenuItem miToLowercase;

	public CodeAreaHelper(T codeArea) {
		this.codeArea = codeArea;

		this.codeArea.setOnMouseClicked(defaultSelectionHandler);
		codeMoveDeligator = new CodeAreaMoveLineHelper(codeArea);
		this.dragDropHelper = new CodeAreaDragDropHelper(codeArea);

		// this.codeArea.addEventHandler(MouseDragEvent.MOUSE_DRAG_OVER,
		// this::codeAreaDagOver);
		// this.codeArea.addEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED_TARGET,
		// this::codeAreaDagEnteredTarget);


		contextMenu = codeArea.getContextMenu();
		if (contextMenu == null) {
			contextMenu = new ContextMenu();
			codeArea.setContextMenu(contextMenu);
		}
		createMenus();
	}



	/**
	 *
	 * 2016-10-27 키 이벤트를 setAccelerator를 사용하지않고 이벤트 방식으로 변경 이유 : 도킹기능을 적용하하면
	 * setAccelerator에 등록된 이벤트가 호출안됨
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 27.
	 */
	public void createMenus() {
		menuSearch = new Menu("Search");
		miFindReplace = new MenuItem("Find/Replace");
		menuMoveToLine = new MenuItem("Move to line");
		miToUppercase = new MenuItem("To Uppercase");
		miToLowercase = new MenuItem("To Lowercase");

		miFindReplace.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));
		miFindReplace.setOnAction(this::findReplaceEvent);
		menuMoveToLine.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));
		menuMoveToLine.setOnAction(this::moveToLineEvent);
		miToUppercase.setAccelerator(new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		miToUppercase.setOnAction(this::toUppercaseEvent);
		miToLowercase.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		miToLowercase.setOnAction(this::toLowercaseEvent);

		menuSearch.getItems().add(miFindReplace);
		codeArea.getContextMenu().getItems().addAll(menuSearch, menuMoveToLine, miToUppercase, miToLowercase);

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 20.
	 * @param appendable
	 */
	public void customMenuHandler(CodeAreaCustomMenusHandler<T> appendable) {
		appendable.customMenus(this.codeArea, this.contextMenu);
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

			// bugfix
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
	 *
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
	 * 찾기 바꾸기 이벤트
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 13.
	 * @param e
	 */
	protected void findReplaceEvent(Event e) {

		if (e.isConsumed())
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

				// codeArea.getUndoManager().mark();
				// codeArea.replaceText(startIndex, (startIndex +
				// searchText.length()), replaceText);
				setContent(startIndex, startIndex + searchText.length(), replaceText);
				// codeArea.getUndoManager().mark();

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

	protected void toUppercaseEvent(KeyEvent e) {
		if (e.getCode() == KeyCode.U && (e.isControlDown() && !e.isAltDown() && !e.isShiftDown())) {
			if (e.isConsumed())
				return;
			toUppercaseEvent(e);
			e.consume();
		}
	}

	protected void toLowercaseEvent(KeyEvent e) {
		if (e.getCode() == KeyCode.L && (e.isControlDown() && !e.isAltDown() && !e.isShiftDown())) {

			if (e.isConsumed())
				return;
			toLowercaseEvent(e);
			e.consume();
		}
	}

	private void toUppercaseEvent(Event e) {
		String selectedText = codeArea.getSelectedText();
		if (ValueUtil.isNotEmpty(selectedText)) {
			// codeArea.replaceSelection(sqlFormatter.toUpperCase(selectedText));
			replaceSelection(sqlFormatter.toUpperCase(selectedText));
		}
	}

	private void toLowercaseEvent(Event e) {
		String selectedText = codeArea.getSelectedText();
		if (ValueUtil.isNotEmpty(selectedText)) {
			// codeArea.replaceSelection(sqlFormatter.toLowerCase(selectedText));
			replaceSelection(sqlFormatter.toLowerCase(selectedText));
		}
	}

	protected void moveToLineEvent(Event e) {
		Optional<Pair<String, String>> showInputDialog = DialogUtil.showInputDialog(this.codeArea, "Go to Line", "Input Line Number",
				str -> ValueUtil.isNumber(str));

		showInputDialog.ifPresent(v -> {
			String value = v.getValue();
			BigDecimal bigDecimal = new BigDecimal(value);
			int intValue = bigDecimal.intValue();
			moveToLine(intValue);
		});
		e.consume();
	}

	/**
	 * 키클릭 이벤트 처리
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 14.
	 * @param e
	 */
	public void codeAreaKeyClick(KeyEvent e) {

		if (KeyCode.F == e.getCode() && e.isControlDown() && !e.isShiftDown() && !e.isAltDown()) {
			if (!e.isConsumed()) {
				findReplaceEvent(new ActionEvent());
				e.consume();
			}

		} else if (KeyCode.L == e.getCode() && e.isControlDown() && !e.isShiftDown() && !e.isAltDown()) {
			if (!e.isConsumed()) {
				moveToLineEvent(new ActionEvent());
				e.consume();
			}
		} else if (KeyCode.U == e.getCode() && e.isControlDown() && e.isShiftDown() && !e.isAltDown()) {
			if (!e.isConsumed()) {
				toUppercaseEvent(new ActionEvent());
				e.consume();
			}
		} else if (KeyCode.L == e.getCode() && e.isControlDown() && e.isShiftDown() && !e.isAltDown()) {
			if (!e.isConsumed()) {
				toLowercaseEvent(new ActionEvent());
				e.consume();
			}
		} else {
			codeArea.getUndoManager().mark();
		}

		// System.out.println("sqlKeywords");
		// System.out.println(e.getCode());
		// CTRL + F 찾기
		// if ((e.getCode() == KeyCode.F) && (e.isControlDown() &&
		// !e.isShiftDown())) {
		// findReplaceEvent(e);
		// }
		// Ctr + U 선택된 문자 또는 전체 문자를 대문자로 치환
		// toUppercaseEvent(e);
		// Ctr + L 선택된 문자 또는 전체 문자를 소문자로 치환
		// toLowercaseEvent(e);

	}

	/**
	 * 특정라인으로 이동처리하는 메소드
	 *
	 * 특정라인블록 전체를 선택처리함.
	 *
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

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 28.
	 * @return
	 */
	public IndexRange getSelection() {
		return codeArea.getSelection();
	}

}
