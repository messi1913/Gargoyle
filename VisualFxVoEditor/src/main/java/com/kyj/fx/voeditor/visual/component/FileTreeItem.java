/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2016. 07. 04.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URISyntaxException;

import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * 자바프로젝트 트리Item 처리 기반 Tree
 *
 * @author KYJ
 *
 */
public class FileTreeItem extends TreeItem<FileWrapper> {

	private static final FilenameFilter SVN_WC_DB_FILTER = (FilenameFilter) (dir1, name1) -> "wc.db".equals(name1);
	private static final FilenameFilter IS_CONTAINS_SVN_FILE_FILTER = (FilenameFilter) (dir, name) -> ".svn".equals(name)
			&& isContainsWcDB(dir);
	private static final FilenameFilter IS_JAVA_PROJECT_FILTER = (FilenameFilter) (dir, name) -> ".classpath".equals(name);

	public FileTreeItem(FileWrapper fileWrapper) {
		super(fileWrapper);
		//		this.fileWrapper = fileWrapper;

		operate(fileWrapper);
		updateGraphics(fileWrapper);
		//		isJavaProjectFile = isJavaProjectCondition();
		//		isSVNConnected = isSVNConnected();

	}

	/********************************
	 * 작성일 : 2016. 7. 10. 작성자 : KYJ
	 *
	 * 자바 프로젝트 메타정보를 처리함.
	 * 
	 * @param fileWrapper
	 ********************************/
	private void operate(FileWrapper fileWrapper) {
		File check = fileWrapper.getFile();
		if (check != null && check.isDirectory()) {
			File[] listFiles = check.listFiles();
			for (File file : listFiles) {

				boolean isJavaProject = !fileWrapper.isJavaProjectFile();
				boolean isSVNConnected = fileWrapper.isSVNConnected();
				if (isJavaProject) {
					if (IS_JAVA_PROJECT_FILTER.accept(file, file.getName())) {
						fileWrapper.setJavaProjectFile(true);
					}
				}

				if (!isSVNConnected) {
					if (IS_CONTAINS_SVN_FILE_FILTER.accept(file, file.getName())) {
						fileWrapper.setSVNConnected(true);
					}
				}

				if (isJavaProject && isSVNConnected)
					return;
			}
		}
	}

	/********************************
	 * 작성일 : 2016. 7. 10. 작성자 : KYJ
	 *
	 * UI에 보여질 Dispay 처리.
	 * 
	 * @param fileWrapper
	 ********************************/
	private void updateGraphics(FileWrapper fileWrapper) {

		String meta = String.format("[%b][%b]", fileWrapper.isJavaProjectFile(), fileWrapper.isSVNConnected());
		ImageView createImageView = null;

		InputStream imageStream = null;
		if (fileWrapper.isJavaProjectFile()) {
			//project image.
			imageStream = ClassLoader.getSystemClassLoader().getResourceAsStream("META-INF/images/eclipse/project.gif");
			createImageView = FxUtil.createImageView(imageStream);
		} else {
			createImageView = FxUtil.createImageIconView(fileWrapper.getFile());
		}

		HBox value = new HBox(createImageView, new Label(fileWrapper.getFile().getName()), new Label(meta));
		value.getStyleClass().add("fiile-tree-item");

		setGraphic(value);

	}

	/********************************
	 * 작성일 : 2016. 7. 10. 작성자 : KYJ
	 *
	 * .svn파일안에는 wc.db라는 파일이 존재함.
	 * 
	 * @param dir
	 * @return
	 ********************************/
	private static boolean isContainsWcDB(File dir) {
		File[] listFiles = dir.listFiles(SVN_WC_DB_FILTER);
		return (listFiles != null && listFiles.length == 1);
	}

	//	private boolean isSVNConnected() {
	//		File f = getValue().getFile();
	//		if (f.isDirectory()) {
	//			File[] listFiles = f.listFiles(IS_CONTAINS_SVN_FILE_FILTER);
	//			if (listFiles != null && listFiles.length >= 1) {
	//				return true;
	//			}
	//		}
	//		return false;
	//	}

	/********************************
	 * 작성일 : 2016. 7. 4. 작성자 : KYJ
	 * 
	 * 자바 프로젝트가 될 조건을 기술.
	 * 
	 * @return
	 ********************************/
	//	protected boolean isJavaProjectCondition() {
	//
	//		File f = getValue().getFile();
	//		if (f.isDirectory()) {
	//
	//			File[] listFiles = f.listFiles(IS_JAVA_PROJECT_FILTER);
	//
	//			if (listFiles != null && listFiles.length >= 1) {
	//				isJavaProjectFile = true;
	//			}
	//
	//		}
	//		return isJavaProjectFile;
	//	}

}