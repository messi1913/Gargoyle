/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.svn.java
 *	작성일   : 2016. 3. 23.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn.java;

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

import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.scm.manager.core.commons.IListCommand;

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

			repository.getDir(path, parseLong, true, list);

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

}
