/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.suppliers
 *	작성일   : 2016. 11. 17.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.suppliers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.RealtimeSearchItemVO;
import com.kyj.fx.voeditor.visual.framework.RealtimeSearchVO;
import com.kyj.fx.voeditor.visual.util.RequestUtil;
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

	public List<RealtimeSearchVO> getMeta() {
		List<RealtimeSearchVO> rsltDVOList = Collections.emptyList();
		try {
			rsltDVOList = NaverRealtimeSearchFactory.getInstance().getRealtimeSearchMeta();
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

	private static final Logger LOGGER = LoggerFactory.getLogger(NaverRealtimeSearchFactory.class);
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

	private static String NAVER_REALTIME_URL = "https://search.naver.com/search.naver?sm=tab_hty.top&where=nexearch&ie=utf8&query=%EC%97%90%EB%84%A4%EC%8A%A4&x=0&y=0";

	private static NaverRealtimeSearchFactory nb;

	public static NaverRealtimeSearchFactory getInstance() {
		if (nb == null) {
			nb = new NaverRealtimeSearchFactory();
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

		/* 검색어 결과 List */
		List<String> rsltDVOList = null;

		String realtimeSearch = getDrityString();

		parse(realtimeSearch);

		/* 텍스트의 내용을 기존 네이버 OPEN API처럼 가공 */
		realtimeSearch = makePrettyFormat(realtimeSearch);

		/* 검색결과 임시파일 저장 */

		/* 저장에 성공한경우 realtimeSearch 이용 */
		// if (BaseUtil.isNotEmpty(createFileNm))
		// {
		rsltDVOList = parsingOnlyName(realtimeSearch);

		return rsltDVOList;

	}

	public List<RealtimeSearchVO> getRealtimeSearchMeta() throws Exception {
		String realtimeSearch = getDrityString();
		return parse(realtimeSearch);
	}

	private String getDrityString() throws Exception, MalformedURLException {
		String realtimeSearch = RequestUtil.reqeustSSL(new URL(NAVER_REALTIME_URL), (is, code) -> {
			int cnt = 0;
			do {
				if (cnt == 5) {
					break;
				}

				try {
					return ValueUtil.toString(is);
				} catch (Exception e) {
					LOGGER.error(ValueUtil.toString(e));
				}

				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}

				cnt++;
			} while ((code != 200));

			return "";
		});
		return realtimeSearch;
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

	public List<RealtimeSearchVO> parse(String htmlBody) {
		Document doc = Jsoup.parse(htmlBody);

		Elements r = doc.select("[class*='realtime_srch']");

		Elements select = r.select(".lst_realtime_srch");

		List<RealtimeSearchVO> realtimeSearchItems = select.stream().map(e -> {

			RealtimeSearchVO realtimeSearchVO = null;
			Element previousElementSibling = e.previousElementSibling();
			if (previousElementSibling != null) {
				realtimeSearchVO = new RealtimeSearchVO();
				realtimeSearchVO.setTitle(previousElementSibling.text());

				Elements liTags = e.getElementsByTag("li");
				List<RealtimeSearchItemVO> items = liTags.stream().map(li -> {
					RealtimeSearchItemVO item = new RealtimeSearchItemVO();

					Element aTag = li.getElementsByTag("a").first();
					Elements elementsByAttribute = aTag.getElementsByAttribute("href");
					String url = elementsByAttribute.attr("href");

					Element numElement = li.getElementsByClass("num").first();
					String num = numElement.text();
					Element titElement = li.getElementsByClass("tit").first();
					String title = titElement.text();

					item.setRank(Integer.parseInt(num, 10));
					item.setKeyword(title);
					item.setLink(url);
					LOGGER.debug("title [{}] num [{}]  url : [{}] , toString : {}", title, num, url, li.toString());
					return item;
				}).collect(Collectors.toList());

				realtimeSearchVO.setItems(items);
			}

			return realtimeSearchVO;
		}).filter(v -> v != null).collect(Collectors.toList());
		return realtimeSearchItems;
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