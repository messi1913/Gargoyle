/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : kyj.Fx.scm.manager.core
 *	작성일   : 2016. 3. 22.   최초작성
 *              2016.7          API 추가. Commit , Log등
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.dimmension;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.kyj.fx.voeditor.visual.exceptions.GagoyleParamEmptyException;
import com.kyj.scm.manager.core.commons.DimKeywords;
import com.sun.star.uno.RuntimeException;

/**
 * SVN명령어를 모아놓은 매니저클래스
 *
 * @author KYJ
 *
 */

public class DimmensionManager implements DimKeywords, DimFormatter {

	private DimCat catCommand;

	private DimList listCommand;

	private DimLog logCommand;

	private DimCheckout checkoutCommand;

	private DimDiff diffCommand;

	private DimImport importCommand;

	private DimCommit commitCommand;

	private DimResource resourceCommand;

	private Properties properties;

	public DimmensionManager(Properties properties) {
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
		this.checkoutCommand = new DimCheckout(this, properties);
		this.catCommand = new DimCat(this, properties);
		this.listCommand = new DimList(this, properties);
		this.logCommand = new DimLog(this, properties);
		this.diffCommand = new DimDiff(this, properties);
		this.importCommand = new DimImport(this, properties);
		this.commitCommand = new DimCommit(this, properties);
		this.resourceCommand = new DimResource(this, properties);
	}

	/**
	 * create new instance
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 1.
	 * @param url
	 * @return
	 */
	public static final DimmensionManager createNewInstance(String url) {
		Properties properties = new Properties();
		properties.put(DimmensionManager.DIM_URL, url);
		return new DimmensionManager(properties);
	}

	/**
	 * URL정보가 존재하는지 체크함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 4.
	 * @return
	 */
	public boolean isContainsURL() {
		return this.properties.containsKey(DIM_URL) && this.properties.containsValue(DIM_URL);
	}

	/**
	 * USER ID정보가 존재하는지 체크함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 4.
	 * @return
	 */
	public boolean isContainsUserId() {
		return this.properties.containsKey(DIM_USER_ID) && this.properties.containsValue(DIM_USER_ID);
	}

	/**
	 * SVN Connection URL RETURN.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 2.
	 * @return
	 */
	public String getUrl() {
		Object objURL = this.properties.get(DIM_URL);
		if (objURL == null)
			throw new GagoyleParamEmptyException("Dimmension URL IS EMPTY.");
		return objURL.toString();
	}

	public Object getUserId() {
		return this.properties.get(DIM_USER_ID);
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

	/**
	 * 메타정보를 포함하는 SVN 엔트리 반환
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
		long parseLong = Long.parseLong(revision, 10);
		List<SVNDirEntry> listEntry = listCommand.listEntry(path, revision, recurive, exceptionHandler);
		Predicate<? super SVNDirEntry> predicate = null;

		if (parseLong == -1)
			predicate = v -> true;
		else
			predicate = v -> parseLong <= v.getRevision();

		return listEntry.stream().filter(predicate).collect(Collectors.toList());
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
		importCommand.importProject(from, to);
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
	 * @throws Exception
	 *             신규파일이 아닌 , 존재하는 파일 경우 에러발생.
	 */
	public SVNCommitInfo commit_new(String relativePath, String fileName, byte[] data, String commitMessage) throws Exception {
		return commitCommand.addFileCommit(relativePath, fileName, data, commitMessage);
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
	 * @throws Exception
	 *             신규파일이 아닌 , 존재하는 파일 경우 에러발생.
	 */
	public SVNCommitInfo commit_new(String relativePath, String fileName, InputStream inputStream, String commitMessage)
			throws Exception {
		return commitCommand.addFileCommit(relativePath, fileName, inputStream, commitMessage);
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
	 * @throws Exception
	 */
	public SVNCommitInfo commit_new(String relativePath, String commitMessage) throws Exception {
		return commitCommand.addDirCommit(relativePath, commitMessage);
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
	 * @throws Exception
	 * @throws IOException
	 */
	public SVNCommitInfo commit_modify(String relativePath, String fileName, InputStream oldData, InputStream newData, String commitMessage)
			throws Exception, IOException {
		return commitCommand.modifyFileCommit(relativePath, fileName, oldData, newData, commitMessage);
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
	 * @throws Exception
	 * @throws IOException
	 */
	public SVNCommitInfo commit_modify(String relativePath, String fileName, InputStream newData, String commitMessage)
			throws Exception, IOException {
		String headerRevisionContent = this.catCommand.cat(relativePath.concat("/").concat(fileName));
		return commitCommand.modifyFileCommit(relativePath, fileName, new ByteArrayInputStream(headerRevisionContent.getBytes("UTF-8")),
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
	 * @throws Exception
	 * @throws IOException
	 */
	public SVNCommitInfo commit_modify_reverse(String relativePath, String fileName, long revision) throws Exception, IOException {

		String currentContent = this.catCommand.cat(relativePath.concat("/").concat(fileName));
		String revertContent = this.catCommand.cat(relativePath.concat("/").concat(fileName), String.valueOf(revision));

		return commitCommand.modifyFileCommit(relativePath, fileName, new ByteArrayInputStream(currentContent.getBytes("UTF-8")),
				new ByteArrayInputStream(revertContent.getBytes("UTF-8")), "Revert");

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 13.
	 * @param paths
	 * @param commitMessage
	 * @return
	 * @throws Exception
	 * @throws IOException
	 *
	 * @deprecated 서버간의 API연계에서 사용되면 안됨. 해당 API가 사용되는 때는 FileSystem으로 버젼관리가 되는 상황에서만 사용되야함. (( 로컬시스템으로 svn파일이 관리되는 경우에만 사용. ))
	 */
	@Deprecated
	public SVNCommitInfo commitClient(File[] paths, String commitMessage) throws Exception, IOException {
		return commitCommand.commitClient(paths, commitMessage);
	}

	/**
	 * Resource가 서버에 존재하는지 여부를 체크함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @param relativePath
	 * @return
	 * @throws Exception
	 */
	public boolean isExistsPath(String projSpec, String relativePath) throws Exception {
		return this.resourceCommand.isExists(projSpec, relativePath);
	}

	public Collection<SVNLogEntry> getAllLogs(String projSpec, String relativePath) throws Exception {
		return getAllLogs(projSpec, relativePath, 0);
	}

	public Collection<SVNLogEntry> getAllLogs(long startRevision, long endRevision) throws Exception {
		return getAllLogs("", startRevision, endRevision);
	}

	public Collection<SVNLogEntry> getAllLogs(String projSpec, String relativePath, long startRevision) throws Exception {
		long latestRevision = this.resourceCommand.getLatestRevision(projSpec);
		return getAllLogs(relativePath, startRevision, latestRevision);
	}

	public Collection<SVNLogEntry> getAllLogs(String relativePath, long startRevision, long endRevision) throws Exception {
		return this.logCommand.getAllLogs(relativePath, startRevision, endRevision);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 19.
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public Collection<SVNLogEntry> getAllLogs(Date date) throws Exception {
		throw new RuntimeException("Not Yet Support");
	}

	/**
	 * 가장 최신 리비젼 번호를 구함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @return
	 * @throws Exception
	 */
	public long getLatestRevision(String projSpec) throws Exception {
		return this.resourceCommand.getLatestRevision(projSpec);
	}

	/**
	 * 날짜에 매치되는 리비젼 번호를 구함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public long getRevision(String projSpec, Date date) throws Exception {
		long r = this.resourceCommand.getRevision(date);
		return r < resourceCommand.getLatestRevision(projSpec) ? r + 1 : r;
	}

	/**
	 * SVN Root Url
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 21.
	 * @return
	 */
	public String getRootUrl() {
		return this.resourceCommand.getRootUrl();
	}

	/********************************
	 * 작성일 :  2016. 7. 31. 작성자 : KYJ
	 *
	 * SVN 서버 접속 여부를 확인
	 * @throws Exception
	 ********************************/
	public void ping() throws Exception {
		this.resourceCommand.ping();
	}

	/********************************
	 * 작성일 :  2016. 7. 31. 작성자 : KYJ
	 *
	 * SVN 서버 RepositoryUUID를 리턴.
	 *
	 * @return
	 * @throws Exception
	 ********************************/
	public String getRepositoryUUID() throws Exception {
		return this.resourceCommand.getRepositoryUUID();
	}

	/**
	 * SVN 서버에 연결된 루트 디렉토리에 속하는
	 * 로컬 파일시스템의 파일에 해당하는 SVN서버 경로를
	 * 리턴한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 1.
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public SVNURL getSvnUrlByFileSystem(File file) throws Exception {
		throw new RuntimeException("Not Yet Support");
	}

	/**
	 * 현재 객체에 설정된 Properties를 리턴한다.
	 * 원본 데이터가 변경되자않게하기위해
	 * 객체를 새로 생성후 반환한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 9.
	 * @param file
	 * @return
	 */
	public Properties getProperties() {
		return (Properties) this.properties.clone();
	}
}
