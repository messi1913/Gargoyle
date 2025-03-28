/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : kyj.Fx.scm.manager.core
 *	작성일   : 2016. 3. 22.   최초작성
 *              2016.7          API 추가. Commit , Log등
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn.java;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.util.SVNURLUtil;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.kyj.fx.voeditor.visual.framework.handler.ExceptionHandler;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.scm.manager.core.commons.AbstractScmManager;
import com.kyj.scm.manager.core.commons.SCMKeywords;
import com.kyj.scm.manager.core.commons.ScmDirHandler;

/**
 * SVN명령어를 모아놓은 매니저클래스
 *
 * @author KYJ
 *
 */

public class JavaSVNManager extends AbstractScmManager implements SCMKeywords, SVNFormatter {

	private static final Logger LOGGER = LoggerFactory.getLogger(JavaSVNManager.class);

	private SVNCat catCommand;

	private SVNList listCommand;

	private SVNLog logCommand;

	private SVNCheckout checkoutCommand;

	private SVNDiff diffCommand;

	private SVNImport svnImport;

	private SVNCommit svnCommit;

	private SVNResource svnResource;

	private Properties properties;

	/*
	 * [시작] SVN URL의 특이 프로토콜에 대한 체크를 위해 추가 변수 생성
	 *
	 */
	/**
	 * @최초생성일 2016. 12. 13.
	 */
	private boolean isHttp;
	/**
	 * @최초생성일 2016. 12. 13.
	 */
	private boolean isHttps;
	/* [끝] SVN URL의 특이 프로토콜에 대한 체크를 위해 추가 변수 생성 */

	public JavaSVNManager(Properties properties) {
		super();
		init(properties);
	}

	/**
	 * 초기화 처리
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 2.
	 * @param properties
	 */
	public void init(Properties properties) {
		this.properties = properties;
		this.checkoutCommand = new SVNCheckout(this, properties);
		this.catCommand = new SVNCat(this, properties);
		this.listCommand = new SVNList(this, properties);
		this.logCommand = new SVNLog(this, properties);
		this.diffCommand = new SVNDiff(this, properties);
		this.svnImport = new SVNImport(this, properties);
		this.svnCommit = new SVNCommit(this, properties);
		this.svnResource = new SVNResource(this, properties);

		isHttp = this.svnResource.isHttp();
		isHttps = this.svnResource.isHttps();
	}

	/**
	 * create new instance
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 1.
	 * @param url
	 * @return
	 */
	public static final JavaSVNManager createNewInstance(String url) {
		Properties properties = new Properties();
		properties.put(JavaSVNManager.SVN_URL, url);
		return new JavaSVNManager(properties);
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
		return this.svnResource.getUrl();
		// Object objURL = this.properties.get(SVN_URL);
		// if (objURL == null)
		// throw new GagoyleParamEmptyException("SVN URL IS EMPTY.");
		// return objURL.toString();
	}

	public String getUserId() {
		return this.svnResource.getUserId();
		// return getProperties().get(SVN_USER_ID).toString();
	}

	@Override
	public String getUserPassword() {
		return this.svnResource.getUserPassword();
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
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 20.
	 * @param url
	 * @return
	 */
	public String cat(SVNURL url) {
		return catCommand.cat(url);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 20.
	 * @param url
	 * @return
	 */
	public String cat(SVNURL url, String revision) {
		return catCommand.cat(url, revision);
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

	/**
	 * 메타정보를 포함하는 SVN 엔트리 반환
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 3.
	 * @param path
	 * @return
	 */
	public List<SVNDirEntry> listEntry(String path) {
		return listEntry(path, "-1", false, null);
	}

	/**
	 * 메타정보를 포함하는 SVN 엔트리 반환
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 3.
	 * @param path
	 * @param exceptionHandler
	 * @return
	 */
	public List<SVNDirEntry> listEntry(String path, Consumer<Exception> exceptionHandler) {
		return listEntry(path, "-1", false, exceptionHandler);
	}

	/**
	 * 메타정보를 포함하는 SVN 엔트리 반환
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 3.
	 * @param path
	 * @param revision
	 * @param exceptionHandler
	 * @return
	 */
	public List<SVNDirEntry> listEntry(String path, String revision, Consumer<Exception> exceptionHandler) {
		return listEntry(path, revision, false, exceptionHandler);
	}

	/**
	 * 메타정보를 포함하는 SVN 엔트리 반환
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 3.
	 * @param path
	 * @param revision
	 * @param recurive
	 * @param exceptionHandler
	 * @return
	 */
	public List<SVNDirEntry> listEntry(String path, String revision, boolean recurive, Consumer<Exception> exceptionHandler) {
		return listEntry(path, revision, recurive, null, exceptionHandler);
	}

	public List<SVNDirEntry> listEntry(String path, String revision, boolean recurive, Comparator<SVNDirEntry> compare,
			Consumer<Exception> exceptionHandler) {
		long parseLong = Long.parseLong(revision, 10);
		List<SVNDirEntry> listEntry = listCommand.listEntry(path, revision, recurive, exceptionHandler);
		Predicate<? super SVNDirEntry> predicate = null;

		if (parseLong == -1)
			predicate = v -> true;
		else
			predicate = v -> parseLong <= v.getRevision();

		Stream<SVNDirEntry> filter = listEntry.stream().filter(predicate);
		if (compare != null) {
			filter = filter.sorted(compare);
		}
		return filter.collect(Collectors.toList());
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 20.
	 * @param relativePath
	 * @param handler
	 * @throws Exception
	 */
	public void listEntry(String relativePath, SVNDirHandler handler) throws Exception {
		listCommand.listEntry(relativePath, handler);
	}

	@Override
	public <T> void listEntry(String relativePath, ScmDirHandler<T> handler) throws Exception {
		listEntry(relativePath, (SVNDirHandler) handler);
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
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 15.
	 * @param path
	 * @param revision
	 * @param exceptionHandler
	 * @return
	 */
	public List<SVNLogEntry> log(String path, int revision, Consumer<Exception> exceptionHandler) {
		return logCommand.log(path, String.valueOf(revision), exceptionHandler);
	}

	public List<SVNLogEntry> log(String path, int revision) throws RuntimeException {
		return logCommand.log(path, String.valueOf(revision), err -> {
			throw new RuntimeException(err);
		});
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
		return logCommand.logFileSystem( /* only one target supported. */new File[] { path }, 0, endDate, exceptionHandler);
	}

	/**
	 * 코드 체크아웃 <br/>
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 24.
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Long checkout(String param, File outDir) throws Exception {
		return checkoutCommand.checkout(param, outDir);
	}

	/**
	 * 코드 체크아웃 <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 15.
	 * @param param
	 * @param revision
	 * @param outDir
	 * @param exceptionHandler
	 * @return
	 * @throws Exception
	 */
	public Long checkout(String param, String revision, File outDir) throws Exception {
		return checkoutCommand.checkout(param, revision, outDir, null);
	}

	/**
	 * 코드 체크아웃 <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 15.
	 * @param param
	 * @param revision
	 * @param outDir
	 * @param exceptionHandler
	 * @return
	 * @throws Exception
	 */
	public Long checkout(String param, String revision, File outDir, ExceptionHandler exceptionHandler) throws Exception {
		return checkoutCommand.checkout(param, revision, outDir, exceptionHandler);
	}

	/**
	 * 코드 체크아웃 <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 20.
	 * @param param
	 * @param pegrevision
	 * @param revision
	 * @param outDir
	 * @return
	 * @throws Exception
	 */
	public Long checkout(String param, String pegrevision, String revision, File outDir) throws Exception {
		return checkoutCommand.checkout(param, pegrevision, revision, outDir, null);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 20.
	 * @param absolutePath
	 * @param pegrevision
	 * @param revision
	 * @param outDir
	 * @return
	 * @throws Exception
	 */
	public Long checkout(SVNURL absolutePath, String pegrevision, String revision, File outDir) throws Exception {
		return checkoutCommand.checkout(absolutePath, SVNRevision.parse(pegrevision), SVNRevision.parse(revision), SVNDepth.INFINITY,
				outDir, null);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 20.
	 * @param absolutePath
	 * @param pegrevision
	 * @param revision
	 * @param depth
	 * @param outDir
	 * @return
	 * @throws Exception
	 */
	public Long checkout(SVNURL absolutePath, String pegrevision, String revision, SVNDepth depth, File outDir) throws Exception {
		return checkoutCommand.checkout(absolutePath, SVNRevision.parse(pegrevision), SVNRevision.parse(revision), depth, outDir, null);
	}

	/**
	 * 코드 체크아웃 <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 20.
	 * @param param
	 * @param pegrevision
	 * @param revision
	 * @param outDir
	 * @param exceptionHandler
	 * @return
	 * @throws Exception
	 */
	public Long checkout(String param, String pegrevision, String revision, File outDir, ExceptionHandler exceptionHandler)
			throws Exception {
		return checkoutCommand.checkout(param, pegrevision, revision, outDir, exceptionHandler);
	}

	/********************************
	 * 작성일 : 2016. 5. 5. 작성자 : KYJ
	 *
	 * 차이점 비교
	 *
	 * @param path1
	 * @param path2
	 * @return
	 * @throws Exception
	 ********************************/
	public String diff(String path1, String path2) throws Exception {
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
	 * @throws Exception
	 ********************************/
	public String diff(String path1, SVNRevision rivision1, String path2, SVNRevision rivision2) throws Exception {
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
	 * @param relativePath
	 * @param filePath
	 * @param data
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 *             신규파일이 아닌 , 존재하는 파일 경우 에러발생.
	 */
	public SVNCommitInfo commit_new(String relativePath, String fileName, byte[] data, String commitMessage) throws SVNException {
		return svnCommit.addFileCommit(relativePath, fileName, data, commitMessage);
	}

	/**
	 *
	 * 추가되는 코드가 신규파일인경우 사용. (형상으로 관리되지않던 신규파일인경우에만 사용.)
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @param relativePath
	 * @param filePath
	 * @param inputStream
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 *             신규파일이 아닌 , 존재하는 파일 경우 에러발생.
	 */
	public SVNCommitInfo commit_new(String relativePath, String fileName, InputStream inputStream, String commitMessage)
			throws SVNException {
		return svnCommit.addFileCommit(relativePath, fileName, inputStream, commitMessage);
	}

	/**
	 *
	 * 디렉토리 추가.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 13.
	 * @param relativePath
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 */
	public SVNCommitInfo commit_new(String relativePath, String commitMessage) throws SVNException {
		return svnCommit.addDirCommit(relativePath, commitMessage);
	}

	/**
	 *
	 * 코드 수정후 서버 반영
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 13.
	 * @param relativePath
	 * @param fileName
	 * @param oldData
	 * @param newData
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 * @throws IOException
	 */
	public SVNCommitInfo commit_modify(String relativePath, String fileName, InputStream oldData, InputStream newData, String commitMessage)
			throws SVNException, IOException {
		return svnCommit.modifyFileCommit(relativePath, fileName, oldData, newData, commitMessage);
	}

	/**
	 * 코드 수정후 서버 반영
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 13.
	 * @param relativePath
	 * @param fileName
	 * @param newData
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 * @throws IOException
	 */
	public SVNCommitInfo commit_modify(String relativePath, String fileName, InputStream newData, String commitMessage)
			throws SVNException, IOException {
		String headerRevisionContent = this.catCommand.cat(relativePath.concat("/").concat(fileName));
		return svnCommit.modifyFileCommit(relativePath, fileName, new ByteArrayInputStream(headerRevisionContent.getBytes("UTF-8")),
				newData, commitMessage);
	}

	/**
	 * revision 기준으로 코드를 되돌린다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 13.
	 * @param relativePath
	 * @param fileName
	 * @param revision
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 * @throws IOException
	 */
	public SVNCommitInfo commit_modify_reverse(String relativePath, String fileName, long revision) throws SVNException, IOException {

		String currentContent = this.catCommand.cat(relativePath.concat("/").concat(fileName));
		String revertContent = this.catCommand.cat(relativePath.concat("/").concat(fileName), String.valueOf(revision));

		return svnCommit.modifyFileCommit(relativePath, fileName, new ByteArrayInputStream(currentContent.getBytes("UTF-8")),
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
	 * @deprecated 서버간의 API연계에서 사용되면 안됨. 해당 API가 사용되는 때는 FileSystem으로 버젼관리가 되는
	 *             상황에서만 사용되야함. (( 로컬시스템으로 svn파일이 관리되는 경우에만 사용. ))
	 */
	@Deprecated
	public SVNCommitInfo commitClient(File[] paths, String commitMessage) throws SVNException, IOException {
		return svnCommit.commitClient(paths, commitMessage);
	}

	/**
	 * Resource가 서버에 존재하는지 여부를 체크함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @param relativePath
	 * @return
	 * @throws SVNException
	 */
	public boolean isExistsPath(String relativePath) throws Exception {
		return this.svnResource.isExists(relativePath);
	}

	public Collection<SVNLogEntry> getAllLogs(String relativePath) throws SVNException {
		return getAllLogs(relativePath, 0);
	}

	public Collection<SVNLogEntry> getAllLogs(long startRevision, long endRevision) throws SVNException {
		return getAllLogs("", startRevision, endRevision);
	}

	public Collection<SVNLogEntry> getAllLogs(String relativePath, long startRevision) throws SVNException {
		long latestRevision = this.svnResource.getLatestRevision();
		return getAllLogs(relativePath, startRevision, latestRevision);
	}

	public Collection<SVNLogEntry> getAllLogs(String relativePath, long startRevision, long endRevision) throws SVNException {
		return this.logCommand.getAllLogs(relativePath, startRevision, endRevision);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 19.
	 * @param date
	 * @return
	 * @throws SVNException
	 */
	public Collection<SVNLogEntry> getAllLogs(Date date) throws SVNException {
		long startRevision = getRevision(date);
		long endRevision = getRevision(date);
		return getAllLogs(startRevision, endRevision);
	}

	/**
	 * 가장 최신 리비젼 번호를 구함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @return
	 * @throws SVNException
	 */
	public long getLatestRevision() throws SVNException {
		return this.svnResource.getLatestRevision();
	}

	/**
	 * 날짜에 매치되는 리비젼 번호를 구함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @param date
	 * @return
	 * @throws SVNException
	 */
	public long getRevision(Date date) throws SVNException {
		long r = this.svnResource.getRevision(date);
		return r < svnResource.getLatestRevision() ? r + 1 : r;
	}

	/**
	 * SVN Root Url
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 21.
	 * @return
	 */
	public String getRootUrl() {
		return this.svnResource.getRootUrl();
	}

	/********************************
	 * 작성일 : 2016. 7. 31. 작성자 : KYJ
	 *
	 * SVN 서버 접속 여부를 확인
	 * 
	 * @throws SVNException
	 ********************************/
	public void ping() throws SVNException {
		this.svnResource.ping();
	}

	/********************************
	 * 작성일 : 2016. 7. 31. 작성자 : KYJ
	 *
	 * SVN 서버 RepositoryUUID를 리턴.
	 *
	 * @return
	 * @throws SVNException
	 ********************************/
	public String getRepositoryUUID() throws SVNException {
		return this.svnResource.getRepositoryUUID();
	}

	/**
	 * SVN 서버에 연결된 루트 디렉토리에 속하는 로컬 파일시스템의 파일에 해당하는 SVN서버 경로를 리턴한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 1.
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public SVNURL getSvnUrlByFileSystem(File file) throws Exception {
		return this.svnResource.getSvnUrlByFileSystem(file, SVNRevision.create(-1L));
	}

	/**
	 * 현재 객체에 설정된 Properties를 리턴한다. 원본 데이터가 변경되자않게하기위해 객체를 새로 생성후 반환한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 9.
	 * @param file
	 * @return
	 */
	public Properties getProperties() {
		return (Properties) this.properties.clone();
	}

	/**
	 * 상대경로를 구함.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 13.
	 * @param path
	 * @return
	 * @throws SVNException
	 */
	public String relativePath(SVNURL path) throws SVNException {
		return svnResource.relativePath(path);
	}

	/**
	 * svnurl이 http 프로토콜인지 확인
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 13.
	 * @return
	 */
	public final boolean isHttp() {
		return isHttp;
	}

	/**
	 * svnurl이 https 프로토콜인지 확인
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 13.
	 * @return
	 */
	public final boolean isHttps() {
		return isHttps;
	}

	@Override
	public File tmpDir() {
		return new File(SystemUtils.getJavaIoTmpDir(), "svn");
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 23.
	 * @param parentURL
	 * @param childURL
	 * @param urlEncoding
	 * @return
	 */
	public static String relativePath(String parentURL, String childURL, boolean urlEncoding) {
		String relativePath = childURL;
		// 상대경로로 수정
		if (ValueUtil.isNotEmpty(childURL)) {
			try {

				if (childURL.startsWith(parentURL)) {
					SVNURL pa = SVNURL.parseURIEncoded(parentURL);
					SVNURL s = SVNURL.parseURIEncoded(childURL);
					String relativeURL = SVNURLUtil.getRelativeURL(pa, s, urlEncoding);
					relativePath = relativeURL;
				} else {
					return childURL;
				}

			} catch (SVNException e) {
				e.printStackTrace();
			}
		}

		return relativePath;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 20.
	 * @param relativePath
	 * @param relativePath
	 * @param properties
	 * @param contents
	 * @return
	 * @throws SVNException
	 */
	public long getCopy(String relativePath, long revision, SVNProperties properties, OutputStream contents) throws SVNException {
		return checkoutCommand.getCopy(relativePath, revision, properties, contents);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 20.
	 * @param absolutePath
	 * @param revision
	 * @param properties
	 * @param contents
	 * @return
	 * @throws SVNException
	 */
	public long getCopy(SVNURL absolutePath, long revision, SVNProperties properties, OutputStream contents) throws SVNException {
		return checkoutCommand.getCopy(absolutePath, revision, properties, contents);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 20.
	 * @param absolutePath
	 * @param revision
	 * @param properties
	 * @param outFile
	 * @return
	 * @throws SVNException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public File getCopy(SVNURL absolutePath, long revision, SVNProperties properties, File outFile)
			throws SVNException, FileNotFoundException, IOException {
		return checkoutCommand.getCopy(absolutePath, revision, properties, outFile);
	}
}
