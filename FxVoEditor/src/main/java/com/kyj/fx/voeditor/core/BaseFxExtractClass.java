/**
 * KYJ
 * 2015. 10. 11.
 */
package com.kyj.fx.voeditor.core;

import java.lang.reflect.Modifier;

import com.kyj.fx.voeditor.core.model.meta.ClassMeta;
import com.kyj.fx.voeditor.core.model.vo.FxVo;

/**
 * @author KYJ
 *
 */
public class BaseFxExtractClass extends FxVoCommons implements IExtractClass<ClassMeta> {

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.kyj.fx.voeditor.core.IExtractModel#extract(com.kyj.fx.voeditor.core
	 * .model.vo.FxVo, java.lang.Object) KYJ
	 */
	@Override
	public void extract(FxVo vo, ClassMeta t) throws Exception {

		StringBuffer classPart = vo.getClassPart();

		String className = t.getName();
		int modifier = t.getModifier();
		Class<?> extendClassName = t.getExtendClassName();
		String extendClassNameStr = t.getExtendClassNameStr();
		Class<?>[] interfaceNames = t.getInterfaceNames();

		if ((Modifier.classModifiers() & 0x0) == 0) {
			if (Modifier.isPrivate(modifier)) {
				classPart.append("private ");
			} else if (Modifier.isPublic(modifier)) {
				classPart.append("public ");
			} else {
				classPart.append("protected ");
			}

			classPart.append("class ").append(className).append(" ");

			if (extendClassName != null) {

				boolean addImport = addImport(vo, extendClassName);

				if (addImport) {
					classPart.append("extends ").append(extendClassName.getSimpleName());
				}
			} else if (extendClassNameStr != null) {
				boolean addImport = addImport(vo, extendClassNameStr);
				if (addImport) {
					int lastIndexOf = extendClassNameStr.lastIndexOf('.');
					if(lastIndexOf >=0)
					{
						String substring = extendClassNameStr.substring(lastIndexOf + 1);
						classPart.append("extends ").append(substring);
					}
				}
			}

			if (interfaceNames != null) {
				int SIZE = interfaceNames.length;
				if (SIZE != 0) {
					classPart.append(" implements ");
				}
				for (int i = 0; i < SIZE; i++) {
					Class<?> interfaceClass = interfaceNames[i];
					boolean addImport = addImport(vo, interfaceClass);

					if (addImport) {
						classPart.append(interfaceClass.getSimpleName()).append(" ,");

						// TODO 인터페이스가존재하는경우 오버라이드 함수 추가
					}
				}
				classPart.setLength(classPart.length() - 1);
			}
		} else {
			throw new Exception("Class Name is empty...");
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.kyj.fx.voeditor.core.IExtractClass#work(com.kyj.fx.voeditor.core.
	 * model.vo.FxVo, java.lang.Object) KYJ
	 */
	@Override
	public void work(FxVo vo, ClassMeta t) {
		try {
			extract(vo, t);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
