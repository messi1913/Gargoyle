/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.layout
 *	작성일   : 2015. 10. 15.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.popup;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.text.XMLEditor;
import com.kyj.fx.voeditor.visual.framework.handler.ExceptionHandler;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kyj.Fx.dao.wizard.core.util.ValueUtil;

/**
 * 단순한 텍스트를 보여주는 용도로만 사용되됨.
 *
 * @author KYJ
 *
 */
public class XMLTextView extends BorderPane {
	private static Logger LOGGER = LoggerFactory.getLogger(XMLTextView.class);

	/**
	 * 버튼박스
	 */

	private HBox hboxButtons = new HBox();

	private Button btnClose;

	// private StringProperty textProperty;
	private Stage stage = new Stage();

	private File file;

	private String text;

	private XMLEditor xmlEditor;

	/**
	 * @param file
	 * @param b
	 * @throws IOException
	 */
	public XMLTextView(File file, boolean showbuttons) throws IOException {
		this(file, showbuttons, null);
	}

	private XMLTextView(File file, boolean showButtons, ExceptionHandler handler) throws IOException {
		this.file = file;
		try {
			hboxButtons.setVisible(showButtons);
			text = FileUtils.readFileToString(file, "UTF-8");

			xmlEditor = new XMLEditor();
			xmlEditor.setContent(text);
			setCenter(xmlEditor);

		} catch (Exception e) {
			if (handler == null) {
				LOGGER.error(ValueUtil.toString(e));
			} else {
				handler.handle(e);
			}
		}
	}

	/**
	 * @return the file
	 */
	public final File getFile() {
		return file;
	}

	/**
	 * @return the text
	 */
	public final String getText() {
		return text;
	}

	public void setContent(String content) {
		xmlEditor.setContent(content);
	}

	public void setEditable(boolean editable) {
		xmlEditor.setEditable(editable);
	}

	public void show(double width, double height) throws IOException {

		btnClose.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				close();
			}
		});

		Scene scene = new Scene(this, width, height);
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(SharedMemory.getPrimaryStage());
		stage.show();

	}

	public void show() throws IOException {
		show(1100, 700);
	}

	private void close() {
		stage.close();
	}

}
