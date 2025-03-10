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

import com.kyj.fx.voeditor.visual.framework.PrimaryStageCloseable;
import com.kyj.fx.voeditor.visual.framework.handler.ExceptionHandler;
import com.kyj.fx.voeditor.visual.framework.thread.CloseableCallable;
import com.kyj.fx.voeditor.visual.framework.thread.DemonThreadFactory;
import com.kyj.fx.voeditor.visual.main.Main;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/***************************
 *
 * @author KYJ
 *
 *         중복실행 방지처리를 위한 로직 구현
 ***************************/

public abstract class AppDuplDepenceInitializer implements Initializable, ExceptionHandler, PrimaryStageCloseable {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppDuplDepenceInitializer.class);
	int port = 54545;
	private ServerSocket local;
	private DemonThreadFactory<Boolean> fac = new DemonThreadFactory<>();
	private boolean enable;

	/**
	 * @throws IOException
	 */
	public AppDuplDepenceInitializer(int port, boolean enable) {
		this.port = port;
		this.enable = enable;
		try {
			if (enable)
				local = new ServerSocket(port);
		} catch (Exception e) {
			handle(e);
		}

	}

	public AppDuplDepenceInitializer(boolean enable) throws IOException {
		this(54545, enable);

	}

	public AppDuplDepenceInitializer() throws IOException {
		this(54545, true);
	}

	/**
	 * 어플리케이션 중복 실행 방지를 어떻게 표현할지에 대한 모델 구현체.
	 * 
	 * 소켓을 이용해 먼저 바인드함, 그러면 이후에 켜지는 동일한 어플리케이션은 동일 포트에 중복접속이 불가능하기때문에
	 * 
	 * 종료되게됨.
	 * 
	 * @author KYJ
	 *
	 */
	class CheckModel implements CloseableCallable<Boolean> {

		@Override
		public Boolean call() throws Exception {
			if (local != null) {
				LOGGER.debug("중복실행 방지처리 시작.");
				local.accept(); // Infinity Blocking..
				return true;
			} else {
				return false;
			}

		}

		@Override
		public void close() throws IOException {
			LOGGER.debug("----");
		}

	}

	@Override
	public void initialize() throws Exception {

		Thread newThread = fac.newThread(new CheckModel(), r -> {

			if (r) {
				LOGGER.debug("Keep Going...");
			}

		}, e -> handle(e));

		newThread.setName("Application Duplication Check Thread - Gargoyle");
		newThread.setDaemon(true);
		newThread.start();

		Main.addPrimaryStageCloseListener(this);
	}

	@Override
	public void closeRequest() {
		if (local != null) {
			try {
				local.close();
				LOGGER.debug("Close");
			} catch (IOException e) {
				LOGGER.error(ValueUtil.toString(e));
			}
		}

	}

}
