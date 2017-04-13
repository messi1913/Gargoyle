/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.core.commons
 *	작성일   : 2017. 4. 13.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.core.commons;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import org.tmatesoft.svn.core.SVNException;

/**
 * 
 * 형상서버 연결 및 API를 정의하는
 * 추상클래스
 * 
 * @author KYJ
 *
 */
public abstract class AbstractScmManager implements SCMCommonable, DimKeywords {

	/**
	 * URL정보가 존재하는지 체크함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 4.
	 * @return
	 */
	public abstract boolean isContainsURL();

	/**
	 * USER ID정보가 존재하는지 체크함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 4.
	 * @return
	 */
	public abstract boolean isContainsUserId();

	/**
	 * Connection URL
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 2.
	 * @return
	 */
	public abstract String getUrl();

	/* 
	 * 사용자 계정 정보 리턴
	 * (non-Javadoc)
	 * @see com.kyj.scm.manager.core.commons.SCMCommonable#getUserId()
	 */
	public abstract String getUserId();

	/**
	 * cat명령어
	 * path에 대한 실제 본문 내용을 읽어온후 리턴
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 23.
	 * @param param
	 * @return
	 */
	public abstract String cat(String path);

	/**
	 * svn cat명령어
	 *	path에 대한 실제 본문 내용을 읽어온후 리턴
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 23.
	 * @param path
	 * @param revision
	 * @return
	 */
	public abstract String cat(String path, String revision);

	/**
	 * path에 해당하는 하위 엔트리 조회
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 23.
	 * @param path
	 * @return
	 */
	public abstract List<String> list(String path);

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 20.
	 * @param relativePath
	 * @param handler
	 * @throws Exception
	 */

	public abstract <T> void listEntry(String relativePath, ScmDirHandler<T> handler) throws Exception;

	/**
	 * 코드 체크아웃
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 24.
	 * @param param
	 * @return
	 * @throws FileNotFoundException
	 */
	public abstract Long checkout(String param, File outDir) throws FileNotFoundException;

	/**
	 * Resource가 서버에 존재하는지 여부를 체크함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @param relativePath
	 * @return
	 * @throws SVNException
	 */

	public abstract boolean isExistsPath(String relativePath) throws Exception;

	/**
	 * 가장 최신 리비젼 번호를 구함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @return
	 * @throws Exception
	 */
	public abstract long getLatestRevision() throws Exception;

	/**
	 * 날짜에 매치되는 리비젼 번호를 구함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public abstract long getRevision(Date date) throws Exception;

	/**
	 * SVN Root Url
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 21.
	 * @return
	 */
	public abstract String getRootUrl();

	/********************************
	 * 작성일 :  2016. 7. 31. 작성자 : KYJ
	 *
	 * 서버 접속 여부를 확인
	 * @throws SVNException
	 ********************************/
	public abstract void ping() throws Exception;
	
	
	
	/**
	 * 시스템 관리 임시디렉토리
	 * @return 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 13. 
	 */
	public abstract File tmpDir();

}
