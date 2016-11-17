/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.suppliers
 *	작성일   : 2016. 11. 17.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.suppliers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.NetworkUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * 네이버 실시간 검색에 데이터를 리턴.
 * 
 * @author KYJ
 *
 */
public final class NaverRealtimeSrchSupplier implements Supplier<List<String>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(NaverRealtimeSrchSupplier.class);

	private static NaverRealtimeSrchSupplier INSTANCE;

	public static final NaverRealtimeSrchSupplier getInstance() {
		if (INSTANCE == null)
			INSTANCE = new NaverRealtimeSrchSupplier();
		return INSTANCE;
	}

	private NaverRealtimeSrchSupplier() {

	}

	/*
	 * 실시간 검색 결과를 리턴
	 * 
	 */
	public List<String> get() {
		List<String> rsltDVOList = Collections.emptyList();

		try {
			rsltDVOList = NaverRealtimeSearchFactory.getInstance().getRealtimeSearch();
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}

		return rsltDVOList;
	}

}

/**
 * @author KYJ
 *
 */
final class NaverRealtimeSearchFactory {

	/* URL에서 얻어온 String중 가져오는 실시간 검색어 시작부분 */
	/* 주석사유 : 2014.09.11 네이버 검색어 HTML 변경 */
	// private static final String STRONG_CLASS_TEXT =
	// "<strong class=\"_text\" >";
	private static final String STRONG_CLASS_TEXT = "실시간 급상승 검색어";
	/* URL에서 얻어온 String중 가져오는 실시간 검색어 끝낼부분 */
	/* 주석사유 : 2014.09.11 네이버 검색어 HTML 변경 */
	// private static final String P_CLASS_RTRANK_DATE =
	// "<p class=\"_rtrank_date\">";
	private static final String P_CLASS_RTRANK_DATE = "<div class=\"ly_realtime_srch _tab_chart\" id=\"nxfr_brlayer\">";

	/* 주석사유 : 2014.09.11 네이버 검색어 HTML 변경 */
	// public static String SEARCH_CONTANT_START = "<strong class=\"_text\" >";
	public static String SEARCH_CONTANT_START = "<span class=\"tit _text\">";

	public static int SEARCH_CONTANT_START_LENGTH = SEARCH_CONTANT_START.length();
	/* 주석사유 : 2014.09.11 네이버 검색어 HTML 변경 */
	// public static String SEARCH_CONTANT_END = "</strong>";
	public static String SEARCH_CONTANT_END = "</span>";

	public static int SEARCH_CONTANT_END_LENGTH = SEARCH_CONTANT_END.length();

	private static String NAVER_REALTIME_URL = "http://search.naver.com/search.naver?sm=tab_hty.top&where=nexearch&ie=utf8&query=%EC%97%90%EB%84%A4%EC%8A%A4&x=0&y=0";

	private static NaverRealtimeSearchFactory nb;
	private static CloseableHttpResponse response;
	private static CloseableHttpClient httpclient;
	private static HttpGet httpGet;

	public static NaverRealtimeSearchFactory getInstance() {
		if (nb == null) {
			httpGet = new HttpGet(NAVER_REALTIME_URL);
			System.out.println(NAVER_REALTIME_URL);
			httpclient = HttpClients.createDefault();
			nb = new NaverRealtimeSearchFactory();

			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

				@Override
				public void run() {

					try {
						if (httpclient != null) {
							httpclient.close();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}));
		}
		return nb;
	}

	/**
	 * 네이버 실시간 검색어 호출 </br>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 17.
	 * @return
	 * @throws Exception
	 */
	public List<String> getRealtimeSearch() throws Exception {
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();

		/* 검색어 결과 List */
		List<String> rsltDVOList = null;

		try {
			int statusCode = -1;
			int cnt = 0;

			while (statusCode != 200) {
				if (cnt == 5) {
					break;
				}

				if (NetworkUtil.isUseProxy()) {
					HttpHost proxy = NetworkUtil.getProxyHost();
					response = httpclient.execute(proxy, httpGet);
				} else {
					response = httpclient.execute(httpGet);
				}

				StatusLine statusLine = response.getStatusLine();
				statusCode = statusLine.getStatusCode();
				Thread.sleep(100);
				cnt++;
			}
			HttpEntity entity = response.getEntity();

			br = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));

			String temp = null;
			while ((temp = br.readLine()) != null) {
				sb.append(temp).append("\n");
			}

			EntityUtils.consume(response.getEntity());

			String realtimeSearch = sb.toString();

			// createFile(realtimeSearch);

			/* 텍스트의 내용을 기존 네이버 OPEN API처럼 가공 */
			realtimeSearch = makePrettyFormat(realtimeSearch);

			/* 검색결과 임시파일 저장 */

			/* 저장에 성공한경우 realtimeSearch 이용 */
			// if (BaseUtil.isNotEmpty(createFileNm))
			// {
			rsltDVOList = parsingOnlyName(realtimeSearch);

		} finally {

		}
		return rsltDVOList;
	}

	/**
	 * 2014. 6. 10. Administrator </br>
	 * 
	 * @param content
	 * @return
	 * @throws Exception
	 * @처리내용 : 검색어로 파싱된 String형의 content의 내용을 List<String> 형태로 반환
	 */
	private List<String> parsingOnlyName(String _content) throws Exception {
		String content = _content;
		List<String> rsltList = new JSONArray();
		// List<String> rsltList = new ArrayList<String>();
		while (true) {
			int startIndex = content.indexOf(SEARCH_CONTANT_START);
			int endIndex = content.indexOf(SEARCH_CONTANT_END, startIndex);

			if (startIndex == -1 || endIndex == -1) {
				break;
			}

			String rsltIem = content.substring(startIndex + SEARCH_CONTANT_START_LENGTH, endIndex);

			content = content.substring(endIndex + SEARCH_CONTANT_END_LENGTH, content.length());

			if (rsltList.contains(rsltIem)) {
				continue;
			}
			rsltList.add(rsltIem);
		}

		return rsltList;

	}

	/**
	 * URL파싱결과 필요한부분만 GET
	 *
	 * @param content
	 * @return
	 */
	public String makePrettyFormat(String content) {
		int indexOf = content.indexOf(STRONG_CLASS_TEXT); /* 검색어 시작부 */
		int lastIndexOf = content.indexOf(P_CLASS_RTRANK_DATE); /* 검색어 종료부 */
		return content.substring(indexOf, lastIndexOf);
	}

}