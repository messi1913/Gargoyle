/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2016. 8. 23.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.popup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.momory.SkinManager;
import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import kyj.Fx.dao.wizard.core.util.ValueUtil;

/**
 * 에러내용을 보여주기 위한 뷰.
 *
 * @author KYJ
 *
 */
@FXMLController(value = "ExceptionDialog.fxml", isSelfController = true)
public class ExceptionDialogComposite extends BorderPane {

	private static Logger LOGGER = LoggerFactory.getLogger(ExceptionDialogComposite.class);

	/**
	 * 다이얼로그 제목
	 * @최초생성일 2016. 8. 23.
	 */
	private static final String EXCEPTION_DIALOG_TITLE = "ExceptionDialog";

	@FXML
	private Label lblHeader;

	@FXML
	private TextArea txtConent;
	@FXML
	private Button btnOk;
	@FXML
	private ImageView ivErr;

	private Throwable ex;

	private Stage stage;

	private String userContent;

	public ExceptionDialogComposite(Throwable ex, String conent) {
		try {
			this.ex = ex;
			this.userContent = "";
			FxUtil.loadRoot(ExceptionDialogComposite.class, this);
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}
	}

	public ExceptionDialogComposite(Throwable ex) {
		this(ex, "");
	}

	@FXML
	public void initialize() {

		this.ivErr.setImage(new Image(ClassLoader.getSystemResourceAsStream("META-INF/images/nodeicons/dialog-error.png")));

		if (this.ex != null) {
			String name = this.ex.getClass().getName();
			lblHeader.setText(name);
			if (this.userContent.isEmpty())
				txtConent.setText(ValueUtil.toString(this.ex));
			else
				txtConent.setText(this.userContent);
		}

	}

	/**
	 * 다이얼로그 OPEN.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 23.
	 * @param node
	 */
	public void show(Node node) {
		Window _root = null;
		if (node != null) {
			Scene _scene = node.getScene();
			if (_scene != null) {
				_root = _scene.getWindow();
			}
		}
		show(_root);
	}

	/**
	 * 다이얼로그 OPEN.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 23.
	 * @param root
	 */
	public void show(Window root) {

		stage = new Stage();
		stage.setTitle(EXCEPTION_DIALOG_TITLE);
		Scene scene = new Scene(this);
		scene.getStylesheets().add(SkinManager.getInstance().getSkin());
		stage.setScene(scene);

		stage.initOwner(root);
		stage.showAndWait();
	}

	@FXML
	public void btnOkOnAction() {
		if (this.stage != null)
			this.stage.close();
	}

}
