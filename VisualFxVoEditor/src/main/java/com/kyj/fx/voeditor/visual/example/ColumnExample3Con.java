/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 2. 12.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.MapValueFactory;
import javafx.util.Callback;

/**
 * @author KYJ
 *
 */
public class ColumnExample3Con {

	@FXML
	private TableView<Map<String, String>> tableView;

	@FXML
	private TableColumn<Map<String, String>, String> colCode;

	@FXML
	private TableColumn<Map<String, String>, String> colCodeNm;

	@FXML
	public void initialize() {
		tableView.setEditable(true);
		{
			Map<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("commCode", "코드1");
			hashMap.put("commCodeNm", "데이터 1");
			tableView.getItems().add(hashMap);
		}

		{
			Map<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("commCode", "코드1");
			hashMap.put("commCodeNm", "데이터 223492342309842402394823049238420942384029343809248420934809428409238409238423094823049");
			tableView.getItems().add(hashMap);
		}

		{
			Map<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("commCode", "코드3");
			hashMap.put("commCodeNm", "데이터 3");
			tableView.getItems().add(hashMap);
		}

		{
			Map<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("commCode", "코드4");
			hashMap.put("commCodeNm", "데이터 4");
			tableView.getItems().add(hashMap);
		}
		colCode.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> param) {
				return new SimpleStringProperty(param.getValue().get("commCode"));
			}
		});

		colCodeNm.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map<String, String>, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Map<String, String>, String> param) {
				return new SimpleStringProperty(param.getValue().get("commCodeNm"));
			}
		});

	}
}
