/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : kyj.Fx.scm.manager.command.svn.concreate
 *	작성일   : 2016. 3. 22.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn.java;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.io.SVNRepository;

import com.kyj.scm.manager.core.commons.ICatCommand;
import com.kyj.scm.manager.stream.StringOutputStream;
import com.sun.star.uno.RuntimeException;

import kyj.Fx.dao.wizard.core.util.ValueUtil;

/**
 * SVN의 CAT명령어를 수행한다.
 *
 * @author KYJ
 *
 */
class SVNCat extends AbstractSVN implements ICatCommand<String, String> {

	private static Logger LOGGER = LoggerFactory.getLogger(SVNCat.class);

	private static final String DEFAULT_ENCODING = "UTF-8";

	/**
	 * @param properties
	 */
	public SVNCat(Properties properties) {
		super(properties);
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
		String result = "";
		SVNProperties fileProperties = new SVNProperties();
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

			/*
			 * Checks up if the specified path really corresponds to a file. If
			 * doesn't the program exits. SVNNodeKind is that one who says what
			 * is located at a path in a revision. -1 means the latest revision.
			 */

 			SVNRepository repository = getRepository();
			SVNNodeKind nodeKind = repository.checkPath(path, -1);

			if (nodeKind == SVNNodeKind.NONE) {
				if (exceptionHandler != null) {
					exceptionHandler.accept(new RuntimeException("There is no entry at '" + getUrl() + "'."));
				} else
					throw new RuntimeException("There is no entry at '" + getUrl() + "'.");
			} else if (nodeKind == SVNNodeKind.DIR) {
				if (exceptionHandler != null) {
					exceptionHandler
							.accept(new RuntimeException("The entry at '" + getUrl() + "' is a directory while a file was expected."));
				} else
					throw new RuntimeException("The entry at '" + getUrl() + "' is a directory while a file was expected.");
			}

			/*
			 * Gets the contents and properties of the file located at filePath
			 * in the repository at the latest revision (which is meant by a
			 * negative revision number).
			 */
			long parseLong = -1;
			try {
				parseLong = Long.parseLong(revision);
			} catch (NumberFormatException e) {
			}
			repository.getFile(path, parseLong, fileProperties, baos);

			/*
			 * Here the SVNProperty class is used to get the value of the
			 * svn:mime-type property (if any). SVNProperty is used to
			 * facilitate the work with versioned properties.
			 */
			String mimeType = fileProperties.getStringValue(SVNProperty.MIME_TYPE);

			/*
			 * SVNProperty.isTextMimeType(..) method checks up the value of the
			 * mime-type file property and says if the file is a text (true) or
			 * not (false).
			 */
			boolean isTextType = SVNProperty.isTextMimeType(mimeType);

			Iterator<String> iterator = fileProperties.nameSet().iterator();
			/*
			 * Displays file properties.
			 */
			while (iterator.hasNext()) {
				String propertyName = iterator.next();
				String propertyValue = fileProperties.getStringValue(propertyName);
				LOGGER.debug("File property: " + propertyName + "=" + propertyValue);
			}

			/*
			 * Displays the file contents in the console if the file is a text.
			 */
			if (isTextType) {
				try(StringOutputStream out = new StringOutputStream())
				{
					baos.writeTo(out);
					result = baos.toString(encoding); // out.getString();
				}
			} else {
				LOGGER.debug(
						"File contents can not be displayed in the console since the mime-type property says that it's not a kind of a text file.");

			}
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
			if (exceptionHandler != null)
				exceptionHandler.accept(e);
		}
		return result;
	}

}
