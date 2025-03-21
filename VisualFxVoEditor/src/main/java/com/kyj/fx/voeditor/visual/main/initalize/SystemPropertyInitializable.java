/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.initalize
 *	작성일   : 2016. 2. 5.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main.initalize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;

/**
 * @author KYJ
 *
 */
@GagoyleInitializable
public class SystemPropertyInitializable implements Initializable {

	private static Logger LOGGER = LoggerFactory.getLogger(SystemPropertyInitializable.class);

	@Override
	public void initialize() throws Exception {
		
		LOGGER.debug("Start System Property Initialize!");
		registAppName();
		LOGGER.debug("Show System Properties");
		setFileEncoingInit();

//		ValueUtil.toString( System.getProperties());
//		Properties properties = System.getProperties();
//		Enumeration<Object> keys = properties.keys();
//		LOGGER.debug("Default Charset : {} ", Charset.defaultCharset().displayName());
//		while (keys.hasMoreElements()) {
//			Object key = keys.nextElement();
//			Object value = properties.get(key);
//			LOGGER.debug(String.format("Key : %s Value : %s", key.toString(), value.toString()));
//		}

		networkSettings();

	}

	/**
	 * 어플리케이션 이름 등록
	 * @작성자 : KYJ
	 * @작성일 : 2017. 7. 21. 
	 */
	private void registAppName() {
		System.setProperty(ConfigResourceLoader.APP_NAME, ConfigResourceLoader.GARGOYLE_APP);
	}

	private void networkSettings() {
		//1.7 부터는 SNI support의 부분이 기본적으로 enable 되어 있다
		System.setProperty("jsse.enableSNIExtension", "false");

	}

	void setFileEncoingInit() {

		String file_encoding = ConfigResourceLoader.getInstance().getOrDefault(ConfigResourceLoader.FILE_ENCODING, "UTF-8");
		String jnu_encoding = ConfigResourceLoader.getInstance().getOrDefault(ConfigResourceLoader.SUN_JNU_ENCODING, "UTF-8");
		System.setProperty(ConfigResourceLoader.FILE_ENCODING, file_encoding);
		System.setProperty(ConfigResourceLoader.SUN_JNU_ENCODING, jnu_encoding);

		// System.setProperty("client.encoding.override", "UTF-8");

	}

}
