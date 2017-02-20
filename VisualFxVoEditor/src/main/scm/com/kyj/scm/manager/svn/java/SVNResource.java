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
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @param relativePath
	 * @return
	 * @throws SVNException
	 */
	public boolean isExists(String relativePath) throws Exception {
		//		ArrayList<Object> dirEntries = new ArrayList<>();
		//		getRepository().getDir(relativePath, -1, null, dirEntries);
		SVNNodeKind checkPath = getRepository().checkPath(relativePath, -1);
		if (checkPath == SVNNodeKind.FILE || checkPath == SVNNodeKind.DIR)
			return true;
		return false;
	}

	/**
	 * 저장소에 저장된 최신 리비젼 리턴.
	 *
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
	 * 작성일 : 2016. 7. 21. 작성자 : KYJ
	 *
	 * SVN 서버 접속 여부 확인
	 *
	 * @throws SVNException
	 ********************************/
	public void ping() throws SVNException {
		getRepository().testConnection();
	}

	/********************************
	 * 작성일 : 2016. 7. 31. 작성자 : KYJ
	 *
	 * SVN 서버 RepositoryUUID를 리턴.
	 *
	 * @return
	 * @throws SVNException
	 ********************************/
	public String getRepositoryUUID() throws SVNException {
		return getRepository().getRepositoryUUID(false);
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
	 * SVN 서버에 연결된 루트 디렉토리에 속하는 로컬 파일시스템의 파일에 해당하는 SVN서버 경로를 리턴한다.
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

	/**
	 * 프로토콜에 따라 경로를 적절한 상대경로화 처리함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 13.
	 * @param path
	 * @return
	 * @throws SVNException
	 */
	public String relativePath(SVNURL path) throws SVNException {

		if (getJavaSVNManager().isHttp() || getJavaSVNManager().isHttps()) {

			return relativePath(path.toString());
		}

		return path.getPath();
	}

	/**
	 * 프로토콜에 따라 경로를 적절한 상대경로화 처리함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 13.
	 * @param relativePath
	 * @return
	 * @throws SVNException
	 */
	private final String relativePath(String relativePath) throws SVNException {

		String repositoryPath = relativePath;

		if (getJavaSVNManager().isHttp() || getJavaSVNManager().isHttps()) {

			//http or https 프로토콜은 아래 함수 인자에 false만줘도 리턴되는 값이 있으나 svn:// 프로토콜은 true로 줘야함.
			SVNURL repoRoot = getRepository().getRepositoryRoot(false);
			if (repoRoot == null)
				repoRoot = getRepository().getRepositoryRoot(true);

			//실제 root의 경로만 리턴받음.
			String rootPath = repoRoot.toString();

			//루트로부터 상대경로만 뽑아내고 리턴.
			int indexOf = relativePath.indexOf(rootPath) + rootPath.length();
			repositoryPath = relativePath.substring(indexOf);

		}

		return repositoryPath;
	}

	/**
	 * JavaSVNManager를 이용하여 아래 값을 체크할것.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 12.
	 * @return
	 */
	boolean isHttp() {
		String protocol = getRepository().getLocation().getProtocol();
		return "http".equals(protocol);
	}

	/**
	 * JavaSVNManager를 이용하여 아래 값을 체크할것.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 13.
	 * @return
	 */
	boolean isHttps() {
		String protocol = getRepository().getLocation().getProtocol();
		return "https".equals(protocol);
	}
}
