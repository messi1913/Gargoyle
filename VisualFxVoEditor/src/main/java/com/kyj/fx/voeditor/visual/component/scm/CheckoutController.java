/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2016. 4. 26.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.scm;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNLogEntry;

import com.kyj.fx.voeditor.visual.main.layout.GagoyleTabProxy;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.NullExpresion;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/***************************
 *
 * 체크아웃 처리를 위한 뷰
 *
 * @author KYJ
 *
 ***************************/

public class CheckoutController extends BorderPane {
	private static Logger LOGGER = LoggerFactory.getLogger(CheckoutController.class);

	@FXML
	private BorderPane borRoot; // Value injected by FXMLLoader

	/**
	 * @최초생성일 2016. 4. 27.
	 */
	@FXML
	private TextField txtProjectName;

	@FXML
	private Label lblLocation, txtRevision;

	@FXML
	private CheckBox chkIsDefaultLocation;

	@FXML
	private TextField txtCheckoutLocation;

	@FXML
	private Button btnBrowse;

	@FXML
	private Button btnFinish;

	/**
	 * cancel버튼을 누르거나 checkout이 이상없이 처리된경우 처리하는 행위를 기술함.
	 *
	 * @최초생성일 2016. 5. 11.
	 */
	private ObjectProperty<Consumer<Void>> onCloseAction = new SimpleObjectProperty<>();
	/**
	 * 액션이 정상적으로 완료되는 기본적인 행위는 stage가 존재하면 stage를 닫음.
	 *
	 *
	 * @최초생성일 2016. 5. 11.
	 */
	private Consumer<Void> defaultCloseAction = t -> closeStage();

	private SVNItem svnItem;

	/**
	 * 프로젝트명
	 *
	 * @최초생성일 2016. 5. 10.
	 */
	private StringProperty fileName = new SimpleStringProperty();

	/**
	 * 휴효하지않는 상태인경우 btnFinish를 비활성화 시키고자하는 목적으로 사용
	 *
	 * @최초생성일 2016. 4. 27.
	 */
	private BooleanBinding btnFinishDisableBinding;

	/**
	 */
	public CheckoutController() {

	}

	@FXML
	public void initialize() {

		// 기본경로 처리
		txtCheckoutLocation.textProperty().set(getBaseDir());
		txtCheckoutLocation.disabledProperty().addListener((oba, old, newv) -> btnBrowse.setDisable(newv));

		// btnFinish 활성화/비활성화 처리를 담당한다.
		BooleanBinding empty1 = txtProjectName.textProperty().isEmpty();
		BooleanBinding empty2 = txtCheckoutLocation.textProperty().isEmpty();
		btnFinishDisableBinding = empty1.or(empty2);
		btnFinishDisableBinding.addListener((oba, old, newv) -> btnFinish.setDisable(newv));

		txtProjectName.textProperty().bind(fileName);

		/*
		 * checkout이 정상적으로 처리완료되거나 cancel버튼을 클릭한경우 행위를 기술한다.
		 *
		 * 디폴트값은 stage를 닫는다.
		 */

		onCloseAction.set(defaultCloseAction);

		advanceOption.addListener(new ChangeListener<SVNAdvanceOption>() {

			@Override
			public void changed(ObservableValue<? extends SVNAdvanceOption> observable, SVNAdvanceOption oldValue,
					SVNAdvanceOption newValue) {
				if (newValue != null) {
					txtRevision.setText(String.valueOf(newValue.getRevision()));
				}
			}
		});
	}

	/********************************
	 * 작성일 : 2016. 4. 27. 작성자 : KYJ
	 *
	 * 프로젝트를 선택했던 기본경로 리턴
	 *
	 * @return
	 ********************************/
	private String getBaseDir() {
		return ResourceLoader.getInstance().get(ResourceLoader.BASE_DIR);
	}

	// ***** Event 함수 *************************************************/

	/********************************
	 * 작성일 : 2016. 4. 27. 작성자 : KYJ
	 *
	 * 디폴트 경로 체크 이벤트
	 *
	 * @param e
	 ********************************/
	@FXML
	public void chkIsDefaultLocationOnAction(ActionEvent e) {

		if (chkIsDefaultLocation.isSelected()) {
			txtCheckoutLocation.setText(getBaseDir());
			txtCheckoutLocation.setDisable(true);
		} else {
			txtCheckoutLocation.setDisable(false);
		}
	}

	/********************************
	 * 작성일 : 2016. 4. 27. 작성자 : KYJ
	 *
	 * Finish 버튼 클릭했을때 이벤트 처리
	 *
	 ********************************/
	@FXML
	public void btnFinishOnAction() {
		try {
			String path = svnItem.getPath();

			File outDir = new File(txtCheckoutLocation.getText() + File.separator + txtProjectName.getText());
			if (!outDir.exists())
				outDir.mkdirs();
			LOGGER.debug(path);
			LOGGER.debug(outDir.toString());

			String revision = null;
			SVNAdvanceOption svnAdvanceOption = advanceOption.get();
			if (svnAdvanceOption != null)
				revision = String.valueOf(svnAdvanceOption.getRevision());

			long checkoutCount = -1;
			if (ValueUtil.isNotEmpty(revision)) {
				checkoutCount = svnItem.getManager().checkout(path, revision, outDir);
			} else {
				checkoutCount = svnItem.getManager().checkout(path, outDir);
			}
			LOGGER.debug("Checkout Result ::: " + checkoutCount);
			GagoyleTabProxy.getInstance().refleshWorkspaceTree();

			closeStage();
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}
	}

	/**
	 * checkout받을 디렉토리를 지정.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 5. 10.
	 */
	@FXML
	public void btnBrowseOnAction() {
		File selectedDir = DialogUtil.showDirectoryDialog(SharedMemory.getPrimaryStage(), option -> {
			option.setTitle("Select Checkout Dir.");
			File value = new File(txtCheckoutLocation.getText());
			if (!value.exists()) {
				DialogUtil.showMessageDialog(SharedMemory.getPrimaryStage(), "does not exists directory.");
				option.setInitialDirectory(new File(getBaseDir()));
			} else
				option.setInitialDirectory(value);
		});

		if (selectedDir != null && selectedDir.exists()) {
			txtCheckoutLocation.setText(selectedDir.getAbsolutePath());
		}
	}

	/********************************
	 * 작성일 : 2016. 4. 27. 작성자 : KYJ
	 *
	 * Cancel버튼을 클릭한경우 이벤트 처리
	 *
	 ********************************/
	@FXML
	public void btnCancelOnAction() {

		// TODO 종료시 뭔가 파라미터를 넣고 싶은경우는 제네릭해제해서 사용할것
		NullExpresion.ifNotNullDo(onCloseAction.get(), action -> action.accept(null));
	}

	// ****** setter getter **************************************/

	public ObjectProperty<Consumer<Void>> onCloseActionProperty() {
		return this.onCloseAction;
	}

	public Consumer<?> getOnCloseAction() {
		return this.onCloseActionProperty().get();
	}

	public void setOnCloseAction(final Consumer<Void> onCloseAction) {
		this.onCloseActionProperty().set(onCloseAction);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 5. 10.
	 * @return
	 */
	public final StringProperty fileNameProperty() {
		return this.fileName;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 5. 10.
	 * @return
	 */
	public final java.lang.String getFileName() {
		return this.fileNameProperty().get();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 5. 10.
	 * @param fileName
	 */
	public final void setFileName(final java.lang.String fileName) {
		this.fileNameProperty().set(fileName);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 5. 10.
	 * @param value
	 */
	public void setSVNItem(SVNItem value) {
		this.svnItem = value;
	}

	/**
	 * owner가 존재하는 경우 stage를 다는다,
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 5. 11.
	 */
	public void closeStage() {

		Window window = borRoot.getScene().getWindow();

		// stage가 존재한다면 stage를 닫는 행위를 한다.
		NullExpresion.ifNotNullDo(window, win -> {
			Stage stage = (Stage) win;
			stage.close();
		});
	}

	private ObjectProperty<SVNAdvanceOption> advanceOption = new SimpleObjectProperty<>();

	@FXML
	public void btnAdvanceOnAction() {

		SVNAdvanceComposite svnAdvanceComposite = new SVNAdvanceComposite(svnItem) {

			@Override
			public String getAuthor(int revision, List<SVNLogEntry> entries) {
				return entries.get(0).getAuthor();
			}

			@Override
			public String getMessage(int revision, List<SVNLogEntry> entries) {
				return entries.get(0).getMessage();
			}

			@Override
			protected void onSave() {
				Stage window = (Stage) FxUtil.getWindow(this);
				window.close();
			}
		};

		FxUtil.createStageAndShowAndWait(svnAdvanceComposite, stage -> {
			stage.initOwner(SharedMemory.getPrimaryStage());
			stage.setResizable(false);
			stage.initModality(Modality.APPLICATION_MODAL);
		});

		SVNAdvanceOption value = svnAdvanceComposite.getValue();
		advanceOption.set(value);
	}
}
