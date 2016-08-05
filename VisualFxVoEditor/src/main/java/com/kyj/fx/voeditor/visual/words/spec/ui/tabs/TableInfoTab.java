/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.words.spec.ui.tabs
 *	작성일   : 2016. 2. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.words.spec.ui.tabs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.popup.DatabaseTableView;
import com.kyj.fx.voeditor.visual.framework.SupplySkin;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.fx.voeditor.visual.words.spec.ui.skin.TableInfoController;

import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 *
 */
class TableInfoTab extends AbstractSpecTab implements SupplySkin<BorderPane> {

	private static final Logger LOGGER = LoggerFactory.getLogger(TableInfoTab.class);

	public TableInfoTab(String title, SpecTabPane specTabPane) throws Exception {
		super(title, specTabPane);
	}

	@Override
	public BorderPane supplyNode() {

		try {
			return FxUtil.loadAndControllerAction(TableInfoController.class, (TableInfoController c) -> {

				c.setBorTableDefine(new DatabaseTableView());
				c.setBorTableCollect(new TableView<>());

			});
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}

		return new DatabaseTableView();
	}

}
