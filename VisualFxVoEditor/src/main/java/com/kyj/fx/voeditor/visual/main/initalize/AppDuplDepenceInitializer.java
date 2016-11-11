/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.initalize
 *	작성일   : 2016. 11. 11.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main.initalize;

import java.io.IOException;
import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.exceptions.GargoyleDuplicatedRunException;
import com.kyj.fx.voeditor.visual.main.Main;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/***************************
 * 
 * @author KYJ
 *
 *         중복실행 방지처리를 위한 로직 구현
 ***************************/
//@GagoyleInitializable
public class AppDuplDepenceInitializer implements Initializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppDuplDepenceInitializer.class);

	ObjectProperty<ServerSocket> socket = new SimpleObjectProperty<>();

	/**
	 */
	public AppDuplDepenceInitializer() {

	}

	@Override
	public void initialize() throws Exception {

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				int port = 54545;
				try {
					ServerSocket local = new ServerSocket(port);
					local.accept();
					socket.set(local);

					Runtime.getRuntime().addShutdownHook(new Thread(() -> {
						try {
							LOGGER.debug("중복실행 방지.");

							ServerSocket serverSocket = socket.get();
							if (serverSocket != null)
								serverSocket.close();

						} catch (IOException e) {

						}
					}));

				} catch (IOException e) {
					LOGGER.debug("중복실행 방지.");
					Platform.exit();
				}

			}
		});
		thread.setDaemon(true);
		thread.start();

	}

}
