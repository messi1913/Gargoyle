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

import com.kyj.fx.voeditor.visual.component.chart.AttachedTextValuePieChart;
import com.kyj.fx.voeditor.visual.util.FxClipboardUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;

/**
 * @author KYJ
 *
 */
public class FilePropertiesComposite extends TabPane implements Function<File, List<Map<String, Object>>> {

	public static final String TITLE = "File Properties";

	private File file;

	@FXML
	private TableView<Map<String, Object>> tbFileProperties;

	@FXML
	private AttachedTextValuePieChart picChart;

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
		StringConverter<PieChart.Data> labelConverter = new StringConverter<PieChart.Data>() {

			@Override
			public String toString(Data d) {
				String formatedValue = String.format("%s\n%.2f %%", d.getName(), d.getPieValue());
				return formatedValue;
			}

			@Override
			public Data fromString(String string) {
				return null;
			}
		};
		picChart.setLabelConverter(labelConverter);
		picChart.setTooltipConverter(labelConverter);

		tbFileProperties.getItems().addAll(apply(this.file));

		tbFileProperties.getSelectionModel().setCellSelectionEnabled(true);
		tbFileProperties.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		FxUtil.installClipboardKeyEvent(tbFileProperties);
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

		Long totalSpace = this.file.getTotalSpace();
		Long usableSpace = this.file.getUsableSpace();
		Long freeSpace = this.file.getFreeSpace();
		Long fileSize = this.file.length();

		String name = this.file.getName();
		boolean isExecutable = this.file.canExecute();
		boolean isWriable = this.file.canWrite();
		boolean isReadable = this.file.canRead();
		boolean hidden = this.file.isHidden();
		String path = this.file.getAbsolutePath();

		int totalCnt = 0;
		int fileCnt = 0;
		int dirCnt = 0;

		if(file.isDirectory())
		{
			for (File f : file.listFiles()) {

				if (f.isFile())
					fileCnt++;

				else if (f.isDirectory())
					dirCnt++;

				totalCnt++;
			}
		}

		//grid
		items.add(toMap("name", name));
		items.add(toMap("path", path));

		items.add(toMap("totalSpace(byte)", toNumberString(totalSpace)));
		items.add(toMap("usableSpace(byte)", toNumberString(usableSpace)));
		items.add(toMap("freeSpace(byte)", toNumberString(freeSpace)));
		items.add(toMap("fileSize(byte)", toNumberString(fileSize)));

		items.add(toMap("isExecutable", isExecutable));
		items.add(toMap("isWriable", isWriable));
		items.add(toMap("isReadable", isReadable));
		items.add(toMap("hidden", hidden));

		//chart
		ObservableList<Map<String, Object>> sizeItems = FXCollections.observableArrayList();
		sizeItems.add(toMap("totalSpace(byte)", totalSpace.toString()));
		sizeItems.add(toMap("usableSpace(byte)", usableSpace.toString()));
		sizeItems.add(toMap("freeSpace(byte)", freeSpace.toString()));
		sizeItems.add(toMap("fileSize(byte)", fileSize.toString()));
		this.picChart.setData(ChartCreator.convertNewData(totalSpace, sizeItems));

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

		items.add(toMap("all file count", totalCnt));
		items.add(toMap("only file count ", fileCnt));
		items.add(toMap("only dir count", dirCnt));

		return items;
	}

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String toDate(long dateval) {
		return format.format(new Date(dateval));
	}

	private String toNumberString(long number) {
		return String.format("%,d", number);
	}

	protected Map<String, Object> toMap(String key, Object value) {
		Map<String, Object> map = new HashMap<>();
		map.put("key", key);
		map.put("value", value);
		return map;
	}

	/**********************************************************************************************/

	static class ChartCreator {

		static ObservableList<Data> convertNewData(long totalSpace, ObservableList<Map<String, Object>> items) {

			ObservableList<Data> charItmes = FXCollections.observableArrayList();
			for (Map<String, Object> m : items) {

				long value = Long.valueOf(m.get("value").toString(), 10);
				long percent = value * 100 / totalSpace;
				String key = m.get("key").toString().replace("(byte)", "");
				Data e = new Data(key, percent);
				charItmes.add(e);
			}
			return charItmes;
		}
	}
}
