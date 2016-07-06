/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2016. 07. 04.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.io.File;
import java.io.FilenameFilter;

import javafx.scene.control.TreeItem;

/**
 * 자바프로젝트 트리Item 처리 기반 Tree
 *
 * @author KYJ
 *
 */
public class FileTreeItem extends TreeItem<FileWrapper> {

	//	private FileWrapper fileWrapper;
	private boolean isJavaProjectFile;
	private boolean isSVNConnected;

	private boolean isLeaf;
	private boolean isFirstTimeChildren = true;
	private boolean isFirstTimeLeaf = true;

	private static final FilenameFilter SVN_WC_DB_FILTER = (FilenameFilter) (dir1, name1) -> "wc.db".equals(name1);
	private static final FilenameFilter IS_CONTAINS_SVN_FILE_FILTER = (FilenameFilter) (dir, name) -> ".svn".equals(name)
			&& isContainsWcDB(dir);

	public FileTreeItem(FileWrapper fileWrapper) {
		super(fileWrapper);
		//		this.fileWrapper = fileWrapper;

		//		isJavaProjectFile = isJavaProjectCondition();
		//		isSVNConnected = isSVNConnected();

	}

	private static boolean isContainsWcDB(File dir) {
		File[] listFiles = dir.listFiles(SVN_WC_DB_FILTER);
		return (listFiles != null && listFiles.length == 1);
	}

	private boolean isSVNConnected() {
		File f = getValue().getFile();
		if (f.isDirectory()) {
			File[] listFiles = f.listFiles(IS_CONTAINS_SVN_FILE_FILTER);
			if (listFiles != null && listFiles.length >= 1) {
				return true;
			}
		}
		return false;
	}

	/********************************
	 * 작성일 : 2016. 7. 4. 작성자 : KYJ
	 * 
	 * 자바 프로젝트가 될 조건을 기술.
	 * 
	 * @return
	 ********************************/
	protected boolean isJavaProjectCondition() {

		File f = getValue().getFile();
		if (f.isDirectory()) {

			File[] listFiles = f.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return ".classPath".equals(name);
				}
			});

			if (listFiles != null && listFiles.length >= 1) {
				isJavaProjectFile = true;
			}

		}
		return isJavaProjectFile;
	}

}