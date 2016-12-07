/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.nrch.realtime
 *	작성일   : 2016. 12. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.nrch.realtime;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author KYJ
 *
 */
public class OtherNews {

	/*
	 * query : 검색어 start=2
	 */
	static final String NAVAER_NEW_PREFFIX = "https://search.naver.com/search.naver?where=news&se=0&query=";
	static final String NAVAER_NEW_SUFFIX = "&ie=utf8&sm=tab_opt&sort=1&photo=0&field=0&reporter_article=&pd=1&ds=&de=&docid=&nso=so%3Add%2Cp%3A1w%2Ca%3Aall&mynews=0&mson=0&refresh_start=0&related=0";

	public static List<String> getOtherNewsPages(String keyword) {
		try {
			String encode = URLEncoder.encode(keyword, "UTF-8");
			return Arrays.asList(NAVAER_NEW_PREFFIX + encode + NAVAER_NEW_SUFFIX);
		} catch (UnsupportedEncodingException e) {
		}
		return Collections.emptyList();
	}

}
