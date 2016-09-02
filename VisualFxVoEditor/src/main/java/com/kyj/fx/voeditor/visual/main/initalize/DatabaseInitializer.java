/**
 * package : com.kyj.fx.voeditor.visual.main.initalize
 *	fileName : DatabaseInitializer.java
 *	date      : 2015. 11. 22.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.main.initalize;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.Collections;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * 데이터베이스 스크립트 초기화
 *
 * 가고일에서 사용할 데이터베이스를 초기화한다.
 *
 *
 * 2016-09-02 프로그램 실행시 자동으로 생성되는 로직에서 버튼으로 클릭하여 생성하는 코드로 변경
 * 
 * @author KYJ
 *
 */
//@GagoyleInitializable
public class DatabaseInitializer implements Initializable {
	private static Logger LOGGER = LoggerFactory.getLogger(DatabaseInitializer.class);
	private String dbms;

	private String FILE_NAME = null;
	private Connection con;

	public DatabaseInitializer(Connection con) throws Exception {
		this.con = con;

		//		String driver = ResourceLoader.getInstance().get(ResourceLoader.BASE_KEY_JDBC_DRIVER);

		String driver = DbUtil.getDriverNameByConnection(this.con);

		LOGGER.debug(String.format("INFO driver : %s", driver));
		dbms = ValueUtil.getDriverToDBMSName(driver);
		LOGGER.debug(String.format("INFO dbms: %s", dbms));
		if (ValueUtil.isEmpty(dbms)) {
			LOGGER.error(String.format("Empty DBMS Value : %s", dbms));
		}

		FILE_NAME = String.format("META-INF/script/%s/initialize.script.sql", dbms);
		LOGGER.debug(String.format("FILE NAME : %s", FILE_NAME));
	}

	private Consumer<Throwable> exceptionHandler = event -> {
		LOGGER.error(ValueUtil.toString(event));
	};

	private Consumer<Void> onSuccessHandler;

	public void setExceptionHandler(Consumer<Throwable> exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	public void setOnSuccessHandler(Consumer<Void> onSuccessHandler) {
		this.onSuccessHandler = onSuccessHandler;
	}

	@Override
	public void initialize() {

		//TODO 검증완료후 반영할것.
		//		if (false) {

		LOGGER.debug("Initialize");

		InputStream resourceAsStream = DatabaseInitializer.class.getClassLoader().getResourceAsStream(FILE_NAME);
		if (resourceAsStream == null) {
			LOGGER.debug("Initialize Item is Empty..... [ " + FILE_NAME + " ]");
			return;
		}

		InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream);
		BufferedReader br = new BufferedReader(inputStreamReader);

		// List<String> scriptList = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		try {
			String temp = br.readLine();
			while ((temp = br.readLine()) != null) {
				sb.append(temp).append("\n");
			}

			final String[] split = sb.toString().split(";");
			new Thread(new Runnable() {

				@Override
				public void run() {
					if (split != null && split.length != 0) {
						// DB작업처리 시작.
						try {
							int transactionedScope = DbUtil.getTransactionedScope(split, (t, template) -> {

								for (String sql : t) {
									if (!sql.trim().isEmpty()) {
										LOGGER.info(String.format("Initalize Sciprt : \n %s", sql));
										template.update(sql, Collections.emptyMap());
									}
								}

							}, exceptionHandler);

							if (transactionedScope == 1) {
								if (onSuccessHandler != null)
									onSuccessHandler.accept(null);
							}
						} catch (Exception e) {
							if (exceptionHandler != null)
								exceptionHandler.accept(e);
						}
					} else {
						LOGGER.info(String.format("there is not script dbms : [%s]", dbms));
					}
				}

			}).start();

		} catch (Exception e) {
			if (exceptionHandler != null)
				exceptionHandler.accept(e);
		}

	}
}
