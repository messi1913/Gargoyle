/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.dimmension
 *	작성일   : 2017. 4. 13.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.dimmension;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SystemUtils;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNLogEntry;

import com.kyj.scm.manager.core.commons.AbstractScmManager;
import com.kyj.scm.manager.core.commons.DimKeywords;
import com.kyj.scm.manager.core.commons.ScmDirHandler;

/**
 * 디멘전 명령어를 모아놓은 매니저 클래스
 *
 * @author KYJ
 *
 */

public class DimmensionManager extends AbstractScmManager implements DimKeywords, DimFormatter {

	/**
	 * 조회 처리
	 * @최초생성일 2017. 4. 13.
	 */
	private DimCat catCommand;

	/**
	 * 리스팅 처리
	 * @최초생성일 2017. 4. 13.
	 */
	private DimList listCommand;

	/**
	 * 이력 처리
	 * @최초생성일 2017. 4. 13.
	 */
	private DimLog logCommand;

	/**
	 * 업데이트 및 체크아웃 관리
	 * @최초생성일 2017. 4. 13.
	 */
	private DimCheckout checkoutCommand;

	/**
	 * 리소스 자원에 관한 처리.
	 * @최초생성일 2017. 4. 13.
	 */
	private DimResource resourceCommand;

	private Properties properties;

	public DimmensionManager(Properties properties) {
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
		this.checkoutCommand = new DimCheckout(this, properties);
		this.catCommand = new DimCat(this, properties);
		this.listCommand = new DimList(this, properties);
		this.logCommand = new DimLog(this, properties);
		this.resourceCommand = new DimResource(this, properties);
	}

	/**
	 * create new instance
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 1.
	 * @param url
	 * @return
	 */
	public static final DimmensionManager createNewInstance(Properties properties) {
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
		return this.resourceCommand.getUrl();
	}

	public String getUserId() {
		return this.resourceCommand.getUserId();
	}

	/**
	 * dimension cat명령어
	 *  
	 *  path에 해당하는 데이터 본문을 리턴함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 23.
	 * @param param
	 * @return
	 */
	public String cat(String path) {
		return catCommand.cat(path);
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
	 * Root Url
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

	/**
	
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 13.
	 * @param file
	 * @return
	 */
	public Properties getProperties() {
		return this.resourceCommand.getProperties();
	}

	@Override
	public String getUserPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void listEntry(String relativePath, ScmDirHandler handler) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isExistsPath(String relativePath) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getLatestRevision() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getRevision(Date date) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public File tmpDir() {
		return new File(SystemUtils.getJavaIoTmpDir(), "dimmension");
	}
}
