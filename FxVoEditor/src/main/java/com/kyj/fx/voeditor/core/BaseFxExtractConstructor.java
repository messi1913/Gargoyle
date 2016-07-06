/**
 * KYJ
 * 2015. 10. 11.
 */
package com.kyj.fx.voeditor.core;

import java.lang.reflect.Modifier;

import com.kyj.fx.voeditor.core.model.meta.ClassMeta;
import com.kyj.fx.voeditor.core.model.meta.FieldMeta;
import com.kyj.fx.voeditor.core.model.vo.FxVo;
import com.kyj.fx.voeditor.util.StringUtil;

/**
 * @author KYJ
 *
 */
public class BaseFxExtractConstructor extends FxVoCommons implements IExtractConstructor<FieldMeta> {

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.kyj.fx.voeditor.core.IExtractSetterGetter#extract(com.kyj.fx.voeditor
	 * .core.model.vo.FxVo, java.lang.Object) KYJ
	 */
	@Override
	public void extract(FxVo vo, FieldMeta t) throws Exception {

		//int modifier = t.getModifier();
		Class<?> typeClassType = t.getFieldType();
		Class<?> instanceType = t.getInstanceType();
		StringBuffer constructPart = vo.getConstructPart();

		if ((Modifier.constructorModifiers() & 0x0) == 0) {

			String name = StringUtil.getIndexUppercase(t.getName(), 0);
			String varName = StringUtil.getIndexLowercase(name, 0);
			String typeName = "";

			if (isJavafxProperty(typeClassType)) {
				Class<?> whatistypeClass = instanceType;
				if(whatistypeClass == null)
					whatistypeClass = typeClassType;

				typeName = whatistypeClass.getSimpleName();
				constructPart.append("\t\tthis.").append(varName).append(" = ").append("new ").append(typeName).append("();\n");
				addImport(vo, whatistypeClass);
			}

		} else {
			System.err.println("warnning.....");
		}

	}

	@Override
	public void work(FxVo vo, ClassMeta classMeta, FieldMeta... fieldMetas) {
		StringBuffer constructPart = vo.getConstructPart();

		constructPart.append("\tpublic ").append(classMeta.getName()).append("( ) \n");
		constructPart.append("\t{ \n");
		for (FieldMeta meta : fieldMetas) {
			try {
				this.extract(vo, meta);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		constructPart.append("\t} \n");

	}

}
