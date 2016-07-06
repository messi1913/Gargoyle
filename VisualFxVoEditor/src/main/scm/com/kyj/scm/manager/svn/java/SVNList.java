/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : kyj.Fx.scm.manager.command.svn
 *	작성일   : 2016. 3. 23.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
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

	private static Logger LOGGER = LoggerFactory.getLogger(SVNList.class);

	/**
	 * @param properties
	 */
	public SVNList(Properties properties) {
		super(properties);
	}

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

	/********************************
	 * 작성일 : 2016. 5. 9. 작성자 : KYJ
	 *
	 * 메타정보를 포함하는 SVN 엔트리 반환
	 *
	 * @param path
	 * @param revision
	 * @param exceptionHandler
	 * @return
	 ********************************/
	public List<SVNDirEntry> listEntry(String path, String revision, boolean isRecursive, Consumer<Exception> exceptionHandler) {

		List<SVNDirEntry> resultList = new ArrayList<>();
		try {
			SVNProperties fileProperties = new SVNProperties();
			SVNRepository repository = getRepository();
			long parseLong = Long.parseLong(revision);

			ArrayList<SVNDirEntry> arrayList = new ArrayList<>();
			repository.getDir(path, parseLong, fileProperties, arrayList);

			if (isRecursive) {
				Iterator<SVNDirEntry> iterator = arrayList.iterator();
				while (iterator.hasNext()) {
					SVNDirEntry entry = iterator.next();
					if (entry.getKind() == SVNNodeKind.DIR) {
						SVNURL url = entry.getURL();
						List<SVNDirEntry> listEntry = listEntry(url.getPath(), revision, isRecursive, exceptionHandler);
						resultList.addAll(listEntry);
					}
				}
			}

			resultList.addAll(arrayList);
			return resultList;
		} catch (SVNException e) {
			LOGGER.error(ValueUtil.toString(e));
			if (exceptionHandler != null)
				exceptionHandler.accept(e);
		}

		return resultList;
	}

}
