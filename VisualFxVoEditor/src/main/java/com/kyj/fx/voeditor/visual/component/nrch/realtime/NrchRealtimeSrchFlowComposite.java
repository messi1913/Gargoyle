/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2016. 11. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.nrch.realtime;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.FlowCardComposite;
import com.kyj.fx.voeditor.visual.component.google.trend.GoogleTrendComposite;
import com.kyj.fx.voeditor.visual.framework.RealtimeSearchItemVO;
import com.kyj.fx.voeditor.visual.framework.RealtimeSearchVO;
import com.kyj.fx.voeditor.visual.framework.thread.ExecutorDemons;
import com.kyj.fx.voeditor.visual.main.layout.CloseableParent;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;
import com.kyj.fx.voeditor.visual.suppliers.NaverRealtimeSrchSupplier;
import com.kyj.fx.voeditor.visual.util.DateUtil;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author KYJ
 *
 */
public class NrchRealtimeSrchFlowComposite extends CloseableParent<BorderPane> {

	public static final String TITLE = "네이버 실시간 검색어";

	private static final Logger LOGGER = LoggerFactory.getLogger(NrchRealtimeSrchFlowComposite.class);
	/**
	 * 네이버 실시간 검색어간에 데이터 그룹별ㄹ 색상을 입힘.
	 *
	 * @최초생성일 2016. 11. 22.
	 */
	private AtomicInteger atomicInteger = new AtomicInteger(0);
	/**
	 * @최초생성일 2016. 11. 22.
	 */
	private ChoiceBox<String> choWaitItems;
	/**
	 * 검색어 요청 처리 시간이 입력되는 라벨.
	 *
	 * @최초생성일 2016. 11. 22.
	 */
	private Label lblRequestTime = new Label();

	/**
	 * UI에 인기검색어 카드가 배치되는 Composite의 주소값을 담고있는 property 객체.
	 *
	 * @최초생성일 2016. 11. 22.
	 */
	private ObjectProperty<FlowCardComposite> flowCardComposite = new SimpleObjectProperty<>();

	/**
	 * @최초생성일 2016. 11. 23.
	 */
	private static final String REALTIME_SRCH_THREAD_POOL_NAME = "RealtimeSrch-Thread-Pool";
	/**
	 * Network 연결처리와 UI간의 비동기 처리를 적용하기위한 Executor클래스.
	 *
	 * @최초생성일 2016. 11. 22.
	 */
	private static ExecutorService gargoyleThreadExecutors = ExecutorDemons.newFixedThreadExecutor(REALTIME_SRCH_THREAD_POOL_NAME, 1);

	/**
	 * 실시간 검색어 처리에 대한 코드 구현부
	 *
	 * @최초생성일 2016. 11. 22.
	 */
	private Service<List<RealtimeSearchVO>> service;

	/**
	 * 실시간 검색어 결과의 임시 데이터 보관소.
	 *
	 * @최초생성일 2016. 11. 22.
	 */
	private ObservableList<RealtimeSearchVO> data = FXCollections.observableArrayList();

	private BooleanProperty isRecycle = new SimpleBooleanProperty(false);
	/**
	 * FlowCardComposite 에 데이터가 입력되면 UI로 컨버팅 처리할 Node를 구현하는 부분.
	 *
	 * @최초생성일 2016. 11. 22.
	 */
	private Function<RealtimeSearchVO, List<VBox>> nodeConverter = v -> {

		final int andIncrement = atomicInteger.getAndIncrement();

		List<VBox> collect = IntStream.iterate(0, t -> t + 1).limit(10).mapToObj(idx -> {
			RealtimeSearchItemVO obj = v.getItems().get(idx);

			Label lblCont = new Label(String.format("%s.- %s", obj.getRank(), obj.getKeyword()));
			lblCont.setMaxWidth(Double.MAX_VALUE);
			lblCont.setMaxHeight(Double.MAX_VALUE);
			lblCont.setAlignment(Pos.CENTER);
			lblCont.setFont(FxUtil.getBoldFont());
			VBox.setVgrow(lblCont, javafx.scene.layout.Priority.ALWAYS);
			Label lblTitle = new Label(String.format("[%s]\n", v.getTitle()));
			lblTitle.setStyle("-fx-background-color : #E9E9E9 ; -fx-font-fill : white;");
			VBox vBox = new VBox(lblTitle, lblCont);

			vBox.setStyle("-fx-background-color: " + getDefaultColor(andIncrement % 12));
			vBox.setMaxWidth(Double.MAX_VALUE);

			vBox.setMaxHeight(100d);

			vBox.setOnMouseClicked(ev -> {

				if (ev.getClickCount() == 1 && MouseButton.PRIMARY == ev.getButton()) {
					if (ev.isConsumed())
						return;

					String link = obj.getLink();
					LOGGER.debug("Link : {} " , link);
					FxUtil.openBrowser(this.getParent(), "http:" + link);
					ev.consume();
				}

			});

			vBox.setUserData(obj);

			vBox.setOnMouseEntered(ev -> {
				vBox.setCursor(Cursor.HAND);
			});
			vBox.setOnMouseExited(ev -> {
				vBox.setCursor(Cursor.DEFAULT);
			});

			vBox.getChildren().forEach(n -> {
				n.setMouseTransparent(true);
				n.setFocusTraversable(false);
			});

			contextMenu(vBox);

			return vBox;
		}).collect(Collectors.toList());

		return collect;
	};

	/**
	 * @param nodeConverter
	 */
	public NrchRealtimeSrchFlowComposite() {
		super(new BorderPane());
		init();

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 21.
	 */
	private void contextMenu(Node target) {

		target.setOnMousePressed(ev -> {
			if (MouseButton.SECONDARY == ev.getButton()) {
				if (ev.getClickCount() != 1) {
					return;
				}

				if (ev.getSource() instanceof VBox) {
					VBox tmp = (VBox) ev.getSource();
					Object userData = tmp.getUserData();
					if (userData != null) {
						ContextMenu contextMenu = new ContextMenu();
						MenuItem menuGoogleTrend = new MenuItem("구글 트랜드로 조회");

						menuGoogleTrend.setOnAction(e -> {
							googleChartSearch((RealtimeSearchItemVO) userData);
						});

						MenuItem menuArticleAnalyzer = new MenuItem("기사 분석기 - Preview ver.");

						menuArticleAnalyzer.setOnAction(e -> {

							FxUtil.createStageAndShow(new ArticleExtractorComposite((RealtimeSearchItemVO) userData), stage -> {
								stage.initOwner(FxUtil.getWindow(getParent()));
								stage.setTitle(ArticleExtractorComposite.TITLE);
								stage.sizeToScene();
							});

						});

						contextMenu.getItems().addAll(menuGoogleTrend, menuArticleAnalyzer);
						contextMenu.show(this.getParent().getScene().getWindow(), ev.getScreenX(), ev.getScreenY());
					}
				}

			}
		});

	}

	/**
	 * 구글 트랜드로 조회
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 22.
	 * @param vo
	 */
	public void googleChartSearch(RealtimeSearchItemVO vo) {

		if (vo != null) {
			GoogleTrendComposite googleTrendComposite = new GoogleTrendComposite();
			googleTrendComposite.searchKeywords(vo.getKeyword());
			SharedMemory.getSystemLayoutViewController().loadNewSystemTab(GoogleTrendComposite.TITLE, googleTrendComposite);
		}

	}

	protected void init() {

		lblRequestTime.setMaxHeight(Double.MAX_VALUE);
		choWaitItems = new ChoiceBox<>(FXCollections.observableArrayList("", "5", "10", "15", "20", "25", "30"));

		Button btnReload = new Button("Reload");

		HBox hboxItems = new HBox(5, /* choWaitItems, new Label("단위 (초)"), chkTimer, */btnReload, lblRequestTime);
		hboxItems.setAlignment(Pos.CENTER_LEFT);
		hboxItems.setPadding(new Insets(5));

		BorderPane borTop = new BorderPane();
		Button btnStart = new Button("시작");
		Button btnStop = new Button("중지");
		btnStart.setDisable(true);
		btnStop.setDisable(true);
		borTop.setTop(new TitledPane("Timer", new HBox(5, choWaitItems, btnStart, btnStop)));
		borTop.setCenter(hboxItems);
		this.getParent().setTop(borTop);

		btnReload.setOnAction(ev -> {
			reload();
		});

		choWaitItems.valueProperty().addListener((oba, o, n) -> {

			if (ValueUtil.isNotEmpty(n) && !isRecycle.get()) {
				btnStart.setDisable(false);
			} else {
				btnStart.setDisable(true);
			}
		});

		btnStart.setOnAction(e -> {
			isRecycle.set(true);
			service.restart();
			btnStart.setDisable(true);
			btnStop.setDisable(false);
		});
		btnStop.setOnAction(e -> {
			isRecycle.set(false);
			service.cancel();
			btnStart.setDisable(false);
			btnStop.setDisable(true);
		});

		isRecycle.addListener((oba, o, n) -> {
			if (n) {
				choWaitItems.setDisable(true);
			} else {
				choWaitItems.setDisable(false);
			}

		});

		defineService();

		try {
			service.setExecutor(gargoyleThreadExecutors);
			service.start();
		} catch (RejectedExecutionException e) {

			if (gargoyleThreadExecutors.isShutdown() || gargoyleThreadExecutors.isTerminated()) {
				gargoyleThreadExecutors = ExecutorDemons.newFixedThreadExecutor(REALTIME_SRCH_THREAD_POOL_NAME, 1);
				service.setExecutor(gargoyleThreadExecutors);
			}

			LOGGER.error(ValueUtil.toString(e));
			// One more time.
			defineService();
			service.start();

		}

		// reload();
	}

	private void defineService() {

		/*
		 * 비동기 실시간 검색어 조회 처리가 기술.
		 */
		service = new Service<List<RealtimeSearchVO>>() {

			@Override
			protected Task<List<RealtimeSearchVO>> createTask() {

				return new Task<List<RealtimeSearchVO>>() {

					@Override
					protected List<RealtimeSearchVO> call() throws Exception {
						List<RealtimeSearchVO> meta = Collections.emptyList();
						try {
							meta = NaverRealtimeSrchSupplier.getInstance().getMeta();
						} catch (Exception e) {
							DialogUtil.showExceptionDailog(e);
						}
						return meta;
					}
				};
			}

		};

		service.setOnCancelled(stat -> {

			if (State.CANCELLED == stat.getSource().getState()) {
				LOGGER.debug("Cancel Requested");
			}
		});
		service.setOnSucceeded(stat -> {
			applyResponseTime(DateUtil.getCurrentDateString());
			FlowCardComposite value = new FlowCardComposite();

			flowCardComposite.set(value);
			this.getParent().setCenter(flowCardComposite.get());
			FlowCardComposite tmp = flowCardComposite.get();
			ObservableList<Node> flowChildrens = tmp.getFlowChildrens();
			tmp.setLimitColumn(20);

			data.setAll((List<RealtimeSearchVO>) stat.getSource().getValue());

			List<VBox> collect = data.stream().map(nodeConverter::apply).flatMap(v -> v.stream()).collect(Collectors.toList());
			flowChildrens.setAll(collect);

			if (isRecycle.get()) {

				WaitThread waitThread = new WaitThread(THREAD_RUNNER_GROUP, choWaitItems.getValue()) {

					@Override
					public boolean isContinue() {
						return isRecycle.get();
					}

					@Override
					public void execute() {
						Platform.runLater(() -> {
							if (isContinue())
								service.restart();
						});

					}

					@Override
					public boolean isRecycle() {
						return isRecycle.get();
					}

				};

				waitThread.setDaemon(true);
				waitThread.start();
			}

		});
	}

	private final static ThreadGroup THREAD_RUNNER_GROUP = new ThreadGroup("nrch-wait-thread-group");

	abstract class WaitThread extends Thread {

		private String waitSecond;

		public WaitThread(ThreadGroup group, String name) {
			super(group, name);
		}

		public WaitThread(String waitSecond) {
			super(THREAD_RUNNER_GROUP, "Nrch-Wait-Thread");
			this.waitSecond = waitSecond;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {

			if (ValueUtil.isEmpty(waitSecond))
				return;

			// while (isRecycle()) {
			int ws = Integer.parseInt(waitSecond, 10);
			for (int i = 0; i < ws; i++) {
				if (isContinue()) {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}
				}

			}
			execute();
			// }

		}

		public abstract boolean isContinue();

		public abstract boolean isRecycle();

		public abstract void execute();
	}

	/**
	 * 실시간검색어 조회 완료된 시각이 라벨에 입력됨.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 22.
	 */
	private void applyResponseTime(String dateTimeString) {
		lblRequestTime.setText(String.format("조회 완료 시간 : %s", dateTimeString));
	}

	/**
	 * 실시간 검색어 데이터 리턴
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 21.
	 * @return
	 */
	public ObservableList<RealtimeSearchVO> getData() {
		return FXCollections.unmodifiableObservableList(data);
	}

	/**
	 *
	 * JFXMasonryPane의 버그때문에 reload의 경우 - 새로 JFXMasonryPane을 만드는 작업을 처리함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 21.
	 */
	protected void reload() {

		if (gargoyleThreadExecutors.isShutdown() || gargoyleThreadExecutors.isTerminated()) {

			if (service.isRunning()) {
				service.cancel();
				gargoyleThreadExecutors.shutdown();
			}

			service.setExecutor(gargoyleThreadExecutors = ExecutorDemons.newFixedThreadExecutor(REALTIME_SRCH_THREAD_POOL_NAME, 1));
		}

		if (service.isRunning()) {
			return;
		}

		if (State.SUCCEEDED == service.getState()) {
			service.restart();
		} else {
			service.start();
		}
	}

	private String getDefaultColor(int i) {
		String color = "#B5E61D";
		switch (i) {
		case 0:
			color = "#EBCA2F"; // "#8F3F7E";
			break;
		case 1:
			color = "#B5305F";
			break;
		case 2:
			color = "#CE584A";
			break;
		case 3:
			color = "#DB8D5C";
			break;
		case 4:
			color = "#E5B495";
			break;
		case 5:
			color = "#E9AB44";
			break;
		case 6:
			color = "#FEE435";
			break;
		case 7:
			color = "#99C286";
			break;
		case 8:
			color = "#01A05E";
			break;
		case 9:
			color = "#4A8895";
			break;
		case 10:
			color = "#01DBEF";
			break;
		case 11:
			color = "#6C940B";
			break;
		case 12:
			color = "#4E6A9C";
			break;
		default:
			break;
		}
		return color;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.kyj.fx.voeditor.visual.main.layout.CloseableParent#close()
	 */
	@Override
	public void close() throws IOException {
		if (service != null) {
			isRecycle.set(false);
			gargoyleThreadExecutors.shutdown();
		}

	}

}
