/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2016. 4. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.scm;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.functions.SVNCreateFunction;
import com.kyj.fx.voeditor.visual.functions.SVNModifyFunction;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.scm.manager.core.commons.SCMKeywords;
import com.kyj.scm.manager.svn.java.JavaSVNManager;

import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * SVN 로케이션 추가 설정.
 *
 * @author KYJ
 *
 */
public class AddNewSVNRepositoryController implements SCMKeywords {

	private static Logger LOGGER = LoggerFactory.getLogger(AddNewSVNRepositoryController.class);

	private Stage stage;

	@FXML
	private TextField lblUrl;

	@FXML
	private TextField txtUserId;

	@FXML
	private PasswordField lblUserPass;

	@FXML
	private Button btnFinish;

	@FXML
	private CheckBox chkEmptyUserAndPass;

	/**
	 * 텍스트필드값의 항목이 모두 채워졌는지 확인하는 필드
	 *
	 * @최초생성일 2016. 4. 4.
	 */
	private BooleanProperty isNotEmptyFields = new SimpleBooleanProperty(true);

	private ChangeListener<Boolean> checkIsNotEmptyFieldChangeListener = (observable, oldValue, newValue) -> btnFinish
			.setDisable(chkEmptyUserAndPass.isSelected() ? false : !newValue);

	private ObjectProperty<SVNCreateFunction> saveFunction = new SimpleObjectProperty<>(new SVNCreateFunction());

	private ObjectProperty<Properties> result = new SimpleObjectProperty<>();

	@FXML
	public void initialize() {

		// lblUrl.textProperty().isNotEmpty() &&
		// txtUserId.textProperty().isNotEmpty() &&
		// lblUserPass.textProperty().isNotEmpty()

		BooleanExpression checkFieldsBinding = BooleanExpression.booleanExpression(lblUrl.textProperty().isNotEmpty())
				.and(txtUserId.textProperty().isNotEmpty()).and(lblUserPass.textProperty().isNotEmpty());

		// .booleanExpression(txtUserId.textProperty().isNotEmpty()).booleanExpression(lblUserPass.textProperty().isNotEmpty());
		isNotEmptyFields.bind(checkFieldsBinding);
		isNotEmptyFields.addListener(checkIsNotEmptyFieldChangeListener);

		chkEmptyUserAndPass.selectedProperty().addListener((oba, oldval, newval) -> {
			btnFinish.setDisable(!newval);
		});
	}

	/***********************************************************************************/
	/* 이벤트 구현 */
	@FXML
	public void btnFinishOnAction() {
		Properties properties = new Properties();
		properties.put(SVN_URL, lblUrl.getText());

		if (!txtUserId.getText().isEmpty()) {
			properties.put(SVN_USER_ID, txtUserId.getText());
		}

		if (!lblUserPass.getText().isEmpty()) {
			properties.put(SVN_USER_PASS, lblUserPass.getText());
		}

		if (saveFunction.get().isValide(properties)) {
			doSave(properties);
		} else {

			// Optional<Pair<String, String>> showYesOrNoDialog =
			// DialogUtil.showYesOrNoDialog("Duplicated URL.",
			// "Overrite ?? ");
			// showYesOrNoDialog.ifPresent(v -> {
			// if ("Y".equals(v.getValue())) {
			// doSave(properties);
			// }
			//
			// });
			DialogUtil.showMessageDialog(this.stage, "Duplicated URL.");
			return;
		}

	}

	/**
	 * 입력파라미터로 받아온 properties를 저장처리함.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 9.
	 * @param properties
	 * @return
	 */
	private void doSave(Properties properties) {
		boolean isSaveComplete = saveFunction.get().apply(properties);
		if (isSaveComplete) {
			result.set(properties);
			DialogUtil.showMessageDialog(this.stage, "save Complete!");
			close();
		} else {
			DialogUtil.showMessageDialog(this.stage, "save Fail....!");
		}
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 4.
	 * @return
	 */
	public Properties getResult() {
		return result.get();
	}

	/**
	 * 팝업을 닫는다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 4.
	 */
	@FXML
	public void btnCancelOnAction() {
		close();
	}

	public void close() {
		if (stage != null)
			stage.close();
	}

	/***********************************************************************************/

	/***********************************************************************************/
	/* 일반API 구현 */

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setProperties(Properties prop) {

		if (prop != null) {

			String url = ValueUtil.decode(prop.get(JavaSVNManager.SVN_URL), "").toString();
			String id = ValueUtil.decode(prop.get(JavaSVNManager.SVN_USER_ID), "").toString();
			String pass = ValueUtil.decode(prop.get(JavaSVNManager.SVN_USER_PASS), "").toString();

			this.lblUrl.setText(url);
			this.txtUserId.setText(id);
			this.lblUserPass.setText(pass);

			this.saveFunction.set(new SVNModifyFunction());
		}

	}

	/***********************************************************************************/
}
