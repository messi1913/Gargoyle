/********************************
 *	프로젝트 : batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.core.init
 *	작성일   : 2016. 8. 12.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.bundle;

import java.io.File;
import java.io.FileOutputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author KYJ
 *
 */
public class DefaultLanguageInitializer implements LanguageInitializer {

	private static Logger LOGGER = LoggerFactory.getLogger(DefaultLanguageInitializer.class);

	private Properties properties;

	/**
	 * 다국어 properties의 base 파일 포멧.
	 *
	 * @최초생성일 2016. 8. 12.
	 */
	public static final String FILE_FOR_MAT = "%s/lang_%s.properties";

	public DefaultLanguageInitializer() {

	}

	/* (non-Javadoc)
	 * @see com.samsung.sds.sos.schedule.core.init.LanguageInitializer#checkRequest()
	 */
	@Override
	public boolean checkWebRequest() {
		return "Y".equals(ResourceLoader.getInstance().get(ResourceLoader.LANGUAGE_RELOAD_ON_STARTUP_YN, "N"));
	}

	public File checkAndCreateDir(String location) {

		File resourceDir = new File(location);
		if (!resourceDir.exists()) {
			resourceDir.mkdirs();
		}
		return resourceDir;
	}

	/* (non-Javadoc)
	 * @see com.samsung.sds.sos.schedule.core.init.LanguageInitializer#getURL()
	 */
	@Override
	public URL getURL() throws Exception {

		String http = ResourceLoader.getInstance().get(ResourceLoader.LANGUAGE_REQUEST_URL, "");

		boolean isLoadFileSystem = false;

		//원격저장소에 다국어 설정정보를 로드할지 유무를 확인.
		if (checkWebRequest()) {
			if (http == null || http.isEmpty()) {
				isLoadFileSystem = true;
			} else {
				if (http.startsWith("http") || http.startsWith("https")) {
					isLoadFileSystem = false;
				} else {
					isLoadFileSystem = true;
				}
			}
		} else {
			isLoadFileSystem = true;
		}

		if (isLoadFileSystem) {
			//파일시스템에서 다국어 정보를 로드하기 위한 URL 주소를 리턴.
			return getFileSystemURL();
		} else {
			//원격저장소에서 다국어 정보를 로드하기 위한 URL 주소를 리턴.
			return new URL(http);
		}
	}

	/**
	 * cache된 파일 시스템의  다국어 파일 위치를  로드한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 12.
	 * @return
	 * @throws MalformedURLException
	 */
	private URL getFileSystemURL() throws MalformedURLException {
		String urlString = getPropertyStringPath();
		return new URL("file:///" + urlString);
	}

	/**
	 * 다국어 파일이 존재하는 위치정보를 문자열로 리턴.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 12.
	 * @return
	 */
	private String getPropertyStringPath() {
		
		File location = checkAndCreateDir(ResourceLoader.getInstance().get(ResourceLoader.LANGUAGE_STORED_PROPERTIES_LOCATION));
		String langCode = getLangCode();
		String urlString = String.format(FILE_FOR_MAT, location.getAbsolutePath(), langCode);
		return urlString;
	}

	/**
	 * 환경설정에 정의된 langCode를 리턴.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 12.
	 * @return
	 */
	private String getLangCode() {
		return ValueUtil.decode(ResourceLoader.getInstance().get(ResourceLoader.LANGUAGE_CODE), "ko").toString();
	}

	/* (non-Javadoc)
	 * @see com.samsung.sds.sos.schedule.core.init.LanguageInitializer#load()
	 */
	@Override
	public Properties load() throws Exception {
		URL url = getURL();
		LanguageLoadable loader = null;

		try {
			switch (url.getProtocol()) {
			case "http":
				loader = new LanguageWebServiceURLLoader(getLangCode());
				properties = loader.load(url);
				writeFileSystem(properties);
				break;
			case "https":
				loader = new LanguageWebServiceURLLoader(getLangCode());
				properties = loader.load(url);
				writeFileSystem(properties);
				break;
			case "file":
				loader = new LanguageCachedFileSystemLoader();
				properties = loader.load(url);
				break;
			default:
				throw new RuntimeException("Not Yet Supported protocol :  " + url.getProtocol());
			}
		}
		catch (ConnectException e) {
			/*원격저장소에 접속이 불가능한 상황이라면 캐시된 파일시스템 기반 로딩처리.*/
			loader = new LanguageCachedFileSystemLoader();
			properties = loader.load(getFileSystemURL());
		}

		if (languageOnLoadedListener.get() != null) {
			languageOnLoadedListener.get().onLoaded(() -> properties);
		}

		//		OutputStreamWriter ow = new OutputStreamWriter(http.getOutputStream(), "UTF8");
		//		ow.

		return properties;

	}

	/**
	 * 다국어 Property를 로컬 파일시스템에 write.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 12.
	 * @param proerties
	 * @throws Exception
	 */
	private void writeFileSystem(Properties proerties) throws Exception {
		if (proerties != null) {
			proerties.store(new FileOutputStream(getPropertyStringPath()), String.format("Cached Date : %s", new Date().toString()));
		}
	}

	private ObjectProperty<LanguageOnLoadedListener> languageOnLoadedListener = new SimpleObjectProperty<>();

	/* (non-Javadoc)
	 * @see com.samsung.sds.sos.schedule.core.init.LanguageInitializer#setOnLoaded(com.samsung.sds.sos.schedule.core.listener.LanguageOnLoadedListener)
	 */
	@Override
	public void setOnLoaded(LanguageOnLoadedListener listener) {
		languageOnLoadedListener.set(listener);
	}

	/**
	 * 다국어 파일리턴.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 12.
	 * @return
	 */
	public Properties getProperties() {
		return this.properties;
	}

}
