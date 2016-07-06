/**
 * KYJ
 * 2015. 10. 11.
 */
package com.kyj.fx.voeditor.core;

import java.lang.reflect.Modifier;

import com.kyj.fx.voeditor.core.model.meta.FieldMeta;
import com.kyj.fx.voeditor.core.model.vo.FxVo;
import com.kyj.fx.voeditor.util.StringUtil;

/**
 * @author KYJ
 *
 */
public class BaseFxExtractSetterGetter extends FxVoCommons implements IExtractSetterGetter<FieldMeta> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.kyj.fx.voeditor.core.IExtractSetterGetter#extract(com.kyj.fx.voeditor
	 * .core.model.vo.FxVo, java.lang.Object) KYJ
	 */
	@Override
	public void extract(FxVo vo, FieldMeta t) throws Exception {

		int modifier = t.getModifier();
		Class<?> fieldType = t.getFieldType();
		StringBuffer methodPart = vo.getMethodPart();

		// Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE |
		// Modifier.ABSTRACT | Modifier.STATIC | Modifier.FINAL |
		// Modifier.SYNCHRONIZED | Modifier.NATIVE | Modifier.STRICT;

		if ((Modifier.methodModifiers() & 0x0) == 0) {

			String name = StringUtil.getIndexUppercase(t.getName(), 0);
			String varName = StringUtil.getIndexLowercase(name, 0);
			String typeName = "";
			boolean isJavaFxProperty = false;
			if (isJavafxProperty(fieldType)) {
				isJavaFxProperty = true;
				typeName = convertFxPropertyToPrimitive(fieldType);
			} else {
				isJavaFxProperty = false;
				typeName = fieldType.getSimpleName();
			}

			preffix(modifier, methodPart);
			setter(fieldType, methodPart, name, varName, typeName);

			preffix(modifier, methodPart);
			getter(fieldType, methodPart, name, varName, typeName);

			if (isJavaFxProperty) {
				preffix(modifier, methodPart);
				property(fieldType, methodPart, name, varName, typeName);
			}

			addImport(vo, fieldType);

		} else {
			System.err.println("warnning.....");
		}

	}

	private void preffix(int modifier, StringBuffer methodPart) {
		methodPart.append("\n\t");
		methodPart.append("public");
	}

	private void property(Class<?> fieldType, StringBuffer methodPart, String name, String varName, String typeName) {
		methodPart.append(" ").append(fieldType.getSimpleName());
		methodPart.append(" ").append(varName).append("Property(  ) \n");
		methodPart.append("	{\n");
		methodPart.append("		").append("return ").append(varName).append(";\n");
		methodPart.append("	}\n");
	}

	private void setter(Class<?> fieldType, StringBuffer methodPart, String name, String varName, String typeName) {
		methodPart.append(" void");
		methodPart.append(" set");

		methodPart.append(name).append("( ").append(typeName).append(" ");
		methodPart.append(varName).append(" ) \n");
		methodPart.append("	{\n");

		if (isJavafxProperty(fieldType)) {
			methodPart.append("		").append("this.").append(varName).append(".set(").append(varName).append(");\n");
		} else {
			methodPart.append("		").append("this.").append(varName).append("=").append(varName).append(";\n");
		}
		methodPart.append("	}\n");
	}

	private void getter(Class<?> fieldType, StringBuffer methodPart, String name, String varName, String typeName) {
		methodPart.append(" ").append(typeName);

		if (fieldType == Boolean.class || fieldType == boolean.class) {
			methodPart.append(" is");
		} else {
			methodPart.append(" get");
		}

		methodPart.append(name).append("()\n");
		methodPart.append("	{\n");

		if (isJavafxProperty(fieldType)) {
			methodPart.append("		").append("return ").append(varName).append(".get();\n");
		} else {
			methodPart.append("		").append("return ").append(varName).append(";\n");
		}
		methodPart.append("	}\n");
	}

	@Override
	public void work(FxVo vo, FieldMeta... fieldMetas) {
		for (FieldMeta meta : fieldMetas) {
			try {
				this.extract(vo, meta);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
