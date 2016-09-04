/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.about
 *	작성일   : 2016. 9. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.about;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/***************************
 * 
 * @author KYJ
 *
 ***************************/
@FXMLController("AboutView.fxml")
public class AboutController {

	private static final String VERSION_FILE_NAME = "Gargoyle.cfg";

	@FXML
	private Label lblVersion;

	@FXML
	public void initialize() {

		File file = new File(VERSION_FILE_NAME);
		Properties properties = new Properties();
		try {

			if (file.exists())
				properties.load(new FileInputStream(file));

			String version = ValueUtil.decode(properties.get("app.version"), "dev.version").toString();
			lblVersion.setText(version);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
