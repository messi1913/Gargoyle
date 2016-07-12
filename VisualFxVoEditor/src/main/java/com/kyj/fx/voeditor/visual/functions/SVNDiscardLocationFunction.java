/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.functions
 *	작성일   : 2016. 4. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.functions;

import java.util.Iterator;
import java.util.function.Function;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.scm.SVNRepository;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.scm.manager.core.commons.SVNKeywords;

/**
 * SVN 저장소 정보를 삭제하는 역할을 함.
 *
 * @author KYJ
 *
 */
public class SVNDiscardLocationFunction implements Function<SVNRepository, Boolean>, SVNKeywords {

	private static Logger LOGGER = LoggerFactory.getLogger(SVNDiscardLocationFunction.class);

	/**
	 * @inheritDoc
	 */
	@Override
	public Boolean apply(SVNRepository repo) {
		boolean resultFlag = false;
		if (repo == null)
			return resultFlag;

		JSONArray parse = null;
		try {
			String string = ResourceLoader.getInstance().get(SVN_REPOSITORIES);
			parse = (JSONArray) new JSONParser().parse(string);
		} catch (ParseException e) {
			LOGGER.error(ValueUtil.toString(e));
			return resultFlag;
		}

//		String simpleName = repo.getSimpleName();
		/* URL정보와 USERID가 정보가 합쳐져 키값으로 구성됨. */
		String savedUrl = repo.getURL();
//		Object savedUserId = repo.getUserId();

		boolean isFound = false;
		Iterator<Object> it = parse.iterator();
		int idx = -1;
		while (it.hasNext()) {
			JSONObject e = (JSONObject) it.next();
			Object objURL = e.get(SVN_URL);
			Object objUserId = e.get(SVN_USER_ID);
			idx++;
			if (objURL == null || objUserId == null)
				continue;

			String url = objURL.toString();
			

			if (savedUrl.equals(url)) {
				isFound = true;
				break;
			}

		}

		if (isFound) {
			parse.remove(idx);
			resultFlag = true;
		} else {
			return resultFlag;
		}

		ResourceLoader.getInstance().put(SVN_REPOSITORIES, parse.toJSONString());
		return resultFlag;
	}

}
