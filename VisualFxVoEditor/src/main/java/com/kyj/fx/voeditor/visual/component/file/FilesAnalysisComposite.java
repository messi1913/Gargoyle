/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.file
 *	작성일   : 2017. 10. 9.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.file;

import java.io.File;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.bar.GargoyleSynchLoadBar;
import com.kyj.fx.voeditor.visual.component.image.FileIconImageView;
import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.framework.thread.ExecutorDemons;
import com.kyj.fx.voeditor.visual.functions.LoadFileOptionHandler;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
	private ObjectProperty<File> root = new SimpleObjectProperty<>();
	@FXML
	private TreeView<V> tvFiles;
	@FXML
	private TextField txtFileLocation, txtNameFilter, txtEncoding;

	@FXML
	private TableView<File> tbFiles;

	@FXML
	private TableColumn<File, String> colFileName;
	@FXML
	private ComboBox<String> cbFilterType;

	private FileSearchService service;

	/**
	 * @param root
	 */
	public FilesAnalysisComposite() {
		this(null);
	}

	public FilesAnalysisComposite(File root) {

		this.root.addListener((oba, o, n) -> {
			if (n != null)
				txtFileLocation.setText(n.getAbsolutePath());
		});

		this.root.set(root);

		FxUtil.loadRoot(FilesAnalysisComposite.class, this, err -> {
			LOGGER.error(ValueUtil.toString(err));
		});
		service = new FileSearchService();
		service.setExecutor(ExecutorDemons.getGargoyleSystemExecutorSerivce());
		service.setOnSucceeded(serviceOnSuccessed);
	}

	@FXML
	public void initialize() {
		tvFiles.setCellFactory(treeCellCallback);

		tvFiles.setShowRoot(false);
		tvFiles.setCache(false);
		txtFileLocation.setEditable(false);
		colFileName.setCellValueFactory(v -> {
			return new SimpleStringProperty(v.getValue().getName());
		});
		tbFiles.setContextMenu(createTbFilesContextMenu());

		FxUtil.installClipboardKeyEvent(tbFiles);
	}

	private ContextMenu createTbFilesContextMenu() {

		MenuItem miShowInSystemExplor = new MenuItem("Show In System Explor");
		miShowInSystemExplor.setOnAction(ev -> {
			File selectedItem = tbFiles.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				FileUtil.browseFile(selectedItem);
			}
		});

		MenuItem miProperties = new MenuItem("Properties");
		miProperties.setOnAction(ev -> {
			File selectedItem = tbFiles.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {

				FxUtil.createStageAndShow(new FilePropertiesComposite(selectedItem), stage -> {
					stage.initOwner(FxUtil.getWindow(FilesAnalysisComposite.this));
					stage.setTitle(selectedItem.getName() + " Properties");
				});
			}
		});
		return new ContextMenu(miShowInSystemExplor, miProperties);
	}

	private StringConverter<V> treeItemStringConverter=new StringConverter<V>(){

	@Override public String toString(V object){return String.format("%s (%d)",object.getFileExtension(),object.getItems().size());}

	@Override public V fromString(String string){return null;}};

	private Callback<TreeView<V>, TreeCell<V>> treeCellCallback=new Callback<TreeView<V>,TreeCell<V>>(){

	@Override public TreeCell<V>call(TreeView<V>param){TextFieldTreeCell<V>textFieldTreeCell=new TextFieldTreeCell<V>(treeItemStringConverter){

	@Override public void updateItem(V item,boolean empty){super.updateItem(item,empty);if(empty){setGraphic(null);}else{File file=item.getItems().get(0);setGraphic(new FileIconImageView(file));}}

	};

	return textFieldTreeCell;}};

	private EventHandler<WorkerStateEvent> serviceOnSuccessed=new EventHandler<WorkerStateEvent>(){

	@Override public void handle(WorkerStateEvent event){

	LOGGER.debug("successed!");Object obj=event.getSource().getValue();

	TreeItem<V>root=new TreeItem<>();tvFiles.setRoot(root);

	if(obj!=null){Map<String,ObservableList<File>>value=(Map<String,ObservableList<File>>)obj;

	for(String key:value.keySet()){V v=new V();v.setFileExtension(key);ObservableList<File>list=value.get(key);v.setItems(list);root.getChildren().add(new TreeItem<>(v));}}service.reset();

	}};

	public void search() {

		if (this.root.get() != null) {

			// if (service.isRunning()) {
			//
			// } else {

			service.setFile(this.root.get());
			// int count = 0;
			// while (State.READY != service.getState()) {
			// if(count > 3) {
			// service.cancel();
			// break;
			// }
			// try {
			// Thread.sleep(1000);
			// count ++;
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			// }

			service.start();
			// }

		}

	}

	@FXML
	public void txtFileLocationOnMouseClick() {

		File dir = DialogUtil.showDirectoryDialog(FxUtil.getWindow(this), chooser -> {
			chooser.setTitle("Select Directory");
		});
		if (dir != null && dir.exists()) {
			this.root.set(dir);
		}
	}

	@FXML
	public void btnSearchOnAction() {
		search();
	}

	@FXML
	public void tvFilesOnClick(MouseEvent e) {
		// TreeItem<V> selectedItem =
		// tvFiles.getSelectionModel().getSelectedItem();
		// if (selectedItem != null) {
		// V value = selectedItem.getValue();
		// if (value != null) {
		// ObservableList<File> items = value.getItems();
		// tbFiles.setItems(items);
		// }
		// }
		nameFilterOnAction();
	}

	@FXML
	public void tbFilesOnMouseClick(MouseEvent e) {
		if (e.getClickCount() == 2) {
			File selectedItem = tbFiles.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				FileUtil.openFile(selectedItem, err -> {
					DialogUtil.showMessageDialog(FxUtil.getWindow(FilesAnalysisComposite.this), err.getMessage());
				});
			}
		}
	}

	@FXML
	public void txtNameFilterOnKeyPress(KeyEvent e) {
		if (e.getCode() == KeyCode.ENTER) {
			nameFilterOnAction();
		}
	}

	@FXML
	public void nameFilterOnAction() {

		String text = txtNameFilter.getText();

		TreeItem<V> selectedItem = tvFiles.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			V value = selectedItem.getValue();
			if (value != null) {
				FilteredList<File> items = new FilteredList<>(value.getItems());
				Predicate<? super File> predicate = null;
				if (text.isEmpty()) {
					predicate = v -> {
						return true;
					};
				} else {
					switch (cbFilterType.getValue()) {
					case "File Name":
						predicate = v -> {
							return v.getName().toUpperCase().contains(text.toUpperCase());
						};

						break;
					case "File Content":
						predicate = v -> {
							LoadFileOptionHandler defaultHandler = LoadFileOptionHandler.getDefaultHandler();
							if (!txtEncoding.getText().trim().isEmpty()) {
								defaultHandler.setEncoding(txtEncoding.getText());
							}
							String readFile = FileUtil.readFile(v, defaultHandler);
							return readFile.contains(text);
						};

						break;
					}
				}

				items.setPredicate(predicate);

				SortedList<File> sortedList = new SortedList<>(items);
				sortedList.comparatorProperty().bind(tbFiles.comparatorProperty());
				tbFiles.setItems(sortedList);

			}
		}

	}

	@FXML
	public void txtFileLocationOnKeyPress(KeyEvent e) {
		if(e.getCode() == KeyCode.ENTER)
			search();
	}

	/**
	 * 트리를 표현하기 위한 객체
	 * 
	 * @author KYJ
	 *
	 */
	static class V {
		private String fileExtension;
		private ObservableList<File> items;

		public String getFileExtension() {
			return fileExtension;
		}

		public void setFileExtension(String fileExtension) {
			this.fileExtension = fileExtension;
		}

		public ObservableList<File> getItems() {
			return items;
		}

		public void setItems(ObservableList<File> items) {
			this.items = items;
		}

	}

	/**
	 * 비동기 파일 찾기 서비스
	 * 
	 * @author KYJ
	 *
	 */
	static class FileSearchService extends Service<Map<String, ObservableList<File>>> {

		private File root;
		private Queue<File> q = new LinkedList<>();
		private Map<String, ObservableList<File>> items = new TreeMap<>();

		FileSearchService() {
		}

		FileSearchService(File root) {
			this.root = root;
		}

		public void setFile(File file) {
			this.root = file;
		}

		@Override
		protected void executeTask(Task<Map<String, ObservableList<File>>> task) {
			GargoyleSynchLoadBar<Map<String, ObservableList<File>>> bar = new GargoyleSynchLoadBar<>(SharedMemory.getPrimaryStage(), task);
			bar.start();
		}

		@Override
		protected Task<Map<String, ObservableList<File>>> createTask() {
			items.clear();
			Task<Map<String, ObservableList<File>>> task2 = new Task<Map<String, ObservableList<File>>>() {

				@Override

				protected Map<String, ObservableList<File>> call() throws Exception {

					recursive(root);

					return items;
				}
			};
			return task2;

		}

		/**
		 * 파일탐색
		 * 
		 * @작성자 : KYJ
		 * @작성일 : 2017. 10. 10.
		 * @param root
		 */
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

					if (items.containsKey(
							fileExtension) /*
											 * || items.containsValue(
											 * fileExtension)
											 */) {
						items.get(fileExtension).add(poll);
					} else {
						ObservableList<File> value = FXCollections.observableArrayList();
						value.add(poll);
						items.put(fileExtension, value);
					}

				}

			}

		}

	}

}
