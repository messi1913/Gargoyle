/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2017. 1. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import java.io.File;
import java.io.IOException;

import com.kyj.fx.voeditor.visual.main.layout.CloseableParent;
import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 *
 */

public class LogViewComposite extends CloseableParent<BorderPane> {

	public int READ_SIZE = 1024 * 1024 * 3; // 3MB;

	private File monitoringFile;

	private LogViewController controller;

	public LogViewComposite(File target) throws Exception {
		super(new BorderPane());
		this.monitoringFile = target;

		getParent().setCenter(FxUtil.load(LogViewController.class, null, null, c -> {
			this.controller = c;
			c.setComposite(this);
		}));
	}

	/**
	 * 로그 모니터링 대상이되는 파일 리턴
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 6.
	 * @return
	 */
	File getMonitoringFile() {
		return this.monitoringFile;
	}

	/**
	 * 컨트롤러 클래스 리턴
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 6.
	 * @return
	 */
	LogViewController getController() {
		return this.controller;
	}
	
	
	public void start(){
		getController().start();
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.main.layout.CloseableParent#close()
	 */
	@Override
	public void close() throws IOException {
		if (controller != null)
			controller.close();
	}

}
