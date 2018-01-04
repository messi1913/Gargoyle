/**
 * KYJ
 * 2015. 10. 11.
 */
package com.kyj.fx.voeditor.core;

import java.lang.reflect.Modifier;

import com.kyj.fx.voeditor.core.memory.VoEditorProperties;
import com.kyj.fx.voeditor.core.model.meta.FieldMeta;
import com.kyj.fx.voeditor.core.model.vo.FxVo;
import com.kyj.fx.voeditor.util.ReflectionUtil;

/**
 * @author KYJ
 *
 */
public class BaseFxExtractField extends FxVoCommons implements IExtractField<FieldMeta> {

	@Override
	public void extract(FxVo vo, FieldMeta t) throws Exception {
		int modifier = t.getModifier();
		Class<?> fieldType = t.getFieldType();
		String varName = t.getName();
		StringBuffer fieldPart = vo.getFieldPart();

		// Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE |
		// Modifier.STATIC | Modifier.FINAL | Modifier.TRANSIENT |
		// Modifier.VOLATILE;

		if ((Modifier.fieldModifiers() & 0x0) == 0) {

			if (t.isPrimarykey()) {

				String className = VoEditorProperties.getInstance().get(VoEditorProperties.VOEDITOR_ANNOTATION_NONEEDITABLE_CLASS, "");
				if (className != null && !className.isEmpty()) {
					String simpleName = ReflectionUtil.toSimpleName(className);
					fieldPart.append("\t");
					fieldPart.append("@").append(simpleName).append("\n");
					addImport(vo, /* NonEditable.class */ className);
				}

			}

			String alias = t.getAlias();
			if (alias != null && !alias.isEmpty()) {

				String className = VoEditorProperties.getInstance().get(VoEditorProperties.VOEDITOR_ANNOTATION_COLUMN_CLASS, "");
				if (className != null && !className.isEmpty()) {
					String simpleName = ReflectionUtil.toSimpleName(className);
					fieldPart.append("\t");
					fieldPart.append("@").append(simpleName).append("(\"").append(alias).append("\")\n");
					addImport(vo, /* COLUMN.class */className);
				}

			}
			
			//18.01.04 주석 추가.
			String desc = t.getDesc();
			if(desc!=null && !desc.isEmpty())
			{
				fieldPart.append("\t");
				fieldPart.append("/**");
				fieldPart.append("\t");
				fieldPart.append("\n");
				fieldPart.append("\t");
				fieldPart.append(desc);
				fieldPart.append("\n");
				fieldPart.append("\t");
				fieldPart.append("*/");
				fieldPart.append("\n");
			}
			
			fieldPart.append("\t");
			if (Modifier.isPrivate(modifier)) {
				fieldPart.append("private ");
			} else if (Modifier.isPublic(modifier)) {
				fieldPart.append("public ");
			} else {
				fieldPart.append("protected ");
			}

			String simpleName = fieldType.getSimpleName();

			fieldPart.append(simpleName).append(" ").append(varName).append("; \n");

			addImport(vo, fieldType);

		} else {
			System.err.println("warnning.....");
		}

	}

	@Override
	public void work(FxVo vo, FieldMeta... fieldMetas) {
		if (fieldMetas == null)
			return;
		for (FieldMeta meta : fieldMetas) {
			try {
				this.extract(vo, meta);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

}
