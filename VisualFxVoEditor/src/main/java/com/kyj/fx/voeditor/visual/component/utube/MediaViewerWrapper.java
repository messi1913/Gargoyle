/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.utube
 *	작성일   : 2017. 5. 29.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.utube;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.dock.pane.DockNode;
import com.kyj.fx.voeditor.visual.main.layout.CloseableParent;

import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

/**
 * @author KYJ
 *
 */
public class MediaViewerWrapper extends CloseableParent<DockNode> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UtubeDownloaderComposite.class);

	private MediaPlayer mediaPlayer;

	public MediaViewerWrapper(File f) {
		super(new DockNode(new BorderPane()));
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

		MediaView mediaView = new MediaView(mediaPlayer);
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
		getParent().setPrefSize(BorderPane.USE_COMPUTED_SIZE, BorderPane.USE_COMPUTED_SIZE);
		BorderPane borderPane = (BorderPane) getParent().getContents();
		borderPane.setCenter(mediaView);

		getParent().widthProperty().addListener((oba, o, n) -> {
			mediaView.setFitWidth(n.doubleValue());
		});

		getParent().heightProperty().addListener((oba, o, n) -> {
			mediaView.setFitHeight(n.doubleValue());
		});

	}

	@Override
	public void close() throws IOException {
		if (mediaPlayer != null)
			mediaPlayer.dispose();
	}

	public void setPrefSize(double width, double height) {
		BorderPane borderPane = (BorderPane) getParent().getContents();
		borderPane.setPrefSize(width, height);
	}

}
