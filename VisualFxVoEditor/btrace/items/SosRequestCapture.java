/********************************
 *	프로젝트 : btrace
 *	패키지   : com.kyj.trace.btrace.btrace.external
 *	작성일   : 2016. 5. 25.
 *	작성자   : KYJ
 *******************************/
package com.kyj.trace.btrace.btrace.external;

import static com.sun.btrace.BTraceUtils.println;

import java.lang.reflect.Field;

import com.sun.btrace.BTraceUtils;
import com.sun.btrace.annotations.BTrace;
import com.sun.btrace.annotations.Kind;
import com.sun.btrace.annotations.Location;
import com.sun.btrace.annotations.OnMethod;
import com.sun.btrace.annotations.Return;
import com.sun.btrace.annotations.Self;

/**
 * @author KYJ
 *
 */
@BTrace
public class SosRequestCapture {

	private static final String DS_CLASS = "com.samsung.sds.sos.client.core.screen.AbstractScreen";
	private static final String DS_CLASS_HTTP_CLIENT = "com.samsung.sds.sos.context.http.HttpClientManager";

	@OnMethod(clazz = DS_CLASS, method = "doRequest", location = @Location(Kind.ENTRY) )
	public static void beforeDoRequest_entry(@Self Object obj) {
		println("beforeDoRequest");
		println(obj);
		println("Print Fields");
		BTraceUtils.printFields(obj);
		BTraceUtils.printFields(obj, true);
		println("Field");
		Class<?> classOf = BTraceUtils.classOf(obj);
//		BTraceUtils.print(classOf);
		Class<?> superclass = BTraceUtils.getSuperclass(classOf);

		println("AbstractMain ::: #####");
		BTraceUtils.println(superclass);
		Class<?> superclass2 = BTraceUtils.getSuperclass(superclass);
		println("#####");
		println("AbstractScreen ::: #####");
		BTraceUtils.println(superclass2);
		println("#####1");
		Field field = BTraceUtils.field(superclass2, "abstractSVO");
		println("#####2");
		Object object = BTraceUtils.get(field, obj);

		println("#####3");
		println("AbstractSVO ::: #####");
		BTraceUtils.print(object);
		BTraceUtils.printFields(object);
		String xml = BTraceUtils.toXML(object);
		BTraceUtils.println(xml);

	}

	@OnMethod(clazz = DS_CLASS, method = "doRequest")
	public static void beforeDoRequest_() {
		println("beforeRequest?");
	}

	@OnMethod(clazz = DS_CLASS, method = "doRequest", location = @Location(Kind.RETURN) )
	public static void afterDoRequest__return() {
		println("afterDoRequest__return");
	}

	// @OnMethod(clazz = DS_CLASS_HTTP_CLIENT, method = "doAction")
	// public static void beforedoAction(@Self Object obj) {
	// println("before doaction");
	// println(obj);
	// println("before doaction2");
	// }

	@OnMethod(clazz = DS_CLASS_HTTP_CLIENT, method = "doAction", location = @Location(Kind.ENTRY) )
	public static void beforeDoAction_enrty() {
		// 메소드내용 진입
		println("beforeDoAction_enrty");
	}

	@OnMethod(clazz = DS_CLASS_HTTP_CLIENT, method = "doAction")
	public static void beforedoAction() {
		// 메소드 내용 진입전
		println("beforedoAction");
	}

	@OnMethod(clazz = DS_CLASS_HTTP_CLIENT, method = "doAction", location = @Location(Kind.RETURN) )
	public static void afterdoAction_return(@Return Object connection) {
		// 메소드 내용 진입후
		println("afterdoAction_return");
		println(connection);
		BTraceUtils.printFields(connection);
		println("after doaction");

	}

}
