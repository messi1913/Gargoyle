/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.utube
 *	작성일   : 2017. 5. 29.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.utube;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

/**
 * @author KYJ
 *
 */
public class MediaViewerWrapper extends BorderPane implements Closeable {

	private static final Logger LOGGER = LoggerFactory.getLogger(UtubeDownloaderComposite.class);

	private MediaPlayer mediaPlayer;
	private MediaView mediaView;

	public MediaViewerWrapper(File f) {

		try {
			init(f);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public void init(File m) throws MalformedURLException {
		Media media = new Media(m.toURI().toURL().toExternalForm());

		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setAutoPlay(true);
		mediaPlayer.setOnReady(() -> {
			LOGGER.debug("ready");
		});
		mediaPlayer.setOnEndOfMedia(() -> {
			try {
				close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		mediaView = new MediaView(mediaPlayer);
		mediaView.setPreserveRatio(true);

		mediaView.setOnMouseClicked(ev -> {
			if (ev.getClickCount() == 1) {
				if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
					mediaPlayer.pause();
				} else if (mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
					mediaPlayer.play();
				}
			}
		});
		//		mediaView.setFitHeight(value);
		//		mediaView.setPrefSize(BorderPane.USE_COMPUTED_SIZE, BorderPane.USE_COMPUTED_SIZE);

		//		this.getParent().widthProperty().addListener((oba, o, n) -> {
		//			mediaView.setFitWidth(n.doubleValue());
		//		});
		//
		//		this.getParent().heightProperty().addListener((oba, o, n) -> {
		//			mediaView.setFitHeight(n.doubleValue());
		//		});

		setCenter(mediaView);
	}

	@Override
	public void close() throws IOException {
		if (mediaPlayer != null) {
			mediaPlayer.dispose();
			LOGGER.debug("media disposed");
		}

	}

	public void setFitWidth(double doubleValue) {
		mediaView.setFitWidth(doubleValue);

	}

	public void setFitHeight(double doubleValue) {
		mediaView.setFitHeight(doubleValue);
	}

}
