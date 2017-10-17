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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.NumberingCellValueFactory;
import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.framework.mail.Mail;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * 
 * 메일 보내기 기능을 지원하는 뷰
 * 
 * @author KYJ
 *
 */
@FXMLController(value = "MailView.fxml", isSelfController = true)
class MailViewComposite extends BorderPane implements Closeable {

	private static final Logger LOGGER = LoggerFactory.getLogger(MailViewComposite.class);
	@FXML
	private TextField txtSubject, txtRecipient;
	@FXML
	private WebView wbAprvCont;
	@FXML
	private TableView<MailReceiver> tbReceiver;
	@FXML
	private ListView<File> tbAttachment;
	@FXML
	private TableColumn<MailReceiver, Integer> tcNo;
	@FXML
	private TableColumn<MailReceiver, String> tcEmail, tcType;

	@FXML
	private RadioButton rbTo, rboCc, rboBcc;

	@FXML
	private CheckBox chkAttachments;

	@FXML
	private Button btnSearch, btnDel;

	@FXML
	private Label lblAttachmentCount;

	private Mail mail;

	private WebEngine engine;
	private AttachmentHandler attachmentHandler;
	private SendMailHandler sendMailHandler;
	private RecipientHandler recipientHandler;

	public MailViewComposite() {
		this.mail = new Mail();
		FxUtil.loadRoot(MailViewComposite.class, this, err -> {
			LOGGER.error(ValueUtil.toString(err));
		});
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 16.
	 * @return
	 */
	private RecipientType getSelectedType() {
		if (rbTo.isSelected()) {
			return RecipientType.TO;
		} else if (rboCc.isSelected())
			return RecipientType.CC;
		else if (rboBcc.isSelected())
			return RecipientType.BCC;
		return RecipientType.TO;
	}

	@FXML
	public void initialize() {
		mail.mailSubjectProperty().bind(txtSubject.textProperty());
		attachmentHandler = new AttachmentHandler(this);
		sendMailHandler = new SendMailHandler(this);
		recipientHandler = new RecipientHandler(this);

		try {
			engine = wbAprvCont.getEngine();
			engine.load(new File("tinymce/index.html").toURI().toURL().toExternalForm());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		tcNo.setCellValueFactory(new NumberingCellValueFactory<>(tbReceiver));
		tcEmail.setCellValueFactory(param -> {
			return param.getValue().emailProperty();
		});

		tcType.setCellValueFactory(param -> {
			return param.getValue().typeProperty();
		});

		chkAttachments.selectedProperty().addListener(chkAttachmentsListener);
		tbAttachment.getItems().addListener(tbAttachmentListChangeListener);

	}

	/**
	 * 첨부파일 리스트
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 17.
	 * @return
	 */
	public List<File> getAttachments() {
		return tbAttachment.getItems();
	}

	public List<MailReceiver> getRecipients() {
		return tbReceiver.getItems();
	}

	public final Mail getMail() {
		return mail;
	}

	private ListChangeListener<File> tbAttachmentListChangeListener = new ListChangeListener<File>() {

		@Override
		public void onChanged(javafx.collections.ListChangeListener.Change<? extends File> c) {
			if (c.wasAdded() || c.wasRemoved() || c.wasUpdated()) {
				lblAttachmentCount.setText(String.valueOf(c.getList().size()));
			}
		}
	};

	private ChangeListener<Boolean> chkAttachmentsListener = new ChangeListener<Boolean>() {

		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			btnSearch.setDisable(newValue);
			btnDel.setDisable(newValue);
		}
	};

	@FXML
	public void btnSendOnAction() {
		Object executeScript = engine
				.executeScript(" function outText(){ return tinymce.activeEditor.getContent({format: 'raw'}); } outText(); ");

		// 본문 타입
		mail.setContentType("text/html");

		// 본문
		mail.setMailContent(executeScript.toString());

		// 첨부파일 처리

		this.attachmentHandler.build();
		this.recipientHandler.build();
		this.sendMailHandler.sendMail();
	}

	@FXML
	public void btnAddRecipientOnAction() {
		String email = txtRecipient.getText();
		if (ValueUtil.isNotEmpty(email)) {

			String regexMatch = ValueUtil.regexMatch(ValueUtil.EMAIL_VALIDATION_EXP, email);
			if (ValueUtil.isNotEmpty(regexMatch)) {
				tbReceiver.getItems().add(new MailReceiver(email, getSelectedType().name()));
				this.txtRecipient.setText("");
			}
		}
	}

	@FXML
	public void txtRecipientOnKeyPress(KeyEvent e) {
		if (e.getCode() == KeyCode.ENTER) {
			btnAddRecipientOnAction();
		}
	}

	@Override
	public void close() throws IOException {
		Platform.runLater(() -> {
			if (engine != null)
				engine.load("about:blank");
		});

	}
}
