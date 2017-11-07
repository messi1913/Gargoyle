/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.image
 *	작성일   : 2017. 9. 14.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.image;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfoenix.controls.JFXTextArea;
import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * 
 * 이미지 <-> Base64 변환
 * 
 * @author KYJ
 *
 */
@FXMLController(value = "Base64ImageConvertView.fxml", isSelfController = true, css = "Base64ImageConvertView.css")
public class Base64ImageConvertComposte extends BorderPane {
	private static final Logger LOGGER = LoggerFactory.getLogger(Base64ImageConvertComposte.class);

	@FXML
	JFXTextArea txtBase64;
	@FXML
	ImageView IvImage;
	@FXML
	Hyperlink txtImageLocation;

	public Base64ImageConvertComposte() {

		FxUtil.loadRoot(Base64ImageConvertComposte.class, this, err -> {
			LOGGER.error(ValueUtil.toString(err));
		});
	}

	@FXML
	public void initialize() {
		this.txtImageLocation.setOnAction(ev -> {
			String location = this.txtImageLocation.getText();
			File file = new File(location);
			if (file.exists()) {
				FileUtil.openFile(file);
			}
		});
	}

	@FXML
	public void base64ToImageOnAction() {
		String text = txtBase64.getText();
		if (ValueUtil.isEmpty(text)) {
			DialogUtil.showMessageDialog(FxUtil.getWindow(this), "Empty Content.");
			return;
		}
		byte[] decode = Base64.getDecoder().decode(text);
		IvImage.setImage(new Image(new ByteArrayInputStream(decode)));
	}

	static long MEGA_BYTE = 1024 * 1024;

	@FXML
	public void ImageToBase64OnAction() {

		File showFileDialog = DialogUtil.showFileDialog(FxUtil.getWindow(this), chooser -> {

		});
		if (showFileDialog != null) {
			try {

				long length = showFileDialog.length();
				if (length > MEGA_BYTE * 20) {
					Optional<Pair<String, String>> showYesOrNoDialog = DialogUtil.showYesOrNoDialog((Stage) FxUtil.getWindow(this),
							"Warning....", "파일 사이즈가 20메가가 넘습니다. 시간이 오래 걸릴 수 있습니다.");

					if (showYesOrNoDialog.isPresent()) {

						if ("N".equals(showYesOrNoDialog.get().getValue())) {
							return;
						}
					}
				}

				this.txtImageLocation.setText(showFileDialog.getAbsolutePath());

				byte[] byteArray = FileUtil.toByteArray(showFileDialog);
				this.txtBase64.setText(Base64.getEncoder().encodeToString(byteArray));

				this.IvImage.setImage(new Image(new FileInputStream(showFileDialog)));
			} catch (IOException e) {
				DialogUtil.showExceptionDailog(e);
			}
		}
	}
}
