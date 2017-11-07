/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.file
 *	작성일   : 2016. 9. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.file;

import java.util.function.Consumer;

import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

/***************************
 * 
 * @author KYJ
 *
 ***************************/
@FXMLController(value = "AddResourceView.fxml", isSelfController = true)
public abstract class AbstractAddResource extends BorderPane implements Consumer<Exception> {

	@FXML
	private AnchorPane anchorFileCont;

	@FXML
	private AnchorPane anchorDirCont;

	@FXML
	private TabPane tabRoot;
	
	@FXML
	private RadioButton rdoFile;

	@FXML
	private RadioButton rdoDir;

	public AbstractAddResource() {
		FxUtil.loadRoot(AbstractAddResource.class, this, this);
	}

	@FXML
	public void initialize() {

		rdoFile.setOnAction(this::rdoOnAction);
		rdoDir.setOnAction(this::rdoOnAction);
		applyFileCont(anchorFileCont);
		applyDirCont(anchorDirCont);
	}

	public void rdoOnAction(ActionEvent e) {
		Object source = e.getSource();
		if (source == rdoFile) {
			
		} else if (source == rdoDir) {

		}

	}

	/**
	 * 로딩시 발생되는 어레처리에 대한 구현.
	 * 
	 * @inheritDoc
	 */
	public abstract void accept(Exception ex);

	/********************************
	 * 작성일 : 2016. 9. 4. 작성자 : KYJ
	 *
	 *
	 * @param anchorFileCont
	 ********************************/
	public abstract void applyFileCont(AnchorPane anchorFileCont);

	/********************************
	 * 작성일 : 2016. 9. 4. 작성자 : KYJ
	 *
	 *
	 * @param anchorDirCont
	 ********************************/
	public abstract void applyDirCont(AnchorPane anchorDirCont);

}
