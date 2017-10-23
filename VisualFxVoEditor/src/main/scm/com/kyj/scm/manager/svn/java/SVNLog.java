/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.svn.java
 *	작성일   : 2016. 3. 23.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn.java;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.scm.manager.core.commons.ILogCommand;

/**
 * SVN의 이력 정보 조회
 *
 * @author KYJ
 *
 */
class SVNLog extends AbstractSVN implements ILogCommand<String, List<SVNLogEntry>> {

	/**
	 * @param javaSVNManager
	 * @param properties
	 */
	public SVNLog(JavaSVNManager javaSVNManager, Properties properties) {
		super(javaSVNManager, properties);
	}

	private static Logger LOGGER = LoggerFactory.getLogger(SVNLog.class);

	/*
	 * @inheritDoc
	 */
	@Override
	public List<SVNLogEntry> log(String path) {
		return log(path, "-1", null);
	}

	/********************************
	 * 작성일 : 2016. 5. 5. 작성자 : KYJ
	 *
	 * 이력정보 조회
	 *
	 * @param path
	 *            상대경로
	 * @param revision
	 *            리비젼번호
	 * @param exceptionHandler
	 *            에러발생시 처리할 핸들 정의
	 * @return
	 ********************************/
	public List<SVNLogEntry> log(String path, String revision, Consumer<Exception> exceptionHandler) {
		SVNLogClient logClient = getSvnManager().getLogClient();
		List<SVNLogEntry> result = new ArrayList<>();
		try {
			ISVNLogEntryHandler handler = logEntry -> {

				//				System.out.println(logEntry.getChangedPaths());
				LOGGER.debug("rivision :: {} date :: {} author :: {} message :: {} ", logEntry.getRevision(), logEntry.getDate(),
						logEntry.getAuthor(), logEntry.getMessage());

				//				LOGGER.debug("rivision :: " + logEntry.getRevision());
				//				LOGGER.debug("date :: " + logEntry.getDate());
				//				LOGGER.debug("author :: " + logEntry.getAuthor());
				//				LOGGER.debug("message :: " + logEntry.getMessage());
				//				LOGGER.debug("properties :: " + logEntry.getRevisionProperties());
				result.add(logEntry);
			};

			String _path = path;
			try {
				_path = URLDecoder.decode(_path, "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}

//			SVNURL svnURL = getSvnURL();
//			_path = JavaSVNManager.relativePath(svnURL.toString(), _path, true);
			
			
			logClient.doLog(getSvnURL(), new String[] { _path }, SVNRevision.create(Long.parseLong(revision)),
					SVNRevision.create(Long.parseLong(revision) == -1 ? 0 : Long.parseLong(revision)), SVNRevision.HEAD, true, false, 100L,
					handler);

		} catch (SVNException e) {

			if (exceptionHandler != null)
				exceptionHandler.accept(e);
			else
				LOGGER.error(ValueUtil.toString(e));
		}

		return result;
	}

	public List<SVNLogEntry> log(String path, long startRevision, Date endDate, Consumer<Exception> exceptionHandler) {
		SVNLogClient logClient = getSvnManager().getLogClient();
		List<SVNLogEntry> result = new ArrayList<>();
		try {
			ISVNLogEntryHandler handler = logEntry -> {

				//				System.out.println(logEntry.getChangedPaths());
				LOGGER.debug("path :: {}  rivision :: {} date :: {} author :: {} message :: {} ", path, logEntry.getRevision(),
						logEntry.getDate(), logEntry.getAuthor(), logEntry.getMessage());

				//				LOGGER.debug("rivision :: " + logEntry.getRevision());
				//				LOGGER.debug("date :: " + logEntry.getDate());
				//				LOGGER.debug("author :: " + logEntry.getAuthor());
				//				LOGGER.debug("message :: " + logEntry.getMessage());
				//				LOGGER.debug("properties :: " + logEntry.getRevisionProperties());
				result.add(logEntry);
			};

			logServer(path, startRevision, endDate, logClient, handler);

		} catch (SVNException e) {
			LOGGER.error(ValueUtil.toString(e));
			if (exceptionHandler != null)
				exceptionHandler.accept(e);
		}

		return result;
	}

	/********************************
	 * 작성일 : 2016. 7. 13. 작성자 : KYJ
	 *
	 *
	 * @param path
	 * @param startRevision
	 * @param endDate
	 * @param exceptionHandler
	 * @return
	 ********************************/
	public List<SVNLogEntry> logFileSystem(File[] path, long startRevision, Date endDate, Consumer<Exception> exceptionHandler) {
		SVNLogClient logClient = getSvnManager().getLogClient();
		List<SVNLogEntry> result = new ArrayList<>();
		try {
			ISVNLogEntryHandler handler = logEntry -> {
				LOGGER.debug("path :: {}  rivision :: {} date :: {} author :: {} message :: {} ", path, logEntry.getRevision(),
						logEntry.getDate(), logEntry.getAuthor(), logEntry.getMessage());
				result.add(logEntry);
			};

			doLog(path, startRevision, endDate, logClient, handler);

		} catch (SVNException e) {
			LOGGER.error(ValueUtil.toString(e));
			if (exceptionHandler != null)
				exceptionHandler.accept(e);
		}

		return result;
	}

	public List<SVNLogEntry> logFileSystem(File[] path, Date endDate, Consumer<Exception> exceptionHandler) {
		return logFileSystem(path, 0, endDate, exceptionHandler);
	}

	private void logServer(String path, long startRevision, Date endDate, SVNLogClient logClient, ISVNLogEntryHandler handler)
			throws SVNException {
		logClient.doLog(getSvnURL(), new String[] { path }, SVNRevision.create(startRevision), SVNRevision.create(endDate),
				SVNRevision.HEAD, true, false, 1000L, handler);
	}

	private void doLog(File[] path, long startRevision, Date endDate, SVNLogClient logClient, ISVNLogEntryHandler handler)
			throws SVNException {

		/*
		 *   * @param paths
		*            an array of Working Copy paths, should not be <span
		*            class="javakeyword">null</span>
		* @param pegRevision
		*            a revision in which <code>path</code> is first looked up in
		*            the repository
		* @param startRevision
		*            a revision for an operation to start from (including this
		*            revision)
		* @param endRevision
		*            a revision for an operation to stop at (including this
		*            revision)
		* @param stopOnCopy
		*            <span class="javakeyword">true</span> not to cross copies
		*            while traversing history, otherwise copies history will be
		*            also included into processing
		* @param discoverChangedPaths
		*            <span class="javakeyword">true</span> to report of all changed
		*            paths for every revision being processed (those paths will be
		*            available by calling
		*            {@link org.tmatesoft.svn.core.SVNLogEntry#getChangedPaths()})
		* @param limit
		*            a maximum number of log entries to be processed
		* @param handler
		*            a caller's log entry handler
		* @throws SVNException
		*             if one of the following is true:
		*             <ul>
		*             <li>a path is not under version control <li>can not obtain a
		*             URL of a WC path - there's no such entry in the Working Copy
		*             <li><code>paths</code> contain entries that belong to
		*             different repositories
		 */
		logClient.doLog(path, SVNRevision.HEAD, SVNRevision.create(startRevision), SVNRevision.create(endDate), true, false, 1000L,
				handler);
	}

	public Collection<SVNLogEntry> getAllLogs(String relativePath, long startRevision, long endRevision) throws SVNException {

		SVNRepository repository = getRepository();
		@SuppressWarnings("unchecked")
		Collection<SVNLogEntry> logEntries = repository.log(new String[] { relativePath }, null, startRevision, endRevision, true, true);
		return logEntries;
	}

}
