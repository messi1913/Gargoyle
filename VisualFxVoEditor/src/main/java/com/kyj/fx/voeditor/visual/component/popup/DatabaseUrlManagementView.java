/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.layout
 *	작성일   : 2015. 10. 15.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.popup;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.kyj.fx.voeditor.visual.component.ButtonTableColumn;
import com.kyj.fx.voeditor.visual.component.ColorPickerTableColumn;
import com.kyj.fx.voeditor.visual.component.ComboBoxTableColumn;
import com.kyj.fx.voeditor.visual.component.PasswordTextFieldTableCell;
import com.kyj.fx.voeditor.visual.component.dock.pane.DockNode;
import com.kyj.fx.voeditor.visual.component.sql.view.CommonsSqllPan;
import com.kyj.fx.voeditor.visual.exceptions.NotYetSupportException;
import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.main.model.vo.Code;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.EncrypUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.sun.star.uno.RuntimeException;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * @author Hong
 * 
 * 2016-12-03 FxUtil.loadRoot()함수를 이용한 로딩으로 수정 by kyj
 *
 */
@FXMLController(value = "DatabaseUrlManagementView.fxml", isSelfController = true)
public class DatabaseUrlManagementView extends BorderPane {

	private static Logger LOGGER = LoggerFactory.getLogger(DatabaseConfigView.class);
	@FXML
	private TableView<Map<String, Object>> tbDatabase;
	@FXML
	private TableColumn<Map<String, Object>, Object> colKey;
	@FXML
	private TableColumn<Map<String, Object>, Object> colAlias;
	@FXML
	private TableColumn<Map<String, Object>, Object> colUrl;
	@FXML
	private TableColumn<Map<String, Object>, Object> colUserName;
	@FXML
	private TableColumn<Map<String, Object>, Object> colUserPassword;

	// @FXML
	// private TableColumn<Map<String, Object>, Object> colPing;

	private TableColumn<Map<String, Object>, Color> colColor;

	private TableColumn<Map<String, Object>, Object> colDbms;

	private TableColumn<Map<String, Object>, Object> colPopup;

	private TableColumn<Map<String, Object>, Object> colPing;

	@FXML
	private Button btnSave;

	private ObservableList<Map<String, Object>> observableArrayList;

	/**
	 * 생성자
	 */
	public DatabaseUrlManagementView() {
		FxUtil.loadRoot(DatabaseUrlManagementView.class, this, err -> LOGGER.error(ValueUtil.toString(err)));
		
//		FXMLLoader loader = new FXMLLoader();
//		loader.setLocation(getClass().getResource("DatabaseUrlManagementView.fxml"));
//		loader.setController(this);
//		loader.setRoot(this);
//		try {
//			loader.load();
//		} catch (Exception e) {
//			LOGGER.error(e.getMessage());
//		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@FXML
	public void initialize() {
		// /tbDatabase.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		colKey.setCellValueFactory(param -> {
			Object object = param.getValue().get("seqNum");
			return new SimpleObjectProperty<>(object);
		});

		String property = ResourceLoader.getInstance().get(ResourceLoader.DBMS_SUPPORT);

		// 사용되지않은 코드
		// String colOrder =
		// ResourceLoader.getInstance().get(ResourceLoader.DATABASE_COLUMN_ORDER);

		// 2015.11.30 데이터에 []코드를 삭제하면서 아래 코드존재가 불필요해서 주석
		// 오류가 발생하여 임시 처리.
		// if (colOrder != null) {
		// colOrder = colOrder.replace("[", "");
		// colOrder = colOrder.replace("]", "");
		// }

		List<Code> dbms = Stream.of(StringUtils.tokenizeToStringArray(property, ",")).map(t -> new Code(t, t)).collect(Collectors.toList());

		colDbms = new ComboBoxTableColumn<>("dbms", FXCollections.observableArrayList(dbms), "code", "codeNm");
		tbDatabase.getColumns().add(1, colDbms);
		tbDatabase.getColumns().get(1).setText("DBMS");
		tbDatabase.getColumns().get(1).setId("colDbms");

		colColor = new ColorPickerTableColumn<Map<String, Object>>(colColor, "color");
		colColor.setMaxWidth(50);
		tbDatabase.getColumns().add(1, colColor);
		tbDatabase.getColumns().get(1).setText("Color");
		tbDatabase.getColumns().get(1).setId("colColor");

		colPopup = new ButtonTableColumn() {
			@Override
			public void clickHandle(int nRow) {
				try {
					showSqlPane(nRow);
				} catch (Exception e) {
					DialogUtil.showExceptionDailog(e);
					LOGGER.error(ValueUtil.toString(e));
				}
			}
		};

		colPing = new ButtonTableColumn("Ping") {

			@Override
			public void clickHandle(int nRow) {
				try {
					ping(nRow);
				} catch (Exception e) {
					DialogUtil.showExceptionDailog(e);
					LOGGER.error(ValueUtil.toString(e));
				}
			}
		};

		colPopup.setMaxWidth(50);

		tbDatabase.getColumns().add(1, colPopup);
		tbDatabase.getColumns().get(1).setText("Popup");
		tbDatabase.getColumns().get(1).setId("colPopup");
		tbDatabase.getColumns().add(colPing);

		setColumnText(colAlias, "alias");
		colUrl.setPrefWidth(300);
		setColumnText(colUrl, ResourceLoader.BASE_KEY_JDBC_URL);
		setColumnText(colUserName, ResourceLoader.BASE_KEY_JDBC_ID);

		setColumnText(colUserPassword, ResourceLoader.BASE_KEY_JDBC_PASS, param -> new PasswordTextFieldTableCell());

		// colUserPassword.setCellValueFactory(param -> {
		// Object initialValue =
		// param.getValue().get(ResourceLoader.BASE_KEY_JDBC_PASS);
		// SimpleObjectProperty<Object> simpleObjectProperty = new
		// SimpleObjectProperty<>(initialValue);
		// simpleObjectProperty.addListener((oba, oldval, newval) ->
		// param.getValue().put(ResourceLoader.BASE_KEY_JDBC_PASS, newval));
		// return simpleObjectProperty;
		// });
		// colUserPassword.setCellFactory(param -> new
		// PasswordTextFieldTableCell());

		observableArrayList = loadResource();
		tbDatabase.getItems().addAll(observableArrayList);
	}

	private Callback<TableColumn<Map<String, Object>, Object>, TableCell<Map<String, Object>, Object>> defaultCallback = new Callback<TableColumn<Map<String, Object>, Object>, TableCell<Map<String, Object>, Object>>() {
		@Override
		public TableCell<Map<String, Object>, Object> call(TableColumn<Map<String, Object>, Object> param) {
			return new TextFieldTableCell<>(defaultConverter);
		}
	};

	StringConverter<Object> defaultConverter = new StringConverter<Object>() {
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

	public void setColumnText(TableColumn<Map<String, Object>, Object> col, String key) {
		setColumnText(col, key, defaultCallback);
	}

	public void setColumnText(TableColumn<Map<String, Object>, Object> col, String key,
			Callback<TableColumn<Map<String, Object>, Object>, TableCell<Map<String, Object>, Object>> cellfactory) {
		col.setCellValueFactory(param -> {
			SimpleObjectProperty<Object> simpleObjectProperty = new SimpleObjectProperty<>(param.getValue().get(key));
			simpleObjectProperty.addListener((ChangeListener<Object>) (oba, oldval, newval) -> param.getValue().put(key, newval));
			return simpleObjectProperty;
		});
		col.setCellFactory(cellfactory);
	}

	/**
	 * 화면에 리소스를 읽어들인다.
	 *
	 *
	 * 변경이력 : 2015.11.30, kyj, static 으로 변경
	 *
	 * @return
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 11. 4.
	 */
	@SuppressWarnings("unchecked")
	public static ObservableList<Map<String, Object>> loadResource() {
		ResourceLoader instance = ResourceLoader.getInstance();
		ObservableList<Map<String, Object>> observableArrayList = FXCollections.observableArrayList();
		Enumeration<Object> keySet = instance.keySet();
		while (keySet.hasMoreElements()) {
			Object _key = keySet.nextElement();
			if (_key == null)
				continue;
			String key = (String) _key;
			if (!key.startsWith("database.info."))
				continue;

			String value = instance.get(key);
			if ("jdbc.pass".equals(key))
				value = decryp(value);
			JSONObject json = ValueUtil.toJSONObject(value);
			Map<String, Object> map = new HashMap<String, Object>(json);
			map.put("seqNum", key);
			observableArrayList.add(0, map);
		}

		return observableArrayList;
	}

	// private static String encryp(Object str) {
	// try {
	// if (ValueUtil.isEmpty(str))
	// return "";
	// return EncrypUtil.encryp(str.toString());
	// } catch (Exception e) {
	// LOGGER.error(ValueUtil.toString(e));
	// throw new RuntimeException(e.toString());
	// }
	// }

	private static String decryp(Object str) {
		if (ValueUtil.isEmpty(str))
			return "";

		try {
			return EncrypUtil.decryp(str.toString());
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
			throw new RuntimeException(e.toString());
		}
	}

	@SuppressWarnings("unchecked")
	@FXML
	public void btnSaveOnMouseClick(MouseEvent event) {

		FileOutputStream out = null;
		ResourceLoader instance = ResourceLoader.getInstance();
		instance.initDataBaseInfo();
		try {
			Map<String, Object> map = new HashMap<>();
			List<String> colList = new ArrayList<String>();
			tbDatabase.getItems().forEach(item -> {
				Object key = item.get("seqNum");
				JSONObject json = new JSONObject();
				json.put("seqNum", item.get("seqNum"));
				json.put("dbms", item.get("dbms"));
				json.put("alias", item.get("alias"));
				json.put(ResourceLoader.BASE_KEY_JDBC_URL, item.get(ResourceLoader.BASE_KEY_JDBC_URL));
				json.put(ResourceLoader.BASE_KEY_JDBC_ID, item.get(ResourceLoader.BASE_KEY_JDBC_ID));
				json.put(ResourceLoader.BASE_KEY_JDBC_PASS, item.get(ResourceLoader.BASE_KEY_JDBC_PASS));
				json.put("color", item.get("color"));
				if (key == null)
					return;
				map.put(key.toString(), json.toJSONString());
			});
			tbDatabase.getColumns().forEach(col -> {
				if (col.isVisible())
					colList.add(col.getId());
			});
			map.put("database.column.order", colList.toString());
			instance.putAll(map);

			tbDatabase.getItems().clear();
			tbDatabase.getItems().addAll(loadResource());

			DialogUtil.showMessageDialog("Save Complete...");
		} catch (Exception e) {
			DialogUtil.showExceptionDailog(e);
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				LOGGER.error(ValueUtil.toString(e));
			}
		}
	}

	@FXML
	public void btnAddOnMouseClick(MouseEvent event) {
		HashMap<String, Object> newItem = new HashMap<String, Object>();
		Optional<Integer> max = tbDatabase.getItems().stream().map(arg0 -> {
			String[] tokenizeToStringArray = StringUtils.tokenizeToStringArray(arg0.get("seqNum").toString(), ".");
			return Integer.parseInt(tokenizeToStringArray[tokenizeToStringArray.length - 1]);
		}).max(Integer::compare);

		int seqNum = max.isPresent() ? max.get() + 1 : 0;
		newItem.put("seqNum", "database.info." + seqNum);
		tbDatabase.getItems().add(newItem);
	}

	@FXML
	public void btnDeleteOnMouseClick(MouseEvent event) {
		tbDatabase.getSelectionModel().getSelectedItems().forEach(map -> {
			tbDatabase.getItems().remove(map);
		});
	}

	public void tbDatabaseMouseClick(MouseEvent e) {

	}

	public void ping(int nRow) throws Exception {
		Map<String, Object> map = tbDatabase.getItems().get(nRow);

		Object object = map.get("dbms");
		if (object == null)
			return;
		String dbms = object.toString();
		if (dbms == null || dbms == "") {
			String msg = "not yet supported..." + dbms;
			LOGGER.error(msg);
			DialogUtil.showMessageDialog(msg);
		}

		DbUtil.ping(() -> {

			String driver = ConfigResourceLoader.getInstance().get("dbms." + dbms);
			String url = map.get(ResourceLoader.BASE_KEY_JDBC_URL).toString();

			String username = map.get(ResourceLoader.BASE_KEY_JDBC_ID) != null ? map.get(ResourceLoader.BASE_KEY_JDBC_ID).toString() : "";
			String password = map.get(ResourceLoader.BASE_KEY_JDBC_PASS) != null ? map.get(ResourceLoader.BASE_KEY_JDBC_PASS).toString()
					: "";

			password = decryp(password);
			PoolProperties poolProperties = new PoolProperties();
			poolProperties.setDriverClassName(driver);
			poolProperties.setUrl(url);
			poolProperties.setUsername(username);
			poolProperties.setPassword(password);

			return poolProperties;
		}, (bool) -> {
			String msg = "fail!";
			if (bool)
				msg = "success!";
			DialogUtil.showMessageDialog(msg);
		}, ex -> {
			LOGGER.info(ValueUtil.toString("ping test", ex));
		});

		// LOGGER.debug(map.toString());

	}

	public void showSqlPane(int nRow) throws Exception {
		Map<String, Object> map = tbDatabase.getItems().get(nRow);
		Object object = map.get("dbms");
		if (object == null)
			return;
		String dbms = object.toString();
		if (dbms == null || dbms == "") {
			String msg = "not yet supported..." + dbms;
			LOGGER.error(msg);
			throw new NotYetSupportException(msg);
		}

		String jdbcDriver = ConfigResourceLoader.getInstance().get("dbms." + dbms);
		map.put("driver", jdbcDriver);

		CommonsSqllPan sqlPane = CommonsSqllPan.getSqlPane(dbms);
		sqlPane.initialize(map);

		// Scene scene = new Scene(root, 1100, 700);
		sqlPane.setPrefSize(1100, 900);
		String title = String.format("Database[%s]", sqlPane.getClass().getSimpleName());

		DockNode dockNode = new DockNode(sqlPane, title);
		// dockNode.setFloating(true, new Point2D(0,0));
		// dockNode.getStage().centerOnScreen();

		FxUtil.createDockStageAndShow(null, dockNode);

		// FxUtil.createStageAndShow(title, dockNode, stage -> {
		// stage.getScene().getStylesheets().add(SkinManager.getInstance().getSkin());
		// dockNode.floatingProperty().addListener((oba, o, n) -> {
		// if (n)
		// stage.close();
		// });
		// });

		// Stage stage = new Stage();
		// sqlPane.setStage(stage);

		// scene.getStylesheets().add(SkinManager.getInstance().getSkin());
		// stage.setScene(scene);
		// stage.setTitle(dbms);
		//
		//
		//
		// stage.show();
	}
}
