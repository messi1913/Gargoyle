/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : kyj.Fx.scm.manager.core.common
 *	작성일   : 2016. 3. 24.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.core.commons;

/**
 * SVN 리비젼 정보 조회
 *
 * @author KYJ
 *
 */
public interface ILogCommand<T, R> extends SCMCommonable {

	/********************************
	 * 작성일 : 2016. 5. 4. 작성자 : KYJ
	 *
	 *
	 * @param t
	 * @return
	 ********************************/
	R log(T t);
}
