/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.view
 *	작성일   : 2016. 1. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.view;

import java.util.HashMap;

import org.springframework.jdbc.core.RowMapper;

import com.kyj.fx.voeditor.visual.component.sql.table.AbstractTableIndexInformationController;
import com.kyj.fx.voeditor.visual.component.sql.table.TableIndexLeaf;
import com.kyj.fx.voeditor.visual.component.sql.table.TableIndexNode;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * @author KYJ
 *
 */
public class CommonTableIndexInformationController extends AbstractTableIndexInformationController {

	public CommonTableIndexInformationController() throws Exception {
		super();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.kyj.fx.voeditor.visual.component.sql.table.
	 * AbstractTableIndexInformationController#getIndexSQL(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public String getIndexSQL(String databaseName, String tableName) throws Exception {

		String sql = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.SQL_TABLE_INDEX_WRAPPER, getDbmsDriver());
		if(ValueUtil.isNotEmpty(databaseName))
			sql = sql.replaceAll(":databaseName", databaseName);
		sql = sql.replaceAll(":tableName", tableName);

		HashMap<String, Object> map = new HashMap<>();
		map.put("databaseName", databaseName);
		map.put("tableName", tableName);
		return ValueUtil.getVelocityToText(sql, map);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.kyj.fx.voeditor.visual.component.sql.table.
	 * AbstractTableIndexInformationController#mapper()
	 */
	@Override
	public RowMapper<TableIndexNode> mapper() {
		return (rs, row) -> {
			TableIndexLeaf tableIndexNode = new TableIndexLeaf("", "");
			tableIndexNode.setType(rs.getString("CONSTRAINT_TYPE"));
			tableIndexNode.setConstraintName(rs.getString("CONSTRAINT_NAME"));
			tableIndexNode.setColumnNane(rs.getString("COLUMN_NAME"));
			return tableIndexNode;
		};
	}

}
