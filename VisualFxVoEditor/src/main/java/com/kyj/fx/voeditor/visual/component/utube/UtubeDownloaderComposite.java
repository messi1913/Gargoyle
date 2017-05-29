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
import java.util.concurrent.ExecutorService;
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
import com.github.axet.wget.SpeedInfo;
import com.github.axet.wget.info.DownloadInfo;
import com.github.axet.wget.info.DownloadInfo.Part;
import com.github.axet.wget.info.DownloadInfo.Part.States;
import com.github.axet.wget.info.ProxyInfo;
import com.github.axet.wget.info.ex.DownloadIOCodeError;
import com.github.axet.wget.info.ex.DownloadInterruptedError;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.kyj.fx.voeditor.visual.component.dock.pane.DockNode;
import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.framework.annotation.FxPostInitialize;
import com.kyj.fx.voeditor.visual.framework.thread.ExecutorDemons;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
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
	private JFXTextField txtDownloadLocation, txtUtubeURL;
	@FXML
	private Label txtFileName;
	@FXML
	private JFXButton btnDownload, btnOpen;
	@FXML
	private ProgressBar pb;
	@FXML
	private JFXComboBox<YoutubeQuality> cbQuality;
	@FXML
	private JFXTextArea lblStatusMsg;

	private ObjectProperty<File> downloadedFile = new SimpleObjectProperty<>();

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

		this.downloadedFile.addListener((oba, o, n) -> {
			if (n != null && n.exists()) {
				//				btnOpen.setDisable(false);
				this.txtFileName.setText(n.getName());
			} else {
				//				btnOpen.setDisable(true);
			}
		});
	}

	@FxPostInitialize
	public void postInit() {
		String location = ResourceLoader.getInstance().get(ResourceLoader.UTUBE_LAST_DOUWNLOAD_LOCATION);
		if (ValueUtil.isNotEmpty(location)) {
			File file = new File(location);
			if (file.exists()) {
				txtDownloadLocation.setText(file.getAbsolutePath());
			}
		}
	}

	private BooleanProperty wasDownloading = new SimpleBooleanProperty();

	public void doDownload(String url, File path) {

		ExecutorService gargoyleSystemExecutorSerivce = ExecutorDemons.getGargoyleSystemExecutorSerivce();

		gargoyleSystemExecutorSerivce.execute(new Runnable() {

			@Override
			public void run() {

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

					ProxyInfo proxyInfo = new ProxyInfo("168.219.61.252", 8080);
					// // create proper html parser depends on url
					user = VGet.parser(web);
					//
					// // download limited video quality from youtube
					//					user = new YouTubeQParser(cbQuality.getValue());
					//
					// // download mp4 format only, fail if non exist
					//					user = new YouTubeMPGParser();
					//
					// // create proper videoinfo to keep specific video information
					VideoInfo videoinfo = user.info(web);
					//
					VGet v = new VGet(videoinfo, path);
					//
					VGetStatus notify = new VGetStatus(videoinfo, stop);
					//
					// // [OPTIONAL] call v.extract() only if you d like to get video title
					// // or download url link before start download. or just skip it.
					v.extract(user, stop, notify);
					//
					LOGGER.debug("Title: " + videoinfo.getTitle());
					Platform.runLater(() -> {
						lblStatusMsg.appendText("Title: " + videoinfo.getTitle());
						lblStatusMsg.appendText("\n");
						List<VideoFileInfo> list = videoinfo.getInfo();
						if (list != null) {
							for (VideoFileInfo d : list) {
								lblStatusMsg.appendText("Download URL: " + d.getSource() + "\nfilename : " + d.getContentFilename());
								lblStatusMsg.appendText("\n");
							}
						}

						downloadedFile.set(null);
						getBar().setProgress(0.0d);

						wasDownloading.set(true);
					});

					doDownload(v, user, notify, stop);

				} catch (DownloadInterruptedError e) {
					LOGGER.error(ValueUtil.toString(e));
					Platform.runLater(() -> {
						wasDownloading.set(false);
						lblStatusMsg.appendText(ValueUtil.toString(e));
						lblStatusMsg.appendText("\n");
					});
				} catch (RuntimeException e) {
					LOGGER.error(ValueUtil.toString(e));

					Platform.runLater(() -> {
						wasDownloading.set(false);
						lblStatusMsg.appendText(ValueUtil.toString(e));
						lblStatusMsg.appendText("\n");
					});
				} catch (Exception e) {
					LOGGER.error(ValueUtil.toString(e));

					Platform.runLater(() -> {
						wasDownloading.set(false);
						lblStatusMsg.appendText(ValueUtil.toString(e));
						lblStatusMsg.appendText("\n");
					});
				}
			}
		});

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
		AtomicBoolean stop;
		Map<VideoFileInfo, SpeedInfo> map = new HashMap<VideoFileInfo, SpeedInfo>();

		public VGetStatus(VideoInfo i, AtomicBoolean stop) {
			this.videoinfo = i;
			this.stop = stop;
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
			Platform.runLater(() -> {
				List<VideoFileInfo> dinfoList = videoinfo.getInfo();

				// notify app or save download state
				// you can extract information from DownloadInfo info;
				switch (videoinfo.getState()) {
				//				case EXTRACTING:

				//				break;
				//				case EXTRACTING_DONE: {
				//				break;
				//				}
				case DONE: {
					if (videoinfo instanceof YouTubeInfo) {
						YouTubeInfo i = (YouTubeInfo) videoinfo;
						lblStatusMsg.appendText(videoinfo.getState() + " " + i.getVideoQuality());
						lblStatusMsg.appendText("\n");
						//TODO 이미지 로드 작업
						//						String imageUrl = i.getImageUrl();
						//						if (ValueUtil.isNotEmpty(imageUrl)) {
						//							byte[] image = null;
						//							try {
						//								image = RequestUtil.requestSSL(new URL(imageUrl), (is, code) -> {
						//									byte[] buff = new byte[1024 * 1024 * 1024];
						//									try {
						//										is.read(buff);
						//									} catch (Exception e) {
						//									}
						//									return buff;
						//								});
						//							} catch (Exception e) {
						//								e.printStackTrace();
						//							}
						//							
						//						}

						//					contName = i.getInfo().get(0).getContentFilename();
					} else if (videoinfo instanceof VimeoInfo) {
						VimeoInfo i = (VimeoInfo) videoinfo;
						lblStatusMsg.appendText(videoinfo.getState() + " " + i.getVideoQuality());
						lblStatusMsg.appendText("\n");
						//					contName = i.getInfo().get(0).getContentFilename();
					} else {
						lblStatusMsg.appendText("downloading unknown quality");
						lblStatusMsg.appendText("\n");
					}

					VideoFileInfo d = videoinfo.getInfo().get(0);
					// for (VideoFileInfo d : videoinfo.getInfo()) {
					SpeedInfo speedInfo = getSpeedInfo(d);
					speedInfo.end(d.getCount());
					lblStatusMsg.appendText(String.format("file:%d - %s (%s)", dinfoList.indexOf(d), d.targetFile,
							formatSpeed(speedInfo.getAverageSpeed())));
					lblStatusMsg.appendText("\n");

					if (com.github.axet.vget.info.VideoInfo.States.DONE == videoinfo.getState()) {

						String contentFilename = d.getContentFilename();

						if (ValueUtil.isEmpty(contentFilename)) {
							File target = d.getTarget();
							downloadedFile.set(target);
						}

						wasDownloading.set(false);
						getBar().setProgress(1.0d);

					}

				}

					break;
				case ERROR:

					lblStatusMsg.appendText(videoinfo.getState() + " " + videoinfo.getDelay());
					if (dinfoList != null) {
						for (DownloadInfo dinfo : dinfoList) {

							LOGGER.debug(ValueUtil.toString(dinfo.getException()));

							lblStatusMsg.appendText(
									"file:" + dinfoList.indexOf(dinfo) + " - " + dinfo.getException() + " delay:" + dinfo.getDelay());
							lblStatusMsg.appendText("\n");
						}
					}
					wasDownloading.set(false);

					//					LOGGER.error(ValueUtil.toString(videoinfo.getException()));
					break;
				case RETRYING:
					lblStatusMsg.appendText(videoinfo.getState() + " " + videoinfo.getDelay());
					lblStatusMsg.appendText("\n");
					int errorCode = -1;
					if (dinfoList != null) {
						for (DownloadInfo dinfo : dinfoList) {
							Throwable exception = dinfo.getException();
							if (exception instanceof DownloadIOCodeError) {
								errorCode = ((DownloadIOCodeError) exception).getCode();
							}
							lblStatusMsg.appendText("file:" + dinfoList.indexOf(dinfo) + " - " + dinfo.getState() + " " + exception
									+ " delay:" + dinfo.getDelay());
							lblStatusMsg.appendText("\n");
						}
					}

					if (videoinfo.getDelay() == 0 && (errorCode == 403)) {
						LOGGER.error(ValueUtil.toString(videoinfo.getException()));
						stop.set(true);
					}

					break;
				case DOWNLOADING:
					long now = System.currentTimeMillis();
					if (now - 1000 > last) {
						last = now;

						String parts = "";

						//						VideoFileInfo dinfo = dinfoList.get(0);
						for (VideoFileInfo dinfo : dinfoList) {

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
							String format = String.format("file:%d - %s %.2f %s (%s)", dinfoList.indexOf(dinfo), videoinfo.getState(),
									progress, parts, formatSpeed(speedInfo.getCurrentSpeed()));

							lblStatusMsg.appendText(format);
							lblStatusMsg.appendText("\n");
							lblStatusMsg.appendText("progress : " + progress);
							lblStatusMsg.appendText("\n");

							getBar().setProgress(progress);
						}

					}
					break;
				default:
					break;
				}
			});

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
			ResourceLoader.getInstance().put(ResourceLoader.UTUBE_LAST_DOUWNLOAD_LOCATION, showDirectoryDialog.getAbsolutePath());
		}
	}

	@FXML
	public void btnOpenOnAction() {

		//		if (file != null && file.exists()) {
		//			FileUtil.openFile(file);
		//		}

		File showFileDialog = DialogUtil.showFileDialog(null, chooser -> {
			File file = downloadedFile.get();
			if (file != null) {
				chooser.setInitialDirectory(file.getParentFile());
				chooser.setInitialFileName(file.getName());
			}

		});

		if (showFileDialog == null)
			return;

		//			Media media = new Media(showFileDialog.toURI().toURL().toExternalForm());
		//
		//			MediaPlayer mediaPlayer = new MediaPlayer(media);
		//			mediaPlayer.setAutoPlay(true);
		//			mediaPlayer.setOnReady(() -> {
		//				LOGGER.debug("ready");
		//			});
		//			mediaPlayer.setOnEndOfMedia(() -> {
		//				mediaPlayer.dispose();
		//			});
		//
		//			MediaView mediaView = new MediaView(mediaPlayer);
		//			mediaView.setPreserveRatio(true);
		//
		//			mediaView.setOnMouseClicked(ev -> {
		//				if (ev.getClickCount() == 1) {
		//					if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
		//						mediaPlayer.pause();
		//					} else if (mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
		//						mediaPlayer.play();
		//					}
		//				}
		//			});
		//
		//			BorderPane borderPane = new BorderPane(mediaView);
		//			borderPane.setPrefSize(1200d, 800d);
		//			Scene scene = new Scene(borderPane, 1200d, 800d);

		MediaViewerWrapper wrapper = new MediaViewerWrapper(showFileDialog);
		Stage owner = new Stage();
		wrapper.setPrefSize(1200d, 800d);

		FxUtil.createDockStageAndShow(owner, new DockNode(wrapper, showFileDialog.getName()));

		owner.widthProperty().addListener((oba, o, n) -> {
			wrapper.setFitWidth(n.doubleValue());
		});

		owner.heightProperty().addListener((oba, o, n) -> {
			wrapper.setFitHeight(n.doubleValue());
		});

	}

}
