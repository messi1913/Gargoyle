/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2017. 7. 12.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.popup;

import java.util.Enumeration;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.grid.AbstractDVO;
import com.kyj.fx.voeditor.visual.component.grid.BaseOptions;
import com.kyj.fx.voeditor.visual.component.grid.CommonsBaseGridView;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.utils.EncrypUtil;
import com.sun.star.uno.RuntimeException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class DatabaseURLSelectView extends BorderPane {

	private static Logger LOGGER = LoggerFactory.getLogger(DatabaseURLSelectView.class);

	private CommonsBaseGridView<DabaseInfo> commonsBaseGridView;

	/**
	 * 
	 */
	private DatabaseURLSelectView() {
		initView();
		loadInfo();
	}

	private void loadInfo() {
		ResourceLoader instance = ResourceLoader.getInstance();
		ObservableList<DabaseInfo> observableArrayList = commonsBaseGridView.getItems();
		Enumeration<Object> keySet = instance.keySet();
		while (keySet.hasMoreElements()) {

			Object _key = keySet.nextElement();
			if (_key == null)
				continue;
			String key = (String) _key;
			if (!key.startsWith("database.info."))
				continue;

			DabaseInfo dabaseInfo = new DabaseInfo();
			String value = instance.get(key);
			if ("jdbc.pass".equals(key))
				value = decryp(value);

			JSONObject json = ValueUtil.toJSONObject(value);

			String dbms = ValueUtil.decode(json.get("dbms"), "").toString();
			dabaseInfo.setDatabase(dbms);
			dabaseInfo.setDriver(ValueUtil.getDbmsNameToDriver(dbms));
			dabaseInfo.setPassword(value);
			dabaseInfo.setUrl(ValueUtil.decode(json.get("jdbc.url"), "").toString());
			dabaseInfo.setId(ValueUtil.decode(json.get("jdbc.id"), "").toString());
			
			observableArrayList.add(dabaseInfo);
		}

	}

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

	public static class DabaseInfo extends AbstractDVO {
		StringProperty database = new SimpleStringProperty();
		StringProperty url = new SimpleStringProperty();
		StringProperty id = new SimpleStringProperty();
		StringProperty password = new SimpleStringProperty();
		StringProperty driver = new SimpleStringProperty();

		public final StringProperty databaseProperty() {
			return this.database;
		}

		public final java.lang.String getDatabase() {
			return this.databaseProperty().get();
		}

		public final void setDatabase(final java.lang.String database) {
			this.databaseProperty().set(database);
		}

		public final StringProperty urlProperty() {
			return this.url;
		}

		public final java.lang.String getUrl() {
			return this.urlProperty().get();
		}

		public final void setUrl(final java.lang.String url) {
			this.urlProperty().set(url);
		}

		public final StringProperty idProperty() {
			return this.id;
		}

		public final java.lang.String getId() {
			return this.idProperty().get();
		}

		public final void setId(final java.lang.String id) {
			this.idProperty().set(id);
		}

		public final StringProperty passwordProperty() {
			return this.password;
		}

		public final java.lang.String getPassword() {
			return this.passwordProperty().get();
		}

		public final void setPassword(final java.lang.String password) {
			this.passwordProperty().set(password);
		}

		public final StringProperty driverProperty() {
			return this.driver;
		}

		public final java.lang.String getDriver() {
			return this.driverProperty().get();
		}

		public final void setDriver(final java.lang.String driver) {
			this.driverProperty().set(driver);
		}

	}

	private ObjectProperty<DabaseInfo> selected = new SimpleObjectProperty<>();

	private void initView() {
		commonsBaseGridView = new CommonsBaseGridView<>(DabaseInfo.class, new BaseOptions() {

			@Override
			public boolean visible(String columnName) {
				if ("password".equals(columnName))
					return false;
				return true;
			}

		});
		setCenter(commonsBaseGridView);

		commonsBaseGridView.setOnMouseClicked(e -> {
			if (e.getClickCount() == 2) {
				selected.set(commonsBaseGridView.getSelectionModel().getSelectedItem());
				Stage window = (Stage) FxUtil.getWindow(this);
				window.close();
			}
		});
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 7. 12. 
	 * @return
	 */
	public static DabaseInfo showAndWait() {
		Stage stage = new Stage();
		DatabaseURLSelectView root = new DatabaseURLSelectView();
		stage.setScene(new Scene(root, 800d, 800d));
		stage.initModality(Modality.WINDOW_MODAL);
		stage.showAndWait();
		return root.selected.get();
	}

}
