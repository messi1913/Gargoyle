/**
 * KYJ
 * 2015. 10. 11.
 */
package kyj.Fx.dao.wizard.core;

import java.lang.reflect.Modifier;

import kyj.Fx.dao.wizard.core.model.vo.FxDao;

import com.kyj.fx.voeditor.core.model.meta.COLUMN;
import com.kyj.fx.voeditor.core.model.meta.FieldMeta;
import com.kyj.fx.voeditor.core.model.meta.NonEditable;

/**
 * 사용되지않을클래스
 *
 * @author KYJ
 *
 */
@Deprecated
public class BaseFxExtractDaoField<F extends FieldMeta> extends FxDaoCommons implements IExtractDaoField<F> {

	public void extract(FxDao vo, F t) throws Exception {
		int modifier = t.getModifier();
		Class<?> fieldType = t.getFieldType();
		String varName = t.getName();
		StringBuffer fieldPart = vo.getFieldPart();

		// Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE |
		// Modifier.STATIC | Modifier.FINAL | Modifier.TRANSIENT |
		// Modifier.VOLATILE;

		if ((Modifier.fieldModifiers() & 0x0) == 0) {

			if (t.isPrimarykey()) {
				fieldPart.append("\t");
				fieldPart.append("@PRIMARYKEY\n");
				addImport(vo, NonEditable.class);
			}

			String alias = t.getAlias();
			if (alias != null && !alias.isEmpty()) {
				fieldPart.append("\t");
				fieldPart.append("@COLUMN(\"").append(alias).append("\")\n");
				addImport(vo, COLUMN.class);
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

	public void work(FxDao vo, F... t) {
		if (t == null)
			return;
		for (F meta : t) {
			try {
				this.extract(vo, meta);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

}
