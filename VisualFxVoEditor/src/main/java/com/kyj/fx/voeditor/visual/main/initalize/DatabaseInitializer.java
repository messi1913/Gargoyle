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
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * 데이터베이스 스크립트 초기화
 *
 * 가고일에서 사용할 데이터베이스를 초기화한다.
 *
 * @author KYJ
 *
 */
@GagoyleInitializable
public class DatabaseInitializer implements Initializable {
	private static Logger LOGGER = LoggerFactory.getLogger(DatabaseInitializer.class);
	private String dbms;

	private String FILE_NAME = null;

	public DatabaseInitializer() {

		String driver = ResourceLoader.getInstance().get(ResourceLoader.BASE_KEY_JDBC_DRIVER);
		LOGGER.debug(String.format("INFO driver : %s", driver));
		dbms = ValueUtil.getDriverToDBMSName(driver);
		LOGGER.debug(String.format("INFO dbms: %s", dbms));
		if (ValueUtil.isEmpty(dbms)) {
			LOGGER.error(String.format("Empty DBMS Value : %s", dbms));
		}

		FILE_NAME = String.format("META-INF/script/%s/initialize.script.sql", dbms);
		LOGGER.debug(String.format("FILE NAME : %s", FILE_NAME));
	}

	@Override
	public void initialize() throws Exception {
		
		//TODO 검증완료후 반영할것.
		if (false) {

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
							DbUtil.getTransactionedScope(split, (t, template) -> {

								for (String sql : t) {
									if (!sql.trim().isEmpty()) {
										LOGGER.info(String.format("Initalize Sciprt : \n %s", sql));
										template.update(sql, Collections.emptyMap());
									}
								}

							}, event -> {
								LOGGER.error(ValueUtil.toString(event));
							});
						} catch (Exception e) {
							LOGGER.error(ValueUtil.toString(e));
						}
					} else {
						LOGGER.info(String.format("there is not script dbms : [%s]", dbms));
					}
				}

			}).start();
		}
	}
}
