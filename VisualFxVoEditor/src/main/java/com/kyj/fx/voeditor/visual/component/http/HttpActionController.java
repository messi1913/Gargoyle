/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.http
 *	작성일   : 2017. 9. 28.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.XMLFormatter;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * @author KYJ
 *
 */

public class HttpActionController {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpActionController.class);
	private BasicCookieStore cookieStore = new BasicCookieStore();

	@FXML
	private ComboBox<String> cboReqType, cboReceivedType;
	@FXML
	private TextField txtReqUrl;

	@FXML
	public void initialize() {

	}

	@FXML
	void btnSendOnAction(ActionEvent event) {

		String url = txtReqUrl.getText();
		String reqType = cboReqType.getValue();
		String receivedType = cboReceivedType.getValue();

		// 포멧 처리
		Function<String, String> format = str -> {
			String res = str;
			switch (receivedType) {
			case "XML":
				res = new com.kyj.fx.voeditor.visual.util.XMLFormatter().format(str);
				break;
			}

			return res;
		};
		
		// 데이터 처리
		Function<CloseableHttpResponse, String> convert = (res) -> {

			try {
				InputStream content = res.getEntity().getContent();
				return format.apply(ValueUtil.toString(content));
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
			return "";
		};
		switch (reqType) {

		case "GET":
			break;
		case "POST":
			break;
		}

	}

	private <T> T requestGet(String url, Header[] headers, Function<CloseableHttpResponse, T> res)
			throws ClientProtocolException, IOException {

		T rslt = null;
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
			rslt = res.apply(response);
		} finally {
			if (response != null)
				EntityUtils.consume(response.getEntity());
			if (httpclient != null) {
				httpclient.close();
			}
		}

		return rslt;

	}

	private <T> T requestPost(String url, Header[] headers, Map<String, String> data, Function<CloseableHttpResponse, T> res)
			throws ClientProtocolException, IOException {

		T rslt = null;
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

			List<NameValuePair> dataArr = new ArrayList<NameValuePair>();

			if (ValueUtil.isNotEmpty(data)) {
				Iterator<String> iterator = data.keySet().iterator();
				while (iterator.hasNext()) {
					String key = iterator.next();
					String value = data.get(key);
					dataArr.add(new BasicNameValuePair(key, value));
				}

			}

			UrlEncodedFormEntity sendEntityData = new UrlEncodedFormEntity(dataArr, "UTF-8");
			http.setEntity(sendEntityData);
			// 끝 서버로 보낼 데이터를 묶음.

			/* 프록시 체크 */
			// if (USE_PROXY) {
			// HttpHost proxy = new HttpHost(PROXY_URL, PROXY_PORT, "http");
			// response = httpclient.execute(proxy, http);
			// }
			// else {
			response = httpclient.execute(http);
			// }

			rslt = res.apply(response);

		} finally {

			if (response != null)
				EntityUtils.consume(response.getEntity());

			if (httpclient != null) {
				httpclient.close();
			}

		}

		return rslt;

	}
}
