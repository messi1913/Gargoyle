/********************************
 *	프로젝트 : kyj.Fx.dao.wizard
 *	패키지   : kyj.Fx.dao.wizard.core
 *	작성일   : 2015. 10. 29.
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.dao.wizard.core;

import java.util.List;
import java.util.function.Supplier;

import kyj.Fx.dao.wizard.core.model.vo.BaseResultMapper;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoColumnsDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoFieldsDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoMethodsDVO;

/**
 *
 * 람다식 기반.
 *
 * meerket Core에서 제공되는 AbstractDAO기반의 매퍼부분을 생성한다.
 *
 * @author KYJ
 *
 */
public class DaoBaseResultSetStatement<T extends BaseResultMapper<M>, M extends TbpSysDaoMethodsDVO> extends FxDaoCommons
		implements Supplier<String> {
	/**
	 * 텍스트가 입력될때 들어갈 \t의 수를 정의함.
	 *
	 * @최초생성일 2015. 10. 29.
	 */
	private int appendTabKeyCount;
	private T mapper;
	//	private IResultSetConverter converter;

	public DaoBaseResultSetStatement(T mapper) {
		this(mapper, 0);
	}

	public DaoBaseResultSetStatement(T mapper, int appendTabKeyCount) {
		if (mapper == null)
			throw new IllegalArgumentException("mapper is null");
		this.appendTabKeyCount = appendTabKeyCount;
		this.mapper = mapper;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 26.
	 * @return
	 */
	protected IResultSetConverter resultSetConverter() {
		return new FxDaoResultSetConverter();
	}

	public String convert() {
		// 결과
		StringBuffer resultPart = new StringBuffer();
		// resultSet파트
		StringBuffer returnPart = new StringBuffer();
		// query 파트
		StringBuffer resultSetMappingPart = new StringBuffer();

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
		resultPart.append("return query(").append(returnPart.toString()).append(");");

		return applyedTabKeys(resultPart.toString(), this.appendTabKeyCount);
	}

	@Override
	public String get() {
		return convert();
	}

	/**
	 * @return the appendTabKeyCount
	 */
	public int getAppendTabKeyCount() {
		return appendTabKeyCount;
	}

	/**
	 * @param appendTabKeyCount
	 *            the appendTabKeyCount to set
	 */
	public void setAppendTabKeyCount(int appendTabKeyCount) {
		this.appendTabKeyCount = appendTabKeyCount;
	}

}
