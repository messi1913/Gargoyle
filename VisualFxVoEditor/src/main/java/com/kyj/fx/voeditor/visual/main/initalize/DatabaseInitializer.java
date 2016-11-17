/**
 * package : com.kyj.fx.voeditor.visual.main.initalize
 *	fileName : DatabaseInitializer.java
 *	date      : 2015. 11. 22.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.main.initalize;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.DbUtil;
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
	private Supplier<Connection> connectionSupplier;

	public DatabaseInitializer(Supplier<Connection> connectionSupplier) throws Exception {
		this.connectionSupplier = connectionSupplier;

		//		String driver = ResourceLoader.getInstance().get(ResourceLoader.BASE_KEY_JDBC_DRIVER);

		try (Connection connection = this.connectionSupplier.get()) {
			String driver = DbUtil.getDriverNameByConnection(connection);

			LOGGER.debug(String.format("INFO driver : %s", driver));
			dbms = ValueUtil.getDriverToDBMSName(driver);
			LOGGER.debug(String.format("INFO dbms: %s", dbms));
			if (ValueUtil.isEmpty(dbms)) {
				LOGGER.error(String.format("Empty DBMS Value : %s", dbms));
			}

			FILE_NAME = String.format("META-INF/script/%s/initialize.script.sql", dbms);
			LOGGER.debug(String.format("FILE NAME : %s", FILE_NAME));
		}

	}

	private Consumer<Exception> exceptionHandler = event -> {
		LOGGER.error(ValueUtil.toString(event));
	};

	private Consumer<Void> onSuccessHandler;

	public void setExceptionHandler(Consumer<Exception> exceptionHandler) {
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
		StringBuilder sb = new StringBuilder();

		try (InputStream resourceAsStream = DatabaseInitializer.class.getClassLoader().getResourceAsStream(FILE_NAME)) {

			if (resourceAsStream == null) {
				LOGGER.debug("Initialize Item is Empty..... [ " + FILE_NAME + " ]");
				return;
			}

			try (InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream)) {
				try (BufferedReader br = new BufferedReader(inputStreamReader)) {
					String temp = br.readLine();
					while ((temp = br.readLine()) != null) {
						sb.append(temp).append("\n");
					}
				}
			}

		} catch (IOException e) {
			LOGGER.error(ValueUtil.toString(e));
			if (exceptionHandler != null)
				exceptionHandler.accept(e);
		}

		String[] split = sb.toString().split(";");

		try {
			/*
			 * thread처리에서 변경. API목적이 프로그램 초기화시 처리되는 기능에서 UI 버튼 클릭시 스키마 생성으로 변경되었기때문에
			 * trhead처리를 본 API를 감싸는 대상에서 처리할 수 있게함.
			 */

			if (split != null && split.length != 0) {
				// DB작업처리 시작.

				try (Connection con = this.connectionSupplier.get()) {
					
					int transactionedScope = DbUtil.getTransactionedScope(con, split, t -> Arrays.asList(t), exceptionHandler);
					if (transactionedScope != -1) {
						if (onSuccessHandler != null)
							onSuccessHandler.accept(null);
					}
				} catch (Exception e) {
					LOGGER.error(ValueUtil.toString(e));
					if (exceptionHandler != null)
						exceptionHandler.accept(e);
				}

//				DataSource dataSource = DbUtil.getDataSource();
//				int transactionedScope = DbUtil.getTransactionedScope(dataSource, split, (arr, tem) -> {
//					for (String sql : arr) {
//						if (ValueUtil.isNotEmpty(sql)) {
//
//							int update = tem.update(sql, Collections.emptyMap());
//							LOGGER.debug("{}", update);
//						}
//					}
//				} , exceptionHandler);

//				if (transactionedScope == 1) {
//					if (onSuccessHandler != null)
//						onSuccessHandler.accept(null);
//				}

			} else {
				LOGGER.info(String.format("there is not script dbms : [%s]", dbms));
			}

			//			new Thread(new Runnable() {
			//
			//				@Override
			//				public void run() {
			//					if (split != null && split.length != 0) {
			//						// DB작업처리 시작.
			//						try {
			//							int transactionedScope = DbUtil.getTransactionedScope(con, split, new Function<String[], List<String>>() {
			//								@Override
			//								public List<String> apply(String[] t) {
			//									return Arrays.asList(t);
			//								}
			//							}, exceptionHandler);
			//
			//							if (transactionedScope == 1) {
			//								if (onSuccessHandler != null)
			//									onSuccessHandler.accept(null);
			//							}
			//						} catch (Exception e) {
			//							if (exceptionHandler != null)
			//								exceptionHandler.accept(e);
			//						}
			//					} else {
			//						LOGGER.info(String.format("there is not script dbms : [%s]", dbms));
			//					}
			//				}
			//
			//			}).start();

		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
			if (exceptionHandler != null)
				exceptionHandler.accept(e);
		}

	}
}
