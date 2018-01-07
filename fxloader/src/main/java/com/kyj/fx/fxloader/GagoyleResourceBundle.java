/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 6. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.fxloader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/***************************
 *
 * 키에 해당하는 값이 없는경우 key를 리턴함.
 *
 * @author KYJ
 *
 ***************************/
public class GagoyleResourceBundle extends PropertyResourceBundle {

	public static String BUNDLE_NAME = "bundles.GargoyleBundle";

	/**
	 * @param stream
	 * @throws IOException
	 */
	public GagoyleResourceBundle(InputStream stream) throws IOException {
		super(stream);
	}

	/**
	 * @param reader
	 * @throws IOException
	 */
	public GagoyleResourceBundle(Reader reader) throws IOException {
		super(reader);
	}

	/***********************************************************************************/
	/* 이벤트 구현 */

	/***********************************************************************************/

	/***********************************************************************************/
	/* 일반API 구현 */

	@Override
	public Object handleGetObject(String key) {

		// 값이 없는경우 키를 리턴.
		if (!super.keySet().contains(key))
			return key;

		return super.handleGetObject(key);
	}

	@Override
	public boolean containsKey(String key) {
		// 값은 무조건 true로 지정.
		return true;
	}

	private static ResourceBundle newBundle;

	public static ResourceBundle getDefaultBundle() throws IllegalAccessException, InstantiationException, IOException {
		if (newBundle == null)
			newBundle = getDefaultBundle(Locale.getDefault());
		return newBundle;
	}

	public static ResourceBundle getDefaultBundle(Locale locale) throws IllegalAccessException, InstantiationException, IOException {
		if (newBundle == null) {
			newBundle = newBundle(BUNDLE_NAME, locale, ClassLoader.getSystemClassLoader(), false);
			if (newBundle.getLocale() != locale) {
				return newBundle(BUNDLE_NAME, locale, ClassLoader.getSystemClassLoader(), false);
			}
		}
		return newBundle;
	}

	public static ResourceBundle newBundle() throws IllegalAccessException, InstantiationException, IOException {
		return newBundle(BUNDLE_NAME, Locale.getDefault(), ClassLoader.getSystemClassLoader(), false);
	}

	public static ResourceBundle newBundle(String baseName) throws IllegalAccessException, InstantiationException, IOException {
		return newBundle(baseName, Locale.getDefault(), ClassLoader.getSystemClassLoader(), false);
	}

	public static ResourceBundle newBundle(String baseName, Locale locale, ClassLoader loader, boolean reload)
			throws IllegalAccessException, InstantiationException, IOException {
		return new UTF8Control().newBundle(baseName, locale, loader, reload);
	}

	static class UTF8Control extends Control {
		public ResourceBundle newBundle(String baseName, Locale locale, ClassLoader loader, boolean reload)
				throws IllegalAccessException, InstantiationException, IOException {
			// The below is a copy of the default implementation.
			String bundleName = toBundleName(baseName, locale);
			String resourceName = toResourceName(bundleName, "properties");
			ResourceBundle bundle = null;
			InputStream stream = null;
			if (reload) {
				URL url = loader.getResource(resourceName);
				if (url != null) {
					URLConnection connection = url.openConnection();
					if (connection != null) {
						connection.setUseCaches(false);
						stream = connection.getInputStream();
					}
				}
			} else {
				stream = loader.getResourceAsStream(resourceName);
			}
			if (stream != null) {
				try {
					// Only this line is changed to make it to read properties
					// files as UTF-8.
					bundle = new GagoyleResourceBundle(new InputStreamReader(stream, "UTF-8"));
				} finally {
					stream.close();
				}
			}
			return bundle;
		}
	}

	/***********************************************************************************/
}
