/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.words.spec.auto.msword.filemodel
 *	작성일   : 2016. 2. 15.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.words.spec.auto.msword.util;

import java.util.List;
import java.util.Map;

import com.kyj.fx.voeditor.visual.exceptions.ProgramSpecSourceNullException;
import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * TODO 구현
 * 
 * @author KYJ
 *
 */
public class DbOracleUtil {

	public static List<Map<String, Object>> select(String sql) throws Exception {
		return DbUtil.select(sql);
	}

	/**
	 * String 입력값으로부터 테이블명을 찾아본다. 글자내에 소문자가 포함되면 앞에 '_'를 붙인다.
	 * 
	 * @param sourceNm
	 * @return
	 * @throws ProgramSpecSourceNullException
	 */
	public static String getTableName(String sourceNm) {
		StringBuffer stringBuffer = new StringBuffer();
		char[] charArray = sourceNm.toCharArray();

		int length = charArray.length;
		stringBuffer.append(charArray[0]);
		for (int i = 1; i < length; i++) {
			stringBuffer.append(charArray[i]);
			if (i + 1 < length) {

				if (Character.isUpperCase(charArray[i + 1])) {
					stringBuffer.append('_');
				}
			}// end if

		}// end for

		return stringBuffer.toString().toUpperCase();
	}

	public static String getTableNameByBizName(String bizName) throws ProgramSpecSourceNullException {

		if (ValueUtil.isEmpty(bizName)) {
			throw new ProgramSpecSourceNullException("parameter is null");
		}
		StringBuffer stringBuffer = new StringBuffer();
		char[] charArray = bizName.substring(8, bizName.length() - 3).toCharArray();

		int length = charArray.length;
		for (int i = 0; i < length; i++) {
			stringBuffer.append(charArray[i]);
			if (i + 1 < length) {

				if (Character.isUpperCase(charArray[i + 1])) {
					stringBuffer.append('_');
				}
			}// end if

		}// end for

		return stringBuffer.toString().toUpperCase();
	}

}
