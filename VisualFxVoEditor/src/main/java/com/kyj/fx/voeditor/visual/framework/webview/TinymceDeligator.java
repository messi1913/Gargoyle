/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.webview
 *	작성일   : 2017. 10. 25.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.webview;

import java.io.File;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.text.XMLEditor;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

/**
 * @author KYJ
 *
 */
public class TinymceDeligator {

	private static final Logger LOGGER = LoggerFactory.getLogger(TinymceDeligator.class);

	private WebView webview;

	/**
	 * 메일관련 webview가 로드되었는지 확인
	 * 
	 * @최초생성일 2017. 10. 18.
	 */
	private BooleanProperty webViewLoaded = new SimpleBooleanProperty();
	private final String initCont;

	public TinymceDeligator() {
		this("");
	}

	/**
	 */
	public TinymceDeligator(String initCont) {
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
	 * 생성자로 객체생성후 initialize 호출.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 25.
	 */
	public TinymceDeligator initialize() {
		try {

			webview.setContextMenuEnabled(false);
			webview.setOnContextMenuRequested(ev -> {
				
				MenuItem miReload = new MenuItem("Reload");
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
					});
					// FxUtil.createStageAndShow("Source", new
					// WebViewConsole(view));

				});

				ContextMenu contextMenu = new ContextMenu(miReload, miSource);
				contextMenu.show(FxUtil.getWindow(webview), ev.getScreenX(), ev.getScreenY());

			});

			WebEngine engine = webview.getEngine();

			engine.setOnError(ev -> {
				LOGGER.error(ValueUtil.toString(ev.getException()));
			});

			ChangeListener<State> listener = new ChangeListener<State>() {

				@Override
				public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
					if (newValue == State.SUCCEEDED) {

						if (ValueUtil.isNotEmpty(initCont)) {
							engine.executeScript(" document.getElementById('gargoyle-textarea').innerHTML= '" + initCont + "'; ");
						}

						engine.getLoadWorker().stateProperty().removeListener(this);
						webViewLoaded.set(true);
					}
				}
			};

			engine.getLoadWorker().stateProperty().addListener(listener);

			engine.setConfirmHandler(new Callback<String, Boolean>() {

				@Override
				public Boolean call(String param) {
					System.out.println("confirm handler : " + param);
					return true;
				}
			});

			engine.setCreatePopupHandler(new Callback<PopupFeatures, WebEngine>() {

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
			});

			engine.load(new File("javascript/tinymce/index.html").toURI().toURL().toExternalForm());

		} catch (MalformedURLException e) {
			LOGGER.error(ValueUtil.toString(e));
		}

		return this;
	}

	public void setReadOnly(boolean flag) {

		if (!canWrite()) {
			ChangeListener<Boolean> listener = new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					if (newValue) {

						if (flag) {
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
			if (flag) {
				webview.getEngine().executeScript("tinymce.activeEditor.setMode('readonly');");
			} else {
				webview.getEngine().executeScript("tinymce.activeEditor.setMode('design');");
			}
		}
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

						webview.getEngine().executeScript(sb.toString());
						webViewLoaded.removeListener(this);
					}
				}
			};
			webViewLoaded.addListener(listener);
		} else {
			webview.getEngine().executeScript(String.format("tinymce.activeEditor.setContent(\"%s\");", content));
		}

	}
}
