/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.proxy
 *	작성일   : 2017. 2. 10.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.proxy;

import java.net.Socket;

/**
 * @author KYJ
 *
 */
class ConnectionInfo {

	private Socket client;

	public ConnectionInfo(Socket client) {
		this.client = client;
	}

	/**
	 * @return the client
	 */
	public final Socket getClient() {
		return client;
	}

}
