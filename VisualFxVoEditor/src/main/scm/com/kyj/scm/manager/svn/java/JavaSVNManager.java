/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : kyj.Fx.scm.manager.core
 *	작성일   : 2016. 3. 22.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn.java;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.kyj.fx.voeditor.visual.exceptions.GagoyleParamEmptyException;
import com.kyj.scm.manager.core.commons.SVNKeywords;

/**
 * SVN명령어를 모아놓은 매니저클래스
 *
 * @author KYJ
 *
 */
public class JavaSVNManager implements SVNKeywords {

	private SVNCat catCommand;

	private SVNList listCommand;

	private SVNLog logCommand;

	private SVNCheckout checkoutCommand;

	private SVNDiff diffCommand;

	private SVNImport svnImport;

	private SVNCommit svnCommit;

	private Properties properties;

	public JavaSVNManager(Properties properties) {
		init(properties);
	}

	/**
	 * 초기화 처리
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 2.
	 * @param properties
	 */
	void init(Properties properties) {
		this.properties = properties;
		this.checkoutCommand = new SVNCheckout(this, properties);
		this.catCommand = new SVNCat(this, properties);
		this.listCommand = new SVNList(this, properties);
		this.logCommand = new SVNLog(this, properties);
		this.diffCommand = new SVNDiff(this, properties);
		this.svnImport = new SVNImport(this, properties);
		this.svnCommit = new SVNCommit(this, properties);
	}

	/**
	 * URL정보가 존재하는지 체크함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 4.
	 * @return
	 */
	public boolean isContainsURL() {
		return this.properties.containsKey(SVN_URL) && this.properties.containsValue(SVN_URL);
	}

	/**
	 * USER ID정보가 존재하는지 체크함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 4.
	 * @return
	 */
	public boolean isContainsUserId() {
		return this.properties.containsKey(SVN_USER_ID) && this.properties.containsValue(SVN_USER_ID);
	}

	/**
	 * SVN Connection URL RETURN.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 2.
	 * @return
	 */
	public String getUrl() {
		Object objURL = this.properties.get(SVN_URL);
		if (objURL == null)
			throw new GagoyleParamEmptyException("SVN URL IS EMPTY.");
		return objURL.toString();
	}

	public Object getUserId() {
		return this.properties.get(SVN_USER_ID);
		//		if (objUserId == null)
		//			throw new GagoyleParamEmptyException("SVN ID IS EMPTY.");
		//		return objUserId.toString();
	}

	/**
	 * svn cat명령어
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 23.
	 * @param param
	 * @return
	 */
	public String cat(String param) {
		return catCommand.cat(param);
	}

	/**
	 * svn cat명령어
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 23.
	 * @param revision
	 * @param path
	 * @return
	 */
	public String cat(String path, String revision) {
		return catCommand.cat(path, revision);
	}

	/**
	 * svn cat명령어
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 23.
	 * @param param
	 * @return
	 */
	public List<String> list(String path) {
		return listCommand.list(path);
	}

	public List<SVNDirEntry> listEntry(String path) {
		return listCommand.listEntry(path, "-1", false, null);
	}

	public List<SVNDirEntry> listEntry(String path, Consumer<Exception> exceptionHandler) {
		return listCommand.listEntry(path, "-1", false, exceptionHandler);
	}

	/**
	 * svn 리비젼 정보 조회
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 24.
	 * @param param
	 * @return
	 */
	public List<SVNLogEntry> log(String path) {
		return logCommand.log(path);
	}

	/**
	 * svn 리비젼 정보 조회
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 13.
	 * @param path
	 * @param revision
	 * @param exceptionHandler
	 * @return
	 */
	public List<SVNLogEntry> log(String path, String revision, Consumer<Exception> exceptionHandler) {
		return logCommand.log(path, revision, exceptionHandler);
	}

	/**
	 * 코드 체크아웃
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 24.
	 * @param param
	 * @return
	 * @throws FileNotFoundException
	 */
	public Long checkout(String param, File outDir) throws FileNotFoundException {
		return checkoutCommand.checkout(param, outDir);
	}

	/********************************
	 * 작성일 : 2016. 5. 5. 작성자 : KYJ
	 *
	 * 차이점 비교
	 *
	 * @param path1
	 * @param path2
	 * @return
	 * @throws SVNException
	 ********************************/
	public String diff(String path1, String path2) throws SVNException {
		return diffCommand.diff(path1, path2);
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
		return diffCommand.diff(path1, rivision1, path2, rivision2);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 11.
	 * @param from
	 * @param to
	 * @throws Exception
	 */
	public void doImport(String from, SVNURL to) throws Exception {
		svnImport.importProject(from, to);
	}

	/**
	 * Commit Operator.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @param dirPath
	 * @param filePath
	 * @param data
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 */
	public SVNCommitInfo commit_new(String dirPath, String fileName, byte[] data, String commitMessage) throws SVNException {
		return svnCommit.addDirCommit(dirPath, fileName, data, commitMessage);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @param dirPath
	 * @param filePath
	 * @param inputStream
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 */
	public SVNCommitInfo commit_new(String dirPath, String fileName, InputStream inputStream, String commitMessage) throws SVNException {
		return svnCommit.addDirCommit(dirPath, fileName, inputStream, commitMessage);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @param dirPath
	 * @param fileName
	 * @param oldData
	 * @param newData
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 * @throws UnsupportedEncodingException
	 */
	public SVNCommitInfo commit_modify(String dirPath, String fileName, InputStream newData, String commitMessage)
			throws SVNException, UnsupportedEncodingException {
		String headerRevisionContent = this.catCommand.cat(dirPath.concat("/").concat(fileName));
		return svnCommit.modifyFileCommit(dirPath, fileName, new ByteArrayInputStream(headerRevisionContent.getBytes("UTF-8")), newData,
				commitMessage);
	}
}
