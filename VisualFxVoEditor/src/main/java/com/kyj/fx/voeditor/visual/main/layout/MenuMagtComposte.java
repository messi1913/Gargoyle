/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.layout
 *	작성일   : 2017. 9. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main.layout;

import java.util.Arrays;
import java.util.List;

import com.kyj.fx.voeditor.visual.momory.SharedMemory;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * 
 * 메인 메뉴에 대한 정보를 보여주는 HBox
 * 
 * @author KYJ
 *
 */

public class MenuMagtComposte extends HBox {

	public MenuMagtComposte() {
		getStyleClass().add("hbox-gargoyle");
		this.setSpacing(20.0);
		this.setPadding(new Insets(5d, 0d, 5d, 15d));

		Label label = new Label("VoEditor");
		label.setOnMouseClicked(e -> {
			SharedMemory.getSystemLayoutViewController().lblVoEditorOnAction(new ActionEvent());
		});

		Label label2 = new Label("DaoWizard");
		label2.setOnMouseClicked(e -> {
			SharedMemory.getSystemLayoutViewController().lblDaoWizardOnMouseClick(e);
		});
		Label label3 = new Label("Configuration");
		label3.setOnMouseClicked(e -> {
			SharedMemory.getSystemLayoutViewController().lblConfigonMouseClick(e);
		});
		Label label4 = new Label("Database");
		label4.setOnMouseClicked(e -> {
			SharedMemory.getSystemLayoutViewController().lblDatabaseMouseClick(e);
		});
		Label label5 = new Label("SpreadSheet");
		label5.setOnMouseClicked(e -> {
			SharedMemory.getSystemLayoutViewController().lblSpreadSheetOnMouseClick(e);
		});
		Label label6 = new Label("DB Console");
		label6.setOnMouseClicked(e -> {
			SharedMemory.getSystemLayoutViewController().lblDBConsoleOnMouseClick(e);
		});
		Label label7 = new Label("System Console");
		label7.setOnMouseClicked(e -> {
			SharedMemory.getSystemLayoutViewController().lblSystemConsoleOnMouseClick(e);
		});

		List<Label> arrayList = Arrays.asList(label, label2, label3, label4, label5, label6, label7);

		arrayList.forEach(n -> {
			n.setStyle("-fx-font-size: 18.0px");
			n.setCursor(Cursor.HAND);
		});

		getChildren().addAll(arrayList);
	}

}
