/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2015. 10. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.popup;

import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.kyj.fx.voeditor.visual.component.text.SqlKeywords;
import com.kyj.fx.voeditor.visual.framework.KeyValue;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;
import com.kyj.fx.voeditor.visual.momory.SkinManager;
import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import kyj.Fx.dao.wizard.core.model.vo.TableModelDVO;

/**
 * 단순 SQL조회 결과를 그리드로 보여주기위한 VIEW
 *
 * 주의 1회성 뷰이기때문에 한번 재사용은 금지한다.
 *
 * @author KYJ
 *
 */
public class SimpleSQLResultView extends BorderPane {

	private static Logger LOGGER = LoggerFactory.getLogger(SimpleSQLResultView.class);
	private String sql;
	private SqlKeywords sqlKeywords;
	private SqlKeywords mappingedSqlKeywords;
	private TableView<Map<String, Object>> tbResult;
	private TableView<KeyValue> tbBind;
	private TableColumn<KeyValue, Object> keyColumn;
	private TableColumn<KeyValue, Object> valueColumn;

	private Map<String, Object> param;
	private Stage stage = new Stage();
	private List<TableModelDVO> columns = FXCollections.observableArrayList();
	private SplitPane splitPane;

	public SimpleSQLResultView() {
		this.sql = "";
		this.param = Collections.emptyMap();
	}

	public SimpleSQLResultView(String sql, Map<String, Object> param) {
		this.sql = sql;

		sqlKeywords = new SqlKeywords();
		mappingedSqlKeywords = new SqlKeywords();

		tbResult = new TableView<Map<String, Object>>();
		tbBind = new TableView<KeyValue>();
		this.param = new HashMap<>(param);

		keyColumn = new TableColumn<>("Key");
		valueColumn = new TableColumn<>("Value");
		tbBind.getColumns().add(keyColumn);
		tbBind.getColumns().add(valueColumn);

		keyColumn.setCellValueFactory(new PropertyValueFactory<KeyValue, Object>("key"));
		valueColumn.setCellValueFactory(new PropertyValueFactory<KeyValue, Object>("value"));

		splitPane = new SplitPane(sqlKeywords, mappingedSqlKeywords, tbBind);
		tbBind.setMinWidth(80);
		tbBind.setMaxWidth(180);
		splitPane.setDividerPositions(0.5);

		splitPane.setOrientation(Orientation.HORIZONTAL);
		this.setCenter(splitPane);
		// this.setRight(tbBind);
		this.setBottom(tbResult);

	}

	/**
	 * SQL을 실행하고 결과를 팝업형태로 보여준다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 21.
	 */
	public void executeSQL() {
		executeSQL(null);
	}

	public void executeSQL(Node root) {

		LOGGER.debug("sql check....");
		if (this.sql == null || this.sql.isEmpty()) {
			return;
		}

		LOGGER.debug("param bind....");
		/* [시작] 바인드변수 맵핑시키는 테이블 */
		param.keySet().stream().forEach(key -> {
			KeyValue keyValue = new KeyValue();
			keyValue.setKey(key);
			keyValue.setValue(param.get(key));
			tbBind.getItems().add(keyValue);
		});
		/* [끝] 바인드변수 맵핑시키는 테이블 */

		// String wrapperedSQL =
		// ConfigResourceLoader.getInstance().get(ConfigResourceLoader.SQL_LIMIT_WRAPPER);
		// LOGGER.debug(String.format("wrapperedSql : %s", wrapperedSQL));
		// param.put(ConfigResourceLoader.USER_SQL, this.sql);
		// String velocityToText = ValueUtil.getVelocityToText(wrapperedSQL,
		// param);
		// param.remove(ConfigResourceLoader.USER_SQL);

		// LOGGER.debug(String.format("before velocityText %s",
		// velocityToText));
		String sql = ValueUtil.getVelocityToText(this.sql, param);
		LOGGER.debug(String.format("Convert SQL : \n%s", sql));

		// columns = new ArrayList<>();
		try {

			// Parameters.
			MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(param);

			// RowMapper.
			RowMapper<Map<String, Object>> rowMapper = (rs, row) -> {
				Map<String, Object> hashMap = new HashMap<String, Object>();

				final ResultSetMetaData metaData = rs.getMetaData();
				final int columnCount = metaData.getColumnCount();

				hashMap = new HashMap<String, Object>();
				for (int c = 1; c <= columnCount; c++) {
					String columnLabel = metaData.getColumnLabel(c);

					if (row == 1) {
						TableModelDVO tableModelDVO = new TableModelDVO();
						tableModelDVO.setDatabaseColumnName(columnLabel);
						String columnTypeName = metaData.getColumnTypeName(c);
						// metaData.getColumnType(column)

						if ("unknown".equals(columnTypeName)) {
							LOGGER.debug("unknown type detected....");
							LOGGER.debug("convert varchar type...");
							LOGGER.debug("type Number : " + metaData.getColumnType(c));

							// TODO 잠재적인 버그가 있을 가능성이 있을지 ???? 확신이 안섬.
							columnTypeName = "varchar";

						}
						tableModelDVO.setDabaseTypeName(columnTypeName);
						columns.add(tableModelDVO);
					}

					hashMap.put(columnLabel, rs.getString(c));
				}
				return hashMap;
			};

			// Execute.
			clear();
			List<Map<String, Object>> select = execute(sql, mapSqlParameterSource, rowMapper);
			if (select != null && !select.isEmpty()) {
				createColumns(columns);
				tbResult.getItems().addAll(select);
			}

		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
			// DialogUtil.showConfirmDialog();
			// 에러 다이얼로그 수정.
			DialogUtil.showExceptionDailog(SharedMemory.getPrimaryStage(), e);
		}

		Iterator<String> iterator = param.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			Object value = param.get(key);
			if (value == null || value.toString().isEmpty())
				param.put(key, null);
			else if (value instanceof List) {
				List<Object> items = (List<Object>) value;
				// StringBuffer sb = new StringBuffer();
				// for (Object obj : items) {
				// sb.append(obj).append(",");
				// }

				// if (items != null && !items.isEmpty()) //bug fix. sb가 빈 경우
				// 에러발생.
				// sb.setLength(sb.length() - 1);
				param.put(key, items);
			} else
				param.put(key, value);
		}

		this.mappingedSqlKeywords.setContent(ValueUtil.getVelocityToText(this.sql, param, true));
		this.sqlKeywords.setContent(this.sql);

	}

	private List<Map<String, Object>> execute(String sql, MapSqlParameterSource mapSqlParameterSource,
			RowMapper<Map<String, Object>> rowMapper) throws Exception {

		/* Custom DataSource 정보가 존재한다면 Custom DataSource를 활용한다. */

		ResourceLoader instance = ResourceLoader.getInstance();
		String url = instance.get(ResourceLoader.CUSTOM_DAOWIZARD_KEY_JDBC_URL, null);
		if (ValueUtil.isNotEmpty(url)) {
			String driver = instance.get(ResourceLoader.CUSTOM_DAOWIZARD_KEY_JDBC_DRIVER, "");
			String id = instance.get(ResourceLoader.CUSTOM_DAOWIZARD_KEY_JDBC_ID, "");
			String pass = instance.get(ResourceLoader.CUSTOM_DAOWIZARD_KEY_JDBC_PASS, "");

			if (ValueUtil.isNotEmpty(pass))
				pass = DbUtil.decryp(pass);
			DataSource dataSource = DbUtil.getDataSource(driver, url, id, pass);

			return DbUtil.selectLimit(dataSource, sql, mapSqlParameterSource, rowMapper, 100);
		}

		return DbUtil.selectLimit(sql, mapSqlParameterSource, rowMapper, 100);
	}

	/**
	 * 두번이상 호출하지말것. 1회성 팝업뷰
	 *
	 * @Date 2015. 10. 21.
	 * @throws IOException
	 * @User KYJ
	 */
	public void show() throws IOException {

		LOGGER.debug("SHOW SimpleSQLResult View.....");
		LOGGER.debug("call executeSQL function....");
		try {
			executeSQL(this);
			LOGGER.debug("end function");
			Scene scene = new Scene(this, 1100, 700);
			scene.getStylesheets().add(SkinManager.getInstance().getSkin());
			stage.setScene(scene);
			stage.setAlwaysOnTop(false);
			// stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(SharedMemory.getPrimaryStage());
			stage.showAndWait();

			// 재사용금지 1회성 뷰
			close();
		} catch (Exception e) {
			DialogUtil.showExceptionDailog(SharedMemory.getPrimaryStage(), e);
		}
	}

	private void close() {

		LOGGER.debug("Close Request.... SimpleSQLResult View.....");
		// tableView.getColumns().clear();
		// columns.clear();
		stage.close();
	}

	private void clear() {
		tbResult.getColumns().clear();
	}

	public List<TableModelDVO> getColumns() {
		return this.columns;
	}

	private void createColumns(List<TableModelDVO> columns) {

		ObservableList<TableColumn<Map<String, Object>, ?>> tableColumns = tbResult.getColumns();

		for (TableModelDVO column : columns) {
			String databaseColumnName = column.getDatabaseColumnName();
			TableColumn e = new TableColumn(databaseColumnName);
			e.setCellValueFactory(new MapValueFactory<Object>(databaseColumnName));
			tableColumns.add(e);
		}

	}

}
