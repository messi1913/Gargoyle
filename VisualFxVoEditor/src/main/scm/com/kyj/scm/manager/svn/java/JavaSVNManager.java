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
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
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
	 * svn 리비젼 정보 조회
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 13.
	 * @param path
	 * @param endDate
	 * @param exceptionHandler
	 * @return
	 */
	public List<SVNLogEntry> log(String path, Date endDate, Consumer<Exception> exceptionHandler) {
		return logCommand.log(path, -1, endDate, exceptionHandler);
	}

	/**
	 * svn 리비젼 정보 조회
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 13.
	 * @param path
	 * @param startRevision
	 * @param endDate
	 * @param exceptionHandler
	 * @return
	 */
	public List<SVNLogEntry> log(String path, long startRevision, Date endDate, Consumer<Exception> exceptionHandler) {
		return logCommand.log(path, startRevision, endDate, exceptionHandler);
	}

	/********************************
	 * 작성일 : 2016. 7. 13. 작성자 : KYJ
	 *
	 * FileSystem Base Log.
	 * 
	 * @param path
	 * @param startRevision
	 * @param endDate
	 * @param exceptionHandler
	 * @return
	 ********************************/
	public List<SVNLogEntry> logFileSystem(File path, Date endDate, Consumer<Exception> exceptionHandler) {
		return logCommand.logFileSystem( /*only one target supported.*/new File[] { path }, 0, endDate, exceptionHandler);
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
	 * 추가되는 코드가 신규파일인경우 사용. (형상으로 관리되지않던 신규파일인경우에만 사용.)
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @param dirPath
	 * @param filePath
	 * @param data
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 *             신규파일이 아닌 , 존재하는 파일 경우 에러발생.
	 */
	public SVNCommitInfo commit_new(String dirPath, String fileName, byte[] data, String commitMessage) throws SVNException {
		return svnCommit.addFileCommit(dirPath, fileName, data, commitMessage);
	}

	/**
	 *
	 * 추가되는 코드가 신규파일인경우 사용. (형상으로 관리되지않던 신규파일인경우에만 사용.)
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @param dirPath
	 * @param filePath
	 * @param inputStream
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 *             신규파일이 아닌 , 존재하는 파일 경우 에러발생.
	 */
	public SVNCommitInfo commit_new(String dirPath, String fileName, InputStream inputStream, String commitMessage) throws SVNException {
		return svnCommit.addFileCommit(dirPath, fileName, inputStream, commitMessage);
	}

	/**
	 *
	 * 디렉토리 추가.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 13.
	 * @param dirPath
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 */
	public SVNCommitInfo commit_new(String dirPath, String commitMessage) throws SVNException {
		return svnCommit.addDirCommit(dirPath, commitMessage);
	}

	/**
	 *
	 * 코드 수정후 서버 반영
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 13.
	 * @param dirPath
	 * @param fileName
	 * @param oldData
	 * @param newData
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 * @throws IOException
	 */
	public SVNCommitInfo commit_modify(String dirPath, String fileName, InputStream oldData, InputStream newData, String commitMessage)
			throws SVNException, IOException {
		return svnCommit.modifyFileCommit(dirPath, fileName, oldData, newData, commitMessage);
	}

	/**
	 * 코드 수정후 서버 반영
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 13.
	 * @param dirPath
	 * @param fileName
	 * @param newData
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 * @throws IOException
	 */
	public SVNCommitInfo commit_modify(String dirPath, String fileName, InputStream newData, String commitMessage)
			throws SVNException, IOException {
		String headerRevisionContent = this.catCommand.cat(dirPath.concat("/").concat(fileName));
		return svnCommit.modifyFileCommit(dirPath, fileName, new ByteArrayInputStream(headerRevisionContent.getBytes("UTF-8")), newData,
				commitMessage);
	}

	/**
	 * revision 기준으로 코드를 되돌린다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 13.
	 * @param dirPath
	 * @param fileName
	 * @param revision
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 * @throws IOException
	 */
	public SVNCommitInfo commit_modify_reverse(String dirPath, String fileName, long revision) throws SVNException, IOException {

		String currentContent = this.catCommand.cat(dirPath.concat("/").concat(fileName));
		String revertContent = this.catCommand.cat(dirPath.concat("/").concat(fileName), String.valueOf(revision));

		return svnCommit.modifyFileCommit(dirPath, fileName, new ByteArrayInputStream(currentContent.getBytes("UTF-8")),
				new ByteArrayInputStream(revertContent.getBytes("UTF-8")), "Revert");

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 13.
	 * @param paths
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 * @throws IOException
	 *
	 * @deprecated 서버간의 API연계에서 사용되면 안됨. 해당 API가 사용되는 때는 FileSystem으로 버젼관리가 되는 상황에서만 사용되야함. (( 로컬시스템으로 svn파일이 관리되는 경우에만 사용. ))
	 */
	@Deprecated
	public SVNCommitInfo commitClient(File[] paths, String commitMessage) throws SVNException, IOException {
		return svnCommit.commitClient(paths, commitMessage);
	}

}
