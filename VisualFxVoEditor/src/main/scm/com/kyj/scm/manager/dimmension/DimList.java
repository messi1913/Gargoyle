/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.dimmension
 *	작성일   : 2017. 4. 13.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.dimmension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.scm.manager.core.commons.IListCommand;
import com.kyj.scm.manager.core.commons.ScmDirHandler;
import com.serena.dmclient.api.DimensionsConnection;
import com.serena.dmclient.api.DimensionsObjectFactory;
import com.serena.dmclient.api.DimensionsRelatedObject;
import com.serena.dmclient.api.Filter;
import com.serena.dmclient.api.ItemRevision;
import com.serena.dmclient.api.Project;
import com.serena.dmclient.api.RepositoryFolder;
import com.serena.dmclient.api.SystemAttributes;

/**
 * SVN의 LIST명령어를 수행한다.
 *
 * @author KYJ
 *
 */
class DimList extends AbstractDimmension implements IListCommand<String, List<String>> {

	private static Logger LOGGER = LoggerFactory.getLogger(DimList.class);
	private DimmensionManager manager;

	/**
	 * @param javaSVNManager
	 * @param properties
	 */
	public DimList(DimmensionManager manager, Properties properties) {
		super(manager, properties);
		this.manager = manager;
	}

	/**
	 * 에러 로그 핸들러
	 * @최초생성일 2017. 4. 24.
	 */
	static final Consumer<Exception> DEFAULT_LOGGER = err -> {
		LOGGER.error(ValueUtil.toString(err));
	};

	/**
	 * toString 기본처리 .
	 * 파일명만 처리.
	 * @최초생성일 2017. 4. 24.
	 */
	static Function<ItemRevision, String> DEFAULT_ITEMREVISION_TOSTRING = r -> {
		r.queryAttribute(SystemAttributes.ITEMFILE_FILENAME);
		Object fileName = r.getAttribute(SystemAttributes.ITEMFILE_FILENAME);
		return fileName.toString();
	};

	/* 
	 * @inheritDoc
	 * 디렉토리를 제외한 모든 파일 정보를 리턴.
	 */
	@Override
	public List<String> list(String path) {
		return list(getProjSpec(), path);
	}

	/* 
	 * @inheritDoc
	 * 디렉토리를 제외한 모든 파일 정보를 리턴.
	 */
	public List<String> list(String projSpec, String path) {
		List<String> collections = Collections.emptyList();

		try {
			DimensionsConnection conn = getConnection();
			Project projObj = getProject(conn, projSpec);

			RepositoryFolder folder = projObj.findRepositoryFolderByPath(path);

			collections = toString(Arrays.asList(folder), DEFAULT_ITEMREVISION_TOSTRING);
		} catch (Exception e) {
			DEFAULT_LOGGER.accept(e);
		}
		return collections;
	}

	/**
	 * 변환 처리.
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 24. 
	 * @param revisions
	 * @param convert
	 * @return
	 */
	<T> List<T> to(List<ItemRevision> revisions, Function<ItemRevision, T> convert) {
		return revisions.stream().map(convert).collect(Collectors.toList());
	}

	/**
	 * 변환처리
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 24. 
	 * @param r
	 * @param convert
	 * @return
	 */
	<T> T to(ItemRevision r, Function<ItemRevision, T> convert) {
		return convert.apply(r);
	}

	/**
	 * path에 속하는 하위 구성정보 조회
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 24. 
	 * @param projSpec
	 * @param path
	 * @param fileName
	 * @param convert
	 * @return
	 */
	public <T> List<T> list(String projSpec, String path, String fileName, Function<ItemRevision, T> convert) {
		return list(projSpec, path, fileName, "-1", convert, DEFAULT_LOGGER);
	}

	/**
	 * path에 속하는 하위 구성정보 조회
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 24. 
	 * @param projSpec
	 * @param path
	 * @param fileName
	 * @param revision
	 * @param convert
	 * @return
	 */
	public <T> List<T> list(String projSpec, String path, String fileName, String revision, Function<ItemRevision, T> convert) {
		return list(projSpec, path, fileName, revision, convert, DEFAULT_LOGGER);
	}

	/********************************
	 * 작성일 : 2017. 4. 24. 작성자 : KYJ
	 *
	 * path에 속하는 하위 구성정보 조회
	 *
	 * @param path
	 * @param revision
	 * @param exceptionHandler
	 * @return
	 ********************************/
	public <T> List<T> list(String projSpec, String path, String fileName, String revision, Function<ItemRevision, T> convert,
			Consumer<Exception> exceptionHandler) {

		List<T> collections = Collections.emptyList();
		DimensionsConnection conn = null;
		try {
			conn = getConnection();
			Project projObj = getProject(conn, projSpec);

			RepositoryFolder findRepositoryFolderByPath = projObj.findRepositoryFolderByPath(path);

			Filter filter = new Filter();

			if (ValueUtil.isNotEmpty(fileName))
				filter.criteria().add(new Filter.Criterion(SystemAttributes.ITEMFILE_FILENAME, fileName, Filter.Criterion.EQUALS));

			if (ValueUtil.equals("-1", revision)) {
				filter.criteria().add(new Filter.Criterion(SystemAttributes.IS_LATEST_REV, "Y", 0));
			} else {
				filter.criteria().add(new Filter.Criterion(SystemAttributes.REVISION, revision, Filter.Criterion.EQUALS));
			}

			List allChildFolders = findRepositoryFolderByPath.getAllChildFolders();
			List<DimensionsRelatedObject> childItems = findRepositoryFolderByPath.getChildItems(filter);

			//			Stream.concat(allChildFolders, childItems);
			List<ItemRevision> collect = childItems.stream().map(i -> (ItemRevision) i.getObject()).collect(Collectors.toList());
			collections = collect.stream().map(convert).collect(Collectors.toList());
		} catch (Exception e) {
			exceptionHandler.accept(e);
		} finally {
			manager.close(conn);
		}

		return collections;
	}

	/********************************
	 * 작성일 : 2016. 5. 9. 작성자 : KYJ
	 *
	 * 디멘전 트리 순회
	 *
	 * 2016-11-03 버그 수정
	 *
	 * @param path
	 * @param revision
	 * @param exceptionHandler
	 * @return
	 ********************************/

	public void listEntry(String projSpec, String path, DimDirHandler handler) throws Exception {
		DimensionsConnection conn = null;
		try {
			conn = getConnection();
			DimensionsObjectFactory objectFactory = conn.getObjectFactory();
			Project project = objectFactory.getProject(projSpec);
			RepositoryFolder rootFoler = project.findRepositoryFolderByPath(path);

			listEntry(rootFoler, handler);
		} finally {
			manager.close(conn);
		}
	}

	/**
	 * 디멘전 트리 순회
	 * 
	 * projSpec값으로는 
	 * 초기 properties에 정의한 값으로 세팅됨.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 24. 
	 * @param path
	 * @param handler
	 * @throws Exception
	 */
	public void listEntry(String path, DimDirHandler handler) throws Exception {
		listEntry(getProjSpec(), path, handler);
	}

	/**
	 * 디멘전 트리 순회
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 24. 
	 * @param parent
	 * @param handler
	 * @return
	 * @throws Exception
	 */
	public List<DimensionsRelatedObject> listEntry(RepositoryFolder parent, ScmDirHandler<RepositoryFolder> handler) throws Exception {
		ArrayList<DimensionsRelatedObject> items = new ArrayList<DimensionsRelatedObject>();

		//자기 자신에 대한 정보
		if (handler.test(parent)) {
			handler.accept(parent);
		}

		//모든 하위 디렉토리를 출력한것.
		List<RepositoryFolder> allChildFolders = parent.getAllChildFolders();
		if (allChildFolders.isEmpty()) {
			return items;
		}

		for (RepositoryFolder r : allChildFolders) {

			if (handler.test(r)) {
				handler.accept(r);
				/*
				 * getAllChildFolder()함수 자체가 재귀호출 로직을 처리하지않게끔.
				 * 모든 하위 디렉토리를 출력함.
				 */
				// listEntry(r, handler);  ** 재귀처리하지않는다. 
			}
		}

		return items;

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 24. 
	 * @param allChildFolders
	 * @return
	 */
	private List<String> toString(List<RepositoryFolder> allChildFolders, Function<ItemRevision, String> toString) {
		return allChildFolders.stream().flatMap(folder -> {
			List<ItemRevision> revisions = folder.getLatestItemRevisions();
			List<String> list = to(revisions, toString);
			return list.stream();
		}).collect(Collectors.toList());
	}

}
