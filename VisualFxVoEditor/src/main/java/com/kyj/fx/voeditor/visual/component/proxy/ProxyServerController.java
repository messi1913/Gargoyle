/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.proxy
 *	작성일   : 2017. 2. 7.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.proxy;

import java.io.Closeable;
import java.io.IOException;

import org.fxmisc.richtext.CodeArea;

import com.jfoenix.controls.JFXListView;
import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.fxloader.FxPostInitialize;
import com.kyj.fx.voeditor.visual.component.text.IntegerField;
import com.kyj.fx.voeditor.visual.framework.proxy.SimpleProxyServer;
import com.kyj.fx.voeditor.visual.framework.proxy.UTF8EncodingProxyListener;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "ProxyServerView.fxml")
public class ProxyServerController implements Closeable {

	private ProxyServerComposite proxyServerComposite;

	@FXML
	private TextField txtRemoteHost;
	@FXML
	private IntegerField txtLocalPort, txtRemotePort;
	@FXML
	private CodeArea codeServerLog;
	@FXML
	private JFXListView<String> lvLocalData, lvRemoteData;

	private SimpleProxyServer server;
	private ProxyServerLogManager log;

	public ProxyServerController() {

		this.server = new SimpleProxyServer();
		this.server.addOnRequestListener(new UTF8EncodingProxyListener() {

			@Override
			public void onAction(int seq, String str) {
				Platform.runLater(() -> {
					lvLocalData.getItems().add(String.format("[%d] Content : %s", seq, str));
				});
			}
		});

		server.addOnResponseListener(new UTF8EncodingProxyListener() {

			@Override
			public void onAction(int seq, String str) {

				Platform.runLater(() -> {
					lvRemoteData.getItems().add(String.format("[%d]Content : %s", seq, str));
				});
			}
		});

		server.addOnErrorListener(msg -> {
			Platform.runLater(() -> {
				codeServerLog.appendText(ValueUtil.toString(msg));
			});
		});

		log = new ProxyServerLogManager(message -> {
			codeServerLog.appendText(message);
		});

	}

	@FXML
	public void initialize() {

	}

	@FxPostInitialize
	public void afterInitialize() {
		initLocal();
		initRemote();
	}

	private void initLocal() {
		MenuItem miClear = new MenuItem("Clear All");
		lvLocalData.setContextMenu(new ContextMenu(miClear));

		miClear.setOnAction(ev -> {
			lvLocalData.getItems().clear();
		});
	}

	private void initRemote() {
		MenuItem miClear = new MenuItem("Clear All");
		lvRemoteData.setContextMenu(new ContextMenu(miClear));

		miClear.setOnAction(ev -> {
			lvRemoteData.getItems().clear();
		});
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 7.
	 * @param proxyServerComposite
	 */
	public void setComposite(ProxyServerComposite proxyServerComposite) {
		this.proxyServerComposite = proxyServerComposite;
	}

	@FXML
	public void btnStartOnAction() {
		try {
			//검증
			validate();

			if (!this.server.isRunning()) {
				this.server.setHost(this.txtRemoteHost.getText());
				this.server.setLocalPort(Integer.parseInt(this.txtLocalPort.getText(), 10));
				this.server.setRemotePort(Integer.parseInt(this.txtRemotePort.getText(), 10));
				//프록시 서버 시작.
				this.server.start();
				log.push("Server Start....\n");
			} else {
				log.push("Already Started....\n");
			}
		} catch (Exception e) {
			log.push(e.getMessage());
		}
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 7.
	 */
	private void validate() throws Exception {

		//포트번호 유효성 체크
		String localPort = txtLocalPort.getText();
		if (ValueUtil.isEmpty(localPort)) {
			throw new Exception("localPort is empty \n");
		}

		int nLocalPort = Integer.parseInt(localPort);
		if (nLocalPort < 0 || nLocalPort > 65535) {
			throw new Exception("Invalide Port Number - Local port \n");
		}

		String remotePort = txtRemotePort.getText();
		if (ValueUtil.isEmpty(remotePort)) {
			throw new Exception("remotePort is empty. \n");
		}

		int nRemotePort = Integer.parseInt(remotePort);
		if (nRemotePort < 0 || nRemotePort > 65535) {
			throw new Exception("Invalide Port Number - Remote port \n");
		}

		//호스트 유효성 체크
		String host = txtRemoteHost.getText();
		if (ValueUtil.isEmpty(host)) {
			throw new Exception("host is empty. \n");
		}
	}

	@FXML
	public void btnStopOnAction() {
		log.push("Server Stop.");

		try {
			this.server.close();
			log.push("Server Stoped..");
		} catch (IOException e) {
			log.push("Failure Stoped.");
		}
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 7.
	 */
	public void close() {
		if (server != null) {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
