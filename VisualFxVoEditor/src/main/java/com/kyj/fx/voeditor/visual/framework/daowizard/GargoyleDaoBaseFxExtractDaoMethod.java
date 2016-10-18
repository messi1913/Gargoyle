/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.parser
 *	작성일   : 2016. 10. 17.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.daowizard;

import java.util.StringTokenizer;

import kyj.Fx.dao.wizard.core.DaoBaseResultSetStatement;
import kyj.Fx.dao.wizard.core.FxDaoResultSetConverter;
import kyj.Fx.dao.wizard.core.IResultSetConverter;
import kyj.Fx.dao.wizard.core.IRetrunStatement;
import kyj.Fx.dao.wizard.core.RetrunStatement;
import kyj.Fx.dao.wizard.core.model.vo.BaseResultMapper;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoColumnsDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoMethodsDVO;
import kyj.Fx.dao.wizard.memory.DatabaseTypeMappingResourceLoader;

/**
 * @author KYJ
 *
 */
public class GargoyleDaoBaseFxExtractDaoMethod<T extends BaseResultMapper<M>, M extends TbpSysDaoMethodsDVO>
		extends DaoBaseResultSetStatement<T, M> {

	/**
	 * @param mapper
	 */
	public GargoyleDaoBaseFxExtractDaoMethod(T mapper) {
		super(mapper);
	}

	/**
	 * @param mapper
	 * @param appendTabKeyCount
	 */
	public GargoyleDaoBaseFxExtractDaoMethod(T mapper, int appendTabKeyCount) {
		super(mapper, appendTabKeyCount);
	}

	/**
	 * @param mapper
	 * @param baseReturnStatement
	 * @param appendTabKeyCount
	 * 	tab key 수
	 */
	public GargoyleDaoBaseFxExtractDaoMethod(T mapper, IRetrunStatement baseReturnStatement, int appendTabKeyCount) {
		super(mapper, baseReturnStatement, appendTabKeyCount);
	}


	/* (non-Javadoc)
	 * @see kyj.Fx.dao.wizard.core.DaoBaseResultSetStatement#convert()
	 */
	@Override
	public String convert() {
		return super.convert();
	}

	protected IRetrunStatement baseReturnStatement() {
		return new RetrunStatement();
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
