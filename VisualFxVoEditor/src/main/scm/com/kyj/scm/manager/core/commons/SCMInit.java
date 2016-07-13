/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.core.commons
 *	작성일   : 2016. 3. 22.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.core.commons;

import java.util.Properties;

/**
 * SCM 공통 속성
 *
 * @author KYJ
 *
 */
public interface SCMInit extends SVNKeywords {

	/**
	 * 접속
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 23.
	 * @param properties
	 * @return
	 */
	public void init(Properties properties);

}
