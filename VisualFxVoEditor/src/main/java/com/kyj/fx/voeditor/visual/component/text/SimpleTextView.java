/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2015. 10. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import java.io.File;
import java.nio.charset.Charset;

import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.fxmisc.richtext.CodeArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.PrimaryStageCloseable;
import com.kyj.fx.voeditor.visual.framework.handler.ExceptionHandler;
import com.kyj.fx.voeditor.visual.framework.word.AsynchWordExecutor;
import com.kyj.fx.voeditor.visual.framework.word.ContentMimeHtmlAdapter;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;

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

	/**
	 * 나모형태를 웹형태로 바꿔서 뷰를 보여줌
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 29.
	 */
	@FXML
	public void miOpenNamoMsWordOnAction() {
		String content = codeArea.getText();
		try {
			AsynchWordExecutor executor = new AsynchWordExecutor(new ContentMimeHtmlAdapter(content));
			executor.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Mime 데이터타입을 Html컨텐츠형태로 조회하기 위한 웹 뷰를 오픈함.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 29.
	 */
	@FXML
	public void miOpenNamoWebViewOnAction(){

		String content = codeArea.getText();

		try {
			WebView webView = new WebView();

//			String encodeToString = Base64.getEncoder().encodeToString(content.getBytes());
//
//			byte[] decode = Base64.getDecoder().decode(encodeToString);
//			String string = new String(decode);
//			MimeBodyPart part = new MimeBodyPart(new ByteArrayInputStream(content.getBytes()));
//			Enumeration allHeaders = part.getAllHeaders();
//			while(allHeaders.hasMoreElements())
//			{
//				javax.mail.Header nextElement = (Header) allHeaders.nextElement();
//
//				System.out.println(nextElement.getName());
//				System.out.println(nextElement.getValue());
//			}
//
//			DataHandler dataHandler = part.getDataHandler();
//			Object transferData = dataHandler.getTransferData(new DataFlavor("text/html"));
//			System.out.println(transferData);
//			ContentMimeHtmlAdapter adapter = new ContentMimeHtmlAdapter(content);
//			File tempFile = adapter.toTempFile();


//			byte[] decode = Base64.getMimeDecoder().decode(content.getBytes("UTF-8"));
//			String string = new String(decode);

			FxUtil.createStageAndShow(webView, stage->{
				stage.setAlwaysOnTop(true);
				stage.initOwner(getScene().getWindow());
				stage.focusedProperty().addListener((oba,o, n ) ->{
					if(!n)
						stage.close();

				});
			});

			javafx.application.Platform.runLater(() -> {
				try {
//					LOGGER.debug("Temp File Dir {} "  , tempFile.getAbsolutePath());
					webView.getEngine().load(content);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				webView.getEngine().loadContent(content, "text/html");
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@FXML
	public void miOpenHtmlWordOnAction(){
		String content = codeArea.getText();
		try {
			AsynchWordExecutor executor = new AsynchWordExecutor(new ContentMimeHtmlAdapter(content));
			executor.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@FXML
	public void miOpenHtmlWevViewOnAction() {}

	/**
	 * menuItem 다른이름으로 저장.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 27.
	 */
	@FXML
	public void miSaveAsOnAction(){
		File saveAs = DialogUtil.showFileSaveCheckDialog(getScene().getWindow(), chooser->{});
		if(saveAs!=null && saveAs.exists())
		{
			FileUtil.writeFile(saveAs, codeArea.getText(), Charset.forName("UTF-8"), err -> LOGGER.error(ValueUtil.toString(err)));
		}
	}
}
