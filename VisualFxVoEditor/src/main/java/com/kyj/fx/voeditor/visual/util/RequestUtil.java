/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 11. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KYJ
 *
 */
public class RequestUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestUtil.class);
	private static HostnameVerifier hostnameVerifier = (arg0, arg1) -> {
		return true;
	};

	public static String reqeustSSL_JSONString(URL url, BiFunction<InputStream, Integer, String> response) throws Exception {
		return requestSSL(url, (is, code) -> {
			String dirtyConent = "";
			if (200 == code) {
				// 버퍼로 그냥 읽어봐도 되지만 인코딩 변환을 추후 쉽게 처리하기 위해 ByteArrayOutputStream을
				// 사용

				try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
					// int read = -1;
					byte[] b = new byte[4096];
					while (is.read(b) != -1) {
						out.write(b);
					}
					out.flush();
					dirtyConent = out.toString("UTF-8");
				} catch (Exception e) {
					LOGGER.error(ValueUtil.toString(e));
				}
			} else {
				LOGGER.warn("not unnomal response code");
			}
			return dirtyConent;

		});
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 6.
	 * @param url
	 * @param response
	 * @param b
	 * @throws Exception
	 */
	public static <T> T req200(URL url, BiFunction<InputStream, Charset, T> response, boolean autoClose) throws Exception {
		String protocol = url.getProtocol();

		if ("http".equals(protocol))
			return request200(url, response, autoClose);
		else if ("https".equals(protocol)) {
			return reqeustSSL200(url, response, autoClose);
		}

		return reqeustSSL200(url, response, autoClose);
	}

	public static <T> T req(URL url, BiFunction<InputStream, Integer, T> response) throws Exception {
		String protocol = url.getProtocol();

		if ("http".equals(protocol))
			return request(url, response);
		else if ("https".equals(protocol)) {
			return requestSSL(url, response);
		}

		return reqeustSSL(url, response, true);
	}

	public static <T> T requestSSL(URL url, BiFunction<InputStream, Integer, T> response) throws Exception {
		return reqeustSSL(url, response, true);
	}

	public static <T> T reqeustSSL(URL url, BiFunction<InputStream, Integer, T> response, boolean autoClose) throws Exception {

		// SSLContext ctx = SSLContext.getInstance("TLS");

		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		InputStream is = null;
		T result = null;
		try {
			conn.setDefaultUseCaches(true);
			conn.setUseCaches(true);

			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:12.0) Gecko/20100101 Firefox/12.0");
			conn.setRequestProperty("Accept-Encoding", "UTF-8");
			// conn.setRequestProperty("Connection", "keep-alive");

			conn.setRequestProperty("Accept", "text/html");
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.setRequestProperty("Accept-Encoding", "UTF-8");
			conn.setRequestProperty("Accept-Language", "KR");
			// conn.setRequestProperty("Cache-Control", "no-store");
			// conn.setRequestProperty("Pragma", "no-cache");

			conn.setHostnameVerifier(hostnameVerifier);

			conn.setConnectTimeout(6000);

			conn.connect();

			is = conn.getInputStream();

			LOGGER.debug("code : [{}] [{}] URL : {} ,  ", conn.getResponseCode(), conn.getContentEncoding(), url.toString());

			result = response.apply(is, conn.getResponseCode());

		} finally {

			if (autoClose) {
				if (is != null)
					is.close();

				if (conn != null)
					conn.disconnect();
			}

		}

		return result;
	}

	public static <T> T reqeustSSL200(URL url, BiFunction<InputStream, Charset, T> response, boolean autoClose) throws Exception {

		// SSLContext ctx = SSLContext.getInstance("TLS");

		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		InputStream is = null;
		T result = null;
		try {
			conn.setDefaultUseCaches(true);
			conn.setUseCaches(true);

			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:12.0) Gecko/20100101 Firefox/12.0");
			conn.setRequestProperty("Accept-Encoding", "UTF-8");
			// conn.setRequestProperty("Connection", "keep-alive");

			conn.setRequestProperty("Accept", "text/html");
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.setRequestProperty("Accept-Encoding", "UTF-8");
			conn.setRequestProperty("Accept-Language", "KR");
			// conn.setRequestProperty("Cache-Control", "no-store");
			// conn.setRequestProperty("Pragma", "no-cache");

			conn.setHostnameVerifier(hostnameVerifier);

			conn.setConnectTimeout(6000);

			conn.connect();

			is = conn.getInputStream();

			String contentType = conn.getContentType();

			String charset = Stream.of(contentType.split(";")).filter(txt -> txt.toLowerCase().contains("charset")).findFirst().map(v -> {
				return v.substring(v.indexOf("=") + 1);
			}).get();

			LOGGER.debug("code : [{}] [{}] URL : {} ,  ", conn.getResponseCode(), conn.getContentEncoding(), url.toString());

			if (200 == conn.getResponseCode()) {
				result = response.apply(is, Charset.forName(charset));
			}

		} finally {

			if (autoClose) {
				if (is != null)
					is.close();

				if (conn != null)
					conn.disconnect();
			}

		}

		return result;
	}

	public static <T> T request(URL url, BiFunction<InputStream, Integer, T> response) throws Exception {
		return request(url, response, true);
	}

	public static <T> T request200(URL url, BiFunction<InputStream, Charset, T> response, boolean autoClose) throws Exception {
		return request200(url, null, response, autoClose);
	}

	public static <T> T request200(URL url, byte[] out, BiFunction<InputStream, Charset, T> response, boolean autoClose) throws Exception {

		URLConnection openConnection = url.openConnection();
		HttpURLConnection conn = (HttpURLConnection) openConnection;
		InputStream is = null;
		T result = null;
		try {

			conn.setDefaultUseCaches(true);
			conn.setUseCaches(true);

			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:12.0) Gecko/20100101 Firefox/12.0");
			conn.setRequestProperty("Accept-Encoding", "UTF-8");
			// conn.setRequestProperty("Connection", "keep-alive");

			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.setRequestProperty("Accept-Encoding", "UTF-8");
			conn.setRequestProperty("Accept-Language", "KR");

			conn.setConnectTimeout(6000);
			conn.setReadTimeout(6000);

			if (out != null) {
				conn.setDoOutput(true);
				OutputStream stream = conn.getOutputStream();
				stream.write(out);
			}

			conn.connect();

			is = conn.getInputStream();

			String contentType = conn.getContentType();

			String charset = "UTF-8";
			if (contentType != null) {
				charset = Stream.of(contentType.split(";")).filter(txt -> txt.toLowerCase().contains("charset")).findFirst().map(v -> {
					return v.substring(v.indexOf("=") + 1);
				}).get();
			}

			LOGGER.debug("code : [{}] [{}] URL : {} ,  ", conn.getResponseCode(), url.toString());

			if (200 == conn.getResponseCode()) {
				result = response.apply(is, Charset.forName(charset));
			}

		} finally {

			if (autoClose) {
				if (is != null)
					is.close();

				if (conn != null)
					conn.disconnect();
			}

		}
		return result;

	}

	public static <T> T request(URL url, BiFunction<InputStream, Integer, T> response, boolean autoClose) throws Exception {

		URLConnection openConnection = url.openConnection();
		HttpURLConnection conn = (HttpURLConnection) openConnection;
		InputStream is = null;
		T result = null;
		try {

			conn.setDefaultUseCaches(true);
			conn.setUseCaches(true);

			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:12.0) Gecko/20100101 Firefox/12.0");
			conn.setRequestProperty("Accept-Encoding", "UTF-8");
			// conn.setRequestProperty("Connection", "keep-alive");

			conn.setRequestProperty("Accept", "text/html");
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.setRequestProperty("Accept-Encoding", "UTF-8");
			conn.setRequestProperty("Accept-Language", "KR");

			conn.setConnectTimeout(6000);

			conn.connect();

			is = conn.getInputStream();
			// conn.getHeaderFields().entrySet().forEach(e -> {
			// LOGGER.debug("k : {} , v : {}", e.getKey(), e.getValue());
			// });

			LOGGER.debug("code : [{}] [{}] URL : {} ,  ", conn.getResponseCode(), url.toString());

			// LOGGER.debug(conn.getPermission().toString());
			result = response.apply(is, conn.getResponseCode());

		} finally {

			if (autoClose) {
				if (is != null)
					is.close();

				if (conn != null)
					conn.disconnect();
			}

		}
		return result;
	}

	public static class CookieBase {

		private static BasicCookieStore cookieStore = new BasicCookieStore();

		public static <T> T request(String url, Map<String, String> data, Function<CloseableHttpResponse, T> res) throws Exception {
			return request(url, null, data, res);
		}

		public static <T> Function<CloseableHttpResponse, T> forEntry(Function<HttpEntity, T> res) {
			return response -> {
				Stream.of(response.getAllHeaders()).forEach(header -> {
					LOGGER.debug("{} : {} ", header.getName(), header.getValue());
				});

				HttpEntity entity = response.getEntity();
				return res.apply(entity);
			};
		}

		public static <T> T request(String url, Header[] headers, Map<String, String> data, Function<CloseableHttpResponse, T> res /*, boolean storeCookie, boolean clearCookie*/)
				throws Exception {

			T rslt = null;
			CloseableHttpResponse response = null;
			CloseableHttpClient httpclient = null;

			// 시작 쿠키관리
			List<Cookie> cookies = cookieStore.getCookies();
			LOGGER.debug("Get cookies...  : " + cookies);

			// BasicClientCookie cookie = new BasicClientCookie("userId", (String)
			// SharedMemoryMap.getInstance().get(
			// SharedMemoryMap.USER_ID));
			// cookie.setDomain(url);
			// cookie.setPath("/");
			// cookieStore.addCookie(cookie);
			// 종료 쿠키관리

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
				//			if (USE_PROXY) {
				//				HttpHost proxy = new HttpHost(PROXY_URL, PROXY_PORT, "http");
				//				response = httpclient.execute(proxy, http);
				//			}
				//			else {
				response = httpclient.execute(http);
				//			}

				rslt = res.apply(response);

				//				Stream.of(response.getAllHeaders()).forEach(header -> {
				//					LOGGER.debug("{} : {} ", header.getName(), header.getValue());
				//				});

				//				HttpEntity entity = response.getEntity();

				//				rslt = res.apply(entity);
				//				rslt = res.apply(entity.getContent(), response.getStatusLine().getStatusCode());

			} finally {

				if (response != null)
					EntityUtils.consume(response.getEntity());

				if (httpclient != null) {
					httpclient.close();
				}

			}

			return rslt;

		}

		public static <T> T request(String url, Map<String, String> data, BiFunction<InputStream, Integer, T> res) throws Exception {
			return request(url, null, data, res);
		}

		public static <T> T request(String url, Header[] headers, Map<String, String> data, BiFunction<InputStream, Integer, T> res)
				throws Exception {

			T rslt = null;
			CloseableHttpResponse response = null;
			CloseableHttpClient httpclient = null;

			// 시작 쿠키관리
			List<Cookie> cookies = cookieStore.getCookies();
			LOGGER.debug("Get cookies...  : " + cookies);

			// BasicClientCookie cookie = new BasicClientCookie("userId", (String)
			// SharedMemoryMap.getInstance().get(
			// SharedMemoryMap.USER_ID));
			// cookie.setDomain(url);
			// cookie.setPath("/");
			// cookieStore.addCookie(cookie);
			// 종료 쿠키관리

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
				//			if (USE_PROXY) {
				//				HttpHost proxy = new HttpHost(PROXY_URL, PROXY_PORT, "http");
				//				response = httpclient.execute(proxy, http);
				//			}
				//			else {
				response = httpclient.execute(http);
				//			}

				Stream.of(response.getAllHeaders()).forEach(h -> {

					//					if ("Set-cookie".equals(h.getName()))
					//						cookieStore.getCookies().add(new BasicClientCookie("Set-cookie", h.getValue()));

					LOGGER.debug("[[Response Cookie]] {} : {} ", h.getName(), h.getValue());
				});

				HttpEntity entity = response.getEntity();

				rslt = res.apply(entity.getContent(), response.getStatusLine().getStatusCode());

			} finally {

				if (response != null)
					EntityUtils.consume(response.getEntity());

				if (httpclient != null) {
					httpclient.close();
				}

			}

			return rslt;

		}

		/**
		 * @return
		 * @작성자 : KYJ
		 * @작성일 : 2017. 3. 22.
		 */
		public static List<Cookie> getCookies() {
			return cookieStore.getCookies();
		}
	}

}
