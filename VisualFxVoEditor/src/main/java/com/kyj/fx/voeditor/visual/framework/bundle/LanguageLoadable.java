/********************************
 *	프로젝트 : batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.core.init
 *	작성일   : 2016. 8. 12.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.bundle;

import java.net.URL;
import java.util.Properties;

/**
 * @author KYJ
 *
 */
interface LanguageLoadable {

	/**
	 * 다국어 파일을 로드.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 12.
	 */
	public Properties load(URL url) throws Exception;

}
