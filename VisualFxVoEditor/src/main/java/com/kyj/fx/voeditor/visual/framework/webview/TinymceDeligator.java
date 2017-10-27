/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.webview
 *	작성일   : 2017. 10. 25.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.webview;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.text.XMLEditor;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

/**
 * 
 * TinyMCE HTML 지원하기 위한 Wrapping 클래스.
 * 
 * @author KYJ
 *
 */
public class TinymceDeligator {

	private static final String TYNYMCE_LOCATION = "javascript/tinymce/index.html";

	private static final Logger LOGGER = LoggerFactory.getLogger(TinymceDeligator.class);

	private WebView webview;

	/**
	 * 메일관련 webview가 로드되었는지 확인
	 * 
	 * @최초생성일 2017. 10. 18.
	 */
	private BooleanProperty webViewLoaded = new SimpleBooleanProperty();
	private BooleanProperty readOnlyProperty = new SimpleBooleanProperty(true);

	private final String initCont;

	/**
	 */
	protected TinymceDeligator() {
		this("");
	}

	/**
	 * @param initCont
	 */
	protected TinymceDeligator(String initCont) {
		webview = new WebView();
		this.initCont = initCont;
	}

	/**
	 * return webview <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 25.
	 * @return
	 */
	public WebView getWebView() {
		return this.webview;
	}

	/**
	 * 객체 생성 <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 25.
	 * @return this
	 */
	public static final TinymceDeligator createInstance() {
		return new TinymceDeligator("").initialize();
	}

	/**
	 * @최초생성일 2017. 10. 25.
	 */
	private EventHandler<WebErrorEvent> onErrorHandler = ev -> {
		LOGGER.error(ValueUtil.toString(ev.getException()));
	};

	/**
	 * @최초생성일 2017. 10. 25.
	 */
	private ChangeListener<State> stateChangeListener = new ChangeListener<State>() {

		@Override
		public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
			if (newValue == State.SUCCEEDED) {
				WebEngine engine = getWebView().getEngine();
				if (ValueUtil.isNotEmpty(initCont)) {
					engine.executeScript(" document.getElementById('gargoyle-textarea').innerHTML= '" + initCont + "'; ");
				}

				engine.getLoadWorker().stateProperty().removeListener(this);
				webViewLoaded.set(true);
			}
		}
	};

	/**
	 * @최초생성일 2017. 10. 25.
	 */
	private Callback<String, Boolean> confirmHandler = new Callback<String, Boolean>() {

		@Override
		public Boolean call(String param) {
			LOGGER.debug("confirm handler : {} ", param);
			return true;
		}
	};

	/**
	 * @최초생성일 2017. 10. 25.
	 */
	private Callback<PopupFeatures, WebEngine> createPopupHandler = new Callback<PopupFeatures, WebEngine>() {

		@Override
		public WebEngine call(PopupFeatures p) {

			Stage stage = new Stage(StageStyle.UTILITY);
			WebView wv2 = new WebView();
			VBox vBox = new VBox(5);
			vBox.getChildren().add(wv2);
			vBox.getChildren().add(new Button("업로딩"));
			wv2.getEngine().setJavaScriptEnabled(true);
			stage.setScene(new Scene(vBox));
			stage.show();
			return wv2.getEngine();
		}
	};

	/**
	 * @최초생성일 2017. 10. 25.
	 */
	private EventHandler<? super ContextMenuEvent> contextMenuRequestHandler = ev -> {

		MenuItem miReload = new MenuItem("Reload");
		miReload.setDisable(true);
		miReload.setOnAction(e -> {
			webview.getEngine().reload();
		});

		MenuItem miSource = new MenuItem("Source");
		miSource.setOnAction(e -> {
			Object executeScript = webview.getEngine().executeScript("document.documentElement.innerHTML");
			String htmlCoded = executeScript.toString();

			XMLEditor fxmlTextArea = new XMLEditor();
			fxmlTextArea.setContent(htmlCoded);
			FxUtil.createStageAndShow(fxmlTextArea, stage -> {
				stage.setWidth(1200d);
				stage.setHeight(800d);
				stage.initOwner(FxUtil.getWindow(webview));
			});
			// FxUtil.createStageAndShow("Source", new
			// WebViewConsole(view));

		});

		ContextMenu contextMenu = new ContextMenu(miReload, miSource);
		contextMenu.show(FxUtil.getWindow(webview), ev.getScreenX(), ev.getScreenY());

	};

	/**
	 * @최초생성일 2017. 10. 25.
	 */
	private ChangeListener<Boolean> readOnyChangeListener = new ChangeListener<Boolean>() {

		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

			// 기능이 정상작동하면 리스너를 없앰. 최초 로딩시 TinyMCE 로딩 지연때문에 처리
			if (!canWrite()) {
				ChangeListener<Boolean> listener = new ChangeListener<Boolean>() {

					@Override
					public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
						if (newValue) {

							if (newValue) {
								webview.getEngine().executeScript("tinymce.activeEditor.setMode('readonly');");
							} else {
								webview.getEngine().executeScript("tinymce.activeEditor.setMode('design');");
							}
							webViewLoaded.removeListener(this);
						}
					}
				};

				webViewLoaded.addListener(listener);
			} else {
				if (newValue) {
					webview.getEngine().executeScript("tinymce.activeEditor.setMode('readonly');");
				} else {
					webview.getEngine().executeScript("tinymce.activeEditor.setMode('design');");
				}
			}

		}
	};

	private EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>() {
		@Override
		public void handle(KeyEvent ev) {

			if (ev.isControlDown() && ev.getCode() == KeyCode.V) {
				pasteHandler(ev);
			}
		}
	};

	/**
	 * 붙여넣기 핸들링 <br/>
	 * 
	 * 10.26 이미지 붙여넣기
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 26.
	 * @param ev
	 */
	protected void pasteHandler(KeyEvent ev) {
		Clipboard systemClipboard = Clipboard.getSystemClipboard();
		Set<DataFormat> contentTypes = systemClipboard.getContentTypes();
		LOGGER.debug("{}", contentTypes);

		List<File> files = systemClipboard.getFiles();

		if (systemClipboard.getImage() != null) {
			pasteImage(systemClipboard.getImage());
		} else if (files != null) {

			for (File f : files) {
				try {
					String contentType = Files.probeContentType(f.toPath());
					LOGGER.debug(contentType);
					if (contentType.startsWith("image/")) {
						pasteImage(contentType, FileUtil.getBytes(f));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void pasteImage(Image img) {
		try {
			LOGGER.debug("image.");
			BufferedImage in = SwingFXUtils.fromFXImage(img, null);
			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				ImageIO.write(in, "png", out);
				String imageData = Base64.getEncoder().encodeToString(out.toByteArray());
				setText(String.format("<img src=data:image/jpeg;base64,%s></img>", imageData));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void pasteImage(String contentType, byte[] bytes) {
		try {
			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				String imageData = Base64.getEncoder().encodeToString(bytes);
				setText(String.format("<img src=data:" + contentType + ";base64,%s></img>", imageData));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 생성자로 객체생성후 initialize 호출.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 25.
	 */
	protected TinymceDeligator initialize() {
		try {

			WebEngine engine = webview.getEngine();

			// Define Context Menu.
			webview.setContextMenuEnabled(false);
			webview.setOnContextMenuRequested(contextMenuRequestHandler);

			webview.addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);

			// Define Events.
			engine.setOnError(onErrorHandler);
			engine.getLoadWorker().stateProperty().addListener(stateChangeListener);
			engine.setConfirmHandler(confirmHandler);
			engine.setCreatePopupHandler(createPopupHandler);

			// Load HTML
			engine.load(new File(TYNYMCE_LOCATION).toURI().toURL().toExternalForm());
		} catch (MalformedURLException e) {
			LOGGER.error(ValueUtil.toString(e));
		}

		// Read Property Chagne Listener.
		readOnlyProperty.addListener(readOnyChangeListener);
		return this;
	}

	/**
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 25.
	 * @param flag
	 *            true : readonly <br/>
	 *            false : can write <br/>
	 */
	public void setReadOnly(boolean flag) {
		this.readOnlyProperty.set(flag);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 25.
	 * @return
	 */
	public boolean canWrite() {
		return webViewLoaded.get();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 25.
	 * @param content
	 */
	public void setText(String content) {
		// 기능이 정상작동하면 리스너를 없앰. 최초 로딩시 TinyMCE 로딩 지연때문에 처리
		if (!canWrite()) {
			ChangeListener<Boolean> listener = new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					if (newValue) {

						StringBuffer sb = new StringBuffer();
						sb.append("setTimeout(function(){\n");
						sb.append(String.format("tinymce.activeEditor.setContent(\"%s\");", content));
						sb.append("tinymce.activeEditor.focus();\n");
						sb.append(" \n");
						sb.append("},100)\n");
						// LOGGER.debug(sb.toString());
						LOGGER.debug("setTimeout");
						webview.getEngine().executeScript(sb.toString());
						webViewLoaded.removeListener(this);
					}
				}
			};
			LOGGER.debug("Add Listener.");
			webViewLoaded.addListener(listener);
		} else {
			LOGGER.debug("Apply Text {} ", content);
			webview.getEngine().executeScript(String.format("tinymce.activeEditor.setContent(\"%s\");", content));
		}

	}
}
