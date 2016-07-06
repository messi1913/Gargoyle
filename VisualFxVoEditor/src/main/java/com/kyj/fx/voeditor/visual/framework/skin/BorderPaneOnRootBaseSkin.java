/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.words.spec.ui.skin
 *	작성일   : 2016. 2. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.skin;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 *
 */
public abstract class BorderPaneOnRootBaseSkin extends BorderPane implements Skin<Control> {

	protected BorderPaneOnRootBaseSkin() {

		try {
			URL fxml = getFxml();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(fxml);
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public abstract void initialize();

	/**
	 * fxml이 위치한 URL정보를 리턴
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 2. 18.
	 * @return
	 */
	public abstract URL getFxml();

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.scene.control.Skin#getNode()
	 */
	@Override
	public final Node getNode() {
		return this;
	}

}
