/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.builder
 *	작성일   : 2017. 11. 04.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.uc.items;

import java.sql.Connection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.framework.annotation.FxPostInitialize;
import com.kyj.fx.voeditor.visual.framework.loader.core.BusinessRegistable;
import com.kyj.fx.voeditor.visual.framework.loader.core.RegistItem;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.IdGenUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.sun.btrace.BTraceUtils.Strings;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "SqlMappingTableViewRegister.fxml", isSelfController = true, css = "SqlMappingTableViewRegister.css")
public class SqlMappingTableViewRegister extends BorderPane implements BusinessRegistable {

	private static final Logger LOGGER = LoggerFactory.getLogger(SqlMappingTableViewRegister.class);
	private String id;
	private String displayName;
	private String alias;
	private String desc;

	// private String driver;
	// private String url;
	// private String userName;
	// private String password;
	private ObjectProperty<ConnectionSupplier> connectionSupplier = new SimpleObjectProperty<>();

	private String sql;
	private List<String> columns;

	@FXML
	private ComboBox<Map<String, Object>> cbConnection;
	@FXML
	private TextArea txtSql;
	@FXML
	private TableView<Map<String, Object>> tbData;

	public SqlMappingTableViewRegister() {
		this.id = generateId();

		FxUtil.loadRoot(SqlMappingTableViewRegister.class, this, err -> {
			LOGGER.error(ValueUtil.toString(err));
		});
	}

	@FXML
	public void initialize() {

		StringConverter<Map<String, Object>> stringConverter = new StringConverter<Map<String, Object>>() {

			@Override
			public String toString(Map<String, Object> object) {
				if (object == null)
					return "";

				Object alias = object.get("alias");
				Object url = object.get("jdbc.url");

				if (ValueUtil.isNotEmpty(alias))
					return String.format("%s (%s)", alias.toString(), url.toString());

				return url.toString();
			}

			@Override
			public Map<String, Object> fromString(String string) {
				return null;
			}
		};
		cbConnection.setConverter(stringConverter);
		cbConnection.setCellFactory(TextFieldListCell.forListView(stringConverter));

		cbConnection.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Map<String, Object>>() {

			@Override
			public void changed(ObservableValue<? extends Map<String, Object>> observable, Map<String, Object> oldValue,
					Map<String, Object> newValue) {

				if (newValue == null)
					return;

				String jdbcDriver = ConfigResourceLoader.getInstance().get("dbms." + newValue.get(ResourceLoader.DBMS.toString()));

				registConnection(jdbcDriver, newValue.get(ResourceLoader.BASE_KEY_JDBC_URL).toString(),
						newValue.get(ResourceLoader.BASE_KEY_JDBC_ID) == null ? ""
								: newValue.get(ResourceLoader.BASE_KEY_JDBC_ID).toString(),
						newValue.get(ResourceLoader.BASE_KEY_JDBC_PASS) == null ? ""
								: newValue.get(ResourceLoader.BASE_KEY_JDBC_PASS).toString());

			}
		});

	}

	/**
	 * 후처리
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 4.
	 */
	@FxPostInitialize
	public void postInit() {

		List<Map<String, Object>> availableConnections = DbUtil.getAvailableConnections();
		cbConnection.getItems().addAll(availableConnections);
	}

	/**
	 * database ping 확인 <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 5.
	 */
	@FXML
	public void btnPingOnAction() {
		ping(flag -> {
			DialogUtil.showMessageDialog(FxUtil.getWindow(this), "success!");
		}, err -> {
			DialogUtil.showExceptionDailog(FxUtil.getWindow(this), err);
		});
	}

	/**
	 * SQL문을 실행.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 5.
	 */
	@FXML
	public void btnExecuteOnAction() {

		String sql = this.txtSql.getText();
		if (ValueUtil.isEmpty(sql)) {
			return;
		}

		execute(sql);

	}

	/**
	 * 쿼리 실행후 결과를 그리드에 바인딩<br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 5.
	 * @param sql
	 */
	protected void execute(String sql) {

		ConnectionSupplier connectionSupplier = this.connectionSupplier.get();
		if (connectionSupplier == null) {
			DialogUtil.showMessageDialog(FxUtil.getWindow(this), "Select Dabase.");
			return;
		}
		try (Connection con = connectionSupplier.getConnection()) {

			List<Map<String, Object>> selectLimit = DbUtil.selectLimit(con, sql, 10, 100);

			tbData.getItems().clear();
			tbData.getColumns().clear();

			binding(selectLimit);
			tbData.getItems().addAll(selectLimit);

		} catch (Exception e) {
			DialogUtil.showExceptionDailog(FxUtil.getWindow(this), e);
		}

	}

	private void binding(List<Map<String, Object>> query) {
		Map<String, Object> map = query.get(0);
		Iterator<String> iterator = map.keySet().iterator();
		// tbResult.getColumns().add(tcSelectRow);
		while (iterator.hasNext()) {
			String column = iterator.next();
			TableColumn<Map<String, Object>, Object> e = new TableColumn<>(column);

			e.setCellFactory(arg -> {
				return new TextFieldTableCell<>();
			});

			e.setCellValueFactory(arg -> {
				Object value = arg.getValue().get(column);
				return new SimpleObjectProperty<>(value);
			});
			e.setComparator((a, b) -> {

				if (a == null && b == null)
					return 0;

				if (a == null)
					return -1;

				if (b == null)
					return -1;

				if (ValueUtil.isNumber(a.toString()) && ValueUtil.isNumber(b.toString())) {
					return Double.compare(Double.parseDouble(a.toString()), Double.parseDouble(b.toString()));
				} else {
					return Strings.compareTo(a.toString(), b.toString());
				}

			});
			tbData.getColumns().add(e);
		}
	}

	protected String generateId() {
		return IdGenUtil.generate();
	}

	/**
	 * Step 1
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 15.
	 * @param displayName
	 * @param alias
	 * @param desc
	 * @return
	 */
	public SqlMappingTableViewRegister regist(String displayName, String alias, String desc) {
		this.displayName = displayName;
		this.alias = alias;
		this.desc = desc;
		return this;
	}

	/**
	 * Step 2
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 15.
	 * @param driver
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	public SqlMappingTableViewRegister registConnection(String driver, String url, String username, String password) {
		this.connectionSupplier.set(new ConnectionSupplier(driver, url, username, password));
		return this;
	}

	/**
	 * Step 3
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 15.
	 * @param sql
	 * @return
	 */
	public SqlMappingTableViewRegister registSql(String sql) {
		this.sql = sql;
		this.columns = avaliableFilters();
		return this;
	}

	/**
	 * Step 4
	 * 
	 * Insert item.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 15.
	 * @return
	 */
	public SqlMappingTableViewRegister create() {
		return this;
	}

	public void setFilters(List<String> columns) {
		this.columns = columns;
	}

	public List<String> avaliableFilters() {
		return ValueUtil.getVelocityKeys(this.sql);
	}

	/**
	 * get id
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 15.
	 * @return
	 */

	public String getAppId() {
		return this.id;
	}

	public void ping(Consumer<Boolean> onSuccess, Consumer<Throwable> exHandler) {
		ConnectionSupplier conSupplier = this.connectionSupplier.get();
		if (conSupplier == null)
			return;

		DbUtil.pingAsync(conSupplier, onSuccess, exHandler);
	}

	class ConnectionSupplier implements Supplier<PoolProperties> {
		PoolProperties poolProperties = new PoolProperties();
		String driver, url, username, password;

		public ConnectionSupplier(String driver, String url, String username, String password) {

			this.driver = driver;
			this.url = url;
			this.username = username;
			this.password = password;

			poolProperties.setUrl(url);
			poolProperties.setDriverClassName(driver);
			poolProperties.setUsername(username);
			poolProperties.setPassword(password);

		}

		@Override
		public PoolProperties get() {
			return poolProperties;
		}

		public Connection getConnection() {
			try {
				return DbUtil.getConnection(driver, url, username, password);
			} catch (Exception e) {
				LOGGER.error(ValueUtil.toString(e));
			}
			return null;
		}

	}

	@Override
	public boolean regist(List<Map<String, Object>> properties) throws Exception {
		return false;
	}

	@Override
	public List<RegistItem> registPages() {
		return Collections.emptyList();
	}
}
