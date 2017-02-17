/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.svn.java
 *	작성일   : 2016. 5. 5.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.dimmension;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.kyj.scm.manager.core.commons.IDiffCommand;
import com.sun.star.uno.RuntimeException;

/***************************
 *
 * Diff 구현
 *
 * @author KYJ
 *
 ***************************/
public class DimDiff extends AbstractDimmension implements IDiffCommand<String, String, String> {

	/**
	 * @param javaSVNManager
	 * @param properties
	 */
	public DimDiff(DimmensionManager javaSVNManager, Properties properties) {
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
	public String diff(String path1, SVNRevision rivision1, String path2, SVNRevision rivision2) throws Exception {
		throw new RuntimeException("Not yet support");
	}

}
