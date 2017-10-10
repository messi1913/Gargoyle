/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.layout
 *	작성일   : 2017. 10. 9.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main.layout;

import org.controlsfx.control.HyperlinkLabel;

import com.kyj.fx.voeditor.visual.main.Main;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * @author KYJ
 *
 */
public class GargoyleHelpComposite extends BorderPane {

	public GargoyleHelpComposite() {
		VBox vbCenter = new VBox();
		vbCenter.getChildren().add(new Label("Gargoyle"));
		String url = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.ABOUT_PAGE_URL);
		vbCenter.getChildren().add(new HyperlinkLabel(url));
		vbCenter.getChildren().add(new Label("Version : "));
		vbCenter.getChildren().add(new Label(Main.getVersion()));
		vbCenter.setStyle("-fx-padding: 5px");
		this.setCenter(vbCenter);
	}
}
