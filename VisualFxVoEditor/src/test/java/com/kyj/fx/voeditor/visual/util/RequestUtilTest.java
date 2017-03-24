/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2017. 3. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.kyj.fx.voeditor.visual.component.grid.AbstractDVO;
import com.kyj.fx.voeditor.visual.util.SAXPasrerUtil.SAXHandler;

/**
 * @author KYJ
 *
 */
public class RequestUtilTest {

	@Test
	public void getCookie() throws Exception {
		HashMap<String, String> data = new HashMap<String, String>();

		RequestUtil.CookieBase.request("http://70.20.1.140:8501/newssologinSessionSave.do", new Header[] {}, data,
				response -> {
					System.out.println("header print");
					Stream.of(response.getAllHeaders()).peek(header -> {
						System.out.printf("%s : %s \n", header.getName(), header.getValue());
					}).collect(Collectors.toList());

					HttpEntity entity = response.getEntity();
					try {
						return ValueUtil.toString(entity.getContent());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return "";
				});

		System.out.println("cookie print");
		List<Cookie> cookies = RequestUtil.CookieBase.getCookies();
		cookies.forEach(System.out::println);
	}

	@Test
	public void getSession() throws Exception {
		HashMap<String, String> data = new HashMap<String, String>();
		String secureBox = SSOAuth.getSecureBox();
		// RequestUtil.CookieBase.request("http://70.20.1.140:8501/ssologinV2.do",
		// new Header[] {}, data, response -> {
		// HttpEntity entity = response.getEntity();
		// try {
		// return ValueUtil.toString(entity.getContent());
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// return "";
		// });

		data.put("totaldata", secureBox);
		data.put("LoginFlag", "NSOV2");
		RequestUtil.CookieBase.request("http://70.20.1.140:8501/newssologinSessionSave.do", new Header[] {}, data,
				response -> {
					HttpEntity entity = response.getEntity();
					try {
						return ValueUtil.toString(entity.getContent());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return "";
				});

		List<Cookie> cookies = RequestUtil.CookieBase.getCookies();

		System.out.println("### respoinse cookie ####");
		cookies.forEach(System.out::println);

		Optional<String> reduce = cookies.stream().filter(c -> {
			return true;
		}).map(c -> {
			return c.getName().concat("=").concat(c.getValue());

		}).reduce((str1, str2) -> {
			return str1.concat(";").concat(str2);
		});

		Header[] headers = null;
		if (reduce.isPresent()) {
			// 영준
			// 4fdcd939a24d97651e1ebbe6afe265e0e3ddf94c144bbf16e1b61b6c5cac390c6c9d5cad694b44c6dbbf90d924d449e8eb846bd33aaeb878da1a5a9b48b18678
			// 상민
			// 4fdcd939a24d97651e1ebbe6afe265e0e3ddf94c144bbf16104d76df318d8bc16c9d5cad694b44c6dbbf90d924d449e8eb846bd33aaeb878da1a5a9b48b18678

			String strCookie = reduce.get().concat(
					";id=4fdcd939a24d97651e1ebbe6afe265e0e3ddf94c144bbf16e1b61b6c5cac390c6c9d5cad694b44c6dbbf90d924d449e8eb846bd33aaeb878da1a5a9b48b18678;pagelat=7e191099de456767322e3700dc7c35c7f25666fe77429de0da1a5a9b48b18678;PortalhomelastLoginCheck=Y; QSN_SKIP=Y");
			System.out.println(strCookie);
			headers = new Header[] { new BasicHeader("Cookie", strCookie) };
		}

		// Cookie:
		// _pdss_ckinfo=2f2f946ca433cdc76f44284ba7c3439254b37cadb63d26ab69bb5aa0aa36661b0c29c995b1b4e6c3da1a5a9b48b18678;
		// cert=48c7bf3b4e63d3738d4a46eba03d20892b18f043bc9aa96f80416e62abcf0b78^+m4/AS7CcKpq0rqUMwS3Jg==;
		// lat=6322d1473700125000611643bc1cac422b18f043bc9aa96f80416e62abcf0b78;
		// JSESSIONID=j1H1YSBPQvpPC1q4qFqfHJQxzYt2SGSjggfH1y0zJnZcKdyyRRkw!-372289251;
		// id=4fdcd939a24d97651e1ebbe6afe265e0e3ddf94c144bbf16e1b61b6c5cac390c6c9d5cad694b44c6dbbf90d924d449e8eb846bd33aaeb878da1a5a9b48b18678;
		// pagelat=7e191099de456767c870f528e5807125f25666fe77429de0da1a5a9b48b18678
		data = new HashMap<String, String>();
		String gridData = RequestUtil.CookieBase.request(
				"http://70.20.1.140:8501/GTPortalFlextimeMgmtRetrieve.do?isIBSheet=Y&_isUseZip=N&pageInfo_pageid=gtx0350&pageInfo_seqno=0&pageInfo_locatecd=&pageInfo_authsik=f82b861e0520d14c894a539512ffa0a5b815eea870cfa50e118fc571c867d82a376e4c743aa44055f67fb88aaf1e44efcd94657cc59ae4e78c1cd8d971ba49d33ee85f49db6e4ce680416e62abcf0b78&pageInfo_authdeptsik=13e87f568d5bc2ad46d3f9dbd07d8c9a80416e62abcf0b78&pageInfo_tgtsik=13e87f568d5bc2ad46d3f9dbd07d8c9a80416e62abcf0b78&pageInfo_actyn=N&pageInfo_locategbncd=SA313&pageInfo_retlogyn=N&pageInfo_updlogyn=N&pageInfo_pagetypecd=1&pageInfo_datasource=&pageInfo_authemp=&pageInfo_localgbn=&empinfotype0=empinfogt8&usertypecd=1&s_locatecd=A&s_deptcddeptposyn=Y&workdtStartView=2017.03.20&workdtStart=20170320&workdtEndView=2017.03.21&workdtEnd=20170321&s_locatecd=A",
				headers, data, (is, code) -> {
					byte[] b = new byte[4096];

					StringBuffer sb = new StringBuffer();
					try {

						while (is.read(b) != -1) {
							sb.append(new String(b, 0, 4096, "UTF-8"));
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					return sb.toString();

				});
		System.err.println("#####");
		System.err.println(gridData);
	}

	@Test
	public void gridInfo() throws Exception {
		HashMap<String, String> data = new HashMap<String, String>();

		Header[] headers = {
				// new BasicHeader("Cookie",
				// "_pdss_ckinfo=2f2f946ca433cdc76f44284ba7c3439254b37cadb63d26ab69bb5aa0aa36661b0c29c995b1b4e6c3da1a5a9b48b18678;
				// Grids=mySheet#+alfrAAqqnAAEd-C3]]-b-b-bIuSbJcWC1IbjPanelHb+WC2LeVC3uVC4uUC5WC6uWC79RC8KeQC9YC10YC11YC12YC13YC14YC15YC16YC17++;
				// cert=d3f79440614e2d386617656f2ce062852b18f043bc9aa96f80416e62abcf0b78^4omfdsX4OqC5Q3e1Y0WN+g==;
				// lat=6322d14737001250528cc4791d35eeac2b18f043bc9aa96f80416e62abcf0b78;
				// id=4fdcd939a24d97651e1ebbe6afe265e0e3ddf94c144bbf16e1b61b6c5cac390c6c9d5cad694b44c6dbbf90d924d449e8eb846bd33aaeb878da1a5a9b48b18678;
				// pagelat=7e191099de456767429d131f04eed6b4f25666fe77429de0da1a5a9b48b18678;
				// PortalhomelastLoginCheck=Y; QSN_SKIP=Y"),
				new BasicHeader("Cookie",
						"_pdss_ckinfo=2f2f946ca433cdc76f44284ba7c3439254b37cadb63d26ab69bb5aa0aa36661b0c29c995b1b4e6c3da1a5a9b48b18678; "
								+ "cert=365e5ee2fde6b9ca8be8beb1d586fd6e2b18f043bc9aa96f80416e62abcf0b78^uGtB8XGYs7Qai3XkaDWexg==; "
								+ "lat=6322d147370012504b81988cd55628e32b18f043bc9aa96f80416e62abcf0b78; "
								+ "JSESSIONID=jpn6YSvXTBxnbL34x5Kgs73JNbfG2r6WMgqvQ25p2CdZjHBycnJQ!-1149058482; "

								+ "id=4fdcd939a24d97651e1ebbe6afe265e0e3ddf94c144bbf16e1b61b6c5cac390c6c9d5cad694b44c6dbbf90d924d449e8eb846bd33aaeb878da1a5a9b48b18678; "
								+ "pagelat=7e191099de456767322e3700dc7c35c7f25666fe77429de0da1a5a9b48b18678; "
								+ "PortalhomelastLoginCheck=Y; " + "QSN_SKIP=Y; ") };

		// FIXED
		// PortalhomelastLoginCheck=Y; QSN_SKIP=Y
		// pagelat=7e191099de456767322e3700dc7c35c7f25666fe77429de0da1a5a9b48b18678;
		// headers = null;
		// "usertypecd=1&s_locatecd=A&s_deptcddeptposyn=Y&workdtStartView=2017.03.20&workdtStart=20170320&workdtEndView=2017.03.21&workdtEnd=20170321&s_locatecd=A"
		/// GridCodeRetrieveAction.do
		// GTPortalFlextimeMgmtRetrieve.do
		String request = RequestUtil.CookieBase.request(
				"http://70.20.1.140:8501/GTPortalFlextimeMgmtRetrieve.do?" + "isIBSheet=Y&" + "_isUseZip=N&"
						+ "pageInfo_pageid=gtx0350&" + "pageInfo_seqno=0&" + "pageInfo_locatecd=&"
						+ "pageInfo_authsik=f82b861e0520d14c894a539512ffa0a5b815eea870cfa50e118fc571c867d82a376e4c743aa44055f67fb88aaf1e44efcd94657cc59ae4e78c1cd8d971ba49d33ee85f49db6e4ce680416e62abcf0b78&"
						+ "pageInfo_authdeptsik=13e87f568d5bc2ad46d3f9dbd07d8c9a80416e62abcf0b78&"
						+ "pageInfo_tgtsik=13e87f568d5bc2ad46d3f9dbd07d8c9a80416e62abcf0b78&" + "pageInfo_actyn=N&"
						+ "pageInfo_locategbncd=SA313&" + "pageInfo_retlogyn=N&" + "pageInfo_updlogyn=N&"
						+ "pageInfo_pagetypecd=1&" + "pageInfo_datasource=&" + "pageInfo_authemp=&"
						+ "pageInfo_localgbn=&" + "empinfotype0=empinfogt8&" + "usertypecd=1&" + "s_locatecd=A&"
						+ "s_deptcddeptposyn=Y&" + "workdtStartView=2017.03.20&" + "workdtStart=20170320&"
						+ "workdtEndView=2017.03.21&" + "workdtEnd=20170321&" + "s_locatecd=A",
				headers, data, (is, code) -> {
					byte[] b = new byte[4096];

					StringBuffer sb = new StringBuffer();
					try {

						while (is.read(b) != -1) {
							sb.append(new String(b, 0, 4096, "UTF-8"));
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					return sb.toString();
				});

		System.out.println(request);
	}

	@Test
	public void gridInfo2() throws Exception {
		// HashMap<String, String> data = new HashMap<String, String>();
		//
		// Header[] headers = {
		// // new BasicHeader("Cookie",
		// "_pdss_ckinfo=2f2f946ca433cdc76f44284ba7c3439254b37cadb63d26ab69bb5aa0aa36661b0c29c995b1b4e6c3da1a5a9b48b18678;
		// Grids=mySheet#+alfrAAqqnAAEd-C3]]-b-b-bIuSbJcWC1IbjPanelHb+WC2LeVC3uVC4uUC5WC6uWC79RC8KeQC9YC10YC11YC12YC13YC14YC15YC16YC17++;
		// cert=d3f79440614e2d386617656f2ce062852b18f043bc9aa96f80416e62abcf0b78^4omfdsX4OqC5Q3e1Y0WN+g==;
		// lat=6322d14737001250528cc4791d35eeac2b18f043bc9aa96f80416e62abcf0b78;
		// id=4fdcd939a24d97651e1ebbe6afe265e0e3ddf94c144bbf16e1b61b6c5cac390c6c9d5cad694b44c6dbbf90d924d449e8eb846bd33aaeb878da1a5a9b48b18678;
		// pagelat=7e191099de456767429d131f04eed6b4f25666fe77429de0da1a5a9b48b18678;
		// PortalhomelastLoginCheck=Y; QSN_SKIP=Y"),
		// new BasicHeader("Cookie",
		// "_pdss_ckinfo=2f2f946ca433cdc76f44284ba7c3439254b37cadb63d26ab69bb5aa0aa36661b0c29c995b1b4e6c3da1a5a9b48b18678;
		// "
		// +
		// "cert=365e5ee2fde6b9ca8be8beb1d586fd6e2b18f043bc9aa96f80416e62abcf0b78^uGtB8XGYs7Qai3XkaDWexg==;
		// "
		// +
		// "lat=6322d147370012504b81988cd55628e32b18f043bc9aa96f80416e62abcf0b78;
		// "
		// +
		// "JSESSIONID=jpn6YSvXTBxnbL34x5Kgs73JNbfG2r6WMgqvQ25p2CdZjHBycnJQ!-1149058482;
		// "
		//
		// +
		// "id=4fdcd939a24d97651e1ebbe6afe265e0e3ddf94c144bbf16e1b61b6c5cac390c6c9d5cad694b44c6dbbf90d924d449e8eb846bd33aaeb878da1a5a9b48b18678;
		// "
		// +
		// "pagelat=7e191099de456767322e3700dc7c35c7f25666fe77429de0da1a5a9b48b18678;
		// "
		// + "PortalhomelastLoginCheck=Y; "
		// + "QSN_SKIP=Y; "
		// )
		// };
		//
		// String request = RequestUtil.CookieBase.request(
		// "http://70.20.1.140:8501/GTPortalFlextime2MgmtRetrieve.do?" +
		// "isIBSheet=Y&_isUseZip=N&pageInfo_pageid=gtx0550&pageInfo_seqno=0&pageInfo_locatecd=&pageInfo_authsik=f82b861e0520d14c894a539512ffa0a5b815eea870cfa50e118fc571c867d82a376e4c743aa44055f67fb88aaf1e44efcd94657cc59ae4e78c1cd8d971ba49d33ee85f49db6e4ce680416e62abcf0b78&pageInfo_authdeptsik=13e87f568d5bc2ad46d3f9dbd07d8c9a80416e62abcf0b78&pageInfo_tgtsik=13e87f568d5bc2ad46d3f9dbd07d8c9a80416e62abcf0b78&pageInfo_actyn=N&pageInfo_locategbncd=SA313&pageInfo_retlogyn=N&pageInfo_updlogyn=N&pageInfo_pagetypecd=1&pageInfo_datasource=&pageInfo_authemp=&pageInfo_localgbn=&empinfotype0=empinfogt8&usertypecd=1&s_locatecd=A&s_weekseqstartdt=20170320&s_weekseqenddt=20170326&s_deptcddeptposyn=Y&s_wkymView=2017.03&s_wkym=201703&s_weekseqView=3%EC%9B%94+3%EC%A3%BC%EC%B0%A8+%282017.03.20+%7E+2017.03.26%29&s_weekseq=3&s_weekseqdetailcd=&s_weekseqnm=&s_weekseqengnm=&s_weekseqincd=&s_weekseqfreecol1=&s_weekseqfreecol2=&s_weekseqfreecol3=&s_weekseqfreecol4=&s_weekseqfreecol5=&s_locatecd=A"
		// ,headers, data, (is, code) -> {
		// byte[] b = new byte[4096];
		//
		// StringBuffer sb = new StringBuffer();
		// try {
		//
		// while (is.read(b) != -1) {
		// sb.append(new String(b, 0, 4096, "UTF-8"));
		// }
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// return sb.toString();
		// });

		String request = FileUtil.readToString(RequestUtilTest.class.getResourceAsStream("dumny.html"));

		String xmlData = request.substring(request.indexOf("<SHEET>", 0), request.length());
		System.out.println(xmlData);

		// SAXPasrerUtil.loadXml(file, requireType)

		// Sheet sheet = SAXPasrerUtil.loadXml(new
		// ByteArrayInputStream(xmlData.getBytes()), Sheet.class);
		// System.out.println(sheet);
		// Document parse = Jsoup.parse(request);
		// Elements select = parse.select("DATA");
		// Element element = select.get(0);
		// element.childNodes().forEach(n -> {
		//
		// System.out.println(n);
		// });

		ServiceHandler defaultHandler = new ServiceHandler();
		SAXPasrerUtil.getAllQNames(new ByteArrayInputStream(xmlData.getBytes()), defaultHandler);
		List<Td> list = defaultHandler.getTds();
		list.forEach(System.out::println);

	}

	class ServiceHandler extends SAXHandler {

		private List<Td> qNameList = new ArrayList<>();
		int seq = -1;
		int idx = -1;
		String qName;

		public List<Td> getTds() {
			return this.qNameList;
		}

		public synchronized void characters(char[] cbuf, int start, int len) {
			// System.out.print("Characters:");
			// System.out.println();
			if(cbuf.length != 0)
			{
				String trim = new String(cbuf, start, len).trim();
				if ("TD".equals(qName)) {
					
					qNameList.add(new Td(idx, seq, trim));
				}
				System.out.println(qName + " " + trim);
			}
			
		}

		@Override
		public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
			// TODO Auto-generated method stub
			super.ignorableWhitespace(ch, start, length);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.kyj.fx.voeditor.visual.util.SAXPasrerUtil.SAXHandler#
		 * startElement(java.lang.String, java.lang.String, java.lang.String,
		 * org.xml.sax.Attributes)
		 */
		@Override
		public synchronized void startElement(String url, String arg1, String qName, Attributes arg3)
				throws SAXException {
			this.qName = qName;
//			super.startElement(url, arg1, qName, arg3);
			if ("TR".equals(qName))
				idx++;

			if ("TD".equals(qName))
				seq++;
			
			
			

		}

		@Override
		public synchronized void endElement(String uri, String localName, String qName) throws SAXException {
			super.endElement(uri, localName, qName);
			if (this.qName.equals(qName)) {
				this.qName = "";
			}
			
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.xml.sax.helpers.DefaultHandler#fatalError(org.xml.sax.
		 * SAXParseException)
		 */
		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			// super.fatalError(e);

			// ignore
		}

	}

	public static class Td {
		private int idx;
		private int seq;
		private String value;

		public Td(int idx, int seq, String value) {
			super();
			this.idx = idx;
			this.seq = seq;
			this.value = value;
		}

		public int getIdx() {
			return idx;
		}

		public void setIdx(int idx) {
			this.idx = idx;
		}

		public int getSeq() {
			return seq;
		}

		public void setSeq(int seq) {
			this.seq = seq;
		}

		/**
		 * @return the value
		 */
		public final String getValue() {
			return value;
		}

		/**
		 * @param value
		 *            the value to set
		 */
		public final void setValue(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return "Td [idx=" + idx + ", seq=" + seq + ", value=" + value + "]";
		}

	}

	/**
	 * Test method for
	 * {@link com.kyj.fx.voeditor.visual.util.RequestUtil#request(java.net.URL, java.util.function.BiFunction)}
	 * .
	 *
	 * @throws Exception
	 * @throws MalformedURLException
	 */
	@Test
	public final void request() throws MalformedURLException, Exception {

		String secureBox = SSOAuth.getSecureBox();
		System.out.println(secureBox);

		HashMap<String, String> data = new HashMap<String, String>();
		data.put("SSO_TRAY_DATA", secureBox);
		data.put("initSession", "true");
		data.put("fromRefer", "Single");
		data.put("temp_url", "/htmlNinsa/flextime/flextime_input.jsp");

		String request = RequestUtil.CookieBase.request("http://70.20.1.240:9073/htmlNinsa/flextime/flextime_input.jsp",
				data, (is, code) -> {
					byte[] b = new byte[4096];

					StringBuffer sb = new StringBuffer();
					try {

						while (is.read(b) != -1) {
							sb.append(new String(b, 0, 4096, "EUC-KR"));
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					return sb.toString();
				});

		Document parse = Jsoup.parse(request);
		Elements select = parse.select(".tbl2 > tbody> tr ");
		Element trIn = select.get(0);
		Element child1 = trIn.child(1);
		Element child2 = trIn.child(3);

		System.out.println(child1.text());
		System.out.println(child2.text());

	}

	private static final Logger logger = LoggerFactory.getLogger("test");

	/**
	 * Test method for
	 * {@link com.kyj.fx.voeditor.visual.util.RequestUtil#request200(java.net.URL, java.util.function.BiFunction, boolean)}
	 * .
	 *
	 * @throws Exception
	 * @throws MalformedURLException
	 */
	@Test
	public final void testRequest200() throws MalformedURLException, Exception {

		FileMoveSVO svo = new FileMoveSVO();
		FileMoveDVO fileMoveDVO = new FileMoveDVO();
		fileMoveDVO.setSrcFile(new File("C:\\Users\\KYJ\\내자료\\src").getAbsolutePath());
		fileMoveDVO.setDstFile(new File("C:\\Users\\KYJ\\내자료\\dst").getAbsolutePath());
		svo.setFileMoveDVO(fileMoveDVO);

		JSONObject jsonObject = new JSONObject(svo);
		System.out.println(jsonObject.toString());

		CloseableHttpClient build = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost("http://localhost:8100/filemove");
		httpPost.addHeader("content-type", "application/json");

		// StringEntity stringEntity = new StringEntity();

		StringEntity requestEntity = new StringEntity(jsonObject.toString(), "application/json", "UTF-8");

		// stringEntity.setContentType("application/json; charset=UTF-8");
		// stringEntity.setContentEncoding("UTF-8");
		httpPost.setEntity(requestEntity);

		String execute = build.execute(httpPost, new ResponseHandler<String>() {

			@Override
			public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {

				int statusCode = response.getStatusLine().getStatusCode();
				if (200 == statusCode) {
					return ValueUtil.toString(response.getEntity().getContent());
				}
				System.out.println("res code invalide " + statusCode);
				return "";
			}
		});

		System.out.println(execute);

	}

	public class FileMoveDVO extends AbstractDVO {

		private String srcFile;

		private String dstFile;

		/**
		 * @return the srcFile
		 */
		public String getSrcFile() {
			return srcFile;
		}

		/**
		 * @param srcFile
		 *            the srcFile to set
		 */
		public void setSrcFile(String srcFile) {
			this.srcFile = srcFile;
		}

		/**
		 * @return the dstFile
		 */
		public String getDstFile() {
			return dstFile;
		}

		/**
		 * @param dstFile
		 *            the dstFile to set
		 */
		public void setDstFile(String dstFile) {
			this.dstFile = dstFile;
		}

	}

	public class FileMoveSVO {

		private FileMoveDVO fileMoveDVO;

		/**
		 * @return the fileMoveDVO
		 */
		public FileMoveDVO getFileMoveDVO() {
			return fileMoveDVO;
		}

		/**
		 * @param fileMoveDVO
		 *            the fileMoveDVO to set
		 */
		public void setFileMoveDVO(FileMoveDVO fileMoveDVO) {
			this.fileMoveDVO = fileMoveDVO;
		}

	}

	/**
	 * Test method for
	 * {@link com.kyj.fx.voeditor.visual.util.RequestUtil#request(java.net.URL, java.util.function.BiFunction, boolean)}
	 * .
	 */
	@Test
	public final void testRequestURLBiFunctionOfInputStreamIntegerTBoolean() {
		fail("Not yet implemented"); // TODO
	}

}
