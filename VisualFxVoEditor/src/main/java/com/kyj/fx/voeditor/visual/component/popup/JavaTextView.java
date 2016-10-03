/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.layout
 *	작성일   : 2015. 10. 15.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.popup;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.ExceptionHandler;
import com.kyj.fx.voeditor.visual.component.text.JavaTextArea;
import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.framework.annotation.FxPostInitialize;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.NamedArg;
import javafx.beans.property.BooleanProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * 단순한 텍스트를 보여주는 용도로만 사용되됨.
 *
 * @author KYJ
 *
 */
@FXMLController(value = "SimpleTextView.fxml", isSelfController = true)
public class JavaTextView extends BorderPane {
	private static Logger LOGGER = LoggerFactory.getLogger(JavaTextView.class);
	private JavaTextArea javaTextArea;
	//	private String content;
	private boolean showButtons;
	/**
	 * 버튼박스
	 */
	@FXML
	private HBox hboxButtons;

	@FXML
	private Button btnClose;

	// private StringProperty textProperty;
	private Stage stage = new Stage();

	public JavaTextView() {
		this("", true, e -> LOGGER.error(ValueUtil.toString(e)));
	}

	public JavaTextView(@NamedArg("content") String content) {
		this(content, true, e -> LOGGER.error(ValueUtil.toString(e)));
	}

	public JavaTextView(String content, boolean showButtons) {
		this(content, showButtons, e -> LOGGER.error(ValueUtil.toString(e)));
	}

	public JavaTextView(String content, boolean showButtons, ExceptionHandler handler) {
		LOGGER.debug("JavaTextView Constructor called");
		//		this.content = content;
		this.showButtons = showButtons;
		javaTextArea = new JavaTextArea();
//		javaTextArea.setEditable(false);
		javaTextArea.setContent(content);


		/*
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(JavaTextView.class.getResource("SimpleTextView.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		*/

		FxUtil.loadRoot(JavaTextView.class, this, ex -> handler.handle(ex));
	}

	@FxPostInitialize
	public void initPost() {

	}

	@FXML
	public void initialize() {
		hboxButtons.setVisible(showButtons);
		setCenter(javaTextArea);
		//		javaTextArea.setContent(content);
	}

	public BooleanProperty btnCloseVisibleProperty() {
		return this.btnClose.visibleProperty();
	}

	public void setBtnCloseVisible(boolean visible) {
		this.btnClose.setVisible(visible);
	}

	public boolean getBtnVisible() {
		return this.btnClose.isVisible();
	}

	public void setContent(String content) {
		javaTextArea.setContent(content);
	}

	public String getContent() {
		return javaTextArea.getContent();
	}

	public void setEditable(boolean editable) {
		javaTextArea.setEditable(editable);
	}

	public void show(double width, double height) throws IOException {
		show(SharedMemory.getPrimaryStage(), width, height);
	}

	public void show(Window root, double width, double height) throws IOException {

		btnClose.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				close();
			}
		});

		Scene scene = new Scene(this, width, height);
		stage.setTitle("JavaTextView Popup");
		stage.setScene(scene);
		//		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(root);
		stage.show();

	}

	public void show() throws IOException {
		show(1100, 700);
	}

	private void close() {
		stage.close();
	}

}
