/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2015. 10. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.popup;

import java.io.IOException;

import org.fxmisc.richtext.CodeArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.ExceptionHandler;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * 이 기능을 쓸때.. 대용량 텍스트를 읽어오는경우 멈춤현상이 있다.
 *
 * 데이터 길이가 짧은 텍스트 기반을 읽어오는 경우만 사용해야한다.
 *
 * @author KYJ
 *
 */
public class SimpleTextView extends BorderPane {
	private static Logger LOGGER = LoggerFactory.getLogger(SimpleTextView.class);

	private String content;
	private boolean showButtons;
	// private TextArea javaTextArea;
	private TextArea textArea;

	/**
	 * 버튼박스
	 */
	@FXML
	private HBox hboxButtons;

	@FXML
	private Button btnClose;

	private Label lblLineInfo = new Label();

	// private StringProperty textProperty;
	private Stage stage = new Stage();

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
		textArea.setWrapText(value);
	}

	public void setEditable(boolean editable) {
		textArea.setEditable(false);
	}

	public void show() throws IOException {

//		btnClose.setOnMouseClicked(new EventHandler<MouseEvent>() {
//
//			@Override
//			public void handle(MouseEvent event) {
//				close();
//			}
//		});
//
//		Scene scene = new Scene(this, 1100, 700);
//		stage.setScene(scene);
//		stage.setAlwaysOnTop(true);
//		stage.initModality(Modality.APPLICATION_MODAL);
//		stage.initOwner(SharedMemory.getPrimaryStage());
//		stage.show();
		show(true);

	}

	public void show(boolean modal) throws IOException {

		btnClose.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				close();
			}
		});

		Scene scene = new Scene(this, 1100, 700);
		stage.setScene(scene);
		if (modal) {
			stage.setAlwaysOnTop(true);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(SharedMemory.getPrimaryStage());
			stage.show();
		} else {
//			stage.setAlwaysOnTop(false);
//			stage.initOwner(SharedMemory.getPrimaryStage());
			stage.showAndWait();
		}

	}

	@FXML
	public void initialize() {
		textArea = new TextArea();
		hboxButtons.setVisible(showButtons);

		// textProperty = new SimpleStringProperty();

		// txtVoEditor.textProperty().bind(textProperty);
		setCenter(textArea);
		// javaTextArea.setText(content);
		textArea.appendText(content);

//		ChangeListener<? super IndexRange> listener = (oba, oldval, newval) -> {
//			int start = newval.getStart();
//			int end = newval.getEnd();
//			int caretColumn = textArea.getCaretColumn();
//
//			String format = String.format("line : %d selectionStart : %d selectionEnd : %d column : %d ",
//					textArea.getCurrentParagraph() + 1, start + 1, end + 1, caretColumn + 1);
//
//			lblLineInfo.setText(format);
//		};

//		textArea.selectionProperty().addListener(listener);

		// textProperty.set(content);
	}

	private void close() {
		stage.close();
	}

}
