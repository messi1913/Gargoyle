/********************************
 *	프로젝트 : btrace
 *	패키지   : com.kyj.trace.btrace.btrace.external.samples
 *	작성일   : 2015. 12. 29.
 *	작성자   : KYJ
 *******************************/
package com.kyj.trace.btrace.btrace.external.samples;

import static com.sun.btrace.BTraceUtils.printFields;
import static com.sun.btrace.BTraceUtils.println;

import java.sql.Connection;

import com.sun.btrace.annotations.BTrace;
import com.sun.btrace.annotations.Kind;
import com.sun.btrace.annotations.Location;
import com.sun.btrace.annotations.OnMethod;
import com.sun.btrace.annotations.Return;

/**
 * 내 프로그램내에서 커넥션을 가져오는 부분만 로깅처리. 아래 테스트프로그램을 통해 Connection을 구현한 다른 클래스를 찾아낼 수
 * 있었음.
 * 
 * @author KYJ
 *
 */
@BTrace
public class MyTestConnectionManager {

	private static final String DS_CLASS = "com.kyj.fx.voeditor.visual.util.ConnectionManager";

	/**
	 * 호출하길 원하는 메인함수정의
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 30.
	 */
	@OnMethod(clazz = DS_CLASS, method = "getConnection", location = @Location(Kind.RETURN))
	public static void onGetDataSource(@Return Connection connection) {
		println("################");
		printFields(connection);
	}

	@OnMethod(clazz = DS_CLASS, type = "void (java.lang.String, java.lang.String, java.lang.String)", method = "getConnection", location = @Location(Kind.RETURN))
	public static void onGetDataSource2(@Return Connection connection, String url, String id, String pass) {
		println("url  :" + url + "\nid : " + id + "\npass : " + pass);
		printFields(connection);
	}

	@OnMethod(clazz = DS_CLASS, type = "void (java.lang.String, java.lang.String, java.lang.String, java.lang.String)", method = "getConnection", location = @Location(Kind.RETURN))
	public static void onGetDataSource3(@Return Connection connection, String driver, String url, String id, String pass) {
		println("driver : " + driver + "\nurl : " + url + "\nid : " + id + "\npass : " + pass);
		printFields(connection);
	}

}
