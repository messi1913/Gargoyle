/********************************
 *	프로젝트 : kyj.Fx.dao.wizard
 *	패키지   : kyj.Fx.dao.wizard
 *	작성일   : 2015. 10. 28.
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.dao.wizard.core;

import java.lang.reflect.Modifier;
import java.util.List;

import kyj.Fx.dao.wizard.core.model.vo.BaseResultMapper;
import kyj.Fx.dao.wizard.core.model.vo.FxDao;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoMethodsDVO;
import kyj.Fx.dao.wizard.core.util.ValueUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KYJ
 *
 */
public class BaseFxExtractDaoMethod<T extends TbpSysDaoMethodsDVO> extends FxDaoCommons implements IExtractDaoMethod<T> {
	private static Logger LOGGER = LoggerFactory.getLogger(BaseFxExtractDaoMethod.class);

	/**
	 * 사용할 버퍼 클래스 명
	 *
	 * @최초생성일 2015. 10. 28.
	 */
	public static final String STRING_BUFFER_CLASS_TYPE = "StringBuffer";

	/**
	 * 사용할 버퍼 클래스 변수이름 TODO 환경변수파일에 등록
	 */
	public static final String STRING_BUFFER_VARIABLE_NAME = "sb";

	/**
	 * TODO 환경변수파일에 등록하여 관리
	 *
	 * @최초생성일 2015. 11. 2.
	 */
	public static final String STRING_METHOD_PARAMETER_VARIABLE_NAME = "paramMap";

	public void extract(FxDao vo, T t) throws Exception {

		StringBuffer methodPart = new StringBuffer();
		String methodName = t.getMethodName();
		String desc = t.getMethodDesc();
		String resultVoClass = t.getResultVoClass();
		String codeBody = t.getSqlBody();
		int modifier = t.getModifier();
		Exception exceptionClass = t.getExceptionClass();

		if (methodName == null || methodName.isEmpty()) {
			throw new IllegalAccessException("[Method empty] class : + " + t);
		}

		/* [시작] 메소드 주석 */
		if (desc != null && !desc.isEmpty()) {
			methodPart.append(applyedTabKeys("/**\n", 1));
			methodPart.append(applyedTabKeys(desc, 1, () -> "*"));
			methodPart.append(applyedTabKeys("*/\n", 1));
		}

		/*
		 * 메소드 접근인지 체크. Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE
		 * | Modifier.ABSTRACT | Modifier.STATIC | Modifier.FINAL |
		 * Modifier.SYNCHRONIZED | Modifier.NATIVE | Modifier.STRICT;
		 */
		if ((Modifier.methodModifiers() & 0x0) == 0) {
			if (Modifier.isPrivate(modifier)) {
				methodPart.append("\tprivate ");
			} else if (Modifier.isPublic(modifier)) {
				methodPart.append("\tpublic ");
			} else {
				methodPart.append("\tprotected ");
			}

			if (Modifier.isAbstract(modifier))
				methodPart.append("abstract ");

			if (Modifier.isStatic(modifier))
				methodPart.append("static ");

			if (Modifier.isFinal(modifier))
				methodPart.append("final ");

			if (Modifier.isSynchronized(modifier))
				methodPart.append("synchronized ");

			// 리턴타입
			if (resultVoClass != null && !resultVoClass.isEmpty()) {

				int lastIndexOf = resultVoClass.lastIndexOf('.');
				if (lastIndexOf >= 0) {
					lastIndexOf += 1;
					// TODO 환경변수에 등록하여 관리할것
					methodPart.append("List<").append(resultVoClass.substring(lastIndexOf)).append("> ");
					addImport(vo, java.util.List.class);
				} else {
					methodPart.append(resultVoClass).append(" ");
				}

				addImport(vo, resultVoClass);
			} else {
				methodPart.append("void ");
			}

			methodPart.append(methodName).append(" ( ");
			// [시작]파라미터

			/* 정적파라미터 설정 */
			StringBuffer paramPart = new StringBuffer();
			paramPart.append("Map<String,Object> ").append(STRING_METHOD_PARAMETER_VARIABLE_NAME);
			methodPart.append(paramPart.toString());
			addImport(vo, java.util.Map.class);
			methodPart.append(" ) ");
			// [끝]파라미터

			// [시작] 예외처리
			if (exceptionClass != null) {
				methodPart.append("throws ").append(exceptionClass.getClass().getSimpleName());
			}
			// [끝] 예외처리

			// [시작] 메소드 body
			methodPart.append("\n\t{\n");
			String packageName = t.getParent().getPackageName();
			String className = t.getParent().getName();
			methodPart.append(applyedTabKeys(getApplicationCodeWrapper(packageName, className, methodName, codeBody), 2));
			// 결과 sql을 맵핑하는 로직
			methodPart.append(resultSetMappingBody(vo, t));

			methodPart.append("\t}\n");

		}

		vo.getMethodPart().append(methodPart.toString());
	}

	/**
	 * 결과 sql을 맵핑.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 29.
	 * @param vo
	 * @param t
	 */
	protected String resultSetMappingBody(FxDao vo, T t) {
		//		BaseResultMapper<T> baseResultMapper = baseResultMapper(vo, t);
		//		baseResultMapper.setStringBufferVarName(STRING_BUFFER_VARIABLE_NAME);
		//		baseResultMapper.setParameterMapVarName(STRING_METHOD_PARAMETER_VARIABLE_NAME);
		return daoBaseResultSetStatement(vo, t).convert();
	}

	protected BaseResultMapper<T> baseResultMapper(FxDao vo, T t) {
		BaseResultMapper<T> baseResultMapper = new BaseResultMapper<>(vo, t);
		baseResultMapper.setStringBufferVarName(STRING_BUFFER_VARIABLE_NAME);
		baseResultMapper.setParameterMapVarName(STRING_METHOD_PARAMETER_VARIABLE_NAME);
		return baseResultMapper;
	}

	protected DaoBaseResultSetStatement<BaseResultMapper<T>, T> daoBaseResultSetStatement(FxDao vo, T t) {
		BaseResultMapper<T> baseResultMapper = baseResultMapper(vo, t);
		return new DaoBaseResultSetStatement<BaseResultMapper<T>, T>(baseResultMapper, 2);
	}

	@Override
	public void work(FxDao vo, List<T> methods) {

		try {
			for (T method : methods) {
				extract(vo, method);
			}
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}

		// 기본적으로 import되는 항목
		addImport(vo, StringBuffer.class);
	}

	public String getApplicationCodeWrapper(String methodName, String sql) {
		return getApplicationCodeWrapper(null, null, methodName, sql);
	}

	/**
	 * 개발자 코드로 sql문을 감쌈.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 28.
	 * @param sql
	 * @return
	 */
	public String getApplicationCodeWrapper(String packageName, String className, String methodName, String sql) {
		String[] split = sql.split("\n");
		StringBuilder sb = new StringBuilder();
		sb.append(STRING_BUFFER_CLASS_TYPE).append(" ").append(STRING_BUFFER_VARIABLE_NAME).append(" = new ")
				.append(STRING_BUFFER_CLASS_TYPE).append("();\n");
		for (String str : split) {
			sb.append(STRING_BUFFER_VARIABLE_NAME).append(".append(\"").append(str).append("\\n").append("\");\n");
		}

		StringBuffer addpendPackage = new StringBuffer();
		addpendPackage.append("/**");
		if (packageName != null && !methodName.isEmpty())
			addpendPackage.append(packageName).append(".");
		if (className != null && !methodName.isEmpty())
			addpendPackage.append(className).append(".");
		if (methodName != null && !methodName.isEmpty())
			addpendPackage.append(methodName);
		addpendPackage.append("*/");

		sb.append(STRING_BUFFER_VARIABLE_NAME).append(".append(\"").append(addpendPackage.toString()).append("\\n").append("\");\n");

		return sb.toString();
	}

}
