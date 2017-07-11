package com.kyj.fx.voeditor.visual.component.sql.functions;

import java.sql.Connection;
import java.util.Map;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.handler.ExceptionHandler;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.utils.EncrypUtil;

/**
 * 데이터베이스 커넥션정보를 리턴해주는 객체
 *  더불어 접속 관련 메타정보를 기록.
 *  
 * @author KYJ
 *
 */
public class ConnectionSupplier implements Supplier<Connection> {

	private static Logger LOGGER = LoggerFactory.getLogger(ConnectionSupplier.class);

	private String url;
	private String username;
	private String password;
	private String driver;
	private ExceptionHandler exHandler;

	public ConnectionSupplier() {

	}

	public ConnectionSupplier(Map<String, Object> map) throws Exception {
		this.url = map.get(ResourceLoader.BASE_KEY_JDBC_URL).toString();
		this.username = map.get(ResourceLoader.BASE_KEY_JDBC_ID) == null ? "" : map.get(ResourceLoader.BASE_KEY_JDBC_ID).toString();
		this.password = map.get(ResourceLoader.BASE_KEY_JDBC_PASS) == null ? ""
				: EncrypUtil.decryp(map.get(ResourceLoader.BASE_KEY_JDBC_PASS).toString());
		this.driver = map.get("driver").toString();
	}

	@Override
	public Connection get() {

		Connection con = null;
		try {

			if (getUrl() == null || getUsername() == null || getPassword() == null) {
				con = DbUtil.getConnection();
			} else {
				con = DbUtil.getConnection(getDriver(), getUrl(), getUsername(), getPassword());
			}
			// Class.forName(driver);
			// con = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			if (exHandler != null)
				exHandler.handle(e);
			else
				LOGGER.error(ValueUtil.toString(e));
		}
		return con;
	}

	public ExceptionHandler getExHandler() {
		return exHandler;
	}

	public void setExHandler(ExceptionHandler exHandler) {
		this.exHandler = exHandler;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getDriver() {
		return driver;
	}

}
