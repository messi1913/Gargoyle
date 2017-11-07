/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.config.view
 *	작성일   : 2017. 6. 13.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.config.view;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.GargoyleExtensionFilters;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 *  Postgre PgAdmin의 위치를 기억하기위한 설정을 처리함.
 * @author KYJ
 *
 */
@FXMLController(value = "PostgrePgAdminConfigView.fxml", isSelfController = true)
public class PostgrePgAdminConfigComposite extends BorderPane {

	private static final Logger LOGGER = LoggerFactory.getLogger(PostgrePgAdminConfigComposite.class);
	@FXML
	private TextField txtPostgreLocation;
	@FXML
	private Button button;

	public PostgrePgAdminConfigComposite() {
		FxUtil.loadRoot(PostgrePgAdminConfigComposite.class, this, err -> {
			LOGGER.error(ValueUtil.toString(err));
		});
	}

	@FXML
	public void initialize() {
		String location = ResourceLoader.getInstance().get(ResourceLoader.POSTGRE_PGADMIN_BASE_DIR);
		if (ValueUtil.isNotEmpty(location)) {
			txtPostgreLocation.setText(location);
		}
	}

	@FXML
	public void btnSaveOnAction() {
		String text = txtPostgreLocation.getText();
		ResourceLoader.getInstance().put(ResourceLoader.POSTGRE_PGADMIN_BASE_DIR, text);
		DialogUtil.showMessageDialog("저장되었습니다.");
	}

	@FXML
	public void txtPostgreLocationOnMouseClick() {

		File pgdumpFile = DialogUtil.showFileDialog(FxUtil.getWindow(this), chooser -> {
			chooser.setInitialFileName("pg_dump.exe");
			chooser.getExtensionFilters().add(new ExtensionFilter(GargoyleExtensionFilters.EXE_NAME, GargoyleExtensionFilters.EXE));
		});
		if (pgdumpFile != null) {
			txtPostgreLocation.setText(pgdumpFile.getAbsolutePath());
		}

	}
}
