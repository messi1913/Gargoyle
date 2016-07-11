/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.svn.java
 *	작성일   : 2016. 5. 5.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn.java;

import java.util.Properties;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.kyj.scm.manager.core.commons.IDiffCommand;
import com.kyj.scm.manager.stream.StringOutputStream;

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
	public String diff(String path1, String path2) throws SVNException {
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
	 ********************************/
	public String diff(String path1, SVNRevision rivision1, String path2, SVNRevision rivision2) throws SVNException {
		SVNDiffClient diffClient = getSvnManager().getDiffClient();
		StringOutputStream result = new StringOutputStream();

		SVNURL svnUrl1 = SVNURL.parseURIEncoded(getUrl() + "/" + path1);
		SVNURL svnUrl2 = SVNURL.parseURIEncoded(getUrl() + "/" + path2);

		diffClient.doDiff(svnUrl1, rivision1, svnUrl2, rivision2, SVNDepth.UNKNOWN, true, result);
		return result.getString();
	}

}
