/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.svn.java
 *	작성일   : 2016. 7. 14.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn.java;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 *
 * SVN Resource정보 리턴.
 *
 * @author KYJ
 *
 */
class SVNResource extends AbstractSVN {

	/**
	 * @param javaSVNManager
	 * @param properties
	 */
	public SVNResource(JavaSVNManager javaSVNManager, Properties properties) {
		super(javaSVNManager, properties);
	}

	/**
	 * 존재여부 확인
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @param relativePath
	 * @return
	 * @throws SVNException
	 */
	public SVNNodeKind isExists(String relativePath) throws Exception {
		//		ArrayList<Object> dirEntries = new ArrayList<>();
		//		getRepository().getDir(relativePath, -1, null, dirEntries);
		return getRepository().checkPath(relativePath, -1);
	}

	/**
	 * 저장소에 저장된 최신 리비젼 리턴.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @return
	 * @throws SVNException
	 */
	public long getLatestRevision() throws SVNException {
		return getRepository().getLatestRevision();
	}

	/**
	 * 날짜에 매치되는 리비젼을 리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @param date
	 * @return
	 * @throws SVNException
	 */
	public long getRevision(Date date) throws SVNException {
		return getRepository().getDatedRevision(date);
	}

	/********************************
	 * 작성일 :  2016. 7. 21. 작성자 : KYJ
	 *
	 *
	 * @throws SVNException
	 ********************************/
	public void ping() throws SVNException {
		getRepository().testConnection();
	}

	/**
	 * SVN Root Url
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 21.
	 * @return
	 */
	public String getRootUrl() {
		SVNURL location = getRepository().getLocation();
		String decodedString = location.toDecodedString();
		String uriEncodedPath = location.getURIEncodedPath();
		String rootUrl = decodedString.replaceAll(uriEncodedPath, "");
		return rootUrl;
	}

	/**
	 * SVN 서버에 연결된 루트 디렉토리에 속하는
	 * 로컬 파일시스템의 파일에 해당하는 SVN서버 경로를
	 * 리턴한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 1.
	 * @return
	 * @throws Exception
	 */
	public SVNURL getSvnUrlByFileSystem(File file, SVNRevision revision) throws Exception {
		SVNClientManager svnManager2 = getSvnManager();
		SVNInfo doInfo = svnManager2.getWCClient().doInfo(file, revision);
		return doInfo.getURL();
	}

}
