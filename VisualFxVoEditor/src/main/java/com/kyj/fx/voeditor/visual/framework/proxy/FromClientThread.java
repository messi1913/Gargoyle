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
public class FromClientThread extends ProxyThread {

	final byte[] request = new byte[1024];
	private InputStream streamFromClient;
	private OutputStream streamToServer;
	private ConnectionInfo info;
	public FromClientThread(SimpleProxyServer server, ConnectionInfo info, InputStream streamFromClient, OutputStream streamToServer) {
		super(server);
		this.streamFromClient = streamFromClient;
		this.streamToServer = streamToServer;
		this.info = info;
	}

	@Override
	public void run() {
		int bytesRead;
		int seq = 0;
		try {
			//							System.out.printf("request : \n ");
			while ((bytesRead = streamFromClient.read(request)) != -1) {

				System.out.println("Local Socket : "  +  info.getClient().getLocalAddress());
				System.out.println("Remote Socket : "  +  info.getClient().getRemoteSocketAddress());
				//								String string = new String(request, Charset.forName("UTF-8"));
				on(seq, EVENT_TYPE.REQUEST, request);

				streamToServer.write(request, 0, bytesRead);
				streamToServer.flush();
				seq = server.reqIncrementAndGet();
			}

		} catch (IOException e) {
		}

		try {
			streamToServer.close();
		} catch (IOException e) {
		}
	}
}
