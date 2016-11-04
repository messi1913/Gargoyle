/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : external
 *	작성일   : 2016. 11. 1.
 *	작성자   : KYJ
 *******************************/
package external;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.Before;
import org.junit.Test;

import com.kyj.fx.voeditor.visual.main.initalize.ProxyInitializable;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * @author KYJ
 *
 */
public class GoogleTrend {

	@Before
	public void proxySetting() throws Exception {

		//프록시 관련 설정을  세팅함.
		new ProxyInitializable().initialize();

	}

	@Test
	public void urlEncode() throws Exception {
		System.out.println(URLEncoder.encode(":", "UTF-8"));
	}
	@Test
	public void urlDecode() throws Exception {



		System.out.println(URLDecoder.decode("2016.+10.+31.%2Ccd_max%3A2016.+11.+2.&tbm=", "UTF-8"));

		System.out.println(URLEncoder.encode("2016.+10.+31.%2Ccd_max%3A2016.+11.+2.&tbm=", "UTF-8"));

		System.out.println(URLDecoder.decode(
				"https://www.google.co.kr/search?q=%EB%B0%95%EA%B7%BC%ED%98%9C&biw=1920&bih=990&tbas=0&source=lnt&tbs=cdr%3A1%2Ccd_min%3A2016.+10.+31.%2Ccd_max%3A2016.+11.+2.&tbm=",
				"UTF8"));
	}

	@Test
	public void simpleSearch() throws Exception {

		String keyWord = "%EB%B0%95%EA%B7%BC%ED%98%9C";
		String startDate = "2016.10.30.";
		String endDate = "2016.11.02.";

		keyWord = URLEncoder.encode(keyWord, "UTF-8");
		startDate = URLEncoder.encode(startDate, "UTF-8");
		endDate = URLEncoder.encode(endDate, "UTF-8");

		String URL_FORMAT = "https://www.google.co.kr/search?q=:keyWord&biw=1920&bih=990&source=lnt&tbs=cdr%3A1%2Ccd_min%3A:startDate.%2Ccd_max%3A:endDate&tbm=";

		Map<String, Object> hashMap = new HashMap<>();
		hashMap.put("keyWord", keyWord);
		hashMap.put("startDate", startDate);
		hashMap.put("endDate", endDate);

		String formattedURL = ValueUtil.getVelocityToText(URL_FORMAT, hashMap, true, null, str -> str);
		System.out.println(formattedURL);

		SSLContext ctx = SSLContext.getInstance("TLS");

		ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
		SSLContext.setDefault(ctx);

		URL url = new URL(formattedURL);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

		try {
			//			conn.setDoInput(true);
			//			conn.setDoOutput(true);
			conn.setDefaultUseCaches(false);
			conn.setUseCaches(false);

			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:12.0) Gecko/20100101 Firefox/12.0");
			//			conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			//			conn.setRequestProperty("Accept-Language", "ko-KR,kr;q=0.5");
			conn.setRequestProperty("Accept-Encoding", "UTF-8");
			conn.setRequestProperty("Connection", "keep-alive");

			//		conn.setIfModifiedSince(100L);

			conn.setRequestProperty("Accept", "text/html");
			//			conn.setRequestProperty("Accept-Charset", "UTF-8");
			//					conn.setRequestProperty("Accept-Encoding", "UTF-8");
			//					conn.setRequestProperty("Accept-Language", "KR");
			//		conn.setRequestProperty("Cache-Control", "no-store");
			//					conn.setRequestProperty("Pragma", "no-cache");

			conn.setHostnameVerifier(hostnameVerifier);

			//		System.out.println(conn.getContentEncoding());

			conn.getHeaderFields().forEach((str, li) -> {
				System.out.printf("%s : %s \n", str, li);
			});

			conn.setConnectTimeout(6000);

			conn.connect();

			//Charset
			//
			//Description
			//
			//US-ASCII Seven-bit ASCII, a.k.a. ISO646-US, a.k.a. the Basic Latin block of the Unicode character set
			//ISO-8859-1   ISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1
			//UTF-8 Eight-bit UCS Transformation Format
			//UTF-16BE Sixteen-bit UCS Transformation Format, big-endian byte order
			//UTF-16LE Sixteen-bit UCS Transformation Format, little-endian byte order
			//UTF-16 Sixteen-bit UCS Transformation Format, byte order identified by an optional byte-order mark

			//필터링해야할 텍스트가 모두 포함된 원본 데이터
			String dirtyConent = null;

			//버퍼로 그냥 읽어봐도 되지만 인코딩 변환을 추후 쉽게 처리하기 위해 ByteArrayOutputStream을 사용
			try (InputStream is = conn.getInputStream()) {
				try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
					int read = -1;
					while ((read = is.read()) != -1) {
						out.write(read);
					}
					out.flush();
					dirtyConent = out.toString();
				}
			}

			if (200 == conn.getResponseCode()) {
				System.out.printf("dirty content %s \n", dirtyConent);
			}

		} finally {
			conn.disconnect();
		}

	}

	private HostnameVerifier hostnameVerifier = (arg0, arg1) -> {
		System.out.println(arg0);
		System.out.println(arg1);
		return true;
	};

	/**
	 * 1. SSL 통신으로 데이터를 주고 받아야함.
	 * 		1- a Proxy 설정이 추가적으로 필요한경우 설정
	 *
	 * 2. URL Encoding으로 한글 데이터를 바꿔야함. << 여기서 많이 고생했다. 특정글자 부분에 한글이 깨져서
	 *
	 * 3. conn.connect(); 먼저 호출하고 inputStream객체를 리턴받아 핸들링하자
	 *
	 * 4. response code가 203이 리턴되는 경우가 있다.
	 * 		주로 프록시 서버관계인경우 발생되는것으로 보임 . 통신은 정상적으로 이루어 졌으나
	 * 		프록시 서버가 데이터를 리턴하는경우 발생하는것같다.
	 *
	 *5. 데이터중 일부가 불량임. 데이터 변환 기능을 추가함.
	 *		ex) "v" :new Date(2011,1,1) 형태로 되어있어 json 포멧을 만드는데 어려움
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 1.
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {

		String geo = "KR";
		String date = "all";
		//		String keywords = "%EB%B0%95%EA%B7%BC%ED%98%9C,%EC%B5%9C%EC%88%9C%EC%8B%A4";
		String origin_keywords = "박근혜,최순실";
		String converted_keywords = URLEncoder.encode(origin_keywords, "UTF-8");

		//핫 트랜드 https://www.google.com/trends/hottrends/atom
		String URL_FORMAT = "https://www.google.com/trends/fetchComponent?cid=TIMESERIES_GRAPH_0&export=3&q=:keywords&geo=:geo";

		Map<String, Object> hashMap = new HashMap<>();
		hashMap.put("geo", geo);
		hashMap.put("date", date);
		hashMap.put("keywords", converted_keywords);

		String formattedURL = ValueUtil.getVelocityToText(URL_FORMAT, hashMap, true, null, str -> str);
		System.out.println(formattedURL);

		SSLContext ctx = SSLContext.getInstance("TLS");

		ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
		SSLContext.setDefault(ctx);

		URL url = new URL(formattedURL);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

		try {
			//			conn.setDoInput(true);
			//			conn.setDoOutput(true);
			conn.setDefaultUseCaches(false);
			conn.setUseCaches(false);

			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:12.0) Gecko/20100101 Firefox/12.0");
			//			conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			//			conn.setRequestProperty("Accept-Language", "ko-KR,kr;q=0.5");
			conn.setRequestProperty("Accept-Encoding", "UTF-8");
			conn.setRequestProperty("Connection", "keep-alive");

			//		conn.setIfModifiedSince(100L);

			conn.setRequestProperty("Accept", "text/html");
			//			conn.setRequestProperty("Accept-Charset", "UTF-8");
			//					conn.setRequestProperty("Accept-Encoding", "UTF-8");
			//					conn.setRequestProperty("Accept-Language", "KR");
			//		conn.setRequestProperty("Cache-Control", "no-store");
			//					conn.setRequestProperty("Pragma", "no-cache");

			conn.setHostnameVerifier(hostnameVerifier);

			//		System.out.println(conn.getContentEncoding());

			conn.getHeaderFields().forEach((str, li) -> {
				System.out.printf("%s : %s \n", str, li);
			});

			conn.setConnectTimeout(6000);

			conn.connect();

			//Charset
			//
			//Description
			//
			//US-ASCII Seven-bit ASCII, a.k.a. ISO646-US, a.k.a. the Basic Latin block of the Unicode character set
			//ISO-8859-1   ISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1
			//UTF-8 Eight-bit UCS Transformation Format
			//UTF-16BE Sixteen-bit UCS Transformation Format, big-endian byte order
			//UTF-16LE Sixteen-bit UCS Transformation Format, little-endian byte order
			//UTF-16 Sixteen-bit UCS Transformation Format, byte order identified by an optional byte-order mark

			//필터링해야할 텍스트가 모두 포함된 원본 데이터
			String dirtyConent = null;

			//버퍼로 그냥 읽어봐도 되지만 인코딩 변환을 추후 쉽게 처리하기 위해 ByteArrayOutputStream을 사용
			try (InputStream is = conn.getInputStream()) {
				try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
					int read = -1;
					while ((read = is.read()) != -1) {
						out.write(read);
					}
					out.flush();
					dirtyConent = out.toString();
				}
			}

			//접속불량.
			//		print_https_cert(conn);
			if (203 == conn.getResponseCode()) {
				//
				System.out.println(conn.getResponseMessage());
				//			File file = new File("sample.html");
				//			FileUtil.writeFile(file, dirtyConent, Charset.forName("UTF-8"));
				//			FileUtil.openFile(file);

			} else if (200 == conn.getResponseCode()) {
				System.out.printf("dirty content %s \n", dirtyConent);

				//원본 데이터에서 불필요한 부분 삭제
				String regexMatch = ValueUtil.regexMatch("\\(.*\\)", dirtyConent, str -> {
					return str.substring(1, str.length() - 1);
				});

				//데이터중 new Date(xxx,x,x) 라고 JSON이 인식못하는 텍스트를 제거함.
				regexMatch = ValueUtil.regexReplaceMatchs("new Date\\([0-9,]+\\)", regexMatch, str -> {
					int startIdx = str.indexOf('(');
					int endIdx = str.indexOf(')', startIdx);

					if (startIdx == -1 || endIdx == -1)
						return str;

					startIdx++;
					return String.format("\"%s\"", str.substring(startIdx, endIdx).replaceAll(",", "-"));
				});

				//결과출력
				System.out.printf("result string : %s \n", regexMatch);
			}

		} finally {
			conn.disconnect();
		}

	}

	/**
	 *
	 *  SSL 통신 인증
	 *
	 * @author KYJ
	 *
	 */
	private static class DefaultTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			System.out.println("######################");
			System.out.println("checkClientTrusted");
			System.out.println(arg1);
			System.out.println("######################");
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			System.out.println("########################################################################################");
			System.out.println("checkServerTrusted");
			System.out.println(arg1);

			Stream.of(arg0).forEach(System.out::println);

			System.out.println("########################################################################################");
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	private void print_https_cert(HttpsURLConnection con) {

		if (con != null) {

			try {

				System.out.println("Response Code : " + con.getResponseCode());
				System.out.println("Cipher Suite : " + con.getCipherSuite());
				System.out.println("\n");

				Certificate[] certs = con.getServerCertificates();
				for (Certificate cert : certs) {
					System.out.println("Cert Type : " + cert.getType());
					System.out.println("Cert Hash Code : " + cert.hashCode());
					System.out.println("Cert Public Key Algorithm : " + cert.getPublicKey().getAlgorithm());
					System.out.println("Cert Public Key Format : " + cert.getPublicKey().getFormat());
					System.out.println("\n");
				}

			} catch (SSLPeerUnverifiedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}
