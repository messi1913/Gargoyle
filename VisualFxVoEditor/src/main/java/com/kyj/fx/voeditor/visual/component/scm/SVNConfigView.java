/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.layout
 *	작성일   : 2016. 4. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.scm;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.voeditor.visual.component.popup.DatabaseConfigView;
import com.kyj.fx.voeditor.visual.component.popup.DatabaseUrlManagementView;
import com.kyj.fx.voeditor.visual.main.model.vo.Code;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * SVN 설정
 *
 * @author KYJ
 *
 */
@FXMLController(value = "SVNConfigView.fxml", isSelfController = true)
public class SVNConfigView extends BorderPane {
	private static Logger LOGGER = LoggerFactory.getLogger(DatabaseConfigView.class);
	@FXML
	private TableView<Map<String, Object>> tbDatabase;
	@FXML
	private TableColumn<Map<String, Object>, Object> colKey;
	@FXML
	private TableColumn<Map<String, Object>, Object> colValue;
	@FXML
	private Button btnSave;

	// @FXML
	// private ChoiceBox<Code> choUrlItems;

	private ObservableList<Map<String, Object>> observableArrayList;

	/**
	 * 생성자
	 */
	public SVNConfigView() {
		FxUtil.loadRoot(SVNConfigView.class, this, err -> LOGGER.error(ValueUtil.toString(err)));
//		FXMLLoader loader = new FXMLLoader();
//		loader.setLocation(getClass().getResource("SVNConfigView.fxml"));
//		loader.setController(this);
//		loader.setRoot(this);
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

		observableArrayList = loadResource();

		// Database Url Management에서 가져온 리소스정보
		// List<Code> databaseUrlResources = loadDatabaseResourceView();
		// choUrlItems.getItems().addAll(databaseUrlResources);

		tbDatabase.getItems().addAll(observableArrayList);
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

		ObservableList<Map<String, Object>> loadResource = DatabaseUrlManagementView.loadResource();

		return loadResource.stream().map(map -> {

			Object url = map.get(ResourceLoader.BASE_KEY_JDBC_URL);
			Object codeNm = ValueUtil.decode(map.get("alias"), String.format("%s (%s)", map.get("alias"), url), url);
			return new Code(codeNm.toString(), map);
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
		ResourceLoader instance = ResourceLoader.getInstance();
		Set<Entry<Object, Object>> entry = instance.getEntry();
		return load(entry);
	}

	private <T, K> ObservableList<Map<String, Object>> load(Set<Entry<T, K>> entry) {
		ObservableList<Map<String, Object>> observableArrayList = FXCollections.observableArrayList();
		if (entry.isEmpty())
			return observableArrayList;

		String keys = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.SVN_BASE_KEYS);
		String[] split = keys.split(",");

		// Iterator<Entry<T, K>> iterator = entry.iterator();

		for (String svnKey : split) {
			Optional<T> findFirst = entry.stream().map(e -> e.getKey()).filter(k -> svnKey.equals(k)).findFirst();
			boolean isFound = findFirst.isPresent();

			if (isFound) {
				String value = ResourceLoader.getInstance().get(svnKey);
				observableArrayList.add(getMap(svnKey, value));
			} else {
				observableArrayList.add(getMap(svnKey, ""));
			}
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
	}

	/**
	 * 데이터접속이 가능한지 연결테스트
	 *
	 * @throws Exception
	 * @throws SQLException
	 */
	@FXML
	public void btnPingOnMouseClick() {

		//Not yet Support..
//		DbUtil.ping(() -> {
//
//			Map<String, Object> bufMap = new HashMap<>();
//			tbDatabase.getItems().forEach(item -> {
//
//				Object key = item.get("key");
//				Object value = item.get("value");
//				if (key == null)
//					return;
//				bufMap.put(key.toString(), value.toString());
//			});
//
//			String driver = bufMap.get(ResourceLoader.BASE_KEY_JDBC_DRIVER).toString();
//			String url = bufMap.get(ResourceLoader.BASE_KEY_JDBC_URL).toString();
//			String username = bufMap.get(ResourceLoader.BASE_KEY_JDBC_ID).toString();
//			String password = bufMap.get(ResourceLoader.BASE_KEY_JDBC_PASS).toString();
//
//			PoolProperties poolProperties = new PoolProperties();
//			poolProperties.setDriverClassName(driver);
//			poolProperties.setUrl(url);
//			poolProperties.setUsername(username);
//			poolProperties.setPassword(password);
//
//			return poolProperties;
//		}, (bool) -> {
//			String msg = "fail!";
//			if (bool)
//				msg = "success!";
//			DialogUtil.showMessageDialog(msg);
//		}, ex -> {
//			LOGGER.info(ValueUtil.toString("ping test", ex));
//		});

	}

}
