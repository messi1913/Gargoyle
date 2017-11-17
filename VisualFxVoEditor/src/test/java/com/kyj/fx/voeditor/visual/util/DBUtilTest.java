/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 2. 11.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;

/**
 * @author KYJ
 *
 */
public class DBUtilTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(DBUtilTest.class);

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

	@Test
	public void selectCursorTest() throws Exception {
		System.out.println("###################################################");
		{
			List<Map<String, Object>> selectCursor = DbUtil.selectCursor(DbUtil.getConnection(), "select * from tbm_sys_user", 0);
			selectCursor.stream().forEach(System.out::println);
		}

		System.out.println("###################################################");
		{
			List<Map<String, Object>> selectCursor = DbUtil.selectCursor(DbUtil.getConnection(), "select * from tbm_sys_user", 5);
			selectCursor.stream().forEach(System.out::println);
		}

		System.out.println("###################################################");
		{
			List<Map<String, Object>> selectCursor = DbUtil.selectCursor(DbUtil.getConnection(), "select * from tbm_sys_user", 10);
			selectCursor.stream().forEach(System.out::println);
		}
	}

	@Test
	public void pkPrintTest() throws Exception {

		{
			Connection connection = DbUtil.getConnection();
			ResultSet rs = connection.getMetaData().getPrimaryKeys(null, null, "tbm_sys_dao");

			while (rs.next()) {
				System.out.println(rs.getString(4));
			}

		}

		DbUtil.pks("tbm_sys_dao", rs -> {
			String string = "";
			try {

				string = rs.getString(4);

				System.out.println("row : " + rs.getRow() + " : " + string);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return string;

		});
	}

	@Test
	public void selectCursorTest1() throws Exception {

		String sql = "select * from tbm_sm_realtime_search";

		int startRow = 0;
		int limitRow = 1000;
		int length = 1000;

		for (int page = 0; page < 3; page++) {

			startRow = page * length;
			// limitRow = (page * length) * 2;
			Connection connection = DbUtil.getConnection();
			List<Map<String, Object>> selectCursor = DbUtil.selectCursor(connection, sql, startRow, limitRow);
			for (int i = 0; i < selectCursor.size(); i++) {
				System.out.printf("%d %s\n", i, selectCursor.get(i));
			}
			connection.close();
		}

	}

	@Test
	public void pksTest() throws Exception {
		{
			List<String> pks = DbUtil.pks("tbm_sm_realtime_search");
			List<String> columns = DbUtil.columns("tbm_sm_realtime_search");
			System.out.println(pks);
			System.out.println(columns);
		}
		{
			List<String> pks = DbUtil.pks("study.deparment");
			List<String> columns = DbUtil.columns("study.deparment");
			System.out.println(pks);
			System.out.println(columns);
		}

		Connection connection = DbUtil.getConnection();
		DatabaseMetaData metaData = connection.getMetaData();
		System.out.printf("catalog : %s \n", connection.getCatalog());
		System.out.printf("schema : %s \n", connection.getSchema());
		ResultSet rs = metaData.getPrimaryKeys(null, "study", "deparment");
		System.out.println("#case1");
		while (rs.next()) {
			System.out.println(rs.getObject(4));
		}
		System.out.println("#case2");
		// rs = metaData.getPrimaryKeys("study", null, "deparment");
		while (rs.next()) {
			System.out.println(rs.getObject(4));
		}
		System.out.println("#case3");
		rs = metaData.getPrimaryKeys(null, null, "study.deparment");
		while (rs.next()) {
			System.out.println(rs.getObject(4));
		}

		connection.close();
		// DbUtil.getConnection().getMetaData().getPrimaryKeys(catalog, schema,
		// table)

	}

	@Test
	public void executeProcedureTest() {

		// String callSql = "call DMI_Manu32.MatRec_GetLocations('Receiving')";
		Optional<Map<String, Object>> findFirst = DbUtil.getAvailableConnections().stream().filter(map -> {
			LOGGER.debug(map.toString());
			return "123".equals(map.get("alias"));
		}).findFirst();

		findFirst.ifPresent(map -> {

			try (Connection connection = DbUtil.getConnection(map)) {

				List<Map<String, Object>> apply = DbUtil.getProcedureColumns(connection, "SamsungDB", "dbo", "DMICustom_GetContainerDetailsWithParents_NT");

				apply.forEach(m -> LOGGER.debug(m.toString()));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});

	}

}
