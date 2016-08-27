/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.functions
 *	작성일   : 2015. 11. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.functions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import kyj.Fx.dao.wizard.core.model.vo.TbmSysDaoDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoColumnsDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoFieldsDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoMethodsDVO;

import org.springframework.jdbc.core.RowMapper;

import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * @author KYJ
 *
 */
public class FxDAOReadFunction implements Function<TbmSysDaoDVO, TbmSysDaoDVO> {

	@Override
	public TbmSysDaoDVO apply(TbmSysDaoDVO t) {

		try {
			List<TbpSysDaoMethodsDVO> method = getMethod(t);
			t.setTbpSysDaoMethodsDVOList(method);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	private List<TbpSysDaoMethodsDVO> getMethod(TbmSysDaoDVO daoDVO) throws Exception {

		Map<String, Object> map = ValueUtil.toMap(daoDVO);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT \n");
		sb.append("PACKAGE_NAME  , \n");
		sb.append("CLASS_NAME  , \n");
		sb.append("METHOD_NAME  , \n");
		sb.append("RESULT_VO_CLASS  , \n");
		sb.append("SQL_BODY  , \n");
		sb.append("METHOD_DESC \n");
		sb.append(" FROM meerkat.TBP_SYS_DAO_METHODS \n");
		sb.append(" WHERE 1=1 \n");
		sb.append("AND PACKAGE_NAME = ':packageName'\n");
		sb.append("AND CLASS_NAME = ':className'\n");

		List<TbpSysDaoMethodsDVO> select = DbUtil.select(sb.toString(), map, new RowMapper<TbpSysDaoMethodsDVO>() {

			@Override
			public TbpSysDaoMethodsDVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				TbpSysDaoMethodsDVO dvo = new TbpSysDaoMethodsDVO(daoDVO);
				dvo.setResultVoClass(rs.getString("RESULT_VO_CLASS"));
				dvo.setSqlBody(rs.getString("SQL_BODY"));
				dvo.setMethodName(rs.getString("METHOD_NAME"));
				dvo.setMethodDesc(rs.getString("METHOD_DESC"));
				return dvo;
			}
		});

		for (TbpSysDaoMethodsDVO methodDVO : select) {
			List<TbpSysDaoColumnsDVO> columns = getColumns(daoDVO, methodDVO);
			methodDVO.setTbpSysDaoColumnsDVOList(columns);

			List<TbpSysDaoFieldsDVO> fields = getField(daoDVO, methodDVO);
			methodDVO.setTbpSysDaoFieldsDVOList(fields);
		}

		return select;

	}

	private List<TbpSysDaoFieldsDVO> getField(TbmSysDaoDVO daoDVO, TbpSysDaoMethodsDVO methodDVO) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT \n");
		sb.append("FIELD_NAME  , \n");
		sb.append("TYPE  , \n");
		sb.append("TEST_VALUE   \n");
		sb.append(" FROM meerkat.tbp_sys_dao_fields \n");
		sb.append(" WHERE 1=1 \n");
		sb.append("AND PACKAGE_NAME = ':packageName'\n");
		sb.append("AND CLASS_NAME = ':className'\n");
		sb.append("AND METHOD_NAME = ':methodName'\n");
		sb.toString();

		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("packageName", daoDVO.getPackageName());
		hashMap.put("className", daoDVO.getClassName());
		hashMap.put("methodName", methodDVO.getMethodName());

		return DbUtil.select(sb.toString(), hashMap, new RowMapper<TbpSysDaoFieldsDVO>() {

			@Override
			public TbpSysDaoFieldsDVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				TbpSysDaoFieldsDVO dvo = new TbpSysDaoFieldsDVO();
				dvo.setFieldName(rs.getString("FIELD_NAME"));
				dvo.setTestValue(rs.getString("TEST_VALUE"));
				dvo.setType(rs.getString("TYPE"));
				return dvo;
			}
		});
	}

	List<TbpSysDaoColumnsDVO> getColumns(TbmSysDaoDVO daoDVO, TbpSysDaoMethodsDVO methodDVO) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT \n");
		sb.append("COLUMN_NAME  , \n");
		sb.append("COLUMN_TYPE, \n");
		sb.append("PROGRAM_TYPE, \n");
		sb.append("LOCK_YN \n");
		sb.append(" FROM meerkat.tbp_sys_dao_columns \n");
		sb.append(" WHERE 1=1 \n");
		sb.append("AND PACKAGE_NAME = ':packageName'\n");
		sb.append("AND CLASS_NAME = ':className'\n");
		sb.append("AND METHOD_NAME = ':methodName'\n");

		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("packageName", daoDVO.getPackageName());
		hashMap.put("className", daoDVO.getClassName());
		hashMap.put("methodName", methodDVO.getMethodName());

		return DbUtil.select(sb.toString(), hashMap, new RowMapper<TbpSysDaoColumnsDVO>() {

			@Override
			public TbpSysDaoColumnsDVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				TbpSysDaoColumnsDVO dvo = new TbpSysDaoColumnsDVO();
				dvo.setColumnType(rs.getString("COLUMN_TYPE"));
				dvo.setColumnName(rs.getString("COLUMN_NAME"));
				dvo.setProgramType(rs.getString("PROGRAM_TYPE"));
				dvo.setLockYn(rs.getString("LOCK_YN"));
				return dvo;
			}
		});
	}

}
