/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.words.spec.ui.skin
 *	작성일   : 2016. 8. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.words.spec.auto.msword.ui.skin;



import com.kyj.fx.voeditor.visual.words.spec.auto.msword.ui.tabs.AbstractSpecTab;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.ui.tabs.SpecTabPane;

import javafx.scene.layout.BorderPane;

/***************************
 * 
 * 기타정의사항
 * 
 * @author KYJ
 *
 ***************************/

public class EtcDefineTabPane extends AbstractSpecTab {

	public EtcDefineTabPane(String title, SpecTabPane specTabPane) throws Exception {
		super(title, specTabPane);
	}

	//	public EtcDefineComposite(String string, SpecTabPane specTabPane) throws Exception {
	//	
	//		FxUtil.loadRoot(EtcDefineComposite.class, this);
	//	}

	@Override
	public BorderPane supplyNode() throws Exception {
		return new EtcDefineComposite();
	}

}
