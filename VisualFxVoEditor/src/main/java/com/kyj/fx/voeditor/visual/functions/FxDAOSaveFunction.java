/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.functions
 *	작성일   : 2015. 10. 29.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.functions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.IdGenUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import kyj.Fx.dao.wizard.core.model.vo.TbmSysDaoDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoColumnsDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoFieldsDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoMethodsDVO;

/**
 * 데이터베이스 저장 처리
 *
 * @author KYJ
 *
 */
public class FxDAOSaveFunction implements Function<TbmSysDaoDVO, Integer>, BiTransactionScope<TbmSysDaoDVO, NamedParameterJdbcTemplate> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FxDAOSaveFunction.class);

	private static final String METHOD_DESC = "methodDesc";
	private static final String METHOD_NAME = "methodName";

	private Consumer<Exception> exceptionHandler;

	public FxDAOSaveFunction(Consumer<Exception> exceptionHandler) {
		this.exceptionHandler = exceptionHandler;

	}

	/*
	 * 성공적으로 처리가 끝나면 1을 반환 아니면 -1을 반환
	 */
	@Override
	public Integer apply(TbmSysDaoDVO t) {

		try {

			return DbUtil.getTransactionedScope(t, this, exceptionHandler);
		} catch (Exception e) {
			return -1;
		}
	}

	@Override
	public int scope(TbmSysDaoDVO t, NamedParameterJdbcTemplate u) throws Exception {
		try {
			boolean existsSchemaDatabase = DbUtil.isExistsSchemaDatabase();
			updateTbmSysDao(t, u, existsSchemaDatabase);
			updateTbmSysDaoMethods(t, u, existsSchemaDatabase);
			return 1;
		} catch (Exception e) {
			throw e;
		}
	}

	private List<Map<String, Object>> supplyFieldMap(TbmSysDaoDVO t, final TbpSysDaoMethodsDVO methodDVO) {
		List<Map<String, Object>> supplyer = new ArrayList<>();
		List<TbpSysDaoFieldsDVO> tbpSysDaoFields = methodDVO.getTbpSysDaoFieldsDVOList();
		for (TbpSysDaoFieldsDVO dvo : tbpSysDaoFields) {
			Map<String, Object> map = ValueUtil.toMap(dvo);
			map.put(METHOD_NAME, methodDVO.getMethodName());
			applyClassMeta(t, map);
			supplyer.add(map);
		}
		return supplyer;
	}

	private List<Map<String, Object>> supplyColumnMap(TbmSysDaoDVO t, final TbpSysDaoMethodsDVO methodDVO) {
		List<Map<String, Object>> supplyer = new ArrayList<>();
		List<TbpSysDaoColumnsDVO> tbpSysDaoColumnsDVOList = methodDVO.getTbpSysDaoColumnsDVOList();
		for (TbpSysDaoColumnsDVO dvo : tbpSysDaoColumnsDVOList) {
			Map<String, Object> map = ValueUtil.toMap(dvo);
			map.put(METHOD_NAME, methodDVO.getMethodName());
			applyClassMeta(t, map);
			supplyer.add(map);
		}
		return supplyer;
	}

	private Map<String, Object> supplyMethodMap(final TbmSysDaoDVO t, final TbpSysDaoMethodsDVO dvo) throws Exception {
		String methodName = dvo.getMethodName();
		if (methodName == null || methodName.isEmpty()) {
			throw new IllegalArgumentException("method name is null!");
		}
		Map<String, Object> map = ValueUtil.toMap(dvo, "resultVoClass", "sqlBody", METHOD_DESC, METHOD_NAME);
		applyClassMeta(t, map);

		// history
		String histTsp = IdGenUtil.generate();
		map.put("histTsp", histTsp);
		map.put("fstRegDt", new Timestamp(System.currentTimeMillis()));
		return map;
	}

	private void applyClassMeta(TbmSysDaoDVO supplyer, Map<String, Object> applyedTarget) {
		String packageName = supplyer.getPackageName();
		String className = supplyer.getClassName();
		applyedTarget.put("packageName", packageName);
		applyedTarget.put("className", className);
	}

	@SuppressWarnings("unchecked")
	private void updateTbmSysDaoMethods(TbmSysDaoDVO t, NamedParameterJdbcTemplate u, boolean existsSchemaDatabase) throws Exception {
		List<TbpSysDaoMethodsDVO> tbpSysDaoMethodsDVOList = t.getTbpSysDaoMethodsDVOList();
		if (tbpSysDaoMethodsDVOList == null || tbpSysDaoMethodsDVOList.isEmpty())
			return;

		StringBuffer deleteFieldSQLBuf;
		StringBuffer deleteColumnSQLBuf;
		StringBuffer deleteMethodSQLBuf;
		StringBuffer insertMethodSql;
		StringBuffer insertMethodHistSql;
		StringBuffer insertColumnsSql;
		StringBuffer insertFieldsSql;

		deleteFieldSQLBuf = new StringBuffer();
		deleteFieldSQLBuf.append("DELETE FROM  \n");
		if (existsSchemaDatabase)
			deleteFieldSQLBuf.append(" meerkat.TBP_SYS_DAO_FIELDS \n");
		else
			deleteFieldSQLBuf.append(" TBP_SYS_DAO_FIELDS \n");
		deleteFieldSQLBuf.append(" WHERE 1=1 \n");
		deleteFieldSQLBuf.append("AND PACKAGE_NAME = :packageName \n");
		deleteFieldSQLBuf.append("AND CLASS_NAME = :className \n");
		// deleteFieldSQLBuf.append("AND METHOD_NAME = :methodName\n");

		deleteColumnSQLBuf = new StringBuffer();
		deleteColumnSQLBuf.append("DELETE FROM  \n");
		if (existsSchemaDatabase)
			deleteColumnSQLBuf.append(" meerkat.TBP_SYS_DAO_COLUMNS WHERE 1=1 \n");
		else
			deleteColumnSQLBuf.append(" TBP_SYS_DAO_COLUMNS WHERE 1=1 \n");
		deleteColumnSQLBuf.append("AND PACKAGE_NAME = :packageName \n");
		deleteColumnSQLBuf.append("AND CLASS_NAME = :className \n");
		// deleteColumnSQLBuf.append("AND METHOD_NAME = :methodName\n");

		deleteMethodSQLBuf = new StringBuffer();
		deleteMethodSQLBuf.append("DELETE FROM  \n");
		if (existsSchemaDatabase)
			deleteMethodSQLBuf.append("meerkat.TBP_SYS_DAO_METHODS WHERE 1=1 \n");
		else
			deleteMethodSQLBuf.append("TBP_SYS_DAO_METHODS WHERE 1=1 \n");
		deleteMethodSQLBuf.append("AND PACKAGE_NAME = :packageName \n");
		deleteMethodSQLBuf.append("AND CLASS_NAME = :className \n");
		// deleteMethodSQLBuf.append("AND METHOD_NAME = :methodName \n");

		insertMethodSql = new StringBuffer();
		insertMethodSql.append("INSERT INTO  \n");
		if (existsSchemaDatabase)
			insertMethodSql.append("meerkat.TBP_SYS_DAO_METHODS \n");
		else
			insertMethodSql.append("TBP_SYS_DAO_METHODS \n");

		insertMethodSql.append("(PACKAGE_NAME,\n");
		insertMethodSql.append("CLASS_NAME,\n");
		insertMethodSql.append("METHOD_NAME,\n");
		insertMethodSql.append("RESULT_VO_CLASS,\n");
		insertMethodSql.append("SQL_BODY,\n");
		insertMethodSql.append("METHOD_DESC )\n");
		insertMethodSql.append("VALUES (\n");
		insertMethodSql.append(":packageName, \n");
		insertMethodSql.append(":className , \n");
		insertMethodSql.append(":methodName, \n");
		insertMethodSql.append(":resultVoClass,\n");
		insertMethodSql.append(":sqlBody,\n");
		insertMethodSql.append(":methodDesc ) ");

		insertMethodHistSql = new StringBuffer();
		insertMethodHistSql.append("INSERT INTO  \n");
		if (existsSchemaDatabase)
			insertMethodHistSql.append("meerkat.TBP_SYS_DAO_METHODS_H \n");
		else
			insertMethodHistSql.append("TBP_SYS_DAO_METHODS_H \n");

		insertMethodHistSql.append("(HIST_TSP,\n");
		insertMethodHistSql.append("PACKAGE_NAME,\n");
		insertMethodHistSql.append("CLASS_NAME,\n");
		insertMethodHistSql.append("METHOD_NAME,\n");
		insertMethodHistSql.append("RESULT_VO_CLASS,\n");
		insertMethodHistSql.append("SQL_BODY,\n");
		insertMethodHistSql.append("DML_TYPE,\n");
		insertMethodHistSql.append("FST_REG_DT,\n");
		insertMethodHistSql.append("METHOD_DESC )\n");
		insertMethodHistSql.append("VALUES (\n");
		insertMethodHistSql.append(":histTsp, \n");
		insertMethodHistSql.append(":packageName, \n");
		insertMethodHistSql.append(":className , \n");
		insertMethodHistSql.append(":methodName, \n");
		insertMethodHistSql.append(":resultVoClass,\n");
		insertMethodHistSql.append(":sqlBody,\n");
		insertMethodHistSql.append(":dmlType,\n");
		insertMethodHistSql.append(":fstRegDt,\n");
		insertMethodHistSql.append(":methodDesc ) ");

		insertColumnsSql = new StringBuffer();
		insertColumnsSql.append("INSERT INTO  \n");
		if (existsSchemaDatabase)
			insertColumnsSql.append("meerkat.TBP_SYS_DAO_COLUMNS ( \n");
		else
			insertColumnsSql.append("TBP_SYS_DAO_COLUMNS ( \n");

		insertColumnsSql.append("PACKAGE_NAME,\n");
		insertColumnsSql.append("CLASS_NAME,\n");
		insertColumnsSql.append("METHOD_NAME,\n");
		insertColumnsSql.append("COLUMN_NAME,\n");
		insertColumnsSql.append("COLUMN_TYPE, \n");
		insertColumnsSql.append("PROGRAM_TYPE, \n");
		insertColumnsSql.append("LOCK_YN ) \n");
		insertColumnsSql.append("VALUES (\n");
		insertColumnsSql.append(":packageName, \n");
		insertColumnsSql.append(":className , \n");
		insertColumnsSql.append(":methodName , \n");
		insertColumnsSql.append(":columnName, \n");
		insertColumnsSql.append(":columnType,  \n");
		insertColumnsSql.append(":programType, \n");
		insertColumnsSql.append(":lockYn ) \n");

		insertFieldsSql = new StringBuffer();
		insertFieldsSql.append("INSERT INTO  \n");
		if (existsSchemaDatabase)
			insertFieldsSql.append("meerkat.TBP_SYS_DAO_FIELDS (  \n");
		else
			insertFieldsSql.append("TBP_SYS_DAO_FIELDS (  \n");
		insertFieldsSql.append("PACKAGE_NAME,\n");
		insertFieldsSql.append("CLASS_NAME,\n");
		insertFieldsSql.append("METHOD_NAME,\n");
		insertFieldsSql.append("FIELD_NAME,\n");
		insertFieldsSql.append("TYPE , \n");
		insertFieldsSql.append("TEST_VALUE ) \n");
		insertFieldsSql.append("VALUES (\n");
		insertFieldsSql.append(":packageName, \n");
		insertFieldsSql.append(":className , \n");
		insertFieldsSql.append(":methodName , \n");
		insertFieldsSql.append(":fieldName, \n");
		insertFieldsSql.append(":type, \n");
		insertFieldsSql.append(":testValue ) \n");

		// List<Map<String, Object>> updateMethodList = new ArrayList<>();
		List<Map<String, Object>> insertMethodList = new ArrayList<>();
		List<Map<String, Object>> deleteMethodList = new ArrayList<>();

		List<Map<String, Object>> columnsInsertList = new ArrayList<>();
		List<Map<String, Object>> fieldsInsertList = new ArrayList<>();
		for (TbpSysDaoMethodsDVO methodDVO : tbpSysDaoMethodsDVOList) {

			{
				Map<String, Object> methodMap = supplyMethodMap(t, methodDVO);
				// if ("Y".equals(methodDVO.getDelYn())) {
				// LOGGER.debug("삭제요청 ... 요청된 정보");
				LOGGER.debug(String.format("package : %s class : %s method :%s", t.getPackageName(), t.getClassName(),
						methodDVO.getMethodName()));
				LOGGER.debug(String.format("히스토리 ID : %s", methodMap.get("histTsp").toString()));
				methodMap.put("dmlType", "D");
				deleteMethodList.add(new LinkedHashMap<>(methodMap));
				// continue;
				// }
			}
			/*
			 * UI에서는 삭제처리를 해버리면 데이터베이스에 삭제처리된 내용을 처리할 방법이 없으므로 모두 삭제하고 저장하는 방법으로
			 * 변경한다.
			 */

			// if (checkExists(SQL_CHECK_EXISTS_TBM_SYS_DAO_METHOD, methodMap,
			// u)) {
			// methodMap.put("dmlType", "U");
			// updateMethodList.add(methodMap);
			// }
			// else
			// {
			// methodMap.put("dmlType", "I");
			// insertMethodList.add(methodMap);
			// }
			Map<String, Object> methodMap = supplyMethodMap(t, methodDVO);
			methodMap.put("dmlType", "I");
			insertMethodList.add(methodMap);

			// if("D".equals(methodMap.get("dmlType")))
			// continue;
			// columnsForDelList.add(methodMap);
			columnsInsertList.addAll(supplyColumnMap(t, methodDVO));

			fieldsInsertList.addAll(supplyFieldMap(t, methodDVO));
		}

		try {
			// 컬럼메타 삭제
			if (!columnsInsertList.isEmpty()) {
				int update = update(deleteColumnSQLBuf.toString(), columnsInsertList.get(0), u);
				LOGGER.debug("DELETE COLUMN META DML RESULT : " + update);
			}

			// 필드 메타 삭제
			if (!fieldsInsertList.isEmpty()) {
				int update = update(deleteFieldSQLBuf.toString(), fieldsInsertList.get(0), u);
				LOGGER.debug("DELETE COLUMN META DML RESULT : " + update);
			}

			// int usize = updateMethodList.size();
			int isize = insertMethodList.size();
			int dsize = deleteMethodList.size();

			if (dsize != 0) {
				Map<String, Object>[] array = deleteMethodList.toArray(new LinkedHashMap[dsize]);
				batchUpdate(deleteMethodSQLBuf.toString(), array, u);
				batchUpdate(insertMethodHistSql.toString(), array, u);
			}

			// if (usize != 0) {
			// Map<String, Object>[] array = updateMethodList.toArray(new
			// HashMap[usize]);
			// batchUpdate(updateMethodsSql.toString(), array, u);
			// batchUpdate(insertMethodHistSql.toString(), array, u);
			// }

			// insert batch

			if (isize != 0) {
				Map<String, Object>[] array = insertMethodList.toArray(new LinkedHashMap[isize]);
				batchUpdate(insertMethodSql.toString(), array, u);
				batchUpdate(insertMethodHistSql.toString(), array, u);
			}

			// column insert batch
			if (!columnsInsertList.isEmpty())
				batchUpdate(insertColumnsSql.toString(), columnsInsertList.toArray(new LinkedHashMap[columnsInsertList.size()]), u);

			// field insert batch
			if (!fieldsInsertList.isEmpty())
				batchUpdate(insertFieldsSql.toString(), fieldsInsertList.toArray(new LinkedHashMap[fieldsInsertList.size()]), u);

		} catch (Exception e) {
			throw e;
		}

	}

	private void updateTbmSysDao(TbmSysDaoDVO t, NamedParameterJdbcTemplate u, boolean existsSchemaDatabase) throws Exception {
		StringBuffer sb = new StringBuffer();

		if (t.getClassName() == null || t.getClassName().isEmpty()) {
			throw new IllegalArgumentException("class Name is null.");
		}

		StringBuffer sqlCheckExistsTbmSysDao;
		sqlCheckExistsTbmSysDao = new StringBuffer();
		sqlCheckExistsTbmSysDao.append("SELECT 1 FROM \n");
		if (existsSchemaDatabase)
			sqlCheckExistsTbmSysDao.append("meerkat.TBM_SYS_DAO \n");
		else
			sqlCheckExistsTbmSysDao.append("TBM_SYS_DAO \n");
		sqlCheckExistsTbmSysDao.append("WHERE PACKAGE_NAME =:packageName AND CLASS_NAME =:className");

		Map<String, Object> hashMap = new LinkedHashMap<String, Object>();
		hashMap.put("packageName", t.getPackageName());
		hashMap.put("className", t.getClassName());
		hashMap.put("location", t.getLocation());
		hashMap.put("classDesc", t.getClassDesc());
		hashMap.put("tableName", t.getTableName());

		if (checkExists(sqlCheckExistsTbmSysDao.toString(), hashMap, u)) {
			sb.append("UPDATE\n");
			if (existsSchemaDatabase)
				sb.append(" meerkat.TBM_SYS_DAO  \n");
			else
				sb.append(" TBM_SYS_DAO  \n");
			sb.append("SET LOCATION = :location,\n");
			sb.append("CLASS_NAME =:className,\n");
			sb.append("TABLE_NAME =:tableName,\n");
			sb.append("CLASS_DESC =:classDesc\n");
			sb.append("WHERE 1=1\n");
			sb.append("AND PACKAGE_NAME =:packageName\n");
			sb.append("AND CLASS_NAME =:className\n");

		} else {
			sb.append("INSERT INTO \n");
			if (existsSchemaDatabase)
				sb.append("meerkat.TBM_SYS_DAO\n");
			else
				sb.append("TBM_SYS_DAO\n");

			sb.append("(PACKAGE_NAME, CLASS_NAME , LOCATION,\n");
			sb.append("CLASS_DESC, TABLE_NAME\n");
			sb.append(") VALUES\n");
			sb.append("(\n");
			sb.append(":packageName, \n");
			sb.append(":className,\n ");
			sb.append(":location, \n");
			sb.append(":classDesc, \n");
			sb.append(":tableName \n");
			sb.append(")\n");
		}

		String velocityToText = ValueUtil.getVelocityToText(sb.toString(), hashMap, false);

		try {
			u.update(velocityToText, hashMap);
		} catch (Exception e) {
			LOGGER.error("### ERROR QUERY ###");
			LOGGER.error(sb.toString());
			LOGGER.error("### ERROR Velocity QUERY ###");
			LOGGER.error(ValueUtil.getVelocityToText(sb.toString(), hashMap, true));
			LOGGER.error("### PARAMETER ###");
			LOGGER.error(hashMap.toString());
			throw e;
			// 최종연산 및 트랜잭션에 실패했다는 메세지를 전달하기 위해 e를 다시 던짐.
		}
	}

	/**
	 * 조회결과를 단순히 boolean으로만 리턴한다. 즉 조회되는 데이터가 있는지 없는지 확인처리만 하기위한 함수.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 30.
	 * @param sql
	 * @param paramMap
	 * @param u
	 * @return
	 */
	private boolean checkExists(final String sql, final Map<String, Object> paramMap, NamedParameterJdbcTemplate u) {
		if (u.query(sql, paramMap, new ResultSetExtractor<Boolean>() {

			@Override
			public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next())
					return true;
				return false;
			}
		}))
			return true;
		return false;
	}

	/**
	 * batchupdate를 수행한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 11. 2.
	 * @param sql
	 * @param params
	 * @param u
	 */
	private void batchUpdate(String sql, Map<String, ?>[] params, NamedParameterJdbcTemplate u) {
		u.batchUpdate(sql, params);
	}

	/**
	 * batchupdate를 수행한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 11. 2.
	 * @param sql
	 * @param params
	 * @param u
	 * @return
	 */
	private int update(String sql, Map<String, Object> param, NamedParameterJdbcTemplate u) {
		return u.update(sql, param);
	}
}
