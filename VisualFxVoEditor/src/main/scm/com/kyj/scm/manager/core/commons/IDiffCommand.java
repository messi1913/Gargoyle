/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.core.commons
 *	작성일   : 2016. 5. 5.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.core.commons;

import org.tmatesoft.svn.core.SVNException;

/***************************
 * 차이점 분석
 *
 * @author KYJ
 *
 ***************************/
public interface IDiffCommand<T, F, R> {

	/********************************
	 * 작성일 : 2016. 5. 5. 작성자 : KYJ
	 *
	 * 차이점 분석
	 *
	 * @param t
	 * @param f
	 * @return
	 * @throws SVNException
	 ********************************/
	public R diff(T t, F f) throws Exception;

}
