/**
 * KYJ
 * 2015. 10. 12.
 */
package com.kyj.fx.voeditor.visual.component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.RuntimeClassUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 * 이미파일 처리 기반 Tree
 *
 * @author KYJ
 *
 */
public class ProjectFileTreeItemCreator {

	/**
	 * 열어본 파일 트리(Workspace)에 대한 정보가 Serialize되는 파일 .
	 *
	 * @최초생성일 2016. 7. 18.
	 */
	private static final String OPEND_HISTORY_FILE_NAME = "opendInfo.dat";
	private static Logger LOGGER = LoggerFactory.getLogger(ProjectFileTreeItemCreator.class);
	/**
	 * 파일을 오픈한 대상에 대한 히스토리를 남기고, 프로그램이 종료된후 다시 열었을때, 전의 상태로 되돌리기 위한 변수
	 *
	 * @최초생성일 2016. 3. 16.
	 */
	private static Set<File> cacheExpaned;

	/**
	 * SQLite DbFile. SVN에 대한 메타정보가 담긴 파일.
	 *
	 * @최초생성일 2016. 7. 18.
	 */
	private static final String WCDB_FILE_NAME = "wc.db";
	private static final FilenameFilter SVN_WC_DB_FILTER = (FilenameFilter) (dir1, name1) -> WCDB_FILE_NAME.equals(name1);
	private static final FilenameFilter IS_CONTAINS_SVN_FILE_FILTER = (FilenameFilter) (dir, name) -> ".svn".equals(name)
			&& isContainsWcDB(dir);
	private static final FilenameFilter IS_JAVA_PROJECT_FILTER = (FilenameFilter) (dir, name) -> ".classpath".equals(name);

	static {
		if (!load()) {
			LOGGER.debug("create new opendInfo.dat");
			cacheExpaned = new HashSet<>();
		}
		RuntimeClassUtil.addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(OPEND_HISTORY_FILE_NAME))) {
					out.writeObject(cacheExpaned);
					LOGGER.debug("tmp opend history file success!");
				} catch (Exception e) {
					LOGGER.error(ValueUtil.toString(e));
				}
			}
		}));
	}

	public ProjectFileTreeItemCreator() {

	}

	private static boolean load() {
		File file = new File(OPEND_HISTORY_FILE_NAME);
		if (!file.exists()) {
			return false;
		}
		// object.dat 파일로 부터 객체를 읽어오는 스트림을 생성한다.
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(OPEND_HISTORY_FILE_NAME))) {
			cacheExpaned = (Set<File>) ois.readObject();
			return true;
		} catch (IOException e) {
			LOGGER.error(ValueUtil.toString(e));
		} catch (ClassNotFoundException e) {
			LOGGER.error(ValueUtil.toString(e));
		}
		return false;

	}

	/********************************
	 * 작성일 : 2016. 6. 27. 작성자 : KYJ
	 *
	 *
	 * @param f
	 * @return
	 ********************************/
	public TreeItem<FileWrapper> createNode(final File f) {
		return createDefaultNode(createFileWrapper(f));
	}

	public TreeItem<FileWrapper> createJavaProjectNode(final FileWrapper f) {
		return new JavaProjectFileTreeItem(f) {
			private boolean isLeaf;
			private boolean isFirstTimeChildren = true;
			private boolean isFirstTimeLeaf = true;

			@Override
			public ObservableList<TreeItem<FileWrapper>> getChildren() {
				if (isFirstTimeChildren) {
					isFirstTimeChildren = false;
					super.getChildren().setAll(buildChildren(this, FILE_TREE_TYPE.JAVA_PROJECT));
				}
				return super.getChildren();
			}

			@Override
			public boolean isLeaf() {
				if (isFirstTimeLeaf) {
					isFirstTimeLeaf = false;
					FileWrapper f = (FileWrapper) getValue();
					isLeaf = f.isFile();
				}
				return isLeaf;
			}
		};
	}

	public TreeItem<FileWrapper> createJavaProjectMemberNode(final FileWrapper f) {
		TreeItem<FileWrapper> treeItem =  new JavaProjectMemberFileTreeItem(f) {
			private boolean isLeaf;
			private boolean isFirstTimeChildren = true;
			private boolean isFirstTimeLeaf = true;

			@Override
			public ObservableList<TreeItem<FileWrapper>> getChildren() {
				if (isFirstTimeChildren) {
					isFirstTimeChildren = false;
					super.getChildren().setAll(buildChildren(this, FILE_TREE_TYPE.JAVA_PROJECT_MEMBER));
				}
				return super.getChildren();
			}

			@Override
			public boolean isLeaf() {
				if (isFirstTimeLeaf) {
					isFirstTimeLeaf = false;
					FileWrapper f = (FileWrapper) getValue();
					isLeaf = f.isFile();
				}
				return isLeaf;
			}
		};

		treeItem.expandedProperty().addListener((oba, oldval, newval) -> {
			FileWrapper value = f;
			File file = value.getFile();

			if (newval) {
				cacheExpaned.add(file);
			} else {
				cacheExpaned.remove(file);
			}

		});

		if (cacheExpaned.contains(f.getFile())) {
			treeItem.setExpanded(true);
		}
		return treeItem;
	}

	public TreeItem<FileWrapper> createFileNode(final FileWrapper f) {
		return new FileTreeItem(f) {
			private boolean isLeaf;
			private boolean isFirstTimeChildren = true;
			private boolean isFirstTimeLeaf = true;

			@Override
			public ObservableList<TreeItem<FileWrapper>> getChildren() {
				if (isFirstTimeChildren) {
					isFirstTimeChildren = false;
					super.getChildren().setAll(buildChildren(this, FILE_TREE_TYPE.NOMAL));
				}
				return super.getChildren();
			}

			@Override
			public boolean isLeaf() {
				if (isFirstTimeLeaf) {
					isFirstTimeLeaf = false;
					FileWrapper f = (FileWrapper) getValue();
					isLeaf = f.isFile();
				}
				return isLeaf;
			}

		};
	}

	/**
	 * 파일 트리를 생성하기 위한 노드를 반환한다.
	 *
	 * @Date 2015. 10. 14.
	 * @param f
	 * @return
	 * @User KYJ
	 */
	public TreeItem<FileWrapper> createDefaultNode(final FileWrapper f) {
		TreeItem<FileWrapper> treeItem = null;

		if (f.isJavaProjectFile()) {
			treeItem = createJavaProjectNode(f);
		} else {
			treeItem = createFileNode(f);
		}

		treeItem.expandedProperty().addListener((oba, oldval, newval) -> {
			FileWrapper value = f;
			File file = value.getFile();

			if (newval) {
				cacheExpaned.add(file);
			} else {
				cacheExpaned.remove(file);
			}

		});

		if (cacheExpaned.contains(f.getFile())) {
			treeItem.setExpanded(true);
		}

		return treeItem;
	}

	enum FILE_TREE_TYPE {
		NOMAL, JAVA_PROJECT, JAVA_PROJECT_MEMBER
	}

	private ObservableList<TreeItem<FileWrapper>> buildChildren(TreeItem<FileWrapper> treeItem, FILE_TREE_TYPE type) {

		FileWrapper f = treeItem.getValue();
		boolean isParentSvnConnected = f.isSVNConnected();
		File parentWcDbFile = f.getWcDbFile();
		if (f == null) {
			return FXCollections.emptyObservableList();
		}

		if (f.isFile()) {
			return FXCollections.emptyObservableList();
		}

		File[] files = f.listFiles();
		if (files != null) {
			ObservableList<TreeItem<FileWrapper>> children = FXCollections.observableArrayList();

			switch (type) {
			case NOMAL:

				for (File childFile : files) {
					TreeItem<FileWrapper> createNode = createDefaultNode(createFileWrapper(childFile));
					children.add(createNode);
				}
				break;

			case JAVA_PROJECT:

				for (File childFile : files) {
					TreeItem<FileWrapper> createNode = createJavaProjectMemberNode(createFileWrapper(childFile, fw -> {
						fw.setWcDbFile(parentWcDbFile);
						fw.setSVNConnected(isParentSvnConnected);
					}));
					children.add(createNode);
				}
				break;

			case JAVA_PROJECT_MEMBER:

				for (File childFile : files) {
					TreeItem<FileWrapper> createNode = createJavaProjectMemberNode(createFileWrapper(childFile, fw -> {
						fw.setWcDbFile(parentWcDbFile);
						fw.setSVNConnected(isParentSvnConnected);
					}));
					children.add(createNode);
				}
				break;
			}

			return children;
		}
		return FXCollections.emptyObservableList();
	}

	/********************************
	 * 작성일 : 2016. 6. 27. 작성자 : KYJ
	 *
	 *
	 * @param childFile
	 * @return
	 ********************************/
	//	private FileWrapper createFileWrapper(File childFile) {
	//		return createFileWrapper(childFile, false);
	//	}
	private FileWrapper createFileWrapper(File childFile) {
		return createFileWrapper(childFile, fileWrapper -> operate(fileWrapper));
	}

	//	private FileWrapper createFileWrapper(File childFile, UnaryOperator<FileWrapper> andThanOperate) {
	//		return createFileWrapper(childFile,  andThanOperate);
	//	}

	private FileWrapper createFileWrapper(File childFile, Consumer<FileWrapper> operator) {
		FileWrapper fileWrapper = new FileWrapper(childFile);

		if (operator != null)
			operator.accept(fileWrapper);

		return fileWrapper;
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

	/********************************
	 * 작성일 : 2016. 7. 10. 작성자 : KYJ
	 *
	 * 자바 프로젝트 메타정보를 처리함.
	 *
	 * 현재 처리내용은 svn이 Connected되었는지, javaProject파일인지 여부를 확인함.
	 *
	 * @param fileWrapper
	 ********************************/
	void operate(FileWrapper fileWrapper) {
		File check = fileWrapper.getFile();
		if (check != null && check.isDirectory()) {
			File[] listFiles = check.listFiles();
			for (File file : listFiles) {

				boolean isNotJavaProject = !fileWrapper.isJavaProjectFile();
				boolean isNotSVNConnected = !fileWrapper.isSVNConnected();
				if (isNotJavaProject) {
					if (IS_JAVA_PROJECT_FILTER.accept(file, file.getName())) {
						fileWrapper.setJavaProjectFile(true);
					}
				}

				if (isNotSVNConnected) {
					if (IS_CONTAINS_SVN_FILE_FILTER.accept(file, file.getName())) {
						fileWrapper.setSVNConnected(true);
						fileWrapper.setWcDbFile(new File(file, WCDB_FILE_NAME));
					}
				}

				//				if (isNotJavaProject && isNotSVNConnected)
				//					return;
			}
		}
	}

}