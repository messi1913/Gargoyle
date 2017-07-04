/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.view
 *	작성일   : 2016. 1. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.view;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.kyj.fx.voeditor.visual.component.sql.table.AbstractTableCreateCodeInformationController;
import com.kyj.fx.voeditor.visual.framework.thread.ExecutorDemons;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.util.PogstgreUtil;
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

	/* 
	 * use embedded
	 * (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.sql.table.AbstractTableCreateCodeInformationController#getCreateTableSQL(java.lang.String, java.lang.String)
	 */
	@Deprecated
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
	public String fromQuery(List<String> result) {
		Optional<String> reduce = result.stream().reduce((a, b) -> {
			return a.concat(b).concat("\n");
		});
		if (reduce.isPresent())
			return reduce.get();
		return super.fromQuery(result);
	}

	@Override
	public String convertString(String t) {
		return t;
	}

	@Override
	protected boolean isEmbeddedSupport() {
		pgadminBasedir = PogstgreUtil.getPgdumpLocation();
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
		if (ValueUtil.isEmpty(pgadminBasedir))
			return;

		Service<String> service = new Service<String>() {

			@Override
			protected Task<String> createTask() {

				return new Task<String>() {

					@Override
					protected String call() throws Exception {
						try (Connection connection = getFrame().getConnection()) {
							String databaseName = getDatabaseName();
							String tableName = getTableName();

							return PogstgreUtil.dumpTable(connection, databaseName, tableName);
						} catch (SQLException e) {
							LOGGER.error(ValueUtil.toString(e));
						} catch (Exception e) {
							LOGGER.error(ValueUtil.toString(e));
						}
						return "";
					}
				};
			}
		};

		service.setExecutor(ExecutorDemons.getGargoyleSystemExecutorSerivce());
		service.setOnSucceeded(ev -> {

			Platform.runLater(() -> {
				Object value = ev.getSource().getValue();
				setTextSql(value.toString());
				txtSql.moveToLine(1, 1);
			});

		});

		service.start();
	}
}
