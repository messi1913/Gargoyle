/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.words.spec.ui.controls
 *	작성일   : 2016. 2. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.words.spec.ui.tabs;

import java.io.IOException;

import com.kyj.fx.voeditor.visual.component.grid.CrudBaseGridView;
import com.kyj.fx.voeditor.visual.framework.SupplySkin;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.vo.MethodDVO;
import com.kyj.fx.voeditor.visual.words.spec.ui.skin.BaseInfoController;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * 사양서의 기본정보를 담는 Tab객체
 *
 * @author KYJ
 *
 */
public class BaseInfoTab extends Tab implements SupplySkin<BorderPane> {

	private SpecTabPane specTabPane;

	public BaseInfoTab(SpecTabPane specTabPane) {
		this.specTabPane = specTabPane;
		setText("사양서 기본 정보");
		setContent(supplyNode());
	}

	@Override
	public BorderPane supplyNode() {

		BorderPane root = new BorderPane();

		try {
			/* BaseInfo */
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(BaseInfoController.class.getResource("BaseInfoApp.fxml"));
			BorderPane supplyNode = loader.load();
			supplyNode.setPrefWidth(BorderPane.USE_COMPUTED_SIZE);

			/* 버튼박스 */
			HBox hboxButton = new HBox(5);
			hboxButton.setPrefHeight(HBox.USE_COMPUTED_SIZE);
			HBox.setHgrow(hboxButton, Priority.NEVER);
			Button btnGenerate = new Button("사양서 생성");
			btnGenerate.setPrefWidth(120);
			hboxButton.getChildren().add(btnGenerate);

			/* TableInfo */
			CrudBaseGridView<MethodDVO> gv = new CrudBaseGridView<MethodDVO>(MethodDVO.class);

			supplyNode.setBottom(hboxButton);
			root.setTop(supplyNode);
			root.setCenter(gv);

		} catch (IOException e) {
			e.printStackTrace();
		}

		root.setPrefSize(BorderPane.USE_COMPUTED_SIZE, BorderPane.USE_COMPUTED_SIZE);

		root.setPadding(new Insets(5, 5, 5, 5));
		return root;
	}
}
