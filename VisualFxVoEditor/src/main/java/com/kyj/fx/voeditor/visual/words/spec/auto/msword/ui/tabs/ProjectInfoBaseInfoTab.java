/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.words.spec.ui.controls
 *	작성일   : 2016. 2. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.words.spec.auto.msword.ui.tabs;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.grid.AnnotateBizOptions;
import com.kyj.fx.voeditor.visual.component.grid.CrudBaseGridView;
import com.kyj.fx.voeditor.visual.framework.SupplySkin;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.GargoyleExtensionFilters;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.ui.skin.BaseInfoComposite;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.vo.ImportsDVO;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.vo.MethodDVO;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.vo.ProgramSpecSVO;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.vo.UserSourceMetaDVO;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * 사양서의 기본정보를 담는 Tab객체
 *
 * @author KYJ
 *
 */
class ProjectInfoBaseInfoTab extends AbstractSpecTab implements SupplySkin<BorderPane> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectInfoBaseInfoTab.class);

	public ProjectInfoBaseInfoTab(String title, SpecTabPane specTabPane) throws Exception {
		super(title, specTabPane);
	}

	private BaseInfoComposite baseInfoController;

	/* TableInfo */
	private CrudBaseGridView<MethodDVO> gv;
	/**
	 * 사양서생성 버튼.
	 * 
	 * @최초생성일 2016. 8. 14.
	 */
	private Button btnGenerate;

	@Override
	public BorderPane supplyNode() throws Exception {

		BorderPane root = new BorderPane();

		try {

			/* BaseInfo */

			baseInfoController = new BaseInfoComposite(this);
			//			FXMLLoader loader = new FXMLLoader();
			//			loader.setLocation(BaseInfoComposite.class.getResource("BaseInfoApp.fxml"));
			//			BorderPane supplyNode = loader.load();
			//			supplyNode.setPrefWidth(BorderPane.USE_COMPUTED_SIZE);
			//			BaseInfoComposite baseInfoController = loader.getController();

			/* 버튼박스 */
			HBox hboxButton = new HBox(5);
			hboxButton.setPrefHeight(HBox.USE_COMPUTED_SIZE);
			HBox.setHgrow(hboxButton, Priority.NEVER);
			btnGenerate = new Button("사양서 생성");
			btnGenerate.setOnMouseClicked(this::btnGenerateOnMouseClick);
			btnGenerate.setPrefWidth(120);
			hboxButton.getChildren().add(btnGenerate);

			/* TableInfo */
			gv = new CrudBaseGridView<MethodDVO>(MethodDVO.class, new AnnotateBizOptions<MethodDVO>(MethodDVO.class) {

				@Override
				public boolean isCreateColumn(String columnName) {
					if ("methodMetaDVO".equals(columnName))
						return false;
					//					else if("methodParameterDVOList".equals(columnName))
					//						return false;
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

			baseInfoController.setBottom(hboxButton);
			root.setTop(baseInfoController);
			root.setCenter(gv);

		} catch (IOException | NullPointerException e) {
			LOGGER.error(ValueUtil.toString(e));
		}

		root.setPrefSize(BorderPane.USE_COMPUTED_SIZE, BorderPane.USE_COMPUTED_SIZE);

		root.setPadding(new Insets(5, 5, 5, 5));
		return root;
	}

	/********************************
	 * 작성일 : 2016. 8. 14. 작성자 : KYJ
	 *
	 * 사양서 생성하는 역할을 처리하는 클릭 이벤트.
	 * 
	 * @param e
	 ********************************/
	public void btnGenerateOnMouseClick(MouseEvent e) {

		File showFileSaveDialog = DialogUtil.showFileSaveDialog(null, chooser -> {
			chooser.getExtensionFilters().add(
					new ExtensionFilter(GargoyleExtensionFilters.DOCX_NAME, GargoyleExtensionFilters.DOCX, GargoyleExtensionFilters.DOC));

		});
		if (showFileSaveDialog != null) {
			//			ProgramSpecSVO svo = new ProgramSpecSVO();
			//			UserSourceMetaDVO meta = new UserSourceMetaDVO();
			//			meta.setPackages(baseInfoController.getPackage());
			//			meta.setProjectName(baseInfoController.getProjectName());
			//			meta.setProgramName(baseInfoController.getProjectName());
			//			meta.setUserPcName(baseInfoController.getUserName());
			//			svo.setUserSourceMetaDVO(meta);
			//
			//			ImportsDVO importDvo = new ImportsDVO();
			//			importDvo.setImports(baseInfoController.getImports());
			//			svo.setImportsDVO(importDvo);
			//
			//			svo.setMethodDVOList(baseInfoController.getMethodData());

			//			ProgramSpecUtil.createDefault(svo, showFileSaveDialog);

			File result = getSpecTabPane().createDocument(showFileSaveDialog);
			if (result != null) {
				FileUtil.openFile(result);
			}

		}

	}

	@Override
	public void createDocumentAction(ProgramSpecSVO svo) {
		UserSourceMetaDVO meta = new UserSourceMetaDVO();
		meta.setPackages(baseInfoController.getPackage());
		meta.setProjectName(baseInfoController.getProjectName());
		meta.setProgramName(baseInfoController.getProjectName());
		meta.setUserPcName(baseInfoController.getUserName());
		svo.setUserSourceMetaDVO(meta);

		ImportsDVO importDvo = new ImportsDVO();
		importDvo.setImports(baseInfoController.getImports());
		svo.setImportsDVO(importDvo);

		svo.setMethodDVOList(baseInfoController.getMethodData());

	}

}
