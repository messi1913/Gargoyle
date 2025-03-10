/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.proxy
 *	작성일   : 2017. 1. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.kyj.fx.voeditor.visual.framework.proxy.SimpleProxyServer.EVENT_TYPE;

/**
 * @author KYJ
 *
 */
public class FromServerThread extends ProxyThread {

	byte[] reply = new byte[4096];
	private InputStream streamFromServer;
	private OutputStream streamToClient;
	private ConnectionInfo info;
	public FromServerThread(SimpleProxyServer server, ConnectionInfo info, InputStream streamFromServer, OutputStream streamToClient) {
		super(server);
		this.streamFromServer = streamFromServer;
		this.streamToClient = streamToClient;
		this.info = info;
	}

	@Override
	public void run() {
		int bytesRead = -1;
		int seq = 0;
		try {

			while ((bytesRead = streamFromServer.read(reply)) != -1) {
				on(seq, EVENT_TYPE.RESPONSE, reply);
				streamToClient.write(reply, 0, bytesRead);
				streamToClient.flush();
				seq = server.resIncrementAndGet();
			}

		} catch (IOException e) {
		}
		try {
			streamToClient.close();
		} catch (IOException e) {
		}
	}
}
