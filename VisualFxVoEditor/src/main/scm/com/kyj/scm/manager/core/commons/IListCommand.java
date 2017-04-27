/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.core.commons
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
	 * T에 대한 노드 정보를 통해 
	 * 자식 노드에 대한 정보(R) 를 리턴함  
	 * 
	 * @param t
	 * @return
	 ********************************/
	public R list(T t);

}
