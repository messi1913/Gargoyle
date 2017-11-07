/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.config.skin
 *	작성일   : 2017. 4. 11.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.config.skin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "InstalledJresView.fxml", isSelfController = true)
public class InstalledJresComposte extends BorderPane{

	private static final Logger LOGGER = LoggerFactory.getLogger(InstalledJresComposte.class);

	public InstalledJresComposte() {
		FxUtil.loadRoot(InstalledJresComposte.class, this, err -> {
			LOGGER.error(ValueUtil.toString(err));
		});
	}
}
