/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.words.spec.ui.tabs
 *	작성일   : 2016. 2. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.words.spec.ui.tabs;

import com.kyj.fx.voeditor.visual.component.popup.DatabaseTableView;
import com.kyj.fx.voeditor.visual.framework.SupplySkin;

import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 *
 */
public class TableInfoTab extends Tab implements SupplySkin<BorderPane> {

	private SpecTabPane specTabPane;

	public TableInfoTab(SpecTabPane specTabPane) {
		this.specTabPane = specTabPane;
		this.setText("테이블 정의");
		this.setContent(supplyNode());
	}

	@Override
	public BorderPane supplyNode() {
		return new DatabaseTableView();
	}
}
