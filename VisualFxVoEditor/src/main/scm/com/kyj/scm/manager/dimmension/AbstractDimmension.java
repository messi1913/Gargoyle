/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.dimmension
 *	작성일   : 2017. 4. 13.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.dimmension;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.scm.manager.core.commons.AbstractScm;
import com.kyj.scm.manager.core.commons.AbstractScmManager;
import com.kyj.scm.manager.core.commons.DimKeywords;
import com.kyj.scm.manager.core.commons.SCMCommonable;
import com.serena.dmclient.api.DimensionsConnection;
import com.serena.dmclient.api.DimensionsConnectionDetails;
import com.serena.dmclient.api.DimensionsConnectionManager;
import com.serena.dmclient.api.DimensionsObjectFactory;
import com.serena.dmclient.api.Project;

/**
 * SVN에 접속하기 위한 메타정보를 처리한다.
 *
 * @author KYJ
 *
 */
abstract class AbstractDimmension extends AbstractScm implements SCMCommonable, DimKeywords {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDimmension.class);

	/** [시작] 일반상수 정의 */
	public static final String SVN_REVISION_HISTORY_ROW_COUNT = "15";

	/** [끝] 일반상수 정의 */

	private AbstractScmManager dimManager;
	private DimensionsConnectionDetails details;

	public AbstractDimmension(AbstractScmManager dimManager, Properties properties) {
		super(properties);
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
		if (getProperties() == null) {
			throw new RuntimeException("properties is null.");
		}

		Object url = getProperties().get(DimKeywords.DIM_URL);
		if (url == null || url.toString().trim().isEmpty())
			throw new RuntimeException("does not exist url");

		Object projectSpec = getProperties().get(DimKeywords.PROJECT_SPEC);
		if (projectSpec == null || projectSpec.toString().trim().isEmpty())
			throw new RuntimeException("does not exist projectSpec");

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 13. 
	 * @return
	 */
	public String getProjSpec() {
		return (String) getProperties().get(DimKeywords.PROJECT_SPEC);
	}

	/**
	 * 접속정보 초기화
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 5. 2.
	 * @param properties
	 */
	public void init(Properties properties) {
		validate();

		if (ValueUtil.isEmpty(properties.getProperty(PROJECT_SPEC))) {
			throw new RuntimeException("Properties Infomation is empty.  [ project spec ]");
		}

		try {
			details = new DimensionsConnectionDetails();

			if ((getUserId() == null && getUserPassword() == null) || (getUserId().isEmpty() && getUserPassword().isEmpty())) {
				throw new RuntimeException("Properties Infomation is empty.  [userId or passwrd ]");
			}

			if ((getDbName() == null || getDbName().isEmpty())) {
				throw new RuntimeException("Properties Infomation is empty.  [ dbName ]");
			}

			if ((getDbName() == null || getDbName().isEmpty())) {
				throw new RuntimeException("Properties Infomation is empty.  [ dbName ]");
			}

			if ((getUrl() == null || getUrl().isEmpty())) {
				throw new RuntimeException("Properties Infomation is empty.  [ server url ]");
			}

			{
				details.setUsername(getUserId());
				details.setPassword(getUserPassword());
				details.setDbName(getDbName());
				details.setDbConn(getDbConn());
				details.setServer(getUrl());
			}

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
		return getProperties().getProperty(DIM_DB_CONN);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 16.
	 * @return
	 */
	private String getDbName() {
		return getProperties().getProperty(DIM_DB_NAME);
	}

	@Override
	public String getUserId() {
		return getProperties().getProperty(DIM_USER_ID);
	}

	@Override
	public String getUserPassword() {
		return getProperties().getProperty(DIM_USER_PASS);
	}

	@Override
	public String getUrl() {
		return getProperties().getProperty(DIM_URL);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 16.
	 * @return
	 */
	public final DimensionsObjectFactory getDimmensionObjectFactory() {
		return getConnection().getObjectFactory();
	}

	public DimensionsConnection getConnection() {
		return DimensionsConnectionManager.getConnection(details);
	}

	public Project getProject(String projSpec) {
		DimensionsObjectFactory fac = getDimmensionObjectFactory();
		Project projObj = fac.getProject(projSpec);
		return projObj;
	}

	public AbstractScmManager getManager() {
		return this.dimManager;
	}

}
