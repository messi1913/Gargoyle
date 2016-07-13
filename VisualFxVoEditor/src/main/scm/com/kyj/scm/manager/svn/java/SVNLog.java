/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : kyj.Fx.scm.manager.command.svn
 *	작성일   : 2016. 3. 23.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn.java;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
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
			logClient.doLog(getSvnURL(), new String[] { path }, SVNRevision.create(-1), SVNRevision.create(0), SVNRevision.HEAD, true,
					false, 10L, handler);

		} catch (SVNException e) {
			LOGGER.error(ValueUtil.toString(e));
			if (exceptionHandler != null)
				exceptionHandler.accept(e);
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

			logClient.doLog(getSvnURL(), new String[] { path }, SVNRevision.create(startRevision), SVNRevision.create(endDate),
					SVNRevision.HEAD, true, false, 1000L, handler);

		} catch (SVNException e) {
			LOGGER.error(ValueUtil.toString(e));
			if (exceptionHandler != null)
				exceptionHandler.accept(e);
		}

		return result;
	}

}
