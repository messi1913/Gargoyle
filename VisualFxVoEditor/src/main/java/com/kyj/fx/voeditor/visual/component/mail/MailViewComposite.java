/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.mail
 *	작성일   : 2017. 10. 10.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.mail;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.framework.mail.Mail;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "MailView.fxml", isSelfController = true)
class MailViewComposite extends BorderPane implements Closeable {

	private static final Logger LOGGER = LoggerFactory.getLogger(MailViewComposite.class);
	@FXML
	private TextField txtSubject;
	@FXML
	private WebView wbAprvCont;

	private Mail mail;

	private WebEngine engine;

	public MailViewComposite() {
		this.mail = new Mail();
		FxUtil.loadRoot(MailViewComposite.class, this, err -> {
			LOGGER.error(ValueUtil.toString(err));
		});
	}

	@FXML
	public void initialize() {
		txtSubject.textProperty().bind(mail.mailSubjectProperty());

		try {
			engine = wbAprvCont.getEngine();
			engine.load(new File("tinymce/index.html").toURI().toURL().toExternalForm());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void btnSendOnAction() {

	}

	@Override
	public void close() throws IOException {
		Platform.runLater(() -> {
			if (engine != null)
				engine.load("about:blank");
		});

	}
}
