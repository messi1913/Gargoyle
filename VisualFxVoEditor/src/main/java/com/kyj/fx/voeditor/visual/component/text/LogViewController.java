/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2017. 1. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.framework.annotation.FxPostInitialize;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "LogView.fxml")
public class LogViewController {

	public int READ_SIZE = 1024 * 1024 * 3; // 3MB;

	private LogViewComposite composite;
	@FXML
	private TextArea txtLog;

	public LogViewController() throws Exception {

	}


	@FxPostInitialize
	public void onAfter() throws IOException {

		//		System.out.println(watchTargetFile.exists());
		File monitoringFile = composite.getMonitoringFile();
		WatchService newWatchService = FileSystems.getDefault().newWatchService();
		monitoringFile.getParentFile().toPath().register(newWatchService, ENTRY_MODIFY);

		RandomAccessFile randomAccessFile = new RandomAccessFile( /*new File(watchTargetFile, "batch-scheduler.log")*/ monitoringFile, "r");

		long length = randomAccessFile.length();

		long seek = (int) (length - READ_SIZE);
		if (seek < 0) {
			seek = 0;
		}

		randomAccessFile.seek(seek);
		byte[] b = new byte[READ_SIZE];
		randomAccessFile.read(b);
		txtLog.setText(new String(b));

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {

				try {

					byte[] b = new byte[READ_SIZE];

					while (true) {
						try {
							System.out.println("start");
							WatchKey key = newWatchService.take();
							for (WatchEvent<?> event : key.pollEvents()) {
								WatchEvent.Kind<?> kind = event.kind();
								@SuppressWarnings("unchecked")
								WatchEvent<Path> ev = (WatchEvent<Path>) event;
								Path fileName = ev.context();

								System.out.println(kind.name() + " : " + fileName);

								if (kind == ENTRY_MODIFY && fileName.toString().equals("batch-scheduler.log")) {
									System.out.println("My source file has changed!!!");

									Platform.runLater(() -> {

										try {
											randomAccessFile.read(b);
											randomAccessFile.seek(b.length);

											String text = new String(b);
											txtLog.appendText(text);
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

									});
								}
							}
							boolean valid = key.reset();
							if (!valid) {
								break;
							}
						} catch (InterruptedException e) {
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.setDaemon(true);
		thread.start();

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 6.
	 * @param logViewComposite
	 */
	public void setComposite(LogViewComposite composite) {
		this.composite = composite;
	}
}
