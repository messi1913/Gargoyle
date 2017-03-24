/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.svn.java
 *	작성일   : 2016. 3. 22.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.dimmension;

import java.util.Properties;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.scm.manager.core.commons.ICatCommand;
import com.sun.star.uno.RuntimeException;

/**
 * SVN의 CAT명령어를 수행한다.
 *
 * @author KYJ
 *
 */
class DimCat extends AbstractDimmension implements ICatCommand<String, String> {

	private static Logger LOGGER = LoggerFactory.getLogger(DimCat.class);

	private static final String DEFAULT_ENCODING = "UTF-8";

	/**
	 * @param properties
	 */
	public DimCat(DimmensionManager javaSVNManager, Properties properties) {
		super(javaSVNManager, properties);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String cat(String path) {
		return cat(path, "-1");
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 5. 2.
	 * @param path
	 * @param revision
	 * @return
	 */
	public String cat(String path, String revision) {
		return cat(path, revision, null);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 5. 2.
	 * @param path
	 * @param revision
	 * @param exceptionHandler
	 * @return
	 */
	public String cat(String path, String revision, Consumer<Exception> exceptionHandler) {
		return cat(path, revision, DEFAULT_ENCODING, exceptionHandler);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 13.
	 * @param path
	 * @param revision
	 * @param encoding
	 * @param exceptionHandler
	 * @return
	 */
	public String cat(String path, String revision, String encoding, Consumer<Exception> exceptionHandler) {
		return cat(getProjSpec(), path  , revision , encoding, exceptionHandler);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 3. 14.
	 * @param prjSpec
	 * @param path
	 * @param revision
	 * @param encoding
	 * @param exceptionHandler
	 * @return
	 */
	public String cat(String prjSpec, String path, String revision, String encoding, Consumer<Exception> exceptionHandler) {
		getConnection();
		throw new RuntimeException("Not yet support");
	}
}
