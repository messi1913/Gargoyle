/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2016. 6. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.io.File;

import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser.ExtensionFilter;

/***************************
 *
 * @author KYJ
 *
 ***************************/
@FXMLController(value = "SceneBuilderLocationView.fxml", isSelfController = true)
public class SceneBuilderLocationComposite extends BorderPane {

	@FXML
	private TextField txtLocation;

	public SceneBuilderLocationComposite() throws Exception {
		FxUtil.loadRoot(SceneBuilderLocationComposite.class, this);
	}

	@FXML
	public void initialize() {

		String location = ResourceLoader.getInstance().get(ResourceLoader.SCENEBUILDER_LOCATION);
		if (ValueUtil.isNotEmpty(location)) {
			txtLocation.setText(location);
		}

	}

	/***********************************************************************************/
	/* 이벤트 구현 */

	@FXML
	public void btnBrowseOnMouseClick() {
		File selectedFile = DialogUtil.showFileDialog(this.getScene().getWindow(), chooser -> {
			String property = System.getProperty("user.home");

			String pathname = String.join(File.separator, property, "AppData", "Local", "SceneBuilder");
			File file = new File(pathname);
			if (!file.exists())
				file = new File(property);
			chooser.setTitle("Scenebuilder Location");
			chooser.setInitialFileName("SceneBuilder.exe");
			chooser.setInitialDirectory(file);
			chooser.setSelectedExtensionFilter(new ExtensionFilter("exe files (*.exe)", "*.exe"));
		});

		if (selectedFile != null && selectedFile.exists()) {
			txtLocation.setText(selectedFile.getAbsolutePath());
		}

	}

	@FXML
	public void btnSaveOnMouseClick() {
		File sceneBuilderLocation = new File(txtLocation.getText());
		if (sceneBuilderLocation != null && sceneBuilderLocation.exists()) {
			ResourceLoader.getInstance().put(ResourceLoader.SCENEBUILDER_LOCATION, sceneBuilderLocation.getAbsolutePath());
		}
	}

	/***********************************************************************************/

	/***********************************************************************************/
	/* 일반API 구현 */

	/***********************************************************************************/
}
