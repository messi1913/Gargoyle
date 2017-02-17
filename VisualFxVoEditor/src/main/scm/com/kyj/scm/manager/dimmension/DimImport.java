/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.svn.java
 *	작성일   : 2016. 7. 11.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.dimmension;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNCommitClient;

import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.scm.manager.core.commons.ISCMCommit;
import com.sun.star.uno.RuntimeException;

/**
 * 최초 프로젝트 import
 *
 * @author KYJ
 *
 */
class DimImport extends AbstractDimmension implements ISCMCommit {

	private static final Logger LOGGER = LoggerFactory.getLogger(DimImport.class);

	/**
	 * @param javaSVNManager
	 * @param properties
	 */
	public DimImport(DimmensionManager javaSVNManager, Properties properties) {
		super(javaSVNManager, properties);
	}

	//	public void importProject(SVNURL from, SVNURL to) throws FileNotFoundException {
	//		getJavaSVNManager().checkout(from.getPath(), new File("c:\\user\\desktop"));
	//	}

	/**
	 *
	 *  최초 한번 SVN에 저장된 한 프로젝트를 다른 서버로 이관시키는 경우 사용한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 11.
	 * @param from
	 * 			svn root에서 상대경로.
	 * @param to
	 * 			복사할 대상 URL
	 * @throws FileNotFoundException
	 * @return int
	 *    결과 상태값  -1 : 실패
	 * @throws SVNException
	 */
	public int importProject(String from, SVNURL to) throws Exception {throw new RuntimeException("Not yet support");}

	/**
	 *  SVN Import Command 수행.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 11.
	 * @param checkoutedDir
	 * @param to
	 * @return
	 * @throws SVNException
	 */
	private SVNCommitInfo doImport(File checkoutedDir, SVNURL to) throws SVNException {throw new RuntimeException("Not yet support");}
}
