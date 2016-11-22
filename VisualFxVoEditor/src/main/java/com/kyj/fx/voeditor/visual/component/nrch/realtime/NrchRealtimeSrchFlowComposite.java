/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2016. 11. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.nrch.realtime;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.kyj.fx.voeditor.visual.component.FlowCardComposite;
import com.kyj.fx.voeditor.visual.component.google.trend.GoogleTrendComposite;
import com.kyj.fx.voeditor.visual.framework.RealtimeSearchItemVO;
import com.kyj.fx.voeditor.visual.framework.RealtimeSearchVO;
import com.kyj.fx.voeditor.visual.framework.thread.ExecutorDemons;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;
import com.kyj.fx.voeditor.visual.suppliers.NaverRealtimeSrchSupplier;
import com.kyj.fx.voeditor.visual.util.DateUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.beans.property.ObjectProperty;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author KYJ
 *
 */
public class NrchRealtimeSrchFlowComposite extends BorderPane {

	private AtomicInteger atomicInteger = new AtomicInteger(0);
	//	private ChoiceBox<String> choWaitItems;

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
					FxUtil.openBrowser(this, "https:" + link);
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
		super();
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
						contextMenu.getItems().add(menuGoogleTrend);
						contextMenu.show(this.getScene().getWindow(), ev.getScreenX(), ev.getScreenY());
					}
				}

			}
		});

	}

	/**
	 * 구글 트랜드로 조회
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

	private ObjectProperty<FlowCardComposite> flowCardComposite = new SimpleObjectProperty<>();

	private static final ExecutorService newFixedThreadExecutor = ExecutorDemons.newFixedThreadExecutor(1);

	private Service<List<RealtimeSearchVO>> service;

	private ObservableList<RealtimeSearchVO> data = FXCollections.observableArrayList();

	protected void init() {

		Label lblRequestTime = new Label();
		lblRequestTime.setMaxHeight(Double.MAX_VALUE);
		//		choWaitItems = new ChoiceBox<>(FXCollections.observableArrayList("", "5", "10", "15", "20", "25", "30"));

		Button btnReload = new Button("Reload");

		//		CheckBox chkTimer = new CheckBox("타이머 사용");
		//		chkTimer.selectedProperty().addListener((oba, o, n) -> {
		//			btnReload.setDisable(n.booleanValue());
		//		});
		//		chkTimer.setDisable(true);

		HBox hboxItems = new HBox(5, /*choWaitItems, new Label("단위 (초)"), chkTimer, */btnReload, lblRequestTime);
		hboxItems.setAlignment(Pos.CENTER_LEFT);
		hboxItems.setPadding(new Insets(5));
		setTop(hboxItems);

		btnReload.setOnAction(ev -> {
			reload();
		});

		service = new Service<List<RealtimeSearchVO>>() {

			@Override
			protected Task<List<RealtimeSearchVO>> createTask() {

				return new Task<List<RealtimeSearchVO>>() {

					@Override
					protected List<RealtimeSearchVO> call() throws Exception {
						return NaverRealtimeSrchSupplier.getInstance().getMeta();
					}
				};
			}

		};

		service.setOnSucceeded(stat -> {

			lblRequestTime.setText(String.format("조회 완료 시간 : %s", DateUtil.getCurrentDateString()));
			FlowCardComposite value = new FlowCardComposite();

			flowCardComposite.set(value);
			setCenter(flowCardComposite.get());
			FlowCardComposite tmp = flowCardComposite.get();
			ObservableList<Node> flowChildrens = tmp.getFlowChildrens();
			tmp.setLimitColumn(20);

			data.setAll((List<RealtimeSearchVO>) stat.getSource().getValue());

			List<VBox> collect = data.stream().map(nodeConverter::apply).flatMap(v -> v.stream()).collect(Collectors.toList());
			flowChildrens.setAll(collect);

			//			if (chkTimer.isSelected()) {
			//				String selectedItem = choWaitItems.getSelectionModel().getSelectedItem();
			//				if (ValueUtil.isNotEmpty(selectedItem)) {
			//					int waitMills = Integer.parseInt(selectedItem);
			//					int waitSecond = waitMills * 1000;
			//					if (waitSecond >= 1000) {
			//
			//						try {
			//							Thread.sleep(waitSecond);
			//							System.out.println("Reload");
			//							service.restart();
			//						} catch (Exception e) {
			//							e.printStackTrace();
			//						}
			//					}
			//
			//				}
			//			}
		});

		service.setExecutor(newFixedThreadExecutor);
		service.start();

		//		choWaitItems.setOnAction(ev -> {
		//			String selectedItem = choWaitItems.getSelectionModel().getSelectedItem();
		//			if (ValueUtil.isNotEmpty(selectedItem)) {
		//				chkTimer.setDisable(false);
		//			} else {
		//				chkTimer.setDisable(true);
		//				chkTimer.setSelected(false);
		//			}
		//		});

		reload();
	}

	/**
	 * 실시간 검색어 데이터 리턴
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
		if (service.isRunning())
			return;
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
}
