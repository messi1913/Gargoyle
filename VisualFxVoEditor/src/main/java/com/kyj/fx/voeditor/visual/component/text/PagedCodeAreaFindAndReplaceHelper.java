/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2016. 12. 14.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import org.fxmisc.richtext.CodeArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.popup.TextSearchAndReplaceView;

import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 *
 *  CodeArea의 FindAndReplace에 대한 공통기능을 처리할 수 있게 도와주는 클래스.
 * @author KYJ
 *
 */
public class PagedCodeAreaFindAndReplaceHelper {

	private static Logger LOGGER = LoggerFactory.getLogger(PagedCodeAreaFindAndReplaceHelper.class);

	private BigTextView view;
	protected Menu menuSearch;
	protected MenuItem miFindReplace;
	//	private EventHandler<? super MouseEvent> defaultSelectionHandler;;

	public PagedCodeAreaFindAndReplaceHelper(BigTextView view) {
		this.view = view;
		createMenus();
		view.addEventHandler(MouseEvent.MOUSE_PRESSED, ev -> {
			if (ev.getButton() == MouseButton.SECONDARY) {
				if (ev.isConsumed())
					return;
				menuSearch.show();
			}
		});
		//		defaultSelectionHandler = new CodeAreaDefaultSelectionHandler(codeArea);
	}

	/**
	 *  FindAndReplace에 대한 메뉴를 정의.
	 * @return
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 13.
	 */
	public Menu createMenus() {
		menuSearch = new Menu("Search");
		miFindReplace = new MenuItem("Find/Replace");
		miFindReplace.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));
		miFindReplace.setOnAction(this::findReplaceEvent);
		menuSearch.getItems().add(miFindReplace);
		return menuSearch;
	}

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

		SimpleTextView currentPageView = view.getCurrentPageView();
		CodeArea codeArea = currentPageView.getCodeArea();

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

		textSearchView.setOnSelectionMoveListener(vo -> {
			codeArea.selectRange(vo.getStartIndex(), vo.getEndIndex());
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

		//		codeArea.setOnMouseClicked(defaultSelectionHandler);

		e.consume();
	}

	public void setContent(String content) {
		SimpleTextView currentPageView = this.view.getCurrentPageView();
		if (currentPageView != null) {
			CodeArea codeArea = currentPageView.getCodeArea();
			codeArea.getUndoManager().mark();
			codeArea.clear();
			codeArea.replaceText(0, 0, content);
			codeArea.getUndoManager().mark();
		}

	}

	public void setContent(int start, int end, String text) {
		SimpleTextView currentPageView = this.view.getCurrentPageView();
		if (currentPageView != null) {
			CodeArea codeArea = currentPageView.getCodeArea();
			codeArea.getUndoManager().mark();
			codeArea.replaceText(start, end, text);
			codeArea.getUndoManager().mark();
		}
	}

	//	// 선택 범위 지정
	//	EventHandler<? super MouseEvent> defaultSelectionHandler = event -> {
	//		if (event.getClickCount() == 1) {
	//			// codeArea.setStyleSpans(0,
	//			// groupBackgroundColor(codeArea.getText(),
	//			// codeArea.getCaretPosition()));
	//		} else if (event.getClickCount() == 2) {
	//			String selectedText = codeArea.getSelectedText();
	//			if (ValueUtil.isNotEmpty(selectedText)) {
	//				IndexRange selection = codeArea.getSelection();
	//				String ltrimText = selectedText.replaceAll("^\\s+", "");
	//				String firstStr = ltrimText.substring(0, 1).replaceAll(CHARACTERS_MATCH, "");
	//				int start = selection.getStart();
	//				int end = selection.getEnd();
	//				codeArea.selectRange(start + (selectedText.length() - ltrimText.length() + 1 - firstStr.length()), end);
	//			}
	//		}
	//	};

}
