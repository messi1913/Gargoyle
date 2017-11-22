/**
 * KYJ
 * 2015. 10. 12.
 */
package com.kyj.fx.voeditor.visual.main.layout;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.ImageViewPane;
import com.kyj.fx.voeditor.visual.component.JavaProjectFileTreeItem;
import com.kyj.fx.voeditor.visual.component.JavaProjectFileWrapper;
import com.kyj.fx.voeditor.visual.component.JavaProjectMemberFileTreeItem;
import com.kyj.fx.voeditor.visual.component.PDFImageBasePaneWrapper;
import com.kyj.fx.voeditor.visual.component.ProjectFileTreeItemCreator;
import com.kyj.fx.voeditor.visual.component.ResultDialog;
import com.kyj.fx.voeditor.visual.component.capture.ErdScreenAdapter;
import com.kyj.fx.voeditor.visual.component.console.ReadOnlyConsole;
import com.kyj.fx.voeditor.visual.component.console.ReadOnlySingletonConsole;
import com.kyj.fx.voeditor.visual.component.console.SystemConsole;
import com.kyj.fx.voeditor.visual.component.console.WebViewConsole;
import com.kyj.fx.voeditor.visual.component.dock.tab.DockTab;
import com.kyj.fx.voeditor.visual.component.dock.tab.DockTabPane;
import com.kyj.fx.voeditor.visual.component.file.FilePropertiesComposite;
import com.kyj.fx.voeditor.visual.component.file.FilesAnalysisComposite;
import com.kyj.fx.voeditor.visual.component.google.trend.GoogleTrendComposite;
import com.kyj.fx.voeditor.visual.component.http.HttpActionComposite;
import com.kyj.fx.voeditor.visual.component.image.Base64ImageConvertComposte;
import com.kyj.fx.voeditor.visual.component.mail.MailViewCompositeWrapper;
import com.kyj.fx.voeditor.visual.component.monitor.bci.view.JavaProcessMonitor;
import com.kyj.fx.voeditor.visual.component.nrch.realtime.NrchRealtimeSrchFlowComposite;
import com.kyj.fx.voeditor.visual.component.pmd.DesignerFxComposite;
import com.kyj.fx.voeditor.visual.component.pmd.PMDCheckedListComposite;
import com.kyj.fx.voeditor.visual.component.popup.FXMLTextView;
import com.kyj.fx.voeditor.visual.component.popup.GagoyleWorkspaceOpenResourceView;
import com.kyj.fx.voeditor.visual.component.popup.JavaTextView;
import com.kyj.fx.voeditor.visual.component.popup.SelectWorkspaceView;
import com.kyj.fx.voeditor.visual.component.popup.XMLTextView;
import com.kyj.fx.voeditor.visual.component.popup.ZipFileViewerComposite;
import com.kyj.fx.voeditor.visual.component.proxy.ProxyServerComposite;
import com.kyj.fx.voeditor.visual.component.scm.SVNViewer;
import com.kyj.fx.voeditor.visual.component.sql.view.CommonsSqllPan;
import com.kyj.fx.voeditor.visual.component.text.BigTextView;
import com.kyj.fx.voeditor.visual.component.text.CodeAnalysisJavaTextArea;
import com.kyj.fx.voeditor.visual.component.text.LogViewComposite;
import com.kyj.fx.voeditor.visual.component.text.SimpleTextView;
import com.kyj.fx.voeditor.visual.component.text.XMLEditor;
import com.kyj.fx.voeditor.visual.component.text.XsltTransformComposite;
import com.kyj.fx.voeditor.visual.component.utube.UtubeDownloaderComposite;
import com.kyj.fx.voeditor.visual.exceptions.GargoyleException;
import com.kyj.fx.voeditor.visual.framework.GagoyleParentBeforeLoad;
import com.kyj.fx.voeditor.visual.framework.GagoyleParentOnLoaded;
import com.kyj.fx.voeditor.visual.framework.GargoyleTabPanable;
import com.kyj.fx.voeditor.visual.framework.thread.CloseableCallable;
import com.kyj.fx.voeditor.visual.framework.thread.DemonThreadFactory;
import com.kyj.fx.voeditor.visual.loder.JarWrapper;
import com.kyj.fx.voeditor.visual.loder.PluginLoader;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;
import com.kyj.fx.voeditor.visual.momory.SkinManager;
import com.kyj.fx.voeditor.visual.util.DateUtil;
import com.kyj.fx.voeditor.visual.util.DbExecListener;
import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.GargoyleExtensionFilters;
import com.kyj.fx.voeditor.visual.util.NullExpresion;
import com.kyj.fx.voeditor.visual.util.PMDUtil;
import com.kyj.fx.voeditor.visual.util.RuntimeClassUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.ui.model.SpecResource;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.ui.tabs.SpecTabPane;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.util.ProgramSpecUtil;
import com.kyj.scm.manager.svn.java.JavaSVNManager;
import com.kyj.scm.manager.svn.java.SVNWcDbClient;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;

/**
 * @author KYJ
 *
 */

public class SystemLayoutViewController implements DbExecListener, GagoyleTabLoadable {

	private static Logger LOGGER = LoggerFactory.getLogger(SystemLayoutViewController.class);

	public static final String TAB_TITLE_SPREAD_SHEET = "SpreadSheet";

	private static final String HOME_URL = ResourceLoader.getInstance().get(ResourceLoader.START_URL);

	/**
	 * 쿼리 모니터링화면에 대한 정보
	 *
	 * @최초생성일 2016. 2. 4.
	 */
	private ObjectProperty<ReadOnlyConsole> dbConsoleProperty = new SimpleObjectProperty<>();

	/**
	 * 시스템 콘솔에 대한 정보. 한개의 화면만 띄우기 위한 작업처리이다.
	 *
	 * @최초생성일 2016. 3. 31.
	 */
	private ObjectProperty<ReadOnlyConsole> systemConsoleProperty = new SimpleObjectProperty<>();

	/**
	 * Tab에 Item이 추가된경우 발생하는 이벤트.
	 *
	 * @최초생성일 2016. 6. 16.
	 */
	private ObservableList<GagoyleParentOnLoaded> onParentloaded = FXCollections.observableArrayList();

	/**
	 * Tabdp Item
	 *
	 * @최초생성일 2016. 6. 16.
	 */
	private GagoyleParentBeforeLoad beforeParentLoad = null;
	/**
	 * 마스터 path
	 */
	@FXML
	private BorderPane borderPaneMain;

	/**
	 * 브라우져
	 */
	@FXML
	private WebView webvWelcome;

	/**
	 * URL 텍스트필드
	 */
	@FXML
	private TextField txtUrl;

	@FXML
	private Button btnUrlSearch;

	@FXML
	private MenuItem menuImportProject;

	/**
	 * 파일트리 마스터 노드
	 */
	@FXML
	private TreeView<JavaProjectFileWrapper> treeProjectFile;

	@FXML
	private DockTabPane tabPanWorkspace;

	@FXML
	private VBox accordionItems;
	@FXML
	private Tab tabPackageExplorer;

	private File selectDirFile;
	private JavaProjectFileWrapper tmpSelectFileWrapper = null;

	/**
	 * 플러그인 메뉴가 등록되는 최상위 메뉴
	 *
	 * @최초생성일 2015. 12. 18.
	 */
	@FXML
	private Menu menuPlugins;

	private ContextMenu fileTreeContextMenu;

	@FXML
	public void initialize() {

		// 쿼리 리스너를 등록

		DbUtil.registQuertyListener(this);
		try {
			FXMLLoader loader = FxUtil.createNewFxmlLoader();
			loader.setLocation(SystemLayoutViewController.class.getResource("DAOLoaderView.fxml"));
			TitledPane titledPane = loader.load();
			VBox.setVgrow(titledPane, Priority.ALWAYS);
			accordionItems.getChildren().add(titledPane);
//			DAOLoaderController controller = loader.getController();
//			controller.setSystemLayoutViewController(this);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		SharedMemory.setSystemLayoutView(this);
		SharedMemory.setWorkspaceTab(tabPanWorkspace);

		// tab key에 대한 이벤트 처리 등록....
		SharedMemory.getPrimaryStage().addEventHandler(KeyEvent.ANY, event -> {
			boolean isCloseALLtabKeyCode = event.isControlDown() && event.isShiftDown() && KeyCode.W == event.getCode();

			boolean isTabMoveCode = event.isControlDown() && isNumberCode(event.getCode());
			ObservableList<DockTab> tabs = tabPanWorkspace.getTabs();

			// tab 전부 닫기
			if (isCloseALLtabKeyCode) {
				LOGGER.debug("CLOSE ALL TABS...");

				for (int i = tabs.size() - 1; i > 0; i--) {
					tabs.remove(i);
				}
			}
			// 특정 탭으로 이동하기
			else if (isTabMoveCode) {

				int tabIndex = Integer.parseInt(event.getCode().getName());
				if (tabIndex > 0 && tabs.size() < tabIndex)
					return;

				LOGGER.debug("MOVE TAB" + event.getCode().getName());
				tabPanWorkspace.getSelectionModel().select(tabIndex - 1);
			}
		});

		String baseDir = ResourceLoader.getInstance().get(ResourceLoader.BASE_DIR);
		selectDirFile = new File(baseDir);

		createNewTreeViewMenuItems();
		webvWelcome.getEngine().setJavaScriptEnabled(true);

		webvWelcome.getEngine().load(HOME_URL);
		txtUrl.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if (KeyCode.ENTER == event.getCode()) {
				webvWelcome.getEngine().load(txtUrl.getText());
			}
		});
		btnUrlSearch.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getClickCount() >= 1) {
				webvWelcome.getEngine().load(txtUrl.getText());
			}
		});

		webvWelcome.setOnKeyPressed(key -> {

			if (key.getCode() == KeyCode.F12) {

				FxUtil.createStageAndShow("Simple Web Console", new WebViewConsole(webvWelcome));
			}

		});

		treeProjectFile.setRoot(createNewTree(selectDirFile));
		treeProjectFile.setShowRoot(false);

		// 트리 컨테스트 요청 이벤트
		treeProjectFile.setOnContextMenuRequested(this::treeProjectFileOnContextMenuRequested);
		// 트리 마우스 이벤트
		treeProjectFile.setOnMouseClicked(this::treeProjectFileOnMouseClick);
		// 트리 키 이벤트
		treeProjectFile.addEventHandler(KeyEvent.KEY_PRESSED, this::treeProjectFileOnKeyPressed);

		/*******************************/
		// 17.11.21 KYJ
		/* [시작] 파일경로 드래그 드롭 이벤트 처리 */
		treeProjectFile.setOnDragDetected(this::treeProjectFileOnDragDetected);
		/* [끝] 파일경로 드래그 드롭 이벤트 처리 */

		/** 플러그인들을 로드함. **/
		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				List<JarWrapper> load = PluginLoader.getInstance().load();
				load.stream().forEach(jarWrapper -> {
					try {

						String displayMenuName = jarWrapper.getDisplayMenuName();
						MenuItem pluginMenu = new MenuItem(displayMenuName);
						pluginMenu.setUserData(jarWrapper);
						pluginMenu.setOnAction(event -> {
							JarWrapper jar = (JarWrapper) pluginMenu.getUserData();

							try {
								Class<?> nodeClass = jar.getNodeClass();
								Object newInstance = jar.loader.loadClass(nodeClass.getName()).newInstance();
								// Object newInstance =
								// jar.getNodeClass().newInstance();

								if (newInstance instanceof CloseableParent<?>) {
									loadNewSystemTab(jar.getDisplayMenuName(), (CloseableParent<?>) newInstance);
								} else {
									loadNewSystemTab(jar.getDisplayMenuName(), (Parent) newInstance,
											SkinManager.getInstance().getJavafxDefaultSkin());
								}

							} catch (Exception e) {
								LOGGER.error("regist fail plugin.");
								LOGGER.error(ValueUtil.toString(e));
							}
						});

						try {
							Class<GagoyleParentBeforeLoad> setBeforeParentLoadListenerClass = jarWrapper
									.getSetOnParentBeforeLoadedListenerClass();
							if (setBeforeParentLoadListenerClass != null)
								setOnbeforeParentLoad(setBeforeParentLoadListenerClass.newInstance());
						} catch (Exception e) {
							LOGGER.error("regist fail 'GagoyleParentBeforeLoad' listener.");
						}

						try {
							Class<GagoyleParentOnLoaded> addOnParentLoadedListenerClass = jarWrapper.getAddOnParentLoadedListenerClass();
							if (addOnParentLoadedListenerClass != null)
								addOnParentLoadedListener(addOnParentLoadedListenerClass.newInstance());
						} catch (Exception e) {
							LOGGER.error("regist fail 'GagoyleParentOnLoaded' listener.");
						}

						menuPlugins.getItems().add(pluginMenu);

					} catch (Exception e) {
						LOGGER.debug(ValueUtil.toString(e));
					}
				});

			}

		});

		// tab image 아이콘 처리
		try (InputStream is = getClass().getResourceAsStream("/META-INF/images/eclipse/eview16/packages.gif")) {
			tabPackageExplorer.setGraphic(new ImageView(new Image(is)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		tabPanWorkspace.getTabs().addListener(dockTabCloseListener);
	}

	/**
	 * tab이 닫혔을떄 리소스를 해제하는 리스너
	 * 
	 * @최초생성일 2017. 6. 1.
	 */
	private ListChangeListener<DockTab> dockTabCloseListener = new ListChangeListener<DockTab>() {

		@Override
		public void onChanged(javafx.collections.ListChangeListener.Change<? extends DockTab> c) {
			if (c.next()) {
				List<? extends DockTab> removed = c.getRemoved();
				removed.forEach(tab -> {

					// 만들어진 closeRequest 이벤트 호출
					EventHandler<Event> onCloseRequest = tab.getOnCloseRequest();
					if (onCloseRequest != null)
						onCloseRequest.handle(new ActionEvent());

					Node content = tab.getContent();
					if (content != null && content instanceof Closeable) {
						try {
							((Closeable) content).close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				});
			}
		}

	};

	/**
	 * 파일을 연다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 2. 22.
	 * @param file
	 * @param tabs
	 */
	void openFile(File file) {

		List<String> exts = ConfigResourceLoader.getInstance().getValues(ConfigResourceLoader.FILE_OPEN_NOT_INPROCESSING_EXTENSION, ",");

		String fileName = file.getName();
		int dotIndex = fileName.indexOf('.');
		/* 확장자부분이 없는경우 처리. */
		if (dotIndex == -1) {
			openBigText(file);
			return;
		}

		String EXTENSION = FileUtil.getFileExtension(fileName);
		Optional<String> findFirst = exts.stream().filter(ext -> EXTENSION.equals(ext) || EXTENSION.isEmpty()).findFirst();
		if (findFirst.isPresent()) {
			/* open OS Denpendency. */
			FileUtil.openFile(file);
		} else {

			try {
				if (FileUtil.isJavaFile(file)) {
					openJava(file);
				} else if (FileUtil.isImageFile(file)) {
					openImage(file);
				} else if (FileUtil.isPdfFile(file)) {
					openPdf(file);
				} else if (FileUtil.isFXML(file))
					openFXML(file);
				else if (FileUtil.isXML(file)) {
					openXML(file);
				} else if (FileUtil.isZip(file) || FileUtil.isJar(file)) {
					openZip(file);
				} else {
					openBigText(file);
				}
				/* 예외에 걸린경우 텍스트방식으로 read */
			} catch (Exception e) {
				DialogUtil.showMessageDialog("파일열기에 실패하여 텍스트 형식으로 읽어옵니다.");
				openBigText(file);
			}
		}

	}

	/**
	 * 이미지를 연다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 2. 22.
	 * @param file
	 * @throws Exception
	 */
	private void openImage(File file) throws Exception {
		try {
			loadNewSystemTab(file.getName(), new ImageViewPane(new FileInputStream(file)));
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
			throw e;
		}
	}

	/**
	 * 텍스트기반 파일 형식을 연다.
	 *
	 * 무거운 텍스트를 열때... 멈추는 현상이 있음..
	 *
	 * 무거운 텍스트를 열경우 openBigText(File) 함수를 호출하도록 한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 2. 22.
	 * @param file
	 * @param tabs
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void openText(File file) {
		try {
			String content2 = FileUtils.readFileToString(file);
			SimpleTextView simpleTextView = new SimpleTextView(content2, false);
			simpleTextView.setEditable(false);
			loadNewSystemTab(file.getName(), simpleTextView);
		} catch (IOException e2) {
			LOGGER.debug(ValueUtil.toString(e2));
			DialogUtil.showExceptionDailog(e2);
		}
	}

	/**
	 * 사이즈가 큰 텍스트파일을 페이징방식으로 로드한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 2. 22.
	 * @param file
	 */
	private void openBigText(File file) {

		CloseableParent<BigTextView> closeableParent = new CloseableParent<BigTextView>(new BigTextView(file)) {

			@Override
			public void close() throws IOException {
				getParent().close();
				LOGGER.debug("Closed BigText...");
			}
		};

		loadNewSystemTab(file.getName(), closeableParent);
	}

	/**
	 * PDF파일을 연다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 2. 22.
	 * @param file
	 * @throws Exception
	 */
	private void openPdf(File file) throws Exception {
		try {

			// CloseableParent<PDFImageBasePane> pdfPane = new
			// CloseableParent<PDFImageBasePane>(new PDFImageBasePane(file)) {
			//
			// @Override
			// public void close() throws IOException {
			// LOGGER.debug("Close doc . reuqest ");
			// getParent().close();
			// }
			// };

			PDFImageBasePaneWrapper pane = new PDFImageBasePaneWrapper(file);
			loadNewSystemTab(file.getName(), pane);

		} catch (Exception e) {
			LOGGER.debug(ValueUtil.toString(e));
			throw e;
		}
	}

	/**
	 * FXML 파일을 연다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 16.
	 * @param file
	 * @throws IOException
	 */
	private void openFXML(File file) throws IOException {

		try {

			FXMLTextView fxmlTextView = new FXMLTextView(file, false);
			// fxmlTextView.setEditable(tr);
			loadNewSystemTab(file.getName(), fxmlTextView);
		} catch (IOException e1) {
			LOGGER.debug(ValueUtil.toString(e1));
			throw e1;
		}

	}

	/********************************
	 * 작성일 : 2016. 8. 19. 작성자 : KYJ
	 *
	 * XML 파일을 연다.
	 *
	 * @param file
	 * @throws IOException
	 ********************************/
	private void openXML(File file) throws IOException {
		try {
			XMLTextView fxmlTextView = new XMLTextView(file, false);
			// fxmlTextView.setEditable(tr);
			loadNewSystemTab(file.getName(), fxmlTextView);
		} catch (IOException e1) {
			LOGGER.debug(ValueUtil.toString(e1));
			throw e1;
		}
	}

	/**
	 * 
	 * ZIP 파일을 연다.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 15.
	 * @param file
	 */
	private void openZip(File file) {
		ZipFileViewerComposite view = new ZipFileViewerComposite(file);
		loadNewSystemTab(file.getName(), view);
	}

	/**
	 * 자바파일을 연다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 2. 22.
	 * @param file
	 * @throws IOException
	 */
	private void openJava(File file) throws IOException {
		try {
			String content1 = FileUtils.readFileToString(file, "UTF-8");
			JavaTextView javaTextView = new JavaTextView(content1, false);
			// 2016-10-03 editable 주석 해제.
			// javaTextView.setEditable(false);
			loadNewSystemTab(file.getName(), javaTextView);
		} catch (IOException e1) {
			LOGGER.debug(ValueUtil.toString(e1));
			throw e1;
		}
	}

	/********************************
	 * 작성일 : 2016. 4. 26. 작성자 : KYJ
	 *
	 * 입력된 코드가 숫자인지 아닌리 리턴한다.
	 *
	 * [단추키 기능으로 사용하기 위해 사용]
	 *
	 * @param code
	 * @return
	 ********************************/
	private boolean isNumberCode(KeyCode code) {
		return code.isDigitKey();
	}

	/********************************
	 * 작성일 : 2016. 4. 26. 작성자 : KYJ
	 *
	 * TODO 파일로부터 DAO WIZARD 로드 처리
	 *
	 * @param e
	 ********************************/
	private void daoWizardMenuItemOnActionEvent(ActionEvent e) {
		TreeItem<JavaProjectFileWrapper> selectedItem = treeProjectFile.getSelectionModel().getSelectedItem();
		if (selectedItem == null)
			return;

		JavaProjectFileWrapper value = selectedItem.getValue();
		File file = value.getFile();
		if (file.isFile()) {
			String fileName = file.getName();
			String absoluteFile = file.getAbsolutePath();
			String uLocationPath = ResourceLoader.getInstance().get(ResourceLoader.USER_SELECT_LOCATION_PATH);

			if (uLocationPath.indexOf(absoluteFile) > 0) {

			}

		}
	}

	/**
	 * 트리뷰의 메뉴컨텍스트를 새로 생성ㅎ한다.
	 *
	 * @Date 2015. 10. 17.
	 * @User KYJ
	 */
	private void createNewTreeViewMenuItems() {
		fileTreeContextMenu = new ContextMenu();
		MenuItem openFileMenuItem = new MenuItem("Open");

		Menu menuOpenWidth = new Menu("Open With ");

		/***********************************************************************************/
		// 조건에 따라 보여주는 아이템. [start]
		/***********************************************************************************/
		// FXML선택한경우에만 보여주는 조건처리.
		final MenuItem menuItemOpenWithSceneBuilder = new MenuItem("SceneBuilder");
		final MenuItem menuItemSCMGraphs = new MenuItem("SCM Graphs");
		menuItemSCMGraphs.setDisable(true);
		menuItemSCMGraphs.setOnAction(this::menuItemSCMGraphsOnAction);
		menuItemOpenWithSceneBuilder.setOnAction(this::menuItemOpenWithSceneBuilderOnAction);
		menuOpenWidth.setOnShowing(event -> {

			/* [시작]씬빌더 열기 기능 처리 */
			String sceneBuilderLocation = ResourceLoader.getInstance().get(ResourceLoader.SCENEBUILDER_LOCATION);
			TreeItem<JavaProjectFileWrapper> selectedTreeItem = this.treeProjectFile.getSelectionModel().getSelectedItem();
			boolean isRemoveOpenWidthSceneBuilderMenuItem = true;

			if (selectedTreeItem != null) {
				JavaProjectFileWrapper fileWrapper = selectedTreeItem.getValue();
				File selectedTree = fileWrapper.getFile();
				if (FileUtil.isFXML(selectedTree)) {

					if (!menuOpenWidth.getItems().contains(menuItemOpenWithSceneBuilder)) {
						menuOpenWidth.getItems().add(menuItemOpenWithSceneBuilder);
					}
					isRemoveOpenWidthSceneBuilderMenuItem = false;

					File file = new File(sceneBuilderLocation);
					/* 씬빌더 존재 유무에 따라 활성화 여부를 지정. */
					if (file.exists()) {
						menuItemOpenWithSceneBuilder.setDisable(false);
					} else {
						menuItemOpenWithSceneBuilder.setDisable(true);
					}
				}

			}

			if (isRemoveOpenWidthSceneBuilderMenuItem)
				menuOpenWidth.getItems().remove(menuItemOpenWithSceneBuilder);
			/* [끝]씬빌더 열기 기능 처리 */

		});

		Menu menuRunAs = new Menu("Run As");
		Menu menuPMD = new Menu("PMD");

		fileTreeContextMenu.setOnShowing(ev -> {
			TreeItem<JavaProjectFileWrapper> selectedTreeItem = this.treeProjectFile.getSelectionModel().getSelectedItem();

			/* [시작] SVN Graph 관련 Disable 여부 체크 */
			boolean isDisableSCMGraphsMenuItem = true;
			if (selectedTreeItem != null) {
				menuRunAs.getItems().clear();
				menuPMD.getItems().clear();
				JavaProjectFileWrapper fileWrapper = selectedTreeItem.getValue();
				File file = fileWrapper.getFile();
				if (fileWrapper.isSVNConnected())
					isDisableSCMGraphsMenuItem = false;
				// if(fileWrapper.is)

				if (fileWrapper.isJavaProjectFile()) {
					LOGGER.debug("isJavaProjectFile true");
					// TODO 자바 프로젝트 파일인경우 처리할 항목 기술.
					menuRunAs.getItems().add(new MenuItem("Java Application"));
				}

				if (selectedTreeItem instanceof JavaProjectMemberFileTreeItem) {
					LOGGER.debug("projectFile Member true");
					// TODO 메인함수인지 아닌지 여부를 결정해서 Java Application 기능 처리.

					if (FileUtil.isJavaFile(file)) {
						MenuItem runJavaApp = new MenuItem("Java Application");
						runJavaApp.setOnAction(this::miRunJavaAppOnAction);
						menuRunAs.getItems().add(runJavaApp);
					}
				}

				if (PMDUtil.isSupportedLanguageVersions(file)) {
					MenuItem menuRunPmd = new MenuItem("Run PMD");
					menuRunPmd.setUserData(file);
					menuRunPmd.setOnAction(this::menuRunPmdOnAction);
					menuPMD.getItems().add(menuRunPmd);
				}

				if (file.isDirectory()) {
					MenuItem menuRunAllPmd = new MenuItem("Run All PMD");
					menuRunAllPmd.setUserData(file);
					menuRunAllPmd.setOnAction(this::menuRunPmdOnAction);
					menuPMD.getItems().add(menuRunAllPmd);
				}

				// 항상출력
				menuRunAs.getItems().add(new SeparatorMenuItem());
				menuRunAs.getItems().add(new MenuItem("Run Configurations"));

			}
			menuItemSCMGraphs.setDisable(isDisableSCMGraphsMenuItem);
			/* [끝] SVN Graph 관련 Disable 여부 체크 */

		});
		/***********************************************************************************/
		// 조건에 따라 보여주는 아이템. [end]
		/***********************************************************************************/

		{
			MenuItem openSystemExplorerMenuItem = new MenuItem("Open Sys. Explorer");
			openSystemExplorerMenuItem.setOnAction(this::openSystemExplorerMenuItemOnAction);
			menuOpenWidth.getItems().add(openSystemExplorerMenuItem);
		}

		Menu newFileMenuItem = new Menu("New");
		MenuItem newDir = new MenuItem("Dir");
		newDir.setOnAction(this::newDirOnAction);
		newFileMenuItem.getItems().add(newDir);
		MenuItem deleteFileMenuItem = new MenuItem("Delete");

		KeyCodeCombination value = new KeyCodeCombination(KeyCode.DELETE);
		deleteFileMenuItem.setAccelerator(value);
		deleteFileMenuItem.setOnAction(this::deleteFileMenuItemOnAction);

		// 선택한 파일아이템을 VoEditor에서 조회시 사용
		// MenuItem voEditorMenuItem = new MenuItem("Show VO Editor");
		// //선택한 파일아이템을 DaoWizard에서 조회시 사용
		// MenuItem daoWizardMenuItem = new MenuItem("Show DAO Wizard");
		// 선택한 파일경로를 Vo Editor Location에 바인딩함.
		// MenuItem setVoEditorMenuItem = new MenuItem("SET Vo Editor
		// Directory");

		// 선택한 파일경로를 Vo Editor Location에 바인딩함.
		MenuItem voEditorMenuItem = new MenuItem("Vo Editor");

		// 선택한 파일경로를 DaoWizard Location에 바인딩함.
		MenuItem setDaoWizardMenuItem = new MenuItem("SET DAO Wizard Directory");
		// 경로 리프레쉬
		MenuItem refleshMenuItem = new MenuItem("Reflesh");

		// 코드분석
		MenuItem chodeAnalysisMenuItem = new MenuItem("자바 코드 분석");
		// 사양서 생성
		MenuItem makeProgramSpecMenuItem = new MenuItem("Gen. Program Spec.");

		// 파일 속성 조회
		MenuItem menuProperties = new MenuItem("Properties");

		menuProperties.setOnAction(this::menuPropertiesOnAction);
		chodeAnalysisMenuItem.setOnAction(this::menuItemCodeAnalysisMenuItemOnAction);

		fileTreeContextMenu.getItems().addAll(openFileMenuItem, menuOpenWidth, newFileMenuItem, deleteFileMenuItem, /*
																													 * voEditorMenuItem,
																													 * daoWizardMenuItem,
																													 */
				voEditorMenuItem, /* setVoEditorMenuItem, */ setDaoWizardMenuItem, chodeAnalysisMenuItem, makeProgramSpecMenuItem,
				menuItemSCMGraphs, new SeparatorMenuItem(), refleshMenuItem, new SeparatorMenuItem(), menuPMD, new SeparatorMenuItem(),
				menuRunAs, new SeparatorMenuItem(), menuProperties);

		// daoWizardMenuItem.addEventHandler(ActionEvent.ACTION,
		// this::daoWizardMenuItemOnActionEvent);

		// Vo Editor
		// setVoEditorMenuItem.addEventHandler(ActionEvent.ACTION, event -> {
		// Node lookup = borderPaneMain.lookup("#txtLocation");
		// if (lookup != null && tmpSelectFileWrapper != null) {
		// TextField txtLocation = (TextField) lookup;
		// File file = tmpSelectFileWrapper.getFile();
		// if (file.isDirectory()) {
		// String absolutePath = file.getAbsolutePath();
		// ResourceLoader.getInstance().put(ResourceLoader.USER_SELECT_LOCATION_PATH,
		// absolutePath);
		// txtLocation.setText(absolutePath);
		// } else {
		// DialogUtil.showMessageDialog("Only Directory.");
		// }
		// }
		// tmpSelectFileWrapper = null;
		// });

		voEditorMenuItem.addEventHandler(ActionEvent.ACTION, this::voEditorMenuItemOnAction);

		// Dao Wizard
		setDaoWizardMenuItem.addEventHandler(ActionEvent.ACTION, event -> {
			Node lookup = borderPaneMain.lookup("#txtDaoLocation");
			if (lookup != null && tmpSelectFileWrapper != null) {
				TextField txtLocation = (TextField) lookup;
				File file = tmpSelectFileWrapper.getFile();
				if (file.exists() && file.isDirectory()) {
					ResourceLoader.getInstance().put(ResourceLoader.USER_SELECT_LOCATION_PATH, file.getAbsolutePath());

					// 경로를 생대경로화 시킨다.
					Path relativize = FileUtil.toRelativizeForGagoyle(file);

					txtLocation.setText(File.separator + relativize.toString());
				} else {
					DialogUtil.showMessageDialog("Only Directory.");
				}
			}
			tmpSelectFileWrapper = null;
		});

		refleshMenuItem.setOnAction(event -> {
			TreeItem<JavaProjectFileWrapper> selectedItem = treeProjectFile.getSelectionModel().getSelectedItem();
			refleshWorkspaceTreeItem(selectedItem);
		});

		// 함수위치로 이동
		// deleteFileMenuItem.setOnAction(event -> {
		// TreeItem<FileWrapper> selectedItem =
		// treeProjectFile.getSelectionModel().getSelectedItem();
		// if (selectedItem != null) {
		// File file = selectedItem.getValue().getFile();
		// if (file != null && file.exists()) {
		// Optional<Pair<String, String>> showYesOrNoDialog =
		// DialogUtil.showYesOrNoDialog("파일삭제.",
		// file.getName() + " 정말 삭제하시겠습니까? \n[휴지통에 보관되지않음.]");
		// showYesOrNoDialog.ifPresent(pair -> {
		// if ("Y".equals(pair.getValue())) {
		// TreeItem<FileWrapper> root = treeProjectFile.getRoot();
		// root.getChildren().remove(selectedItem);
		// file.delete();
		// }
		// });
		// }
		// }
		// });

		makeProgramSpecMenuItem.setOnAction(event -> {

			TreeItem<JavaProjectFileWrapper> selectedItem = treeProjectFile.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				File sourceFile = selectedItem.getValue().getFile();
				if (sourceFile != null && sourceFile.exists()) {
					try {
						if (FileUtil.isJavaFile(sourceFile)) {

							// 파일을 생성하고, 생성하고 나면 오픈.
							File targetFile = DialogUtil.showFileSaveCheckDialog(SharedMemory.getPrimaryStage(), chooser -> {

								chooser.setInitialFileName(DateUtil.getCurrentDateString(DateUtil.SYSTEM_DATEFORMAT_YYYYMMDDHHMMSS));
								chooser.getExtensionFilters()
										.add(new ExtensionFilter(GargoyleExtensionFilters.DOCX_NAME, GargoyleExtensionFilters.DOCX));
								chooser.setTitle("Save Program Spec. Doc");
								chooser.setInitialDirectory(new File(SystemUtils.USER_HOME));

							});

							if (targetFile != null) {
								boolean createDefault = ProgramSpecUtil.createDefault(sourceFile, targetFile);
								if (createDefault)
									FileUtil.openFile(targetFile);
							}

						} else {
							DialogUtil.showMessageDialog("사양서 작성 가능한 유형이 아닙니다.");
						}
					} catch (Exception e) {
						DialogUtil.showExceptionDailog(e);
					}
				}
			}

		});

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 14.
	 * @param selectedItem
	 */
	private void refleshWorkspaceTreeItem(final TreeItem<JavaProjectFileWrapper> selectedItem) {
		selectedItem.getChildren().clear();
		selectedItem.getChildren().addAll(createNewTree(selectedItem.getValue().getFile()).getChildren());
	}

	private ProjectFileTreeItemCreator projectFileTreeCreator = new ProjectFileTreeItemCreator();

	/**
	 * 디렉토리 기준으로 파일트리를 새로 생성한다.
	 *
	 * @Date 2015. 10. 17.
	 * @param dir
	 * @return
	 * @User KYJ
	 */
	private TreeItem<JavaProjectFileWrapper> createNewTree(File dir) {
		return projectFileTreeCreator.createNode(dir);
	}

	@FXML
	public void menuOpenOnAction() {
		File showFileDialog = DialogUtil.showFileDialog(SharedMemory.getPrimaryStage());
		if (showFileDialog != null) {
			openFile(showFileDialog);
		}
	}

	@FXML
	public void menuSwitchWorkspaceOnAction(ActionEvent e) throws IOException {
		SelectWorkspaceView view = new SelectWorkspaceView();
		ResultDialog<Object> show = view.showAndWait();
		if (show != null && ResultDialog.OK == show.getStatus()) {
			String baseDir = ResourceLoader.getInstance().get(ResourceLoader.BASE_DIR);
			selectDirFile = new File(baseDir);
			treeProjectFile.setRoot(createNewTree(selectDirFile));
			treeProjectFile.setShowRoot(false);

			// 2016-10-14 kyj Resource 리로팅
			SharedMemory.init();

			// TODO 열러있던 탭을 모두 close, 이 처리가 되기전에 열려있는 창에대한 문구 표시 필요
			// tabPanWorkspace.getTabs().clear();

		}
	}

	/**
	 * Help > About Click Event.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @param e
	 */
	@FXML
	public void miAboutOnAction(ActionEvent e) {
		// String url =
		// ConfigResourceLoader.getInstance().get(ConfigResourceLoader.ABOUT_PAGE_URL);
		// DialogUtil.showMessageDialog(String.format("Gagoyle\nGithub :
		// %s\nVersion : %s", url, Main.getVersion()));
		FxUtil.createStageAndShow(new GargoyleHelpComposite());
	}

	/**
	 * 탭 닫기 메뉴
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 8.
	 * @return
	 */
	public ContextMenu closeContextMenu() {

		MenuItem closeMenuItem = new MenuItem("Close");

		closeMenuItem.setOnAction(hander -> {
			DockTab tab = tabPanWorkspace.getSelectionModel().getSelectedItem();
			closeTab(tab);
		});
		MenuItem closeAllMenuItem = new MenuItem("Close All   CTRL + SHIFT + W");

		closeAllMenuItem.setOnAction(handler -> {
			for (int i = tabPanWorkspace.getTabs().size() - 1; i >= 1; i--) {
				DockTab tab = tabPanWorkspace.getTabs().get(i);
				closeTab(tab);
			}
		});

		MenuItem closeOtherMenuItem = new MenuItem("Close Others");
		closeOtherMenuItem.setOnAction(hander -> {
			DockTab tab = tabPanWorkspace.getSelectionModel().getSelectedItem();

			for (int i = tabPanWorkspace.getTabs().size() - 1; i >= 1; i--) {
				DockTab otherTab = tabPanWorkspace.getTabs().get(i);
				if (tab != otherTab)
					closeTab(otherTab);
			}

		});

		MenuItem closeRightMenuItem = new MenuItem("Close Tab to the Right");
		closeRightMenuItem.setOnAction(hander -> {
			ObservableList<DockTab> tabs = tabPanWorkspace.getTabs();
			int tabSize = tabs.size();
			int selectedIndex = tabPanWorkspace.getSelectionModel().getSelectedIndex();
			if (selectedIndex == -1)
				return;

			for (int i = tabSize - 1; i > selectedIndex; i--) {
				DockTab otherTab = tabs.get(i);
				closeTab(otherTab);
			}

		});

		MenuItem closeLeftMenuItem = new MenuItem("Close Tab to the Left");
		closeLeftMenuItem.setOnAction(hander -> {
			DockTab selectedItem = tabPanWorkspace.getSelectionModel().getSelectedItem();
			if (selectedItem == null)
				return;

			ObservableList<DockTab> tabs = null;
			while (true) {

				// 삭제처리이기때문에 배열을 지속적으로 다시 가져와야함.
				tabs = tabPanWorkspace.getTabs();
				// WelCome Page는 닫지않음
				if (tabs.size() == 1)
					break;

				// WelCome Page -> 0 index므로 1번부터 조회
				DockTab dockTab = tabs.get(1);

				// 선택된 탭이면 종료
				if (selectedItem == dockTab)
					break;

				if (dockTab != null)
					closeTab(dockTab);

			}

		});

		ContextMenu contextMenu = new ContextMenu(closeMenuItem, closeOtherMenuItem, closeAllMenuItem, closeRightMenuItem,
				closeLeftMenuItem);

		return contextMenu;
	}

	/**
	 * 탭닫기 (공통 이벤트 호출 포함.)
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 8.
	 * @param tab
	 */
	public void closeTab(DockTab tab) {
		if (tab != null) {

			// WelCome Page는 닫지않음.
			if (tabPanWorkspace.getTabs().indexOf(tab) == 0)
				return;

			// main tab close
			tabPanWorkspace.getTabs().remove(tab);

		}
	}

	/********************************
	 * 작성일 : 2016. 5. 29. 작성자 : KYJ
	 *
	 * OS 시스템에 의존되는 형식으로 파일을 오픈함.
	 *
	 * @param e
	 ********************************/
	public void openSystemExplorerMenuItemOnAction(ActionEvent e) {
		TreeItem<JavaProjectFileWrapper> selectedItem = this.treeProjectFile.getSelectionModel().getSelectedItem();
		NullExpresion.ifNotNullDo(selectedItem, item -> {
			JavaProjectFileWrapper fw = item.getValue();
			File file = fw.getFile();
			if (file != null && file.exists()) {
				FileUtil.openFile(file);
			}
		});
		//
	}

	/********************************
	 * 작성일 : 2016. 8. 29. 작성자 : KYJ
	 *
	 * 디렉토리를 새로 생성한다.
	 *
	 * @param e
	 ********************************/
	public void newDirOnAction(ActionEvent e) {
		TreeItem<JavaProjectFileWrapper> selectedItem = treeProjectFile.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			JavaProjectFileWrapper value = selectedItem.getValue();
			File file = value.getFile();
			if (file.isDirectory()) {
				Optional<Pair<String, String>> showInputDialog = DialogUtil.showInputDialog("Directory Name", "Input New Dir Name");
				showInputDialog.ifPresent(v -> {
					String newFileName = v.getValue();
					File createdNewFile = new File(file, newFileName);
					boolean mkdir = createdNewFile.mkdir();
					if (mkdir) {

						TreeItem<JavaProjectFileWrapper> createDefaultNode = projectFileTreeCreator
								.createDefaultNode(new JavaProjectFileWrapper(createdNewFile));
						createDefaultNode.setExpanded(true);
						selectedItem.getChildren().add(createDefaultNode);
					}
				});

			}
		}
	}

	/********************************
	 * 작성일 : 2016. 8. 29. 작성자 : KYJ
	 *
	 * 파일삭제.
	 *
	 * @param e
	 ********************************/
	public void deleteFileMenuItemOnAction(ActionEvent e) {

		TreeItem<JavaProjectFileWrapper> selectedItem = treeProjectFile.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			File file = selectedItem.getValue().getFile();
			if (file != null && file.exists()) {

				Optional<Pair<String, String>> showYesOrNoDialog = DialogUtil.showYesOrNoDialog("파일삭제.",
						file.getName() + " 정말 삭제하시겠습니까? \n[휴지통에 보관되지않음.]");
				showYesOrNoDialog.ifPresent(pair -> {
					if ("Y".equals(pair.getValue())) {

						if (file.isDirectory())
							FileUtil.deleteDir(file);
						else
							file.delete();

						TreeItem<JavaProjectFileWrapper> root = selectedItem.getParent();// treeProjectFile.getRoot();
						root.getChildren().remove(selectedItem);

					}
				});
			}
		}

	}

	/**
	 * PMD 실행처리
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 4.
	 * @param e
	 */
	public void menuRunPmdOnAction(ActionEvent e) {
		MenuItem source = (MenuItem) e.getSource();
		File file = (File) source.getUserData();
		if (file != null && file.exists()) {
			String name = file.getName();
			PMDCheckedListComposite parent = new PMDCheckedListComposite(file);
			loadNewSystemTab(String.format("PMD - %s", name), (CloseableParent<BorderPane>) parent);
			parent.runAsynch();
		}
	}

	/********************************
	 * 작성일 : 2016. 5. 29. 작성자 : KYJ
	 *
	 * TODO 자바 코드 분석기 테스트단계.
	 *
	 * @param e
	 ********************************/
	public void menuItemCodeAnalysisMenuItemOnAction(ActionEvent e) {
		TreeItem<JavaProjectFileWrapper> selectedItem = treeProjectFile.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			JavaProjectFileWrapper value = selectedItem.getValue();
			File sourceFile = value.getFile();
			// if(selectedItem instanceof JavaProjectMemberFileTreeItem)
			{
				if (value.isSVNConnected()) {
					File wcDbFile = value.getWcDbFile();
					if (wcDbFile != null && wcDbFile.exists()) {
						SVNWcDbClient client;
						try {
							client = new SVNWcDbClient(wcDbFile);

							// TODO 코드 완성시키기.
							// new SVNFileHistoryComposite(
							// JavaSVNManager.createNewInstance(client.getUrl())
							// , sourceFile);
							// new JavaSVNManager(new Properties(defaults))
						} catch (Exception e1) {
							LOGGER.error(ValueUtil.toString(e1));
						}
					}
				}
			}

			if (sourceFile != null && sourceFile.exists()) {
				try {
					if (FileUtil.isJavaFile(sourceFile)) {

						JavaProjectFileTreeItem javaProjectFileTreeItem = FileUtil.toJavaProjectFileTreeItem(selectedItem);
						if (javaProjectFileTreeItem != null) {

							Tab tab = new Tab(sourceFile.getName(), new CodeAnalysisJavaTextArea(sourceFile));
							SpecResource resource = new SpecResource(javaProjectFileTreeItem.getValue().getFile(), sourceFile);
							SpecTabPane newInstance = new SpecTabPane(resource, tab);
							loadNewSystemTab(sourceFile.getName(), newInstance);

						}

					} else {
						DialogUtil.showMessageDialog("자바 파일이 아닙니다.");
					}
				} catch (Exception ex) {
					DialogUtil.showExceptionDailog(ex);
				}
			}
		}
	}

	/********************************
	 * 작성일 : 2016. 7. 3. 작성자 : KYJ
	 *
	 * 파일로부터 VO Editor를 오픈함.
	 *
	 * @param e
	 ********************************/
	public void voEditorMenuItemOnAction(ActionEvent e) {

		TreeItem<JavaProjectFileWrapper> selectedItem = this.treeProjectFile.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			JavaProjectFileWrapper value = selectedItem.getValue();
			File file = value.getFile();

			try {
				Parent parent = FxUtil.load(VoEditorController.class, null, null, c -> {
					c.setFromFile(file);
				});

				loadNewSystemTab("VoEditor", parent);

			} catch (Exception e1) {
				LOGGER.error(ValueUtil.toString(e1));
			}

		}
	}

	/********************************
	 * 작성일 : 2016. 6. 11. 작성자 : KYJ
	 *
	 * 트리 컨텍스트 요청 이벤트
	 *
	 * @param event
	 ********************************/
	public void treeProjectFileOnContextMenuRequested(ContextMenuEvent event) {

		if (event.getSource() == treeProjectFile) {
			fileTreeContextMenu.hide();
			TreeItem<JavaProjectFileWrapper> selectedItem = treeProjectFile.getSelectionModel().getSelectedItem();
			if (selectedItem == null)
				return;

			tmpSelectFileWrapper = selectedItem.getValue();
			fileTreeContextMenu.show(treeProjectFile, event.getScreenX(), event.getScreenY());
		} else {
			fileTreeContextMenu.hide();
		}

	}

	/**
	 * 트리 드래그 디텍트 이벤트 처리. <br/>
	 * 트리내에 구성된 파일의 위치정보를 드래그 드롭 기능으로 <br/>
	 * 전달해주는 역할을 수행한다.<br/>
	 * <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 21.
	 * @param ev
	 */
	public void treeProjectFileOnDragDetected(MouseEvent ev) {
		TreeItem<JavaProjectFileWrapper> selectedItem = treeProjectFile.getSelectionModel().getSelectedItem();
		if (selectedItem == null || selectedItem.getValue() == null) {
			return;
		}

		File file = selectedItem.getValue().getFile();
		if (file == null || !file.exists())
			return;
		
		Dragboard board = treeProjectFile.startDragAndDrop(TransferMode.LINK);
		ClipboardContent content = new ClipboardContent();
		content.putFiles(Arrays.asList(file));
		board.setContent(content);

		ev.consume();
	}

	/********************************
	 * 작성일 : 2016. 6. 11. 작성자 : KYJ
	 *
	 * 트리 키 클릭 이벤트
	 *
	 * @param event
	 ********************************/
	public void treeProjectFileOnKeyPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.R && event.isControlDown() && event.isShiftDown() && !event.isAltDown()) {
			try {
				GagoyleWorkspaceOpenResourceView resourceView = new GagoyleWorkspaceOpenResourceView();
				ResultDialog<File> show = resourceView.show();
				File data = show.getData();
				if (data != null && data.exists()) {
					TreeItem<JavaProjectFileWrapper> search = search(data);

					treeProjectFile.getSelectionModel().select(search);
					treeProjectFile.getFocusModel().focus(treeProjectFile.getSelectionModel().getSelectedIndex());
					treeProjectFile.scrollTo(treeProjectFile.getSelectionModel().getSelectedIndex());

					openFile(data);
				}

			} catch (Exception e) {
				LOGGER.error(ValueUtil.toString(e));
			}
		} else if (event.getCode() == KeyCode.DELETE && !event.isControlDown() && !event.isShiftDown() && !event.isAltDown()) {

			// 이벤트 발생시킴.
			ActionEvent.fireEvent(tail -> tail.append((event1, tail1) -> {
				deleteFileMenuItemOnAction((ActionEvent) event1);
				return event1;
			}), new ActionEvent());

		} else if (KeyCode.F5 == event.getCode()) {
			TreeItem<JavaProjectFileWrapper> selectedItem = treeProjectFile.getSelectionModel().getSelectedItem();
			if (selectedItem != null)
				refleshWorkspaceTreeItem(selectedItem);
		}

	}

	public TreeItem<JavaProjectFileWrapper> search(File f) {
		TreeItem<JavaProjectFileWrapper> root = treeProjectFile.getRoot();

		Path p = FileUtil.toRelativizeForGagoyle(f);
		String[] split = StringUtils.split(p.toString(), File.separator);

		boolean isFound = false;
		ObservableList<TreeItem<JavaProjectFileWrapper>> children = root.getChildren();
		int sliding = 0;
		TreeItem<JavaProjectFileWrapper> treeItem = null;
		for (int i = 0; i < split.length; i++) {
			isFound = false;

			for (TreeItem<JavaProjectFileWrapper> w : children) {
				treeItem = w;
				String name = treeItem.getValue().getFile().getName();

				if (split[sliding].equals(name)) {
					isFound = true;

					break;
				}

			}
			if (isFound) {
				children = treeItem.getChildren();
				sliding++;
			} else {
				// 아예못찾은경우는 종료.
				break;
			}
		}

		return treeItem;
	}

	/********************************
	 * 작성일 : 2016. 6. 11. 작성자 : KYJ
	 *
	 * 프로젝트 트리 마우스 클릭 이벤트
	 *
	 * @param event
	 ********************************/
	public void treeProjectFileOnMouseClick(MouseEvent event) {

		fileTreeContextMenu.hide();

		/* 탭 추가 기능. */
		if (event.getClickCount() == 2) {
			TreeItem<JavaProjectFileWrapper> selectedItem = treeProjectFile.getSelectionModel().getSelectedItem();
			if (selectedItem == null)
				return;

			JavaProjectFileWrapper value = selectedItem.getValue();
			if (value != null) {
				File file = value.getFile();
				LOGGER.debug(String.format(" file : %s ", file.toPath()));
				if (file.exists() && file.isFile()) {
					try {
						boolean javaFile = FileUtil.isJavaFile(file);
						LOGGER.debug("is javafile ? " + javaFile);
						openFile(file);
					} catch (Exception e) {
						LOGGER.error(ValueUtil.toString(e));
					}
				}

			}

		}

	}

	/********************************
	 * 작성일 : 2016. 5. 12. 작성자 : KYJ
	 *
	 * 속성을 조회하는 팝업을 로드한다.
	 *
	 * @param e
	 ********************************/
	public void menuPropertiesOnAction(ActionEvent e) {

		TreeItem<JavaProjectFileWrapper> selectedItem = treeProjectFile.getSelectionModel().getSelectedItem();
		NullExpresion.ifNotNullDo(selectedItem, item -> {
			File file = item.getValue().getFile();
			if (file.exists()) {

				FilePropertiesComposite composite = new FilePropertiesComposite(file);
				FxUtil.createStageAndShow(composite, stage -> {
					stage.setTitle(FilePropertiesComposite.TITLE);
					stage.initOwner(SharedMemory.getPrimaryStage());
				});
			}

		});

	}

	/**
	 * 탭항목 추가.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 11. 2.
	 * @param tab
	 */
	public void addTabItem(DockTab tab) {
		tab.setClosable(true);
		tabPanWorkspace.getTabs().add(tab);
		tab.setContextMenu(closeContextMenu());

		/*
		 * 17.9.17 kyj 기능이 처리되지않음. DragDrop 기능 설치.
		 * 
		 */
		// new DefaultFileDragDropHelper(tab.getContent());

	}

	/**
	 * 탭에 대해 로드함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 11. 4.
	 * @param tabName
	 * @param fxmlName
	 */
	@Override
	public void loadNewSystemTab(String tabName, String fxmlName) {
		Platform.runLater(() -> {
			try {
				FXMLLoader loader = FxUtil.createNewFxmlLoader();
				loader.setLocation(getClass().getResource(fxmlName));
				Parent parent = loader.load();

				if (beforeParentLoad != null && beforeParentLoad.filter(parent)) {
					beforeParentLoad.beforeLoad(parent);

					if (beforeParentLoad.isUnloadParent()) {
						return;
					}

				}

				/*
				 * DockTab tab = new DockTab(tabName, parent);
				 * tab.setTooltip(new
				 * Tooltip(loader.getController().getClass().getName()));
				 * 
				 * addTabItem(tab);
				 * tab.getTabPane().getSelectionModel().select(tab);
				 * 
				 * // 리스너 호출. onParentloaded.forEach(v -> { v.onLoad(parent);
				 * });
				 * 
				 * if (parent instanceof GargoyleTabPanable) {
				 * GargoyleTabPanable _tabPanable = (GargoyleTabPanable) parent;
				 * _tabPanable.setTab(tab);
				 * _tabPanable.setTabPane(tabPanWorkspace); }
				 */
				DockTab tab = new DockTab(tabName, parent);
				loadNewSystemTab(tabName, tab, null);
			} catch (IOException e1) {
				DialogUtil.showExceptionDailog(e1);
			}
		});
	}

	/**
	 * 탭에 대해 로드함. <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 11. 4.
	 * @param tableName
	 * @param fxmlName
	 */
	public void loadNewSystemTab(String tableName, Parent parent, String skin) {
		Platform.runLater(() -> {
			try {
				if (beforeParentLoad != null && beforeParentLoad.filter(parent)) {
					beforeParentLoad.beforeLoad(parent);

					if (beforeParentLoad.isUnloadParent()) {
						return;
					}
				}

				DockTab tab = new DockTab(tableName, parent);
				loadNewSystemTab(tableName, tab, skin);

				tab.setOnCloseRequest(ev -> {
					try {
						LOGGER.debug("closeable parent on close request , tabName : {} ", tableName);
						if (parent instanceof Closeable) {
							((Closeable) parent).close();
						}
					} catch (Exception e) {
						LOGGER.error(ValueUtil.toString(e));
					}
				});
			} catch (Exception e1) {
				DialogUtil.showExceptionDailog(e1);
			}
		});
	}

	/**
	 * 탭 로드 <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 20.
	 * @param tableName
	 * @param tab
	 */
	public void loadNewSystemTab(String tabName, DockTab tab) {
		loadNewSystemTab(tabName, tab, null);
	}

	/**
	 * 탭 로드 <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 20.
	 * @param tableName
	 * @param tab
	 * @param skin
	 */
	public void loadNewSystemTab(String tableName, DockTab tab, String skin) {

		Parent parent = (Parent) tab.getContent();
		// 툴팁 처리 (클래스위치)
		tab.setTooltip(new Tooltip(parent.getClass().getName()));
		addTabItem(tab);
		if (skin != null) {
			parent.getStylesheets().clear();
		}
		tabPanWorkspace.getSelectionModel().select(tab);

		// 리스너 호출.
		onParentloaded.forEach(v -> v.onLoad(parent));
		// _parent.getStylesheets().clear();

		List<Node> findAllByNodes = FxUtil.findAllByNodes(parent, n -> n instanceof Button);
		findAllByNodes.forEach(btn -> {
			btn.getStyleClass().add("button-gargoyle");
		});

		if (parent instanceof GargoyleTabPanable) {
			GargoyleTabPanable _tabPanable = (GargoyleTabPanable) parent;
			_tabPanable.setTab(tab);
			_tabPanable.setTabPane(tabPanWorkspace);
		}

	}

	/**
	 * 탭에 대해 로드함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 11. 4.
	 * @param tableName
	 * @param fxmlName
	 */
	@Override
	public void loadNewSystemTab(String displayMenuName, Parent newInstance) {
		loadNewSystemTab(displayMenuName, newInstance, null);
	}

	/********************************
	 * 작성일 : 2016. 4. 26. 작성자 : KYJ
	 *
	 * 새로운 탭을 로드한다.
	 *
	 * @param tableName
	 * @param parent
	 ********************************/
	public void loadNewSystemTab(String tableName, CloseableParent<?> parent) {
		Platform.runLater(() -> {
			try {

				Parent _parent = parent.getParent();

				if (beforeParentLoad != null && beforeParentLoad.filter(_parent)) {
					beforeParentLoad.beforeLoad(_parent);

					if (beforeParentLoad.isUnloadParent()) {
						return;
					}
				}

				DockTab tab = new DockTab(tableName, _parent);
				loadNewSystemTab(tableName, tab);

				tab.setOnCloseRequest(ev -> {
					try {
						LOGGER.debug("closeable parent on close request , tabName : {} ", tableName);
						parent.close();
					} catch (Exception e) {
						LOGGER.error(ValueUtil.toString(e));
					}
				});

				/*
				 * DockTab tab = new DockTab(tableName, _parent); // 툴팁 처리
				 * (클래스위치) tab.setTooltip(new
				 * Tooltip(parent.getClass().getName()));
				 * 
				 * addTabItem(tab);
				 * tabPanWorkspace.getSelectionModel().select(tab);
				 * 
				 * tab.setOnCloseRequest(ev -> { try { LOGGER.
				 * debug("closeable parent on close request , tabName : {} ",
				 * tableName); parent.close(); } catch (Exception e) {
				 * LOGGER.error(ValueUtil.toString(e)); } });
				 * 
				 * // 리스너 호출. onParentloaded.forEach(v ->
				 * v.onLoad(parent.getParent()));
				 * 
				 * List<Node> findAllByNodes = FxUtil.findAllByNodes(_parent, n
				 * -> n instanceof Button); findAllByNodes.forEach(btn -> {
				 * btn.getStyleClass().add("button-gargoyle"); });
				 * 
				 * if (_parent instanceof GargoyleTabPanable) {
				 * GargoyleTabPanable _tabPanable = (GargoyleTabPanable)
				 * _parent; _tabPanable.setTab(tab);
				 * _tabPanable.setTabPane(tabPanWorkspace); }
				 * 
				 */

			} catch (Exception e1) {
				DialogUtil.showExceptionDailog(e1);
			}
		});
	}

	@FXML
	public void lblDaoWizardOnMouseClick(MouseEvent e) {
		loadNewSystemTab("DaoWizard", "DaoWizardView.fxml");
	}

	@FXML
	public void lblDaoWizardOnAction(ActionEvent e) {
		lblDaoWizardOnMouseClick(null);
	}

	/**
	 * 설정 화면을 로드한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 11. 4.
	 * @param e
	 */
	@FXML
	public void lblConfigonMouseClick(MouseEvent e) {
		loadNewSystemTab("Configuration", "ConfigurationView.fxml");
	}

	@FXML
	public void lblConfigonOnAction(ActionEvent e) {
		lblConfigonMouseClick(null);
	}

	@FXML
	public void lblSVNOnAction(ActionEvent e) {
		try {

			Scene scene = new Scene(new BorderPane(new SVNViewer()), 1100, 900);
			scene.getStylesheets().add(SkinManager.getInstance().getSkin());
			FxUtil.createStageAndShow(scene, stage -> {
				stage.setTitle("SVN");
				stage.initOwner(SharedMemory.getPrimaryStage());
				stage.setAlwaysOnTop(false);
				stage.centerOnScreen();
			});

			// Stage stage = new Stage();
			// Scene scene = new Scene(new BorderPane(new SVNViewer()), 1100,
			// 900);
			//
			//
			// scene.getStylesheets().add(SkinManager.getInstance().getSkin());
			// stage.setScene(scene);
			// stage.initOwner(SharedMemory.getPrimaryStage());
			//
			// stage.setTitle("SVN");
			// stage.setAlwaysOnTop(false);
			// stage.centerOnScreen();
			// stage.show();
		} catch (Exception ex) {
			LOGGER.error(ValueUtil.toString(ex));
			DialogUtil.showExceptionDailog(ex);
		}

	}

	/**
	 * Vo Editor 로드
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 11. 4.
	 * @param e
	 */
	@FXML
	public void lblVoEditorOnMouseClick(MouseEvent e) {
		loadNewSystemTab("VoEditor", "VoEditorView.fxml");
	}

	@FXML
	public void lblVoEditorOnAction(ActionEvent e) {
		lblVoEditorOnMouseClick(null);
	}

	@FXML
	public void lblDatabaseMouseClick(MouseEvent e) {

		/* 17.11.15 DB 비동기 접속 처리 로직 구현 */
		Label source = (Label) e.getSource();
		source.setDisable(true);

		DemonThreadFactory<Boolean> newInstance = DemonThreadFactory.newInstance();
		newInstance.newThread(new CloseableCallable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				try (Connection connection = DbUtil.getConnection()) {
					connection.getMetaData().getCatalogTerm();
				}
				return true;
			}
		}, flag -> {

			if (flag) {
				Platform.runLater(() -> {
					source.setDisable(false);
					try {
						CommonsSqllPan sqlPane = CommonsSqllPan.getSqlPane();
						loadNewSystemTab(String.format("Database[%s]", sqlPane.getClass().getSimpleName()), sqlPane);
					} catch (Exception ex) {
						LOGGER.error(ValueUtil.toString(ex));
						DialogUtil.showExceptionDailog(SharedMemory.getPrimaryStage(), ex);
					}
				});
			} else {
				DialogUtil.showMessageDialog(SharedMemory.getPrimaryStage(), "Connection Fail.. ");
			}

		}, ex -> {

			Platform.runLater(() -> {
				source.setDisable(false);
			});

			LOGGER.error(ValueUtil.toString(ex));
			DialogUtil.showExceptionDailog(SharedMemory.getPrimaryStage(), ex);
		}).start();

	}

	@FXML
	public void lblDatabaseOnAction(ActionEvent e) {
		lblDatabaseMouseClick(null);
	}

	@FXML
	public void lblSpreadSheetOnMouseClick(MouseEvent e) {
		loadNewSystemTab(TAB_TITLE_SPREAD_SHEET, new SchoolMgrerSpreadSheetView());
	}

	@FXML
	public void lblSpreadSheetOnAction(ActionEvent e) {
		lblSpreadSheetOnMouseClick(null);
	}

	@FXML
	public void lblDBConsoleOnAction(ActionEvent e) {
		lblDBConsoleOnMouseClick(null);
	}

	@FXML
	public void lblDBConsoleOnMouseClick(MouseEvent e) {
		if (dbConsoleProperty.get() != null)
			return;

		try {
			ReadOnlyConsole console = ReadOnlySingletonConsole.getInstance();
			console.init();
			dbConsoleProperty.set(console);
			Stage stage = new Stage();
			BorderPane root = new BorderPane(console);

			Scene scene = new Scene(root, 700, 300);
			scene.getStylesheets().add(SkinManager.getInstance().getSkin());
			stage.setScene(scene);
			double x = SharedMemory.getPrimaryStage().getX();
			double y = SharedMemory.getPrimaryStage().getY();
			stage.setTitle("Database Console");
			stage.setX(x);
			stage.setY(y);
			stage.setAlwaysOnTop(false);
			stage.initOwner(SharedMemory.getPrimaryStage());
			stage.centerOnScreen();
			stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, ev -> {
				try {
					dbConsoleProperty.set(null);
					console.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			});

			stage.show();
			// dbConsoleProperty.set(null);
		} catch (Exception ex) {
			DialogUtil.showExceptionDailog(ex);
			LOGGER.error(ValueUtil.toString(ex));
		}
	}

	@FXML
	public void lblSystemConsoleOnAction(ActionEvent e) {
		lblSystemConsoleOnMouseClick(null);
	}

	@FXML
	public void lblSystemConsoleOnMouseClick(MouseEvent e) {

		if (systemConsoleProperty.get() != null)
			return;

		ReadOnlyConsole console = null;
		try {
			console = SystemConsole.getInstance();
			console.init();
			systemConsoleProperty.set(console);
			Stage stage = new Stage();
			BorderPane root = new BorderPane(console);

			Scene scene = new Scene(root, 700, 300);
			scene.getStylesheets().add(SkinManager.getInstance().getSkin());

			stage.setScene(scene);
			double x = SharedMemory.getPrimaryStage().getX();
			double y = SharedMemory.getPrimaryStage().getY();
			stage.setTitle("System Console");
			stage.setX(x);
			stage.setY(y);
			stage.setAlwaysOnTop(false);
			stage.initOwner(SharedMemory.getPrimaryStage());
			stage.centerOnScreen();

			stage.setOnCloseRequest(ev -> {

				systemConsoleProperty.set(null);
				// 스트림 복구.

				try {
					SystemConsole.getInstance().close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}

			});
			stage.show();

		} catch (Exception ex) {
			DialogUtil.showExceptionDailog(ex);
			LOGGER.error(ValueUtil.toString(ex));
		}
	}

	/**
	 * 어플리케이션 종료처리
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 11. 6.
	 */
	@FXML
	public void menuExitOnAction() {
		Optional<Pair<String, String>> showYesOrNoDialog = DialogUtil.showYesOrNoDialog("종료", "Exit ?");
		showYesOrNoDialog.ifPresent(str -> {

			if ("RESULT".equals(str.getKey()) && "Y".equals(str.getValue())) {
				SharedMemory.getPrimaryStage().close();
				Platform.exit();
			}
		});

	}

	/********************************
	 * 작성일 : 2016. 6. 19. 작성자 : KYJ
	 *
	 * 씬빌더 open하기 위한 이벤트.
	 *
	 * SceneBuilderOpenEvent로 넘겨받은 항목만 호출됨.
	 *
	 * @param e
	 ********************************/
	public void menuItemOpenWithSceneBuilderOnAction(ActionEvent event) {
		TreeItem<JavaProjectFileWrapper> selectedTreeItem = this.treeProjectFile.getSelectionModel().getSelectedItem();

		if (selectedTreeItem != null) {
			File selectedTree = selectedTreeItem.getValue().getFile();
			if (FileUtil.isFXML(selectedTree)) {

				String sceneLocation = ResourceLoader.getInstance().get(ResourceLoader.SCENEBUILDER_LOCATION);

				if (ValueUtil.isEmpty(sceneLocation) || !(new File(sceneLocation).exists())) {
					DialogUtil.showMessageDialog("You have to set up SceneBuilder Location.");
					lblConfigonMouseClick(null);
					return;
				}
				List<String> args = Arrays.asList(sceneLocation, selectedTree.getAbsolutePath());
				try {
					RuntimeClassUtil.simpleExec(args);
				} catch (Exception e) {
					LOGGER.error(ValueUtil.toString(e));
				}

			}
		}

	}

	/**
	 * SCM Graph분석 화면을 생성후 보여주는 처리를 함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 21.
	 * @param event
	 */
	public void menuItemSCMGraphsOnAction(ActionEvent event) {
		TreeItem<JavaProjectFileWrapper> selectedItem = treeProjectFile.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			JavaProjectFileWrapper value = selectedItem.getValue();
			if (value.isSVNConnected()) {
				File wcDbFile = value.getWcDbFile();
				if (wcDbFile.exists()) {
					try {
						SVNWcDbClient client = new SVNWcDbClient(wcDbFile);
						String rootUrl = client.getUrl();
						LOGGER.debug("root URL : {}", rootUrl);
						Properties properties = new Properties();
						properties.put(JavaSVNManager.SVN_URL, rootUrl);

						loadNewSystemTab("Scm Graph", FxUtil.createSVNGraph(properties));

					} catch (GargoyleException e) {
						LOGGER.error(ValueUtil.toString(e));
						DialogUtil.showExceptionDailog(e, e.getErrorCode().getCodeMessage());
					} catch (Exception e) {

					}
				}
			}
		}
	}

	/********************************
	 * 작성일 : 2016. 6. 30. 작성자 : KYJ
	 *
	 * 현재 선택된 탭을 출력.
	 *
	 ********************************/
	@FXML
	public void menuPrintOnAction() {
		DockTab selectedItem = this.tabPanWorkspace.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			FxUtil.printJob(SharedMemory.getPrimaryStage(), selectedItem.getContent());
		}
	}

	/********************************
	 * 작성일 : 2016. 7. 13. 작성자 : KYJ
	 *
	 * 캡쳐후 이미지 핸들링 TODO Expertiment.
	 ********************************/
	@FXML
	public void lblCaptureOnAction() {
		DockTab selectedItem = this.tabPanWorkspace.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			new ErdScreenAdapter(selectedItem.getContent(), System.err::println).load().show();
		}

	}

	public void lblPmdDesignerOnAction() {
		loadNewSystemTab("PMD Designer", new DesignerFxComposite());
	}

	/**
	 * 구글 트랜드 OPEN
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 4.
	 */
	public void lblGoogleTrendOnAction() {
		loadNewSystemTab(GoogleTrendComposite.TITLE, new GoogleTrendComposite());
	}

	public void lblNaverRschOnAction() {
		loadNewSystemTab(NrchRealtimeSrchFlowComposite.TITLE, new NrchRealtimeSrchFlowComposite());
	}

	@FXML
	public void lblUtubeDownloaderOnAction() {

		loadNewSystemTab(UtubeDownloaderComposite.TITLE, new UtubeDownloaderComposite());
	}

	/*
	 * 쿼리 리스너 구현체.
	 *
	 * 쿼리가 처리될때마다 콘솔에 출력하기 위한 처리를 한다.
	 *
	 * @inheritDoc
	 */
	@Override
	public void onQuerying(String query) {
		ReadOnlyConsole readOnlyConsole = dbConsoleProperty.get();
		if (readOnlyConsole != null)
			readOnlyConsole.appendText(query);

	}

	/**
	 * 메인에서 Parent Tab이 추가될때 처리할 리스너를 넣음.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 16.
	 * @param onload
	 */
	public boolean addOnParentLoadedListener(GagoyleParentOnLoaded onload) {
		if (onload != null)
			return onParentloaded.add(onload);
		return false;
	}

	/**
	 * 메인에서 Parent Tab 리스너 삭제
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 16.
	 * @param onload
	 */
	public boolean removeOnParentLoadedListener(GagoyleParentOnLoaded onload) {
		if (onload != null)
			return onParentloaded.remove(onload);
		return false;
	}

	private void setOnbeforeParentLoad(GagoyleParentBeforeLoad beforeLoad) {
		this.beforeParentLoad = beforeLoad;
	}

	/********************************
	 * 작성일 : 2016. 7. 14. 작성자 : KYJ
	 *
	 * 트리에 선택된 모델 리턴.
	 *
	 * @return
	 ********************************/
	public final MultipleSelectionModel<TreeItem<JavaProjectFileWrapper>> getTreeProjectFileSelectionModel() {
		return treeProjectFile.getSelectionModel();
	}

	public void refleshWorkspaceTree() {
		String baseDir = ResourceLoader.getInstance().get(ResourceLoader.BASE_DIR);
		selectDirFile = new File(baseDir);
		treeProjectFile.setRoot(createNewTree(selectDirFile));
	}

	/**
	 * 프로그램이 설치된 디렉토리를 open.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 10.
	 */
	@FXML
	public void menuShowInstalledLocation() {
		FileUtil.openFile(new File(ValueUtil.getBaseDir()));
	}

	/**
	 * LogView
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 16.
	 */
	@FXML
	public void miLogViewOnAction() {

		File showFileDialog = DialogUtil.showFileDialog(SharedMemory.getPrimaryStage());
		if (showFileDialog != null && showFileDialog.exists()) {
			try {
				LogViewComposite composite = new LogViewComposite(showFileDialog);
				loadNewSystemTab(showFileDialog.getName(), (CloseableParent<BorderPane>) composite);
				composite.start();
			} catch (Exception e) {
				LOGGER.error(ValueUtil.toString(e));
			}
		}

	}

	@FXML
	public void miJavaTaskMgrOnAction() throws Exception {
		CloseableParent<SplitPane> javaProcessMonitor = new JavaProcessMonitor();
		loadNewSystemTab("Java-Task-Manager", javaProcessMonitor);
	}

	@FXML
	public void miProxyServerOnAction() {
		CloseableParent<BorderPane> javaProcessMonitor = new ProxyServerComposite();
		loadNewSystemTab(ProxyServerComposite.class.getSimpleName(), javaProcessMonitor);
	}

	/**
	 * text viewer
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 7. 19.
	 */
	@FXML
	public void miSimpleTextViewOnAction() {
		SimpleTextView newInstance = new SimpleTextView("");
		loadNewSystemTab(SimpleTextView.APP_NAME, newInstance);
	}

	/**
	 * xml viewer
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 7. 19.
	 */
	@FXML
	public void miXmlViewOnAction() {
		loadNewSystemTab("XML Viewer", new XMLEditor());
	}

	/**
	 * 
	 */
	@FXML
	public void miXlstConvertViewOnAction() {
		loadNewSystemTab("XLST-Convert", new XsltTransformComposite());
	}

	@FXML
	public void miBase64ImageOnAction() {
		loadNewSystemTab("Base64 <-> Image", new Base64ImageConvertComposte());
	}

	@FXML
	public void miHttpRequestOnAction() {
		loadNewSystemTab("HttpUtil", new HttpActionComposite());
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 10.
	 */
	@FXML
	public void miFileAnalysisViewOnAction() {
		loadNewSystemTab("File Analysis View", new FilesAnalysisComposite());
	}

	@FXML
	public void miMailOnAction() {
		loadNewSystemTab(MailViewCompositeWrapper.getName(), new MailViewCompositeWrapper());
	}

	/**
	 * Java Application 실행처리.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 3. 6.
	 * @param e
	 */
	public void miRunJavaAppOnAction(ActionEvent e) {
		TreeItem<JavaProjectFileWrapper> selectedItem = treeProjectFile.getSelectionModel().getSelectedItem();
		if (selectedItem instanceof JavaProjectMemberFileTreeItem) {
			JavaProjectMemberFileTreeItem ti = (JavaProjectMemberFileTreeItem) selectedItem;

		}
		if (selectedItem != null) {
			JavaProjectFileWrapper value = selectedItem.getValue();
			// new EclipseJavaCompiler(value);
		}
	}

}
