/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.dimmension
 *	작성일   : 2017. 4. 13.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.dimmension;

import java.io.File;
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
import org.tmatesoft.svn.core.wc.SVNLogClient;

import com.kyj.scm.manager.core.commons.ILogCommand;

/**
 * 
 * Not yet support. 
 * @author KYJ
 *
 */
@Deprecated
class DimLog extends AbstractDimmension implements ILogCommand<String, List<SVNLogEntry>> {

	/**
	 * @param javaSVNManager
	 * @param properties
	 */
	public DimLog(DimmensionManager javaSVNManager, Properties properties) {
		super(javaSVNManager, properties);
	}

	private static Logger LOGGER = LoggerFactory.getLogger(DimLog.class);

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
		throw new RuntimeException("Not yet support");
	}

	public List<SVNLogEntry> log(String path, long startRevision, Date endDate, Consumer<Exception> exceptionHandler) {
		throw new RuntimeException("Not yet support");
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
		throw new RuntimeException("Not yet support");
	}

	public List<SVNLogEntry> logFileSystem(File[] path, Date endDate, Consumer<Exception> exceptionHandler) {
		return logFileSystem(path, 0, endDate, exceptionHandler);
	}

	private void logServer(String path, long startRevision, Date endDate, SVNLogClient logClient, ISVNLogEntryHandler handler)
			throws SVNException {
		throw new RuntimeException("Not yet support");
	}

	private void doLog(File[] path, long startRevision, Date endDate, SVNLogClient logClient, ISVNLogEntryHandler handler)
			throws SVNException {
		throw new RuntimeException("Not yet support");
	}

	public Collection<SVNLogEntry> getAllLogs(String relativePath, long startRevision, long endRevision) throws SVNException {
		throw new RuntimeException("Not yet support");
	}

}
