/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.words.spec.ui.skin
 *	작성일   : 2016. 8. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.words.spec.auto.msword.ui.skin;

import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.scene.layout.BorderPane;

/***************************
 * 
 * 기타정의사항
 * 
 * @author KYJ
 *
 ***************************/
@FXMLController(value = "EtcDefineView.fxml", isSelfController = true)
public class EtcDefineComposite extends BorderPane {

	public EtcDefineComposite() throws Exception {
		FxUtil.loadRoot(EtcDefineComposite.class, this);
	}

}
