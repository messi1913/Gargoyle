/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : kyj.Fx.scm.manager.core.svn
 *	작성일   : 2016. 3. 22.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.core.commons;

/**
 * TODO 클래스 역할
 *
 * @author KYJ
 *
 */
public interface IListCommand<T, R> extends SCMCommonable {

	/********************************
	 * 작성일 : 2016. 5. 4. 작성자 : KYJ
	 *
	 *
	 * @param t
	 * @return
	 ********************************/
	public R list(T t);

}
