/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.functions
 *	작성일   : 2015. 10. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.functions;

import java.util.List;
import java.util.StringTokenizer;
import java.util.function.BiFunction;

import com.kyj.fx.voeditor.core.model.meta.ClassMeta;
import com.kyj.fx.voeditor.core.model.meta.FieldMeta;

import kyj.Fx.dao.wizard.DaoWizard;
import kyj.Fx.dao.wizard.core.BaseFxExtractDaoMethod;
import kyj.Fx.dao.wizard.core.DaoBaseResultSetStatement;
import kyj.Fx.dao.wizard.core.FxDaoResultSetConverter;
import kyj.Fx.dao.wizard.core.IExtractDaoMethod;
import kyj.Fx.dao.wizard.core.IResultSetConverter;
import kyj.Fx.dao.wizard.core.model.vo.BaseResultMapper;
import kyj.Fx.dao.wizard.core.model.vo.FxDao;
import kyj.Fx.dao.wizard.core.model.vo.TbmSysDaoDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoColumnsDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoMethodsDVO;
import kyj.Fx.dao.wizard.memory.DatabaseTypeMappingResourceLoader;

/**
 * @author KYJ
 *
 */
public class TypeChangedGenerateDaoWizardFunction<C extends ClassMeta, DAO extends TbmSysDaoDVO, M extends TbpSysDaoMethodsDVO, F extends FieldMeta>
		implements BiFunction<C, DAO, DaoWizard<C, M, F>> {

	@Override
	public DaoWizard<C, M, F> apply(C t, DAO vo) {

		String className = t.getName();
		String packageName = t.getPackageName();
		Class<?> extendsBaseClass = t.getExtendClassName();

		vo.setClassName(className);
		vo.setPackageName(packageName);
		vo.setExtendsClassName(extendsBaseClass == null ? "" : extendsBaseClass.getName());

		DaoWizard<C, M, F> daowizard = (DaoWizard<C, M, F>) new TypeChgDaoWizard<>(t, vo.getTbpSysDaoMethodsDVOList());
		daowizard.build();
		return daowizard;
	}

}

class TypeChgDaoWizard<C extends ClassMeta, DAO extends TbmSysDaoDVO, M extends TbpSysDaoMethodsDVO, F extends FieldMeta>
		extends DaoWizard<C, M, F> {

	/**
	 * @param classMeta
	 * @param methods
	 */
	public TypeChgDaoWizard(C classMeta, List<M> methods) {
		super(classMeta, methods);
	}

	/* (non-Javadoc)
	 * @see kyj.Fx.dao.wizard.DaoWizard#getIExtractMethod()
	 */
	@Override
	protected IExtractDaoMethod<M> getIExtractMethod() {
		return new BaseFxExtractDaoMethod<M>() {

			/* (non-Javadoc)
			 * @see kyj.Fx.dao.wizard.core.BaseFxExtractDaoMethod#daoBaseResultSetStatement(kyj.Fx.dao.wizard.core.model.vo.FxDao, kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoMethodsDVO)
			 */
			@Override
			protected DaoBaseResultSetStatement<BaseResultMapper<M>, M> daoBaseResultSetStatement(FxDao vo, M t) {
				BaseResultMapper<M> baseResultMapper = baseResultMapper(vo, t);
				return new DaoBaseResultSetStatement<BaseResultMapper<M>, M>(baseResultMapper, 2) {

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

							private String getStatement(String resultSetVarName, String columnType, String programType,
									String dbColumnName) {
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

				};
			}

		};
	}

}
