/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.loder
 *	작성일   : 2015. 10. 23.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.loder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class ResourceInfo {
	private ResourceInfo() {
	}

	private static ResourceInfo info;

	public static ResourceInfo getInstance() {
		if (info == null)
			info = new ResourceInfo();
		return info;
	}

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

	public Map getLoadingResourceInfo(String resourceName) {
		Map result = new HashMap();

		ClassLoader classLoader = getClass().getClassLoader();
		URL url = classLoader.getResource(resourceName);
		Enumeration urls = null;
		try {
			urls = getClass().getClassLoader().getResources(resourceName);
		} catch (IOException e) {
		}

		if (url != null) {
			try {
				result.put("X_LOADING_RESOURCE_FILE", url.toURI().toString());
				File file = getFileFromURI(url.toURI());
				if (file.exists()) {
					result.put("X_LOADING_RESOURCE_DATE", sdf.format(new Date(file.lastModified())));
				}
			} catch (URISyntaxException e) {
			}
		}

		if (urls != null) {
			ArrayList resources = new ArrayList();
			while (urls.hasMoreElements()) {
				URL u = (URL) urls.nextElement();
				try {
					resources.add(u.toURI().toString());
				} catch (URISyntaxException e) {
					resources.add(e.getMessage());
				}
			}
			result.put("X_RESOURCES", resources);
		} else {
			result.put("X_RESOURCES", new ArrayList());
		}

		return result;
	}

	public File getFileFromURI(URI uri) {
		File result = null;
		String schema = uri.getScheme();
		if ("file".equals(schema))
			result = new File(uri);
		else if ("zip".equals(schema)) {
			String uriStr = uri.getSchemeSpecificPart();
			String file = uriStr.substring(0, uriStr.indexOf('!'));
			result = new File(file);
		} else if ("jar".equals(schema)) {
			String uriStr = uri.getSchemeSpecificPart();
			String file = uriStr.substring(6, uriStr.indexOf('!'));
			result = new File(file);
		}
		return result;
	}

}