/********************************
 *	프로젝트 : ScmManager
 *	패키지   : kyj.Fx.scm.manager.command.svn
 *	작성일   : 2016. 3. 23.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn;

import java.io.File;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.EncrypUtil;
import com.kyj.scm.manager.core.commons.SCMCommonable;
import com.kyj.scm.manager.core.commons.SVNKeywords;

/**
 * SVN에 접속하기 위한 메타정보를 처리한다.
 *
 * @author KYJ
 *
 */
@Deprecated
public abstract class AbstractSVNCommand implements SCMCommonable, SVNKeywords {

	private static Logger LOGGER = LoggerFactory.getLogger(AbstractSVNCommand.class);

	/** [시작] 일반상수 정의 */
	public static final String SVN_BASE_PATH = "Subversion";
	public static final String WINDOW_SVN_BINARY_PATH = "\\window\\bin\\";
	public static final String SVN_FILE_NAME = "svn.exe";
	public static final String SVN_REVISION_HISTORY_ROW_COUNT = "15";
	public static String SVN_FILE_DIR = SVN_BASE_PATH + WINDOW_SVN_BINARY_PATH + SVN_FILE_NAME;
	public static String OS_NAME = System.getProperty("os.name");
	/** [끝] 일반상수 정의 */

	private Properties properties;

	public AbstractSVNCommand(Properties properties) {
		this.properties = properties;
		if (this.properties.containsValue(SVN_PATH)) {
			SVN_FILE_DIR = this.properties.get(SVN_PATH).toString();
		}
	}

	protected String getSvnPath() {

		// TODO 리눅스에서는 등록되엇다고 가정. 수정가능성 높음.
		if ("Linux".equals(OS_NAME)) {
			return "svn";
		}

		validate();

		return SVN_FILE_DIR;
	}

	/**
	 * svn 커맨드 실행가능여부를 확인한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 24.
	 */
	private void validate() {
		File file = new File(SVN_FILE_DIR);
		if (!file.exists())
			throw new RuntimeException("svn 파일이 이 존재하지않습니다. " + SVN_FILE_DIR);

		if (!file.canExecute()) {
			throw new RuntimeException("svn 실행권한이 없습니다." + SVN_FILE_DIR);
		}
	}

	@Override
	public void init(Properties properties) {
		this.properties = properties;
	}

	@Override
	public String getUserId() {
		return this.properties.getProperty(SVN_USER_ID);
	}

	@Override
	public String getUserPassword() {
		try {
			String property = this.properties.getProperty(SVN_USER_PASS);
			return EncrypUtil.decryp(property);
		} catch (Exception e) {
			LOGGER.warn("fail to decryp..");
		}
		return "";
	}

	@Override
	public String getUrl() {
		return this.properties.getProperty(SVN_URL);
	}

}
