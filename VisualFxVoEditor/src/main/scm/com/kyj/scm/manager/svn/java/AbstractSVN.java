/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : kyj.Fx.scm.manager.command.svn
 *	작성일   : 2016. 3. 23.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn.java;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.kyj.scm.manager.core.commons.SCMCommonable;
import com.kyj.scm.manager.core.commons.SVNKeywords;

import kyj.Fx.dao.wizard.core.util.ValueUtil;

/**
 * SVN에 접속하기 위한 메타정보를 처리한다.
 *
 * @author KYJ
 *
 */
abstract class AbstractSVN implements SCMCommonable, SVNKeywords {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSVN.class);

	/** [시작] 일반상수 정의 */
	public static final String SVN_REVISION_HISTORY_ROW_COUNT = "15";
	/** [끝] 일반상수 정의 */

	private Properties properties;

	private SVNURL svnURL;
	private SVNRepository repository;
	private ISVNAuthenticationManager authManager;
	private SVNClientManager svnManager;
	private final JavaSVNManager javaSVNManager;

	public AbstractSVN(JavaSVNManager javaSVNManager, Properties properties) {
		this.javaSVNManager = javaSVNManager;
		init(properties);
	}

	/**
	 * svn 커맨드 유효성 체크
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 24.
	 */
	private void validate() {
		if (this.properties == null) {
			throw new RuntimeException("properties is null.");
		}

		Object url = this.properties.get(SCMCommonable.SVN_URL);
		if (url == null || url.toString().trim().isEmpty())
			throw new RuntimeException("does not exist url");

		//		Object userId = this.properties.get(SCMCommonable.SVN_USER_ID);
		//		if (userId == null || userId.toString().trim().isEmpty())
		//			throw new RuntimeException("does not exist userId");
		//
		//		Object userPass = this.properties.get(SCMCommonable.SVN_USER_PASS);
		//		if (userPass == null || userPass.toString().trim().isEmpty()) {
		//			throw new RuntimeException("does not exist userPassword");
		//		}

	}

	/**
	 * 접속정보 초기화
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 5. 2.
	 * @param properties
	 */
	public void init(Properties properties) {
		this.properties = properties;
		validate();

		try {
			svnURL = SVNURL.parseURIEncoded(getUrl());
			repository = SVNRepositoryFactory.create(svnURL);

			if ((getUserId() == null && getUserPassword() == null) || (getUserId().isEmpty() && getUserPassword().isEmpty())) {
				authManager = SVNWCUtil.createDefaultAuthenticationManager(SVNWCUtil.getDefaultConfigurationDirectory());
			} else {
				authManager = SVNWCUtil.createDefaultAuthenticationManager(getUserId(), getUserPassword().toCharArray());
			}

			repository.setAuthenticationManager(authManager);

			DefaultSVNOptions options = new DefaultSVNOptions();
			svnManager = SVNClientManager.newInstance(options, authManager);

			//			svnManager.dispose();
			//			repository.closeSession();
		} catch (SVNException e) {
			LOGGER.error(ValueUtil.toString(e));
//			throw new RuntimeException(e);
		}

	}

	@Override
	public String getUserId() {
		return this.properties.getProperty(SVN_USER_ID);
	}

	@Override
	public String getUserPassword() {
		return this.properties.getProperty(SVN_USER_PASS);
	}

	@Override
	public String getUrl() {
		return this.properties.getProperty(SVN_URL);
	}

	/**
	 * @return the svnURL
	 */
	public final SVNURL getSvnURL() {
		return svnURL;
	}

	/**
	 * @return the repository
	 */
	public final SVNRepository getRepository() {
		return repository;
	}

	/********************************
	 * 작성일 : 2016. 5. 4. 작성자 : KYJ
	 *
	 *
	 * @return
	 ********************************/
	public Properties getProperties() {
		return properties;
	}

	/********************************
	 * 작성일 : 2016. 5. 4. 작성자 : KYJ
	 *
	 *
	 * @return
	 ********************************/
	public ISVNAuthenticationManager getAuthManager() {
		return authManager;
	}

	public SVNClientManager getSvnManager() {
		return svnManager;
	}

	/**
	 * @return the javaSVNManager
	 */
	public final JavaSVNManager getJavaSVNManager() {
		return javaSVNManager;
	}

}
