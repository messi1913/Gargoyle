/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2017. 6. 13.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Arrays;
import java.util.prefs.BackingStoreException;

import com.kyj.fx.voeditor.visual.momory.ResourceLoader;

/**
 * @author KYJ
 *
 */
public class PogstgreUtil {

	/**
	 * pg_dump.exe 파일경로를 리턴
	 * 
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 13. 
	 * @return
	 */
	public static String getPgdumpLocation() {

		String location = ResourceLoader.getInstance().get(ResourceLoader.POSTGRE_PGADMIN_BASE_DIR);
		if (ValueUtil.isEmpty(location)) {
			location = pgadminPreferencesVal();
			if (ValueUtil.isNotEmpty(location)) {
				File file = new File(location, "pg_dump.exe");
				if (file.exists())
					return file.getAbsolutePath();
			}

		} else {
			File file = new File(location);
			if (file.exists())
				return file.getAbsolutePath();
		}

		return "";

	}

	/**
	 * pgadmin이 설치된 경우 레지스트리에서 찾음.
	 * 
	 * 윈도우 기반 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 13. 
	 * @return
	 * @throws BackingStoreException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public static String pgadminPreferencesVal() {
		String readString = "";
		try {
			readString = WinRegistry.readString(WinRegistry.HKEY_CURRENT_USER, //HKEY
					"SOFTWARE\\pgAdmin III\\", "PostgreSQLPath");
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
		}
		return readString;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 13. 
	 * @param con
	 * @param schemaName
	 * @param tableName
	 * @return
	 * @throws FileNotFoundException
	 *    pg_dump.exe file not found
	 * @throws Exception
	 */
	public static String dumpTable(Connection con, String schemaName, String tableName) throws FileNotFoundException, Exception {
		DatabaseMetaData metaData = con.getMetaData();
		String jdbcUrl = metaData.getURL();
		String userName = metaData.getUserName();
		return dumpTable(jdbcUrl, userName, schemaName, tableName);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 13. 
	 * @param jdbcUrl
	 * 		jdbc URL ex) jdbc://ip:port/dbName
	 * @param userName
	 * 		
	 * @param schemaName
	 * @param tableName
	 * @return
	 * @throws FileNotFoundException
	 * 		pg_dump.exe file not found
	 * @throws Exception
	 */
	public static String dumpTable(String jdbcUrl, String userName, String schemaName, String tableName)
			throws FileNotFoundException, Exception {
		String replace = jdbcUrl.replace("jdbc:postgresql://", "");
		String[] split = replace.split(":");
		String ip = split[0];
		String[] split2 = split[1].split("/");
		String port = split2[0];
		String dbName = split2[1];
		return dumpTable(ip, port, dbName, userName, schemaName, tableName);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 13. 
	 * @param hostIp
	 * @param hostPort
	 * @param dbName
	 * @param userName
	 * @param schemaName
	 * @param tableName
	 * @return
	 * @throws FileNotFoundException
	 * 		pg_dump.exe file not found
	 * @throws Exception
	 */
	public static String dumpTable(String hostIp, String hostPort, String dbName, String userName, String schemaName, String tableName)
			throws FileNotFoundException, Exception {

		String path = getPgdumpLocation();

		if (ValueUtil.isEmpty(path))
			throw new FileNotFoundException("pg_dump.exe does not exists.");
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		String argUserName = "--username=" + userName;
		String argHost = "--host=" + hostIp;
		String argPort = "--port=" + hostPort;
		String argNoPasswd = "--no-password";
		String argDbName = "--dbname=" + dbName;

		String argSchema = schemaName + "." + tableName;
		if (ValueUtil.isEmpty(schemaName))
			argSchema = tableName;

		RuntimeClassUtil.exe(Arrays.asList(path, argUserName, argHost, argPort, argNoPasswd, argDbName, "-t", argSchema, "--schema-only"),
				out, out);

		return out.toString("UTF-8");
	}

}
