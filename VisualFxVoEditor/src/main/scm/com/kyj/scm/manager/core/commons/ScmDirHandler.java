
/********************************
 *	프로젝트 : batch-module
 *	패키지   : com.samsung.sds.sos.server.core.svn.commons
 *	작성일   : 2017. 1. 18.
 *	프로젝트 : OPERA
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.core.commons;

/**
 *  SVNDirHandler
 * @author KYJ
 *
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

}
