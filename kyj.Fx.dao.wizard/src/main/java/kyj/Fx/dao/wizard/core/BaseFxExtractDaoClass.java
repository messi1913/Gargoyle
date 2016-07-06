/********************************
 *	프로젝트 : kyj.Fx.dao.wizard
 *	패키지   : kyj.Fx.dao.wizard
 *	작성일   : 2015. 10. 28.
 *	프로젝트 : SOS 미어캣 프로젝트
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.dao.wizard.core;

import java.lang.reflect.Modifier;

import kyj.Fx.dao.wizard.core.model.vo.FxDao;

import com.kyj.fx.voeditor.core.model.meta.ClassMeta;

/**
 * @author KYJ
 *
 */
public class BaseFxExtractDaoClass extends FxDaoCommons implements IExtractDaoClass<ClassMeta> {

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.kyj.fx.voeditor.core.IExtractModel#extract(com.kyj.fx.voeditor.core
	 * .model.vo.FxVo, java.lang.Object) KYJ
	 */
	@Override
	public void extract(FxDao vo, ClassMeta t) throws Exception {

		StringBuffer classPart = new StringBuffer();// vo.getClassPart();

		String className = t.getName();
		int modifier = t.getModifier();
		Class<?> extendClassName = t.getExtendClassName();
		Class<?>[] interfaceNames = t.getInterfaceNames();

		String desc = t.getDesc();

		/* [시작] 메소드 주석 */
		if (desc != null && !desc.isEmpty()) {
			classPart.append("/**\n");
			classPart.append(applyedTabKeys(desc, 0, () -> "*"));
			classPart.append("*/\n");
		}

		/* [끝] 메소드 주석 */

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
			} else {
				String extendClassNameStr = t.getExtendClassNameStr();
				if (extendClassNameStr != null && !extendClassNameStr.isEmpty()) {
					addImport(vo, extendClassNameStr);
					String type = getType(extendClassNameStr);
					classPart.append("extends ").append(type);
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
		
		vo.setClassPart(classPart);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.kyj.fx.voeditor.core.IExtractClass#work(com.kyj.fx.voeditor.core.
	 * model.vo.FxVo, java.lang.Object) KYJ
	 */
	@Override
	public int work(FxDao vo, ClassMeta t) {
		try {
			extract(vo, t);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 1;
	}
}
