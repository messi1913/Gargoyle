/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : kyj.Fx.scm.manager.core.svn
 *	작성일   : 2016. 3. 22.
 *	작성자   : KYJ
 * 라이센스 : BSD 라이센스
 *******************************/
package com.kyj.scm.manager.core.commons;

import java.io.File;
import java.io.FileNotFoundException;

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
	public R checkout(T param) throws FileNotFoundException;

	public R checkout(T param, File outDir) throws FileNotFoundException;

}
