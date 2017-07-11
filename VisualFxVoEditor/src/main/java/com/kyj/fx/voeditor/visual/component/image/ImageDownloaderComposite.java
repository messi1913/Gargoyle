/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.image
 *	작성일   : 2017. 5. 12.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.image;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLImageElement;

import com.jfoenix.controls.JFXTextField;
import com.kyj.fx.voeditor.visual.component.console.WebViewConsole;
import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.IdGenUtil;
import com.kyj.fx.voeditor.visual.util.RequestUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import jfxtras.scene.layout.HBox;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "ImageDownloaderView.fxml", isSelfController = true)
public class ImageDownloaderComposite extends BorderPane {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImageDownloaderComposite.class);

	private JFXMasonryPaneWrapper<NodeWrapper> fpCont;
	// @FXML
	// private BorderPane fpCont2;
	@FXML
	private JFXTextField txtUrl;

	private WebViewConsole wbConsole;
	private WebView wb;

	@FXML
	private SplitPane sp;

	private Button btnDownload;

	public ImageDownloaderComposite() {

		FxUtil.loadRoot(ImageDownloaderComposite.class, this, err -> LOGGER.error(ValueUtil.toString(err)));
	}

	@FXML
	public void initialize() {

		fpCont = new JFXMasonryPaneWrapper<>();
		fpCont.setStyle("-fx-background-color : white");
		fpCont.setHSpacing(3d);
		fpCont.setVSpacing(3d);

		btnDownload = new Button("다운로드");
		HBox hBox = new HBox(btnDownload);

		BorderPane borderPane = new BorderPane();
		borderPane.setTop(hBox);
		borderPane.setCenter(fpCont);

		ScrollPane scrollPane = new ScrollPane(borderPane);
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);

		this.wb = new WebView();

		// this.wbConsole = new WebViewConsole(wb);
		sp.getItems().add(wb);
		// sp.getItems().add(this.wbConsole);
		sp.getItems().add(scrollPane);

		this.wb.setOnKeyPressed(ev -> {

			if (ev.getCode() == KeyCode.F12) {
				FxUtil.createStageAndShow(new WebViewConsole(wb));
			}
		});
		// fpCont.setCache(false);
		// fpCont2.setCache(false);
		wb.getEngine().setJavaScriptEnabled(true);

		// wb.getEngine().setPromptHandler(new Callback<PromptData, String>() {
		//
		// @Override
		// public String call(PromptData param) {
		//
		// System.out.println(param);
		// System.out.println(param.getDefaultValue());
		// return param.getDefaultValue();
		// }
		// });

		wb.getEngine().documentProperty().addListener(new ChangeListener<Document>() {

			@Override
			public void changed(ObservableValue<? extends Document> observable, Document oldValue, Document newValue) {
				if (newValue != null) {
					NodeList elementsByTagName = newValue.getElementsByTagName("img");
					System.out.println("#########################################");
					System.out.println(newValue);
					System.out.println(elementsByTagName.getLength());
					System.out.println("#########################################");
				}

			}
		});

		wb.getEngine().getLoadWorker().workDoneProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				// System.out.println(newValue.intValue());

				if (newValue.intValue() == 0) {
					// fpCont.getFlowChildrens().clear();
					fpCont.getChildren().clear();
				}

				if (newValue.intValue() == 100) {

					txtUrl.setText(wb.getEngine().getDocument().getDocumentURI());
					System.out.println("asdasdasdsdasdasd!!!!!!!!!!!!!!!!!");
					Document document = wb.getEngine().getDocument();

					String domainName = wb.getEngine().executeScript("document.domain").toString();
					String protocol = wb.getEngine().executeScript("location.protocol").toString();

					LOGGER.debug("protocol : {} domain : {}", protocol, domainName);

					// System.out.println(document.getOwnerDocument().getDocumentURI());
					// System.out.println(document.getOwnerDocument().getBaseURI());
					System.out.println(document.getDocumentURI());
					NodeList elementsByTagName = wb.getEngine().getDocument().getElementsByTagName("img");
					int length = elementsByTagName.getLength();

					for (int i = 0; i < length; i++) {

						HTMLImageElement item = (HTMLImageElement) elementsByTagName.item(i);

						// System.out.println(item.getAttribute("currentSrc"));
						Attr attributeNode = item.getAttributeNode("src");
						String src = item.getAttribute("src");
						// NodeList childNodes = attributeNode.getChildNodes();

						try {

							if (attributeNode == null)
								continue;

							String textContent = attributeNode.getTextContent();
							boolean isBase64 = false;
							try {

								Node node = null;
								if (textContent.startsWith("data:image")) {
									textContent = textContent.replace("data:image/jpeg;base64,", "");
									byte[] decode = null;
									decode = Base64.getMimeDecoder().decode(textContent);
									InputStream is = new ByteArrayInputStream(decode);
									ImageView e = new ImageView(new Image(is, Double.parseDouble(item.getWidth()),
											Double.parseDouble(item.getHeight()), false, false));

									isBase64 = true;
									node = e;

								} else {
									WebView wb2 = new WebView();
									wb2.setContextMenuEnabled(false);
									wb2.getEngine().setJavaScriptEnabled(false);
									wb2.setStyle("-fx-background-color : blue; -fx-opticity : 0.5");
									wb2.setPrefSize(200d, 140d);
									wb2.setMaxSize(200d, 140d);
									// wb2.setPickOnBounds(true);
									wb2.setMouseTransparent(true);

									if (textContent.startsWith("/")) {
										textContent = protocol + "//" + domainName + textContent;
										LOGGER.debug("start with / {} ", textContent);
										wb2.getEngine().load(textContent);
									} else if (textContent.startsWith("http") || textContent.startsWith("https")) {
										LOGGER.debug("full url : {} ", textContent);
										wb2.getEngine().load(textContent);
									} else {
										LOGGER.debug("else url : {} ", textContent);
										wb2.getEngine().load(textContent);
									}

									
									node = wb2;
								}

								NodeWrapper e = new NodeWrapper(node, textContent, isBase64);
								e.setPrefSize(200d, 140d);
								e.setMaxSize(200d, 140d);
								e.setPickOnBounds(true);
								fpCont.getChildren().add(e);

							} catch (IllegalArgumentException e) {

								WebView wb2 = new WebView();
								wb2.setPrefSize(200d, 140d);
								wb2.setMaxSize(200d, 140d);
								wb2.setStyle("-fx-background-color : transparent; -fx-opticity : 0.5");
								wb2.getEngine().load(textContent);
								// wb2.setPickOnBounds(true);
								fpCont.getChildren().add(new VBox(wb2));
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			}
		});

		this.btnDownload.setOnAction(this::btnDownloadOnAction);
	}

	public void btnDownloadOnAction(ActionEvent e) {

		File showDirectoryDialog = DialogUtil.showDirectoryDialog(FxUtil.getWindow(this));
		if (showDirectoryDialog != null) {
			ObservableList<NodeWrapper> checkedItems = fpCont.getCheckedModel().getCheckedItems();
			checkedItems.forEach(n -> {
				String content = n.getContent();
				if (n.isBase64()) {

				} else {
					try {

//						WebView w = (WebView) n.getCenter();
//						System.out.println(w.getEngine().executeScript("document.images[0].src"));

						//
//						BiFunction<InputStream, Integer, Object> res = (is, code) -> {
//
//							try {
//								FileUtil.copy2(is, new File(showDirectoryDialog, IdGenUtil.generate() + ".png"));
//							} catch (IOException e1) {
//								e1.printStackTrace();
//							}
//
//							return null;
//						};
//						
//
//						RequestUtil.CookieBase.request(content, null, res);
						BiFunction<InputStream, Charset, Object> res2 = (is, code) -> {

							try {
								FileUtil.copy2(is, new File(showDirectoryDialog, IdGenUtil.generate() + ".png"));
							} catch (IOException e1) {
								e1.printStackTrace();
							}

							return null;
						};
						RequestUtil.req200(new URL(content), res2, true);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
		}

	}

	@FXML
	public void btnSrchOnAction() throws Exception {
		String url = txtUrl.getText();

		// dummy request
		RequestUtil.CookieBase.request(url, null, (is, code) -> {
			return null;
		});

		wb.getEngine().load(url);

	}

	@FXML
	public void btnBack() {
		wb.getEngine().executeScript("hisotry.back()");
	}
}
