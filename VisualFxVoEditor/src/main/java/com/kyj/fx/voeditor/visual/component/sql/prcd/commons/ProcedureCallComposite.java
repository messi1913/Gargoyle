/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.prcd
 *	작성일   : 2017. 11. 29.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.prcd.commons;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.ProcedureReader;
import com.kyj.fx.voeditor.visual.component.sql.functions.ConnectionSupplier;
import com.kyj.fx.voeditor.visual.exceptions.GagoyleRuntimeException;
import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * 
 * 프로시저를 실행하고 결과 데이터를 반환하는 기능을 처리하는 view <br/>
 * 
 * @author KYJ
 *
 */
@FXMLController(value = "ProcedureCallView.fxml", isSelfController = true)
public abstract class ProcedureCallComposite<T> extends BorderPane {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProcedureCallComposite.class);

	// database catalog
	private StringProperty catalog;
	// database schema
	private StringProperty schema;
	// databvase procedureName
	private StringProperty procedureName;

	@FXML
	private TextArea txtProcedureCont;
	@FXML
	private TableView<ProcedureParamVo> tbProcedureCols;
	@FXML
	private TableColumn<ProcedureParamVo, Integer> colIndex;
	@FXML
	private TableColumn<ProcedureParamVo, String> colName;
	@FXML
	private TableColumn<ProcedureParamVo, Object> colValue;
	@FXML
	private TableColumn<ProcedureParamVo, Boolean> colNullable;
	@FXML
	private TableColumn<ProcedureParamVo, String> colType;

	@FXML
	private TextField txtCatalog, txtSchema, txtProcedureName;

	// private ObjectProperty<ConnectionSupplier> connectionSuplier = new
	// SimpleObjectProperty<>();

	public ProcedureCallComposite() {

		FxUtil.loadRoot(ProcedureCallComposite.class, this, err -> {
			LOGGER.error(ValueUtil.toString(err));
		});
	}

	@FXML
	public void initialize() {
		catalog = new SimpleStringProperty();
		schema = new SimpleStringProperty();
		procedureName = new SimpleStringProperty();

		txtCatalog.textProperty().bind(catalog);
		txtSchema.textProperty().bind(schema);
		txtProcedureName.textProperty().bind(procedureName);

		colIndex.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Integer>() {

			@Override
			public String toString(Integer object) {
				return object == null ? "" : object.toString();
			}

			@Override
			public Integer fromString(String string) {
				return Integer.valueOf(string);
			}
		}));

		colName.setCellFactory(TextFieldTableCell.forTableColumn());
		colValue.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Object>() {

			@Override
			public String toString(Object object) {
				return object == null ? "" : object.toString();
			}

			@Override
			public Object fromString(String string) {
				return string;
			}
		}));
		colNullable.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Boolean>() {

			@Override
			public String toString(Boolean object) {
				return object == null ? "" : object.toString();
			}

			@Override
			public Boolean fromString(String string) {
				return ValueUtil.isEmpty(string) ? null : Boolean.valueOf(string);
			}
		}));
		colType.setCellFactory(TextFieldTableCell.forTableColumn());

		colIndex.setCellValueFactory(val -> val.getValue().indexProperty());
		colName.setCellValueFactory(val -> val.getValue().nameProperty());
		colValue.setCellValueFactory(val -> val.getValue().valueProperty());
		colNullable.setCellValueFactory(val -> val.getValue().nullableProperty());
		colType.setCellValueFactory(val -> val.getValue().typeProperty());

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 29.
	 * @param vo
	 */
	public void addProcedureCallVo(ProcedureParamVo vo) {
		tbProcedureCols.getItems().add(vo);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 29.
	 */
	public void clearProcedureCallVo() {
		tbProcedureCols.getItems().clear();
	}

	public void setProcedure(String catalog, String schema, String procedureName) {
		this.catalog.set(catalog);
		this.schema.set(schema);
		this.procedureName.set(procedureName);
	}

	/**
	 * 프로시저 리더를 구현 <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 29.
	 * @return
	 */
	public abstract ProcedureReader getProcedureReader();

	/**
	 * 데이터베이스에 접속하기위한 클래스 <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 30.
	 * @return
	 */
	public abstract ConnectionSupplier connectionSupplier();

	/**
	 * Bean Mapper <br/>
	 * 
	 * @최초생성일 2017. 11. 30.
	 */
	private RowMapper<ProcedureParamVo> mapper = new RowMapper<ProcedureParamVo>() {

		/*
		 * <OL> <LI><B>PROCEDURE_CAT</B> String {@code =>} procedure catalog
		 * (may be <code>null</code>) <LI><B>PROCEDURE_SCHEM</B> String {@code
		 * =>} procedure schema (may be <code>null</code>)
		 * <LI><B>PROCEDURE_NAME</B> String {@code =>} procedure name
		 * <LI><B>COLUMN_NAME</B> String {@code =>} column/parameter name
		 * <LI><B>COLUMN_TYPE</B> Short {@code =>} kind of column/parameter:
		 * <UL> <LI> procedureColumnUnknown - nobody knows <LI>
		 * procedureColumnIn - IN parameter <LI> procedureColumnInOut - INOUT
		 * parameter <LI> procedureColumnOut - OUT parameter <LI>
		 * procedureColumnReturn - procedure return value <LI>
		 * procedureColumnResult - result column in <code>ResultSet</code> </UL>
		 * <LI><B>DATA_TYPE</B> int {@code =>} SQL type from java.sql.Types
		 * <LI><B>TYPE_NAME</B> String {@code =>} SQL type name, for a UDT type
		 * the type name is fully qualified <LI><B>PRECISION</B> int {@code =>}
		 * precision <LI><B>LENGTH</B> int {@code =>} length in bytes of data
		 * <LI><B>SCALE</B> short {@code =>} scale - null is returned for data
		 * types where SCALE is not applicable. <LI><B>RADIX</B> short {@code
		 * =>} radix <LI><B>NULLABLE</B> short {@code =>} can it contain NULL.
		 * <UL> <LI> procedureNoNulls - does not allow NULL values <LI>
		 * procedureNullable - allows NULL values <LI> procedureNullableUnknown
		 * - nullability unknown </UL> <LI><B>REMARKS</B> String {@code =>}
		 * comment describing parameter/column <LI><B>COLUMN_DEF</B> String
		 * {@code =>} default value for the column, which should be interpreted
		 * as a string when the value is enclosed in single quotes (may be
		 * <code>null</code>) <UL> <LI> The string NULL (not enclosed in quotes)
		 * - if NULL was specified as the default value <LI> TRUNCATE (not
		 * enclosed in quotes) - if the specified default value cannot be
		 * represented without truncation <LI> NULL - if a default value was not
		 * specified </UL> <LI><B>SQL_DATA_TYPE</B> int {@code =>} reserved for
		 * future use <LI><B>SQL_DATETIME_SUB</B> int {@code =>} reserved for
		 * future use <LI><B>CHAR_OCTET_LENGTH</B> int {@code =>} the maximum
		 * length of binary and character based columns. For any other datatype
		 * the returned value is a NULL <LI><B>ORDINAL_POSITION</B> int {@code
		 * =>} the ordinal position, starting from 1, for the input and output
		 * parameters for a procedure. A value of 0 is returned if this row
		 * describes the procedure's return value. For result set columns, it is
		 * the ordinal position of the column in the result set starting from 1.
		 * If there are multiple result sets, the column ordinal positions are
		 * implementation defined. <LI><B>IS_NULLABLE</B> String {@code =>} ISO
		 * rules are used to determine the nullability for a column. <UL> <LI>
		 * YES --- if the column can include NULLs <LI> NO --- if the column
		 * cannot include NULLs <LI> empty string --- if the nullability for the
		 * column is unknown </UL> <LI><B>SPECIFIC_NAME</B> String {@code =>}
		 * the name which uniquely identifies this procedure within its schema.
		 * </OL>
		 */
		@Override
		public ProcedureParamVo mapRow(ResultSet rs, int rowNum) throws SQLException {

			ProcedureParamVo vo = new ProcedureParamVo();
			vo.setIndex(rowNum);
			vo.setName(rs.getString("COLUMN_NAME"));
			vo.setNullable(rs.getBoolean("NULLABLE"));
			vo.setType(rs.getString("TYPE_NAME"));

			return vo;
		}
	};

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 3.
	 * @param catalog
	 * @param schema
	 * @param procedureName
	 */
	public void prepare(String catalog, String schema, String procedureName) {
		setProcedure(catalog, schema, procedureName);
		prepare();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 30.
	 */
	private void prepare() {
		String catalog = this.catalog.get();
		String schema = this.schema.get();
		String procedureName = this.procedureName.get();
		ConnectionSupplier connectionSupplier = connectionSupplier();

		if (connectionSupplier == null) {
			throw new GagoyleRuntimeException("Connection Supplier is empty.");
		}

		// List<Map<String, Object>> procedures = DbUtil.getProcedures(con,
		// catalog, schema, procedureName);

		doAsynchFx(() -> {
			try (Connection con = connectionSupplier.get()) {
				return DbUtil.getProcedures(con, catalog, schema, procedureName);
			}
		}, (List<Map<String, Object>> procedures) -> {

			if (procedures.isEmpty()) {
				this.txtProcedureCont.setText("Procedure does not exists.");
				return;
			}

			Map<String, Object> map = procedures.get(0);
			ProcedureReader procedureReader = getProcedureReader();

			map.put("catalog", map.get("PROCEDURE_CAT"));
			map.put("schema", map.get("PROCEDURE_SCHEM"));
			map.put("procedureName", map.get("PROCEDURE_NAME"));

			try {
				String readProcedure = procedureReader.readProcedure(map);
				this.txtProcedureCont.setText(readProcedure);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}, ex -> {
			this.txtProcedureCont.setText("Procedure does not exists.");
		});

		doAsynchFx(() -> {

			try (Connection con = connectionSupplier.get()) {
				return DbUtil.getProcedureColumns(con, catalog, schema, procedureName, "%",
						new Callback<ResultSet, List<ProcedureParamVo>>() {

							@Override
							public List<ProcedureParamVo> call(ResultSet param) {

								List<ProcedureParamVo> arr = new ArrayList<>();
								int seq = 0;
								try {
									while (param.next()) {
										ProcedureParamVo v = mapper.mapRow(param, ++seq);
										arr.add(v);
									}
								} catch (Exception e) {
									LOGGER.error(ValueUtil.toString(e));
								}
								return arr;
							}
						});
			}

		}, procedureColumns -> {
			tbProcedureCols.getItems().addAll(procedureColumns);
		}, err -> {
			this.txtProcedureCont.setText("Procedure does not exists.");
		});

	}

	private <R> void doAsynchFx(Callable<R> action, Consumer<R> consume, Consumer<Exception> onError) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {

				try {

					R call = action.call();

					Platform.runLater(() -> {
						consume.accept(call);
					});

				} catch (Exception e) {
					Platform.runLater(() -> {
						onError.accept(e);
					});
				}
			}
		}, "doAsynch-Thread");
		thread.setDaemon(true);
		thread.start();
	}

//	private <R> void doAsynch(Callable<R> action, Consumer<R> consume, Consumer<Exception> onError) {
//		Thread thread = new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//
//				try {
//					R call = action.call();
//					consume.accept(call);
//				} catch (Exception e) {
//					onError.accept(e);
//				}
//			}
//		}, "doAsynch-Thread");
//		thread.setDaemon(true);
//		thread.start();
//	}

	@FXML
	public void btnExecuteOnAction() {
		execute();
	}

	/**
	 * 데이터 결과 변환
	 * 
	 * @최초생성일 2017. 12. 3.
	 */
	private ObjectProperty<Callback<ResultSet, T>> converter = new SimpleObjectProperty<>();

	private ObjectProperty<T> result = new SimpleObjectProperty<>();

	public void execute() {
		// String cont = this.txtProcedureCont.getText();

		String catalog = this.catalog.get();
		String schema = this.schema.get();
		String procedureName = this.procedureName.get();

		String cont = String.format("%s.%s.%s", catalog, schema, procedureName);
		execute(cont);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 29.
	 * @return
	 */
	public void execute(String procedureCont) {

		if (ValueUtil.isEmpty(procedureCont))
			return;

		ConnectionSupplier connectionSupplier = connectionSupplier();
		if (connectionSupplier == null) {
			throw new GagoyleRuntimeException("Connection Supplier is empty.");
		}

		String paramStatement = "";
		ObservableList<ProcedureParamVo> items = this.tbProcedureCols.getItems();
		if (!items.isEmpty()) {
			StringBuffer paramSb = new StringBuffer();

			// 변수의 수는 무조건 바인드 처리해야함.
			for (int i = 0; i < items.size(); i++) {
				paramSb.append(",").append("?");
			}

			paramStatement = "(".concat(paramSb.substring(1)).concat(")");
		}

		try (Connection con = connectionSupplier.get()) {

			// 첫번째 필드는 프로시저명, 두번째 필드는 파라미터 정의
			String statement = String.format(" { call %s %s } ",
					/* this.catalog.get(), */ procedureCont, paramStatement);

			// 현재 카탈로그 set
//			con.setCatalog(this.catalog.get());

			CallableStatement prepareCall = con.prepareCall(statement);

			// TODO 디폴트값 처리하는것 구현할것.
			for (ProcedureParamVo v : items) {
				Object value = v.getValue();
				Integer index = v.getIndex();
				if (v.isNullable() && ValueUtil.isEmpty(value)) {
					prepareCall.setNull(index, java.sql.Types.NULL);
				} else {

					// Nullable이 아닌 경우 null값이 들어온경우 공백을 넣어줌.
					if (value == null)
						value = "";

					prepareCall.setObject(index, value);
				}
			}

			ResultSet rs = prepareCall.executeQuery();

			Callback<ResultSet, T> converter = this.converter.get();
			if (converter != null) {

				// 컨버터로 값을 변환
				T call = converter.call(rs);
				// 결과 처리
				setResult(call);
			} else {
				LOGGER.info("Convert does not defined...");
			}

		} catch (SQLException e) {
			throw new GagoyleRuntimeException(e);
		}

	}

	public final ObjectProperty<T> resultProperty() {
		return this.result;
	}

	final T getResult() {
		return this.resultProperty().get();
	}

	final void setResult(final T result) {
		this.resultProperty().set(result);
	}

	public final ObjectProperty<Callback<ResultSet, T>> converterProperty() {
		return this.converter;
	}

	public final Callback<ResultSet, T> getConverter() {
		return this.converterProperty().get();
	}

	public final void setConverter(final Callback<ResultSet, T> converter) {
		this.converterProperty().set(converter);
	}

}
