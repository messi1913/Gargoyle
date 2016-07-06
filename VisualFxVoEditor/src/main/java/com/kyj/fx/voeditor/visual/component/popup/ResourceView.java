/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2016. 6. 12.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.popup;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.ResultDialog;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;
import com.kyj.fx.voeditor.visual.momory.SkinManager;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxClipboardUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;

/***************************
 *
 * @author KYJ
 *
 ***************************/
public abstract class ResourceView<R> extends BorderPane {

	Logger LOGGER = LoggerFactory.getLogger(ResourceView.class);

	@FXML
	private MenuBar menuOptional;

	@FXML
	protected ListView<R> lvResources;

	@FXML
	private TextField txtFilter;
	@FXML
	private Button btnSelect;

	@FXML
	private Button btnCancel;

	private Stage stage = new Stage();

	// 자동완성기능인데 사용시 initialize함수와 함께 주석풀것
	// private AbstractFxVoAutoCompletionBinding<String> autoCompletionBinding;

	private List<R> resources;

	protected List<R> getResources() {
		return resources;
	}

	protected void setResources(List<R> loadClasses) {
		this.resources = loadClasses;
	}

	/**
	 * 팝업선택결과를 반환할 데이터 결과 객체 KYJ
	 */
	private ResultDialog<R> result = new ResultDialog<>();

	/**
	 * 사용자 정의 Consumer
	 *
	 * @최초생성일 2015. 10. 27.
	 */
	public ObjectProperty<Consumer<R>> consumer;

	@FXML
	private Button btnReflesh;

	@FXML
	private Label lblSubTitle;

	@FXML
	private Label txtStatus;

	/**
	 * 리소스 다이얼로그를 오픈한다.
	 *
	 * @throws Exception
	 */
	public ResourceView() throws Exception {
		this("");
	}

	protected String data;

	/**
	 * 리소스 다이얼로그를 오픈한다.
	 *
	 * @param data
	 *            다이얼로그를 오픈할때 텍스트필드에 기본값으로 셋팅할 텍스트
	 * @throws Exception
	 */
	public ResourceView(String data) throws Exception {
		this.data = data;
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(ResourceView.class.getResource("OpenClassResource.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		loader.load();

	}

	/********************************
	 * 작성일 : 2016. 6. 11. 작성자 : KYJ
	 *
	 * 값이 true인경우 일치라고 판단하여 데이터를 필터링해주게된다.
	 *
	 * @param value
	 * @param text
	 * @return
	 ********************************/
	public abstract boolean isMatch(R value, String text);

	/********************************
	 * 작성일 : 2016. 6. 11. 작성자 : KYJ
	 *
	 * ListView<R>에 표현할 converter를 구현한다.
	 *
	 * @return
	 ********************************/
	public abstract StringConverter<R> stringConverter();

	@FXML
	public void initialize() {

		if (data != null)
			txtFilter.setText(data);

		consumer = new SimpleObjectProperty<>();

		// 이벤트 리스너 등록 consumer가 변경될때마다 ResultDialog에 값을 set.
		consumer.addListener((oba, oldval, newval) -> {
			result.setConsumer(consumer.get());
		});

		resources = FXCollections.observableArrayList();

		lvResources.setCellFactory(TextFieldListCell.forListView(stringConverter()));

		/* Control + C 기능 적용 */

		lvResources.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {

			if (ev.isControlDown() && !ev.isShiftDown() && KeyCode.C == ev.getCode()) {
				R selectedItem = lvResources.getSelectionModel().getSelectedItem();
				String string = stringConverter().toString(selectedItem);
				FxClipboardUtil.putString(string);
				ev.consume();
			}

		});

		// lvResources.getItems().addAll(loadClasses);

		// 자동완성기능 사용시 주석풀것
		// autoCompletionBinding = getCompletionBinding();

		txtFilter.textProperty().addListener((oba, oldval, newval) -> {

			Platform.runLater(() -> {

				if (newval == null)
					return;

				lvResources.getItems().clear();

				List<R> collect = resources.stream().filter(r -> {
					return isMatch(r, newval);
				}).collect(Collectors.toList());

				lvResources.getItems().addAll(collect);
			});

		});

		btnReflesh.setOnMouseClicked(this::btnRefleshOnMouseClick);
		init();
	}

	protected abstract void init();

	/**
	 * 팝업호출
	 *
	 * @Date 2015. 10. 17.
	 * @return
	 * @User KYJ
	 */
	public ResultDialog<R> show() {
		return show(null, true);
	}

	/********************************
	 * 작성일 : 2016. 6. 12. 작성자 : KYJ
	 *
	 * 팝업호출
	 *
	 * @param modal
	 * @return
	 ********************************/
	public ResultDialog<R> show(boolean modal) {

		return show(null, modal);
	}

	/**
	 * 팝업호출
	 *
	 * @Date 2015. 10. 17.
	 * @return
	 * @User KYJ
	 */
	public ResultDialog<R> show(Window owner, boolean modal) {
		stage = new Stage();
		Scene scene = new Scene(this);
		scene.getStylesheets().add(SkinManager.getInstance().getSkin());
		stage.setScene(scene);

		stage.setTitle(titleProperty.get());

		// stage.setResizable(false);
		if (modal) {
			stage.initModality(Modality.APPLICATION_MODAL);
			if (owner != null) {
				stage.initOwner(owner);
			} else {
				stage.initOwner(SharedMemory.getPrimaryStage());
			}

			stage.showAndWait();
		} else {

			stage.show();
		}

		return result;
	}

	/**
	 * 창을 닫음
	 *
	 * @Date 2015. 10. 17.
	 * @User KYJ
	 */
	public void close() {
		this.stage.close();
	}

	/**
	 * 타이틀 정의.
	 *
	 * @최초생성일 2016. 6. 12.
	 */
	private StringProperty titleProperty = new SimpleStringProperty(getClass().getSimpleName());

	public String getTitle() {
		return titleProperty.get();
	}

	public void setTitle(String title) {
		titleProperty.set(title);
	}

	/**
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 27.
	 * @param event
	 */
	@FXML
	public void btnSelectOnMouseClick(MouseEvent event) {
		R selectedItem = lvResources.getSelectionModel().getSelectedItem();

		if (ValueUtil.isEmpty(selectedItem)) {
			DialogUtil.showMessageDialog("행을 선택하세요.");
			return;
		}

		result.setClickButton(btnSelect);
		result.setStatus(ResultDialog.SELECT);
		result.setData(selectedItem);
		close();
	}

	@FXML
	public void btnCancelOnMouseClick(MouseEvent event) {
		result.setClickButton(btnCancel);
		result.setStatus(ResultDialog.CANCEL);
		result.setData(null);
		close();
	}

	public abstract void btnRefleshOnMouseClick(MouseEvent event);

	@FXML
	public void lvResourcesMouseClick(MouseEvent event) {
		if (event.getClickCount() == 2) {
			btnSelectOnMouseClick(event);
		}
	}

	/**
	 * @return the consumer
	 */
	public Consumer<R> getConsumer() {
		return consumer.get();
	}

	/**
	 * @param consumer
	 *            the consumer to set
	 */
	public void setConsumer(Consumer<R> consumer) {
		this.consumer.set(consumer);
	}

	public final TextField getTxtFilter() {
		return txtFilter;
	}

	/**
	 * 상태바에 메세지를 남긴다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 20.
	 * @param message
	 */
	public void setStatusMessage(String message) {
		txtStatus.setText(message);
	}

	public void setSubTitle(String subTitle) {
		lblSubTitle.setText(subTitle);
	}

	/**
	 * 메뉴를 사용하고하는 화면에선 아래 MenuBar를 리턴받아 구현.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 22.
	 * @return
	 */
	public MenuBar getMenuOptional() {
		return menuOptional;
	}

	public Button getBtnCancel() {
		return btnCancel;
	}

	public Button getSelectButton() {
		return btnSelect;
	}

}
