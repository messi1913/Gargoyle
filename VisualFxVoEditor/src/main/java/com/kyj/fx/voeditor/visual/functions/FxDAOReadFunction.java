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
import java.util.stream.Collectors;

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
	public TbmSysDaoDVO apply(TbmSysDaoDVO dvo) {

		try {
			List<TbpSysDaoMethodsDVO> methods = getMethod(dvo);
			dvo.setTbpSysDaoMethodsDVOList(methods);

			List<TbpSysDaoColumnsDVO> columns = getColumns(dvo);
			List<TbpSysDaoFieldsDVO> fields = getFields(dvo);

			for (TbpSysDaoMethodsDVO m : methods) {
				List<TbpSysDaoColumnsDVO> collect = columns.stream().filter(c -> m.getMethodName().equals(c.getMethodName()))
						.collect(Collectors.toList());
				m.setTbpSysDaoColumnsDVOList(collect);
			}

			for (TbpSysDaoMethodsDVO m : methods) {
				List<TbpSysDaoFieldsDVO> collect = fields.stream().filter(c -> m.getMethodName().equals(c.getMethodName()))
						.collect(Collectors.toList());
				m.setTbpSysDaoFieldsDVOList(collect);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dvo;
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
		sb.append("AND PACKAGE_NAME = :packageName\n");
		sb.append("AND CLASS_NAME = :className\n");

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
		return select;

	}

	private List<TbpSysDaoFieldsDVO> getFields(TbmSysDaoDVO daoDVO) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select\n");
		sb.append("    method_name ,\n");
		sb.append("    field_name ,\n");
		sb.append("    type ,\n");
		sb.append("    test_value \n");
		sb.append("from\n");
		sb.append("    meerkat.tbp_sys_dao_fields \n");
		sb.append("where\n");
		sb.append("    1=1 \n");
		sb.append("    and package_name = :packageName \n");
		sb.append("    and class_name = :className \n");
		sb.append("#if($methodName)\n");
		sb.append("    and method_name = :methodName\n");
		sb.append("#end\n");

		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("packageName", daoDVO.getPackageName());
		hashMap.put("className", daoDVO.getClassName());

		return DbUtil.select(sb.toString(), hashMap, new RowMapper<TbpSysDaoFieldsDVO>() {

			@Override
			public TbpSysDaoFieldsDVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				TbpSysDaoFieldsDVO dvo = new TbpSysDaoFieldsDVO();
				dvo.setMethodName(rs.getString("method_name"));
				dvo.setFieldName(rs.getString("field_name"));
				dvo.setType(rs.getString("type"));
				dvo.setTestValue(rs.getString("test_value"));
				return dvo;
			}
		});
	}

	List<TbpSysDaoColumnsDVO> getColumns(TbmSysDaoDVO daoDVO) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select\n");
		sb.append("    method_name,\n");
		sb.append("    column_name ,\n");
		sb.append("    column_type,\n");
		sb.append("    program_type,\n");
		sb.append("    lock_yn \n");
		sb.append("from\n");
		sb.append("    meerkat.tbp_sys_dao_columns \n");
		sb.append("where\n");
		sb.append("    1=1 \n");
		sb.append("    and package_name = :packageName \n");
		sb.append("    and class_name = :className \n");
		sb.append("#if($methodName)\n");
		sb.append("    and method_name = :methodName\n");
		sb.append("#end\n");

		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("packageName", daoDVO.getPackageName());
		hashMap.put("className", daoDVO.getClassName());
		//		hashMap.put("methodName", methodDVO.getMethodName());

		return DbUtil.select(sb.toString(), hashMap, new RowMapper<TbpSysDaoColumnsDVO>() {

			@Override
			public TbpSysDaoColumnsDVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				TbpSysDaoColumnsDVO dvo = new TbpSysDaoColumnsDVO();
				dvo.setMethodName(rs.getString("method_name"));
				dvo.setColumnName(rs.getString("column_name"));
				dvo.setColumnType(rs.getString("column_type"));
				dvo.setProgramType(rs.getString("program_type"));
				dvo.setLockYn(rs.getString("lock_yn"));
				return dvo;
			}
		});
	}

}
