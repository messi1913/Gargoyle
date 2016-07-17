/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.words.spec.ui.controls
 *	작성일   : 2016. 2. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.words.spec.ui.tabs;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.grid.AnnotateBizOptions;
import com.kyj.fx.voeditor.visual.component.grid.CrudBaseGridView;
import com.kyj.fx.voeditor.visual.component.grid.IOptions;
import com.kyj.fx.voeditor.visual.framework.SupplySkin;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.vo.MethodDVO;
import com.kyj.fx.voeditor.visual.words.spec.ui.skin.BaseInfoController;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * 사양서의 기본정보를 담는 Tab객체
 *
 * @author KYJ
 *
 */
class ProjectInfoBaseInfoTab extends AbstractSpecTab implements SupplySkin<BorderPane> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectInfoBaseInfoTab.class);
	
	public ProjectInfoBaseInfoTab(String title, SpecTabPane specTabPane) {
		super(title, specTabPane);
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
			BaseInfoController baseInfoController = loader.getController();

			/* 버튼박스 */
			HBox hboxButton = new HBox(5);
			hboxButton.setPrefHeight(HBox.USE_COMPUTED_SIZE);
			HBox.setHgrow(hboxButton, Priority.NEVER);
			Button btnGenerate = new Button("사양서 생성");
			btnGenerate.setPrefWidth(120);
			hboxButton.getChildren().add(btnGenerate);

			/* TableInfo */
			CrudBaseGridView<MethodDVO> gv = new CrudBaseGridView<MethodDVO>(MethodDVO.class,
					new AnnotateBizOptions<MethodDVO>(MethodDVO.class) {

						@Override
						public boolean isCreateColumn(String columnName) {
							if ("methodMetaDVO".equals(columnName))
								return false;
							return super.isCreateColumn(columnName);
						}

						@Override
						public boolean visible(String columnName) {
							if ("methodMetaDVO".equals(columnName))
								return false;

							return super.visible(columnName);
						}

					});
			
//			baseInfoController.getMethodData().addListener(new ListChangeListener<MethodDVO>() {
//				@Override
//				public void onChanged(javafx.collections.ListChangeListener.Change<? extends MethodDVO> c) {
//					if (c.next()) {
//						gv.getItems().addAll(c.getAddedSubList());
//					}
//				}
//			});
			
			gv.getItems().addAll(baseInfoController.getMethodData());

			supplyNode.setBottom(hboxButton);
			root.setTop(supplyNode);
			root.setCenter(gv);

		} catch (IOException | NullPointerException e) {
			LOGGER.error(ValueUtil.toString(e));
		}

		root.setPrefSize(BorderPane.USE_COMPUTED_SIZE, BorderPane.USE_COMPUTED_SIZE);

		root.setPadding(new Insets(5, 5, 5, 5));
		return root;
	}

}
