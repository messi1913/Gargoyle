/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2015. 10. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import org.fxmisc.richtext.CodeArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.PrimaryStageCloseable;
import com.kyj.fx.voeditor.visual.framework.handler.ExceptionHandler;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 *
 * 이 기능을 쓸때.. 대용량 텍스트를 읽어오는경우 멈춤현상이 있다.
 *
 * 데이터 길이가 짧은 텍스트 기반을 읽어오는 경우만 사용해야한다.
 *
 * @author KYJ
 *
 */
public class SimpleTextView extends BorderPane implements PrimaryStageCloseable {

	private static Logger LOGGER = LoggerFactory.getLogger(SimpleTextView.class);

	/**
	 * @최초생성일 2016. 10. 6.
	 */
	private static final String POSISION_FORMAT = "line : %d selectionStart : %d selectionEnd : %d column : %d  anchor : %d caret : %d";

	private String content;
	private boolean showButtons;
	// private TextArea javaTextArea;
	private CodeArea codeArea;

	private CodeAreaHelper<CodeArea> helper;
	/**
	 * 버튼박스
	 */
	@FXML
	private HBox hboxButtons;

	@FXML
	private Button btnClose;

	private Label lblLineInfo = new Label();

	public SimpleTextView(String content) {
		this(content, true, null);
	}

	public SimpleTextView(String content, boolean showButtons) {
		this(content, showButtons, null);
	}

	public SimpleTextView(String content, boolean showButtons, ExceptionHandler handler) {
		this.content = content;
		this.showButtons = showButtons;

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("SimpleTextView.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (Exception e) {
			if (handler == null) {
				e.printStackTrace();
			} else {
				handler.handle(e);
			}

		}

	}

	public void setWrapText(boolean value) {
		codeArea.setWrapText(value);
	}

	public void setEditable(boolean editable) {
		codeArea.setEditable(false);
	}

	@FXML
	public void initialize() {

		codeArea = new CodeArea();
		this.helper = new CodeAreaHelper<>(codeArea);
		hboxButtons.setVisible(showButtons);
		if (!showButtons) {
			hboxButtons.setMinHeight(0d);
			hboxButtons.setMaxHeight(0d);
			hboxButtons.setPrefHeight(0d);
		}

		codeArea.appendText(content);

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

		setCenter(codeArea);
		setBottom(lblLineInfo);

	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.framework.PrimaryStageCloseable#closeRequest()
	 */
	@Override
	public void closeRequest() {
	}

	/**
	 * @return the helper
	 */
	public final CodeAreaHelper<CodeArea> getHelper() {
		return helper;
	}

}
