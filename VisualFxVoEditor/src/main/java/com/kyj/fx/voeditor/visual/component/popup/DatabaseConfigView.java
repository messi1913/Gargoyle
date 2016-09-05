/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2015. 11. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.popup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.kyj.fx.voeditor.visual.component.PasswordTextFieldTableCell;
import com.kyj.fx.voeditor.visual.main.initalize.DatabaseInitializer;
import com.kyj.fx.voeditor.visual.main.model.vo.Code;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.EncrypUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.sun.star.uno.RuntimeException;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

/**
 * 데이터베이스 설정 콤포넌트
 *
 * ResourceLoader 클래스에서 jdbc.으로 시작하는 키값을 불러들어온뒤 설정을 변경할 수 있게한다.
 *
 * @author KYJ
 *
 */
public class DatabaseConfigView extends BorderPane {
	private static Logger LOGGER = LoggerFactory.getLogger(DatabaseConfigView.class);
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

	/**
	 * 생성자
	 */
	public DatabaseConfigView() {

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("DatabaseConfigView.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		try {
			loader.load();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	@FXML
	public void initialize() {
		tbDatabase.setEditable(false);
		colKey.setCellValueFactory(param -> {
			Object object = param.getValue().get("key");
			return new SimpleObjectProperty<>(object);
		});

		colValue.setCellValueFactory(param -> {

			// SimpleObjectProperty<Object> simpleObjectProperty = new
			// SimpleObjectProperty<>(param.getValue().get("value"));
			//
			// ChangeListener<? super Object> listener = (oba, oldval, newval)
			// -> {
			// param.getValue().put("value", newval);
			// };
			//
			// simpleObjectProperty.removeListener(listener);
			// simpleObjectProperty.addListener(listener);
			return new SimpleObjectProperty<>(param.getValue().get("value"));

		});
		colValue.setCellFactory(new Callback<TableColumn<Map<String, Object>, Object>, TableCell<Map<String, Object>, Object>>() {
			@Override
			public TableCell<Map<String, Object>, Object> call(TableColumn<Map<String, Object>, Object> param) {
				return new TextFieldTableCell<Map<String, Object>, Object>() {

					@Override
					public void updateItem(Object item, boolean empty) {
						super.updateItem(item, empty);

						if (empty) {
							setText("");
						} else {
							/* 2016.4.20 패스워드에 속하는 영역인경우 패스워드 형태로 변경.*/
							int rowIndex = getTableRow().getIndex();

							if (Objects.equal(ResourceLoader.BASE_KEY_JDBC_PASS, colKey.getCellData(rowIndex)))
								setText(PasswordTextFieldTableCell.getPasswordTextFieldStringConverter().toString(item.toString()));
						}
					}
				};
			}
		});

		observableArrayList = loadResource();

		// Database Url Management에서 가져온 리소스정보
		List<Code> databaseUrlResources = loadDatabaseResourceView();
		choUrlItems.getItems().addAll(databaseUrlResources);

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
		Iterator<Entry<T, K>> iterator = entry.iterator();
		while (iterator.hasNext()) {
			Entry<T, K> next = iterator.next();
			String key = next.getKey() == null ? "" : next.getKey().toString();
			String value = next.getValue() == null ? "" : next.getValue().toString();
			if (!key.startsWith("jdbc."))
				continue;

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
			initDatabaseConfig();
			DialogUtil.showMessageDialog("Save Complete...");
		} catch (Exception e) {
			DialogUtil.showExceptionDailog(e);
		}
	}

	private void initDatabaseConfig() {
		DbUtil.cleanDataSource();
	}

	@FXML
	public void btnSelectOnMouseClick(MouseEvent event) {
		Code selectedItem = choUrlItems.getSelectionModel().getSelectedItem();
		if (selectedItem == null)
			return;
		Object code = selectedItem.getCode();
		if (code != null) {
			Map<String, Object> codeMap = (Map<String, Object>) code;
			if (codeMap != null) {
				Object url = codeMap.get(ResourceLoader.BASE_KEY_JDBC_URL);
				Object id = codeMap.get(ResourceLoader.BASE_KEY_JDBC_ID);
				Object pass = codeMap.get(ResourceLoader.BASE_KEY_JDBC_PASS);
				Object dbms = codeMap.get(ResourceLoader.DBMS);
				String driver = ResourceLoader.getInstance().get(ResourceLoader.DBMS + "." + dbms);
				LOGGER.debug(String.format("url :%s id : %s , dbms: %s , driver : %s", id, url, dbms, driver));

				Map<String, Object> linkedHashMap = new LinkedHashMap<>(4);

				linkedHashMap.put(ResourceLoader.BASE_KEY_JDBC_URL, url);
				linkedHashMap.put(ResourceLoader.BASE_KEY_JDBC_DRIVER, driver);
				linkedHashMap.put(ResourceLoader.BASE_KEY_JDBC_ID, id);
				linkedHashMap.put(ResourceLoader.BASE_KEY_JDBC_PASS, pass);

				ObservableList<Map<String, Object>> load = load(linkedHashMap.entrySet());
				ObservableList<Map<String, Object>> items = this.tbDatabase.getItems();
				items.clear();
				items.addAll(load);
			}
		}
	}

	private String decryp(Object str) {
		if (str == null)
			return "";

		try {
			return EncrypUtil.decryp(str.toString());
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}
	}

	/**
	 * 데이터접속이 가능한지 연결테스트
	 *
	 * @throws Exception
	 * @throws SQLException
	 */
	@FXML
	public void btnPingOnMouseClick() {

		DbUtil.ping(() -> {
			return getPoolProperties();
		} , (bool) -> {
			String msg = "fail!";
			if (bool)
				msg = "success!";
			DialogUtil.showMessageDialog(msg);
		} , ex -> {
			LOGGER.info(ValueUtil.toString("ping test", ex));
		});

	}

	/********************************
	 * 작성일 : 2016. 9. 2. 작성자 : KYJ
	 *
	 * 드라이버 로드 처리를 위함/
	 *
	 * @param driver
	 ********************************/
	private static void classForName(String driver) {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private PoolProperties getPoolProperties() {
		Map<String, Object> bufMap = new HashMap<>();
		tbDatabase.getItems().forEach(item -> {

			Object key = item.get("key");
			Object value = item.get("value");
			if (key == null)
				return;
			bufMap.put(key.toString(), value.toString());
		});

		String driver = bufMap.get(ResourceLoader.BASE_KEY_JDBC_DRIVER).toString();
		String url = bufMap.get(ResourceLoader.BASE_KEY_JDBC_URL).toString();
		String username = bufMap.get(ResourceLoader.BASE_KEY_JDBC_ID).toString();
		String password = bufMap.get(ResourceLoader.BASE_KEY_JDBC_PASS).toString();
		password = decryp(password);
		PoolProperties poolProperties = new PoolProperties();
		poolProperties.setDriverClassName(driver);
		poolProperties.setUrl(url);
		poolProperties.setUsername(username);
		poolProperties.setPassword(password);

		return poolProperties;
	}

	private Connection getConnection() throws SQLException {

		Map<String, Object> bufMap = new HashMap<>();
		tbDatabase.getItems().forEach(item -> {

			Object key = item.get("key");
			Object value = item.get("value");
			if (key == null)
				return;
			bufMap.put(key.toString(), value.toString());
		});

		String driver = bufMap.get(ResourceLoader.BASE_KEY_JDBC_DRIVER).toString();
		String url = bufMap.get(ResourceLoader.BASE_KEY_JDBC_URL).toString();
		String username = bufMap.get(ResourceLoader.BASE_KEY_JDBC_ID).toString();
		String password = bufMap.get(ResourceLoader.BASE_KEY_JDBC_PASS).toString();
		password = decryp(password);

		return DriverManager.getConnection(url, username, password);
	}

	/********************************
	 * 작성일 : 2016. 9. 2. 작성자 : KYJ
	 *
	 * Gargoyle에서 사용하는 데이터베이스 생성
	 *
	 * @throws Exception
	 * @throws SQLException
	 ********************************/
	@FXML
	public void btnGargoyleOnMouseClick() {

		//생성전 뭔저 확인여부
//		Optional<Pair<String, String>> showYesOrNoDialog = DialogUtil.showYesOrNoDialog("Create Database", "Gargoyle용 데이터베이스 생성 확인.");
//
//		showYesOrNoDialog.ifPresent(v -> {
//			if ("Y".equals(v.getValue())) {
//
//			}
//		});

		try {
			DatabaseInitializer databaseInitializer = new DatabaseInitializer(() -> {
				try {
					return DbUtil.getConnection();
				} catch (Exception e) {
					return null;
				}
			});
			databaseInitializer.setExceptionHandler(ex -> DialogUtil.showExceptionDailog(ex));
			databaseInitializer.setOnSuccessHandler(t -> DialogUtil.showMessageDialog("데이터베이스 생성 완료.!"));
			databaseInitializer.initialize();

		} catch (Exception e) {
			DialogUtil.showExceptionDailog(e);
		}

	}
}
