/**
 * KYJ
 * 2015. 10. 12.
 */
package com.kyj.fx.voeditor.visual.component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.FxUtil;
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
public class ImageFileTreeItemCreator {

	private static final String OPEND_HISTORY_FILE_NAME = "opendInfo.dat";
	private static Logger LOGGER = LoggerFactory.getLogger(ImageFileTreeItemCreator.class);
	/**
	 * 파일을 오픈한 대상에 대한 히스토리를 남기고, 프로그램이 종료된후 다시 열었을때, 전의 상태로 되돌리기 위한 변수
	 *
	 * @최초생성일 2016. 3. 16.
	 */
	private static Set<File> cacheExpaned;

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

	public ImageFileTreeItemCreator() {

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
		return createNode(createFileWrapper(f));
	}

	/**
	 * 파일 트리를 생성하기 위한 노드를 반환한다.
	 *
	 * @Date 2015. 10. 14.
	 * @param f
	 * @return
	 * @User KYJ
	 */
	public TreeItem<FileWrapper> createNode(final FileWrapper f) {
		TreeItem<FileWrapper> treeItem = new FileTreeItem(f) {
			private boolean isLeaf;
			private boolean isFirstTimeChildren = true;
			private boolean isFirstTimeLeaf = true;

			@Override
			public ObservableList<TreeItem<FileWrapper>> getChildren() {
				if (isFirstTimeChildren) {
					isFirstTimeChildren = false;
					super.getChildren().setAll(buildChildren(this));
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

			private ObservableList<TreeItem<FileWrapper>> buildChildren(TreeItem<FileWrapper> treeItem) {

				FileWrapper f = treeItem.getValue();
				if (f == null) {
					return FXCollections.emptyObservableList();
				}
				treeItem.setGraphic(FxUtil.createImageView(f.getFile()));

				if (f.isFile()) {
					return FXCollections.emptyObservableList();
				}

				File[] files = f.listFiles();
				if (files != null) {
					ObservableList<TreeItem<FileWrapper>> children = FXCollections.observableArrayList();
					for (File childFile : files) {
						children.add(createNode(createFileWrapper(childFile)));
					}
					return children;
				}
				return FXCollections.emptyObservableList();
			}

		};

		treeItem.expandedProperty().addListener((oba, oldval, newval) -> {

			FileWrapper value = treeItem.getValue();
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

		treeItem.setGraphic(FxUtil.createImageView(f.getFile()));
		return treeItem;
	}

	/********************************
	 * 작성일 : 2016. 6. 27. 작성자 : KYJ
	 *
	 *
	 * @param childFile
	 * @return
	 ********************************/
	protected FileWrapper createFileWrapper(File childFile) {
		return new FileWrapper(childFile);
	}
}