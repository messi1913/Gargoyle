/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.core.commons
 *	작성일   : 2016. 3. 22.
 *	작성자   : KYJ
 * 라이센스 : BSD LICENCE.
 *            누구나 수정 및 재배포를 허용함.
 *******************************/
package com.kyj.scm.manager.core.commons;

/**
 * TODO 클래스 역할
 *
 * @author KYJ
 *
 */
public interface ICatCommand<T, R> extends SCMCommonable {

	/********************************
	 * 작성일 : 2016. 5. 4. 작성자 : KYJ
	 *
	 *
	 * @param t
	 * @return
	 ********************************/
	public R cat(T t);

}
