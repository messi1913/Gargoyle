/**
 * KYJ
 * 2015. 10. 12.
 */
package com.kyj.fx.voeditor.util;



/**
 * @author KYJ
 *
 */
public class StringUtil {
	public enum IndexCaseTypes {
		UPPERCASE, LOWERCASE
	}

	/**
	 * str 문자열에서 index에 해당하는 부분만 대문자, 혹은 소문자로 고친다. (IndexCaseTypes에 따른 설정)
	 * 
	 * @param str
	 *            target
	 * @param index
	 *            character index
	 * @param type
	 *            lower or upper
	 * @return
	 */
	public static String getIndexcase(String str, int index, IndexCaseTypes type) {
		StringBuffer sb = new StringBuffer();

		// DEFAULT UPPERCASE
		char indexChar = Character.toUpperCase(str.charAt(index));
		if (type == IndexCaseTypes.LOWERCASE) {
			indexChar = Character.toLowerCase(str.charAt(index));
		}

		switch (index) {
		case 0:
			sb.append(indexChar).append(str.substring(index + 1));
			break;
		default:
			sb.append(str.substring(0, index)).append(indexChar).append(str.substring(index + 1));
			break;
		}
		return sb.toString();
	}

	/**
	 * str 문자열에서 index에 해당하는 부분만 대문자로 고친다.
	 * 
	 * @param str
	 * @param index
	 * @return
	 */
	public static String getIndexUppercase(String str, int index) {
		return getIndexcase(str, index, IndexCaseTypes.UPPERCASE);
	}

	/**
	 * str 문자열에서 index에 해당하는 부분만 소문자로 고친다.
	 * 
	 * @param str
	 * @param index
	 * @return
	 */
	public static String getIndexLowercase(String str, int index) {
		return getIndexcase(str, index, IndexCaseTypes.LOWERCASE);
	}
}
