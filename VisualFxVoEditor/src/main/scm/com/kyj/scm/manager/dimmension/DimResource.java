/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.svn.java
 *	작성일   : 2016. 7. 14.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.dimmension;

import java.util.Date;
import java.util.Properties;

import org.tmatesoft.svn.core.SVNException;

import com.serena.dmclient.api.ItemRevision;
import com.serena.dmclient.api.Project;
import com.serena.dmclient.api.SystemAttributes;

/**
 *
 * SVN Resource정보 리턴.
 *
 * @author KYJ
 *
 */
class DimResource extends AbstractDimmension {

	/**
	 * @param javaSVNManager
	 * @param properties
	 */
	public DimResource(DimmensionManager javaSVNManager, Properties properties) {
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
	public boolean isExists(String projSpec, String relativePath) throws Exception {
		throw new RuntimeException("Not yet support");
	}

	/**
	 * 저장소에 저장된 최신 리비젼 리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @return
	 * @throws SVNException
	 */
	public long getLatestRevision(String projSpec) throws SVNException {

		Project project = getProject(projSpec);
		ItemRevision findItemRevisionByPath = project.findItemRevisionByPath("");
		ItemRevision latestRevision = findItemRevisionByPath.getLatestRevision();

		return (long) latestRevision.getAttribute(SystemAttributes.REVISION);

		//		return getRepository().getLatestRevision();
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
		throw new RuntimeException("Not yet support");
	}

	/********************************
	 * 작성일 : 2016. 7. 21. 작성자 : KYJ
	 *
	 * SVN 서버 접속 여부 확인
	 *
	 * @throws SVNException
	 ********************************/
	public void ping() throws SVNException {
		//		getConnection().getConnectionState(false);
	}

	/********************************
	 * 작성일 : 2016. 7. 31. 작성자 : KYJ
	 *
	 * SVN 서버 RepositoryUUID를 리턴.
	 *
	 * @return
	 * @throws SVNException
	 ********************************/
	public String getRepositoryUUID() {
		return getConnection().getConnectionDetails().getServer();
	}

	/**
	 * SVN Root Url
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 21.
	 * @return
	 */
	public String getRootUrl() {

		//		DimensionsObjectFactory dimmensionObjectFactory = getDimmensionObjectFactory();
		//		dimmensionObjectFactory.getProject(arg0)
		return getConnection().getConnectionDetails().getServer();
		//		SVNURL location = getRepository().getLocation();
		//		String decodedString = location.toDecodedString();
		//		String uriEncodedPath = location.getURIEncodedPath();
		//		String rootUrl = decodedString.replaceAll(uriEncodedPath, "");
		//		return rootUrl;
	}

}
