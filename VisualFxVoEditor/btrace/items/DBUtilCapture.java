/********************************
 *	프로젝트 : btrace
 *	패키지   : com.kyj.trace.btrace.btrace.external
 *	작성일   : 2016. 5. 25.
 *	작성자   : KYJ
 *******************************/
package com.kyj.trace.btrace.btrace.external;

import static com.sun.btrace.BTraceUtils.println;

import java.sql.Connection;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import com.sun.btrace.BTraceUtils;
import com.sun.btrace.annotations.BTrace;
import com.sun.btrace.annotations.Kind;
import com.sun.btrace.annotations.Location;
import com.sun.btrace.annotations.OnMethod;

/**
 * @author KYJ
 *
 */
@BTrace
public class DBUtilCapture {

	private static final String DS_CLASS = "com.kyj.fx.voeditor.visual.util.DbUtil";

	@OnMethod(clazz = DS_CLASS, method = "select")
	public static void onSelect(String sql) {

		println("beforeDoAction_enrty");
	}

	@OnMethod(clazz = DS_CLASS, method = "select")
	public static void onSelect(Connection con, final String sql) {
		println("beforeDoAction_enrty");
	}

	@OnMethod(clazz = DS_CLASS, method = "select")
	public static void onSelect(final String sql, Map<String, Object> paramMap, RowMapper<?> rowMapper) {

		BTraceUtils.println(sql);
		BTraceUtils.println(paramMap);
		BTraceUtils.println(rowMapper);
	}

	@OnMethod(clazz = DS_CLASS, method = "selectLimit", location = @Location(Kind.ENTRY))
	public static void beforedoAction(String sql) {

		println("beforedoAction");
	}

	// @OnMethod(clazz = DS_CLASS, method = "select", location =
	// @Location(Kind.RETURN))
	// public static void afterdoAction_return(@Return Object connection) {
	// // 메소드 내용 진입후
	// println("afterdoAction_return");
	// println(connection);
	// BTraceUtils.printFields(connection);
	// println("after doaction");
	//
	// }

}
