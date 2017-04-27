/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.dimmension
 *	작성일   : 2017. 4. 13.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.dimmension;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SystemUtils;
import org.tmatesoft.svn.core.SVNException;

import com.serena.dmclient.api.DimensionsConnection;
import com.serena.dmclient.api.DimensionsRelatedObject;
import com.serena.dmclient.api.Filter;
import com.serena.dmclient.api.ItemRevision;
import com.serena.dmclient.api.Project;
import com.serena.dmclient.api.RepositoryFolder;
import com.serena.dmclient.api.SystemAttributes;

/**
 *
 * 디멘전 Resource와 관련된 API 처리 묶음 
 *
 * @author KYJ
 *
 */
class DimResource extends AbstractDimmension {

	/**
	 * @param javaSVNManager
	 * @param properties
	 */
	public DimResource(DimmensionManager manager, Properties properties) {
		super(manager, properties);
	}

	/**
	 * 존재여부 확인
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @param relativePath
	 * @return
	 * @throws SVNException
	 */
	public boolean isExists(String projSpec, String relativePath) throws Exception {
		throw new RuntimeException("Not yet support");
	}

	/**
	 * 저장소에 저장된 최신 리비젼 리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @return
	 * @throws SVNException
	 */

	public long getLatestRevision(String projSpec)  {

		DimensionsConnection connection = null;
		long latestRevision = -1;
		try {
			connection = getConnection();
			latestRevision = getLatestRevision(connection, projSpec);
		} finally {
			close(connection);
		}

		return latestRevision;
	}

	/**
	 * 저장소에 저장된 최신 리비젼 리턴.
	 * 
	 *  -1을 리턴함.
	 *  
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 14. 
	 * @param conn
	 * @param projSpec
	 * @return
	 * @throws SVNException
	 */
	public long getLatestRevision(DimensionsConnection conn, String projSpec) {
		
		/**
		
		Project project = getProject(conn, projSpec);
		project.queryAttribute(SystemAttributes.REVISION);
		//this is null 
		Object attribute = project.getAttribute(SystemAttributes.REVISION);
		
		//this is empty.
		List revisionHistory = project.getRevisionHistory();
		
		
		RepositoryFolder rootFolder = project.getRootFolder();
		List latestItemRevisions = rootFolder.getLatestItemRevisions();
		//this is null
		rootFolder.queryAttribute(SystemAttributes.REVISION);
		//this is null
		Object attribute2 = rootFolder.getAttribute(SystemAttributes.REVISION);
		*/
		
		return -1;
	}

	/**
	 * 날짜에 매치되는 리비젼을 리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @param date
	 * @return
	 * @throws SVNException
	 */
	@Deprecated
	public long getRevision(Date date) throws SVNException {
		throw new RuntimeException("Not yet support");
	}

	/********************************
	 * 작성일 : 2016. 7. 21. 작성자 : KYJ
	 *
	 * SVN 서버 접속 여부 확인
	 *
	 * @throws SVNException
	 ********************************/
	public void ping() throws SVNException {
		//		getConnection().getConnectionState(false);
	}

	/********************************
	 * 작성일 : 2016. 7. 31. 작성자 : KYJ
	 *
	 * SVN 서버 RepositoryUUID를 리턴.
	 *
	 * @return
	 * @throws SVNException
	 ********************************/
	public String getRepositoryUUID() {
		return getConnection().getConnectionDetails().getServer();
	}

	public File tmpDir() {
		return new File(SystemUtils.getJavaIoTmpDir(), "dimmension");
	}

	/**
	 * Root Url
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 13.
	 * @return
	 */
	public String getRootUrl() {
		return getConnection().getConnectionDetails().getServer();
	}

	/***********************************************************************************/
	//리소스 탐색
	public List<ItemRevision> search(RepositoryFolder folder, List<String> linkedList, String revision) {
		return search(folder, linkedList, revision, childItems -> {
			return childItems.stream().map(dm -> {
				return (ItemRevision) dm.getObject();
			}).collect(Collectors.toList());
		});
	}

	public ItemRevision searchFindOne(RepositoryFolder folder, List<String> linkedList, String revision) {
		return search(folder, linkedList, revision, childItems -> {
			Optional<ItemRevision> findFirst = childItems.stream().map(dm -> {
				return (ItemRevision) dm.getObject();
			}).findFirst();
			if (findFirst.isPresent())
				return findFirst.get();
			return null;
		});
	}

	public <K> K search(RepositoryFolder folder, List<String> linkedList, String revision,
			Function<List<DimensionsRelatedObject>, K> convert) {
		K items = null;
		if (linkedList.size() == 0) {
			return items;
		}

		String subPathName = linkedList.get(0);
		if (linkedList.size() > 1) {
			RepositoryFolder childFolder = folder.getChildFolder(subPathName);
			linkedList.remove(0);
			return search(childFolder, linkedList, revision, convert);
		} else {
			Filter filter = new Filter();
			/* 파일명과 동일한것만 가져옴 */
			filter.criteria().add(new Filter.Criterion(SystemAttributes.ITEMFILE_FILENAME, subPathName, Filter.Criterion.EQUALS));

			if ("-1".equals(revision))
				filter.criteria().add(new Filter.Criterion(SystemAttributes.IS_LATEST_REV, "Y", 0)); //$NON-NLS-1$
			else
				filter.criteria().add(new Filter.Criterion(SystemAttributes.REVISION, revision, Filter.Criterion.EQUALS));

			List<DimensionsRelatedObject> childItems = folder.getChildItems(filter);

			return convert.apply(childItems);
		}
	}

	/**
	 * 리소스 존재여부 체크
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 14. 
	 * @param relativePath
	 * @return
	 */
	public boolean isExists(String relativePath) {

		List<String> linkedList = new LinkedList<String>();
		for (String pathItem : relativePath.split("/")) {
			if (pathItem != null && !pathItem.isEmpty()) {
				linkedList.add(pathItem);
			}
		}
		DimensionsConnection connection = null;
		boolean search = false;
		try {
			connection = getConnection();
			Project project = connection.getObjectFactory().getProject(getProjSpec());
			RepositoryFolder rootFolder = project.getRootFolder();
			search = search(rootFolder, linkedList, "-1", items -> {
				return items.isEmpty();
			});
		} finally {
			close(connection);
		}

		return search;
	}

	/**
	 * 리소스 해제
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 14. 
	 * @param conn
	 */
	public void close(DimensionsConnection conn) {
		if (conn != null)
			conn.close();
	}

	/**
	 * 최신 리비전 보기 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 14. 
	 * @return
	 */
	public long getLatestRevision() {
		return getLatestRevision(getProjSpec());
	}

}
