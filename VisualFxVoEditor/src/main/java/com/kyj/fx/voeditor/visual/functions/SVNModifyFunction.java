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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.EncrypUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * @author KYJ
 *
 */
public class SVNModifyFunction extends SVNCreateFunction {

	private static Logger LOGGER = LoggerFactory.getLogger(SVNCreateFunction.class);

	public boolean isValide(Properties t) {
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

		int size = parse.size();

		for (int i = 0; i < size; i++) {
			JSONObject jsonObject = (JSONObject) parse.get(i);
			if (jsonObject != null) {

				String url = ValueUtil.decode(jsonObject.get(SVN_URL), "").toString();

				if (jsonObject.get(SVN_URL).equals(url)) {
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
					break;
				}
			}
		}

		ResourceLoader.getInstance().put(SVN_REPOSITORIES, parse.toJSONString());

		return true;
	}

}
