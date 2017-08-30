/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.capture
 *	작성일   : 2017. 8. 30.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.capture;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

/**
 * @author KYJ
 *
 */
public class ErdBaseTemplateController {

	@FXML
	private Label lblTableName;
	@FXML
	private ListView<String> lvColumns;

	@FXML
	public void initialize() {

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 8. 30.
	 * @return
	 */
	public ListView<String> getLvColumns() {
		return this.lvColumns;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 8. 30. 
	 * @param tableName
	 */
	public void setTableName(String tableName) {
		this.lblTableName.setText(tableName);

	}

}
