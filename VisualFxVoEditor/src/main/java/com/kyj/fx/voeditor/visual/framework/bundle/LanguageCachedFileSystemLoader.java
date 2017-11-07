/********************************
 *	프로젝트 : batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.core.init
 *	작성일   : 2016. 8. 12.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.bundle;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

/**
 * @author KYJ
 *
 */
class LanguageCachedFileSystemLoader implements LanguageLoadable {

	/* (non-Javadoc)
	 * @see com.samsung.sds.sos.schedule.core.init.LanguageLoadable#load()
	 */
	@Override
	public Properties load(URL url) throws Exception {

		Properties properties = new Properties();

		//URL값이 NULL인경우 빈값 리턴.
		if (url == null)
			return properties;

		//파일이 존재하지않는경우 빈 값 리턴.
		File file = new File(url.getFile());
		if (!file.exists())
			return properties;

		//
		try (InputStream openStream = url.openStream()) {
			try (InputStreamReader reader = new InputStreamReader(openStream, "UTF-8")) {
				properties.load(reader);
			}
		}
		return properties;
	}

}
