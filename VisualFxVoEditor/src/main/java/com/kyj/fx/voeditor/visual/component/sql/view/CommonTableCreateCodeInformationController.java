/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.view
 *	작성일   : 2016. 1. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.view;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.RowMapper;

import com.kyj.fx.voeditor.visual.component.sql.table.AbstractTableCreateCodeInformationController;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * 공통 테이블 CREATE문 조회 컨트롤러
 *
 * @author KYJ
 *
 */
public class CommonTableCreateCodeInformationController extends AbstractTableCreateCodeInformationController<String> {

	public CommonTableCreateCodeInformationController() throws Exception {
		super();
	}

	@Override
	public String getCreateTableSQL(String catalog, String databaseName, String tableName) {
		String sql = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.SQL_TABLE_CREATE_WRAPPER, getDbmsDriver());
//		if(ValueUtil.isNotEmpty(databaseName))
//			sql = sql.replaceAll(":databaseName", databaseName);
//		sql = sql.replaceAll(":tableName", tableName);

		HashMap<String, Object> map = new HashMap<>();
		map.put("databaseName", databaseName);
		map.put("tableName", tableName);
		return ValueUtil.getVelocityToText(sql, map, true, null);
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

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.sql.table.AbstractTableCreateCodeInformationController#isEmbeddedSupport()
	 */
	@Override
	protected boolean isEmbeddedSupport() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.sql.table.AbstractTableCreateCodeInformationController#getEmbeddedScript()
	 */
	@Override
	protected String getEmbeddedScript() {
		return null;
	}

}
