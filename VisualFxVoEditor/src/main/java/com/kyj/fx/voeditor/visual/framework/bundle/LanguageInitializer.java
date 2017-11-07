/********************************
 *	프로젝트 : batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.module
 *	작성일   : 2016. 8. 12.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.bundle;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * 다국어 파일을 가져오는 처리.
 *
 * 다국어 파일을 가져오는 리소스의 기준은  Local FileSystem or URL(Webservice 요청) 등이 있으므로
 *
 * 유형에 맞는 로직을 인터페이스 ( LanguageInitializer.java )를 적절히 구현하여 사용하도록 한다.
 *
 * @author KYJ
 *
 */
interface LanguageInitializer {

	/**
	 * Language를 웹서버에 요청할지 여부를 결정한다.
	 *
	 * true인경우 원격저장소에 다국어 정보를 요청.
	 * false인경우 원격저장소로 다국어 정보를 요청하지않고, 로컬저장소의 다국어 정보를 활용.
	 *
	 * 기본값은 true이다.
	 *
	 * 다국어 파일을 원격저장소에 요청하지않고
	 * 파일시스템기반으로 로드할 특별한 조건이 있는경우 아래 함수를 Override하여 구현하도록 하자.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 12.
	 * @return
	 */
	public default boolean checkWebRequest() {
		return true;
	}

	/**
	 * 다국어 파일이 존재하는 URL주소값을 리턴.
	 *
	 * URL값은 file:/// 혹은 http:/// , https:/// 등을 기준으로한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 12.
	 * @return
	 * @throws MalformedURLException
	 */
	public URL getURL() throws Exception;

	/**
	 * 다국어 파일을 로드.
	 *
	 * 실제 URL정보를 기반으로 다국어 파일을 메모리에 로드하는 로직이 구현되는 부분이다.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 12.
	 */
	public Properties load() throws Exception;

	/**
	 * 다국어 파일이 에러없이 정상적으로 로드될때 후처리할 로직이 추가적으로 필요한 경우
	 *
	 * 리스너를 set하여 후처리를 구현할 수 있도록 한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 12.
	 * @param onfinish
	 */
	public void setOnLoaded(LanguageOnLoadedListener listener);
}
