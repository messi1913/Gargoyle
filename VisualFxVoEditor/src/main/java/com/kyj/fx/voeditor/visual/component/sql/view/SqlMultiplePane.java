/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.view
 *	작성일   : 2015. 11. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.view;

import java.io.File;
import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang.SystemUtils;
import org.controlsfx.control.CheckComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.kyj.fx.voeditor.visual.component.dock.pane.DockNode;
import com.kyj.fx.voeditor.visual.component.dock.pane.DockPane;
import com.kyj.fx.voeditor.visual.component.dock.pane.DockPos;
import com.kyj.fx.voeditor.visual.component.popup.VariableMappingView;
import com.kyj.fx.voeditor.visual.component.sql.functions.ConnectionSupplier;
import com.kyj.fx.voeditor.visual.component.sql.functions.ISchemaTreeItem;
import com.kyj.fx.voeditor.visual.component.sql.functions.SQLPaneMotionable;
import com.kyj.fx.voeditor.visual.component.sql.tab.SqlTab;
import com.kyj.fx.voeditor.visual.component.sql.tab.SqlTabPane;
import com.kyj.fx.voeditor.visual.component.text.SimpleTextView;
import com.kyj.fx.voeditor.visual.framework.BigDataDVO;
import com.kyj.fx.voeditor.visual.functions.ToExcelFileFunction;
import com.kyj.fx.voeditor.visual.main.layout.GagoyleTabProxy;
import com.kyj.fx.voeditor.visual.main.layout.SchoolMgrerSpreadSheetView;
import com.kyj.fx.voeditor.visual.main.layout.SystemLayoutViewController;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;
import com.kyj.fx.voeditor.visual.util.DateUtil;
import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.GargoyleExtensionFilters;
import com.kyj.fx.voeditor.visual.util.NullExpresion;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.utils.EncrypUtil;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
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
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.StringConverter;

/**
 * @author KYJ
 *
 */
public abstract class SqlMultiplePane<T, K> extends DockPane implements ISchemaTreeItem<T, K>, SQLPaneMotionable<K> {

	/**
	 * MSG
	 */
	private static final String FILE_OVERWIRTE_MESSAGE = "파일이 이미 존재합니다. 덮어씌우시겠습니까? ";
	private static Logger LOGGER = LoggerFactory.getLogger(SqlMultiplePane.class);
	/**
	 * 스키마 구조를 보여준다.
	 */
	private TreeView<K> schemaTree;
	/**
	 * SQL을 작성하는곳
	 */
	// private SqlKeywords txtSql;

	/**
	 * SQL 조회 결과가 나타난다.
	 */
	private TabPane resultTabPane;

	private TableColumn<Map<String, Object>, Object> tcSelectRow;

	/**
	 * 상태메세지를 보여본다.
	 */
	private Label lblStatus;
	private DockNode sqlEditPane;
	private SqlTabPane sqlTabPane;
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

	private IntegerProperty startRowIndexProperty = new SimpleIntegerProperty();
	private IntegerProperty endRowIndexProperty = new SimpleIntegerProperty();
	private IntegerProperty startColIndexProperty = new SimpleIntegerProperty();
	private IntegerProperty endColIndexProperty = new SimpleIntegerProperty();

	public void setTitle(String title) {
		sqlEditPane.setTitle(title);
	}

	/**
	 * 데이터베이스와 접속한 커넥션을 반환해준다.
	 */
	public ConnectionSupplier connectionSupplier;

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
	public SqlMultiplePane(T t) {
		// 2016.2.11 적용안됨. Stage를 생성하는 시점에 Scene에 적용해야 적용됨.
		// this.getStylesheets().add(SkinManager.getInstance().getSkin());
		// String externalForm =
		// SqlPane.class.getResource(SQL_PANE_CSS).toExternalForm();
		// this.getStylesheets().add(externalForm);
		schemaTree = new TreeView<>();

		DockNode treeView = new DockNode(schemaTree, "Schema");
		treeView.setMinWidth(300);
		// treeView.setClosable(false);

		/* [시작] SQL 입력영역 */
		BorderPane sqlEditLayout = new BorderPane();
		Button btnExec = new Button("Execute");
		btnExec.setTooltip(new Tooltip("[CTRL + ENTER]"));
		btnExec.setOnMouseClicked(this::btnExecOnClick);

		Button btnAddTab = new Button("Add...");
		btnAddTab.setOnMouseClicked(this::btnAddTabOnClick);

		readTypeCheckComboBox = new CheckComboBox<ReadType>(defaultReadTypeItems());
		readTypeCheckComboBox.setConverter(new StringConverter<ReadType>() {

			@Override
			public String toString(ReadType object) {
				return object.getName();
			}

			@Override
			public ReadType fromString(String string) {
				LOGGER.error(string);
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

		sqlEditLayout.setTop(new HBox(5, btnExec, btnAddTab, readTypeCheckComboBox));

		defaultSelectedItems();

		sqlTabPane = new SqlTabPane(createTabItem());
		sqlEditLayout.setCenter(sqlTabPane);

		sqlEditPane = new DockNode(sqlEditLayout, "Sql");
		sqlEditPane.setPrefSize(100, 100);
		sqlEditPane.setClosable(false);
		/* [끝] SQL 입력영역 */

		resultTabPane = new TabPane();
		DockNode sqlResultPane = new DockNode(resultTabPane, "Result");
		sqlResultPane.setMinHeight(200);
		sqlResultPane.setClosable(false);
		/* 도킹처리 */
		// sqlEditPane.setPrefHeight(500);
		// consoleDock.setPrefHeight(200);
		// sqlResultPane.setPrefHeight(200);
		sqlEditPane.dock(this, DockPos.CENTER);
		sqlResultPane.dock(this, DockPos.BOTTOM);
		treeView.dock(this, DockPos.LEFT);

		schemaTree.setRoot(apply(t, this.connectionSupplier));
		schemaTree.setOnMouseClicked(this::schemaTreeOnMouseClick);
		schemaTree.setOnKeyPressed(this::schemaTreeOnKeyClick);

		createTreeContextMenu(schemaTree);

		Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
		DockPane.initializeDefaultUserAgentStylesheet();
	}

	/********************************
	 * 작성일 : 2016. 4. 20. 작성자 : KYJ
	 *
	 *
	 * @return
	 ********************************/
	private Tab getSelectedTab() {
		return resultTabPane.getSelectionModel().getSelectedItem();
	}

	/********************************
	 * 작성일 : 2016. 4. 20. 작성자 : KYJ
	 *
	 * 선택된 탭을 찾는데 없으면 추가한다. 그리고 안에 consumer라는 행위 처리를 한다.
	 *
	 * @param consumer
	 ********************************/

	private void selectedTabAction(Consumer<TableView<Map<String, Object>>> consumer) {
		TableView<Map<String, Object>> content = getSelectedTbResult();
		NullExpresion.ifNotNullDo(content, consumer);
	}

	/********************************
	 * 작성일 : 2016. 4. 20. 작성자 : KYJ
	 *
	 * 선택된 테이블 탭을 리턴함. 없는경우 null을 리턴
	 *
	 * @return
	 ********************************/
	@SuppressWarnings("unchecked")
	private TableView<Map<String, Object>> getSelectedTbResult() {
		Tab selectedItem = getSelectedTab();
		selectedItem = (Tab) ValueUtil.decode(selectedItem, selectedItem, addNewTabResult());
		BorderPane borderPane = (BorderPane) selectedItem.getContent();
		TableView<Map<String, Object>> content = (TableView<Map<String, Object>>) borderPane.getCenter();
		return content;
	}

	/********************************
	 * 작성일 : 2016. 4. 20. 작성자 : KYJ
	 *
	 * 새로운 탭을 만들고 그 안에 consumer라는 행위 처리를 한다.
	 *
	 * @param consumer
	 ********************************/
	@SuppressWarnings("unchecked")
	private void newTabAction(Consumer<TableView<Map<String, Object>>> consumer) {
		Tab newTab = addNewTabResult();
		BorderPane borderPane = (BorderPane) newTab.getContent();
		TableView<Map<String, Object>> content = (TableView<Map<String, Object>>) borderPane.getCenter();
		NullExpresion.ifNotNullDo(content, consumer);
		resultTabPane.getTabs().add(newTab);
		resultTabPane.getSelectionModel().selectLast();

	}

	/********************************
	 * 작성일 : 2016. 4. 20. 작성자 : KYJ
	 *
	 *
	 * @return
	 ********************************/
	private Tab addNewTabResult() {
		TableView<Map<String, Object>> tbResult = new TableView<>();
		// Cell 단위로 선택
		tbResult.getSelectionModel().setCellSelectionEnabled(true);
		tbResult.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tbResult.getItems().addListener((ListChangeListener<Map<String, Object>>) arg0 -> {
			int size = arg0.getList().size();
			lblStatus.textProperty().set(size + " row");
		});
		tbResult.setOnKeyPressed(this::tbResultOnKeyClick);

		{
			tcSelectRow = new TableColumn<>("↓");
			tcSelectRow.setMaxWidth(20);
			tcSelectRow.setSortable(false);
			tcSelectRow.setCellFactory(cell -> {
				return new DragSelectionCell(tbResult);
			});
			// Table Select Drag 처리
			endRowIndexProperty.addListener(event -> tableSelectCell());
			endColIndexProperty.addListener(event -> tableSelectCell());
		}

		BorderPane tbResultLayout = new BorderPane(tbResult);
		lblStatus = new Label("Ready...");
		lblStatus.setMaxHeight(50d);
		tbResultLayout.setBottom(lblStatus);
		createResultTableContextMenu(tbResult);

		Tab tab = new Tab("Example", tbResultLayout);
		return tab;
	}

	private void tableSelectCell() {

		int starRowtIndex = startRowIndexProperty.get();
		int endRowIndex = endRowIndexProperty.get();
		int starColtIndex = startColIndexProperty.get();
		int endColIndex = endColIndexProperty.get();

		selectedTabAction(tbResult -> {

			TableViewSelectionModel<Map<String, Object>> selectionModel = tbResult.getSelectionModel();
			selectionModel.clearSelection();
			if (starColtIndex == 0) {
				selectionModel.selectRange(starRowtIndex, tbResult.getColumns().get(0), endRowIndex,
						tbResult.getColumns().get(tbResult.getColumns().size() - 1));
			} else {
				selectionModel.selectRange(starRowtIndex, tbResult.getColumns().get(starColtIndex), endRowIndex,
						tbResult.getColumns().get(endColIndex));
			}

		});

	}

	public class DragSelectionCell extends TextFieldTableCell<Map<String, Object>, Object> {
		public DragSelectionCell(TableView<Map<String, Object>> tbResult) {

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

						FxUtil.createStageAndShow(new SimpleTextView(dataDVO.getValue()), stage->{
							stage.setAlwaysOnTop(true);
							stage.initModality(Modality.APPLICATION_MODAL);
							stage.initOwner(SqlMultiplePane.this.getScene().getWindow());
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
			this.username = map.get(ResourceLoader.BASE_KEY_JDBC_ID).toString();
			this.password = EncrypUtil.decryp(map.get(ResourceLoader.BASE_KEY_JDBC_PASS).toString());
			this.driver = map.get("driver").toString();

			this.userColor = map.get("color") == null ? null : Color.web(map.get("color").toString());
			this.setTitle(this.url);
			
			//ConnectionSupplier 생성
			connectionSupplier = new ConnectionSupplier(map);
		} catch (Exception e) {
			DialogUtil.showExceptionDailog(this, e, "초기화 실패....");
		}

	}

	private void createResultTableContextMenu(TableView<Map<String, Object>> tbResult) {

		MenuItem menuExportExcel = new MenuItem("Export Excel File");
		menuExportExcel.setOnAction(e -> menuExportExcelOnAction(tbResult));

		MenuItem menuExportSpreadSheet = new MenuItem("Export SpreadSheet");
		menuExportSpreadSheet.setOnAction(this::menuExportSpreadSheetOnAction);

		Menu menuExportExcelFile = new Menu("Export", null, menuExportExcel, menuExportSpreadSheet);

		ContextMenu contextMenu = new ContextMenu(menuExportExcelFile);
		tbResult.setContextMenu(contextMenu);
	}

	/**
	 * 컨텍스트 메뉴 생성 및 기능 적용
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

		MenuItem menuShowData = new MenuItem("Show 100 rows");
		menuShowData.setOnAction(this::show100RowAction);

		MenuItem menuProperties = new MenuItem("Properties");
		menuProperties.setOnAction(this::showProperties);

		MenuItem menuReflesh = new MenuItem("Reflesh");
		menuReflesh.setOnAction(this::menuRefleshOnAction);
		menuReflesh.setAccelerator(new KeyCodeCombination(KeyCode.F5));

		ContextMenu contextMenu = new ContextMenu(menu, menuShowData, menuProperties, new SeparatorMenuItem(), menuReflesh);
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

		StringBuffer sb = new StringBuffer();
		if (tableColumns != null && !tableColumns.isEmpty()) {
			for (String col : tableColumns) {
				sb.append(String.format("'%s' ,", col));
			}
			sb.setLength(sb.length() - 1);
		}

		selectedTab.appendTextSql(String.format("insert into %s ( %s ) \nvalues ( )", tableName, sb.toString()));
	}

	/**
	 * delete문장을 화면에 보여줌.
	 *
	 * @param e
	 */
	public void applyDeleteSelectScript(ActionEvent e) {
		TreeItem<K> selectedItem = schemaTree.getSelectionModel().getSelectedItem();
		String tableName = this.getSelectedTreeByTableName(selectedItem);
		SqlTab selectedTab = getSelectedSqlTab();
		selectedTab.appendTextSql(String.format("delete from %s where ", tableName));
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
		SqlTab selectedTab = getSelectedSqlTab();

		// default is primarykeys...
		StringBuffer whereStatement = new StringBuffer();
		if (primaryKeys != null && !primaryKeys.isEmpty()) {
			for (String col : primaryKeys) {
				whereStatement.append(String.format("'%s' ,", col));
			}
			whereStatement.setLength(whereStatement.length() - 1);
		}

		selectedTab.appendTextSql(String.format("update %s set  \nwhere  %s", tableName, whereStatement.toString()));
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
		Connection connection = connectionSupplier.get();
		try {
			String driver = DbUtil.getDriverNameByConnection(connection);
			String sql = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.SQL_TABLE_COLUMNS_WRAPPER, driver);

			Map<String, Object> map = new HashMap<>(2);
			map.put("databaseName", schema);
			map.put("tableName", tableName);

			sql = ValueUtil.getVelocityToText(sql, map, true);
			List<String> select = DbUtil.selectBeans(connection, sql, 10, (RowMapper<String>) (rs, rowNum) -> rs.getString(1));

			redueceAction(select, ",",
					v -> selectedTab.appendTextSql(String.format("select %s \nfrom %s ", v.substring(0, v.length()), tableName)));

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

	/**
	 * 탭추가.
	 *
	 * @return
	 */
	SqlTab createTabItem() {
		return new SqlTab(this::txtSqlOnKeyEvent);
	}

	/**
	 * SQL 실행 이벤트
	 *
	 * @param e
	 */
	public void btnExecOnClick(MouseEvent e) {
		execute();
	}

	/**
	 * 탭추가 이벤트
	 *
	 * @param e
	 */
	public void btnAddTabOnClick(MouseEvent e) {
		sqlTabPane.getTabs().add(createTabItem());
		sqlTabPane.getSelectionModel().selectLast();
	}

	public void txtSqlOnKeyEvent(KeyEvent e) {
		if (e.getCode() == KeyCode.ENTER && e.isControlDown()) {
			execute();
		}
	}

	private void execute() {

		SqlTab selectedItem = sqlTabPane.getSelectedTab();

		String sql = selectedItem.getSelectedSQLText();
		if (sql == null || sql.isEmpty()) {
			sql = selectedItem.getSqlText();
		}

		if (sql == null || sql.isEmpty())
			return;

		final String _sql = sql;
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

		newTabAction(tbResult -> {

			tbResult.getColumns().clear();
			tbResult.getItems().clear();

			List<Map<String, Object>> query = query(sql, param, success -> {
				lblStatus.setText(success.size() + " row");
			} , (exception, showDialog) -> {
				lblStatus.setText(exception.toString());
				if (showDialog)
					DialogUtil.showExceptionDailog(exception);
			});
			if (query.isEmpty()) {
				return;
			}
			binding(tbResult, query);
			tbResult.getItems().addAll(query);
		});

	}

	/**
	 * 결과 데이터를 테이블에 바인딩
	 *
	 * @param tbResult
	 *
	 * @param query
	 */
	private void binding(TableView<Map<String, Object>> tbResult, List<Map<String, Object>> query) {
		Map<String, Object> map = query.get(0);
		Iterator<String> iterator = map.keySet().iterator();
		tbResult.getColumns().add(tcSelectRow);
		while (iterator.hasNext()) {
			String column = iterator.next();
			TableColumn<Map<String, Object>, Object> e = new TableColumn<>(column);
			e.setCellFactory(arg -> {
				return new DragSelectionCell(tbResult);
			});
			e.setCellValueFactory(arg -> {
				Object value = arg.getValue().get(column);
				return new SimpleObjectProperty<>(value);
			});
			tbResult.getColumns().add(e);
		}
	}

	public Color getUserColor() {
		return userColor;
	}

	/**
	 * 엑셀 export기능
	 *
	 * @param tbResult
	 *
	 * @param e
	 */
	public void menuExportExcelOnAction(TableView<Map<String, Object>> tbResult) {

		File saveFile = DialogUtil.showFileSaveDialog(SharedMemory.getPrimaryStage().getOwner(), option -> {
			option.setInitialFileName(DateUtil.getCurrentDateString(DateUtil.SYSTEM_DATEFORMAT_YYYYMMDDHHMMSS));
			option.getExtensionFilters().add(new ExtensionFilter(GargoyleExtensionFilters.XLSX_NAME, GargoyleExtensionFilters.XLSX));
			option.getExtensionFilters().add(new ExtensionFilter(GargoyleExtensionFilters.XLS_NAME, GargoyleExtensionFilters.XLS));
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

		ObservableList<Map<String, Object>> items = tbResult.getItems();
		ToExcelFileFunction toExcelFileFunction = new ToExcelFileFunction();
		List<String> columns = tbResult.getColumns().stream().map(col -> col.getText()).collect(Collectors.toList());
		toExcelFileFunction.generate0(saveFile, columns, items);
		DialogUtil.showMessageDialog("complete...");

	}

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
		ObservableList<Map<String, Object>> items = getTbResult().getItems();
		boolean isFirst = true;
		for (Map<String, Object> map : items) {

			Iterator<String> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				String column = iterator.next();
				if (isFirst)
					columnBuf.append(column).append("\t");

				dataBuf.append(map.get(column)).append("\t");
			}
			dataBuf.append("\n");
			if (isFirst) {
				isFirst = false;
				columnBuf.append("\n");
			}

		}

		String putString = new StringBuffer().append(columnBuf).append(dataBuf).toString();
		SchoolMgrerSpreadSheetView parent = new SchoolMgrerSpreadSheetView();
		parent.paste(putString, 0, 0);
		tabProxy.loadNewSystemTab(SystemLayoutViewController.TAB_TITLE_SPREAD_SHEET, parent);
	}

	public TableView<Map<String, Object>> getTbResult() {
		return getSelectedTbResult();
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

	/**
	 * 테이블 속성 조회
	 *
	 * @param e
	 */
	public void showProperties(ActionEvent e) {
		TreeItem<K> selectedItem = schemaTree.getSelectionModel().getSelectedItem();
		K value = selectedItem.getValue();
		showProperties(connectionSupplier, value);
	}

}
