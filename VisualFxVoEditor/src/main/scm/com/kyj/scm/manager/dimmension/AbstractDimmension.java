/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : kyj.Fx.scm.manager.command.svn
 *	작성일   : 2016. 3. 23.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.dimmension;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.scm.manager.core.commons.DimKeywords;
import com.kyj.scm.manager.core.commons.SCMCommonable;
import com.serena.dmclient.api.DimensionsConnection;
import com.serena.dmclient.api.DimensionsConnectionDetails;
import com.serena.dmclient.api.DimensionsConnectionManager;
import com.serena.dmclient.api.DimensionsObjectFactory;
import com.serena.dmclient.api.Project;

import kyj.Fx.dao.wizard.core.util.ValueUtil;

/**
 * SVN에 접속하기 위한 메타정보를 처리한다.
 *
 * @author KYJ
 *
 */
abstract class AbstractDimmension implements SCMCommonable, DimKeywords {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDimmension.class);

	/** [시작] 일반상수 정의 */
	public static final String SVN_REVISION_HISTORY_ROW_COUNT = "15";

	/** [끝] 일반상수 정의 */

	private Properties properties;

	private final DimmensionManager dimManager;
	private DimensionsConnection connection;

	public AbstractDimmension(DimmensionManager dimManager, Properties properties) {
		this.dimManager = dimManager;
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
			DimensionsConnectionDetails details = new DimensionsConnectionDetails();
			if ((getUserId() == null && getUserPassword() == null) || (getUserId().isEmpty() && getUserPassword().isEmpty())) {
				throw new RuntimeException("Properties Infomation is empty.");
			} else {
				details.setUsername(getUserId());
				details.setPassword(getUserPassword());
				details.setDbName(getDbName());
				details.setDbConn(getDbConn());
				details.setServer(getUrl());
			}
			connection = DimensionsConnectionManager.getConnection(details);
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 16.
	 * @return
	 */
	private String getDbConn() {
		return this.properties.getProperty(DIM_DB_CONN);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 16.
	 * @return
	 */
	private String getDbName() {
		return this.properties.getProperty(DIM_DB_NAME);
	}

	@Override
	public String getUserId() {
		return this.properties.getProperty(DIM_USER_ID);
	}

	@Override
	public String getUserPassword() {
		return this.properties.getProperty(DIM_USER_PASS);
	}

	@Override
	public String getUrl() {
		return this.properties.getProperty(DIM_URL);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 16.
	 * @return
	 */
	public final DimensionsObjectFactory getDimmensionObjectFactory() {
		return connection.getObjectFactory();
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

	public DimensionsConnection getConnection(){
		return this.connection;
	}
	/**
	 * @return the javaSVNManager
	 */
	public final DimmensionManager getDimmensionManager() {
		return dimManager;
	}

	public Project getProject(String projSpec) {
		DimensionsObjectFactory fac = getDimmensionObjectFactory();
		Project projObj = fac.getProject(projSpec);
		return projObj;
	}

}
