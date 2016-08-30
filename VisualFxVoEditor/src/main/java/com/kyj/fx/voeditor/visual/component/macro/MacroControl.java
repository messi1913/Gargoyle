/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.macro
 *	작성일   : 2016. 8. 30.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.macro;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.FxClipboardUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.util.Callback;

/**
 * SQL 매크로 기능을 지우너하기 위한 컨트롤러
 * 
 * @author KYJ
 *
 */
public class MacroControl extends Control {

	private static final Logger LOGGER = LoggerFactory.getLogger(MacroControl.class);

	/**
	 * 접속가능한 데이터베이스 Connection을 리턴
	 * 
	 * @최초생성일 2016. 8. 30.
	 */
	private Supplier<Connection> connectionSupplier;

	public MacroControl(Supplier<Connection> connectionSupplier) {
		this.connectionSupplier = connectionSupplier;
	}

	/* (non-Javadoc)
	 * @see javafx.scene.control.Control#createDefaultSkin()
	 */
	@Override
	protected Skin<?> createDefaultSkin() {
		return new MacroBaseSkin(this);
	}

	/**
	 * Start 버튼을 클릭하면 결과가 리턴된다. param으로 입력받은 데이터는 textArea에서 적혀져있는 텍스트문자열.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 30.
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public void start(TableView<Map<String, String>> tbResult, String param) throws Exception {

		Connection connection = this.connectionSupplier.get();

		tbResult.getItems().clear();
		tbResult.getColumns().clear();

		//		try {

		String[] split = param.split(";");

		connection.setAutoCommit(false);
		try {
			for (String sql : split) {
				String _sql = sql.trim();
				if (_sql.isEmpty())
					continue;

				boolean dml = DbUtil.isDml(_sql);
				if (dml) {
					DbUtil.update(connection, _sql);
				} else {
					DbUtil.select(connection, _sql, 30, 1000,
							new BiFunction<ResultSetMetaData, ResultSet, List<Map<String, ObjectProperty<Object>>>>() {

								@Override
								public List<Map<String, ObjectProperty<Object>>> apply(ResultSetMetaData t, ResultSet rs) {

									try {
										int columnCount = t.getColumnCount();

										for (int i = 1; i <= columnCount; i++) {
											String columnName = t.getColumnName(i);
											TableColumn<Map<String, String>, String> tbCol = new TableColumn<>(columnName);
											tbCol.setCellFactory(
													new Callback<TableColumn<Map<String, String>, String>, TableCell<Map<String, String>, String>>() {

														@Override
														public TableCell<Map<String, String>, String> call(
																TableColumn<Map<String, String>, String> param) {
															return new TableCell<Map<String, String>, String>() {

																@Override
																protected void updateItem(String item, boolean empty) {
																	super.updateItem(item, empty);
																	if (empty) {
																		setGraphic(null);
																	} else {
																		setGraphic(new Label(item));
																	}
																}

															};
														}
													});

											tbCol.setCellValueFactory(param -> {
												return new SimpleStringProperty(param.getValue().get(columnName));
											});

											tbResult.getColumns().add(tbCol);
										}

										while (rs.next()) {
											Map<String, String> hashMap = new HashMap<>();

											for (int i = 1; i <= columnCount; i++) {
												String columnName = t.getColumnName(i);
												String value = rs.getString(columnName);
												hashMap.put(columnName, value);

											}
											tbResult.getItems().add(hashMap);

										}

									} catch (SQLException e) {
										throw new RuntimeException(e);
									}
									return Collections.emptyList();
								}

							});
				}
			}
		} catch (Exception e) {
			connection.rollback();
			throw new RuntimeException(e);
		} finally {
			DbUtil.close(connection);
		}

	}

	/**
	 * 매크로 동작을 멈춘다.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 30.
	 * @return
	 */
	public boolean stop() {
		return false;
	}

	public void tbResultOnKeyReleased() {

		Skin<?> skin = this.getSkin();
		if (!(skin instanceof MacroBaseSkin)) {
			return;
		}

		MacroBaseSkin mskin = (MacroBaseSkin) skin;
		TableView<Map<String, String>> tbResult = mskin.getTbResult();
		int type = 1;
		

		ObservableList<TablePosition> selectedCells = tbResult.getSelectionModel().getSelectedCells();

		TablePosition tablePosition = selectedCells.get(0);
		TableColumn tableColumn = tablePosition.getTableColumn();
		int row = tablePosition.getRow();
		int col = tbResult.getColumns().indexOf(tableColumn);

		switch (type) {
		case 1:
			StringBuilder sb = new StringBuilder();
			for (TablePosition cell : selectedCells) {
				// TODO :: 첫번째 컬럼(행 선택 기능)도 빈값으로 복사됨..
				// 행변경시
				if (row != cell.getRow()) {
					sb.append("\n");
					row++;
				}
				// 열 변경시
				else if (col != tbResult.getColumns().indexOf(cell.getTableColumn())) {
					sb.append("\t");
				}
				Object cellData = cell.getTableColumn().getCellData(cell.getRow());
				sb.append(ValueUtil.decode(cellData, cellData, "").toString());
			}
			FxClipboardUtil.putString(sb.toString());

			break;
		case 2:
			Object cellData = tableColumn.getCellData(row);
			FxClipboardUtil.putString(ValueUtil.decode(cellData, cellData, "").toString());
			break;
		}

	}

}
