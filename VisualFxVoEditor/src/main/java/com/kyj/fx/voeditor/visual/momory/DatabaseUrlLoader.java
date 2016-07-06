/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2015. 10. 15.
 *	프로젝트 : VisualFxVoEditor
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.momory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

/**
 * 사용자 정의 속성들 Writable속성
 * 
 * @author KYJ
 *
 */
@Deprecated
public class DatabaseUrlLoader {

	private static final String FILE_NAME = "META-INF/database.url.magt.properties";
	

	private static Properties properties;
	private static DatabaseUrlLoader loader;

	public static DatabaseUrlLoader getInstance() {
		if (loader == null) {
			loader = new DatabaseUrlLoader();
			loader.initialize();
		}
		return loader;
	}

	private DatabaseUrlLoader() {
		initialize();
	}

	private void initialize() {
		properties = new Properties();
		FileInputStream inStream = null;
		try {
			File file = new File(FILE_NAME);
			if (!file.exists()) {
				file.createNewFile();
			}
			inStream = new FileInputStream(FILE_NAME);
			properties.load(inStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inStream != null)
					inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Object get(String key) {
		return properties.get(key);
	}

	public void putAll(Map<String, Object> bufMap) {
		properties.putAll(bufMap);
		store();
	}

	public void put(String key, String value) {
		properties.put(key, value);
		store();
	}

	/**
	 * 파일에 적용처리
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 11. 4.
	 */
	private void store() {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(FILE_NAME);
			properties.store(out, "User Conf...");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Enumeration<Object> keySet() {
		return properties.keys();
	}

}
