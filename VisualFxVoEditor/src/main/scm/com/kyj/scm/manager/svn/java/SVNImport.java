/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.svn.java
 *	작성일   : 2016. 7. 11.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn.java;

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

/**
 * 최초 프로젝트 import
 *
 * @author KYJ
 *
 */
class SVNImport extends AbstractSVN implements ISCMCommit {

	private static final Logger LOGGER = LoggerFactory.getLogger(SVNImport.class);

	/**
	 * @param javaSVNManager
	 * @param properties
	 */
	public SVNImport(JavaSVNManager javaSVNManager, Properties properties) {
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
	public int importProject(String from, SVNURL to) throws Exception {

		int result = -1; //시작값은 fail value

		File tempGagoyle = FileUtil.getTempGagoyle();
		File tmpFile = new File(tempGagoyle, "tmpProject" + System.currentTimeMillis());

		if (!tmpFile.exists())
			tmpFile.mkdirs();

		try {
			// import할 대상 프로젝트를 checout 받는다.
			long checkout = getJavaSVNManager().checkout(from, tmpFile);
			if (checkout != -1) {
				doImport(tmpFile, to);
			}

			//update result 1
			result = 1;
		} finally {

			FileUtil.deleteDir(tmpFile);

		}

		return result;

	}

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
	private SVNCommitInfo doImport(File checkoutedDir, SVNURL to) throws SVNException {

		if (checkoutedDir.exists()) {
			SVNCommitClient commitClient = getSvnManager().getCommitClient();
			SVNCommitInfo doImport = commitClient.doImport(checkoutedDir, to, "Init Commit . SVN Import Command", new SVNProperties(), true,
					true, SVNDepth.INFINITY);

			LOGGER.debug(" Do SVN Import Command ");

			return doImport;
		}

		return SVNCommitInfo.NULL;

	}
}
