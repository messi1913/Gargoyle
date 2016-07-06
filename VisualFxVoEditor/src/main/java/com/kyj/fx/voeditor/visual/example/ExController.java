/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 6. 11.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "Ex.fxml")
public class ExController {
	@FXML
	private WebView wb;

	@FXML
	public void initialize() {
		wb.getEngine().load("http://localhost:8000/sos-server/chat.xhtml");
	}
	/**********************************************************************************************/
	/* 이벤트 처리항목 기술 */
	// TODO Auto-generated constructor stub
	/**********************************************************************************************/

	/**********************************************************************************************/
	/* 그 밖의 API 기술 */
	// TODO Auto-generated constructor stub
	/**********************************************************************************************/

}
