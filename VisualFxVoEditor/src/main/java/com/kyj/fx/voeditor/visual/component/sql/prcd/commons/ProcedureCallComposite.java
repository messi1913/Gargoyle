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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.ProcedureReader;
import com.kyj.fx.voeditor.visual.component.sql.functions.ConnectionSupplier;
import com.kyj.fx.voeditor.visual.exceptions.GagoyleRuntimeException;
import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
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
	private TableView<ProcedureCallVo> tbProcedureCols;

	@FXML
	private TableColumn<ProcedureCallVo, String> colName;
	@FXML
	private TableColumn<ProcedureCallVo, Object> colValue;
	@FXML
	private TableColumn<ProcedureCallVo, Boolean> colNullable;
	@FXML
	private TableColumn<ProcedureCallVo, String> colType;

	// private ObjectProperty<ConnectionSupplier> connectionSuplier = new SimpleObjectProperty<>();

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
	public void addProcedureCallVo(ProcedureCallVo vo) {
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
	private RowMapper<ProcedureCallVo> mapper = new RowMapper<ProcedureCallVo>() {
		
		/* 
     *  <OL>
     *  <LI><B>PROCEDURE_CAT</B> String {@code =>} procedure catalog (may be <code>null</code>)
     *  <LI><B>PROCEDURE_SCHEM</B> String {@code =>} procedure schema (may be <code>null</code>)
     *  <LI><B>PROCEDURE_NAME</B> String {@code =>} procedure name
     *  <LI><B>COLUMN_NAME</B> String {@code =>} column/parameter name
     *  <LI><B>COLUMN_TYPE</B> Short {@code =>} kind of column/parameter:
     *      <UL>
     *      <LI> procedureColumnUnknown - nobody knows
     *      <LI> procedureColumnIn - IN parameter
     *      <LI> procedureColumnInOut - INOUT parameter
     *      <LI> procedureColumnOut - OUT parameter
     *      <LI> procedureColumnReturn - procedure return value
     *      <LI> procedureColumnResult - result column in <code>ResultSet</code>
     *      </UL>
     *  <LI><B>DATA_TYPE</B> int {@code =>} SQL type from java.sql.Types
     *  <LI><B>TYPE_NAME</B> String {@code =>} SQL type name, for a UDT type the
     *  type name is fully qualified
     *  <LI><B>PRECISION</B> int {@code =>} precision
     *  <LI><B>LENGTH</B> int {@code =>} length in bytes of data
     *  <LI><B>SCALE</B> short {@code =>} scale -  null is returned for data types where
     * SCALE is not applicable.
     *  <LI><B>RADIX</B> short {@code =>} radix
     *  <LI><B>NULLABLE</B> short {@code =>} can it contain NULL.
     *      <UL>
     *      <LI> procedureNoNulls - does not allow NULL values
     *      <LI> procedureNullable - allows NULL values
     *      <LI> procedureNullableUnknown - nullability unknown
     *      </UL>
     *  <LI><B>REMARKS</B> String {@code =>} comment describing parameter/column
     *  <LI><B>COLUMN_DEF</B> String {@code =>} default value for the column, which should be interpreted as a string when the value is enclosed in single quotes (may be <code>null</code>)
     *      <UL>
     *      <LI> The string NULL (not enclosed in quotes) - if NULL was specified as the default value
     *      <LI> TRUNCATE (not enclosed in quotes)        - if the specified default value cannot be represented without truncation
     *      <LI> NULL                                     - if a default value was not specified
     *      </UL>
     *  <LI><B>SQL_DATA_TYPE</B> int  {@code =>} reserved for future use
     *  <LI><B>SQL_DATETIME_SUB</B> int  {@code =>} reserved for future use
     *  <LI><B>CHAR_OCTET_LENGTH</B> int  {@code =>} the maximum length of binary and character based columns.  For any other datatype the returned value is a
     * NULL
     *  <LI><B>ORDINAL_POSITION</B> int  {@code =>} the ordinal position, starting from 1, for the input and output parameters for a procedure. A value of 0
     *is returned if this row describes the procedure's return value.  For result set columns, it is the
     *ordinal position of the column in the result set starting from 1.  If there are
     *multiple result sets, the column ordinal positions are implementation
     * defined.
     *  <LI><B>IS_NULLABLE</B> String  {@code =>} ISO rules are used to determine the nullability for a column.
     *       <UL>
     *       <LI> YES           --- if the column can include NULLs
     *       <LI> NO            --- if the column cannot include NULLs
     *       <LI> empty string  --- if the nullability for the
     * column is unknown
     *       </UL>
     *  <LI><B>SPECIFIC_NAME</B> String  {@code =>} the name which uniquely identifies this procedure within its schema.
     *  </OL>
		 */
		@Override
		public ProcedureCallVo mapRow(ResultSet rs, int rowNum) throws SQLException {

			ProcedureCallVo vo = new ProcedureCallVo();
			vo.setName(rs.getString("COLUMN_NAME"));
			vo.setNullable(rs.getBoolean("NULLABLE"));
			vo.setType(rs.getString("TYPE_NAME"));

			return vo;
		}
	};

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 30.
	 */
	public void prepare() {

		String catalog = this.catalog.get();
		String schema = this.schema.get();
		String procedureName = this.procedureName.get();

		ConnectionSupplier connectionSupplier = connectionSupplier();
		if (connectionSupplier == null) {
			throw new GagoyleRuntimeException("Connection Supplier is empty.");
		}
		try (Connection con = connectionSupplier.get()) {

			List<Map<String, Object>> procedures = DbUtil.getProcedures(con, catalog, schema, procedureName);
			if (procedures.isEmpty()) {
				this.txtProcedureCont.setText("Procedure does not exists.");
				return;
			}

			// [start] Parameter update
			List<ProcedureCallVo> procedureColumns = DbUtil.getProcedureColumns(con, catalog, schema, procedureName, "%",
					new Callback<ResultSet, List<ProcedureCallVo>>() {

						@Override
						public List<ProcedureCallVo> call(ResultSet param) {

							List<ProcedureCallVo> arr = new ArrayList<>();
							int seq = 0;
							try {
								while (param.next()) {
									ProcedureCallVo v = mapper.mapRow(param, seq++);
									arr.add(v);
								}
							} catch (Exception e) {
								LOGGER.error(ValueUtil.toString(e));
							}
							return arr;
						}
					});

			tbProcedureCols.getItems().addAll(procedureColumns);
			// [end] Parameter update

			// [start] Content Update
			Map<String, Object> map = procedures.get(0);
			ProcedureReader procedureReader = getProcedureReader();

			map.put("catalog", map.get("PROCEDURE_CAT"));
			map.put("schema", map.get("PROCEDURE_SCHEM"));
			map.put("procedureName", map.get("PROCEDURE_NAME"));

			String readProcedure = procedureReader.readProcedure(map);
			this.txtProcedureCont.setText(readProcedure);
			// [end] Content Update

		} catch (Exception e) {
			String msg = ValueUtil.toString(e);
			this.txtProcedureCont.setText(msg);
			LOGGER.error(msg);
		}

	}

	@FXML
	public void btnExecuteOnAction() {
		execute();
	}

	private ObjectProperty<Callback<ResultSet, T>> callback = new SimpleObjectProperty<>();

	private ObjectProperty<T> result = new SimpleObjectProperty<>();

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 29.
	 * @return
	 */
	public void execute() {
		String cont = this.txtProcedureCont.getText();
		if (ValueUtil.isEmpty(cont))
			return;

		ConnectionSupplier connectionSupplier = connectionSupplier();
		if (connectionSupplier == null) {
			throw new GagoyleRuntimeException("Connection Supplier is empty.");
		}

		String paramStatement = "";
		ObservableList<ProcedureCallVo> items = this.tbProcedureCols.getItems();
		if (!items.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			sb.append("(");
			for (ProcedureCallVo v : items) {
				Object value = v.getValue();
				if (ValueUtil.isNotEmpty(value)) {
					sb.append("?").append(" ");
				}
			}
			sb.append(")");
			paramStatement = sb.toString();
		}

		try (Connection con = connectionSupplier.get()) {

			String statement = String.format("{ call %s %s}", cont, paramStatement);
			CallableStatement prepareCall = con.prepareCall(statement);

			for (ProcedureCallVo v : items) {
				String name = v.getName();
				Object value = v.getValue();
				if (ValueUtil.isNotEmpty(value))
					prepareCall.setObject(name, value);
			}

			ResultSet rs = prepareCall.executeQuery();

			Callback<ResultSet, T> callback2 = callback.get();
			if (callback2 != null) {
				T call = callback2.call(rs);
				setResult(call);
			}

		} catch (SQLException e) {
			throw new GagoyleRuntimeException(e);
		}

	}

	public final ObjectProperty<T> resultProperty() {
		return this.result;
	}

	public final T getResult() {
		return this.resultProperty().get();
	}

	private final void setResult(final T result) {
		this.resultProperty().set(result);
	}

}
