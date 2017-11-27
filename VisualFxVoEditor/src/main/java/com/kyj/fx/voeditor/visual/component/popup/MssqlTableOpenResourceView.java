/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2016. 6. 11.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.popup;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;

import com.kyj.fx.voeditor.visual.component.sql.functions.ConnectionSupplier;
import com.kyj.fx.voeditor.visual.exceptions.GargoyleException;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/***************************
 *
 * 테이블 리소스 조회
 *
 * @author KYJ
 *
 ***************************/
public class MssqlTableOpenResourceView extends TableOpenResourceView {

	private static Logger LOGGER = LoggerFactory.getLogger(MssqlTableOpenResourceView.class);

	public MssqlTableOpenResourceView(ConnectionSupplier conSupplier) {
		this(conSupplier, new Stage());
	}

	public MssqlTableOpenResourceView(ConnectionSupplier conSupplier, Stage window) {
		super(conSupplier, (ResourceView<Map<String, Object>>) null);
		this.conSupplier = conSupplier;
		this.delegator = new MssqlTableResourceView(window);
		this.delegator.setTitle("MssqlTableOpenResourceView");
	}

	@Override
	public void close() {
		this.delegator.close();
	}

	class MssqlTableResourceView extends ResourceView<Map<String, Object>> {

		private Stage parent;

		public Stage getStage() {
			return this.parent;
		}

		public MssqlTableResourceView(Stage parent) {
			super();
			this.parent = parent;
		}

		int dutyCount;

		@Override
		public void close() {
			super.close();

			if (dutyCount > 0) {
				dutyCount = 0;
				return;
			}
			dutyCount++;
			MssqlTableOpenResourceView.this.close();
		}

		/*
		 * 초기화 처리 함수. <br/>
		 *
		 * @inheritDoc
		 */
		@Override
		protected void init() {
			Platform.runLater(() -> loadTable(false));
		}

		/*
		 * 데이터 리로딩 <br/>
		 * 
		 * (non-Javadoc)
		 * 
		 * @see com.kyj.fx.voeditor.visual.component.popup.ResourceView#
		 * btnRefleshOnMouseClick(javafx.scene.input.MouseEvent)
		 */
		@Override
		public void btnRefleshOnMouseClick(MouseEvent event) {

			// 데이터베이스 테이블정보를 리로드.
			Platform.runLater(() -> loadTable(true));
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

			if (lvResources.getItems().isEmpty() || reload) {
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

						// [start] Find Tables
						List<Map<String, Object>> tables = tables(connection, "", rs -> {

							try {
								Map<String, Object> mapRow = mapper.mapRow(rs, rs.getRow());
								mapRow.put("TABLE_NAME", mapRow.get("TABLE_SCHEM") + "." + mapRow.get("TABLE_NAME"));
								mapRow.put("TABLE_CAT", mapRow.get("TABLE_CAT"));
								mapRow.put("TABLE_SCHEM", mapRow.get("TABLE_SCHEM"));
								mapRow.put("SIMPLE_TABLE_NAME", mapRow.get("TABLE_NAME"));

								return mapRow;
							} catch (SQLException e) {
								e.printStackTrace();
							}

							return Collections.emptyMap();
						});
						// [end] Find Tables

						// [start] Find Procedures
						List<Map<String, Object>> procedures = procedures(connection, "", rs -> {

							try {
								Map<String, Object> mapRow = mapper.mapRow(rs, rs.getRow());

								// 코드변경을 줄이기 위해 테이블명형태로 변환
								// mapRow.put("PROCEDURE_CAT",
								// mapRow.get("PROCEDURE_CAT"));
								// mapRow.put("PROCEDURE_SCHEM",
								// mapRow.get("TABLE_CAT"));
								// mapRow.put("PROCEDURE_NAME",
								// mapRow.get("TABLE_SCHEM"));

								String procedureName = mapRow.get("PROCEDURE_NAME").toString();
								// int indexOf = procedureName.indexOf(";");

								int lastIndexOf = ValueUtil.lastIndexOf(procedureName, ';');
								if (lastIndexOf >= 0) {
									procedureName = procedureName.substring(0, lastIndexOf);
								}

								mapRow.put("TABLE_NAME", mapRow.get("PROCEDURE_SCHEM") + "." + procedureName);
								mapRow.put("TABLE_CAT", mapRow.get("PROCEDURE_CAT"));
								mapRow.put("TABLE_SCHEM", mapRow.get("PROCEDURE_SCHEM"));
								mapRow.put("SIMPLE_TABLE_NAME", procedureName);

								return mapRow;
							} catch (SQLException e) {
								e.printStackTrace();
							}

							return Collections.emptyMap();
						});
						// [end] Find Procedures

						List<Map<String, Object>> data = new ArrayList<>();
						data.addAll(tables);
						data.addAll(procedures);
						// Collections.addAll(c, elements)
						setResources(data);
						lvResources.getItems().addAll(data);

					}

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

		/*
		 * @inheritDoc
		 */
		@Override
		public boolean isMatch(Map<String, Object> value, String text) {

			boolean equals = getTableName(value).toUpperCase().indexOf(text.toUpperCase()) >= 0;

			return equals;
		}
	}

	/********************************
	 * 작성일 : 2016. 8. 11. 작성자 : KYJ
	 *
	 * 2016-11-10 모든 테이블탐색후 대소문자무시 검색으로 수정 2017-07-12 Connection을 파라미터로 넣어 동적으로
	 * 찾을 수 있게 수정 </br>
	 * </br>
	 * 1.TABLE_CAT String => table catalog (may be null) </br>
	 * 2.TABLE_SCHEM String => table schema (may be null) </br>
	 * 3.TABLE_NAME String => table name </br>
	 * 4.TABLE_TYPE String => table type. Typical types are "TABLE", "VIEW",
	 * "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS",
	 * "SYNONYM". </br>
	 * 5.REMARKS String => explanatory comment on the table </br>
	 * 6.TYPE_CAT String => the types catalog (may be null) </br>
	 * 7.TYPE_SCHEM String => the types schema (may be null) </br>
	 * 8.TYPE_NAME String => type name (may be null) </br>
	 * 9.SELF_REFERENCING_COL_NAME String => name of the designated "identifier"
	 * column of a typed table (may be null) </br>
	 * 10.REF_GENERATION String => specifies how values in
	 * SELF_REFERENCING_COL_NAME are created. Values are "SYSTEM", "USER",
	 * "DERIVED". (may be null) </br>
	 * 
	 * @param connection
	 * @param converter
	 * @return
	 * @throws Exception
	 ********************************/
	public static <T> List<T> tables(Connection connection, String tableNamePattern, Function<ResultSet, T> converter) throws Exception {
		if (converter == null)
			throw new GargoyleException(GargoyleException.ERROR_CODE.PARAMETER_EMPTY, "converter is null ");

		List<T> tables = new ArrayList<>();

		DatabaseMetaData metaData = connection.getMetaData();

		ResultSet schemas = metaData.getCatalogs();
		while (schemas.next()) {

			// String schemaName = schemas.getString(1);
			String catalog = schemas.getString(1);
			// if(schemaName.startsWith("##"))
			// continue;
			ResultSet rs = metaData.getTables(catalog, null,
					"%"/* + tableNamePattern + "%" */, new String[] { "TABLE", });

			String tableNamePatternUpperCase = tableNamePattern.toUpperCase();
			while (rs.next()) {

				// 2016-08-18 특정데이터베이스(sqlite)에서는 인덱스 트리거정보도 동시에 출력된다.
				String tableType = rs.getString(4);
				if ("TABLE".equals(tableType)) {

					String tableName = rs.getString(3);
					if (tableName.toUpperCase().indexOf(tableNamePatternUpperCase) != -1) {
						T apply = converter.apply(rs);
						if (apply != null)
							tables.add(apply);
					}

				}

			}

		}

		return tables;
	}

	/**
	 * 
	 * *
	 * <OL>
	 * <LI><B>PROCEDURE_CAT</B> String {@code =>} procedure catalog (may be
	 * <code>null</code>)
	 * <LI><B>PROCEDURE_SCHEM</B> String {@code =>} procedure schema (may be
	 * <code>null</code>)
	 * <LI><B>PROCEDURE_NAME</B> String {@code =>} procedure name
	 * <LI>reserved for future use
	 * <LI>reserved for future use
	 * <LI>reserved for future use
	 * <LI><B>REMARKS</B> String {@code =>} explanatory comment on the procedure
	 * <LI><B>PROCEDURE_TYPE</B> short {@code =>} kind of procedure:
	 * <UL>
	 * <LI>procedureResultUnknown - Cannot determine if a return value will be
	 * returned
	 * <LI>procedureNoResult - Does not return a return value
	 * <LI>procedureReturnsResult - Returns a return value
	 * </UL>
	 * <LI><B>SPECIFIC_NAME</B> String {@code =>} The name which uniquely
	 * identifies this procedure within its schema.
	 * </OL>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 27.
	 * @param connection
	 * @param procedureNamePattern
	 * @param converter
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> procedures(Connection connection, String procedureNamePattern, Function<ResultSet, T> converter)
			throws Exception {
		if (converter == null)
			throw new GargoyleException(GargoyleException.ERROR_CODE.PARAMETER_EMPTY, "converter is null ");

		List<T> tables = new ArrayList<>();

		DatabaseMetaData metaData = connection.getMetaData();

		ResultSet schemas = metaData.getCatalogs();
		while (schemas.next()) {

			// String schemaName = schemas.getString(1);
			String catalog = schemas.getString(1);
			// if(schemaName.startsWith("##"))
			// continue;

			ResultSet rs = metaData.getProcedures(catalog, null, "%" + procedureNamePattern + "%");

			// String tableNamePatternUpperCase =
			// tableNamePattern.toUpperCase();
			while (rs.next()) {

				// 2016-08-18 특정데이터베이스(sqlite)에서는 인덱스 트리거정보도 동시에 출력된다.
				// String tableType = rs.getString("PROCEDURE_NAME");
				// if ("TABLE".equals(tableType)) {

				// converter.apply(rs);
				// String procedureName = rs.getString("PROCEDURE_NAME");
				// if
				// (tableName.toUpperCase().indexOf(tableNamePatternUpperCase)
				// != -1) {
				T apply = converter.apply(rs);
				if (apply != null)
					tables.add(apply);
				// }

				// }

			}

		} // end while

		return tables;
	}
}
