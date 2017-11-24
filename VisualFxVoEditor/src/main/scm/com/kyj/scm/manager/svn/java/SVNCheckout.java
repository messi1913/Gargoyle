/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.svn.java
 *	작성일   : 2016. 3. 23.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.scm.manager.core.commons.ICheckoutCommand;
import com.sun.star.uno.RuntimeException;

/**
 * SVN의 CHECKOUT 명령을 수행한다
 *
 * @author KYJ
 *
 */
class SVNCheckout extends AbstractSVN implements ICheckoutCommand<String, Long> {

	private static Logger LOGGER = LoggerFactory.getLogger(SVNCheckout.class);

	/**
	 * @param javaSVNManager
	 * @param properties
	 */
	public SVNCheckout(JavaSVNManager javaSVNManager, Properties properties) {
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
		long checkoutResult = -1;
		try {
			if (outDir == null || !outDir.exists())
				throw new FileNotFoundException("not support...");

			SVNURL parseURIEncoded = SVNURL.parseURIEncoded(getUrl() + path);
			LOGGER.debug(parseURIEncoded.toString());
			checkoutResult = getSvnManager().getUpdateClient().doCheckout(parseURIEncoded, outDir, SVNRevision.HEAD, SVNRevision.HEAD,
					SVNDepth.INFINITY, false);

		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
			if (exceptionHandler != null)
				exceptionHandler.accept(e);
		}

		return checkoutResult;
	}

}
