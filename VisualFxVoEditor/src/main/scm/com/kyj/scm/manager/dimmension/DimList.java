/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.dimmension
 *	작성일   : 2017. 4. 13.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.dimmension;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDirEntry;

import com.kyj.scm.manager.core.commons.IListCommand;
import com.serena.dmclient.api.Project;
import com.serena.dmclient.api.RepositoryFolder;
import com.sun.star.uno.RuntimeException;

/**
 * SVN의 LIST명령어를 수행한다.
 *
 * @author KYJ
 *
 */
class DimList extends AbstractDimmension implements IListCommand<String, List<String>> {

	/**
	 * @param javaSVNManager
	 * @param properties
	 */
	public DimList(DimmensionManager manager, Properties properties) {
		super(manager, properties);
	}

	private static Logger LOGGER = LoggerFactory.getLogger(DimList.class);

	/*
	 * @inheritDoc
	 */
	@Override
	@Deprecated
	public List<String> list(String path) {
		throw new RuntimeException("Not Support.");
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
	public <T> List<T> list(String projSpec, String path, String revision, Function<RepositoryFolder, T> convert,
			Consumer<Exception> exceptionHandler) {

		List<String> linkedList = new LinkedList<String>();
		for (String pathItem : path.split("/")) {
			if (pathItem != null && !pathItem.isEmpty()) {
				linkedList.add(pathItem);
			}
		}

		List<T> resultList = Collections.emptyList();

		Project projObj = getProject(projSpec);

		// now get the top folder and display its children.
		RepositoryFolder folder = projObj.getRootFolder();
		RepositoryFolder childFolder = null;
		if (!resultList.isEmpty()) {
			childFolder = folder.getChildFolder(linkedList.get(0));
			linkedList.remove(0);
			/* 세부상세 메소드에 더 진입하기위해 재귀호출 메소드로 보냄 */
			resultList = listChildFolders(childFolder, linkedList, convert);
		} else {
			resultList = listChildFolders(folder, convert);
		}

		return resultList;
	}

	//RepositoryFolder
	@SuppressWarnings("unchecked")
	private <T> List<T> listChildFolders(final RepositoryFolder parent, Function<RepositoryFolder, T> convert) {
		return ((List<RepositoryFolder>) parent.getImmediateChildFolders()).stream().map(convert).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	private List<RepositoryFolder> listChildFolders(final RepositoryFolder parent) {
		return parent.getImmediateChildFolders();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 16.
	 * @param childFolder
	 * @param linkedList
	 * @param convert
	 * @return
	 */
	private <T> List<T> listChildFolders(RepositoryFolder parent, List<String> linkedList, Function<RepositoryFolder, T> convert) {
		if (linkedList.size() == 0)
			return listChildFolders(parent, convert);

		String lastPathName = linkedList.get(0);
		if (linkedList.size() > 1) {
			RepositoryFolder childFolder = parent.getChildFolder(lastPathName);
			linkedList.remove(0);
			return listChildFolders(childFolder, linkedList, convert);
		} else {
			linkedList.remove(0);
			RepositoryFolder childFolder = parent.getChildFolder(lastPathName);
			return listChildFolders(childFolder, convert);
			//			return childFolder.getImmediateChildFolders();

		}
	}

	/**
	 * <pre>
	 * 처리내용 :	세부상세 메소드에 더 진입함. 재귀호출용
	 * </pre>
	 *
	 * @Method Name : listChildFolders
	 * @param parent
	 * @param linkedList
	 * @param dimPropDVO
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<RepositoryFolder> listChildFolders(final RepositoryFolder parent, List<String> linkedList) {
		if (linkedList.size() == 0)
			return listChildFolders(parent);

		String lastPathName = linkedList.get(0);
		if (linkedList.size() > 1) {
			RepositoryFolder childFolder = parent.getChildFolder(lastPathName);
			linkedList.remove(0);
			return listChildFolders(childFolder, linkedList);
		} else {
			linkedList.remove(0);
			RepositoryFolder childFolder = parent.getChildFolder(lastPathName);
			return childFolder.getImmediateChildFolders();
		}
	}

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
		throw new RuntimeException("Not yet support");
	}

}
