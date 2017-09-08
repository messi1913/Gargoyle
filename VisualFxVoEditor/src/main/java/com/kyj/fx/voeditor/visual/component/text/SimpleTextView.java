/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2015. 10. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.apache.commons.lang3.StringEscapeUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Hex;

import com.google.gson.JsonSyntaxException;
import com.kyj.fx.voeditor.visual.framework.PrimaryStageCloseable;
import com.kyj.fx.voeditor.visual.framework.handler.ExceptionHandler;
import com.kyj.fx.voeditor.visual.framework.word.AbstractMimeAdapter;
import com.kyj.fx.voeditor.visual.framework.word.AsynchWordExecutor;
import com.kyj.fx.voeditor.visual.framework.word.HtmlTextToMimeAdapter;
import com.kyj.fx.voeditor.visual.framework.word.MimeToHtmlAdapter;
import com.kyj.fx.voeditor.visual.framework.word.NamoMimeToHtmlAdapter;
import com.kyj.fx.voeditor.visual.framework.word.SimpleWordAdapter;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.fx.voeditor.visual.util.XMLFormatter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
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
	protected CodeArea codeArea;

	protected CodeAreaHelper<CodeArea> helper;
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
		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
		initHelpers();
		initGraphics();

	}

	protected void initHelpers() {
		this.helper = new CodeAreaHelper<>(codeArea);
		this.helper.customMenuHandler(new CodeAreaCustomMenusHandler<CodeArea>() {

			@Override
			public void customMenus(CodeArea codeArea, ContextMenu contextMenu) {

				MenuItem e = new MenuItem("Show ApplicatioN Code");
				e.setOnAction(ev -> {
					FxUtil.EasyFxUtils.showApplicationCode(codeArea.getSelectedText());
				});
				contextMenu.getItems().add(e);

			}
		});
	}

	public final CodeAreaHelper<CodeArea> getHelper() {
		return this.helper;
	}

	protected void initGraphics() {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.kyj.fx.voeditor.visual.framework.PrimaryStageCloseable#closeRequest()
	 */
	@Override
	public void closeRequest() {
	}

	/**
	 * @return the helper
	 */
	// public final CodeAreaHelper<CodeArea> getHelper() {
	// return helper;
	// }

	/**
	 * 마임 형태를 웹형태로 바꿔서 뷰를 보여줌
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 29.
	 */
	@FXML
	public void miOpenNamoMsWordOnAction() {
		String content = codeArea.getText();
		try {
			AsynchWordExecutor executor = new AsynchWordExecutor(
					/* new HtmlTextToMimeAdapter(content) */ new HtmlTextToMimeAdapter(content));
			executor.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Mime 데이터타입을 Html컨텐츠형태로 조회하기 위한 웹 뷰를 오픈함.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 29.
	 */
	@FXML
	public void miOpenNamoWebViewOnAction() {

		String content = codeArea.getText();

		try {

			// new ContentMimeHtmlAdapter(content)

			// String encodeToString =
			// Base64.getEncoder().encodeToString(content.getBytes());
			//
			// byte[] decode = Base64.getDecoder().decode(encodeToString);
			// String string = new String(decode);
			// MimeBodyPart part = new MimeBodyPart(new
			// ByteArrayInputStream(content.getBytes()));
			// Enumeration allHeaders = part.getAllHeaders();
			// while(allHeaders.hasMoreElements())
			// {
			// javax.mail.Header nextElement = (Header)
			// allHeaders.nextElement();
			//
			// System.out.println(nextElement.getName());
			// System.out.println(nextElement.getValue());
			// }
			//
			// DataHandler dataHandler = part.getDataHandler();
			// Object transferData = dataHandler.getTransferData(new
			// DataFlavor("text/html"));
			// System.out.println(transferData);
			// ContentMimeHtmlAdapter adapter = new
			// ContentMimeHtmlAdapter(content);
			// File tempFile = adapter.toTempFile();

			// byte[] decode =
			// Base64.getMimeDecoder().decode(content.getBytes("UTF-8"));
			// String string = new String(decode);

			javafx.application.Platform.runLater(() -> {
				try {

					AbstractMimeAdapter adapter = new NamoMimeToHtmlAdapter(content);
					// FileWriter fileWriter = new FileWriter(new
					// File("sample.html"));
					String html = adapter.getContent();

					FxUtil.openBrowser(codeArea, html, false);
					// WebView webView = new WebView();
					//
					// webView.getEngine().loadContent(content2);
					//
					// webView.setOnKeyPressed(ev -> {
					// if (ev.getCode() == KeyCode.F12) {
					// WebViewConsole webViewConsole = new
					// WebViewConsole(webView);
					// FxUtil.createStageAndShow(webViewConsole, stage -> {
					//
					// stage.initOwner(FxUtil.getWindow(codeArea));
					//
					// });
					// }
					// });
					// FxUtil.createStageAndShow(webView, stage -> {
					// stage.setAlwaysOnTop(true);
					// stage.initOwner(getScene().getWindow());
					// stage.focusedProperty().addListener((oba, o, n) -> {
					// if (!n)
					// stage.close();
					//
					// });
					// });

				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@FXML
	public void miOpenHtmlWordOnAction() {
		String content = codeArea.getText();
		try {
			AsynchWordExecutor executor = new AsynchWordExecutor(new SimpleWordAdapter(content));
			executor.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void miOpenHtmlWevViewOnAction() {
		String content = codeArea.getText();

		try {
			WebView webView = new WebView();
			webView.getEngine().loadContent(content);

			FxUtil.createStageAndShow(webView, stage -> {
				stage.setAlwaysOnTop(true);
				stage.initOwner(getScene().getWindow());
				stage.focusedProperty().addListener((oba, o, n) -> {
					if (!n)
						stage.close();

				});
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * menuItem 다른이름으로 저장.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 27.
	 */
	@FXML
	public void miSaveAsOnAction() {
		FxUtil.saveAsFx(getScene().getWindow(), () -> codeArea.getText());

		// File saveAs =
		// DialogUtil.showFileSaveCheckDialog(getScene().getWindow(),
		// chooser->{});
		// if(saveAs!=null && saveAs.exists())
		// {
		// FileUtil.writeFile(saveAs, codeArea.getText(),
		// Charset.forName("UTF-8"), err ->
		// LOGGER.error(ValueUtil.toString(err)));
		// }
	}

	public CodeArea getCodeArea() {
		return this.codeArea;
	}

	@FXML
	public void miBase64EncodeOnAction() {
		String text = codeArea.getText();
		String encodeToString = Base64.getEncoder().encodeToString(text.getBytes());
		codeArea.getUndoManager().mark();
		codeArea.replaceText(encodeToString);
	}

	@FXML
	public void miBase64DecodeOnAction() {
		try {
			String text = codeArea.getText();
			byte[] b = Base64.getDecoder().decode(text.getBytes());
			codeArea.getUndoManager().mark();
			codeArea.replaceText(new String(b));
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
			DialogUtil.showMessageDialog(FxUtil.getWindow(this), "Invalide Base64 Content.");
		}

	}

	/**
	 * Mime 텍스트를 HTML 텍스트로 변환
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 14.
	 */
	@FXML
	public void miToHtmlCodeOnAction() {

		try {
			MimeToHtmlAdapter adapter = new MimeToHtmlAdapter(content);
			String content = adapter.getContent();
			codeArea.getUndoManager().mark();
			codeArea.replaceText(content);

		} catch (UnsupportedEncodingException e) {
			LOGGER.error(ValueUtil.toString(e));
			DialogUtil.showMessageDialog(FxUtil.getWindow(this), "Unsupported Encoding. ");
		}

	}

	@FXML
	public void prettyFormatOnAction() {
		try {
			String stringPrettyFormat = ValueUtil.toStringPrettyFormat(codeArea.getText());
			codeArea.getUndoManager().mark();
			codeArea.replaceText(stringPrettyFormat);
		} catch (JsonSyntaxException e) {
			// LOGGER.debug(ValueUtil.toString(e));
		}

	}

	@FXML
	public void toStringOnAction() {
		codeArea.getUndoManager().mark();
		byte[] decode = Hex.decode(codeArea.getText());
		codeArea.replaceText(new String(decode));
	}

	@FXML
	public void toHexOnAction() {
		codeArea.getUndoManager().mark();
		String str = codeArea.getText();
		codeArea.replaceText(String.valueOf(Hex.encode(str.getBytes())));
	}

	@FXML
	public void miEncodeOnAction() {
		codeArea.getUndoManager().mark();
		String str = codeArea.getText();
		codeArea.replaceText(StringEscapeUtils.escapeJava(str));
	}

	@FXML
	public void miDecodeOnAction() {
		codeArea.getUndoManager().mark();
		String str = codeArea.getText();
		codeArea.replaceText(StringEscapeUtils.unescapeJava(str));
	}

	@FXML
	public void miShowAppCodeOnAction() {
		FxUtil.EasyFxUtils.showApplicationCode(this.getCodeArea().getText());
	}

	@FXML
	public void miRemoveSpaciesOnAction() {
		codeArea.getUndoManager().mark();
		String str = codeArea.getText();
		char[] charArray = str.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < charArray.length; i++) {

			switch (charArray[i]) {
			case '\t':
				break;
			case '\n':
				break;
			case Character.UNASSIGNED:
				break;
			default:
				sb.append(charArray[i]);
			}

		}
		codeArea.replaceText(sb.toString());
	}

	/**
	 * XML 포멧 기능 확장
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 8.
	 */
	@FXML
	public void prettyXmlFormatOnAction() {
		try {
			String format = XMLFormatter.newInstnace().format(codeArea.getText());
			codeArea.getUndoManager().mark();
			codeArea.replaceText(format);
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}

	}

}
