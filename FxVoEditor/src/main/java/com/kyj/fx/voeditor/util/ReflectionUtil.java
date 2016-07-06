/**
 * KYJ
 * 2015. 10. 11.
 */
package com.kyj.fx.voeditor.util;

/**
 * @author KYJ
 *
 */
public class ReflectionUtil {

	/**
	 * 기본형 타입인지 확인 java.lang 패키지도 기본형으로 포함함.
	 *
	 * @Date 2015. 10. 11.
	 * @param clazz
	 * @return
	 * @User KYJ
	 */
	public static boolean isPrimitiveType(Class<?> clazz) {
		if (clazz != null && (clazz.isPrimitive() || clazz.getName().startsWith("java.lang")))
			return true;
		return false;
	}

	/**
	 * 패키지명이 포함된 클래스명 텍스트에서 클래스명반 리턴
	 *
	 * @param classPackageName
	 * @return
	 */
	public static String toSimpleName(String classPackageName) {

		int lastIndexOf = classPackageName.lastIndexOf('.');
		if (lastIndexOf >= 0)
			return classPackageName.substring(lastIndexOf + 1);
		return "";
	}
}
