package com.kyj.external.school;

import org.apache.http.HttpEntity;

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

	private static final String DS_CLASS_HTTP_CLIENT = "com.samsung.sds.sos.context.http.HttpClientManager";

	// @OnMethod(clazz = DS_CLASS_HTTP_CLIENT, method = "doAction", location =
	// @Location(Kind.ENTRY) )
	// public static void afterdoAction_return() {
	//
	// BTraceUtils.println("entry time : " +
	// BTraceUtils.currentThreadCpuTime());
	// }

	@OnMethod(clazz = DS_CLASS_HTTP_CLIENT, method = "doAction", location = @Location(Kind.RETURN) )
	public static void afterdoAction_return(@Return Object param) {
		BTraceUtils.printFields(param);
	}

	@OnMethod(clazz = "org.apache.http.client.methods.HttpPost", method = "addHeader")
	public static void addHeader_self(@Self Object param) {
		BTraceUtils.printFields(param);
	}

	@OnMethod(clazz = "org.apache.http.client.methods.HttpEntityEnclosingRequestBase", method = "setEntity")
	public static void setEntity_entry(HttpEntity param) {
		BTraceUtils.printFields(param);
	}

	@OnMethod(clazz = "org.apache.http.message.AbstractHttpMessage", method = "addHeader")
	public static void setEntity_entry(final String name, final String value) {
		BTraceUtils.print(name + " , " + value);
	}

	// @OnMethod(clazz = DS_CLASS_HTTP_CLIENT, method = "addHeader", location =
	// @Location(Kind.RETURN) )
	// public static void addHeader_return(@Return CloseableHttpResponse param)
	// {
	// BTraceUtils.printFields(param);
	// }

}
