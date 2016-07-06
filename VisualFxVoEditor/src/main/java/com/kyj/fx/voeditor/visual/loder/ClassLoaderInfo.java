/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.loder
 *	작성일   : 2015. 10. 23.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.loder;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author KYJ
 *
 */
public class ClassLoaderInfo {

	private ClassLoaderInfo() {
	}
	private static ClassLoaderInfo info;
	public static ClassLoaderInfo getInstance() {
		if (info == null)
			info = new ClassLoaderInfo();
		return info;
	}

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

	public Map getLoadingClassInfo(String className) {
		Map result = new HashMap();
		List cl = null;
		try {
			cl = getClassLoadersOfClass(className);
		} catch (Throwable e) {
			result.put("X_ERROR", e);
			return result;
		}
		if (cl != null)
			result.put("X_CLASSLOADERS", cl);
		String resourceName = classNameToResourceName(className);
		URL url = getClass().getResource(resourceName);
		try {
			result.put("X_FILE_NAME", url.toURI().toString());
			File file = getFileFromURI(url.toURI());
			if (file.exists()) {
				result.put("X_FILE_DATE", sdf.format(new Date(file.lastModified())));
			}
		} catch (URISyntaxException e) {
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

	public List getClassLoaderInfos(String className) {
		List result = new ArrayList();
		for (ClassLoader cl = getClass().getClassLoader(); cl != null; cl = cl.getParent())
			result.add(cl.getClass().getName());

		result.add("Bootstrap classloader");
		return result;
	}

	public List getBootClassInfos() {
		return separatePath(System.getProperty("sun.boot.class.path"));
	}

	public List getExtClassInfos() {
		return getJars(System.getProperty("java.ext.dirs"));
	}

	public List getAppClassInfos() {
		return separatePath(System.getProperty("java.class.path"));
	}

	private List separatePath(String classpath) {
		StringTokenizer st = new StringTokenizer(classpath, File.pathSeparator);
		List result = new ArrayList();
		for (; st.hasMoreTokens(); result.add(st.nextToken()));
		return result;
	}

	private List getClassLoadersOfClass(String className) throws Throwable {
		Class c = Class.forName(className.trim());
		List result = new ArrayList();
		for (ClassLoader cl = c.getClassLoader(); cl != null; cl = cl.getParent()) {
			result.add(cl.getClass().getName());
		}

		result.add("Bootstrap classloader");
		return result;
	}

	private String classNameToResourceName(String className) {
		String resourceName = className;
		if (!resourceName.startsWith("/"))
			resourceName = (new StringBuilder()).append("/").append(resourceName).toString();
		resourceName = resourceName.replace('.', '/');
		resourceName = (new StringBuilder()).append(resourceName).append(".class").toString();
		return resourceName;
	}

	private List getJars(String directoryName) {
		File directory = new File(directoryName);
		if (!directory.exists())
			return new ArrayList();
		List result = new ArrayList();
		String allFiles[] = directory.list();
		if (allFiles != null) {
			for (int i = 0; i < allFiles.length; i++)
				if (allFiles[i].endsWith(".jar")) {
					File f = new File(directory, allFiles[i]);
					result.add(f.getPath());
				}

		}
		return result;
	}

}