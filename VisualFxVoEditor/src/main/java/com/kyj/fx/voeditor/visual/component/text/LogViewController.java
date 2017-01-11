/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2017. 1. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.StandardOpenOption;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.framework.annotation.FxPostInitialize;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "LogView.fxml")
public class LogViewController implements Closeable {

	/**
	 * @최초생성일 2017. 1. 9.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(LogViewController.class);

	private int seekSize = 1024 * 1024;

	private LogViewComposite composite;
	@FXML
	private TextArea txtLog;

	private FileChannel fileChannel;
	private File monitoringFile;

	public LogViewController() throws Exception {

	}

	private ByteBuffer buffer;
	//Default Encoding UTF-8
	private ObjectProperty<Charset> charset = new SimpleObjectProperty<>(Charset.forName("UTF-8"));

	@FxPostInitialize
	public void onAfter() throws IOException {
		monitoringFile = composite.getMonitoringFile();
		fileChannel = FileChannel.open(monitoringFile.toPath(), StandardOpenOption.READ);
		buffer = ByteBuffer.allocate(seekSize);
	}

	/**
	 * 마지막에 읽어들인 byte 위치를 임시저장한다.
	 * 저장된 위치를 기준으로 파일이 변경되면 그 위치부터 새롭게 추가된 텍스트를 읽어온다.
	 * @최초생성일 2017. 1. 11.
	 */
	private LongProperty mark = new SimpleLongProperty(-1L);

	private void readAction(long lastModified) {
		try {

			int byteCount;

			long length = monitoringFile.length();
			int totalPage = (int) (length / seekSize) - (length % seekSize > 0 ? 0 : 1);

			long lastMarkedPosition = mark.get();
			if (lastMarkedPosition >= 0)
				fileChannel.position(totalPage * seekSize);
			else
				fileChannel.position(lastMarkedPosition);

			//미리 저장된 포지션이후의 모든 텍스트를 읽어들이기 위한 do~while문
			do {
				byteCount = fileChannel.read(buffer);

				if (byteCount != -1) {

					long position = fileChannel.position();
					mark.set(position);

					buffer.flip();

					onModified(charset.get().decode(buffer).toString());

					buffer.clear();

				}

			} while (byteCount != -1);

		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}
	}

	private Timer fileWatcher;
	private LongProperty lastReadTimeStamp = new SimpleLongProperty(-1L);
	private LongProperty lastLength = new SimpleLongProperty(-1L);

	private void watchFile() {
		if (null == fileWatcher) {

			fileWatcher = new Timer();

			fileWatcher.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {

					if (monitoringFile == null)
						return;

					long lastModified = monitoringFile.lastModified();
					long lastReadTime = lastReadTimeStamp.get();

					long space = -1L;
					try {
						space = fileChannel.size();

					} catch (IOException e) {
						e.printStackTrace();
					}

					//Debug.
					//					LOGGER.debug(" file space : " + space + "  //  cached space : " + lastLength.get());

					/*
					 * 처음에는 수정된 일짜 정보 기준으로 처리하였으나, OS영역에서 파일 내용이 변경되어도 반영이 잘 이루어지지않음.
					 * 그래서 파일 사이즈 기준으로 변경유무를 추가.
					 *   lastReadTime == -1  의 조건은 최초에 읽어들인경우 파일 내용을 읽어들이기 위한 조건처리이다.
					 */
					if (lastReadTime == -1 || (space != lastLength.get()) || (lastModified > lastReadTime)) {

						//						Debug
						//						System.out.println("work ## ");

						readAction(lastModified);

						lastLength.set(space);
						lastReadTimeStamp.set(lastModified);

					}

				}
			}, 0, 500);
		}
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 10.
	 * @param string
	 */
	private void onModified(String string) {

		Platform.runLater(() -> {
			this.txtLog.appendText(string);
		});

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 6.
	 * @param logViewComposite
	 */
	public void setComposite(LogViewComposite composite) {
		this.composite = composite;
	}

	/**
	 * file close
	 * @throws IOException
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 9.
	 */
	@Override
	public void close() throws IOException {
		//		monitorFile.close();
		if (fileWatcher != null)
			fileWatcher.cancel();
		if (fileChannel != null)
			fileChannel.close();
		if (buffer != null) {
			buffer.flip();
			buffer.clear();
		}

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 11.
	 */
	public void start() {

		Thread thread = new Thread(() -> {
			watchFile();
		});
		thread.setDaemon(true);
		thread.start();

	}
}
