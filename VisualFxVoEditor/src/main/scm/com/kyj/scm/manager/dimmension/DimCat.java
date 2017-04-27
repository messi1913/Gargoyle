/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.dimmension
 *	작성일   : 2017. 4. 13.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.dimmension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.functions.LoadFileOptionHandler;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.scm.manager.core.commons.AbstractScmManager;
import com.kyj.scm.manager.core.commons.ICatCommand;
import com.serena.dmclient.api.DimensionsConnection;
import com.serena.dmclient.api.DimensionsResult;
import com.serena.dmclient.api.ItemRevision;
import com.serena.dmclient.api.Project;
import com.serena.dmclient.api.RepositoryFolder;
import com.serena.dmclient.api.SystemAttributes;

/**
 * 형상소스의 본문내용을 읽어온다.
 *
 * @author KYJ
 *
 */
class DimCat extends AbstractDimmension implements ICatCommand<String, String> {

	private static Logger LOGGER = LoggerFactory.getLogger(DimCat.class);

	private static final String DEFAULT_ENCODING = "UTF-8";

	private DimmensionManager manager;

	public DimCat(DimmensionManager manager, Properties properties) {
		super(manager, properties);
		this.manager = manager;
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
	 * 파일명에 리비전 번호를 붙여 읽는도중 다른 리비전 읽기 요청이 온 경우 대비할 수 있게한다.
	 * 
	 * 
	 * 2017.4.27
	 * 파일명에 리비전 번호를 붙임.
	 * 파일명에 리비전 번호를 붙여 읽는도중 다른 리비전 읽기 요청이 온 경우 대비할 수 있게한다.
	 * 
	 * 2017.4.13 kyj.
	 * 한가지 우려되는 부분은 
	 * 읽고 있는도중에 쓰거나 
	 * 쓰는도중에 읽는경우 어떻게 반응할것인가 하는문제.
	 * 
	 * 
	 * ps 디멘전 코드는 까볼수없으니 참..
	 * 
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 3. 14.
	 * @param prjSpec
	 * @param fullPathName
	 * @param revision
	 * @param encoding
	 * @param exceptionHandler
	 * @return
	 * @throws Exception 
	 */
	public File copy(String prjSpec, String fullPathName, String revision, String encoding) {

		List<String> linkedList = new LinkedList<String>();
		for (String pathItem : fullPathName.split("/")) {
			if (pathItem != null && !pathItem.isEmpty()) {
				linkedList.add(pathItem);
			}
		}

		//저장 디렉토리 위치
		File root = tmpDir();
		DimensionsConnection conn = null;
		try {
			conn = getConnection();
			Project project = getProject(conn, prjSpec);

			RepositoryFolder rootFolder = project.getRootFolder();

			ItemRevision ir = manager.searchFindOne(rootFolder, linkedList, revision);//searchFindOne(rootFolder, linkedList, revision);
			if (ir == null)
				return null;

			//반드시 호출.
			ir.queryAttribute(new int[] { SystemAttributes.ITEMFILE_FILENAME, SystemAttributes.ITEMFILE_DIR,
					SystemAttributes.LAST_UPDATED_DATE, SystemAttributes.UTC_MODIFIED_DATE, SystemAttributes.REVISION });

			String pathName = ir.getAttribute(SystemAttributes.ITEMFILE_DIR).toString();
			String name = ir.getAttribute(SystemAttributes.ITEMFILE_FILENAME).toString();
			String itemRevision = ir.getAttribute(SystemAttributes.REVISION).toString();

			if (!root.exists()) {
				try {
					FileUtil.mkDirs(root);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			Path path = Paths.get(root.getAbsolutePath(), pathName, name.concat("_").concat(itemRevision));
			//			File tmp = new File(root.getAbsolutePath(), name.concat("_").concat(newRevision));
			String saveFilePathName = path.toString();//new File(tmp, pathName)  //root.getAbsolutePath() + "/";

			/* 
			 * java.lang.String destinationFileName, 
			 * boolean expandSubstitutionVariables, 
			 * boolean overwriteWritableFiles, 
			 * boolean applySystemTime
			 * 
			 * 첫번째 String : 저장할 파일명  
			 * 두번째 boolean : ? 치환변수?           ->
			 * 세번째 boolean : 덮어씌울지 여부        -> false - 이미존재하면 덮어쓰지않음
			 * 네번째 boolean : 시스템 타임 적용여부 -> false - 원본파일의 날짜 유지 
			 * */
			DimensionsResult copy = ir.getCopy(saveFilePathName, true, false, false);
			//			DimensionsResult copy = ir.getCopyToFolder(saveFilePathName, true, true, false);
			LOGGER.debug("Dimension DOWLNLOAD  START ############################");
			LOGGER.debug("Dimension DOWLNLOAD  File Name :  " + name);
			LOGGER.debug("Dimension DOWLNLOAD  MESSAGE    :  " + copy.getMessage());
			LOGGER.debug("Dimension DOWLNLOAD  ItemRevisionInfo :  " + ir);
			LOGGER.debug("Dimension DOWLNLOAD  END ############################");
		} finally {
			if (conn != null)
				conn.close();
		}

		return new File(root, fullPathName);
	}

	/**
	 * 형상소스가 임시적으로 관리되는 위치를 리턴
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 14. 
	 * @return
	 */
	protected File tmpDir() {
		AbstractScmManager manager = getManager();
		return manager.tmpDir();
	}

}
