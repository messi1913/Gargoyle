/********************************
 *	프로젝트 : kyj.Fx.dao.wizard
 *	패키지   : kyj.Fx.dao.wizard.core
 *	작성일   : 2015. 10. 29.
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.dao.wizard.core;

import java.util.StringTokenizer;

import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoColumnsDVO;
import kyj.Fx.dao.wizard.memory.DatabaseTypeMappingResourceLoader;

/**
 * @author KYJ
 *
 */
public class FxDaoResultSetConverter implements IResultSetConverter {

	@Override
	public String convert(String varName, String resultSetVarName, TbpSysDaoColumnsDVO col) {
		StringBuffer sb = new StringBuffer();
		String columnName = col.getColumnName();
		String columnType = col.getColumnType();

		sb.append(varName).append(".").append(setStatement(columnName)).append("(")
				.append(getStatement(resultSetVarName, columnType, columnName)).append(");");
		return sb.toString();
	}

	@Override
	public String getTypeTo(String columnType) {
		return DatabaseTypeMappingResourceLoader.getInstance().get(columnType);
	}

	private String setStatement(String dbColumnName) {
		String setter = "set" + getSetterStatement(dbColumnName);
		return setter;
	}

	private String getStatement(String resultSetVarName, String columnType, String dbColumnName) {
		return resultSetVarName + "." + "get" + getTypeTo(columnType) + "(\"" + dbColumnName + "\")";
	}

	private String getSetterStatement(String str) {
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
