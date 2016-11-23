/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.initalize
 *	작성일   : 2016. 11. 11.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main.initalize;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/***************************
 *
 * @author KYJ
 *
 *         중복실행 방지처리를 위한 로직 구현
 ***************************/

public abstract class AppDuplDepenceInitializer implements Initializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppDuplDepenceInitializer.class);

	/**
	 */
	public AppDuplDepenceInitializer() {

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 23.
	 * @param e
	 */
	public abstract void handle(Exception e);

	@Override
	public void initialize() throws Exception {
		int port = 54545;
		ServerSocket local = new ServerSocket(port);

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					LOGGER.debug("중복실행 방지처리 시작.");
					local.accept();
				} catch (IOException e) {
					handle(e);
				}

			}
		}, "Application Duplication Check Thread - Gargoyle");

		thread.start();
	}

}
