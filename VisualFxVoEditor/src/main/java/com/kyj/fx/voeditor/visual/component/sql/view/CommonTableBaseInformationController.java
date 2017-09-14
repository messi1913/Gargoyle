/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.view
 *	작성일   :  2016. 1. 1.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kyj.fx.voeditor.visual.component.sql.table.AbstractTableBaseInformationController;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * MYSQL 테이블 이름 및 코멘트정보를 리턴
 *
 * @author KYJ
 *
 */
public class CommonTableBaseInformationController extends AbstractTableBaseInformationController {

	public CommonTableBaseInformationController() throws Exception {
		super();
	}

	@Override
	public void postInit() {

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.kyj.fx.voeditor.visual.component.table.
	 * AbstractTableBaseInformationController
	 * #getTableCommentSQL(java.lang.String, java.lang.String)
	 */
	@Override
	public String getTableCommentSQL(String databaseName, String tableName) throws Exception {

		String sql = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.SQL_TABLE_COMMENT_WRAPPER, getDbmsDriver());
		if (sql != null) {
			//			if (ValueUtil.isNotEmpty(databaseName))
			//				sql = sql.replaceAll(":databaseName", databaseName);
			//			sql = sql.replaceAll(":tableName", tableName);

			HashMap<String, Object> map = new HashMap<>();
			map.put("databaseName", databaseName);
			map.put("tableName", tableName);
			return ValueUtil.getVelocityToText(sql, map, true);

		}

		return "";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.kyj.fx.voeditor.visual.component.table.
	 * AbstractTableBaseInformationController#getTableComment(java.util.List)
	 */
	@Override
	public String getTableComment(List<Map<String, Object>> resultList) {
		String comment = "";
		if (resultList.isEmpty())
			comment = "";
		else {
			Map<String, Object> map = resultList.get(0);
			comment = (String) map.get("COMMENTS");
			if (comment == null)
				comment = "";
		}

		return comment;
	}

}
