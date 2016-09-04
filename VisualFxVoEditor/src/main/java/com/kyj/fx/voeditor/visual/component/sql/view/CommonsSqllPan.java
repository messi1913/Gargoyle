/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.view
 *	작성일   : 2015. 11. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.view;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.ResultDialog;
import com.kyj.fx.voeditor.visual.component.popup.TableOpenResourceView;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.DatabaseTreeCallback;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.DatabaseTreeNode;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.ColumnItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.DatabaseItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.SchemaItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.TableItemTree;
import com.kyj.fx.voeditor.visual.component.sql.table.TableInformationFrameView;
import com.kyj.fx.voeditor.visual.component.sql.table.TableInformationUserMetadataVO;
import com.kyj.fx.voeditor.visual.exceptions.GagoyleParamEmptyException;
import com.kyj.fx.voeditor.visual.exceptions.GargoyleConnectionFailException;
import com.kyj.fx.voeditor.visual.exceptions.NotYetSupportException;
import com.kyj.fx.voeditor.visual.functions.ResultSetToMapConverter;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxClipboardUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * 공통 데이터 조회 전용 패널
 *
 * @author KYJ
 *
 */
public abstract class CommonsSqllPan extends SqlPane<String, DatabaseItemTree<String>>
/* implements SQLPaneMotionable<DatabaseItemTree<String>> */ {

	/**
	 * 테이블 정보보기 팝업의 타이틀
	 */
	private static final String POPUP_TITLE_DATABASE_INFOMATION = "Properties";

	private static Logger LOGGER = LoggerFactory.getLogger(CommonsSqllPan.class);

	public CommonsSqllPan() {
		super("Database");
	}

	public CommonsSqllPan(String t) {
		super(t);
	}

	/**
	 * 스키마 트리를 보여주기위한 TreeItem을 정의한다.
	 */
	@Override
	public abstract TreeItem<DatabaseItemTree<String>> apply(String t, Supplier<Connection> conSupplier);

	@Override
	public void schemaTreeOnMouseClick(MouseEvent e) {

		if (e.getClickCount() == 2) {

			if ("PRIMARY".equals(e.getButton().name()) || e.isPrimaryButtonDown()) {
				schemaTreePrimaryMouseClick(e);
			}
		}
	}

	@Override
	public void schemaTreeOnKeyClick(KeyEvent e) {
		if (KeyCode.C == e.getCode() && e.isControlDown() && !e.isShiftDown()) {

			TreeItem<DatabaseItemTree<String>> selectedItem = getSchemaTree().getSelectionModel().getSelectedItem();
			if (selectedItem == null)
				return;
			DatabaseItemTree<String> value = selectedItem.getValue();
			if (value == null)
				return;

			String clipTarget = value.getName();

			// 클립보드에 텍스트 복사.
			FxClipboardUtil.putString(clipTarget);
		}

		else if (KeyCode.R == e.getCode() && e.isControlDown() && e.isShiftDown()) {
			try {
				TableOpenResourceView tableOpenResourceView = new TableOpenResourceView(connectionSupplier);
				ResultDialog<Map<String, Object>> show = tableOpenResourceView.show(this);

				Map<String, Object> data = show.getData();
				if (ValueUtil.isNotEmpty(data)) {

					String schema = tableOpenResourceView.getSchema(data);
					String databaseName = tableOpenResourceView.getDatabaseName(data);
					String tableName = tableOpenResourceView.getTableName(data);

					TreeItem<DatabaseItemTree<String>> search = search(schema, databaseName, tableName);

					if (search != null) {
						TreeView<DatabaseItemTree<String>> schemaTree = getSchemaTree();
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
	}

	public TreeItem<DatabaseItemTree<String>> search(String schema, String databaseName, String tableName) {
		TreeItem<DatabaseItemTree<String>> root = getSchemaTree().getRoot();

		boolean isFound = false;

		TreeItem<DatabaseItemTree<String>> treeItem = null;

		// schema
		if (ValueUtil.isNotEmpty(schema)) {
			for (TreeItem<DatabaseItemTree<String>> w : root.getChildren()) {
				String _schemaName = w.getValue().toString();

				if (_schemaName.equals(schema)) {
					isFound = true;
					treeItem = w;
					break;
				}
			}
		}

		// database or table
		if (isFound || (treeItem == null && ValueUtil.isNotEmpty(databaseName))) {

			if (treeItem == null)
				treeItem = root;

			for (TreeItem<DatabaseItemTree<String>> w : treeItem.getChildren()) {
				String _databaseName = w.getValue().toString();

				if (_databaseName.equals(databaseName)) {
					isFound = true;
					treeItem = w;
					break;
				}
			}
		}

		// table or column
		if (isFound && ValueUtil.isNotEmpty(tableName)) {

			for (TreeItem<DatabaseItemTree<String>> w : treeItem.getChildren()) {
				String _tableName = w.getValue().toString();

				if (_tableName.equals(tableName)) {
					isFound = true;
					treeItem = w;
					break;
				}
			}
		}

		//아무것도 발견 못한상황에서 schema와 databaseName이 빈경우
		if (!isFound) {
			if (ValueUtil.isEmpty(schema) && ValueUtil.isEmpty(databaseName)) {

				for (TreeItem<DatabaseItemTree<String>> w : root.getChildren()) {
					String _tableName = w.getValue().toString();

					if (_tableName.equals(tableName)) {
						isFound = true;
						treeItem = w;
						break;
					}
				}

				if (!isFound) {
					ObservableList<TreeItem<DatabaseItemTree<String>>> ch = root.getChildren();
					for (TreeItem<DatabaseItemTree<String>> w : ch.get(0).getChildren()) {
						String _tableName = w.getValue().toString();

						if (_tableName.equals(tableName)) {
							isFound = true;
							treeItem = w;
							break;
						}
					}
				}
			}
		}
		// if (isFound) {
		// children = treeItem.getChildren();
		// sliding++;
		// } else {
		// // 아예못찾은경우는 종료.
		// break;
		// }

		return treeItem;
	}

	@Override
	public void menuRefleshOnAction(ActionEvent e) {
		TreeItem<DatabaseItemTree<String>> selectedItem = getSchemaTree().getSelectionModel().getSelectedItem();
		if (selectedItem != null) {

			DatabaseItemTree<String> value = selectedItem.getValue();

			try {

				selectedItem.getChildren().clear();
				value.getChildrens().clear();
				value.read();
				ObservableList<TreeItem<DatabaseItemTree<String>>> childrens = value.getChildrens();

				// TODO 개선이 필요한 상황, 현재는 이중루프.
				childrens.forEach(item -> item.setGraphic(DatabaseTreeNode.getGraphics(item.getValue())));

				selectedItem.getChildren().addAll(childrens);
			} catch (GargoyleConnectionFailException ex) {
				DialogUtil.showExceptionDailog(ex);
			} catch (Exception e1) {
				LOGGER.error(ValueUtil.toString(e1));
			}

		}

	}

	/*
	 * 데이터그리드 키 클릭이벤트
	 */
	@Override
	public void tbResultOnKeyClick(KeyEvent e) {

		 
		/*
		 * 2016-09-03 by kyj.
		 * SqlPane에서 기본 기능이 되도록 재구현.
		 * FxUtil.installClipboardKeyEvent(tb);API를 사용.
		 */

//		int type = -1;
//		if (e.isControlDown() && e.getCode() == KeyCode.C) {
//			if (e.isShiftDown()) {
//				type = 2;
//			} else {
//				type = 1;
//			}
//		}
//
//		if (type == -1)
//			return;
//
//		TableView<Map<String, Object>> tbResult = getTbResult();
//		ObservableList<TablePosition> selectedCells = tbResult.getSelectionModel().getSelectedCells();
//
//		TablePosition tablePosition = selectedCells.get(0);
//		TableColumn tableColumn = tablePosition.getTableColumn();
//		int row = tablePosition.getRow();
//		int col = tbResult.getColumns().indexOf(tableColumn);
//
//		switch (type) {
//		case 1:
//			StringBuilder sb = new StringBuilder();
//			for (TablePosition cell : selectedCells) {
//				// TODO :: 첫번째 컬럼(행 선택 기능)도 빈값으로 복사됨..
//				// 행변경시
//				if (row != cell.getRow()) {
//					sb.append("\n");
//					row++;
//				}
//				// 열 변경시
//				else if (col != tbResult.getColumns().indexOf(cell.getTableColumn())) {
//					sb.append("\t");
//				}
//				Object cellData = cell.getTableColumn().getCellData(cell.getRow());
//				sb.append(ValueUtil.decode(cellData, cellData, "").toString());
//			}
//			FxClipboardUtil.putString(sb.toString());
//
//			// Map<String, Object> map = tbResult.getItems().get(row);
//			// FxClipboardUtil.putString(ValueUtil.toCVSString(map));
//			break;
//		case 2:
//			Object cellData = tableColumn.getCellData(row);
//			FxClipboardUtil.putString(ValueUtil.decode(cellData, cellData, "").toString());
//			break;
//		}

	}

	/**
	 * 트리에서 마우스 좌 더블클릭 이벤트
	 *
	 * @param e
	 */
	public void schemaTreePrimaryMouseClick(MouseEvent e) {
		LOGGER.debug("tree primary key down!");
		TreeView<DatabaseItemTree<String>> tree = getSchemaTree();
		if (tree == null)
			return;
		TreeItem<DatabaseItemTree<String>> selectedItem = tree.getSelectionModel().getSelectedItem();
		if (selectedItem == null)
			return;

		DatabaseItemTree<String> value = selectedItem.getValue();
		LOGGER.debug("current selected treeItem : " + value.toString());

		ObservableList<TreeItem<DatabaseItemTree<String>>> childrens = value.getChildrens();
		if (childrens == null || childrens.isEmpty()) {
			try {
				value.read();
				ObservableList<TreeItem<DatabaseItemTree<String>>> newChildres = value.getChildrens();
				if (newChildres == null)
					return;
				// TODO 개선이 필요한 상황, 현재는 이중루프.
				newChildres.forEach(item -> item.setGraphic(DatabaseTreeNode.getGraphics(item.getValue())));

				if (newChildres != null && !newChildres.isEmpty())
					selectedItem.getChildren().addAll(newChildres);
			} catch (GargoyleConnectionFailException ex) {
				DialogUtil.showExceptionDailog(ex);
			} catch (Exception e1) {
				LOGGER.error(ValueUtil.toString(e1));
			}

		}
		// }

	}

	/**
	 * 설정에 정의된 BASE_KEY_JDBC_DRIVER에 맞는 jdbc Driver의 SQL 패널을 반환
	 *
	 * @return
	 * @throws NotYetSupportException
	 * @throws GargoyleConnectionFailException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static CommonsSqllPan getSqlPane() throws NotYetSupportException, GargoyleConnectionFailException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		CommonsSqllPan postgreSqlPane = null;

		String driver = ResourceLoader.getInstance().get(ResourceLoader.BASE_KEY_JDBC_DRIVER);
		String dbms = ValueUtil.getDriverToDBMSName(driver);
		if (ValueUtil.isEmpty(dbms)) {
			String msg = "not yet supported...";
			throw new NotYetSupportException(msg);
		}

		String panePropertyValue = "dbms.".concat(dbms).concat(".pane");
		if (ValueUtil.isEmpty(panePropertyValue)) {
			String msg = "not yet supported...";
			throw new NotYetSupportException(msg);
		}
		String className = ConfigResourceLoader.getInstance().get(panePropertyValue);
		postgreSqlPane = (CommonsSqllPan) Class.forName(className).newInstance();

		// if (ResourceLoader.DBMS_SUPPORT_MY_SQL.equals(dbms)) {
		// postgreSqlPane = new MysqlPane();
		//
		// } else if (ResourceLoader.DBMS_SUPPORT_POSTGRE.equals(dbms)) {
		// postgreSqlPane = new PostgreSqlPane();
		// } else if (ResourceLoader.DBMS_SUPPORT_ORACLE.equals(dbms)) {
		// postgreSqlPane = new OracleSqlPane();
		// } else if (ResourceLoader.DBMS_SUPPORT_H2.equals(dbms)) {
		// postgreSqlPane = new H2Pane();
		// } else if (dbms == null) {
		// String msg = "Check 'Datatabse Settings' on the 'Configuration tab'
		// ";
		// throw new ConnectionFailException(msg);
		// } else {
		// String msg = "not yet supported...";
		// throw new NotYetSupportException(msg);
		// }
		try {
			String url = DbUtil.getConnection().getMetaData().getURL();
			postgreSqlPane.setTitle(url);

			// 특화된 스타일적용처리 -> 트리에서 기본키이면 붉은색으로 색칠.
			postgreSqlPane.getSchemaTree().setCellFactory(new DatabaseTreeCallback<>());

		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}

		return postgreSqlPane;
	}

	/**
	 * 설정에 정의된 BASE_KEY_JDBC_DRIVER에 맞는 jdbc Driver의 SQL 패널을 반환
	 *
	 * @return
	 * @throws NotYetSupportException
	 */
	public static CommonsSqllPan getSqlPane(String dbms) throws Exception {
		CommonsSqllPan sqlPane = null;
		String classLocation = ResourceLoader.getInstance().get("dbms." + dbms + ".pane");
		try {

			Class<?> forName = Class.forName(classLocation);
			Constructor<?> constructor = forName.getConstructor(String.class);
			constructor.setAccessible(true);
			sqlPane = (CommonsSqllPan) constructor.newInstance(dbms);

			// 특화된 스타일적용처리 -> 트리에서 기본키이면 붉은색으로 색칠.
			sqlPane.getSchemaTree().setCellFactory(new DatabaseTreeCallback<>());
		} catch (Exception e) {
			// e.printStackTrace();
			String msg = "not yet supported..." + e.getMessage();
			LOGGER.error(msg);

			throw new NotYetSupportException(msg);
		}
		return sqlPane;
	}

	/**
	 * 쿼리 기능을 수행한다.
	 *
	 * 어플리케이션 기능적인 쿼리 수행
	 *
	 * @onSuccess : 이벤트 기능이 에러없이 처리된경우
	 *
	 * @exceptionHandler : 기능처리중 에외가 발생한 경우
	 */
	@Override
	public List<Map<String, Object>> query(String query, Map<String, Object> param, Consumer<List<Map<String, Object>>> onSuccess,
			BiConsumer<Exception, Boolean> exceptionHandler) {
		List<Map<String, Object>> arrayList = new ArrayList<>();
		Connection con = null;
		try {
			con = connectionSupplier.get();

			if (con != null) {
				arrayList = singleConnection(con, query, param);
			} else {
				arrayList = baseConnection(query, param);
			}
			onSuccess.accept(arrayList);
		}

		catch (SQLException e) {
			boolean showDialog = true;
			// 사용자가 SQL을 잘못입력한경우 처리할 내용. [크리틱컬하지않는 에러... 처리.]
			if ("42P01".equals(e.getSQLState())) {
				LOGGER.error(ValueUtil.toString(e));
				showDialog = false;
			} else if ("42S02".equals(e.getSQLState())) {
				LOGGER.error(ValueUtil.toString(e));
				showDialog = false;
			}
			/* Postgre sql */
			else if ("42601".equals(e.getSQLState())) {
				LOGGER.error(ValueUtil.toString(e));
				showDialog = false;
			}
			exceptionHandler.accept(e, showDialog);
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
			exceptionHandler.accept(e, true);
		}
		/* 2015.11.12 finnaly문 추가. */
		finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				LOGGER.error(ValueUtil.toString(e));
			}
		}
		return arrayList;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void queryAll(List<String> queryArray, Consumer<Integer> onSuccess, BiConsumer<Exception, Boolean> exceptionHandler) {

		int result = -1;
		Connection con = null;
		try {
			con = connectionSupplier.get();

			// List<List<String>> collect = Stream.of(queryArray).filter(str ->
			// ValueUtil.isNotEmpty(str)).map(str ->
			// str).collect(Collectors.toList());

			result = DbUtil.getTransactionedScope(con, queryArray, arr -> {
				List<String> collect = arr.stream().filter(str -> ValueUtil.isNotEmpty(str)).collect(Collectors.toList());
				return collect;
			}, ex -> {
				LOGGER.error(ValueUtil.toString(ex));
				exceptionHandler.accept(ex, true);
			});

			onSuccess.accept(result);
		}

		catch (SQLException e) {
			boolean showDialog = true;
			// 사용자가 SQL을 잘못입력한경우 처리할 내용. [크리틱컬하지않는 에러... 처리.]
			if ("42P01".equals(e.getSQLState())) {
				LOGGER.error(ValueUtil.toString(e));
				showDialog = false;
			} else if ("42S02".equals(e.getSQLState())) {
				LOGGER.error(ValueUtil.toString(e));
				showDialog = false;
			}
			/* Postgre sql */
			else if ("42601".equals(e.getSQLState())) {
				LOGGER.error(ValueUtil.toString(e));
				showDialog = false;
			}
			exceptionHandler.accept(e, showDialog);
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
			exceptionHandler.accept(e, true);
		}
		/* 2015.11.12 finnaly문 추가. */
		finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				LOGGER.error(ValueUtil.toString(e));
			}
		}

	}

	protected List<Map<String, Object>> baseConnection(String query, Map<String, Object> param) throws Exception {
		List<Map<String, Object>> arrayList = new ArrayList<>();
		if (DbUtil.isDml(query)) {
			int update = DbUtil.update(query);
			Map<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("result", update);
			arrayList.add(hashMap);
		} else {
			String sql = query;
			Properties prop = new Properties();

			// Velocity텍스트이면 변수맵핑 처리를한다.
			if (ValueUtil.isVelocityContext(sql)) {
				sql = getDynmicSQL(sql, param);
			}

			// 큰사이즈 컬럼은 간단한 형식으로 표시할지 유무.
			if ("true".equals(ResourceLoader.getInstance().get(ResourceLoader.SKIP_BIG_DATA_COLUMN))) {
				prop.put(ResourceLoader.SKIP_BIG_DATA_COLUMN, true);
			}
			int limitSize = -1;
			if ("true".equals(ResourceLoader.getInstance().get(ResourceLoader.APPLY_MAX_ROW_COUNT))) {
				/* 2016. 02. 11 velocity 문법을 적용하여 rownum 적용 */
				// String dynamicSql =
				// ConfigResourceLoader.getInstance().get(ConfigResourceLoader.SQL_LIMIT_WRAPPER);
				// HashMap<String, Object> paramMap = new HashMap<String,
				// Object>();
				// paramMap.put(ConfigResourceLoader.USER_SQL, sql);
				// paramMap.put(ConfigResourceLoader.START_ROW, 0);
				// paramMap.put(ConfigResourceLoader.MAX_ROW, 1000);
				// sql = ValueUtil.getVelocityToText(dynamicSql, paramMap);
				limitSize = 1000;
			}
			prop.put("pageIndex", 0);

			arrayList = DbUtil.select(sql, 10, limitSize, new ResultSetToMapConverter(prop));
		}
		return arrayList;
	}

	protected List<Map<String, Object>> singleConnection(Connection con, String query, Map<String, Object> param) throws Exception {
		List<Map<String, Object>> arrayList = new ArrayList<>();
		if (DbUtil.isDml(query)) {
			int update = DbUtil.update(con, query);
			Map<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("result", update);
			arrayList.add(hashMap);
		} else {

			String sql = query;
			Properties prop = new Properties();

			// Velocity텍스트이면 변수맵핑 처리를한다.
			if (ValueUtil.isVelocityContext(sql)) {
				sql = getDynmicSQL(sql, param);
			}

			// 큰사이즈 컬럼은 간단한 형식으로 표시할지 유무.
			if ("true".equals(ResourceLoader.getInstance().get(ResourceLoader.SKIP_BIG_DATA_COLUMN))) {
				prop.put(ResourceLoader.SKIP_BIG_DATA_COLUMN, true);
			}

			int limitSize = -1;
			if ("true".equals(ResourceLoader.getInstance().get(ResourceLoader.APPLY_MAX_ROW_COUNT))) {
				/* 2016. 02. 11 velocity 문법을 적용하여 rownum 적용 */
				// String dynamicSql =
				// ConfigResourceLoader.getInstance().get(ConfigResourceLoader.SQL_LIMIT_WRAPPER);
				// HashMap<String, Object> paramMap = new HashMap<String,
				// Object>();
				// paramMap.put(ConfigResourceLoader.USER_SQL, sql);
				// paramMap.put(ConfigResourceLoader.START_ROW, 0);
				// paramMap.put(ConfigResourceLoader.MAX_ROW, 1000);
				// sql = ValueUtil.getVelocityToText(dynamicSql, paramMap);
				limitSize = 1000;
			}

			prop.put("pageIndex", 0);

			arrayList = DbUtil.select(con, sql, 10, limitSize, new ResultSetToMapConverter(prop));
		}
		return arrayList;
	}

	private String getDynmicSQL(String query, Map<String, Object> param) {
		return getDynmicSQL(query, param, true);
	}

	/**
	 * Velocity텍스트이면 변수맵핑 처리를한다.
	 *
	 * @param query
	 * @param param
	 * @return
	 * @throws GagoyleParamEmptyException
	 */
	private String getDynmicSQL(String query, Map<String, Object> param, boolean appendQuote) {
		String sql = query;
		if (!param.isEmpty()) {
			HashMap<String, Object> reMapping = new HashMap<>();
			Iterator<String> it = param.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				Object value = param.get(key);
				if (value != null) {
					reMapping.put(key, appendQuote ? "'".concat(value.toString()).concat("'") : value.toString());
				}
			}
			sql = ValueUtil.getVelocityToText(query, reMapping, true);
		} else {
			throw new RuntimeException(String.format("param is empty... \nparams :%s", param.keySet().toString()));
		}
		return sql;
	}

	/*
	 * 100개의 데이터 보여주기 위해 처리하는 메소드
	 *
	 * @see
	 * com.kyj.fx.voeditor.visual.component.sql.view.SqlPane#show100RowAction(
	 * javafx.event.ActionEvent)
	 */
	@Override
	public List<Map<String, Object>> show100RowAction() {

		String driver = getDriver();
		String wrapperedSQL = "";
		if (driver != null) {
			String key = ConfigResourceLoader.SQL_LIMIT_WRAPPER.replaceAll("\\{driver\\}", driver);
			wrapperedSQL = ConfigResourceLoader.getInstance().get(key);
		} else {
			wrapperedSQL = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.SQL_LIMIT_WRAPPER);
		}

		// JDBC와 JAVAFX간계 에러발생으로 생략.... 벤더에따라 에러가 발생하기도 함. 에러 발생시
		// Connection connection = connectionSupplier.get();
		//
		// // 코드 에러 범위 수정.
		//
		// try {
		// Properties clientInfo = connection.getClientInfo();
		// LOGGER.debug(String.format("Client Info : %s",
		// clientInfo.toString()));
		// } catch (Throwable e) {
		//
		// }

		TreeItem<DatabaseItemTree<String>> selectedItem = getSchemaTree().getSelectionModel().getSelectedItem();
		if (selectedItem != null) {

			DatabaseItemTree<String> value = selectedItem.getValue();
			if (value instanceof TableItemTree) {
				TreeItem<DatabaseItemTree<String>> schemaTree = selectedItem.getParent();
				String schemaName = schemaTree.getValue().getName();
				//				if(value.isValideSchemaName(schemaName))

				String tableName = "";
				/*
				 * 2016-07-12 SQLite에서는 스키마라는 개념이 존재하지않는다.
				 * Schema Name을 100개의로우를 보여주는 SQL에 적용할지 여부를 결정한다.
				 */
				if (value.isApplySchemaName(schemaName)) {
					tableName = schemaName.concat(".").concat(value.getName());
				} else {
					tableName = value.getName();
				}

				String sql = ValueUtil.getVelocityToText(wrapperedSQL, "usersql", "select * from ".concat(tableName));
				execute(sql);
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.kyj.fx.voeditor.visual.component.sql.view.SqlPane#showProperties(
	 * java.util.function.Supplier, java.lang.Object)
	 */
	@Override
	public void showProperties(Supplier<Connection> connectionSupplier, DatabaseItemTree<String> value) {
		if (value == null)
			return;

		final String name = value.getName();

		if (value instanceof ColumnItemTree) {
			// 작업 예정없음. 참고로 삭제하면 안됨.
		} else if (value instanceof TableItemTree) {
			SchemaItemTree<String> schema = ((TableItemTree<String>) value).getParent();
			final String databaseName = schema.getName();
			showProperties(connectionSupplier, databaseName, name);
		}
	}

	/*
	 * @inheritDoc
	 */
	@Override
	public void showProperties(Supplier<Connection> connectionSupplier, String databaseName, String tableName) {
		try {

			// 팝업씬 생성.
			TableInformationFrameView tableInformationFrameView = new TableInformationFrameView(connectionSupplier, () -> {
				TableInformationUserMetadataVO meta = new TableInformationUserMetadataVO();
				meta.setDatabaseName(databaseName);
				meta.setTableName(tableName);
				return meta;
			});

			final Stage dialog = new Stage();
			dialog.setTitle(POPUP_TITLE_DATABASE_INFOMATION.concat("(" + tableName + ")"));
			// dialog.initModality(Modality.NONE);
			dialog.setAlwaysOnTop(false);
			dialog.centerOnScreen();
			dialog.setMaxWidth(tableInformationFrameView.getPrefWidth());
			dialog.setMaxHeight(tableInformationFrameView.getPrefHeight());

			dialog.initOwner(this.getScene().getWindow());
			Scene dialogScene = new Scene(tableInformationFrameView);
			dialog.setScene(dialogScene);
			dialog.show();
		} catch (IOException e) {
			LOGGER.error(ValueUtil.toString(e));
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.kyj.fx.voeditor.visual.component.sql.view.SqlPane#getTableName(javafx
	 * .scene.control.TreeItem)
	 */
	@Override
	public String getSelectedTreeByTableName(TreeItem<DatabaseItemTree<String>> selectItem) {

		String tableName = null;
		if (selectItem != null) {
			DatabaseItemTree<String> value = selectItem.getValue();

			// 클래스타입이 TableItemTree타입만 처리
			if (TableItemTree.class.isAssignableFrom(value.getClass()) && (!ColumnItemTree.class.isAssignableFrom(value.getClass()))) {
				tableName = value.getName();
			}
		}

		return tableName;
	}

	/*
	 * 선택된 트리로부터 스키마레벨을 리턴함. (non-Javadoc)
	 *
	 * @see
	 * com.kyj.fx.voeditor.visual.component.sql.view.SqlPane#getTableName(javafx
	 * .scene.control.TreeItem)
	 */
	@Override
	public String getSchemaName(TreeItem<DatabaseItemTree<String>> selectItem) {

		String tableName = null;
		if (selectItem != null) {
			DatabaseItemTree<String> value = selectItem.getValue();
			// 클래스타입이 TableItemTree타입만 처리

			// System.out.println(DatabaseItemTree.class.isAssignableFrom(value.getClass()));
			if (!(value instanceof TableItemTree) && !(value instanceof ColumnItemTree)) {
				return selectItem.getValue().getName();
			} else {
				TreeItem<DatabaseItemTree<String>> _tree = selectItem.getParent();
				return getSchemaName(_tree);
			}
		}

		return tableName;
	}

	/*
	 * 선택된 테이블 트리아이템으로부터 DBMS, TABLE명을 받은후, 해당되는 테이블컬럼 목록을 반환받는다. (non-Javadoc)
	 *
	 * @see
	 * com.kyj.fx.voeditor.visual.component.sql.view.SqlPane#getTableColumns(
	 * javafx.scene.control.TreeItem)
	 */
	@Override
	public List<String> getSelectedTreeByTableColumns(TreeItem<DatabaseItemTree<String>> selectItem) {

		List<String> columnList = Collections.emptyList();
		if (selectItem != null) {

			TreeItem<DatabaseItemTree<String>> schemaTree = selectItem.getParent();
			DatabaseItemTree<String> schemaItem = schemaTree.getValue();
			String databaseName = schemaItem.getName();
			DatabaseItemTree<String> tableName = selectItem.getValue();
			String sql = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.SQL_TABLE_COLUMNS_WRAPPER);

			// 클래스타입이 TableItemTree타입만 처리
			if (TableItemTree.class.isAssignableFrom(tableName.getClass())
					&& (!ColumnItemTree.class.isAssignableFrom(tableName.getClass()))) {

				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("databaseName", databaseName);
				paramMap.put("tableName", tableName);
				sql = getDynmicSQL(sql, paramMap, false);
				columnList = query(sql, paramMap, (param) -> {

				}, (err, bool) -> {
					if (bool)
						DialogUtil.showExceptionDailog(err);
				}).stream().map(m -> {
					Object object = m.get("COLUMN_NAME");
					if (object == null)
						object = m.get("column_name");
					return object.toString();
				}).collect(Collectors.toList());
			}
		}

		return columnList;
	}

	/*
	 *
	 * (non-Javadoc)
	 *
	 * @see com.kyj.fx.voeditor.visual.component.sql.view.SqlPane#
	 * getSelectedTreeByPrimaryKey(javafx.scene.control.TreeItem)
	 */
	@Override
	public List<String> getSelectedTreeByPrimaryKey(TreeItem<DatabaseItemTree<String>> selectItem) {

		ObservableList<TreeItem<DatabaseItemTree<String>>> children = selectItem.getChildren();

		if (children == null || children.isEmpty())
			return Collections.emptyList();

		List<String> primaryKeys = new ArrayList<>();
		for (TreeItem<DatabaseItemTree<String>> item : children) {
			DatabaseItemTree<String> value = item.getValue();
			if (value != null) {
				if (value instanceof ColumnItemTree) {
					ColumnItemTree<?> columnItemTree = (ColumnItemTree<?>) value;
					if (columnItemTree.isPrimaryKey()) {
						primaryKeys.add(columnItemTree.getName());
					}
				}
			}

		}

		return primaryKeys;
	}

}
