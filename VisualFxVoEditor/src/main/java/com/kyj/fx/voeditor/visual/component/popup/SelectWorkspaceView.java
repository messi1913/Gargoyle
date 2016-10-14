/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2015. 10. 17.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.popup;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.kyj.fx.voeditor.visual.component.ResultDialog;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;
import com.kyj.fx.voeditor.visual.momory.SkinManager;
import com.kyj.fx.voeditor.visual.util.DialogUtil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class SelectWorkspaceView extends AnchorPane {

	private Stage stage;

	/**
	 * OK버튼 OK버튼 클릭시 작업공간으로 선택됨.
	 */
	@FXML
	private Button btnOk;

	/**
	 * 취소버튼 작업종료
	 */
	@FXML
	private Button btnCancel;

	/**
	 * 워크스페이스 선택 브라우저
	 */
	@FXML
	private Button btnBrowse;

	/**
	 * 선택한 워크스페이스 디렉토리 경로 정보 KYJ
	 */
	@FXML
	private TextField txtWorkspace;
	/**
	 * 팝업선택결과를 반환할 데이터 결과 객체 KYJ
	 */
	private ResultDialog<Object> result;

	/**
	 *
	 * KYJ
	 *
	 * @throws IOException
	 */
	public SelectWorkspaceView() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("SelectWorkspaceView.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		loader.load();
	}

	/**
	 * 팝업호출
	 *
	 * @Date 2015. 10. 17.
	 * @return
	 * @User KYJ
	 */
	public ResultDialog<Object> showAndWait() {
		stage = new Stage();
		Scene scene = new Scene(this);
		scene.getStylesheets().add(SkinManager.getInstance().getSkin());
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setResizable(false);
		stage.initOwner(SharedMemory.getPrimaryStage());
		stage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				stage.close();
			}
		});
		stage.showAndWait();

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

	@FXML
	public void initialize() {

	}

	@FXML
	public void btnBrowseOnMouseClick(MouseEvent e) {
		File selectDirFile = DialogUtil.showDirectoryDialog(SharedMemory.getPrimaryStage());
		//NullException fix.
		if(selectDirFile!=null)
			txtWorkspace.setText(selectDirFile.getAbsolutePath());
	}

	@FXML
	public void btnOkOnMouseClick(MouseEvent e) {

		String workspaceDir = txtWorkspace.getText();

		if (workspaceDir == null || workspaceDir.isEmpty()) {
			DialogUtil.showMessageDialog("경로가 비어있습니다.");
			return;
		}

		File workspaceFile = new File(workspaceDir);

		if (workspaceFile.isFile()) {
			DialogUtil.showMessageDialog("파일의 유형이 잘못되었습니다. 디렉토리를 선택하세요.");
			return;
		}

		if (!workspaceFile.exists()) {
			try {
				FileUtils.forceMkdir(workspaceFile);
			} catch (IOException e1) {
				DialogUtil.showMessageDialog("디렉토리 생성에 실패하였습니다.");
				return;
			}
		}

		ResourceLoader.getInstance().put(ResourceLoader.BASE_DIR, workspaceFile.getAbsolutePath());

		result = new ResultDialog();
		result.setStatus(ResultDialog.OK);
		close();
	}

	/**
	 * 취소버튼 클릭 이벤트
	 *
	 * @Date 2015. 10. 17.
	 * @param e
	 * @User KYJ
	 */
	@FXML
	public void btnCancelOnMouseClick(MouseEvent e) {
		result = new ResultDialog();
		result.setStatus(ResultDialog.CANCEL);
		close();
	}
}
