/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.mail
 *	작성일   : 2017. 10. 10.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.framework.mail.Mail;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "MailView.fxml", isSelfController = true)
public class MailViewComposite extends BorderPane {

	private static final Logger LOGGER = LoggerFactory.getLogger(MailViewComposite.class);
	@FXML
	private TextField txtSubject;
	@FXML
	private WebView wbAprvCont;

	private Mail mail ;

	public MailViewComposite() {
		this.mail = new Mail();
		FxUtil.loadRoot(MailViewComposite.class, this, err -> {
			LOGGER.error(ValueUtil.toString(err));
		});
	}

	@FXML
	public void initialize() {
		txtSubject.textProperty().bind(mail.mailSubjectProperty());

	}
}
