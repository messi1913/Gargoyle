/********************************
 *	프로젝트 : FxVoEditor
 *	패키지   : com.kyj.fx.voeditor.util
 *	작성일   : 2015. 10. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.util;

import com.kyj.fx.voeditor.core.model.meta.ClassMeta;

/**
 * @author KYJ
 *
 */
public class EditorUtil {
	/**
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 16.
	 * @param className
	 * @param packageName
	 * @param extendsBaseClass
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static ClassMeta extractedClassMeta(String className, String packageName, String extendsBaseClass)
			throws ClassNotFoundException {
		ClassMeta classMeta = null;
		if (extendsBaseClass != null && !extendsBaseClass.isEmpty()) {
			try {
				Class<?> extendsBaseClazz = Class.forName(extendsBaseClass);
				classMeta = new ClassMeta(packageName, className, extendsBaseClazz);
			} catch (Exception e) {
				classMeta = new ClassMeta(packageName, className);
				classMeta.setExtendClassNameStr(extendsBaseClass);
			}

		} else {
			classMeta = new ClassMeta(packageName, className);
		}
		return classMeta;
	}

}
