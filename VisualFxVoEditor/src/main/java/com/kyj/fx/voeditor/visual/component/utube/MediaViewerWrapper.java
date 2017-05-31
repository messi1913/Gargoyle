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
 * @Deprecated 지원불가한게 많음.
 */
@Deprecated
public class MediaViewerWrapper extends BorderPane implements Closeable {

	private static final Logger LOGGER = LoggerFactory.getLogger(UtubeDownloaderComposite.class);

	private MediaPlayer mediaPlayer;
	private MediaPlayer audioPlayer;

	private MediaView mediaView;
	private MediaView audioView;
	private File webm, mp4;

	public MediaViewerWrapper(File webm, File mp4) {
		this.webm = webm;
		this.mp4 = mp4;
		try {
			init();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public void init() throws MalformedURLException {

		if (!webm.exists() && mp4.exists()) {
			Media media = new Media(mp4.toURI().toURL().toExternalForm());

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
			setCenter(mediaView);
		}

		else if (webm.exists() && mp4.exists()) {
			Media media = new Media(webm.toURI().toURL().toExternalForm());
			Media audio = new Media(mp4.toURI().toURL().toExternalForm());

			mediaPlayer = new MediaPlayer(media);
			audioPlayer = new MediaPlayer(audio);

			mediaPlayer.setAutoPlay(false);
			audioPlayer.setAutoPlay(false);

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
			audioView = new MediaView(audioPlayer);

			mediaView.setPreserveRatio(true);

			mediaView.setOnMouseClicked(ev -> {
				if (ev.getClickCount() == 1) {
					if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
						mediaPlayer.pause();
						audioPlayer.pause();
					} else if (mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
						mediaPlayer.play();
						audioPlayer.play();
					}
				}
			});
			setCenter(mediaView);
		}

	}

	@Override
	public void close() throws IOException {
		if (mediaPlayer != null) {
			mediaPlayer.dispose();
			LOGGER.debug("media disposed");
		}
		if (audioPlayer != null) {
			audioPlayer.dispose();
			LOGGER.debug("audio disposed");
		}

	}

	public void setFitWidth(double doubleValue) {
		mediaView.setFitWidth(doubleValue);

	}

	public void setFitHeight(double doubleValue) {
		mediaView.setFitHeight(doubleValue);
	}

}
