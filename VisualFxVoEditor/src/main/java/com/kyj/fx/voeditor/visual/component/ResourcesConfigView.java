/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2015. 11. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.main.model.vo.Code;
import com.kyj.fx.voeditor.visual.momory.ClassTypeResourceLoader;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import kyj.Fx.dao.wizard.memory.DatabaseTypeMappingResourceLoader;
import kyj.Fx.dao.wizard.memory.IFileBaseConfiguration;

/**
 * 데이터베이스 설정 콤포넌트
 *
 * ResourceLoader 클래스에서 jdbc.으로 시작하는 키값을 불러들어온뒤 설정을 변경할 수 있게한다.
 *
 * @author KYJ
 *
 */
@FXMLController(value = "ResourcesConfigView.fxml", isSelfController = true)
public class ResourcesConfigView extends BorderPane {
	private static Logger LOGGER = LoggerFactory.getLogger(ResourcesConfigView.class);
	@FXML
	private TableView<Map<String, Object>> tbDatabase;
	@FXML
	private TableColumn<Map<String, Object>, Object> colKey;
	@FXML
	private TableColumn<Map<String, Object>, Object> colValue;
	@FXML
	private Button btnSave;

	@FXML
	private ChoiceBox<Code> choUrlItems;

	private ObservableList<Map<String, Object>> observableArrayList;

	private List<Class<? extends IFileBaseConfiguration>> fileBaseClasses = Arrays.asList(ClassTypeResourceLoader.class,
			ConfigResourceLoader.class, ResourceLoader.class, DatabaseTypeMappingResourceLoader.class);

	/**
	 * 생성자
	 */
	public ResourcesConfigView() {

		FxUtil.loadRoot(ResourcesConfigView.class, this, err-> LOGGER.error(ValueUtil.toString(err)));
//		FXMLLoader loader = new FXMLLoader();
//		loader.setLocation(getClass().getResource("ResourcesConfigView.fxml"));
//		loader.setController(this);
//		loader.setRoot(this);
//		
//		
//		this.getStylesheets().add(SkinManager.getInstance().getSkin());
//		try {
//			loader.load();
//		} catch (Exception e) {
//			LOGGER.error(e.getMessage());
//		}
	}

	@FXML
	public void initialize() {
		colKey.setCellValueFactory(param -> {
			Object object = param.getValue().get("key");
			return new SimpleObjectProperty<>(object);
		});
		colValue.setCellValueFactory(param -> {

			SimpleObjectProperty<Object> simpleObjectProperty = new SimpleObjectProperty<>(param.getValue().get("value"));
			simpleObjectProperty.addListener((oba, oldval, newval) -> {
				param.getValue().put("value", newval);
			});
			return simpleObjectProperty;

		});
		colValue.setCellFactory(new Callback<TableColumn<Map<String, Object>, Object>, TableCell<Map<String, Object>, Object>>() {
			@Override
			public TableCell<Map<String, Object>, Object> call(TableColumn<Map<String, Object>, Object> param) {
				return new TextFieldTableCell<>(converter());
			}

			StringConverter<Object> converter() {
				return new StringConverter<Object>() {

					@Override
					public String toString(Object object) {
						if (object == null)
							return "";

						return object.toString();
					}

					@Override
					public Object fromString(String string) {
						return string;
					}
				};
			}
		});

		// Database Url Management에서 가져온 리소스정보
		List<Code> databaseUrlResources = loadDatabaseResourceView();
		choUrlItems.getItems().addAll(databaseUrlResources);

		observableArrayList = loadResource();
		tbDatabase.getItems().addAll(observableArrayList);
		
		tbDatabase.getSelectionModel().setCellSelectionEnabled(true);
		
		FxUtil.installClipboardKeyEvent(tbDatabase);
		
		
	}

	/**
	 * Database Url Management에서 가져온 리소스정보
	 *
	 * @return
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 11. 30.
	 */
	List<Code> loadDatabaseResourceView() {

		return fileBaseClasses.stream().map(clazz -> {
			String key = clazz.getSimpleName();
			return new Code(key, clazz);
		}).collect(Collectors.toList());

	}

	/**
	 * 화면에 리소스를 읽어들인다.
	 *
	 * @return
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 11. 4.
	 */
	public ObservableList<Map<String, Object>> loadResource() {

		Code selectedItem = choUrlItems.getSelectionModel().getSelectedItem();
		if (selectedItem == null) {
			selectedItem = choUrlItems.getItems().get(0);
		}
		Class<? extends IFileBaseConfiguration> code = (Class<? extends IFileBaseConfiguration>) selectedItem.getCode();

		IFileBaseConfiguration instance = null;

		if (code == ClassTypeResourceLoader.class) {
			instance = ClassTypeResourceLoader.getInstance();
		} else if (code == ConfigResourceLoader.class) {
			instance = ConfigResourceLoader.getInstance();
		} else if (code == ResourceLoader.class) {
			instance = ResourceLoader.getInstance();
		} else if (code == DatabaseTypeMappingResourceLoader.class) {
			instance = DatabaseTypeMappingResourceLoader.getInstance();
		} else {
			instance = ResourceLoader.getInstance();
		}

		Set<Entry<Object, Object>> entry = instance.getEntry();
		return load(entry);
	}

	private <T, K> ObservableList<Map<String, Object>> load(Set<Entry<T, K>> entry) {
		ObservableList<Map<String, Object>> observableArrayList = FXCollections.observableArrayList();
		Iterator<Entry<T, K>> iterator = entry.iterator();
		while (iterator.hasNext()) {
			Entry<T, K> next = iterator.next();
			String key = next.getKey().toString();
			String value = next.getValue().toString();

			Map<String, Object> map = getMap(key, value);
			observableArrayList.add(map);
		}

		return observableArrayList;
	}

	private Map<String, Object> getMap(String key, String value) {
		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("key", key);
		hashMap.put("value", value);
		return hashMap;
	}

	@FXML
	public void btnSaveOnMouseClick(MouseEvent event) {

		try {
			ResourceLoader instance = ResourceLoader.getInstance();

			Map<String, Object> bufMap = new HashMap<>();

			tbDatabase.getItems().forEach(item -> {

				Object key = item.get("key");
				Object value = item.get("value");
				if (key == null)
					return;
				bufMap.put(key.toString(), value.toString());
			});
			instance.putAll(bufMap);
			DialogUtil.showMessageDialog("Save Complete...");
		} catch (Exception e) {
			DialogUtil.showExceptionDailog(e);
		}
	}

	@FXML
	public void btnSelectOnMouseClick(MouseEvent event) {
		ObservableList<Map<String, Object>> loadResource = loadResource();
		ObservableList<Map<String, Object>> items = tbDatabase.getItems();
		items.clear();
		items.addAll(loadResource);
	}
}
