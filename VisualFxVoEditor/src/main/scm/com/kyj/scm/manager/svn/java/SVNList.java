/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.svn.java
 *	작성일   : 2016. 3. 23.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn.java;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNClientManager;

import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.scm.manager.core.commons.IListCommand;
import com.kyj.scm.manager.core.commons.ScmDirHandler;

/**
 * SVN의 LIST명령어를 수행한다.
 *
 * @author KYJ
 *
 */
class SVNList extends AbstractSVN implements IListCommand<String, List<String>> {

	/**
	 * @param javaSVNManager
	 * @param properties
	 */
	public SVNList(JavaSVNManager javaSVNManager, Properties properties) {
		super(javaSVNManager, properties);
	}

	private static Logger LOGGER = LoggerFactory.getLogger(SVNList.class);

	/*
	 * @inheritDoc
	 */
	@Override
	public List<String> list(String path) {
		return list(path, "-1", null);
	}

	/********************************
	 * 작성일 : 2016. 5. 4. 작성자 : KYJ
	 *
	 * path에 속하는 구성정보 조회
	 *
	 * @param path
	 * @param revision
	 * @param exceptionHandler
	 * @return
	 ********************************/
	public List<String> list(String path, String revision, Consumer<Exception> exceptionHandler) {

		List<String> resultList = Collections.emptyList();
		try {
			SVNProperties fileProperties = new SVNProperties();
			SVNRepository repository = getRepository();
			Collection<SVNDirEntry> dir = repository.getDir(path, Long.parseLong(revision), fileProperties, new ArrayList<>());

			resultList = dir.stream().map(d -> {
				SVNNodeKind kind = d.getKind();
				if (SVNNodeKind.DIR == kind) {
					return d.getRelativePath().concat("/");
				} else {
					return d.getRelativePath();
				}
			}).collect(Collectors.toList());
		} catch (SVNException e) {
			LOGGER.error(ValueUtil.toString(e));
			if (exceptionHandler != null)
				exceptionHandler.accept(e);
		}
		return resultList;
	}

	//커밋메세지 기능 추가.
	private Consumer<SVNDirEntry> addMessage = v -> {
		if (v.getKind() == SVNNodeKind.FILE) {
			String svnPath = v.getURL().getPath();

			List<SVNLogEntry> log = getJavaSVNManager().log(svnPath, v.getDate(), ex -> LOGGER.error(ValueUtil.toString(ex)));
			if (!log.isEmpty()) {
				SVNLogEntry svnLogEntry = log.get(log.size() - 1);
				v.setCommitMessage(svnLogEntry.getMessage());
			}
		}
	};

	/********************************
	 * 작성일 : 2016. 5. 9. 작성자 : KYJ
	 *
	 * 메타정보를 포함하는 SVN 엔트리 반환
	 *
	 * 2016-11-03 버그 수정
	 *
	 * @param path
	 * @param revision
	 * @param exceptionHandler
	 * @return
	 ********************************/

	public List<SVNDirEntry> listEntry(String path, String revision, boolean isRecursive, Consumer<Exception> exceptionHandler) {
		List<SVNDirEntry> resultList = new LinkedList<>();
		try {
			SVNRepository repository = getRepository();
			long parseLong = Long.parseLong(revision, 10);

			List<SVNDirEntry> list = new ArrayList<>();
			String _path = path;
			try {
				_path = URLDecoder.decode(_path, "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
//			_path = JavaSVNManager.relativePath(getSvnURL().toString(), _path, true);

//			getSvnURL().
			repository.getDir(_path, parseLong, true, list);

			if (isRecursive) {
				Iterator<SVNDirEntry> iterator = list.iterator();
				while (iterator.hasNext()) {
					SVNDirEntry entry = iterator.next();
					if (entry.getKind() == SVNNodeKind.DIR) {
						SVNURL url = entry.getURL();

						List<SVNDirEntry> listEntry = listEntry(url.getPath(), revision, isRecursive, exceptionHandler);
						resultList.addAll(listEntry);
					} else {
						resultList.add(entry);
					}
				}
			} else {
				resultList.addAll(list);
			}
		} catch (SVNException e) {
			LOGGER.error(ValueUtil.toString(e));
			if (exceptionHandler != null)
				exceptionHandler.accept(e);
		}

		return resultList;
	}

	/**
	 *  메타정보를 포함하는 리소스 탐색.
	 *  데이터를 바로 처리하기위한 handler 처리 .
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 3.
	 * @param relativePath
	 * 			탐색할 상대경로
	 * @param handler
	 * @throws Exception
	 */
	public void listEntry(String relativePath, ScmDirHandler<SVNDirEntry> handler) throws Exception {

		try {
			SVNRepository repository = getRepository();
			SVNURL location = repository.getLocation();

			List<SVNDirEntry> list = new ArrayList<>();

			/*
			 * @param  path                    a directory path
			 * @param  revision                a revision number
			 * @param  includeCommitMessages   if <span class="javakeyword">true</span> then
			 *                                 dir entries (<b>SVNDirEntry</b> objects) will be supplied
			 *                                 with commit log messages, otherwise not
			 * @param  entries                 a collection that receives fetched dir entries
			*/
			repository.getDir(relativePath /* relativePath */, -1, true,
					list /*fileProperties, (SVNDirEntry.DIRENT_ALL | SVNDirEntry.DIRENT_COMMIT_MESSAGE), handler*/);

			Iterator<SVNDirEntry> iterator = list.iterator();
			while (iterator.hasNext()) {

				SVNDirEntry entry = iterator.next();

				/*
				*  @param protocol       a protocol component
				 * @param userInfo       a user info component
				 * @param host           a host component
				 * @param port           a port number
				 * @param path           a path component
				 * @param uriEncoded     <span class="javakeyword">true</span> if
				*/
				/*
				 * 2016-12-14
				 * VisualSVN과의 호환성을 위해 상대경로를 만드는 URL로직을 추가한다.
				 * Root경로를 확인하려면
				 *
				 * SVNDirEntry클래스의 .getRepositoryRoot()를 확인해보면된다.
				 * by kyj.
				 */

				SVNURL url = SVNURL.create(location.getProtocol(), location.getUserInfo(), location.getHost(), location.getPort(),
						getJavaSVNManager().relativePath(entry.getURL()), true);

				if (entry.getKind() == SVNNodeKind.DIR) {
					if (handler.test(entry)) {
						handler.accept(entry);
						//만들어진 상대경로
						listEntry(url.getPath(), handler);
					}

				} else {

					//만들어진 상대경로
					entry = new SVNDirEntry(url, entry.getRepositoryRoot(), entry.getName(), entry.getKind(), entry.getSize(),
							entry.hasProperties(), entry.getRevision(), entry.getDate(), entry.getAuthor(), entry.getCommitMessage());
					handler.accept(entry);
				}
			}

			//			if (parseLong != -1)
			//				resultList.addAll(list.stream().filter(v -> parseLong <= v.getRevision()).collect(Collectors.toList()));
			//			else
			//				resultList.addAll(list);
			//			return resultList;
		} catch (SVNException e) {
			throw e;
		}

	}

	/**
	 * Not Yet Support.
	 * 아직 미구현
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 16.
	 * @param relativePath
	 * @param startRevision
	 * @param isRecursive
	 * @return
	 * @throws SVNException
	 */
	@Deprecated
	public List<SVNLogEntry> listRemoved(String relativePath, long startRevision, boolean isRecursive) throws SVNException {
		SVNClientManager mgr = getSvnManager();
		Collection<SVNLogEntry> allLogs = getJavaSVNManager().getAllLogs(relativePath, startRevision);

		return Collections.emptyList();
	}

}
