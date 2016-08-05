/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.words.spec.ui.controls
 *	작성일   : 2016. 2. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.words.spec.ui.tabs;

import com.kyj.fx.voeditor.visual.framework.SupplySkin;
import com.kyj.fx.voeditor.visual.words.spec.resources.SpecResource;

import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

/**
 * 사양서의 기본정보를 담는 AbstractTab객체
 *
 * @author KYJ
 *
 */
public abstract class AbstractSpecTab extends Tab implements SupplySkin<BorderPane> {

	private SpecTabPane specTabPane;

	public AbstractSpecTab(String title, SpecTabPane specTabPane) throws Exception {
		this.specTabPane = specTabPane;
		setContent(supplyNode());
		setText(title);
	}

	//	public abstract String getTitle();

	public SpecTabPane getSpecTabPane() {
		return specTabPane;
	}

	public final SpecResource getSpecResource() {
		return this.specTabPane.getResource();
	}

}
