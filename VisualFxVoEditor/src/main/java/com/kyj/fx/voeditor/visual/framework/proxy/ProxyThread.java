/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.proxy
 *	작성일   : 2017. 1. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.proxy;

import com.kyj.fx.voeditor.visual.framework.proxy.SimpleProxyServer.EVENT_TYPE;

/**
 * @author KYJ
 *
 */
public class ProxyThread extends Thread {

	private SimpleProxyServer server;

	public ProxyThread(SimpleProxyServer server) {
		this.server = server;
	}

	protected void on(EVENT_TYPE type, byte[] bytes) {

		if (EVENT_TYPE.REQUEST == type) {
			new ProxyEvent(server.getOnRequests(), bytes).fire();
		} else {
			new ProxyEvent(server.getOnResponses(), bytes).fire();
		}
	}

}
