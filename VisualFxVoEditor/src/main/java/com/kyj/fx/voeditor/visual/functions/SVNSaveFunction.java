/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.functions
 *	작성일   : 2016. 4. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.functions;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.function.Function;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.EncrypUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.scm.manager.core.commons.SVNKeywords;

/**
 * @author KYJ
 *
 */
public class SVNSaveFunction implements Function<Properties, Boolean>, SVNKeywords {

	private static Logger LOGGER = LoggerFactory.getLogger(SVNSaveFunction.class);

	public boolean isValide(Properties t) {

		String url = t.getProperty(SVN_URL);
		JSONArray parse = null;
		try {
			String string = ResourceLoader.getInstance().get(SVN_REPOSITORIES);
			parse = (JSONArray) new JSONParser().parse(string);
		} catch (ParseException e) {
			LOGGER.error(ValueUtil.toString(e));
			return false;
		}

		if (parse != null) {
			Iterator<JSONObject> iterator = parse.iterator();
			while (iterator.hasNext()) {
				if (iterator.next().containsValue(url))
					return false;
			}
		}

		return true;

	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Boolean apply(Properties t) {
		JSONArray parse = null;
		try {
			String string = ResourceLoader.getInstance().get(SVN_REPOSITORIES);
			parse = (JSONArray) new JSONParser().parse(string);
		} catch (ParseException e) {
			LOGGER.error(ValueUtil.toString(e));
			return false;
		}

		JSONObject jsonObject = new JSONObject();
		Iterator<Entry<Object, Object>> it = t.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Object, Object> e = it.next();
			Object key = e.getKey();
			Object value = e.getValue();

			if (SVN_USER_PASS.equals(key)) {
				try {
					value = EncrypUtil.encryp(value.toString());
				} catch (Exception e1) {
					LOGGER.error(ValueUtil.toString(e1));
					return false;
				}
			}
			jsonObject.put(key, value);
		}

		parse.add(jsonObject);
		ResourceLoader.getInstance().put(SVN_REPOSITORIES, parse.toJSONString());

		return true;
	}

}
