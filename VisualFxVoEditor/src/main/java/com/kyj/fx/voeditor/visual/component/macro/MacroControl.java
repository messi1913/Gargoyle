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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
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
		DbUtil.select(connection, param, 30, -1, new BiFunction<ResultSetMetaData, ResultSet, List<Map<String, ObjectProperty<Object>>>>() {

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
									public TableCell<Map<String, String>, String> call(TableColumn<Map<String, String>, String> param) {
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

		//		} catch (Exception e) {
		//			errorHandler.accept(e);
		//		}

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

}
