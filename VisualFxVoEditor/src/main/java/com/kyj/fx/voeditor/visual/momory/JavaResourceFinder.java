/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.momory
 *	작성일   : 2016. 3. 20.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.momory;

import static org.apache.commons.lang.SystemUtils.JAVA_HOME;
import static org.apache.commons.lang.SystemUtils.JAVA_VERSION_TRIMMED;

/**
 * TODO 클래스 역할
 *
 * @author KYJ
 *
 */
public class JavaResourceFinder {

	public static final boolean IS_JAVA_1_7 = isJavaVersionMatch(JAVA_VERSION_TRIMMED, "1.7");
	public static final boolean IS_JAVA_1_8 = isJavaVersionMatch(JAVA_VERSION_TRIMMED, "1.8");

	/**
	 * <p>
	 * Decides if the Java version matches.
	 * </p>
	 * <p>
	 * This method is package private instead of private to support unit test
	 * invocation.
	 * </p>
	 *
	 * @param version
	 *            the actual Java version
	 * @param versionPrefix
	 *            the prefix for the expected Java version
	 * @return true if matches, or false if not or can't determine
	 */
	static boolean isJavaVersionMatch(String version, String versionPrefix) {
		if (version == null) {
			return false;
		}
		return version.startsWith(versionPrefix);
	}

	/**
	 * <p>
	 * Decides if the Java version matches.
	 * </p>
	 *
	 * @param versionPrefix
	 *            the prefix for the java version
	 * @return true if matches, or false if not or can't determine
	 */
	private static boolean getJavaVersionMatches(String versionPrefix) {
		return isJavaVersionMatch(JAVA_VERSION_TRIMMED, versionPrefix);
	}

	/***********************************************************************************/
	/* 이벤트 구현 */

	// 

	/***********************************************************************************/

	/***********************************************************************************/
	/* 일반API 구현 */

	public static void main(String[] args) {
		gagoyle_java_home();
		java_home();
	}

	public static String gagoyle_java_home_minimum() {

		return "";
	}

	public static String gagoyle_java_home() {
		if (IS_JAVA_1_8) {

		}
		return "";
	}

	public static String java_home() {

		System.out.println(IS_JAVA_1_7);
		System.out.println(getJavaVersionMatches("1.8"));
		System.out.println(JAVA_HOME);

		return "";
	}
	// 
	/***********************************************************************************/
}
