/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.view
 *	작성일   : 2018. 1. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.view;

import org.springframework.jdbc.core.RowMapper;

import com.kyj.fx.voeditor.visual.component.sql.table.TableColumnMetaVO;

/**
 * @author KYJ
 *
 */
public class MssqlTableColumnInformationController extends CommonTableColumnInformationController {

	public MssqlTableColumnInformationController() throws Exception {
		super();
	}

	@Override
	public void postInit() {
		// TODO Auto-generated method stub
		super.postInit();
	}

	@Override
	public String getTableColumnsSQL(String catalog, String databaseName, String tableName) throws Exception {
		return "";
	}

	@Override
	public RowMapper<TableColumnMetaVO> rowMapper() {
		// TODO Auto-generated method stub
		return super.rowMapper();
	}

}
