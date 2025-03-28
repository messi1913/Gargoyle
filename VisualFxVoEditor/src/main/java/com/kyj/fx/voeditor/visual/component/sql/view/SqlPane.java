/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.view
 *	작성일   : 2015. 11. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.view;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.SystemUtils;
import org.controlsfx.control.CheckComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.kyj.fx.voeditor.visual.component.ResultDialog;
import com.kyj.fx.voeditor.visual.component.TitledBorderPane;
import com.kyj.fx.voeditor.visual.component.dock.pane.DockPane;
import com.kyj.fx.voeditor.visual.component.grid.EditableTableView.ColumnExpression;
import com.kyj.fx.voeditor.visual.component.grid.EditableTableView.ValueExpression;
import com.kyj.fx.voeditor.visual.component.grid.EditableTableViewComposite;
import com.kyj.fx.voeditor.visual.component.macro.MacroControl;
import com.kyj.fx.voeditor.visual.component.popup.TableOpenResourceView;
import com.kyj.fx.voeditor.visual.component.popup.VariableMappingView;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.ProcedureColumnsTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.ProcedureItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.TableItemTree;
import com.kyj.fx.voeditor.visual.component.sql.functions.ConnectionSupplier;
import com.kyj.fx.voeditor.visual.component.sql.functions.ISchemaTreeItem;
import com.kyj.fx.voeditor.visual.component.sql.functions.SQLPaneMotionable;
import com.kyj.fx.voeditor.visual.component.sql.tab.SqlTab;
import com.kyj.fx.voeditor.visual.component.sql.tab.SqlTabPane;
import com.kyj.fx.voeditor.visual.component.text.ASTSqlCodeAreaHelper;
import com.kyj.fx.voeditor.visual.component.text.SimpleTextView;
import com.kyj.fx.voeditor.visual.component.text.SqlKeywords;
import com.kyj.fx.voeditor.visual.framework.BigDataDVO;
import com.kyj.fx.voeditor.visual.functions.ToExcelFileFunction;
import com.kyj.fx.voeditor.visual.main.Main;
import com.kyj.fx.voeditor.visual.main.layout.GagoyleTabProxy;
import com.kyj.fx.voeditor.visual.main.layout.SchoolMgrerSpreadSheetView;
import com.kyj.fx.voeditor.visual.main.layout.SystemLayoutViewController;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;
import com.kyj.fx.voeditor.visual.util.DateUtil;
import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.DialogUtil.CustomInputDialogAction;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.FxCollectors;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.utils.EncrypUtil;
import com.sun.btrace.BTraceUtils.Strings;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
import javafx.util.StringConverter;
import jfxtras.scene.layout.GridPane;

/**
 * 전체적인 뷰 레이아웃 및 레이아웃과 관련된 행위들을 정의함.
 *
 *
 * 2016-10-27 구성되는 컴포넌트가 DockNode에서 BorderPane + SplitPane 기반으로 바꿈.
 *
 * @author KYJ
 *
 */
public abstract class SqlPane<T, K> extends BorderPane implements ISchemaTreeItem<T, K>, SQLPaneMotionable<K> {

	/**
	 * MSG
	 */
	private static final String FILE_OVERWIRTE_MESSAGE = "파일이 이미 존재합니다. 덮어씌우시겠습니까? ";
	private static Logger LOGGER = LoggerFactory.getLogger(SqlPane.class);
	/**
	 * 스키마 구조를 보여준다.
	 */
	protected TreeView<K> schemaTree;
	/**
	 * SQL을 작성하는곳
	 */
	// private SqlKeywords txtSql;
	/**
	 * SQL 조회 결과가 나타난다.
	 */
	private TableView<Map<String, Object>> tbResult;
	private EditableTableViewComposite editableComposite;

	private TableColumn<Map<String, Object>, Object> tcSelectRow;

	/**
	 * 상태메세지를 보여본다.
	 */
	private Label lblStatus;
	private TitledBorderPane sqlEditPane;
	private SqlTabPane sqlTabPane;
	private SqlTab sqlTab;

	private TabPane tabPaneResult;
	private Tab tabResult, tabEdit;

	private CheckComboBox<ReadType> readTypeCheckComboBox;

	private String url;
	private String driver;
	private String username;
	private String password;
	private Color userColor = null;

	/**
	 * 시스템에서 로드할 탭로더 프록시 객체
	 */
	private GagoyleTabProxy tabProxy;

	private Stage stage;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	IntegerProperty startRowIndexProperty = new SimpleIntegerProperty();
	IntegerProperty endRowIndexProperty = new SimpleIntegerProperty();
	IntegerProperty startColIndexProperty = new SimpleIntegerProperty();
	IntegerProperty endColIndexProperty = new SimpleIntegerProperty();

	/**
	 * 데이터베이스 sql 함수 리스트
	 *
	 * @최초생성일 2016. 11. 28.
	 */
	// private ObjectProperty<List<String>> sqlFunctions = new
	// SimpleObjectProperty<>(Collections.emptyList());

	public void setTitle(String title) {
		// sqlEditPane.setTitle(title);
		sqlEditPane.setTitle(title);
	}

	/**
	 * 데이터베이스와 접속한 커넥션을 반환해준다.
	 */
	// public Supplier<Connection> connectionSupplier = () -> {
	// Connection con = null;
	// try {
	//
	// if (url == null || username == null || password == null) {
	// con = DbUtil.getConnection();
	// } else {
	// con = DbUtil.getConnection(driver, url, username, password);
	// }
	// // Class.forName(driver);
	// // con = DriverManager.getConnection(url, username, password);
	// } catch (Exception e)
	//
	// {
	// lblStatus.setText(e.getMessage());
	// LOGGER.error(ValueUtil.toString(e));
	// }
	// return con;
	//
	// };

	public ConnectionSupplier connectionSupplier = new ConnectionSupplier() {
		@Override
		public String getUrl() {
			return url;
		}

		@Override
		public String getUsername() {
			return username;
		}

		@Override
		public String getPassword() {
			return password;
		}

		@Override
		public String getDriver() {
			return driver;
		}
	};

	public String getDriver() {
		return driver;
	}

	private static class ReadType {
		/**
		 * ResourcesLoader에 등록할 키를 기입한다.
		 *
		 * @최초생성일 2016. 2. 11.
		 */
		private String id;
		/**
		 * UI에 Diplay이름
		 *
		 * @최초생성일 2016. 2. 11.
		 */
		private String name;

		/**
		 * @param id
		 *            ResourcesLoader에 등록할 키
		 * @param name
		 *            UI에 Diplay이름
		 */
		public ReadType(String id, String name) {
			super();
			this.id = id;
			this.name = name;
		}

		/**
		 * @return the id
		 */
		public String getId() {
			return id;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ReadType other = (ReadType) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}

	}

	protected List<ReadType> defaultCheckedReadTypes() {
		ObservableList<ReadType> observableArrayList = FXCollections.observableArrayList();
		String v1 = ResourceLoader.getInstance().get(ResourceLoader.SKIP_BIG_DATA_COLUMN);
		String v2 = ResourceLoader.getInstance().get(ResourceLoader.APPLY_MAX_ROW_COUNT);

		if ("true".equals(v1))
			observableArrayList.add(new ReadType(ResourceLoader.SKIP_BIG_DATA_COLUMN, "빅데이터 컬럼 생략"));
		if ("true".equals(v2))
			observableArrayList.add(new ReadType(ResourceLoader.APPLY_MAX_ROW_COUNT, "Max Row 처리 "));
		return observableArrayList;
	}

	protected ObservableList<ReadType> defaultReadTypeItems() {
		ObservableList<ReadType> observableArrayList = FXCollections.observableArrayList();
		observableArrayList.add(new ReadType(ResourceLoader.SKIP_BIG_DATA_COLUMN, "빅데이터 컬럼 생략"));
		observableArrayList.add(new ReadType(ResourceLoader.APPLY_MAX_ROW_COUNT, "Max Row 처리 "));
		return observableArrayList;
	}

	protected void defaultSelectedItems() {
		defaultCheckedReadTypes().forEach(vo -> {
			readTypeCheckComboBox.getCheckModel().check(vo);

		});
	}

	/**
	 * 생성자
	 *
	 * @param t
	 */
	public SqlPane(T t) {
		// 2016.2.11 적용안됨. Stage를 생성하는 시점에 Scene에 적용해야 적용됨.
		// this.getStylesheets().add(SkinManager.getInstance().getSkin());
		// String externalForm =
		// SqlPane.class.getResource(SQL_PANE_CSS).toExternalForm();
		// this.getStylesheets().add(externalForm);
		schemaTree = new TreeView<>();

		TitledBorderPane treeView = createDefaultDockNode(schemaTree, "Schema"); // new
																					// DockNode(schemaTree,
																					// "Schema");
		treeView.setMinWidth(300);
		// treeView.setClosable(false);

		/* [시작] SQL 입력영역 */
		BorderPane sqlEditLayout = new BorderPane();
		Button btnExec = new Button("Execute");
		btnExec.setTooltip(new Tooltip("[CTRL + ENTER]"));
		btnExec.setOnMouseClicked(this::btnExecOnClick);

		Button btnExecAll = new Button("Execute All");
		btnExecAll.setTooltip(new Tooltip("[F5]"));
		btnExecAll.setOnMouseClicked(this::btnExecAllOnClick);

		Button btnAddTab = new Button("New Tab...");
		btnAddTab.setOnMouseClicked(this::btnAddTabOnClick);

		readTypeCheckComboBox = new CheckComboBox<ReadType>(defaultReadTypeItems());
		readTypeCheckComboBox.setConverter(new StringConverter<ReadType>() {

			@Override
			public String toString(ReadType object) {
				return object.getName();
			}

			@Override
			public ReadType fromString(String string) {

				return null;
			}

		});

		readTypeCheckComboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<ReadType>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends ReadType> c) {
				if (c.next()) {
					readTypeCheckComboBox.getItems().forEach(v -> {
						String key = v.getId();
						if (readTypeCheckComboBox.getCheckModel().isChecked(v)) {
							ResourceLoader.getInstance().put(key, "true");
							LOGGER.debug(String.format("CHECK : %s value :%s", key, ResourceLoader.getInstance().get(key)));
						} else {
							ResourceLoader.getInstance().put(key, "false");
							LOGGER.debug(String.format("UNCHECK : %s value :%s", key, ResourceLoader.getInstance().get(key)));
						}
					});
				}
			}
		});

		HBox btnBox = new HBox(5, btnExec, btnExecAll, btnAddTab, readTypeCheckComboBox);
		btnBox.setPrefWidth(Double.MAX_VALUE);
		btnBox.setMaxWidth(Double.MAX_VALUE);
		btnExec.getStyleClass().add("button-gargoyle");
		btnExecAll.getStyleClass().add("button-gargoyle");
		btnAddTab.getStyleClass().add("button-gargoyle");
		btnBox.getStyleClass().add("hbox-gargoyle");

		btnBox.setPadding(new Insets(5));
		sqlEditLayout.setTop(btnBox);

		defaultSelectedItems();

		sqlTab = createTabItem(null);
		sqlTabPane = new SqlTabPane(sqlTab);
		sqlEditLayout.setCenter(sqlTabPane);

		sqlEditPane = createDefaultDockNode(sqlEditLayout, "Sql"); // new
																	// DockNode(sqlEditLayout,
																	// "Sql");
		sqlEditPane.setPrefSize(100, 100);
		// sqlEditPane.setClosable(false);
		/* [끝] SQL 입력영역 */

		tbResult = new TableView<>();

		// Cell 단위로 선택
		tbResult.getSelectionModel().setCellSelectionEnabled(true);
		tbResult.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		// 키 이벤트 기능 설치.
		FxUtil.installClipboardKeyEvent(tbResult);
		Window window = FxUtil.getWindow(SqlPane.this);
		if (window == null)
			window = SharedMemory.getPrimaryStage();
		FxUtil.installFindKeyEvent(window, tbResult);

		BorderPane tbResultLayout = new BorderPane(tbResult);
		lblStatus = new Label("Ready...");
		lblStatus.setMaxHeight(50d);
		// tbResultLayout.setBottom(lblStatus);

		tbResult.getItems().addListener((ListChangeListener<Map<String, Object>>) arg0 -> {
			int size = arg0.getList().size();
			lblStatus.textProperty().set(size + " row");
		});

		{
			tcSelectRow = new TableColumn<>("↓");
			tcSelectRow.setMaxWidth(20);
			tcSelectRow.setSortable(false);
			tcSelectRow.setCellFactory(cell -> {
				return new DragSelectionCell();
			});
			// Table Select Drag 처리
			endRowIndexProperty.addListener(event -> tableSelectCell());
			endColIndexProperty.addListener(event -> tableSelectCell());
		}

		editableComposite = new EditableTableViewComposite(connectionSupplier);

		tabResult = new Tab("Result", tbResultLayout);
		tabResult.setClosable(false);

		tabEdit = new Tab("Edit", editableComposite);
		tabEdit.setClosable(false);

		tabPaneResult = new TabPane(tabResult, tabEdit);
		BorderPane borDataResult = new BorderPane(tabPaneResult);
		borDataResult.setBottom(lblStatus);
		TitledBorderPane sqlResultPane = createDefaultDockNode(borDataResult, "Result");// new
																						// DockNode(borDataResult,
																						// "Result");
		sqlResultPane.setMinHeight(200);

		SplitPane spCenter = new SplitPane(treeView, sqlEditPane);
		spCenter.setOrientation(Orientation.HORIZONTAL);

		SplitPane spRoot = new SplitPane(spCenter, sqlResultPane);
		spRoot.setOrientation(Orientation.VERTICAL);

		this.setCenter(spRoot);
		// this.setBottom(sqlResultPane);
		// this.setLeft(treeView);

		schemaTree.setRoot(apply(t, this.connectionSupplier));
		schemaTree.setOnMouseClicked(this::schemaTreeOnMouseClick);
		schemaTree.setOnKeyPressed(this::schemaTreeOnKeyClick);

		tbResult.setOnKeyPressed(this::tbResultOnKeyClick);

		createTreeContextMenu(schemaTree);
		createResultTableContextMenu(tbResult);
		// 2016-11-10 editableView에서도 컨텍스트 메뉴 추가.
		createResultTableContextMenu(editableComposite.getEditableTableView());

		Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
		DockPane.initializeDefaultUserAgentStylesheet();

		Main.addPrimaryStageCloseListener(() -> onPrimaryCloseRequest(null));

		// Sql 함수 로딩
		// try {
		// sqlFunctions.set(DbUtil.getSqlFunctions(connectionSupplier.get()));
		// } catch (Exception e) {
		// LOGGER.error(ValueUtil.toString(e));
		// }

		// bugfix. 아래처럼 primarystage set을 하게되면 등록된 모든 리스너가 사라짐.
		// SharedMemory.getPrimaryStage().setOnCloseRequest();
	}

	private TitledBorderPane createDefaultDockNode(Node node, String title) {
		TitledBorderPane n = new TitledBorderPane(title, node);
		// dockNode.setOwner(SharedMemory.getPrimaryStage());
		return n;

	}

	/********************************
	 * 작성일 : 2016. 6. 12. 작성자 : KYJ
	 *
	 * main stage가 닫힐때 본 팝업도 달힐 수 있게 한다.
	 *
	 * @param e
	 ********************************/
	public void onPrimaryCloseRequest(WindowEvent e) {
		if (stage != null)
			stage.close();
	}

	private void tableSelectCell() {
		int starRowtIndex = startRowIndexProperty.get();
		int endRowIndex = endRowIndexProperty.get();
		int starColtIndex = startColIndexProperty.get();
		int endColIndex = endColIndexProperty.get();

		TableViewSelectionModel<Map<String, Object>> selectionModel = tbResult.getSelectionModel();
		selectionModel.clearSelection();
		if (starColtIndex == 0) {
			selectionModel.selectRange(starRowtIndex, tbResult.getColumns().get(0), endRowIndex,
					tbResult.getColumns().get(tbResult.getColumns().size() - 1));
		} else {
			selectionModel.selectRange(starRowtIndex, tbResult.getColumns().get(starColtIndex), endRowIndex,
					tbResult.getColumns().get(endColIndex));
		}
	}

	public class DragSelectionCell extends TextFieldTableCell<Map<String, Object>, Object> {
		public DragSelectionCell() {
			this.setConverter(new StringConverter<Object>() {
				@Override
				public String toString(Object object) {
					return object == null ? "" : object.toString();
				}

				@Override
				public Object fromString(String string) {
					return null;
				}
			});
			setOnDragDetected(event -> {
				startFullDrag();
				startRowIndexProperty.setValue(getIndex());
				startColIndexProperty.setValue(tbResult.getColumns().indexOf(getTableColumn()));
			});
			setOnMouseDragEntered(event -> {
				endRowIndexProperty.setValue(getIndex());
				endColIndexProperty.setValue(tbResult.getColumns().indexOf(getTableColumn()));
				event.consume();
			});
			setOnMouseClicked(event -> {
				if (tbResult.getColumns().indexOf(getTableColumn()) == 0) {
					startColIndexProperty.setValue(0);
					startRowIndexProperty.setValue(getIndex());
					endRowIndexProperty.setValue(getIndex());
					tableSelectCell();
				}

				if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {

					Object item = DragSelectionCell.this.getItem();
					if (item instanceof BigDataDVO) {
						BigDataDVO dataDVO = (BigDataDVO) item;

						// {
						FxUtil.createStageAndShow(new SimpleTextView(dataDVO.getValue()), stage -> {
							stage.setAlwaysOnTop(true);
							stage.initModality(Modality.APPLICATION_MODAL);
							stage.initOwner(SqlPane.this.getScene().getWindow());
						});

						// new SimpleTextView(dataDVO.getValue()).show();
						// } catch (Exception e) {
						// LOGGER.error(ValueUtil.toString(e));
						// }
					} else if (item instanceof String) {
						FxUtil.createStageAndShow(new SimpleTextView(item.toString()), stage -> {
							stage.setAlwaysOnTop(true);
							stage.initModality(Modality.APPLICATION_MODAL);
							stage.initOwner(SqlPane.this.getScene().getWindow());
						});
					}

				}
			});

		}
	}

	/**
	 * 커넥션 정보 초기화처리
	 *
	 * @param map
	 */
	public void initialize(Map<String, Object> map) {
		try {
			this.url = map.get(ResourceLoader.BASE_KEY_JDBC_URL).toString();
			this.username = map.get(ResourceLoader.BASE_KEY_JDBC_ID) == null ? "" : map.get(ResourceLoader.BASE_KEY_JDBC_ID).toString();
			this.password = map.get(ResourceLoader.BASE_KEY_JDBC_PASS) == null ? ""
					: EncrypUtil.decryp(map.get(ResourceLoader.BASE_KEY_JDBC_PASS).toString());
			this.driver = map.get("driver").toString();

			this.userColor = map.get("color") == null ? null : Color.web(map.get("color").toString());

			String alias = map.get("alias") == null ? "" : map.get("alias").toString();
			if (ValueUtil.isEmpty(alias)) {
				this.setTitle(this.url);
			} else {
				this.setTitle(String.format("%s - %s", alias, this.url));
			}

			if (ValueUtil.isNotEmpty(userColor)) {
				// String backGroundColor = String.format("#%02X%02X%02X", (int)
				// (userColor.getRed() * 255),
				// (int) (userColor.getGreen() * 255), (int)
				// (userColor.getBlue() * 255));
				// String textColor = String.format("#%02X%02X%02X", 255 - (int)
				// (userColor.getRed() * 255),
				// 255 - (int) (userColor.getGreen() * 255), 255 - (int)
				// (userColor.getBlue() * 255));
				String textColor = FxUtil.toWebString(userColor);
				String backGroundColor = FxUtil.toWebString(userColor);

				sqlEditPane.getTitleLabel().setStyle("-fx-text-fill:" + textColor);
				sqlEditPane.getTitleLabel().getGraphic().setStyle("-fx-background-color:" + backGroundColor);

			}

			// ConnectionSupplier 생성
			connectionSupplier = new ConnectionSupplier(map);
		} catch (Exception e) {
			DialogUtil.showExceptionDailog(this, e, "초기화 실패....");
		}

	}

	private void createResultTableContextMenu(TableView<?> tableView) {

		/**/
		MenuItem menuExportExcel = new MenuItem("Export Excel File");
		menuExportExcel.setOnAction(this::menuExportExcelOnAction);

		MenuItem menuExportSpreadSheet = new MenuItem("Export SpreadSheet");
		menuExportSpreadSheet.setOnAction(this::menuExportSpreadSheetOnAction);

		MenuItem menuExportInsertScript = new MenuItem("Export Insert Script");
		menuExportInsertScript.setOnAction(this::menuExportInsertScriptOnAction);

		MenuItem menuExportMergeScript = new MenuItem("Export Merge Script");
		menuExportMergeScript.setOnAction(this::menuExportMergeScriptOnAction);

		MenuItem menuExportJson = new MenuItem("Export Json");
		menuExportJson.setOnAction(this::menuExportJsonOnAction);

		MenuItem menuExportHtml = new MenuItem("Export HTML");
		menuExportHtml.setOnAction(this::menuExportHtmlOnAction);

		Menu menuExportExcelFile = new Menu("Export", null, menuExportExcel, menuExportSpreadSheet, menuExportInsertScript,
				menuExportMergeScript, menuExportJson, menuExportHtml);
		/**/

		MenuItem rowCopy = new MenuItem("row copy");
		rowCopy.setOnAction(this::rowCopyOnAction);

		ContextMenu contextMenu = new ContextMenu(menuExportExcelFile, rowCopy);
		tableView.setContextMenu(contextMenu);
	}

	/**
	 * 컨텍스트 메뉴 생성 및 기능 적용
	 *
	 *
	 * 2016-10-27 키 이벤트를 setAccelerator를 사용하지않고 이벤트 방식으로 변경 이유 : 도킹기능을 적용하하면 setAccelerator에 등록된 이벤트가 호출안됨
	 * 
	 *
	 * @param schemaTree2
	 */
	private void createTreeContextMenu(TreeView<K> schemaTree) {

		MenuItem menuSelectScript = new MenuItem("Select Script");
		menuSelectScript.setOnAction(this::applySelectScript);

		MenuItem menuUpdateScript = new MenuItem("Update Script");
		menuUpdateScript.setOnAction(this::applyUpdateScript);

		MenuItem menuDeleteScript = new MenuItem("Delete Script");
		menuDeleteScript.setOnAction(this::applyDeleteSelectScript);

		MenuItem menuInsertScript = new MenuItem("Insert Script");
		menuInsertScript.setOnAction(this::applyInsertScript);

		Menu menu = new Menu("Script", null, menuSelectScript, menuUpdateScript, menuDeleteScript, menuInsertScript);

		// MenuItem menuPrimaryKeys = new MenuItem("Primary Keys");

		MenuItem menuShowData = new MenuItem("Show 100 rows [F1]");
		menuShowData.setOnAction(this::show100RowAction);

		MenuItem menuEditShowData = new MenuItem("Show Editable View  [CTRL + F1]");
		menuEditShowData.setOnAction(this::menuEditShowDataAction);

		MenuItem menuFindTable = new MenuItem("Find Table [CTRL + SHIFT + R]");
		// menuFindTable.setAccelerator(new KeyCodeCombination(KeyCode.R,
		// KeyCombination.CONTROL_DOWN));
		menuFindTable.setOnAction(this::showFileTableOnAction);

		MenuItem menuProperties = new MenuItem("Properties");
		menuProperties.setOnAction(this::showProperties);

		MenuItem menuReflesh = new MenuItem("Reflesh [F5]");
		// menuReflesh.setOnAction(this::menuRefleshOnAction);
		menuReflesh.setAccelerator(new KeyCodeCombination(KeyCode.F5));

		MenuItem menuFindProcedure = new MenuItem("Find Procedure");
		menuFindProcedure.setOnAction(this::menuFindProcedureOnAction);

		MenuItem menuReadProcedure = new MenuItem("Read Procedure");
		menuReadProcedure.setOnAction(this::menuReadProcedureOnAction);

		MenuItem menuExecuteProcedure = new MenuItem("Execute Procedure");
		menuExecuteProcedure.setOnAction(this::menuExecuteProcedure);

		ContextMenu contextMenu = new ContextMenu(menu, menuShowData, menuEditShowData, menuFindTable, menuFindProcedure, menuReadProcedure,
				menuExecuteProcedure, menuProperties, new SeparatorMenuItem(), menuReflesh);
		schemaTree.setContextMenu(contextMenu);

	}

	/**
	 * insert문 을 화면에 보여줌.
	 *
	 * @param e
	 */
	public void applyInsertScript(ActionEvent e) {
		TreeItem<K> selectedTableTreeItem = schemaTree.getSelectionModel().getSelectedItem();
		String tableName = this.getSelectedTreeByTableName(selectedTableTreeItem);
		// TreeItem<K> schemaTreeItem = selectedTableTreeItem.getParent();
		// String schema = schemaTreeItem.getValue().toString();

		List<String> tableColumns = this.getSelectedTreeByTableColumns(selectedTableTreeItem);
		SqlTab selectedTab = getSelectedSqlTab();

		StringBuffer sqlColumns = new StringBuffer();
		StringBuffer sqlValues = new StringBuffer();
		if (tableColumns != null && !tableColumns.isEmpty()) {
			for (String col : tableColumns) {
				sqlColumns.append(String.format("'%s' ,", col));
				sqlValues.append(String.format("'%s' ,", ValueUtil.toCamelCase(col)));
			}
			sqlColumns.setLength(sqlColumns.length() - 1);
			sqlValues.setLength(sqlValues.length() - 1);
		}

		selectedTab.appendTextSql(
				String.format("insert into %s ( %s ) \nvalues ( %s )", tableName, sqlColumns.toString(), sqlValues.toString()));
	}

	/**
	 * delete문장을 화면에 보여줌.
	 *
	 * @param e
	 */
	public void applyDeleteSelectScript(ActionEvent e) {
		TreeItem<K> selectedItem = schemaTree.getSelectionModel().getSelectedItem();
		String tableName = this.getSelectedTreeByTableName(selectedItem);
		List<String> primaryKeys = this.getSelectedTreeByPrimaryKey(selectedItem);

		SqlTab selectedTab = getSelectedSqlTab();

		// default is primarykeys...
		StringBuffer whereStatement = new StringBuffer("1=1 ");
		if (primaryKeys != null && !primaryKeys.isEmpty()) {
			for (String col : primaryKeys) {
				whereStatement.append(String.format("\nand %s = :%s ", col, ValueUtil.toCamelCase(col)));
			}
			whereStatement.setLength(whereStatement.length() - 1);
		}

		selectedTab.appendTextSql(String.format("delete from %s \nwhere %s", tableName, whereStatement.toString()));
	}

	/**
	 * update문장을 화면에 보여줌.
	 *
	 * @param e
	 */
	public void applyUpdateScript(ActionEvent e) {
		TreeItem<K> selectedItem = schemaTree.getSelectionModel().getSelectedItem();
		String tableName = this.getSelectedTreeByTableName(selectedItem);
		List<String> primaryKeys = this.getSelectedTreeByPrimaryKey(selectedItem);
		List<String> columns = this.getSelectedTreeByTableColumns(selectedItem);
		SqlTab selectedTab = getSelectedSqlTab();

		// set statement
		StringBuffer setStatement = new StringBuffer();
		if (columns != null && !columns.isEmpty()) {
			for (String col : columns) {
				setStatement.append(String.format("\n %s = :%s ,", col, ValueUtil.toCamelCase(col)));
			}
			setStatement.setLength(setStatement.length() - 1);
		}

		// where statement
		StringBuffer whereStatement = new StringBuffer();
		if (primaryKeys != null && !primaryKeys.isEmpty()) {
			for (String col : primaryKeys) {
				whereStatement.append(String.format("\nand %s = :%s ", col, ValueUtil.toCamelCase(col)));
			}
			whereStatement.setLength(whereStatement.length() - 1);
		}

		selectedTab.appendTextSql(
				String.format("update %s \nset %s \nwhere  %s", tableName, setStatement.toString(), whereStatement.toString()));
	}

	/**
	 * 테이블의 SELECT문을 리턴.
	 *
	 * @param e
	 */
	public void applySelectScript(ActionEvent e) {
		TreeItem<K> selectedItem = schemaTree.getSelectionModel().getSelectedItem();

		String tableName = this.getSelectedTreeByTableName(selectedItem);
		SqlTab selectedTab = getSelectedSqlTab();
		TreeItem<K> schemaTreeItem = selectedItem.getParent();
		String schema = schemaTreeItem.getValue().toString();

		try (Connection connection = connectionSupplier.get()) {

			List<String> columns = DbUtil.columns(connection, tableName);
			if (columns == null || columns.isEmpty()) {
				String driver = DbUtil.getDriverNameByConnection(connection);
				String sql = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.SQL_TABLE_COLUMNS_WRAPPER, driver);

				Map<String, Object> map = new HashMap<>(2);
				map.put("databaseName", schema);
				map.put("tableName", tableName);

				sql = ValueUtil.getVelocityToText(sql, map, true);
				columns = DbUtil.selectBeans(connection, sql, 10, (RowMapper<String>) (rs, rowNum) -> rs.getString(1));
			}

			redueceAction(columns, ",\n",
					v -> selectedTab.appendTextSql(String.format("select\n%s \nfrom %s ", v.substring(0, v.length()), tableName)));

		} catch (Exception e1) {
			LOGGER.error(ValueUtil.toString(e1));
		}

	}

	/**
	 * 문자열을 합친후 action의 내용을 실행함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 30.
	 * @param list
	 * @param delim
	 * @param consumer
	 */
	public void redueceAction(List<String> list, String delim, Consumer<String> action) {
		Optional<String> reduce = list.stream().reduce((t, u) -> t.concat(delim).concat(u));
		reduce.ifPresent(action);
	}

	public SqlTab getSelectedSqlTab() {
		return sqlTabPane.getSelectedTab();
	}

	public TreeView<K> getSchemaTree() {
		return schemaTree;
	}

	protected ASTSqlCodeAreaHelper installASTSqlCodeAreaHelper(SqlKeywords keyword) {
		return new ASTSqlCodeAreaHelper(keyword.getCodeArea(), connectionSupplier);
	}

	/**
	 * 탭추가.
	 *
	 * 2016-12-09 CodeAssistHelper 기능 추가.
	 *
	 * 2016-10-27 키 이벤트를 setAccelerator를 사용하지않고 이벤트 방식으로 변경 이유 : 도킹기능을 적용하하면 setAccelerator에 등록된 이벤트가 호출안됨
	 *
	 * @return
	 */
	SqlTab createTabItem(String text) {
		SqlTab sqlTab = new SqlTab(this::txtSqlOnKeyEvent);
		if (text != null)
			sqlTab.setText(text);
		SqlKeywords sqlNode = sqlTab.getSqlNode();

		// TODO
		// 코드 AreaHelper 설치.
		// new ASTSqlCodeAreaHelper(sqlNode.getCodeArea(), connectionSupplier);
		installASTSqlCodeAreaHelper(sqlNode);

		ContextMenu contextMenu = sqlTab.getTxtSqlPaneContextMenu();
		Menu menuFunc = new Menu("Functions");

		MenuItem menuQueryMacro = new MenuItem("Query-Macro");
		menuQueryMacro.setOnAction(this::menuQueryMacroOnAction);

		MenuItem menuFormatter = new MenuItem("SQL Formatter [F + CTRL + SHIFT] ");
		// 2016-10-27 주석
		// menuFormatter.setAccelerator(new KeyCodeCombination(KeyCode.F,
		// KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN));
		menuFormatter.setOnAction(this::menuFormatterOnAction);

		MenuItem menuShowApplicationCode = new MenuItem("Show Application Code [Java]");
		menuShowApplicationCode.setOnAction(this::menuShowJavaApplicationCodeOnAction);

		MenuItem menuShowDotNetApplicationCode = new MenuItem("Show Application Code [C#]");
		menuShowDotNetApplicationCode.setOnAction(this::menuShowDotNetApplicationCodeOnAction);

		menuFunc.getItems().addAll(menuQueryMacro, menuFormatter, menuShowApplicationCode, menuShowDotNetApplicationCode);

		contextMenu.getItems().add(menuFunc);
		return sqlTab;
	}

	/**
	 * query-macro menu 선택시 발생되는 이벤트에 대한정의.
	 *
	 * 기술내용으로는 쿼리 매크로를 처리할 수 있는 팝업을 로드한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 31.
	 * @param e
	 */
	public void menuQueryMacroOnAction(ActionEvent e) {
		String selectedSQLText = getSelectedSqlTab().getSelectedSQLText();
		MacroControl macroControl = new MacroControl(connectionSupplier, selectedSQLText);
		FxUtil.createStageAndShow(macroControl, stage -> {
			stage.setTitle("Query-Macro");
			stage.initOwner(SqlPane.this.getScene().getWindow());
			// 팝업창을 닫는 요청이 들어온경우 stop()함수를 호출하고 종료
			stage.setOnCloseRequest(ev -> {

				LOGGER.debug("Stop Action Result :stopReuqest ");
				macroControl.stop();

			});
		});

	}

	/**
	 * Sql 포멧처리.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 9. 23.
	 * @param e
	 */
	public void menuFormatterOnAction(ActionEvent e) {
		SqlKeywords sqlNode = this.sqlTab.getSqlNode();
		sqlNode.doSqlFormat();
	}

	/**
	 * Application Code를 보여주는 팝업 </br>
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 9. 23.
	 * @param e
	 */
	public void menuShowJavaApplicationCodeOnAction(ActionEvent e) {
		FxUtil.EasyFxUtils.showJavaApplicationCode(this.sqlTab.getSelectedSQLText());
	}

	/**
	 * Application Code를 보여주는 팝업 </br>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 2.
	 * @param e
	 */
	public void menuShowDotNetApplicationCodeOnAction(ActionEvent e) {
		FxUtil.EasyFxUtils.showDotNetApplicationCode(this.sqlTab.getSelectedSQLText());
	}

	/**
	 * SQL 실행 이벤트
	 *
	 * @param e
	 */
	public void btnExecOnClick(MouseEvent e) {
		execute();
	}

	public void btnExecAllOnClick(MouseEvent e) {
		executeAll();
	}

	/**
	 * 탭추가 이벤트
	 *
	 * @param e
	 */
	public void btnAddTabOnClick(MouseEvent e) {
		newTab(true);
	}

	private SqlTab newTab(boolean select) {
		return newTab(select, null);
	}

	/**
	 * 새로운 탭 생성
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 27.
	 * @param select
	 * @return
	 */
	protected SqlTab newTab(boolean select, String text) {
		SqlTab createTabItem = createTabItem(text);
		sqlTabPane.getTabs().add(createTabItem);
		if (select)
			sqlTabPane.getSelectionModel().selectLast();
		return createTabItem;
	}

	/********************************
	 * 작성일 : 2016. 5. 1. 작성자 : KYJ
	 *
	 * SQL에디터에서 키 이벤트 처리
	 *
	 * @param e
	 ********************************/
	public void txtSqlOnKeyEvent(KeyEvent e) {

		/* CTRL + ENTER */
		// case ENTER:
		if ((e.getCode() == KeyCode.ENTER) && e.isControlDown() && !e.isAltDown() && !e.isShiftDown()) {
			if (e.isConsumed())
				return;
			execute();
			e.consume();
		} else if ((e.getCode() == KeyCode.ENTER) && e.isAltDown()) {
			if (e.isConsumed())
				return;
			try {
				String selectedSQLText = sqlTab.getSelectedSQLText().trim();
				// Connection connection = connectionSupplier.get();
				// String driverName =
				// DbUtil.getDriverNameByConnection(connection);
				// String dbmsName = ValueUtil.getDriverToDBMSName(driverName);
				showProperties(connectionSupplier, /* catalog */null, /* schemaName */null, selectedSQLText);
			} catch (Exception e1) {
				LOGGER.error(ValueUtil.toString(e1));
			}
			e.consume();
		}

		// break;
		/* CTRL + P (Properties) */
		// case P:
		else if ((e.getCode() == KeyCode.P) && e.isControlDown() && !e.isAltDown() && !e.isShiftDown()) {
			if (e.isConsumed())
				return;

			String selectedSQLText = getSelectedSqlTab().getSelectedSQLText();
			TreeItem<K> selectedItem = schemaTree.getSelectionModel().getSelectedItem();

			String selectedSchemName = getSchemaName(selectedItem);
			String catalogName = getCatalogName(selectedItem);
			if (selectedSchemName != null && selectedSQLText != null) {
				showProperties(connectionSupplier, catalogName, selectedSchemName, selectedSQLText);
				e.consume();
			}
		}

		else if (KeyCode.F1 == e.getCode()) {
			if (e.isConsumed())
				return;

			String selectedSQLText = getSelectedSqlTab().getSelectedSQLText();
			List<TreeItem<K>> searchPattern = searchPattern(null, selectedSQLText);
			if (ValueUtil.isNotEmpty(searchPattern)) {
				TreeItem<K> treeItem = searchPattern.get(0);
				schemaTree.getSelectionModel().select(treeItem);
				schemaTree.scrollTo(schemaTree.getSelectionModel().getSelectedIndex());
				show100RowAction();
				e.consume();
			}

		}

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 14.
	 */
	private void executeAll() {
		SqlTab selectedItem = sqlTabPane.getSelectedTab();

		String sql = selectedItem.getSqlText();
		if (sql == null || sql.isEmpty()) {
			sql = selectedItem.getSqlText();
		}

		if (sql == null || sql.isEmpty())
			return;

		String[] split = sql.split(";\n");
		executeAll(split);

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 14.
	 * @param split
	 */
	private void executeAll(String[] split) {

		List<String> asList = Arrays.asList(split);
		queryAll(asList, cnt -> {
			DialogUtil.showMessageDialog(String.format("%d 건 success", cnt));
		}, (e, bool) -> {
			if (bool)
				DialogUtil.showExceptionDailog(e);
		});

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 14.
	 * @param _sql
	 * @param dynamicParams
	 */
	private void executeAll(String[] _sql, Map<String, Object> dynamicParams) {
		DialogUtil.showMessageDialog("[ Execute ALL ] Dynamic SQL Not Yet Support.");
	}

	private void execute() {

		SqlTab selectedItem = sqlTabPane.getSelectedTab();

		String sql = selectedItem.getSelectedSQLText();
		if (sql == null || sql.isEmpty()) {
			sql = selectedItem.getSqlText();

			if (sql == null || sql.isEmpty())
				return;

		}

		String trimedSql = sql.trim();

		// 2016-09-03 오라클에서는 쿼리문장끝에 ';' 이 포함되면 안되므로 제거
		if (trimedSql.endsWith(";")) {
			trimedSql = trimedSql.substring(0, trimedSql.length() - 1);
		}
		String _sql = trimedSql;

		if (ValueUtil.isVelocityContext(_sql)) {
			VariableMappingView mappingView = new VariableMappingView(stage);
			mappingView.extractVariableFromSql(_sql);
			mappingView.showAndWait(dynamicParams -> {
				execute(_sql, dynamicParams);
			});
		} else
			execute(_sql);

	}

	/**
	 * 쿼리 실행후 결과를 그리드에 바인딩
	 *
	 * @param sql
	 */
	protected void execute(String sql) {
		execute(sql, Collections.emptyMap());
	}

	/**
	 * 쿼리 실행후 결과를 그리드에 바인딩
	 *
	 * @param sql
	 * @param param
	 *            다이나믹 변수
	 */
	protected void execute(String sql, Map<String, Object> param) {

		if (ValueUtil.isEditScript(sql)) {
			execiteEdit(sql);
		} else {

			tbResult.getColumns().clear();
			tbResult.getItems().clear();

			List<Map<String, Object>> query = query(sql, param, success -> {
				lblStatus.setText(success.size() + " row");
			}, (exception, showDialog) -> {
				lblStatus.setText(exception.toString());
				if (showDialog)
					DialogUtil.showExceptionDailog(this, exception);
			});

			updateResultUi(query);
		}
	}

	public void updateResultUi(List<Map<String, Object>> data) {

		if (data.isEmpty()) {
			return;
		}

		binding(data);
		tbResult.getItems().addAll(data);
		tabPaneResult.getSelectionModel().select(this.tabResult);
	}

	protected void execiteEdit(String sql) {
		// editableComposite.getColumns().clear();
		editableComposite.clear();

		editableComposite.setSql(sql);

		try {
			lblStatus.setText(editableComposite.execute() + " row");
			tabPaneResult.getSelectionModel().select(this.tabEdit);
		} catch (Exception e) {
			DialogUtil.showExceptionDailog(this, e);
		}
	}

	/**
	 * 결과 데이터를 테이블에 바인딩
	 *
	 * @param query
	 */
	private void binding(List<Map<String, Object>> query) {
		Map<String, Object> map = query.get(0);
		Iterator<String> iterator = map.keySet().iterator();
		tbResult.getColumns().add(tcSelectRow);
		while (iterator.hasNext()) {
			String column = iterator.next();
			TableColumn<Map<String, Object>, Object> e = new TableColumn<>(column);
			e.setCellFactory(arg -> {
				return new DragSelectionCell();
			});
			e.setCellValueFactory(arg -> {
				Object value = arg.getValue().get(column);
				return new SimpleObjectProperty<>(value);
			});
			e.setComparator((a, b) -> {

				if (a == null && b == null)
					return 0;

				if (a == null)
					return -1;

				if (b == null)
					return -1;

				if (ValueUtil.isNumber(a.toString()) && ValueUtil.isNumber(b.toString())) {
					return Double.compare(Double.parseDouble(a.toString()), Double.parseDouble(b.toString()));
				} else {
					return Strings.compareTo(a.toString(), b.toString());
				}

			});
			tbResult.getColumns().add(e);
		}
	}

	public Color getUserColor() {
		return userColor;
	}

	/**
	 * Excel Export.
	 *
	 * @param e
	 */
	public void menuExportExcelOnAction(ActionEvent e) {

		File saveFile = DialogUtil.showFileSaveDialog(SharedMemory.getPrimaryStage().getOwner(), option -> {
			option.setInitialFileName(DateUtil.getCurrentDateString(DateUtil.SYSTEM_DATEFORMAT_YYYYMMDDHHMMSS));
			option.getExtensionFilters().add(new ExtensionFilter("Excel files (*.xlsx)", "*.xlsx"));
			option.getExtensionFilters().add(new ExtensionFilter("Excel files (*.xls)", "*.xls"));
			option.getExtensionFilters().add(new ExtensionFilter("All files", "*.*"));
			option.setTitle("Save Excel");
			option.setInitialDirectory(new File(SystemUtils.USER_HOME));
		});

		if (saveFile == null) {
			return;
		}

		if (saveFile.exists()) {
			Optional<Pair<String, String>> showYesOrNoDialog = DialogUtil.showYesOrNoDialog("overwrite ?? ", FILE_OVERWIRTE_MESSAGE);
			showYesOrNoDialog.ifPresent(consume -> {
				String key = consume.getKey();
				String value = consume.getValue();

				if (!("RESULT".equals(key) && "Y".equals(value))) {
					return;
				}
			});

		}

		ObservableList<Map<String, Object>> items = this.tbResult.getItems();
		ToExcelFileFunction toExcelFileFunction = new ToExcelFileFunction();
		// Bug Fix
		List<String> columns = this.tbResult.getColumns().stream().skip(1).map(col -> col.getText()).collect(Collectors.toList());
		toExcelFileFunction.generate0(saveFile, columns, items);

		if (saveFile.exists()) {
			FileUtil.openFile(saveFile);
		}
		// DialogUtil.showMessageDialog("complete...");

	}

	/**
	 * 
	 * 새로운 로우를 추가하지만 선택된 행에 대산 값들도 복사한다.
	 * 
	 * 
	 * #### #### 값을 새롭게 추가한다음에 그 값에대한 값을 변경하는 형태로 로직구성이되야한다.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 7. 16.
	 * @param e
	 */
	public void rowCopyOnAction(ActionEvent e) {

		TableViewSelectionModel<Map<ColumnExpression, ObjectProperty<ValueExpression>>> selectionModel = this.editableComposite
				.getSelectionModel();
		Map<ColumnExpression, ObjectProperty<ValueExpression>> selectedItem = selectionModel.getSelectedItem();
		if (selectedItem != null && !selectedItem.isEmpty()) {

			Map<ColumnExpression, ObjectProperty<ValueExpression>> newVal = new HashMap<>();

			// 값을 새롭게 추가한다음에 그 값에대한 값을 변경하는 형태로 로직구성이되야한다.
			this.editableComposite.getItems().add(newVal);

			// 값 복사
			{
				for (ObjectProperty<ValueExpression> value : selectedItem.values()) {
					try {
						ValueExpression cloneValueExp = value.get().clone();
						cloneValueExp.setNew(true);
						newVal.put(cloneValueExp.getColumnExpression(), new SimpleObjectProperty<>(cloneValueExp));
					} catch (CloneNotSupportedException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Export JSON Script.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 13.
	 * @param e
	 */
	public void menuExportJsonOnAction(ActionEvent e) {

		ObservableList<Map<String, Object>> items = tbResult.getItems();
		if (items.isEmpty())
			return;

		Map<String, Object> map = items.get(0);
		if (map == null)
			return;

		// 클립보드 복사
		StringBuilder clip = new StringBuilder();

		List<String> valueList = items.stream().map(v -> {
			return ValueUtil.toJSONString(v);
		}).collect(Collectors.toList());

		valueList.forEach(str -> {
			clip.append(str).append(System.lineSeparator());
		});

		try {

			SimpleTextView parent = new SimpleTextView(clip.toString());
			parent.setWrapText(false);
			FxUtil.createStageAndShow(parent, stage -> {
			});
		} catch (Exception e1) {
			LOGGER.error(ValueUtil.toString(e1));
		}

	}

	/**
	 * Export HTML <br/>
	 * 
	 * TODO 템플릿 파일과 데이터를 결합하여 HTML을 보여주는 기능 구현 <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 4.
	 * @param e
	 */
	public void menuExportHtmlOnAction(ActionEvent e) {
		// TODO

		ObservableList<Map<String, Object>> items = this.tbResult.getItems();

		String template = ConfigResourceLoader.getInstance().get("default.database.view.template");

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("data", items);

		String velocityToText = ValueUtil.getVelocityToText(template, map, false);

		System.out.println(velocityToText);

		WebView v = new WebView();
		v.getEngine().loadContent(velocityToText);
		FxUtil.createStageAndShow(v, stage->{
			stage.setWidth(1200d);
			stage.setHeight(800d);
		});

	}

	public abstract void menuExportInsertScriptOnAction(ActionEvent e);

	/**
	 * export spreadSheet event
	 *
	 * @param e
	 */
	public void menuExportSpreadSheetOnAction(ActionEvent e) {
		// 탭을 로드할 수 있는 프록시 객체를 불러옴
		if (tabProxy == null) {
			tabProxy = GagoyleTabProxy.getInstance();
		}

		// 그리드 데이터를 copy

		StringBuffer columnBuf = new StringBuffer();
		StringBuffer dataBuf = new StringBuffer();
		List<Map<String, Object>> items = getTbResult().getItems();
		boolean isFirst = true;

		int colSize = 27;
		for (Map<String, Object> map : items) {

			Iterator<String> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				String column = iterator.next();
				if (isFirst)
					columnBuf.append(column).append('\t');

				dataBuf.append(map.get(column)).append('\t');
			}

			dataBuf.append('\n');
			if (isFirst) {
				isFirst = false;
				columnBuf.append('\n');
				colSize = map.size();
			}

		}

		if (colSize < 27)
			colSize = 27;

		// String putString = new
		// StringBuffer().append(columnBuf).append(dataBuf).toString();
		SchoolMgrerSpreadSheetView parent = new SchoolMgrerSpreadSheetView(colSize);
		// parent.paste(putString, 0, 0);
		parent.paste(items, 0, 0);
		tabProxy.loadNewSystemTab(SystemLayoutViewController.TAB_TITLE_SPREAD_SHEET, parent);
	}

	/**
	 * Merge문 스크립트 작성 추출.
	 *
	 * @작성자 : HJH
	 * @작성일 : 2016. 10. 21.
	 * @param e
	 */
	public abstract void menuExportMergeScriptOnAction(ActionEvent e);

	public TableView<Map<String, Object>> getTbResult() {
		return tbResult;
	}

	/* [시작][추상화 메소드 정의] */

	/**
	 * 100개의 데이터 보여주기 위해 처리하는 메소드
	 *
	 * @param e
	 */
	public void show100RowAction(ActionEvent e) {
		show100RowAction();
	}

	public void menuEditShowDataAction(ActionEvent e) {
		showEditableDataAction();
	}

	public void showFileTableOnAction(ActionEvent e) {
		showTableResourceView();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 25.
	 */
	public void showEditableDataAction() {
		// Default TableName
		TreeItem<K> selectedItem = getSchemaTree().getSelectionModel().getSelectedItem();
		if (null != selectedItem) {
			K value = selectedItem.getValue();
			if (value instanceof TableItemTree) {

				@SuppressWarnings("rawtypes")
				TableItemTree tableItemTree = (TableItemTree) value;
				String schemaName = tableItemTree.getParent().getName();
				String tableName = tableItemTree.getName();
				String sql = String.format("edit %s.%s", schemaName, tableName);
				execiteEdit(sql);
			}
		}
	}

	/**
	 * 테이블 속성 조회
	 *
	 * @param e
	 */
	public void showProperties(ActionEvent e) {
		TreeItem<K> selectedItem = schemaTree.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			K value = selectedItem.getValue();
			showProperties(connectionSupplier, value);
		}
	}
	

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 12.
	 * @param e
	 */
	public void menuFindProcedureOnAction(ActionEvent e) {
		if (!e.isConsumed()) {
			e.consume();

			Optional<Pair<String, String>> showInputDialog = DialogUtil.showInputDialog(FxUtil.getWindow(this), "Find Procedure",
					"Input Procedure Name");
			showInputDialog.ifPresent(c -> {
				String srchKey = c.getValue();
				String rslt = findProcedure(srchKey);
				if (ValueUtil.isNotEmpty(rslt)) {
					getSelectedSqlTab().appendTextSql(rslt);
				}
			});

		}
	}

	/**
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 27.
	 * @param e
	 */
	public void menuExecuteProcedure(ActionEvent e) {

		/*
		 * 필요시 오버라이딩하여 구현할것. 17.12.24 현재 MssqlPane에서만 구현됨.
		 */
	}

	/**
	 * 프로시저 내용을 읽어들인다. <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 27.
	 * @param e
	 */
	public void menuReadProcedureOnAction(ActionEvent e) {
		if (e.isConsumed()) {
			return;
		}

		TreeItem<K> selectedItem = this.schemaTree.getSelectionModel().getSelectedItem();
		if (selectedItem == null || selectedItem.getValue() == null)
			return;

		K value = selectedItem.getValue();

		Class<? extends Object> cls = value.getClass();
		boolean check = ProcedureItemTree.class.isAssignableFrom(cls) && !ProcedureColumnsTree.class.isAssignableFrom(cls);
		if (check) {
			ProcedureItemTree<T> tmp = (ProcedureItemTree<T>) value;
			String readProcedureContent = tmp.readProcedureContent();
			SqlTab newTab = newTab(true, tmp.getProcedureName());
			newTab.appendTextSql(readProcedureContent);
		}

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 12.
	 */
	public void showFindProcedure() {
		menuFindProcedureOnAction(new ActionEvent());
	}

	/**
	 * find procedure.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 12.
	 * @param srchKey
	 * @return
	 */
	public String findProcedure(String srchKey) {
		try (Connection connection = connectionSupplier.get()) {

			List<String> rslts = DbUtil.findProcedure(connection, srchKey);
			Optional<String> reduce = rslts.stream().reduce((str1, str2) -> {
				return str1.concat("\n").concat(str2);
			});
			if (reduce.isPresent()) {
				return reduce.get();
			}
		} catch (SQLException e) {
			LOGGER.error(ValueUtil.toString(e));
		}
		return null;
	}

	/**
	 * 검색 추상함수
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 20.
	 * @param schema
	 * @param databaseName
	 * @param tableName
	 * @return
	 */
	public abstract TreeItem<K> search(String schema, String databaseName, String tableName);

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 16.
	 * @return
	 */
	protected TableOpenResourceView createTableResourceView() {
		TableOpenResourceView tableOpenResourceView = new TableOpenResourceView(connectionSupplier);
		return tableOpenResourceView;
	}

	/**
	 * 테이블을 찾는 리소스 뷰를 오픈
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 20.
	 */
	protected void showTableResourceView() {
		try {
			TableOpenResourceView tableOpenResourceView = createTableResourceView();
			ResultDialog<Map<String, Object>> show = tableOpenResourceView.show(this);

			Map<String, Object> data = show.getData();
			if (ValueUtil.isNotEmpty(data)) {

				String schema = tableOpenResourceView.getSchema(data);
				String databaseName = tableOpenResourceView.getDatabaseName(data);
				String tableName = tableOpenResourceView.getTableName(data);

				TreeItem<K> search = search(schema, databaseName, tableName);

				if (search != null) {
					TreeView<K> schemaTree = getSchemaTree();
					schemaTree.getSelectionModel().select(search);
					schemaTree.getFocusModel().focus(schemaTree.getSelectionModel().getSelectedIndex());
					schemaTree.scrollTo(schemaTree.getSelectionModel().getSelectedIndex());

					LOGGER.debug(search.toString());
					LOGGER.debug(data.toString());
				} else {
					LOGGER.debug("search result empty.");
				}

			}
		} catch (Exception e1) {
			LOGGER.error(ValueUtil.toString(e1));
		}
	}

	public List<TreeItem<K>> searchSchemaTreeItemPattern(String _schema) {
		String schema = _schema;
		if (schema == null)
			schema = "";
		else
			schema = _schema.toLowerCase();

		TreeItem<K> root = getSchemaTree().getRoot();
		List<TreeItem<K>> treeItem = new ArrayList<>();
		for (TreeItem<K> w : root.getChildren()) {
			String _schemaName = w.getValue().toString();

			if (_schemaName.indexOf(schema) >= 0) {
				treeItem.add(w);
			}
		}
		return treeItem;
	}

	public List<TreeItem<K>> searchPattern(String schema, String _tableName) {
		List<TreeItem<K>> searchPattern = searchSchemaTreeItemPattern(schema);
		if (searchPattern.isEmpty())
			return Collections.emptyList();

		String tableName = _tableName.toLowerCase();
		return searchPattern.stream().flatMap(root -> {

			List<TreeItem<K>> subList = new ArrayList<>();
			for (TreeItem<K> w : root.getChildren()) {
				String _schemaName = w.getValue().toString().toLowerCase();

				if (_schemaName.indexOf(tableName) >= 0) {
					subList.add(w);
				}
			}
			return subList.stream();
		}).collect(Collectors.toList());

	}

	public Optional<Pair<String, String[]>> showTableInputDialog(Function<K, String> stringConverter) {

		final List<String> schemaList = getSchemaTree().getRoot().getChildren().stream().map(v -> {
			return stringConverter.apply(v.getValue()); // v.getValue().getName().toString();
		}).collect(Collectors.toList());

		String defaultSchema = "";
		try (Connection con = connectionSupplier.get()) {
			//
			// String catalog = con.getCatalog();
			// String schema = con.getSchema();
			// ResultSet schemas = con.getMetaData().getCatalogs();
			// if (schemas.next()) {
			// String string = schemas.getString(1);
			// System.out.println(string);
			// }

			Map<String, Object> findOne = DbUtil.findOne(con, "select current_schema() as currentschema");
			if (findOne != null && !findOne.isEmpty()) {
				defaultSchema = ValueUtil.decode(findOne.get("currentschema"), "").toString();
			}
		} catch (Exception e4) {
			LOGGER.error(ValueUtil.toString(e4));

		}

		final String _defaultSchema = defaultSchema;

		if (tbResult.getItems().isEmpty())
			return Optional.empty();

		return DialogUtil.showInputCustomDialog(tbResult.getScene().getWindow(), "table Name", "테이블명을 입력하세요.",
				new CustomInputDialogAction<GridPane, String[]>() {

					TextField txtSchema;
					TextField txtTable;

					@Override
					public GridPane getNode() {
						GridPane gridPane = new GridPane();
						txtSchema = new TextField();
						txtTable = new TextField();

						FxUtil.installAutoTextFieldBinding(txtSchema, () -> {
							return schemaList;
						});

						FxUtil.installAutoTextFieldBinding(txtTable, () -> {
							return searchPattern(txtSchema.getText(), txtTable.getText()).stream()
									.map(v -> stringConverter.apply(v.getValue())/* v.getValue().getName() */).collect(Collectors.toList());
						});
						txtSchema.setText(_defaultSchema);

						// Default TableName
						TreeItem<K> selectedItem = getSchemaTree().getSelectionModel().getSelectedItem();
						if (null != selectedItem) {
							K value = selectedItem.getValue();
							if (value instanceof TableItemTree) {
								txtTable.setText(stringConverter.apply(value) /* value.getName() */);
							}
						}

						Label label = new Label("Schema : ");
						Label label2 = new Label("Table : ");
						gridPane.add(label, 0, 0);
						gridPane.add(label2, 1, 0);
						gridPane.add(txtSchema, 0, 1);
						gridPane.add(txtTable, 1, 1);
						return gridPane;
					}

					@Override
					public String[] okClickValue() {

						String schema = txtSchema.getText().trim();
						String table = txtTable.getText().trim();

						String[] okValue = new String[2];
						okValue[0] = schema;
						okValue[1] = table;
						return okValue;
					}

					@Override
					public String[] cancelClickValue() {
						return null;
					}

				});
	}

	/**
	 * 선택된 ResultTab의 데이터리스트를 반환
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 1.
	 * @return
	 */
	public List<Map<String, Object>> getSelectedTabResultItems() {
		int tabIndex = tabPaneResult.getSelectionModel().getSelectedIndex();
		List<Map<String, Object>> result = FXCollections.observableArrayList();
		switch (tabIndex) {
		case 0:
			result = tbResult.getItems();
			break;
		case 1:
			result = editableComposite.getItems().stream().map(m -> {
				TreeMap<String, Object> treeMap = new TreeMap<>();
				Iterator<Entry<ColumnExpression, ObjectProperty<ValueExpression>>> iterator = m.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<ColumnExpression, ObjectProperty<ValueExpression>> next = iterator.next();
					treeMap.put(next.getKey().getColumnName(), next.getValue().getValue().getRealValue());
				}
				return treeMap;
			}).collect(FxCollectors.toObservableList());
			break;
		default:
		}

		return result;
	}
}
