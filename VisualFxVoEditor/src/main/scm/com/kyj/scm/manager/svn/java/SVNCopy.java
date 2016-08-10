/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.svn.java
 *	작성일   : 2016. 7. 11.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn.java;

import java.util.Properties;
import java.util.stream.Stream;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNCopyClient;
import org.tmatesoft.svn.core.wc.SVNCopySource;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.kyj.scm.manager.core.commons.ISCMCommit;

/**
 *
 * Project 복사.
 *
 * Copy의 경우 Revision 정보까지 복사하고자하는 경우 사용.
 *
 * @author KYJ
 *
 */
public class SVNCopy extends AbstractSVN implements ISCMCommit {

	/**
	 * @param javaSVNManager
	 * @param properties
	 */
	public SVNCopy(JavaSVNManager javaSVNManager, Properties properties) {
		super(javaSVNManager, properties);
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
		copyClient.doCopy(convert, targetURL, false, false, false, "copy by O-PERA", new SVNProperties());
	}

	private SVNCopySource[] convert(SVNURL[] urls) {
		return Stream.of(urls).map(s -> convert(s)).toArray(SVNCopySource[]::new);
	}

	private SVNCopySource convert(SVNURL url) {
		return new SVNCopySource(SVNRevision.HEAD, SVNRevision.HEAD, url);
	}
}
