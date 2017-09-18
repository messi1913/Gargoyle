/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2016. 6. 11.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.popup;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;

import com.kyj.fx.voeditor.visual.component.ResultDialog;
import com.kyj.fx.voeditor.visual.component.sql.functions.ConnectionSupplier;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;

/***************************
 *
 * 테이블 리소스 조회
 *
 * @author KYJ
 *
 ***************************/
public class TableOpenResourceView {

	private static Logger LOGGER = LoggerFactory.getLogger(TableOpenResourceView.class);
	/**
	 *
	 * @최초생성일 2016. 6. 14.
	 */
	protected ResourceView<Map<String, Object>> delegator;
	protected ConnectionSupplier conSupplier;
	/**
	 * 데이터베이스 드라이버
	 *
	 * @최초생성일 2016. 6. 14.
	 */
	protected String driver;
	// private String title = "TableResourceView";
	private Stage owner;

	public TableOpenResourceView(ConnectionSupplier conSupplier, ResourceView<Map<String, Object>> deligator) {
		this.conSupplier = conSupplier;
		this.delegator = deligator;

		// this.delegator.setTitle(title);
		// this.stage = deligator.getStage();
	}

	public TableOpenResourceView(ConnectionSupplier conSupplier, Stage owner) {
		this.conSupplier = conSupplier;
		this.delegator = new TableResourceView(owner);
		// this.delegator.setTitle(title);
		this.owner = owner;
	}

	public TableOpenResourceView(ConnectionSupplier conSupplier) {
		this.conSupplier = conSupplier;
		this.owner = new Stage();
		this.delegator = new TableResourceView(this.owner);
		// this.delegator.setTitle(title);
	}

	public ResourceView<Map<String, Object>> getView() {
		return this.delegator;
	}

	/********************************
	 * 작성일 : 2016. 6. 14. 작성자 : KYJ
	 *
	 * 팝업호출
	 *
	 * @param parent
	 *            해당화면을 호출한 부모를 파라미터로 함.
	 * @return
	 ********************************/
	public ResultDialog<Map<String, Object>> show() {
//		Window owner = this.owner;
//		return delegator.show(owner, true);
		return show(null);
	}

	public ResultDialog<Map<String, Object>> show(Parent parent) {
		Window owner = this.owner;
		if (parent != null) {
			Scene scene = parent.getScene();
			if (scene != null) {
				owner = scene.getWindow();
			}
		}
		return delegator.show(owner, true);
	}

	/********************************
	 * 작성일 : 2016. 6. 12. 작성자 : KYJ
	 *
	 * 테이블컬럼에 해당하는 필드값을 리턴.
	 *
	 * @param value
	 * @return
	 ********************************/
	public String getTableName(Map<String, Object> value) {
		String tableName = "TABLE_NAME";

		switch (driver) {
		case ResourceLoader.ORG_POSTGRESQL_DRIVER:
			tableName = "table_name";
			break;

		case ResourceLoader.ORG_MARIADB_JDBC_DRIVER:
			tableName = "table_name";
			break;

		case ResourceLoader.ORG_APACHE_DERBY_JDBC:
			tableName = "TABLENAME";
			break;

		case ResourceLoader.ORACLE_JDBC_DRIVER_ORACLEDRIVER:

			break;

		case ResourceLoader.ORG_H2_DRIVER:

			break;

		case ResourceLoader.ORG_SQLITE_JDBC:
			tableName = "tbl_name";
			break;
		default:
			tableName = "TABLE_NAME";
			break;
		}

		return value.get(tableName) == null ? "" : value.get(tableName).toString();
	}

	public String getSchema(Map<String, Object> value) {
		String schemaName = "table_schema";

		switch (driver) {
		case ResourceLoader.ORG_POSTGRESQL_DRIVER:
			schemaName = "table_schema";
			break;

		case ResourceLoader.ORG_MARIADB_JDBC_DRIVER:

			break;
		case ResourceLoader.ORG_APACHE_DERBY_JDBC:
			schemaName = "TABLETYPE";
			break;
		case ResourceLoader.ORACLE_JDBC_DRIVER_ORACLEDRIVER:

			break;

		case ResourceLoader.ORG_H2_DRIVER:

			break;

		case ResourceLoader.ORG_SQLITE_JDBC:
			return "";
		case ResourceLoader.ORG_MSSQL_JDBC_DRIVER:
			schemaName = "TABLE_CAT";
			break;
		default:
			schemaName = "table_schema";
			break;
		}

		return value.get(schemaName) == null ? "" : value.get(schemaName).toString();
	}

	/********************************
	 * 작성일 : 2016. 6. 14. 작성자 : KYJ
	 *
	 * 데이터베이스 카탈로그 or 스키마 or 데이터베이스명을 리턴.
	 *
	 * 데이터베이스마다 다름..
	 *
	 * @param value
	 * @return
	 ********************************/
	public String getDatabaseName(Map<String, Object> value) {
		String databaseName = "TABLE_SCHEMA";

		switch (driver) {
		case ResourceLoader.ORG_POSTGRESQL_DRIVER:
			databaseName = "table_catalog";
			break;

		case ResourceLoader.ORG_MARIADB_JDBC_DRIVER:
			databaseName = "table_schema";
			break;

		case ResourceLoader.ORACLE_JDBC_DRIVER_ORACLEDRIVER:
			databaseName = "OWNER";
			break;

		case ResourceLoader.ORG_H2_DRIVER:

			break;

		case ResourceLoader.ORG_SQLITE_JDBC:
			return "";

		case ResourceLoader.ORG_MSSQL_JDBC_DRIVER:
			databaseName = "TABLE_SCHEM";
			break;
		default:
			databaseName = "TABLE_SCHEMA";
			break;
		}

		return value.get(databaseName) == null ? "" : value.get(databaseName).toString();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 15.
	 */
	public void close() {
		this.delegator.close();
	}

	private class TableResourceView extends ResourceView<Map<String, Object>> {

		private Stage parent;
		private int dutyCloseCount;

		public Stage getStage() {
			return this.parent;
		}

		public TableResourceView(Stage parent) {
			super();
			this.parent = parent;
		}

		@Override
		public void close() {

			if (dutyCloseCount > 0) {
				dutyCloseCount = 0;
				TableOpenResourceView.this.close();
				return;
			}

			dutyCloseCount++;

			super.close();

			if (parent != null)
				parent.close();

			TableOpenResourceView.this.close();

		}

		/*
		 * 초기화 처리 함수.
		 *
		 * @inheritDoc
		 */
		@Override
		protected void init() {
			Platform.runLater(() -> loadTable(false));
		}

		/********************************
		 * 작성일 : 2016. 6. 14. 작성자 : KYJ
		 *
		 * 데이터베이스 테이블 정보를 로드한다.
		 *
		 * @param reload
		 *            데이터베이스에서 다시 요청할지 유무.
		 ********************************/
		protected void loadTable(boolean reload) {

			Connection connection = null;
			try {
				connection = conSupplier.get();

				driver = DbUtil.getDriverNameByConnection(connection);
				lvResources.getItems().clear();
				String sql = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.ALL_TABLES, driver);
				if (ValueUtil.isNotEmpty(sql)) {
					List<Map<String, Object>> select = DbUtil.select(connection, sql, 30);
					setResources(select);
					lvResources.getItems().addAll(select);
				} else {
					ColumnMapRowMapper mapper = new ColumnMapRowMapper();
					List<Map<String, Object>> tables = DbUtil.tables(connection, "", rs -> {

						try {
							Map<String, Object> mapRow = mapper.mapRow(rs, rs.getRow());
							return mapRow;
						} catch (SQLException e) {
							e.printStackTrace();
						}

						return Collections.emptyMap();
					});

					setResources(tables);
					lvResources.getItems().addAll(tables);

				}

				/*
				 * TODO 추후 아래 메타정보를 이용하여 고칠 수 있게할것. REFERENCES.
				 *
				 * http://docs.oracle.com/javase/6/docs/api/java/sql/
				 * DatabaseMetaData.html#getColumns(java.lang.String,%20java.
				 * lang.String,%20java.lang.String,%20java.lang.String)
				 *
				 */
				// ResultSet tables = connection.getMetaData().getTables(null,
				// null, "%", new String[]{"TABLE"});
				//
				// while(tables.next())
				// {
				// String TABLE_CAT = tables.getString(1);
				// String TABLE_SCHEM = tables.getString(2);
				// String TABLE_NAME = tables.getString(3);
				// HashMap<String, Object> hashMap = new
				// HashMap<String,Object>();
				// hashMap.put("catalog", TABLE_CAT);
				// hashMap.put("schema", TABLE_SCHEM);
				// hashMap.put("tableName", TABLE_NAME);
				// }

			} catch (Exception e1) {
				LOGGER.error(ValueUtil.toString(e1));
				DialogUtil.showExceptionDailog(e1);
			} finally {
				try {
					DbUtil.close(connection);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		/*
		 * @inheritDoc
		 */
		@Override
		public StringConverter<Map<String, Object>> stringConverter() {

			return new StringConverter<Map<String, Object>>() {

				@Override
				public String toString(Map<String, Object> object) {
					// LOGGER.debug(object.toString());
					return String.format("%s // ( %s %s )", getTableName(object).toString(), getSchema(object), getDatabaseName(object));
				}

				@Override
				public Map<String, Object> fromString(String string) {
					return Collections.emptyMap();
				}
			};
		}

		@Override
		public void btnRefleshOnMouseClick(MouseEvent event) {

			// 데이터베이스 테이블정보를 리로드.
			Platform.runLater(() -> loadTable(true));
		}

		/*
		 * @inheritDoc
		 */
		@Override
		public boolean isMatch(Map<String, Object> value, String text) {
			boolean equals = getTableName(value).toUpperCase().indexOf(text.toUpperCase()) >= 0;
			return equals;
		}
	}

	/**
	 * @return
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 9.
	 */
	public ResultDialog<Map<String, Object>> getResult() {
		return delegator.getResult();
	}

}
