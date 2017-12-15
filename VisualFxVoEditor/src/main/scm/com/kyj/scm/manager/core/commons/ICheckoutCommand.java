/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.core.commons
 *	작성일   : 2016. 3. 22.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.core.commons;

import java.io.File;

/**
 * TODO 클래스 역할
 *
 * @author KYJ
 *
 */
public interface ICheckoutCommand<T, R> extends SCMCommonable {

	/********************************
	 * 작성일 : 2016. 5. 4. 작성자 : KYJ
	 *
	 *
	 * @param param
	 * @return
	 ********************************/
	public R checkout(T param) throws Exception;

	public R checkout(T param, File outDir) throws Exception;

}
