/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.dimmension
 *	작성일   : 2017. 4. 13.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.dimmension;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.functions.LoadFileOptionHandler;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.scm.manager.core.commons.AbstractScmManager;
import com.kyj.scm.manager.core.commons.ICatCommand;
import com.serena.dmclient.api.DimensionsRelatedObject;
import com.serena.dmclient.api.DimensionsResult;
import com.serena.dmclient.api.Filter;
import com.serena.dmclient.api.ItemRevision;
import com.serena.dmclient.api.Project;
import com.serena.dmclient.api.RepositoryFolder;
import com.serena.dmclient.api.SystemAttributes;

/**
 * SVN의 CAT명령어를 수행한다.
 *
 * @author KYJ
 *
 */
class DimCat extends AbstractDimmension implements ICatCommand<String, String> {

	private static Logger LOGGER = LoggerFactory.getLogger(DimCat.class);

	private static final String DEFAULT_ENCODING = "UTF-8";

	public DimCat(DimmensionManager abd, Properties properties) {
		super(abd, properties);
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
		return cat(path, revision, DEFAULT_ENCODING);
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
	public String cat(String path, String revision, String encoding) {
		return cat(getProjSpec(), path, revision, encoding);
	}

	public String cat(String prjSpec, String path, String revision, String encoding) {

		File copy = copy(prjSpec, path, revision, encoding);
		LoadFileOptionHandler defaultHandler = LoadFileOptionHandler.getDefaultHandler();
		defaultHandler.setEncoding(encoding);
		return FileUtil.readFile(copy, defaultHandler);
	}

	/**
	 * 
	 * 파일을 복사후 로컬에 붙어넣기.
	 * 
	 * 
	 * 2017.4.13 kyj.
	 * 한가지 우려되는 부분은 
	 * 읽고 있는도중에 쓰거나 
	 * 쓰는도중에 읽는경우 어떻게 반응할것인가 하는문제.
	 * ps 디멘전 코드는 까볼수없으니 참..
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 3. 14.
	 * @param prjSpec
	 * @param path
	 * @param revision
	 * @param encoding
	 * @param exceptionHandler
	 * @return
	 * @throws Exception 
	 */
	public File copy(String prjSpec, String path, String revision, String encoding) {

		List<String> linkedList = new LinkedList<String>();
		for (String pathItem : path.split("/")) {
			if (pathItem != null && !pathItem.isEmpty()) {
				linkedList.add(pathItem);
			}
		}

		//		String[] split = path.split("/");
		//		String lastName = split[split.length - 1];

		//		DimensionsConnection connection = getConnection();
		Project project = getProject(prjSpec);

		RepositoryFolder rootFolder = project.getRootFolder();
		//		List<ItemRevision> result = search(rootFolder, linkedList, revision);
		ItemRevision ir = searchFindOne(rootFolder, linkedList, revision);
		if (ir == null)
			return null;

		//반드시 호출.
		ir.queryAttribute(new int[] { SystemAttributes.ITEMFILE_FILENAME, SystemAttributes.ITEMFILE_DIR, SystemAttributes.LAST_UPDATED_DATE,
				SystemAttributes.UTC_MODIFIED_DATE });

		String name = ir.getAttribute(SystemAttributes.ITEMFILE_FILENAME).toString();
		//		String parentDirName = ir.getAttribute(SystemAttributes.ITEMFILE_DIR).toString();
		//		String itemRevision = ir.getAttribute(SystemAttributes.REVISION).toString();

		//		String name = ir.getName();

		//저장 디렉토리 위치
		File root = tmpDir();
		if (!root.exists()) {
			try {
				FileUtil.mkDirs(root);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		//		File file = new File(root, path);
		//		if (file.exists()) {
		//
		//			String format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH).format(file.lastModified());
		//			if (format.equals(lastUpdateDate))
		//				return file;
		//
		//		}
		//저장 파일
		//		File saveFile = new File(root, name);

		/* 
		 * java.lang.String destinationFileName, 
		 * boolean expandSubstitutionVariables, 
		 * boolean overwriteWritableFiles, 
		 * boolean applySystemTime
		 * 
		 * 첫번째 String : 저장할 파일명 
		 * 두번째 boolean : ? 치환변수?
		 * 세번째 boolean : 덮어씌울지 여부 
		 * 네번째 boolean : 시스템 타임 적용여부 
		 * */
		DimensionsResult copy = ir.getCopyToFolder(root.getAbsolutePath() + "/", false, true, false);
		LOGGER.debug("Dimension DOWLNLOAD  START ############################");
		LOGGER.debug("Dimension DOWLNLOAD  File Name :  " + name);
		LOGGER.debug("Dimension DOWLNLOAD  MESSAGE    :  " + copy.getMessage());
		LOGGER.debug("Dimension DOWLNLOAD  ItemRevisionInfo :  " + ir);
		LOGGER.debug("Dimension DOWLNLOAD  END ############################");

		return new File(root, path);
	}

	protected File tmpDir() {
		AbstractScmManager manager = getManager();
		return manager.tmpDir();
	}

	private List<ItemRevision> search(RepositoryFolder folder, List<String> linkedList, String revision) {
		return search(folder, linkedList, revision, childItems -> {
			return childItems.stream().map(dm -> {
				return (ItemRevision) dm.getObject();
			}).collect(Collectors.toList());
		});
	}

	private ItemRevision searchFindOne(RepositoryFolder folder, List<String> linkedList, String revision) {
		return search(folder, linkedList, revision, childItems -> {
			Optional<ItemRevision> findFirst = childItems.stream().map(dm -> {
				return (ItemRevision) dm.getObject();
			}).findFirst();
			if (findFirst.isPresent())
				return findFirst.get();
			return null;
		});
	}

	private <K> K search(RepositoryFolder folder, List<String> linkedList, String revision,
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

}
