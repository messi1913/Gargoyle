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
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.kyj.fx.voeditor.visual.exceptions.GagoyleRuntimeException;
import com.kyj.fx.voeditor.visual.framework.handler.ExceptionHandler;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * @author KYJ
 *
 */
public class SimpleProxyServer {

	private String host;
	private int localPort, remotePort;
	private ServerSocket ss;

	private boolean closeRequest = true;

	@SuppressWarnings("rawtypes")
	private List<ProxyListener> onRequests = new ArrayList<>();
	@SuppressWarnings("rawtypes")
	private List<ProxyListener> onResponses = new ArrayList<>();
	private List<ExceptionHandler> onError = new ArrayList<>();

	private AtomicInteger reqSeq = new AtomicInteger();
	private AtomicInteger resSeq = new AtomicInteger();

	/* 생성자 **********************************************************************************************/

	public SimpleProxyServer() {

	}

	public SimpleProxyServer(int localPort, String host, int remotePort) {
		this.localPort = localPort;
		this.host = host;
		this.remotePort = remotePort;
	}

	/***********************************************************************************************/

	int reqIncrementAndGet() {
		int addAndGet = reqSeq.addAndGet(1);
		return new Integer(addAndGet);
	}

	int resIncrementAndGet() {
		int addAndGet = resSeq.addAndGet(1);
		return addAndGet;
	}

	/**
	 * @return the onRequests
	 */
	@SuppressWarnings("rawtypes")
	public final List<ProxyListener> getOnRequests() {
		return onRequests;
	}

	/**
	 * @return the onResponses
	 */
	@SuppressWarnings("rawtypes")
	public final List<ProxyListener> getOnResponses() {
		return onResponses;
	}

	public <T> boolean addOnRequestListener(ProxyListener<T> listener) {
		return this.onRequests.add(listener);
	}

	public <T> boolean addOnResponseListener(ProxyListener<T> listener) {
		return this.onResponses.add(listener);
	}

	public boolean addOnErrorListener(ExceptionHandler handler) {
		return this.onError.add(handler);
	}

	public void start() {
		try {
			close();
			validate();
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						runServer(host, remotePort, localPort);
					} catch (IOException e) {
						SimpleProxyServer.this.notifyErrorMessage(e);
					}
				}
			});

			thread.setPriority(4);
			thread.start();
		} catch (IOException e) {
			notifyErrorMessage(e);
		} // never returns
	}

	private void notifyErrorMessage(Exception e) {
		this.onError.forEach(listener -> listener.handle(e));
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 19.
	 */
	private void validate() {

		if (ValueUtil.isEmpty(this.host))
			throw new GagoyleRuntimeException("Invalide host : " + this.host);

		if (this.remotePort < 0 || this.remotePort > 65535)
			throw new GagoyleRuntimeException("Invalide remote port : " + this.remotePort);

		if (this.localPort < 0 || this.localPort > 65535)
			throw new GagoyleRuntimeException("Invalide local port : " + this.localPort);
	}

	private Set<Socket> connectedServer = new HashSet<>();
	private Set<Socket> connectedClient = new HashSet<>();

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 19.
	 * @param host
	 * @param remoteport
	 * @param localport
	 * @throws IOException
	 */
	private void runServer(String host, int remoteport, int localport) throws IOException {

		closeRequest = false;

		ss = new ServerSocket(localport);

		while (!closeRequest) {
			Socket client = null, server = null;
			try {
				client = ss.accept();

				final InputStream streamFromClient = client.getInputStream();
				final OutputStream streamToClient = client.getOutputStream();

				try {
					server = new Socket(host, remoteport);
				} catch (IOException e) {
					PrintWriter out = new PrintWriter(streamToClient);
					out.print("Proxy server cannot connect to " + host + ":" + remoteport + ":\n" + e + "\n");
					out.flush();
					client.close();
					continue;
				}

				connectedServer.add(client);
				connectedClient.add(server);

				final InputStream streamFromServer = server.getInputStream();
				final OutputStream streamToServer = server.getOutputStream();

				Thread fromClient = new FromClientThread(this, new ConnectionInfo(client), streamFromClient, streamToServer);
				fromClient.setDaemon(true);
				fromClient.start();

				Thread fromServer = new FromServerThread(this, new ConnectionInfo(client), streamFromServer, streamToClient);
				fromServer.setDaemon(true);
				fromServer.run();

			} catch (IOException e) {
				notifyErrorMessage(e);
			} finally {
				try {
					if (server != null)
						server.close();
					if (client != null)
						client.close();
				} catch (IOException e) {
					notifyErrorMessage(e);
				}
			}
		}
	}

	static enum EVENT_TYPE {
		REQUEST, RESPONSE;
	}

	/**
	 * @return the host
	 */
	public final String getHost() {
		return host;
	}

	/**
	 * @return the localPort
	 */
	public final int getLocalPort() {
		return localPort;
	}

	/**
	 * @return the remotePort
	 */
	public final int getRemotePort() {
		return remotePort;
	}

	/**
	 * @param host the host to set
	 */
	public final void setHost(String host) {
		this.host = host;
	}

	/**
	 * @param localPort the localPort to set
	 */
	public final void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	/**
	 * @param remotePort the remotePort to set
	 */
	public final void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	private Predicate<? super Socket> validationHanlder = s -> s != null;
	private Consumer<? super Socket> closeHandler = s -> {
		try {
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	};

	/* (non-Javadoc)
	 * @see java.io.Closeable#close()
	 */
	public void close() throws IOException {
		closeRequest = true;

		if (ss != null) {

			if (!ss.isClosed()) {
				ss.close();
				ss = null;
			}
		}

		connectedServer.stream().filter(validationHanlder).forEach(closeHandler);
		connectedClient.stream().filter(validationHanlder).forEach(closeHandler);
	}

	/**
	 * @return
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 7.
	 */
	public boolean isRunning() {

		if (ss != null) {

			if (ss.isClosed()) {
				return true;
			}

		}
		return !closeRequest;

	}
}
