/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.pmd
 *	작성일   : 2016. 9. 30.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.pmd;

import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 *
 */
public class DesignerFxComposite extends BorderPane {

	private DesignerFx designerFx;

	/**
	 *
	 */
	public DesignerFxComposite() {
		designerFx = new DesignerFx(null);
		setTop(designerFx.getFxMenuBar());
		setCenter(designerFx.getJSplitPane());

	}
}
