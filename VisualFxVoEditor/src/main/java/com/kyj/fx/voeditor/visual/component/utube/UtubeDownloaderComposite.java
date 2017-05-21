/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.utube
 *	작성일   : 2017. 5. 20.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.utube;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.axet.vget.VGet;
import com.github.axet.vget.info.VGetParser;
import com.github.axet.vget.info.VideoFileInfo;
import com.github.axet.vget.info.VideoInfo;
import com.github.axet.vget.vhs.VimeoInfo;
import com.github.axet.vget.vhs.YouTubeInfo;
import com.github.axet.vget.vhs.YouTubeInfo.YoutubeQuality;
import com.github.axet.vget.vhs.YouTubeMPGParser;
import com.github.axet.vget.vhs.YouTubeQParser;
import com.github.axet.wget.SpeedInfo;
import com.github.axet.wget.info.DownloadInfo;
import com.github.axet.wget.info.DownloadInfo.Part;
import com.github.axet.wget.info.DownloadInfo.Part.States;
import com.github.axet.wget.info.ex.DownloadInterruptedError;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "UtubeDownloaderView.fxml", isSelfController = true)
public class UtubeDownloaderComposite extends BorderPane {

	private static final Logger LOGGER = LoggerFactory.getLogger(UtubeDownloaderComposite.class);

	public static final String TITLE = "유튜브 다운로더";

	// @FXML
	// private JFXListView<UtubeItemDVO> lvDownlaodCont;
	@FXML
	private JFXTextField txtDownloadLocation, txtUtubeURL, txtFileName;
	@FXML
	private JFXButton btnDownload;
	@FXML
	private ProgressBar pb;
	@FXML
	private JFXComboBox<YoutubeQuality> cbQuality;

	public UtubeDownloaderComposite() {

		FxUtil.loadRoot(UtubeDownloaderComposite.class, this, err -> {
			LOGGER.error(ValueUtil.toString(err));
		});
	}

	@FXML
	public void initialize() {
		btnDownload.setDisable(true);
		this.txtDownloadLocation.setText(System.getProperty("user.home"));
		this.txtDownloadLocation.setPromptText("Downlaod Location");

		// this.lvDownlaodCont.setCellFactory(new Callback<ListView<UtubeItemDVO>, ListCell<UtubeItemDVO>>() {
		//
		// @Override
		// public ListCell<UtubeItemDVO> call(ListView<UtubeItemDVO> param) {
		// return new UtubeListCell();
		// }
		// });
		// this.lvDownlaodCont.setOnDragDetected(ev -> {
		// ev.setDragDetect(true);
		// });

		txtUtubeURL.setOnDragOver(ev -> {
			if (ev.getDragboard().hasUrl()) {
				ev.acceptTransferModes(TransferMode.LINK);
				ev.consume();
			}
		});

		txtUtubeURL.setOnDragDropped(ev -> {
			Dragboard dragboard = ev.getDragboard();
			String url = dragboard.getUrl();
			txtUtubeURL.setText(url);
		});

		txtUtubeURL.textProperty().addListener((oba, o, n) -> {
			btnDownload.setDisable(n.trim().isEmpty());
		});

		// 초기값
		this.cbQuality.getItems().addAll(YoutubeQuality.values());
		// 디폴트
		this.cbQuality.getSelectionModel().select(YoutubeQuality.p480);

		this.cbQuality.setCellFactory(new Callback<ListView<YoutubeQuality>, ListCell<YoutubeQuality>>() {

			@Override
			public ListCell<YoutubeQuality> call(ListView<YoutubeQuality> param) {
				return new TextFieldListCell<>(new StringConverter<YoutubeQuality>() {

					@Override
					public String toString(YoutubeQuality object) {
						return object.name();
					}

					@Override
					public YoutubeQuality fromString(String string) {
						return YoutubeQuality.valueOf(string);
					}
				});
			}
		});

		this.wasDownloading.addListener((oba, o, n) -> {
			if (n != null)
				this.btnDownload.setDisable(n.booleanValue());
		});

	}

	private BooleanProperty wasDownloading = new SimpleBooleanProperty();

	public void doDownload(String url, File path) {

		Service<Void> service = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {

				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {

						try {
							final AtomicBoolean stop = new AtomicBoolean(false);
							URL web = new URL(url);
							//
							// // [OPTIONAL] limit maximum quality, or do not call this function if
							// // you wish maximum quality available.
							// //
							// // if youtube does not have video with requested quality, program
							// // will raise en exception.
							VGetParser user = null;
							//
							// // create proper html parser depends on url
							user = VGet.parser(web);
							//
							// // download limited video quality from youtube
							user = new YouTubeQParser(cbQuality.getValue());
							//
							// // download mp4 format only, fail if non exist
							user = new YouTubeMPGParser();
							//
							// // create proper videoinfo to keep specific video information
							VideoInfo videoinfo = user.info(web);
							//
							VGet v = new VGet(videoinfo, path);
							//
							VGetStatus notify = new VGetStatus(videoinfo);
							//
							// // [OPTIONAL] call v.extract() only if you d like to get video title
							// // or download url link before start download. or just skip it.
							v.extract(user, stop, notify);
							//
							System.out.println("Title: " + videoinfo.getTitle());
							List<VideoFileInfo> list = videoinfo.getInfo();
							if (list != null) {
								VideoFileInfo d = list.get(0);
								// for (VideoFileInfo d : list) {
								// [OPTIONAL] setTarget file for each download source video/audio
								// use d.getContentType() to determine which or use
								// v.targetFile(dinfo, ext, conflict) to set name dynamically or
								// d.targetFile = new File("/Downloads/CustomName.mp3");
								// to set file name manually.

								System.out.println("Download URL: " + d.getSource() + "\nfilename : " + d.getContentFilename());
								// }
								txtFileName.setText(d.getContentFilename());

							}
							//
							// v.download(user, stop, notify);

							wasDownloading.set(true);

							doDownload(v, user, notify, stop);

						} catch (DownloadInterruptedError e) {
							wasDownloading.set(false);
							throw e;
						} catch (RuntimeException e) {
							wasDownloading.set(false);
							throw e;
						} catch (Exception e) {
							wasDownloading.set(false);
							throw new RuntimeException(e);
						}

						return null;
					}
				};
			}

		};

		service.start();

	}

	public void doDownload(VGet v, VGetParser user, VGetStatus notify, AtomicBoolean stop) {
		v.download(user, stop, notify);
	}

	public ProgressBar getBar() {
		return this.pb;
	}

	class VGetStatus implements Runnable {
		VideoInfo videoinfo;
		long last;

		Map<VideoFileInfo, SpeedInfo> map = new HashMap<VideoFileInfo, SpeedInfo>();

		public VGetStatus(VideoInfo i) {
			this.videoinfo = i;
		}

		public SpeedInfo getSpeedInfo(VideoFileInfo dinfo) {
			SpeedInfo speedInfo = map.get(dinfo);
			if (speedInfo == null) {
				speedInfo = new SpeedInfo();
				speedInfo.start(dinfo.getCount());
				map.put(dinfo, speedInfo);
			}
			return speedInfo;
		}

		@Override
		public void run() {
			List<VideoFileInfo> dinfoList = videoinfo.getInfo();

			// notify app or save download state
			// you can extract information from DownloadInfo info;
			switch (videoinfo.getState()) {
			case EXTRACTING:
			case EXTRACTING_DONE: {

			}
			case DONE: {
				if (videoinfo instanceof YouTubeInfo) {
					YouTubeInfo i = (YouTubeInfo) videoinfo;
					System.out.println(videoinfo.getState() + " " + i.getVideoQuality());
				} else if (videoinfo instanceof VimeoInfo) {
					VimeoInfo i = (VimeoInfo) videoinfo;
					System.out.println(videoinfo.getState() + " " + i.getVideoQuality());
				} else {
					System.out.println("downloading unknown quality");
				}

				VideoFileInfo d = videoinfo.getInfo().get(0);
				// for (VideoFileInfo d : videoinfo.getInfo()) {
				SpeedInfo speedInfo = getSpeedInfo(d);
				speedInfo.end(d.getCount());
				System.out.println(
						String.format("file:%d - %s (%s)", dinfoList.indexOf(d), d.targetFile, formatSpeed(speedInfo.getAverageSpeed())));

				// }

				// Platform.runLater(() -> {
				getBar().setProgress(1.0d);
				wasDownloading.set(false);
				// });
			}

				break;
			case ERROR:
				System.out.println(videoinfo.getState() + " " + videoinfo.getDelay());

				if (dinfoList != null) {
					for (DownloadInfo dinfo : dinfoList) {
						System.out
								.println("file:" + dinfoList.indexOf(dinfo) + " - " + dinfo.getException() + " delay:" + dinfo.getDelay());
					}
				}
				break;
			case RETRYING:
				System.out.println(videoinfo.getState() + " " + videoinfo.getDelay());

				if (dinfoList != null) {
					for (DownloadInfo dinfo : dinfoList) {
						System.out.println("file:" + dinfoList.indexOf(dinfo) + " - " + dinfo.getState() + " " + dinfo.getException()
								+ " delay:" + dinfo.getDelay());
					}
				}
				break;
			case DOWNLOADING:
				long now = System.currentTimeMillis();
				if (now - 1000 > last) {
					last = now;

					String parts = "";

					VideoFileInfo dinfo = dinfoList.get(0);
					// for (VideoFileInfo dinfo : dinfoList) {

					SpeedInfo speedInfo = getSpeedInfo(dinfo);
					speedInfo.step(dinfo.getCount());

					List<Part> pp = dinfo.getParts();
					if (pp != null) {
						// multipart download
						for (Part p : pp) {
							if (p.getState().equals(States.DOWNLOADING)) {
								parts += String.format("part#%d(%.2f) ", p.getNumber(), p.getCount() / (float) p.getLength());
							}
						}
					}
					float progress = dinfo.getCount() / (float) dinfo.getLength();
					String format = String.format("file:%d - %s %.2f %s (%s)", dinfoList.indexOf(dinfo), videoinfo.getState(), progress,
							parts, formatSpeed(speedInfo.getCurrentSpeed()));
					System.out.println(format);

					txtFileName.setText(dinfo.getTarget().getName());

					// Platform.runLater(() -> {
					getBar().setProgress(progress);
					// });

					// }
				}
				break;
			default:
				break;
			}
		}
	}

	public String formatSpeed(long s) {
		if (s > 0.1 * 1024 * 1024 * 1024) {
			float f = s / 1024f / 1024f / 1024f;
			return String.format("%.1f GB/s", f);
		} else if (s > 0.1 * 1024 * 1024) {
			float f = s / 1024f / 1024f;
			return String.format("%.1f MB/s", f);
		} else {
			float f = s / 1024f;
			return String.format("%.1f kb/s", f);
		}
	}

	@FXML
	public void btnDownloadOnAction(ActionEvent e) {
		doDownload(txtUtubeURL.getText(), new File(txtDownloadLocation.getText()));
	}

	@FXML
	public void btnBrowseOnAction() {
		File showDirectoryDialog = DialogUtil.showDirectoryDialog(FxUtil.getWindow(this));
		if (showDirectoryDialog != null) {
			this.txtDownloadLocation.setText(showDirectoryDialog.getAbsolutePath());
		}
	}

}
