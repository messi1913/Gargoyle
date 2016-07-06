/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2016. 2. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.console;

import com.kyj.fx.voeditor.visual.component.popup.TextSearchAndReplaceView;
import com.kyj.fx.voeditor.visual.component.popup.TextSearchComposite;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 *
 * 콘솔에 대한 기능적인 구현 및 디자인 만 처리
 *
 *
 * ConsoleManager를 통해 특화된 이벤트에 대한 처리.
 *
 * @author KYJ
 *
 */
class BaseConsole extends BorderPane {
	private static final String STYLE_CLASS_NAME = "console-skin";
	/**
	 * TextArea의 기본적인 기능을 사용하는게 아닌 특화된 기능적 처리를 지원
	 */
	private ConsoleManager manager;

	private TextArea textArea;

	private HBox buttonHbox;
	private Button btnClear;

	public BaseConsole() {

		btnClear = new Button("Clear");
		btnClear.setOnAction(this::btnClearOnAction);
		buttonHbox = new HBox(5, btnClear);
		buttonHbox.setAlignment(Pos.CENTER_RIGHT);
		buttonHbox.setPadding(new Insets(5, 5, 5, 5));
		setTop(buttonHbox);

		textArea = new TextArea();
		textArea.setWrapText(true);
		textArea.addEventHandler(KeyEvent.KEY_PRESSED, this::textAreaOnKeyEvent);
		textArea.getStyleClass().add(STYLE_CLASS_NAME);
		manager = new ConsoleManager(this);
		setCenter(textArea);
		// this.getStyleClass().add(STYLE_CLASS_NAME);
		this.getStylesheets().clear();
		this.getStylesheets().add(getClass().getResource("Console.css").toExternalForm());

	}

	public synchronized void setConsoleManager(ConsoleManager manager) {
		this.manager = manager;
	}

	public ConsoleManager getConsoleManager() {
		return this.manager;
	}

	/********************************
	 * 작성일 : 2016. 6. 23. 작성자 : KYJ
	 *
	 * 콘솔출력부에서 키 클릭 이벤트
	 *
	 * @param e
	 ********************************/
	public void textAreaOnKeyEvent(KeyEvent e) {

		// ctrl + F
		if (e.isControlDown() && e.getCode() == KeyCode.F && !e.isShiftDown() && !e.isAltDown()) {

			TextSearchComposite textSearchView = new TextSearchComposite(this, textArea.textProperty());
			textSearchView.setOnSearchResultListener((vo) -> {

				switch (vo.getSearchType()) {
				case SEARCH_SIMPLE: {
					int startIndex = vo.getStartIndex();
					int endIndex = vo.getEndIndex();
					textArea.selectRange(startIndex, endIndex);
					break;
				}
				default:
					break;
				}

			});

			textSearchView.isSelectScopePropertyProperty().addListener((oba, oldval, newval) -> {
				// if (newval)
				// LOGGER.debug("User Select Locale Scope..");
				// else
				// LOGGER.debug("User Select Gloval Scope..");
			});

			textArea.setOnMouseClicked(event -> {
				IndexRange selection = textArea.getSelection();
				int start = selection.getStart();
				textSearchView.setSlidingStartIndexProperty(start);
			});

			textSearchView.show();

			e.consume();
		}

	}

	/********************************
	 * 작성일 : 2016. 6. 23. 작성자 : KYJ
	 *
	 * clear클릭 이벤트
	 *
	 * @param e
	 ********************************/
	public void btnClearOnAction(ActionEvent e) {
		this.textArea.clear();
	}

	/********************************
	 * 작성일 : 2016. 6. 23. 작성자 : KYJ
	 *
	 *
	 * @param text
	 ********************************/
	public void appendText(String text) {
		textArea.appendText(manager.appendText(text));
	}

	/********************************
	 * 작성일 : 2016. 6. 23. 작성자 : KYJ
	 *
	 *
	 * @param text
	 * @param appendLine
	 ********************************/
	public void appendText(String text, boolean appendLine) {
		textArea.appendText(manager.appendText(text, appendLine));
	}

	/********************************
	 * 작성일 : 2016. 6. 23. 작성자 : KYJ
	 *
	 * 수정가능여부
	 *
	 * @param editable
	 ********************************/
	public void setEditable(boolean editable) {
		textArea.setEditable(editable);
	}
}
