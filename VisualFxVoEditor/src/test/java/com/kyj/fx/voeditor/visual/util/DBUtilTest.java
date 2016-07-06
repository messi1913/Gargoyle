/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 2. 11.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.Test;

import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;

/**
 * @author KYJ
 *
 */
public class DBUtilTest {

	/**
	 * Test method for
	 * {@link com.kyj.fx.voeditor.visual.util.DbUtil#select(java.lang.String)}.
	 *
	 * @throws Exception
	 */
	@Test
	public void testSelectString() throws Exception {

		String string = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.SQL_LIMIT_WRAPPER);

		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		String sql = "SELECT * FROM nrch.TBM_SM_REALTIME_SEARCH";
		paramMap.put("usersql", sql);
		paramMap.put("startRow", 10);
		paramMap.put("maxRow", 10);
		String velocityToText = ValueUtil.getVelocityToText(string, paramMap);
		System.out.println(velocityToText);

		int fetchCount = 10;

		//
		List<Map<String, Object>> select = DbUtil.select(DbUtil.getConnection(), velocityToText, fetchCount);
		select.forEach(System.out::println);

	}

	@Test
	public void dmlkeywordTest() {
		String[] dmlkeyword = DbUtil.dmlkeyword;
		Stream.of(dmlkeyword).forEach(System.out::println);
	}

}
