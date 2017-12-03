/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.prcd.mssql
 *	작성일   : 2017. 11. 30.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.prcd.mssql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.sql.functions.ConnectionSupplier;
import com.kyj.fx.voeditor.visual.framework.mapper.MapBaseRowMapper;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.util.Callback;

/**
 * @author KYJ
 *
 */
public class DefaultMssqlProcedureCallComposite extends MssqlProcedureCallComposite<List<Map<String, Object>>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMssqlProcedureCallComposite.class);

	private MapBaseRowMapper mapper = new MapBaseRowMapper();

	private Callback<ResultSet, List<Map<String, Object>>> converter = new Callback<ResultSet, List<Map<String, Object>>>() {

		@Override
		public List<Map<String, Object>> call(ResultSet rs) {

			int row = 0;
			ArrayList<Map<String, Object>> arrayList = new ArrayList<>();
			try {
				while (rs.next()) {
					arrayList.add(mapper.mapRow(rs, ++row));
				}
			} catch (SQLException e) {
				LOGGER.error(ValueUtil.toString(e));
			}

			return arrayList;
		}
	};

	public DefaultMssqlProcedureCallComposite(ConnectionSupplier connectionSupplier) {
		super(connectionSupplier);
		setConverter(converter);

	}

}
