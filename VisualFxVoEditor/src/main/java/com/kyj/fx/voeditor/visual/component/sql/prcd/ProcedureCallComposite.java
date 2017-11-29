/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.prcd
 *	작성일   : 2017. 11. 29.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.prcd;

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

	private ObjectProperty<ConnectionSupplier> connectionSuplier = new SimpleObjectProperty<>();

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
	 * 프로시저 리더를 구현
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 29.
	 * @return
	 */
	public abstract ProcedureReader getProcedureReader();

	private BeanPropertyRowMapper<ProcedureCallVo> mapper = new BeanPropertyRowMapper<>(ProcedureCallVo.class);

	public void prepare() {

		String catalog = this.catalog.get();
		String schema = this.schema.get();
		String procedureName = this.procedureName.get();

		ConnectionSupplier connectionSupplier = connectionSuplier.get();
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

		ConnectionSupplier connectionSupplier = connectionSuplier.get();
		if (connectionSupplier == null) {
			throw new GagoyleRuntimeException("Connection Supplier is empty.");
		}

		ObservableList<ProcedureCallVo> items = this.tbProcedureCols.getItems();

		// TODO Update Parameter
		try (Connection con = connectionSupplier.get()) {

			String statement = String.format("{ call %s }", cont);
			CallableStatement prepareCall = con.prepareCall(statement);

			for (ProcedureCallVo v : items) {
				String name = v.getName();
				Object value = v.getValue();
				prepareCall.setObject(name, value);
			}

			ResultSet rs = prepareCall.executeQuery();

			Callback<ResultSet, T> callback2 = callback.get();
			if (callback2 != null) {
				T call = callback2.call(rs);
				result.set(call);
			}

		} catch (SQLException e) {
			throw new GagoyleRuntimeException(e);
		}

	}

}
