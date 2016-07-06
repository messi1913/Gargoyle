/********************************
 *	프로젝트 : fileexplorer
 *	패키지   : kyj.Fx.visual.fileexplorer
 *	작성일   : 2016. 3. 11.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.kyj.fx.voeditor.visual.util.FxClipboardUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;

/**
 * @author KYJ
 *
 */
public class FilePropertiesComposite extends TabPane implements Function<File, List<Map<String, Object>>> {

	private File file;

	@FXML
	private TableView<Map<String, Object>> tbFileProperties;

	/**
	 */
	public FilePropertiesComposite() {
		init();
	}

	/**
	 * @param file
	 */
	public FilePropertiesComposite(File file) {
		this.file = file;
		init();
	}

	void init() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(FilePropertiesComposite.class.getResource("FilePropertiesCompositeView.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void initialize() {
		tbFileProperties.getItems().addAll(apply(this.file));

		tbFileProperties.getSelectionModel().setCellSelectionEnabled(true);
		tbFileProperties.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		FxClipboardUtil.installCopyPasteHandler(tbFileProperties);
	}

	/**********************************************************************************************/
	/* 이벤트 처리항목 기술 */
	// TODO Auto-generated constructor stub
	/**********************************************************************************************/

	/**********************************************************************************************/

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.function.Function#apply(java.lang.Object)
	 */
	@Override
	public List<Map<String, Object>> apply(File file) {
		ObservableList<Map<String, Object>> items = FXCollections.observableArrayList();

		long totalSpace = this.file.getTotalSpace();
		long usableSpace = this.file.getUsableSpace();
		long freeSpace = this.file.getFreeSpace();
		String name = this.file.getName();
		boolean isExecutable = this.file.canExecute();
		boolean isWriable = this.file.canWrite();
		boolean isReadable = this.file.canRead();
		boolean hidden = this.file.isHidden();
		String path = this.file.getAbsolutePath();

		items.add(toMap("name", name));
		items.add(toMap("path", path));

		items.add(toMap("totalSpace(byte)", toNumber(totalSpace)));
		items.add(toMap("usableSpace(byte)", toNumber(usableSpace)));
		items.add(toMap("freeSpace(byte)", toNumber(freeSpace)));

		items.add(toMap("isExecutable", isExecutable));
		items.add(toMap("isWriable", isWriable));
		items.add(toMap("isReadable", isReadable));
		items.add(toMap("hidden", hidden));

		try {

			BasicFileAttributes readAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
			FileTime lastAccessTime = readAttributes.lastAccessTime();
			FileTime creationTime = readAttributes.creationTime();
			FileTime lastModifiedTime = readAttributes.lastModifiedTime();
			boolean symbolicLink = readAttributes.isSymbolicLink();

			items.add(toMap("creationTime", toDate(creationTime.toMillis())));
			items.add(toMap("lastAccessTime", toDate(lastAccessTime.toMillis())));
			items.add(toMap("lastModifiedTime", toDate(lastModifiedTime.toMillis())));
			items.add(toMap("symbolicLink", symbolicLink));

		} catch (IOException e) {
			e.printStackTrace();
		}

		return items;
	}

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String toDate(long dateval) {
		return format.format(new Date(dateval));
	}

	private String toNumber(long number) {
		return String.format("%,d", number);
	}

	protected Map<String, Object> toMap(String key, Object value) {
		Map<String, Object> map = new HashMap<>();
		map.put("key", key);
		map.put("value", value);
		return map;
	}
	/**********************************************************************************************/
}
