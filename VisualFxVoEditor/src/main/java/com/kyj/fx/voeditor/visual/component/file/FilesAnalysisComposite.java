/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.file
 *	작성일   : 2017. 10. 9.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.file;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.framework.thread.ExecutorDemons;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * 
 * 파일 종류별로 분석하여 보여줌.
 * 
 * @author KYJ
 *
 */
@FXMLController(value = "FilesAnalysisComposite.fxml", isSelfController = true, css = "FilesAnalysisComposite.css")
public class FilesAnalysisComposite extends BorderPane {

	private static final Logger LOGGER = LoggerFactory.getLogger(FilesAnalysisComposite.class);
	private File root;
	@FXML
	private TreeView<V> tvFiles;

	/**
	 * @param root
	 */
	public FilesAnalysisComposite(File root) {

		this.root = root;

		FxUtil.loadRoot(FilesAnalysisComposite.class, this, err -> {
			LOGGER.error(ValueUtil.toString(err));
		});

	}

	private StringConverter<V> treeItemStringConverter = new StringConverter<V>() {

		@Override
		public String toString(V object) {
			return String.format("%s (%d)", object.getFileExtension(), object.getItems().size());
		}

		@Override
		public V fromString(String string) {
			return null;
		}
	};

	private Callback<TreeView<V>, TreeCell<V>> treeCellCallback = new Callback<TreeView<V>, TreeCell<V>>() {

		@Override
		public TreeCell<V> call(TreeView<V> param) {
			return new TextFieldTreeCell<>(treeItemStringConverter);
		}
	};

	private EventHandler<WorkerStateEvent> serviceOnSuccessed = new EventHandler<WorkerStateEvent>() {

		@Override
		public void handle(WorkerStateEvent event) {

			Object obj = event.getSource().getValue();

			TreeItem<V> root = new TreeItem<>();
			tvFiles.setRoot(root);
			if (obj != null) {
				Map<String, List<File>> value = (Map<String, List<File>>) obj;

				for (String key : value.keySet()) {
					V v = new V();
					v.setFileExtension(key);
					v.setItems(value.get(key));
					root.getChildren().add(new TreeItem<>(v));
				}
			}
		}
	};

	@FXML
	public void initialize() {

		tvFiles.setCellFactory(treeCellCallback);

		tvFiles.setShowRoot(false);

		FileSearchService service = new FileSearchService(this.root);
		service.setExecutor(ExecutorDemons.getGargoyleSystemExecutorSerivce());
		service.setOnSucceeded(serviceOnSuccessed);
		service.start();

	}

	static class V {
		private String fileExtension;
		private List<File> items;

		public String getFileExtension() {
			return fileExtension;
		}

		public void setFileExtension(String fileExtension) {
			this.fileExtension = fileExtension;
		}

		public List<File> getItems() {
			return items;
		}

		public void setItems(List<File> items) {
			this.items = items;
		}

	}

	static class FileSearchService extends Service<Map<String, List<File>>> {

		private File root;
		private Queue<File> q = new LinkedList<>();
		private Map<String, List<File>> items = new TreeMap<>();

		FileSearchService(File root) {
			this.root = root;

		}

		@Override
		protected Task<Map<String, List<File>>> createTask() {

			return new Task<Map<String, List<File>>>() {

				@Override
				protected Map<String, List<File>> call() throws Exception {
					recursive(root);
					return items;
				}
			};
		}

		private void recursive(File root) {

			q.add(root);

			while (!q.isEmpty()) {

				File poll = q.poll();

				if (poll.isDirectory()) {
					for (File f : poll.listFiles()) {
						q.add(f);
					}
				} else {

					String name = poll.getName();
					String fileExtension = FileUtil.getFileExtension(name);

					if (items.containsKey(fileExtension) || items.containsValue(fileExtension)) {
						items.get(fileExtension).add(poll);
					} else {
						ArrayList<File> value = new ArrayList<>();
						value.add(poll);
						items.put(fileExtension, value);
					}

				}

			}

		}

	}

}
