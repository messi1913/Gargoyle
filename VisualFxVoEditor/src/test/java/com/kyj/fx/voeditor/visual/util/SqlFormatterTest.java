/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 4. 28.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import org.junit.Test;

/**
 * @author KYJ
 *
 */
public class SqlFormatterTest {

	@Test
	public void simple() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT\n");
		sb.append("    * \n");
		sb.append("FROM\n");
		sb.append("    TBM_SM_USER \n");
		sb.append("     \n");
		sb.append("UNION \n");
		sb.append(" ALL SELECT\n");
		sb.append("    * \n");
		sb.append("FROM\n");
		sb.append("    TBM_SM_USER\n");

		OracleSqlFormatter formatter = new OracleSqlFormatter();
		String format = formatter.format(sb.toString());
		System.out.println(format);
	}
}
