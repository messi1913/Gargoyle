/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.nrch.realtime
 *	작성일   : 2016. 12. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.nrch.realtime;

import java.util.function.Predicate;

/**
 * @author KYJ
 *
 */
public class URLFilterRepository {

	public Predicate<String> getFilter() {
		return v -> {

			if ("https://submit.naver.com/".equals(v))
				return false;

			else if ("http://www.naver.com".equals(v))
				return false;

			else if (v.startsWith("https://nid.naver.com"))
				return false;

			else if (v.startsWith("http://searchad.naver.com"))
				return false;

			else if (v.contains("namu.wiki"))
				return false;

			else if (v.contains("wikipedia.org"))
				return false;

			else if (v.startsWith("http://music.naver.com"))
				return false;

			else if (v.startsWith("http://m.post.naver.com"))
				return false;

			else if (v.startsWith("http://tvcast.naver.com"))
				return false;

			else if (v.startsWith("http://shopping.naver.com"))
				return false;

			else if (v.startsWith("https://help.naver"))
				return false;

			else if (v.startsWith("http://www.navercorp.com"))
				return false;

			else if (v.startsWith("http://book.naver.com"))
				return false;

			else if (v.startsWith("http://www.cwpyo.com"))
				return false;

			else if (v.startsWith("http://navercast.naver.com"))
				return false;

			else if (v.startsWith("http://localad.naver.com"))
				return false;

			else if (v.startsWith("http://map.naver.com/"))
				return false;

			else if (v.startsWith("http://pay.naver.com"))
				return false;

			return v.contains("news");
		};
	}
}
