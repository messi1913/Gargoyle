/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2016. 4. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.loder;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.scm.SVNItem;
import com.kyj.fx.voeditor.visual.component.scm.SVNRepository;
import com.kyj.fx.voeditor.visual.component.scm.SVNTreeView;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.EncrypUtil;
import com.kyj.scm.manager.svn.java.JavaSVNManager;

import kyj.Fx.dao.wizard.core.util.ValueUtil;

/**
 *
 *
 * @author KYJ
 *
 */
public class SVNInitLoader implements SCMInitLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(SVNInitLoader.class);

	@Override
	public <T extends SVNItem> List<T> load() {

		String jsonArray = ResourceLoader.getInstance().get(ResourceLoader.SVN_REPOSITORIES);
		if (jsonArray == null || jsonArray.length() ==0)
			return null;

		JSONArray parse = null;
		try {
			parse = (JSONArray) new JSONParser().parse(jsonArray);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		List<SVNRepository> repositorys = new ArrayList<>();
		int size = parse.size();
		for (int i = 0; i < size; i++) {
			Object obj = parse.get(i);
			JSONObject jsonObject = (JSONObject) obj;
			Object url = jsonObject.get(SVNTreeView.SVN_URL);
			Object id = jsonObject.get(SVNTreeView.SVN_USER_ID);
			Object pass = jsonObject.get(SVNTreeView.SVN_USER_PASS);

			if (url == null)
				continue;

			Properties properties = new Properties();
			properties.put(SVNTreeView.SVN_URL, url.toString());
			if (id != null && !id.toString().isEmpty())
				properties.put(SVNTreeView.SVN_USER_ID, id.toString());

			if (pass != null && !pass.toString().isEmpty()) {
				try {
					properties.put(SVNTreeView.SVN_USER_PASS, EncrypUtil.decryp(pass.toString()));
				} catch (Exception e) {
					properties.put(SVNTreeView.SVN_USER_PASS, pass.toString());
					ValueUtil.toString(e);
				}
			}

			JavaSVNManager manager = new JavaSVNManager(properties);

			SVNRepository svnRepository = new SVNRepository("", url.toString(), manager);

			repositorys.add(svnRepository);
		}

		return (List<T>) repositorys;
	}

}
