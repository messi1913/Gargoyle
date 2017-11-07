/********************************
 *	프로젝트 : pass-batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.module.main.reg.service
 *	작성일   : 2016. 4. 8.
 *	프로젝트 : BATCH 프로젝트
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.loader.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.voeditor.visual.framework.loader.core.RegistItem;
import com.kyj.fx.voeditor.visual.framework.loader.events.MakeBusinessEventConst;
import com.kyj.fx.voeditor.visual.framework.loader.events.MakeBusinessFrameOnCancelEvent;
import com.kyj.fx.voeditor.visual.framework.loader.events.MakeBusinessFrameOnFinishEvent;
import com.kyj.fx.voeditor.visual.framework.loader.events.MakeBusinessFrameOnMenuClickActivateEvent;
import com.kyj.fx.voeditor.visual.framework.loader.events.MakeBusinessFrameOnNextEvent;
import com.kyj.fx.voeditor.visual.framework.loader.events.MakeBusinessFrameOnPrevEvent;
import com.kyj.fx.voeditor.visual.framework.loader.events.MakeBusinessFrameOnRegisterSuccessEvent;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ListExpresion;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * 트리거 등록 프레임.
 *
 * @author KYJ
 *
 */
@FXMLController(value = "MakeBusinessFrameView.fxml", isSelfController = true)
public class MakeBusinessFrameComposite extends BorderPane implements MakeBusinessEventConst {

	private static Logger LOGGER = LoggerFactory.getLogger(MakeBusinessFrameComposite.class);

	/**
	 * @최초생성일 2016. 9. 22.
	 */
	private static final String DEFAULT_TITLE = "";

	@FXML
	public HBox vbList;

	@FXML
	private Label lblTitle;
	@FXML
	private Button btnPrev;
	@FXML
	private Button btnNext;
	@FXML
	private Button btnCancel;
	@FXML
	private Button btnFinish;

	/**
	 *
	 * @최초생성일 2016. 4. 11.
	 */
	private List<Map<String, Object>> properties;

	private ObservableList<RegistItem> items = FXCollections.observableArrayList();

	private final ObjectProperty<RegistItem> currentPage = new SimpleObjectProperty<RegistItem>();

	private static final ObservableList<MakeBusinessFrameOnRegisterSuccessEvent> successEventList = FXCollections.observableArrayList();

	/**
	 * 어플리케이션 타이틀
	 *
	 * @최초생성일 2016. 4. 26.
	 */
	private StringProperty titleProperty = new SimpleStringProperty("");

	private ReadOnlyObjectWrapper<Object> userParam;

	public MakeBusinessFrameComposite() throws IOException {
		this(DEFAULT_TITLE, null);

	}

	public MakeBusinessFrameComposite(String title) throws IOException {
		this(title, null);
	}

	public MakeBusinessFrameComposite(Object userParam) throws IOException {
		this(DEFAULT_TITLE, userParam);
	}

	public MakeBusinessFrameComposite(String title, Object userParam) throws IOException {
		// FXMLLoader loader = new FXMLLoader();
		// loader.setLocation(MakeBusinessFrameComposite.class.getResource("MakeTriggerFrameView.fxml"));
		// loader.setRoot(this);
		// loader.setController(this);
		// loader.load();

		FxUtil.loadRoot(MakeBusinessFrameComposite.class, this, err -> {
			LOGGER.error(ValueUtil.toString(err));
		});

		properties = new ArrayList<>();
		titleProperty.set(title);
		this.userParam = new ReadOnlyObjectWrapper<Object>(userParam);
		this.currentPage.addListener(currentPageChangeListener);
	}

	@FXML
	public void initialize() {
		items.addListener(itemChangeListener);
		btnFinish.setDisable(true);
		btnNext.setDisable(true);
		btnPrev.setDisable(true);
		lblTitle.setText(titleProperty.get());

		lblTitle.textProperty().bind(titleProperty);
	}

	/**
	 *
	 * @최초생성일 2016. 4. 11.
	 */
	private final ChangeListener<RegistItem> currentPageChangeListener = (oba, o, newRegistItem) -> {
		Parent node = newRegistItem.getNode();
		// Object controller = newRegistItem.getController();
		if (node != null) {
			setCenter(node);

			updateButtons();

			if (node instanceof MakeBusinessFrameOnMenuClickActivateEvent) {
				MakeBusinessFrameOnMenuClickActivateEvent _onloadEvent = (MakeBusinessFrameOnMenuClickActivateEvent) node;
				LOGGER.debug("call onActive ");
				int indexOf = items.indexOf(newRegistItem);

				Map<String, Object> map = properties.get(indexOf);

				// 이전페이지정보 set
				ListExpresion.containsAction(properties, indexOf - 1, v -> {
					map.put(MakeBusinessEventConst.COMMONS_TRIGGER_PREV_PAGE, node);
				});
				// 현재 페이지정보 set
				ListExpresion.containsAction(properties, indexOf, v -> {
					map.put(MakeBusinessEventConst.COMMONS_TRIGGER_CURRENT_PAGE, node);
				});
				// 이후 페이지정보 set
				ListExpresion.containsAction(properties, indexOf + 1, v -> {
					map.put(MakeBusinessEventConst.COMMONS_TRIGGER_NEXT_PAGE, node);
				});

				// 활성상태에서 사용자 정의 파라미터를 onActive()이벤트 활성화시 set해준다.
				map.put(MakeBusinessEventConst.COMMONS_USER_PARAM, userParam.get());

				Map<String, Object> containsMapper = ListExpresion.containsMapper(properties, indexOf - 1, v -> v, v -> null);
				_onloadEvent.onActive(containsMapper, /* properties.get(indexOf) */ map);

			}

		}
	};

	/**
	 * 새로운 노드가 추가되는경우 처리.
	 *
	 * @최초생성일 2016. 4. 8.
	 */
	private ListChangeListener<RegistItem> itemChangeListener = (ListChangeListener<RegistItem>) c -> {
		if (c.next()) {
			ObservableList<? extends RegistItem> list = c.getList();

			if (list == null || list.isEmpty()) {
				return;
			}

			if (c.wasAdded()) {
				addAction(c.getAddedSubList());
			} else if (c.wasRemoved()) {
				removeAction(c.getRemoved());
			} else if (c.wasUpdated()) {

			}

			updateButtons();
		}
	};

	/**
	 * 버튼 활성화/비활성화 처리
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 25.
	 */
	private void updateButtons() {
		int indexOf = items.indexOf(currentPage.get());
		int MAX = items.size();

		boolean isLastPage = false;
		boolean isFirstPage = false;

		// invalide page
		if (indexOf == -1) {
			btnNext.setDisable(true);
			btnFinish.setDisable(true);
			btnPrev.setDisable(true);
			return;
		}
		/* last page */
		else if (MAX == indexOf + 1) {
			isLastPage = true;
		}
		/* first page */
		else if (indexOf == 0) {
			isFirstPage = true;
		}

		if (isFirstPage && isLastPage) {
			btnPrev.setDisable(true);
			btnFinish.setDisable(false);
			btnNext.setDisable(true);
		} else if (isLastPage) {
			btnPrev.setDisable(false);
			btnFinish.setDisable(false);
			btnNext.setDisable(true);
		} else if (isFirstPage) {
			btnPrev.setDisable(true);
			btnFinish.setDisable(true);
			btnNext.setDisable(false);
		} else {
			btnPrev.setDisable(false);
			btnFinish.setDisable(true);
			btnNext.setDisable(false);
		}

	}

	/**
	 * 아이템이 추가되는 경우 이벤트처리.
	 *
	 * 이미 등록된 id인지 체크. 모든 아이템의 중복이 없는경우
	 *
	 * UI에 바인딩.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 8.
	 * @param list
	 */
	private void addAction(List<? extends RegistItem> list) {
		ObservableList<Node> children = vbList.getChildren();
		List<Node> newItemList = new ArrayList<Node>();
		list.forEach(item -> {
			String id = item.getId();

			if (ValueUtil.isEmpty(id))
				throw new RuntimeException("id is empty... ");

			if (item.getNode() == null)
				throw new RuntimeException("node is empty... ");

			if (ListExpresion.contains(children, i -> i.getId().equals(id)))
				throw new RuntimeException("already exists item.... id ::: " + id);

			properties.add(new HashMap<String, Object>());
			newItemList.add(createNewLabel(item));
		});

		//
		boolean checkBeforeEmpty = false;
		if (children.isEmpty()) {
			checkBeforeEmpty = true;
		}
		children.addAll(newItemList);

		if (checkBeforeEmpty && !children.isEmpty()) {
			LOGGER.debug("First Item show");
			Node node = children.get(0);
			currentPageStyling(node);

			RegistItem userData = (RegistItem) node.getUserData();
			currentPage.set(userData);
		}
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 9.
	 * @param current
	 */
	protected void currentPageStyling(Node current) {

		vbList.getChildren().forEach(v -> {

			if (v == current) {
				v.getStyleClass().add("trigger-label-menu-title-active");
				// v.setStyle("-fx-fill:#E18542");
			} else {
				v.getStyleClass().remove("trigger-label-menu-title-active");
				v.getStyleClass().add("trigger-label-menu-title");
			}

		});
	}

	/**
	 * 새로운 리벨항목등록.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 11.
	 * @param item
	 * @return
	 */
	private Label createNewLabel(RegistItem item) {
		Label e = new Label(item.getTitle());
		e.setId(item.getId());
		e.setUserData(item);
		e.getStyleClass().add("trigger-label-menu-title");
		e.setOnMouseClicked(ev -> {
			if (ev.getButton() == MouseButton.PRIMARY && ev.getClickCount() == 2) {
				RegistItem tv = (RegistItem) e.getUserData();
				if (tv != null)
					currentPage.set(tv);

			}
		});

		return e;
	}

	/**
	 * 타이틀을 등록.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 14.
	 * @param title
	 */
	public void setTitle(String title) {
		titleProperty.set(title);
	}

	/**
	 * 아이템이 삭제되는경우 이벤트처리
	 *
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 8.
	 * @param list
	 */
	private void removeAction(List<? extends RegistItem> list) {

		final List<Node> removeItems = new ArrayList<>();
		for (RegistItem item : list) {
			vbList.getChildren().stream().filter(n -> item.getId().equals(n.getId())).findFirst().ifPresent(n -> {
				removeItems.add(n);
			});
		}
		vbList.getChildren().removeAll(removeItems);
	}

	public boolean addItem(RegistItem item) {
		return items.add(item);
	}

	public boolean addAllItem(List<RegistItem> item) {
		return items.addAll(item);
	}

	public ObservableList<RegistItem> getItems() {
		return items;
	}

	/**
	 * prev 클릭 이벤트 처리.
	 *
	 * @최초생성일 2016. 4. 11.
	 */
	private final MakeBusinessFrameOnPrevEvent onPrevEvent = new MakeBusinessFrameOnPrevEvent() {

		@Override
		public void onPrev(Map<String, Object> properties) {
			RegistItem o = currentPage.get();
			int indexOf = items.indexOf(o);

			ListExpresion.containsAction(items, --indexOf, t -> currentPage.set(t));
			// RegistItem next = items.get(indexOf);

			Object c = o.getNode();
			if (c instanceof MakeBusinessFrameOnPrevEvent) {
				MakeBusinessFrameOnPrevEvent event = (MakeBusinessFrameOnPrevEvent) o.getNode();
				event.onPrev(properties);
				currentPageStyling(vbList.getChildren().get(indexOf));
				// if (c instanceof MakeTriggerFrameOnMenuClickActivateEvent) {
				// Map<String, Object> map = MakeTriggerFrameComposite.this.properties.get(indexOf);
				// ((MakeTriggerFrameOnMenuClickActivateEvent) c).onActive(properties, map);
				// }
			}
		}
	};

	/**
	 * next 클릭 이벤트 처리
	 *
	 * @최초생성일 2016. 4. 11.
	 */
	private final MakeBusinessFrameOnNextEvent onNextEvent = new MakeBusinessFrameOnNextEvent() {

		@Override
		public boolean onBeforeNext(Map<String, Object> properties) {
			boolean canNextPage = true;
			RegistItem o = currentPage.get();
			int indexOf = items.indexOf(o);

			if (o.getNode() instanceof MakeBusinessFrameOnNextEvent) {
				MakeBusinessFrameOnNextEvent event = (MakeBusinessFrameOnNextEvent) o.getNode();
				canNextPage = event.onBeforeNext(properties);
			}

			Consumer<RegistItem> consumer = null;
			if (canNextPage) {
				consumer = t -> currentPage.set(t);
				currentPageStyling(vbList.getChildren().get(indexOf + 1));
			}

			else {
				consumer = t -> {
				};
			}

			ListExpresion.containsAction(items, ++indexOf, consumer);

			return canNextPage;
		}
	};

	/**
	 * 취소 클릭 이벤트 처리
	 *
	 * @최초생성일 2016. 4. 11.
	 */
	private MakeBusinessFrameOnCancelEvent onCancelEvent = new MakeBusinessFrameOnCancelEvent() {

		@Override
		public boolean onCancel(Map<String, Object> properties) {
			RegistItem o = currentPage.get();
			boolean closeFlag = true;
			if (o.getNode() instanceof MakeBusinessFrameOnCancelEvent) {
				MakeBusinessFrameOnCancelEvent event = (MakeBusinessFrameOnCancelEvent) o.getNode();
				closeFlag = event.onCancel(properties);
			}
			if (closeFlag) {
				closeComponent();
			}

			return false;
		}

	};

	@FXML
	public void btnPrevOnMouseClick() {
		int indexOf = items.indexOf(currentPage.get());
		onPrevEvent.onPrev(properties.get(indexOf));

	}

	@FXML
	public void btnNextOnMouseClick() {
		int indexOf = items.indexOf(currentPage.get());

		onNextEvent.onBeforeNext(properties.get(indexOf));

	}

	/**
	 * 컴포넌트 닫기 처리
	 * 
	 * @param processEnd
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 25.
	 */
	private void closeComponent() {
		this.closeComponent(END_TYPE.EXIT);
	}

	/**
	 * 컴포넌트 닫기 처리.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 11.
	 * @param processEnd
	 *            트리거 팝업의 닫기의 유형을 정의함.
	 *
	 */
	private void closeComponent(END_TYPE processEnd) {
		Scene scene = MakeBusinessFrameComposite.this.getScene();
		Stage window = (Stage) scene.getWindow();
		endTypeProperty.set(processEnd);
		window.close();
	}

	@FXML
	public void btnCancelOnMouseClick() {
		int indexOf = items.indexOf(currentPage.get());
		onCancelEvent.onCancel(properties.get(indexOf));

	}

	/**
	 * 어플리케이션이 종료되는 유형 정의 EXIT : 단순히 프로세스 종료상태인경우,
	 *
	 * PROCESS_END : 프로세스가 끝나지 처리되고 종료된 경우.
	 *
	 * @author KYJ
	 *
	 */
	public enum END_TYPE {
		NONE, EXIT, PROCESS_END
	}

	public ObjectProperty<END_TYPE> endTypeProperty = new SimpleObjectProperty<>(END_TYPE.NONE);

	@FXML
	public void btnFinishOnMouseClick() {
		ObservableList<RegistItem> tvItems = getItems();

		for (int i = 0; i < tvItems.size(); i++) {
			RegistItem v = tvItems.get(i);
			boolean closeFlag = false;
			Object controller = v.getNode();
			if (controller instanceof MakeBusinessFrameOnFinishEvent) {
				MakeBusinessFrameOnFinishEvent makeTriggerFrameOnFinishEvent = (MakeBusinessFrameOnFinishEvent) controller;
				closeFlag = makeTriggerFrameOnFinishEvent.onFinish(properties);
			}

			if (closeFlag /* && (END == i) */) {
				closeComponent(END_TYPE.PROCESS_END);

				// 트리거작업이 성공적으로 끝났다는 이벤트를 호출한다.
				successEventList.forEach(ev -> {
					try {
						ev.onSuccess(MakeBusinessFrameComposite.this);
					} catch (Exception e) {
						// 이벤트 예외처리는 로깅만 처리.
						LOGGER.error(ValueUtil.toString(e));
					}

				});

			}
		}

	}

	/**
	 * 트리거 등록(데이터베이스처리)까지 성공적으로 처리되는 경우 호출되는 이벤트에 대한 등록
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 10.
	 * @param event
	 * @return
	 */
	public boolean addMakeTriggerFrameOnRegisterSuccessEvent(MakeBusinessFrameOnRegisterSuccessEvent event) {
		return successEventList.add(event);
	}

}
