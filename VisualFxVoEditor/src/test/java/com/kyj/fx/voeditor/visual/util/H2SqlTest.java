/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 4. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;

/***************************
 *
 * @author KYJ
 *
 ***************************/
public class H2SqlTest {

	/********************************
	 * 작성일 : 2016. 4. 16. 작성자 : KYJ
	 *
	 * h2 접속 테스트
	 *
	 * @throws Exception
	 ********************************/
	@Test
	public void h2ConnectionTest() throws Exception {
		String connectionurl = "jdbc:h2:~/.jubula/database/embedded;MVCC=TRUE;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE";
		String driver = "org.h2.Driver";
		String id = "sa";
		String pass = "";

		try (Connection connection = DbUtil.getConnection(driver, connectionurl, id, pass)) {
			connection.close();
		}

	}

	@Test
	public void h2PingSql() throws Exception {
		String connectionurl = "jdbc:h2:~/.jubula/database/embedded;MVCC=TRUE;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE";
		String driver = "org.h2.Driver";
		String id = "sa";
		String pass = "";

		try (Connection connection = DbUtil.getConnection(driver, connectionurl, id, pass)) {
			DbUtil.select(connection, "select 1");
		}

	}

	@Test
	public void h2ColumnsSql() throws Exception {
		String connectionurl = "jdbc:h2:~/.jubula/database/embedded;MVCC=TRUE;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE";
		String driver = "org.h2.Driver";
		String id = "sa";
		String pass = "";

		try (Connection connection = DbUtil.getConnection(driver, connectionurl, id, pass)) {

			
			
			// mysql과 호환되는 지 확인하기위해 driver를 mysql로 ..
			String sql = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.SQL_COLUMN, "org.postgresql.Driver");

			HashMap<String, Object> hashMap = new HashMap<String,Object>();
			hashMap.put("tableName", "AUT");
			sql = ValueUtil.getVelocityToText(sql, hashMap, true);
			System.out.println(sql);
			List<Map<String, Object>> select = DbUtil.select(connection, sql);

			System.out.println(select);
		}

	}

}
