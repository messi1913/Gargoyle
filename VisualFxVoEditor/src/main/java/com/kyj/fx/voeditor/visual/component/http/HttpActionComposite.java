/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.http
 *	작성일   : 2017. 9. 28.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.http.Header;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.fxmisc.richtext.CodeArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.voeditor.visual.component.text.CodeAreaHelper;
import com.kyj.fx.voeditor.visual.framework.contextmenu.FxContextManager;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.sun.codemodel.internal.util.UnicodeEscapeWriter;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "HttpActionComposite.fxml", css = "HttpActionComposite.css", isSelfController = true)
public class HttpActionComposite extends BorderPane {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpActionComposite.class);
	private BasicCookieStore cookieStore = new BasicCookieStore();

	@FXML
	private ComboBox<String> cboReqType, cboReceivedType;
	@FXML
	private TextField txtReqUrl;
	@FXML
	private Label lblStatus;
	@FXML
	private TableView<FxBasicHeader> tbhHeaders;
	@FXML
	private TableColumn<FxBasicHeader, String> colHeaderKey, colHeaderValue;
	@FXML
	private CodeArea txtRequest, txtResponse;

	public HttpActionComposite() {
		FxUtil.loadRoot(HttpActionComposite.class, this, System.out::println);
	}

	class FxBasicHeader {
		private StringProperty key;
		private StringProperty value;

		public FxBasicHeader() {
			key = new SimpleStringProperty();
			value = new SimpleStringProperty();
		}

		public final StringProperty keyProperty() {
			return this.key;
		}

		public final String getKey() {
			return this.keyProperty().get();
		}

		public final void setKey(final String key) {
			this.keyProperty().set(key);
		}

		public final StringProperty valueProperty() {
			return this.value;
		}

		public final String getValue() {
			return this.valueProperty().get();
		}

		public final void setValue(final String value) {
			this.valueProperty().set(value);
		}

	}

	@FXML
	public void initialize() {
		tbhHeaders.setEditable(true);
		colHeaderKey.setEditable(true);
		colHeaderValue.setEditable(true);

		colHeaderKey.setCellFactory(TextFieldTableCell.forTableColumn());
		colHeaderKey.setCellValueFactory(val -> {
			return val.getValue().keyProperty();
		});
		colHeaderValue.setCellFactory(TextFieldTableCell.forTableColumn());
		colHeaderValue.setCellValueFactory(val -> {
			return val.getValue().valueProperty();
		});

		// CodeAreaHelper : 기본 텍스트 기능 지원.
		new CodeAreaHelper(txtRequest);
		new CodeAreaHelper(txtResponse);

		// 클립보드 기능
		FxUtil.installClipboardKeyEvent(tbhHeaders);
		// 컨텍스트 기능
		FxUtil.installContextMenu(contextMenu());
	}

	/**
	 * Fx 테이블 뷰 컨텍스트 메뉴 정의 .
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 29.
	 * @return
	 */
	private FxContextManager contextMenu() {
		MenuItem miAdd = new MenuItem("Add");
		MenuItem miRemove = new MenuItem("Remove");

		miAdd.setOnAction(ev -> {
			tbhHeaders.getItems().add(new FxBasicHeader());
		});

		miRemove.setOnAction(ev -> {
			ObservableList<FxBasicHeader> selectedItems = tbhHeaders.getSelectionModel().getSelectedItems();
			tbhHeaders.getItems().removeAll(selectedItems);
		});

		return new FxContextManager(tbhHeaders, miAdd, miRemove);
	}

	@FXML
	void btnSendOnAction(ActionEvent event) {

		/* 17.12.04 URL Encoding 수정 */
		String _url;
		try {
			URL url = new URL(txtReqUrl.getText());
			String query = url.getQuery();
			String host = url.getHost();
			String protocol = url.getProtocol();
			if (query != null) {
				query = URLEncoder.encode(query, "UTF-8");
				_url = protocol + "://" + host + "?" + query;
			} else {
				_url = url.toExternalForm();
			}

		} catch (MalformedURLException e) {
			_url = txtReqUrl.getText();
		} catch (UnsupportedEncodingException e) {
			_url = txtReqUrl.getText();
		}
		String url = _url;
		LOGGER.debug(url);
		String reqType = cboReqType.getValue();
		String receivedType = cboReceivedType.getValue();

		// 포멧 처리
		Function<byte[], String> format = b -> {

			Runnable run = null;
			switch (receivedType) {
			case "XML":
				run = () -> {
					try {
						txtResponse.replaceText(new com.kyj.fx.voeditor.visual.util.XMLFormatter().format(new String(b)));
					} catch (Exception e) {
						txtResponse.replaceText(new String(b));
					}
				};

				break;
			case "TEXT":
				run = () -> {
					txtResponse.replaceText(new String(b));
				};
			}

			Platform.runLater(run);
			return "";
		};

		// 데이터 처리
		Consumer<CloseableHttpResponse> convert = (res) -> {

			int statusCode = res.getStatusLine().getStatusCode();

			Platform.runLater(() -> {
				lblStatus.setText(String.valueOf(statusCode) + " " + res.getStatusLine().getReasonPhrase());
			});

			if (statusCode == 200) {
				try {
					InputStream is = res.getEntity().getContent();

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}

					ByteArrayOutputStream out = new ByteArrayOutputStream();
					int tmp = -1;
					while ((tmp = is.read()) != -1) {
						out.write(tmp);
					}

					format.apply(out.toByteArray());
				} catch (IllegalStateException | IOException e) {
					e.printStackTrace();
				}
			}

		};

		Header[] array = tbhHeaders.getItems().stream().map(v -> {
			return new BasicHeader(v.getKey(), v.getValue());
		}).toArray(Header[]::new);

		FxUtil.showLoading(new Task<Void>() {

			@Override
			protected Void call() throws Exception {

				switch (reqType) {

				case "GET":
					requestGet(url, array, convert);

					break;
				case "POST":
					requestPost(url, array, () -> {

						AbstractHttpEntity entity = null;
						String cont = txtRequest.getText();
						if (ValueUtil.isNotEmpty(cont)) {
							entity = new StringEntity(cont, "UTF-8");
						} else {
							try {
								entity = new UrlEncodedFormEntity(Collections.emptyList(), "UTF-8");
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
						}

						return entity;
					}, convert);
					break;
				}
				return null;
			}

		});

	}

	/***********************************************************************************/
	/*
	 * 데이터 통신 처리 함수 정의.
	 */
	/***********************************************************************************/

	private void requestGet(String url, Header[] headers, Consumer<CloseableHttpResponse> res) {

		CloseableHttpResponse response = null;
		CloseableHttpClient httpclient = null;

		// 시작 쿠키관리
		List<Cookie> cookies = cookieStore.getCookies();
		LOGGER.debug("Get cookies...  : " + cookies);
		try {
			HttpGet http = new HttpGet(url);
			LOGGER.debug(url);
			if (headers != null) {
				for (Header header : headers) {
					if (header == null)
						continue;
					http.addHeader(header);
				}
			}
			httpclient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
			response = httpclient.execute(http);
			res.accept(response);
		} catch (Exception e) {
			lblStatus.setText(e.getMessage());
			txtResponse.replaceText(ValueUtil.toString(e));
		} finally {
			if (response != null)
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					LOGGER.error(ValueUtil.toString(e));
				}
			if (httpclient != null) {
				try {
					httpclient.close();
				} catch (IOException e) {
					LOGGER.error(ValueUtil.toString(e));
				}
			}
		}

	}

	private void requestPost(String url, Header[] headers, Supplier<AbstractHttpEntity> enti, Consumer<CloseableHttpResponse> res) {

		CloseableHttpResponse response = null;
		CloseableHttpClient httpclient = null;

		// 시작 쿠키관리
		List<Cookie> cookies = cookieStore.getCookies();
		LOGGER.debug("Get cookies...  : " + cookies);

		try {

			HttpEntityEnclosingRequestBase http = new HttpPost(url);
			LOGGER.debug(url);

			if (headers != null) {
				for (Header header : headers) {
					if (header == null)
						continue;
					http.addHeader(header);
				}
			}

			httpclient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
			// httpclient = HttpClients.createDefault();

			// 시작 서버로 보낼 데이터를 묶음.

			// StringEntity entity = new StringEntity(data, "UTF-8");
			http.setEntity(enti.get());

			// 끝 서버로 보낼 데이터를 묶음.

			/* 프록시 체크 */
			// if (USE_PROXY) {
			// HttpHost proxy = new HttpHost(PROXY_URL, PROXY_PORT, "http");
			// response = httpclient.execute(proxy, http);
			// }
			// else {
			response = httpclient.execute(http);
			// }

			LOGGER.debug("Response headers ...   ");
			Stream.of(response.getAllHeaders()).forEach(h -> {
				LOGGER.debug("[res] k : {}  v : {} ", h.getName(), h.getValue());
			});

			res.accept(response);

		} catch (Exception e) {
			lblStatus.setText(e.getMessage());
			LOGGER.error(ValueUtil.toString(e));
		} finally {

			if (response != null)
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					LOGGER.error(ValueUtil.toString(e));
				}

			if (httpclient != null) {
				try {
					httpclient.close();
				} catch (IOException e) {
					LOGGER.error(ValueUtil.toString(e));
				}
			}

		}

	}
}
