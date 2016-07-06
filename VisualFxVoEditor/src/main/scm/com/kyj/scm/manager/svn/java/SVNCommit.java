/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : kyj.Fx.scm.manager.core.common
 *	작성일   : 2016. 7. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn.java;

import java.io.File;
import java.util.Properties;
import java.util.stream.Stream;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNCopyClient;
import org.tmatesoft.svn.core.wc.SVNCopySource;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.kyj.scm.manager.core.commons.ISVNCommit;

/**
 * SVN Commit Operation 처리.
 *
 * @author KYJ
 *
 */
class SVNCommit extends AbstractSVN implements ISVNCommit {

	/**
	 * @param properties
	 */
	public SVNCommit(Properties properties) {
		super(properties);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 6.
	 * @param paths
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 */
	public SVNCommitInfo commit(File[] paths, String commitMessage) throws SVNException {
		SVNCommitClient commitClient = getSvnManager().getCommitClient();
		return commitClient.doCommit(paths, false, commitMessage, new SVNProperties(), null, false, true, SVNDepth.INFINITY);
	}

	public void copy(SVNURL targetURL, SVNURL[] paths) throws SVNException {
		SVNCopySource[] convert = convert(paths);

		/*
			doCopy(   SVNCopySource[] sources,
			              SVNURL dst,
			              boolean isMove,
			              boolean makeParents,
			              boolean failWhenDstExists,
			              java.lang.String commitMessage,
			              SVNProperties revisionProperties  )
			Copies each source in sources to dst.
		*/
		SVNCopyClient copyClient = getSvnManager().getCopyClient();
		copyClient.doCopy(convert, targetURL, false, false, false, "copy", new SVNProperties());
	}

	private SVNCopySource[] convert(SVNURL[] urls) {
		return Stream.of(urls).map(s -> convert(s)).toArray(SVNCopySource[]::new);
	}

	private SVNCopySource convert(SVNURL url) {
		return new SVNCopySource(SVNRevision.HEAD, SVNRevision.HEAD, url);
	}
}
