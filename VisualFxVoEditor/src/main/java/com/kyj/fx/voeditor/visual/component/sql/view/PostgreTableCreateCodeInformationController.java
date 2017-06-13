/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.view
 *	작성일   : 2016. 1. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.view;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.kyj.fx.voeditor.visual.component.sql.table.AbstractTableCreateCodeInformationController;
import com.kyj.fx.voeditor.visual.framework.thread.ExecutorDemons;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.RuntimeClassUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * MYSQL용 테이블 CREATE문 조회 컨트롤러
 *
 * @author KYJ
 *
 */
public class PostgreTableCreateCodeInformationController extends AbstractTableCreateCodeInformationController<String> {

	private static Logger LOGGER = LoggerFactory.getLogger(PostgreTableCreateCodeInformationController.class);

	private String pgadminBasedir;

	public PostgreTableCreateCodeInformationController() throws Exception {
		super();

	}

	@Override
	public String getCreateTableSQL(String databaseName, String tableName) {
		return "";
	}

	@Override
	public RowMapper<String> mapper() {
		return (rs, rowNum) -> {
			return rs.getString(2);
		};
	}

	@Override
	public String applyContent(List<String> result) {
		Optional<String> reduce = result.stream().reduce((a, b) -> {
			return a.concat(b).concat("\n");
		});
		if (reduce.isPresent())
			return reduce.get();
		return super.applyContent(result);
	}

	@Override
	public String convertString(String t) {
		return t;
	}

	@Override
	protected boolean isEmbeddedSupport() {
		pgadminBasedir = ResourceLoader.getInstance().get(ResourceLoader.POSTGRE_PGADMIN_BASE_DIR);
		return ValueUtil.isNotEmpty(pgadminBasedir);
	}

	@Override
	protected String getEmbeddedScript() {
		boolean embeddedSupport = isEmbeddedSupport();

		if (embeddedSupport) {
			getScript();
		}
		return "";
	}

	@Override
	public void postInit() {
	}

	private void getScript() {

		File pgDumpFile = new File(pgadminBasedir);

		Service<String> service = new Service<String>() {

			@Override
			protected Task<String> createTask() {

				return new Task<String>() {

					@Override
					protected String call() throws Exception {
						Connection connection = getFrame().getConnection();
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						ByteArrayOutputStream error = new ByteArrayOutputStream();
						try {
							DatabaseMetaData metaData = connection.getMetaData();

							String url = metaData.getURL();
							String replace = url.replace("jdbc:postgresql://", "");
							String[] split = replace.split(":");
							String ip = split[0];
							String[] split2 = split[1].split("/");
							String port = split2[0];
							String dbName = split2[1];

							String userName = metaData.getUserName();

							String databaseName = getDatabaseName();
							String tableName = getTableName();
							String path = pgDumpFile.getAbsolutePath();
							String argUserName = "--username=" + userName;
							String argHost = "--host=" + ip;
							String argPort = "--port=" + port;
							String argNoPasswd = "--no-password";
							String argDbName = "--dbname=" + dbName;

							String argSchema = databaseName + "." + tableName;
							if (ValueUtil.isEmpty(databaseName))
								argSchema = tableName;

							RuntimeClassUtil.exe(Arrays.asList(path, argUserName, argHost, argPort, argNoPasswd, argDbName, "-t", argSchema,
									"--schema-only"), out, error);

							return out.toString("UTF-8");
						} catch (SQLException e) {
							LOGGER.error(ValueUtil.toString(e));
						} catch (Exception e) {
							LOGGER.error(ValueUtil.toString(e));
						}
						return error.toString();
					}
				};
			}
		};

		service.setExecutor(ExecutorDemons.getGargoyleSystemExecutorSerivce());
		service.setOnSucceeded(ev -> {

			Platform.runLater(() -> {
				//				String message = ev.getSource().getMessage();
				Object value = ev.getSource().getValue();
				setTextSql(value.toString());
				txtSql.moveToLine(1, 1);
			});

		});

		service.start();
	}
}
