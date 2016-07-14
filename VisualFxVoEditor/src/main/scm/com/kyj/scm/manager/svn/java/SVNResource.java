/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.svn.java
 *	작성일   : 2016. 7. 14.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn.java;

import java.util.Date;
import java.util.Properties;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.ISVNReplayHandler;

/**
 *
 * SVN Resource정보 리턴.
 *
 * @author KYJ
 *
 */
public class SVNResource extends AbstractSVN {

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
	public SVNNodeKind isExists(String relativePath) throws SVNException {
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

}
