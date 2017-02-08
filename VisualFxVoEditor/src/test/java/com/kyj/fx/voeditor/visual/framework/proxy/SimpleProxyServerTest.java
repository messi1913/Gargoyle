/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.proxy
 *	작성일   : 2017. 1. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.proxy;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;

import com.kyj.fx.voeditor.visual.exceptions.GagoyleRuntimeException;

/**
 * @author KYJ
 *
 */
public class SimpleProxyServerTest {
	/**
	 * @최초생성일 2017. 1. 19.
	 */
	private static final Charset FOR_NAME = Charset.forName("UTF-8");
	SimpleProxyServer server;

	@Before
	public void setup() {
		server = new SimpleProxyServer(8000, "localhost", 8001);
	}

	@Test
	public final void test() throws GagoyleRuntimeException, IOException {
		server.addOnRequestListener(new UTF8EncodingProxyListener() {

			@Override
			public void onAction(String str) {
				System.out.printf("\n request : \n%s", str);
			}

		});

		server.addOnResponseListener(new UTF8EncodingProxyListener() {

			@Override
			public void onAction(String str) {
				System.out.printf("\n response : \n%s", str);
			}

		});

		server.start();
	}

}
