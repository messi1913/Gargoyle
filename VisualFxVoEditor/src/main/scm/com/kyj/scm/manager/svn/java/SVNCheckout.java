/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.svn.java
 *	작성일   : 2016. 3. 23.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.util.SVNURLUtil;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.kyj.fx.voeditor.visual.framework.handler.ExceptionHandler;
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
	 * @throws Exception
	 ********************************/
	public Long checkout(String path, String outDir) throws Exception {
		return checkout(path, new File(outDir));
	}

	/*
	 * @inheritDoc
	 */
	@Override
	public Long checkout(String path, File outDir) throws Exception {
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
	 * @throws Exception
	 * @throws FileNotFoundException
	 ********************************/
	public Long checkout(String path, String revision, File outDir, ExceptionHandler exceptionHandler) throws Exception {
		return checkout(path, SVNRevision.HEAD, SVNRevision.create(Long.parseLong(revision)), outDir, exceptionHandler);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 20.
	 * @param path
	 * @param pegrevision
	 * @param revision
	 * @param outDir
	 * @param exceptionHandler
	 * @return
	 * @throws Exception
	 */
	public Long checkout(String path, String pegrevision, String revision, File outDir, ExceptionHandler exceptionHandler)
			throws Exception {
		return checkout(path, SVNRevision.parse(pegrevision), SVNRevision.parse(revision), outDir, exceptionHandler);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 20.
	 * @param path
	 * @param pegrevision
	 * @param revision
	 * @param outDir
	 * @param exceptionHandler
	 * @return
	 * @throws Exception
	 */
	public Long checkout(String path, SVNRevision pegrevision, SVNRevision revision, File outDir, ExceptionHandler exceptionHandler)
			throws Exception {
		SVNURL root = getSvnURL();
		SVNURL pathURL = root;
		if (ValueUtil.isNotEmpty(path)) {
			pathURL = root.appendPath(path, true);
		}
		return checkout(pathURL, pegrevision, revision, outDir, exceptionHandler);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 20.
	 * @param absolutePath
	 * @param pegrevision
	 * @param revision
	 * @param outDir
	 * @param exceptionHandler
	 * @return
	 * @throws Exception
	 */
	public Long checkout(SVNURL absolutePath, SVNRevision pegrevision, SVNRevision revision, File outDir, ExceptionHandler exceptionHandler)
			throws Exception {
		return checkout(absolutePath, pegrevision, revision, SVNDepth.INFINITY, outDir, exceptionHandler);

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 20.
	 * @param absolutePath
	 * @param pegrevision
	 * @param revision
	 * @param depth
	 * @param outDir
	 * @param exceptionHandler
	 * @return
	 * @throws Exception
	 */
	public Long checkout(SVNURL absolutePath, SVNRevision pegrevision, SVNRevision revision, SVNDepth depth, File outDir,
			ExceptionHandler exceptionHandler) throws Exception {
		long checkoutResult = -1;
		try {
			if (outDir == null || !outDir.exists())
				throw new FileNotFoundException("not support...");

			LOGGER.debug(absolutePath.toString());
			SVNClientManager svnManager = getSvnManager();

			checkoutResult = svnManager.getUpdateClient().doCheckout(absolutePath, outDir, pegrevision, revision, depth, false);
		} catch (Exception e) {
			if (exceptionHandler != null)
				exceptionHandler.handle(e);
			else
				throw e;
		}

		return checkoutResult;
	}

	public long getCopy(SVNURL absolutePath, long revision, SVNProperties properties, OutputStream contents) throws SVNException {
		SVNURL root = getSvnURL();
		String relativePath = SVNURLUtil.getRelativeURL(root, absolutePath, true).toString();

		return getCopy(relativePath, revision, properties, contents);
	}

	public long getCopy(String relativePath, long revision, SVNProperties properties, OutputStream contents) throws SVNException {

		// URL 검증
		if (relativePath.isEmpty() || relativePath.endsWith("/"))
			throw new SVNException(SVNErrorMessage.create(SVNErrorCode.BAD_URL, "Invalide relativePath : " + relativePath));

		SVNRepository repository = getRepository();

		LOGGER.debug("getCopy : {} ", relativePath);
		SVNNodeKind checkPath = repository.checkPath(relativePath, revision);
		long result = -1;
		if (checkPath == SVNNodeKind.FILE) {
			// return the revision the file has been taken at
			result = repository.getFile(relativePath, revision, properties, contents);

			int lastIndexOf = ValueUtil.lastIndexOf(relativePath, '/');
			String fileName = relativePath.substring(lastIndexOf) + 1;

			LOGGER.debug(fileName);
		}

		return result;
	}

	public File getCopy(SVNURL absolutePath, long revision, SVNProperties properties, File outputFile)
			throws SVNException, FileNotFoundException, IOException {
		SVNURL root = getSvnURL();
		String relativePath = SVNURLUtil.getRelativeURL(root, absolutePath, true).toString();
		return getCopy(relativePath, revision, properties, outputFile);
	}

	/**
	 * 참조할만한 사이트
	 * https://www.programcreek.com/java-api-examples/index.php?api=org.tmatesoft.svn.core.wc.SVNCopySource
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 20.
	 * @param relativePath
	 * @param revision
	 * @param outputFile
	 * @param properties
	 * @return
	 * @throws SVNException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public File getCopy(String relativePath, long revision, SVNProperties properties, File outputFile)
			throws SVNException, FileNotFoundException, IOException {

		// URL 검증
		if (relativePath.isEmpty() || relativePath.endsWith("/"))
			throw new SVNException(SVNErrorMessage.create(SVNErrorCode.BAD_URL, "Invalide relativePath : " + relativePath));

		SVNRepository repository = getRepository();

		LOGGER.debug("getCopy : {} ", relativePath);
		SVNNodeKind checkPath = repository.checkPath(relativePath, revision);
		if (checkPath == SVNNodeKind.FILE) {

			try (FileOutputStream fos = new FileOutputStream(outputFile)) {
				// return the revision the file has been taken at
				long file = repository.getFile(relativePath, revision, properties, fos);
				if (file == -1)
					return null;
			}
		}

		return outputFile;
	}
}
