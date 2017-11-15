/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.uc.items
 *	작성일   : 2017. 11. 15.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.uc.items;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.fxloader.FxPostInitialize;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "BizTreeViewComposite.fxml", isSelfController = true)
public class BizTreeViewComposite extends BorderPane {

	private static final Logger LOGGER = LoggerFactory.getLogger(BizTreeViewComposite.class);

	public BizTreeViewComposite() {

		FxUtil.loadRoot(this.getClass(), this, err -> {
			LOGGER.error(ValueUtil.toString(err));
		});
	}

	@FXML
	public void initialize() {

	}

	@FxPostInitialize
	public void loadData() {

	}
}
