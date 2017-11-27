/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.dbtree.mssql
 *	작성일   : 2017. 11. 27.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.dbtree.mssql;

import java.util.HashMap;
import java.util.Map;

import org.apache.tomcat.jdbc.pool.DataSource;

import com.kyj.fx.voeditor.visual.component.sql.functions.ConnectionSupplier;
import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * @author KYJ
 *
 */
public class MSSQLProcedureReader {

	private DataSource dataSource;

	/**
	 * @param supplier
	 *            this param returns a connection
	 * @throws Exception
	 */
	public MSSQLProcedureReader(ConnectionSupplier supplier) throws Exception {
		dataSource = DbUtil.getDataSource(supplier.getDriver(), supplier.getUrl(), supplier.getUsername(), supplier.getPassword());
	}

	/**
	 * mssql에서 프로시저 내용을 읽어온다. <br/>
	 * 
	 * this function returns the contents of the MSSQL procedure. <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 27.
	 * @param paramMap
	 *            <ol>
	 *            <li>key : catalog // select caltalog</li>
	 *            <li>key :procedureName // procedure name</li>
	 *            </ol>
	 * @return
	 * @throws Exception
	 */
	public String readProcedure(Map<String, Object> paramMap) throws Exception {

		StringBuffer sb = new StringBuffer();

		sb.append("#if($catalog)\n");
		sb.append("use $catalog\n");
		sb.append("#end\n");
		sb.append("\n");
		sb.append("SELECT definition as value\n");
		sb.append("    FROM sys.sql_modules \n");
		sb.append("where object_id = object_id(:procedureName)\n");
		sb.append("/**com.kyj.fx.bioutils.sm.database.procd.app.SystemFunctions.readProcedure*/\n");

		String sql = ValueUtil.getVelocityToText(sb.toString(), paramMap);

		return DbUtil.selectScala(dataSource, sql, paramMap);

	}

	public String readProcedure(String catalog, String procedureName) throws Exception {
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("catalog", catalog);
		param.put("procedureName", procedureName);
		return readProcedure(param);
	}

}
