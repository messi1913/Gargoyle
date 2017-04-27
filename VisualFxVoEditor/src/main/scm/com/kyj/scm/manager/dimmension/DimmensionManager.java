/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.dimmension
 *	작성일   : 2017. 4. 13.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.dimmension;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.SystemUtils;

import com.kyj.scm.manager.core.commons.AbstractScmManager;
import com.kyj.scm.manager.core.commons.DimKeywords;
import com.kyj.scm.manager.core.commons.ScmDirHandler;
import com.serena.dmclient.api.DimensionsConnection;
import com.serena.dmclient.api.Filter;
import com.serena.dmclient.api.ItemRevision;
import com.serena.dmclient.api.RepositoryFolder;
import com.serena.dmclient.api.SystemAttributes;

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
	 * 값이 없거나 유효하지않는경우 -1
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public float getRevision(String projSpec, String path, String fileName) throws Exception {

		DimDirHandler handler = new DimDirHandler() {

			Object findOne;

			@Override
			public boolean test(RepositoryFolder entry) {
				return entry.getName().equals(path);
			}

			@Override
			public Filter itemFilter() {
				Filter filter = new Filter();
				filter.criteria().add(new Filter.Criterion(SystemAttributes.IS_LATEST_REV, "Y", 0));
				filter.criteria().add(new Filter.Criterion(SystemAttributes.ITEMFILE_FILENAME, fileName, Filter.Criterion.EQUALS));
				return filter;
			}

			@Override
			public void handle(ItemRevision r) {
				r.queryAttribute(SystemAttributes.REVISION);
				findOne = r.getAttribute(SystemAttributes.REVISION);
			}

			@Override
			public Object get() {
				return findOne;
			}

		};

		this.listEntry(projSpec, path, handler);

		Object object = handler.get();

		if (object != null)
			return Float.parseFloat(object.toString());

		return -1;
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
		return resourceCommand.getUserPassword();
	}

	@Override
	public <K> void listEntry(String path, ScmDirHandler<K> handler) throws Exception {
		listEntry(path, (DimDirHandler) handler);
	}

	public void listEntry(String path, DimDirHandler handler) throws Exception {
		listCommand.listEntry(path, handler);
	}

	public void listEntry(String projSpec, String path, DimDirHandler handler) throws Exception {
		listCommand.listEntry(projSpec, path, handler);
	}

	public void listEntry(RepositoryFolder rootFolder, DimDirHandler dimDirHandler) throws Exception {
		listCommand.listEntry(rootFolder, dimDirHandler);
	}

	@Override
	public boolean isExistsPath(String relativePath) throws Exception {
		return resourceCommand.isExists(relativePath);
	}

	/* 
	 * 최신 리비전 보기
	 * 디멘전에선 -1 리턴
	 * (non-Javadoc)
	 * @see com.kyj.scm.manager.core.commons.AbstractScmManager#getLatestRevision()
	 * 
	 * @Deprecated
	 * 정확한 수치값이 아닌 -1을 리턴함.
	 */
	@Override
	@Deprecated
	public long getLatestRevision() throws Exception {
		return resourceCommand.getLatestRevision();
	}

	/* 
	 * 디멘전에서 구할 수 없음.
	 * (non-Javadoc)
	 * @see com.kyj.scm.manager.core.commons.AbstractScmManager#getRevision(java.util.Date)
	 */
	@Override
	@Deprecated
	public long getRevision(Date date) throws Exception {
		return resourceCommand.getRevision(date);
	}

	public ItemRevision searchFindOne(RepositoryFolder folder, List<String> linkedList, String revision) {
		return this.resourceCommand.searchFindOne(folder, linkedList, revision);
	}

	/**
	 * 디멘전 커넥션 리턴
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 14. 
	 * @return
	 */
	public DimensionsConnection getConnection() {
		return this.resourceCommand.getConnection();
	}

	@Override
	public File tmpDir() {
		return new File(SystemUtils.getJavaIoTmpDir(), "dimmension");
	}

	/* (non-Javadoc)
	 * @see com.kyj.scm.manager.core.commons.AbstractScmManager#list(java.lang.String)
	 */
	@Override
	public List<String> list(String path) {
		return listCommand.list(path);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 24. 
	 * @param projSpec
	 * @param path
	 * @param fileName
	 * @param revision
	 * @param convert
	 * @param exceptionHandler
	 * @return
	 */
	public <T> List<T> list(String projSpec, String path, String fileName, String revision, Function<ItemRevision, T> convert,
			Consumer<Exception> exceptionHandler) {
		return listCommand.list(projSpec, path, fileName, revision, convert, exceptionHandler);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 24. 
	 * @param conn
	 */
	public void close(DimensionsConnection conn) {
		resourceCommand.close(conn);
	}

	/* 
	 * 아래 파라미터 구현 지원안함.
	 */
	@Deprecated
	@Override
	public Long checkout(String param, File outDir) throws Exception {
		throw new RuntimeException("not support.");
	}

}
