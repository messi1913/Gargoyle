/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.svn.java
 *	작성일   : 2016. 5. 5.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn.java;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.kyj.scm.manager.core.commons.IDiffCommand;

/***************************
 *
 * Diff 구현
 *
 * @author KYJ
 *
 ***************************/
public class SVNDiff extends AbstractSVN implements IDiffCommand<String, String, String> {

	/**
	 * @param javaSVNManager
	 * @param properties
	 */
	public SVNDiff(JavaSVNManager javaSVNManager, Properties properties) {
		super(javaSVNManager, properties);
	}

	/*
	 * 가장 최신의 리비젼 비교
	 *
	 * @inheritDoc
	 */
	@Override
	public String diff(String path1, String path2) throws Exception {
		return diff(path1, SVNRevision.HEAD, path2, SVNRevision.HEAD);
	}

	/********************************
	 * 작성일 : 2016. 5. 5. 작성자 : KYJ
	 *
	 *
	 * @param path1
	 * @param rivision1
	 * @param path2
	 * @param rivision2
	 * @return
	 * @throws SVNException
	 * @throws UnsupportedEncodingException
	 ********************************/
	public String diff(String path1, SVNRevision rivision1, String path2, SVNRevision rivision2) throws SVNException, Exception {
		SVNDiffClient diffClient = getSvnManager().getDiffClient();
		//		StringOutputStream result = new StringOutputStream();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		//		SVNURL svnURL = getSvnURL();
		//		String repositoryPath = getRepository().getRepositoryPath(path1);
		SVNURL location = getRepository().getLocation();
		String decodedString = location.toDecodedString();

		String uriEncodedPath = location.getURIEncodedPath();

		String rootUrl = decodedString.replaceAll(uriEncodedPath, "");

		SVNURL svnUrl1 = SVNURL.parseURIEncoded(rootUrl + "/" + path1/*getRepository().getRepositoryPath(path1)*/);
		SVNURL svnUrl2 = SVNURL.parseURIEncoded(rootUrl + "/" + path2/*getRepository().getRepositoryPath(path2)*/);

		diffClient.doDiff(svnUrl1, rivision1, svnUrl2, rivision2, SVNDepth.UNKNOWN, true, out);
		return out.toString("UTF-8");
	}

}
