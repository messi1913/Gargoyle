
/********************************
 *	프로젝트 : batch-module
 *	패키지   : com.samsung.sds.sos.server.core.svn.commons
 *	작성일   : 2017. 1. 18.
 *	프로젝트 : OPERA
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.core.commons;

/**
 *  SCM Directory Handler
 *  
 *  형상관리 서버 아이템 탐색 처리시 사용
 *  
 * @author KYJ
 */
public interface ScmDirHandler<T> {

	/**
	 * 실제 처리 내용 구현.
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 18.
	 * @param entry
	 */
	public void accept(T entry);

	/**
	 * 탐색하지않을 디렉토리를 정의.
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 18.
	 * @param entry
	 * @return
	 */
	public boolean test(T entry);

	/**
	 * 필요사항에 따라 
	 * 어떤 아이템에 대한 결과를 리턴할때 구현
	 * (사용자 정의함수)
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 27. 
	 * @return
	 */
	public default Object get() {
		return null;
	}

}
