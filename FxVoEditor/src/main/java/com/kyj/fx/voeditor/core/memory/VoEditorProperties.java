/**
 * package : com.kyj.fx.voeditor.core.memory
 *	fileName : ConfigProperties.java
 *	date      : 2015. 12. 8.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.core.memory;

import java.io.IOException;
import java.util.Properties;

/**
 * @author KYJ
 *
 */
public class VoEditorProperties {

	public static final String VOEDITOR_ANNOTATION_COLUMN_CLASS = "voeditor.annotation.column.class";
	public static final String VOEDITOR_ANNOTATION_NONEEDITABLE_CLASS = "voeditor.annotation.noneditable.class";

	private static Properties properties;
	private static VoEditorProperties loader;
	private static final String FILE_NAME = "META-INF/voeditor.annotation.config.properties";

	public static VoEditorProperties getInstance() {
		if (loader == null) {
			loader = new VoEditorProperties();
			loader.initialize();
		}
		return loader;
	}

	private VoEditorProperties() {
		initialize();
	}

	private void initialize() {
		properties = new Properties();
		try {
			ClassLoader classLoader = VoEditorProperties.class.getClassLoader();
			properties.load(classLoader.getResourceAsStream(FILE_NAME));

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String get(String key, String defaultValue) {

		String property = properties.getProperty(key);

		if (property == null)
			return defaultValue;
		return property;
	}

}
