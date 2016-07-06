/**
 * KYJ
 * 2015. 10. 11.
 */
package com.kyj.fx.voeditor.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javafx.beans.property.Property;

import com.kyj.fx.voeditor.core.model.vo.FxVo;
import com.kyj.fx.voeditor.util.ReflectionUtil;

/**
 * @author KYJ
 *
 */
public class FxVoCommons {

	public boolean isPrimitiveType(Class<?> clazz) {
		return ReflectionUtil.isPrimitiveType(clazz);
	}

	/**
	 * 기본형이 아닌 타입인경우 import항목을 추가한다.
	 *
	 * @Date 2015. 10. 11.
	 * @param vo
	 * @param clazz
	 * @return
	 * @User KYJ
	 */
	public boolean addImport(FxVo vo, Class<?> clazz) {
		boolean flag = false;
		if (clazz == null)
			return flag;

		if (!isPrimitiveType(clazz)) {
			StringBuffer importPart = vo.getImportPart();

			// import문이 중복으로 들어가는것을 방지함.
			if (importPart.indexOf(clazz.getName()) < 0) {
				importPart.append("import ").append(clazz.getName()).append(";\n");
			}

			flag = true;
		}
		return flag;
	}

	/**
	 * 기본형이 아닌 타입인경우 import항목을 추가한다.
	 *
	 * @Date 2015. 10. 11.
	 * @param vo
	 * @param clazz
	 * @return
	 * @User KYJ
	 */
	public boolean addImport(FxVo vo, String clazz) {
		boolean flag = false;
		if (clazz == null)
			return flag;

		boolean isPrimitive = false;
		try {
			Class<?> forName = Class.forName(clazz);
			isPrimitive = isPrimitiveType(forName);
		} catch (Exception e) {
		}

		if (!isPrimitive) {
			StringBuffer importPart = vo.getImportPart();

			// import문이 중복으로 들어가는것을 방지함.
			if (importPart.indexOf(clazz) < 0) {
				importPart.append("import ").append(clazz).append(";\n");
			}
			flag = true;
		}
		return flag;
	}

	public boolean isJavafxProperty(Class<?> fieldType) {
		return Property.class.isAssignableFrom(fieldType);
	}

	public String convertFxPropertyToPrimitive(Class<?> fieldType) throws Exception {
		String realType = "";

		if (fieldType != null) {
			Type[] genericInterfaces = fieldType.getGenericInterfaces();
			for (Type type : genericInterfaces) {

				if (type.getTypeName().startsWith("javafx.beans.property.Property")) {
					if (type instanceof ParameterizedType) {
						ParameterizedType imp = (ParameterizedType) type;
						Type[] actualTypeArguments = imp.getActualTypeArguments();
						// Type realtype = actualTypeArguments[0];
						Class<?> realtype = (Class<?>) actualTypeArguments[0];
						// String typeName = realtype.getTypeName();
						realType = realtype.getSimpleName();
						break;
					}
				}
			}
		}

		return realType;
	}
}
