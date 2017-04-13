/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.dimmension
 *	작성일   : 2017. 4. 13.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.dimmension;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.scm.manager.core.commons.ICheckoutCommand;
import com.sun.star.uno.RuntimeException;

/**
 * SVN의 CHECKOUT 명령을 수행한다
 *
 * @author KYJ
 *
 */
class DimCheckout extends AbstractDimmension implements ICheckoutCommand<String, Long> {

	private static Logger LOGGER = LoggerFactory.getLogger(DimCheckout.class);

	/**
	 * @param javaSVNManager
	 * @param properties
	 */
	public DimCheckout(DimmensionManager javaSVNManager, Properties properties) {
		super(javaSVNManager, properties);
	}

	/*
	 * @inheritDoc
	 */
	@Override
	public Long checkout(String param) {
		throw new RuntimeException("not support...");
	}

	/********************************
	 * 작성일 : 2016. 5. 5. 작성자 : KYJ
	 *
	 *
	 * @param path
	 * @param outDir
	 * @return
	 * @throws FileNotFoundException
	 ********************************/
	public Long checkout(String path, String outDir) throws FileNotFoundException {
		return checkout(path, new File(outDir));
	}

	/*
	 * @inheritDoc
	 */
	@Override
	public Long checkout(String path, File outDir) throws FileNotFoundException {
		return checkout(path, "-1", outDir, null);
	}

	/********************************
	 * 작성일 : 2016. 5. 5. 작성자 : KYJ
	 *
	 *
	 * @param path
	 * @param revision
	 * @param outDir
	 * @param exceptionHandler
	 * @return
	 * @throws FileNotFoundException
	 ********************************/
	public Long checkout(String path, String revision, File outDir, Consumer<Exception> exceptionHandler) throws FileNotFoundException {
		throw new RuntimeException("Not yet support");
	}

}
