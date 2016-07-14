/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.words.spec.ui.tabs
 *	작성일   : 2016. 2. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.words.spec.ui.tabs;

import com.kyj.fx.voeditor.visual.component.popup.DatabaseTableView;
import com.kyj.fx.voeditor.visual.framework.SupplySkin;

import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 *
 */
class TableInfoTab extends AbstractSpecTab implements SupplySkin<BorderPane> {

	public TableInfoTab(String title, SpecTabPane specTabPane) {
		super(title, specTabPane);
	}

	@Override
	public BorderPane supplyNode() {
		return new DatabaseTableView();
	}

}
