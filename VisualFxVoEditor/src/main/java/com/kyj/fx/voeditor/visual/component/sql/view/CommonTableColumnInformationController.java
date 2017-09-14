/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.view
 *	작성일   : 2016. 1. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.view;

import java.util.HashMap;

import org.springframework.jdbc.core.RowMapper;

import com.kyj.fx.voeditor.visual.component.sql.table.AbstractTableColumnInformationController;
import com.kyj.fx.voeditor.visual.component.sql.table.ConstraintKeyTypeFactory;
import com.kyj.fx.voeditor.visual.component.sql.table.IKeyType.KEY_TYPE;
import com.kyj.fx.voeditor.visual.component.sql.table.TableColumnMetaVO;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 *
 * 테이블 컬럼에 대한 정보조회후 리턴.
 *
 * @author KYJ
 *
 */
public class CommonTableColumnInformationController extends AbstractTableColumnInformationController {

	public CommonTableColumnInformationController() throws Exception {
		super();
	}

	@Override
	public void postInit() {

	}
	
	private ConstraintKeyTypeFactory fac = new ConstraintKeyTypeFactory((str) -> {
		// TODO 다른 DBMS에서도 적용가능하게 수정해야할부분임.

		// mysql or mariadb
		if ("PRI".equals(str))
			return KEY_TYPE.PRI;
		else if ("MUL".equals(str))
			return KEY_TYPE.MULTI;
		return KEY_TYPE.NOMAL;
	});

	@Override
	public String getTableColumnsSQL(String databaseName, String tableName) throws Exception {

		String sql = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.SQL_TABLE_COLUMNS_WRAPPER, getDbmsDriver());

		//		if (ValueUtil.isNotEmpty(databaseName))
		//			sql = sql.replaceAll(":databaseName", databaseName);
		//		sql = sql.replaceAll(":tableName", tableName);

		HashMap<String, Object> map = new HashMap<>();
		map.put("databaseName", databaseName);
		map.put("tableName", tableName);
		return ValueUtil.getVelocityToText(sql, map, true);
	}

	@Override
	public RowMapper<TableColumnMetaVO> rowMapper() {
		return (rs, rowNum) -> {
			TableColumnMetaVO tableColumnMetaVO = new TableColumnMetaVO();
			KEY_TYPE type = fac.getType(rs.getString("COLUMN_KEY"));
			tableColumnMetaVO.setKeyType(type);
			tableColumnMetaVO.setIsPrimaryKey(fac.isPrimaryKey(type));
			tableColumnMetaVO.setColumnName(rs.getString("COLUMN_NAME"));
			tableColumnMetaVO.setSortOrder(rs.getString("ORDINAL_POSITION"));
			tableColumnMetaVO.setIsNullable(rs.getString("IS_NULLABLE"));
			tableColumnMetaVO.setDataLength(rs.getString("DATA_LENGTH"));
			tableColumnMetaVO.setDataType(rs.getString("DATA_TYPE"));
			return tableColumnMetaVO;
		};
	}

}
