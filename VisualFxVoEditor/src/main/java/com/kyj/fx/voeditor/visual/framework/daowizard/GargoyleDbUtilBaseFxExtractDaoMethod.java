/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.parser
 *	작성일   : 2016. 10. 17.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.daowizard;

import java.util.List;
import java.util.StringTokenizer;

import kyj.Fx.dao.wizard.core.DaoBaseResultSetStatement;
import kyj.Fx.dao.wizard.core.FxDaoResultSetConverter;
import kyj.Fx.dao.wizard.core.IResultSetConverter;
import kyj.Fx.dao.wizard.core.IRetrunStatement;
import kyj.Fx.dao.wizard.core.model.vo.BaseResultMapper;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoColumnsDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoFieldsDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoMethodsDVO;
import kyj.Fx.dao.wizard.memory.DatabaseTypeMappingResourceLoader;

/**
 * @author KYJ
 *
 */
public class GargoyleDbUtilBaseFxExtractDaoMethod<T extends BaseResultMapper<M>, M extends TbpSysDaoMethodsDVO>
		extends DaoBaseResultSetStatement<T, M> {

	/**
	 * @param mapper
	 */
	public GargoyleDbUtilBaseFxExtractDaoMethod(T mapper) {
		super(mapper);
	}

	/**
	 * @param mapper
	 * @param appendTabKeyCount
	 */
	public GargoyleDbUtilBaseFxExtractDaoMethod(T mapper, int appendTabKeyCount) {
		super(mapper, appendTabKeyCount);
	}

	/**
	 * @param mapper
	 * @param baseReturnStatement
	 * @param appendTabKeyCount
	 * 	tab key 수
	 */
	public GargoyleDbUtilBaseFxExtractDaoMethod(T mapper, IRetrunStatement baseReturnStatement, int appendTabKeyCount) {
		super(mapper, baseReturnStatement, appendTabKeyCount);
	}

	/* (non-Javadoc)
	 * @see kyj.Fx.dao.wizard.core.DaoBaseResultSetStatement#convert()
	 */
	@Override
	public String convert() {
		// 결과
		StringBuffer resultPart = new StringBuffer();
		// resultSet파트
		StringBuffer returnPart = new StringBuffer();
		// query 파트
		StringBuffer resultSetMappingPart = new StringBuffer();

		IRetrunStatement baseReturnStatement = getBaseReturnStatement();
		T mapper = getMapper();
		int appendTabKeyCount = getAppendTabKeyCount();

		String stringBufferVarName = mapper.getStringBufferVarName();
		String parameterMapVarName = mapper.getParameterMapVarName();
		String resultSetVarName = mapper.getResultSetVarName();
		String rowNumVarName = mapper.getRowNumVarName();

		List<TbpSysDaoFieldsDVO> inputFields = mapper.getT().getTbpSysDaoFieldsDVOList();
		List<TbpSysDaoColumnsDVO> columns = mapper.getT().getTbpSysDaoColumnsDVOList();
		String resultVoClass = mapper.getT().getResultVoClass();
		String type = getType(resultVoClass);
		/* 람다 expression */

		/* parameter part */
		returnPart.append(stringBufferVarName).append(".toString()").append(",");
		returnPart.append(parameterMapVarName).append(",(").append(resultSetVarName).append(",").append(rowNumVarName).append(")");
		returnPart.append("->{\n");

		/* [시작] Vo생성 statement */
		addImport(mapper.getFxDao(), resultVoClass);
		String varName = getVarName(resultVoClass);
		resultSetMappingPart.append(type).append(" ").append(varName).append(" = new ").append(type).append("();\n");
		/* [끝] Vo생성 statement */

		IResultSetConverter resultSetConverter = resultSetConverter();
		for (TbpSysDaoColumnsDVO col : columns) {
			String statement = resultSetConverter.convert(varName, resultSetVarName, col);
			resultSetMappingPart.append(statement).append("\n");
		}

		returnPart.append(applyedTabKeys(resultSetMappingPart.toString(), 1));
		returnPart.append(applyedTabKeys("return " + varName + ";", 1));

		returnPart.append("}\n");

		/* return문 베이스 */

		resultPart.append(baseReturnStatement.returnKeyword()).append(baseReturnStatement.getReturnStatement(returnPart.toString())); //.append(" query(").append(returnPart.toString()).append(");");

		return applyedTabKeys(resultPart.toString(), appendTabKeyCount);
	}

	/**
	 * 리턴문은 DbUtil statement형태로 수정.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 18.
	 * @return
	 */
	protected IRetrunStatement baseReturnStatement() {
		return new GargoyleDbUtilRetrunStatement();
	}

	/* (non-Javadoc)
	 * @see kyj.Fx.dao.wizard.core.DaoBaseResultSetStatement#resultSetConverter()
	 */
	@Override
	protected IResultSetConverter resultSetConverter() {
		return new FxDaoResultSetConverter() {

			@Override
			public String convert(String varName, String resultSetVarName, TbpSysDaoColumnsDVO col) {
				StringBuffer sb = new StringBuffer();
				String columnName = col.getColumnName();
				String columnType = col.getColumnType();
				String programType = col.getProgramType();

				sb.append(varName).append(".").append(setStatement(columnName)).append("(")
						.append(getStatement(resultSetVarName, columnType, programType, columnName)).append(");");
				return sb.toString();
			}

			@Override
			public String getTypeTo(String columnType) {
				return DatabaseTypeMappingResourceLoader.getInstance().get(columnType);
			}

			private String setStatement(String dbColumnName) {
				String setter = "set" + getSetterStatement(dbColumnName);
				return setter;
			}

			private String getStatement(String resultSetVarName, String columnType, String programType, String dbColumnName) {
				String typeTo = getTypeTo(columnType);

				if (programType != null && !programType.isEmpty()) {
					typeTo = programType;
				}

				//ResultSet에서 getInteger라는 함수는 존재하지않으며, 대신 getInt라는 함수가 존재함.
				if ("Integer".equals(typeTo)) {
					typeTo = "Int";
				} else if ("Long".equals(typeTo)) {
					typeTo = "Long";
				} else if ("Double".equals(typeTo)) {
					typeTo = "Double";
				}

				return resultSetVarName + "." + "get" + typeTo + "(\"" + dbColumnName + "\")";
			}

			private String getSetterStatement(String str) {
				StringTokenizer stringTokenizer = new StringTokenizer(str, "_");
				String nextElement = null;
				char[] charArray = null;
				String temp = "";
				while (stringTokenizer.hasMoreElements()) {
					nextElement = (String) stringTokenizer.nextElement();
					charArray = nextElement.toCharArray();
					charArray[0] = Character.toUpperCase(charArray[0]);
					for (int i = 1; i < charArray.length; i++) {
						charArray[i] = Character.toLowerCase(charArray[i]);
					}
					temp += String.valueOf(charArray);
				}
				return temp;
			}

		};
	}

}
