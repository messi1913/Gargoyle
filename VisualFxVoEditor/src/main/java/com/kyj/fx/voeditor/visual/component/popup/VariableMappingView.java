/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2016. 2. 13.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.popup;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.PreferencesUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.DefaultStringConverter;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoFieldsDVO;

/**
 * 다이나믹변수 매핑 팝업
 *
 * @author KYJ
 *
 */
public class VariableMappingView extends BorderPane {

	private static final String TITLE = "Dynamic Variable Popup";

	private static Logger LOGGER = LoggerFactory.getLogger(VariableMappingView.class);

	@FXML
	// ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	public static final String VALUE = "value";

	public static final String TYPE = "type";

	public static final String NAME = "name";

	@FXML
	private TableView<DynamicField> gvMappingItems;

	@FXML
	private TableColumn<DynamicField, String> colVarName;
	@FXML
	private TableColumn<DynamicField, String> colVarType;
	@FXML
	private TableColumn<DynamicField, String> colVarValue;

	/**
	 *
	 */
	public VariableMappingView() {

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(VariableMappingView.class.getResource("VariableMappingView.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Stage parentStage;

	public VariableMappingView(Stage parentStage) {
		this();
		this.parentStage = parentStage;
	}

	/**
	 * 초기화 처리.
	 */
	@FXML
	public void initialize() {

		assert gvMappingItems != null : "fx:id=\"gvMappingItems\" was not injected: check your FXML file 'VariableMappingView.fxml'.";
		assert colVarName != null : "fx:id=\"colVarName\" was not injected: check your FXML file 'VariableMappingView.fxml'.";
		assert colVarType != null : "fx:id=\"colVarType\" was not injected: check your FXML file 'VariableMappingView.fxml'.";
		assert colVarValue != null : "fx:id=\"colVarValue\" was not injected: check your FXML file 'VariableMappingView.fxml'.";

		gvMappingItems.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		colVarName.setCellFactory(param -> new TextFieldTableCell<DynamicField, String>(new DefaultStringConverter()) {

			/*
			 * (non-Javadoc)
			 *
			 * @see javafx.scene.control.TableCell#commitEdit(java.lang.Object )
			 */
			@Override
			public void commitEdit(String newValue) {
				ObservableList<DynamicField> items = getTableView().getItems();
				LOGGER.debug(newValue);
				if (newValue == null) {
					super.commitEdit(newValue);
					return;
				}
				boolean anyMatch = items.stream().anyMatch(v -> newValue.equals(v.getFieldName()));

				if (!anyMatch) {
					super.commitEdit(newValue);
				} else {
					DialogUtil.showMessageDialog("키가 이미 존재합니다.");
				}

			}

		});
		colVarType.setCellFactory(TextFieldTableCell.forTableColumn());
		colVarValue.setCellFactory(param -> new TextFieldTableCell<DynamicField, String>(new DefaultStringConverter()) {

			/*
			 * (non-Javadoc)
			 *
			 * @see javafx.scene.control.TableCell#commitEdit(java.lang.Object )
			 */
			@Override
			public void commitEdit(String newValue) {
				super.commitEdit(newValue);
				DynamicField dynamicField = getTableView().getItems().get(getIndex());
				String fieldName = dynamicField.getFieldName();
				String testValue = dynamicField.getTestValue();

				getDefaultPref().put(fieldName, testValue);
			}

		});

		colVarName.setCellValueFactory(param -> param.getValue().fieldNameProperty());
		colVarType.setCellValueFactory(param -> param.getValue().typeProperty());
		colVarValue.setCellValueFactory(param -> param.getValue().testValueProperty());

	}

	public class DynamicField extends TbpSysDaoFieldsDVO {

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "DynamicField [getFieldName()=" + getFieldName() + ", getTestValue()=" + getTestValue() + "]";
		}

	}

	/**
	 * sql로부터 다이나믹 변수 추출.
	 *
	 * @param sql
	 * @return
	 */
	public void extractVariableFromSql(String sql) {

		List<DynamicField> collect = Collections.emptyList();
		if (sql != null && !sql.isEmpty()) {
			List<String> velocityKeys = ValueUtil.getVelocityKeys(sql);
			collect = velocityKeys.stream().distinct().map(str -> {
				DynamicField fieldsDVO = new DynamicField();
				fieldsDVO.setFieldName(str);
				String value = getDefaultPref().get(str, null);
				fieldsDVO.setTestValue(value);
				return fieldsDVO;
			}).collect(Collectors.toList());

			setVariableItems(collect);
		}

	}

	/**
	 * 기본키정보를 리턴
	 *
	 * @return
	 */
	Preferences getDefaultPref() {
		return PreferencesUtil.getDefault().node("dynamic");
	}

	/**
	 * 다이나믹 변수를 받아 목록에 바인드
	 *
	 * @param items
	 */
	public void setVariableItems(List<DynamicField> items) {
		LOGGER.debug(items.toString());
		gvMappingItems.getItems().addAll(items);
	}

	/**
	 * 추가버튼이벤트
	 *
	 * @param e
	 */
	@FXML
	public void btnAddItemOnMouseClick(MouseEvent e) {
		gvMappingItems.getItems().add(new DynamicField());
		gvMappingItems.getSelectionModel().selectLast();
		gvMappingItems.getSelectionModel().focus(gvMappingItems.getItems().size());
	}

	/**
	 * 삭제버튼 이벤트
	 *
	 * @param e
	 */
	@FXML
	public void btnRemoveItemOnMouseClick(MouseEvent e) {

		// int selectedIndex =
		// gvMappingItems.getSelectionModel().getSelectedIndex();
		// LOGGER.debug("REMOVE ITEM INDEX : " + selectedIndex);
		// if (selectedIndex >= 0) {
		// String key =
		// gvMappingItems.getItems().get(selectedIndex).getFieldName();
		// if (key != null) {
		// getDefaultPref().remove(key);
		// }
		// gvMappingItems.getItems().remove(selectedIndex);
		// }

		ObservableList<Integer> items = gvMappingItems.getSelectionModel().getSelectedIndices();

		if (items != null && !items.isEmpty()) {
			for (int i = items.size() - 1; i >= 0; i--) {
				String key = gvMappingItems.getItems().get(i).getFieldName();
				if (key != null) {
					getDefaultPref().remove(key);
				}
				gvMappingItems.getItems().remove(i);
			}
		}
	}

	/**
	 * 다이나믹 변수 목록 리턴.
	 *
	 * @return
	 */
	public List<DynamicField> getVariableResult() {
		return gvMappingItems.getItems();
	}

	/**
	 * 결과를 맵으로 리턴한다.
	 *
	 * 중복된 키는 당연히 제거
	 *
	 * @return
	 */
	public Map<String, Object> getVariableResultMap() {
		return gvMappingItems.getItems().stream().filter(v -> v != null && v.getTestValue() != null)
				.collect(Collectors.toMap(v -> v.getFieldName(), v -> v.getTestValue(), (t, u) -> t.toString()));
	}

	public void showAndWait() {
		showAndWait(null);
	}

	public void doMapping(Consumer<Map<String, Object>> resultItem) {
		if (resultItem != null) {
			Map<String, Object> variableResultMap = getVariableResultMap();
			if (variableResultMap != null && !variableResultMap.isEmpty())
				resultItem.accept(variableResultMap);
			else {
				LOGGER.debug("mapped data is empty.");
				resultItem.accept(Collections.emptyMap());
			}

		}
	}

	public void showAndWait(Consumer<Map<String, Object>> resultItem) {
		Stage dialog = new Stage();

		dialog.setScene(new Scene(this));
		Button value2 = new Button("Mapping");
		value2.setPrefWidth(300);
		value2.setOnMouseClicked(e -> {
			doMapping(resultItem);
			dialog.close();
		});
		HBox value3 = new HBox(value2);
		value3.setPrefWidth(HBox.USE_COMPUTED_SIZE);
		value3.setPrefHeight(HBox.USE_COMPUTED_SIZE);
		value3.setAlignment(Pos.CENTER);
		value3.setPadding(new Insets(5, 5, 5, 5));
		this.setBottom(value3);

		dialog.setTitle(TITLE);

		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initStyle(StageStyle.UTILITY);

		if (parentStage != null)
			dialog.initOwner(parentStage);

		dialog.setResizable(false);
		dialog.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
			KeyCode code = event.getCode();
			if (KeyCode.ESCAPE == code) {
				dialog.close();
				event.consume();
			}
		});

		dialog.showAndWait();

	}

}
