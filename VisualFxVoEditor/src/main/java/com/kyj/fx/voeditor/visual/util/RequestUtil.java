/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 11. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KYJ
 *
 */
public class RequestUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestUtil.class);
	private static HostnameVerifier hostnameVerifier = (arg0, arg1) -> {
		LOGGER.debug(arg0);
		return true;
	};

	static SSLContext ctx;

	static {
		try {
			ctx = SSLContext.getInstance("TLS");
			ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
			SSLContext.setDefault(ctx);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 *
	 * SSL 통신 인증
	 *
	 * @author KYJ
	 *
	 */
	private static class DefaultTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			// LOGGER.debug("######################");
			// LOGGER.debug("checkClientTrusted");
			LOGGER.debug(arg1);
			// LOGGER.debug("######################");
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			// LOGGER.debug("########################################################################################");
			// LOGGER.debug("checkServerTrusted");
			// LOGGER.debug(arg1);

			boolean present = Stream.of(arg0).filter(v -> {

				switch (v.getSigAlgName()) {
				case "SHA256withRSA":
					return true;
				case "SHA384withECDSA":
					return true;
				case "SHA384withRSA":
					return true;
				}

				return false;
			}).findFirst().isPresent();

			if (!present) {
				LOGGER.debug("Can't not found Truested Algorisms ");
				Stream.of(arg0).forEach(v -> LOGGER.warn(v.getSigAlgName()));
				throw new CertificateException();
			}

			// LOGGER.debug("########################################################################################");
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	public static String reqeustSSL_JSONString(URL url, BiFunction<InputStream, Integer, String> response) throws Exception {
		return reqeustSSL(url, (is, code) -> {
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

	public static <T> T reqeustSSL(URL url, BiFunction<InputStream, Integer, T> response) throws Exception {
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

//			conn.getHeaderFields().forEach((str, li) -> {
//				LOGGER.debug("{} : {} ", str, li);
//			});

			conn.setConnectTimeout(6000);

			conn.connect();
			// LOGGER.debug("{}", conn.getCipherSuite());
			// Charset
			//
			// Description
			//
			// US-ASCII Seven-bit ASCII, a.k.a. ISO646-US, a.k.a. the Basic
			// Latin block of the Unicode character set
			// ISO-8859-1 ISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1
			// UTF-8 Eight-bit UCS Transformation Format
			// UTF-16BE Sixteen-bit UCS Transformation Format, big-endian byte
			// order
			// UTF-16LE Sixteen-bit UCS Transformation Format, little-endian
			// byte order
			// UTF-16 Sixteen-bit UCS Transformation Format, byte order
			// identified by an optional byte-order mark

			is = conn.getInputStream();
			
			LOGGER.debug("code : [{}] URL : {} ,  ", conn.getResponseCode(),url.toString() );

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

	public static <T> T request(URL url, BiFunction<InputStream, Integer, T> response) throws Exception {
		return request(url, response, true);
	}

	public static <T> T request(URL url, BiFunction<InputStream, Integer, T> response, boolean autoClose) throws Exception {

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
			conn.setReadTimeout(6000);

			conn.connect();

			is = conn.getInputStream();

			LOGGER.debug("code : [{}] URL : {} ,  ", conn.getResponseCode(),url.toString() );

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

}
