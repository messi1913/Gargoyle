/********************************
 *	프로젝트 : kyj.Fx.dao.wizard
 *	패키지   : kyj.Fx.dao.wizard.core.util
 *	작성일   : 2015. 10. 21.
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.dao.wizard.core.util;

import java.util.List;
import java.util.StringTokenizer;

/**
 * 다니아믹 쿼리를 생성한다.
 * 
 * @author KYJ
 *
 */
public class QuerygenUtil {

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 24.
	 * @param catalog
	 * @param schemaName
	 * @param tableName
	 * @param columns
	 * @return
	 */
	public static String queryjen(String catalog, String schemaName, String tableName, List<String> columns) {
		tableName = tableName.trim();
		StringBuffer buf = new StringBuffer();
		buf.append("SELECT \n");
		int cnt = 0;
		for (String str : columns) {
			buf.append(str + " ");
			if (cnt != columns.size() - 1) {
				buf.append(" , \n");
			}
			cnt++;
		}

		boolean isCatalogEmpty = ValueUtil.isEmpty(catalog);
		boolean isSchemaEmpty = ValueUtil.isEmpty(schemaName);

		String name = "";

		if (!isCatalogEmpty) {
			name = catalog.concat(".");
		}

		if (!isSchemaEmpty) {
			name = name.concat(schemaName).concat(".");
		}

		name = name.concat(tableName);

		buf.append("\n FROM " + name + " \n WHERE 1=1 \n");

		for (String str : columns) {

			String textMyEdit = getPrefixLowerTextMyEdit(str);
			buf.append(" #if($" + textMyEdit + ") \n");
			buf.append("AND " + str + " = :" + textMyEdit + "\n");
			buf.append(" #end \n");
		}
		return buf.toString();
	}

	/**
	 * Velocity 기반 SQL문을 작성한다.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 21.
	 * @param tableName
	 * @param columns
	 * @return
	 * @throws Exception
	 */
	public static String queryjen(String tableName, List<String> columns) {
		tableName = tableName.trim();
		StringBuffer buf = new StringBuffer();
		buf.append("SELECT \n");
		int cnt = 0;
		for (String str : columns) {
			buf.append(str + " ");
			if (cnt != columns.size() - 1) {
				buf.append(" , \n");
			}
			cnt++;
		}
		buf.append("\n FROM " + tableName + " \n WHERE 1=1 \n");

		for (String str : columns) {

			String textMyEdit = getPrefixLowerTextMyEdit(str);
			buf.append(" #if($" + textMyEdit + ") \n");
			buf.append("AND " + str + " = :" + textMyEdit + "\n");
			buf.append(" #end \n");
		}
		return buf.toString();
	}

	/**
	 * 문자열 패턴형식이 '_'가 들어가고 해당하는 문단 첫글자만 대문자로 바꾸지만.. 맨 처음 글자는 소문자
	 * 
	 * @param str
	 * @return
	 */
	private static String getPrefixLowerTextMyEdit(String str) {

		char[] charArray = getPrefixUpperTextMyEdit(str).toCharArray();

		String lowerCase = String.valueOf(charArray[0]).toLowerCase();
		charArray[0] = lowerCase.charAt(0);

		return String.valueOf(charArray);
	}

	/**
	 * 문자열 패턴형식이 '_'가 들어가고 해당하는 문단 첫글자만 대문자로 바꾸고 싶을경우사용
	 * 
	 * @param str
	 * @return
	 */
	private static String getPrefixUpperTextMyEdit(String str) {
		StringTokenizer stringTokenizer = new StringTokenizer(str, "_");
		String nextElement = null;
		char[] charArray = null;
		String temp = "";
		while (stringTokenizer.hasMoreElements()) {
			nextElement = (String) stringTokenizer.nextElement();
			charArray = nextElement.toCharArray();
			charArray[0] = Character.toUpperCase(charArray[0]);
			for (int i = 1; i < charArray.length; i++) {
				charArray[i] = Character.toLowerCase(charArray[i]);
			}
			temp += String.valueOf(charArray);
		}
		return temp;
	}

}
