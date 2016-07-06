/********************************
 *	프로젝트 : ScmManager
 *	패키지   : kyj.Fx.scm.manager.core
 *	작성일   : 2016. 3. 22.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.SystemUtils;

import com.kyj.fx.voeditor.visual.exceptions.GagoyleParamEmptyException;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.scm.manager.core.commons.SVNKeywords;

/**
 * SVN명령어를 모아놓은 매니저클래스
 *
 * @author KYJ
 *
 */
@Deprecated
public class SVNCommandManager implements SVNKeywords {

	private SVNCatCommand catCommand;

	private SVNListCommand listCommand;

	private SVNLogCommand logCommand;

	private SVNCheckoutCommand checkoutCommand;

	private Properties properties;

	public SVNCommandManager() {
		ResourceLoader instance = ResourceLoader.getInstance();
		String userId = instance.get(SVN_USER_ID);
		String userPass = instance.get(SVN_USER_PASS);

		String osName = SystemUtils.OS_NAME;

		String svnPath = null;

		if (osName.contains("Windows")) {
			svnPath = ConfigResourceLoader.getInstance().get(SVN_PATH_WINDOW);
		}

		Properties properties = new Properties();
		properties.put(SVN_USER_ID, userId);
		properties.put(SVN_USER_PASS, userPass);
		properties.put(SVN_PATH, svnPath);

		init(properties);

	}

	public SVNCommandManager(Properties properties) {
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
		this.catCommand = new SVNCatCommand(properties);
		this.listCommand = new SVNListCommand(properties);
		this.logCommand = new SVNLogCommand(properties);
		this.checkoutCommand = new SVNCheckoutCommand(properties);

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

	public String getUserId() {
		Object objUserId = this.properties.get(SVN_USER_ID);
		if (objUserId == null)
			throw new GagoyleParamEmptyException("SVN URL IS EMPTY.");
		return objUserId.toString();
	}

	/**
	 * svn cat명령어
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 23.
	 * @param param
	 * @return
	 */
	public List<String> cat(String param) {
		return catCommand.cat(param);
	}

	/**
	 * svn cat명령어
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 23.
	 * @param revision
	 * @param param
	 * @return
	 */
	public List<String> cat(String revision, String param) {
		return catCommand.cat(revision, param);
	}

	/**
	 * svn cat명령어
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 23.
	 * @param param
	 * @return
	 */
	public List<String> list(String param) {
		return listCommand.list(param);
	}

	/**
	 * svn 리비젼 정보 조회
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 24.
	 * @param param
	 * @return
	 */
	public List<String> log(String param) {
		return logCommand.log(param);
	}

	/**
	 * 코드 체크아웃
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 24.
	 * @param param
	 * @return
	 */
	public List<String> checkout(String param) {
		return checkoutCommand.checkout(param);
	}

	/**
	 * 코드 체크아웃
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 24.
	 * @param revision
	 * @param outDir
	 * @param param
	 * @return
	 */
	public List<String> checkout(String outDir, String param) {
		return checkoutCommand.checkout(new File(outDir), param);
	}

	/**
	 * 코드 체크아웃
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 24.
	 * @param revision
	 * @param outDir
	 * @param param
	 * @return
	 */
	public List<String> checkout(File outDir, String param) {
		return checkoutCommand.checkout(outDir, param);
	}
}
