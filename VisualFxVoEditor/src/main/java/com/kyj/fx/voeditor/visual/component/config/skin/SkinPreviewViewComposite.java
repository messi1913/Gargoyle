/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.config.skin
 *	작성일   : 2016. 12. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.config.skin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfoenix.controls.JFXToggleButton;
import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.fxloader.FxPostInitialize;
import com.kyj.fx.voeditor.visual.momory.SkinManager;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "SkinPreviewView.fxml", isSelfController = true)
public class SkinPreviewViewComposite extends BorderPane {

	private static final Logger LOGGER = LoggerFactory.getLogger(SkinPreviewViewComposite.class);
	/**********************************************************************************************/
	@FXML
	private MenuBar mbSample;
	@FXML
	private HBox hboxSample;

	@FXML
	private TabPane tabpaneSample;

	@FXML
	private ColorPicker colorMbSample, colorMbLabelSample, colorHboxSample, colorTabSample1Selected, colorSelectedTabText,
			colorUnSelectedTabText;
	/**********************************************************************************************/

	/**********************************************************************************************/
	//Button Tab

	@FXML
	private TableView<File> tbBtnSkins;
	@FXML
	private TableColumn<File, String> colSkinName;
	@FXML
	private TableColumn<File, String> colPreview;
	@FXML
	private BorderPane borPreview;
	@FXML
	private JFXToggleButton tgbUseBtnDefault;

	@FXML
	private TextArea txtStyle;

	/**********************************************************************************************/

	public SkinPreviewViewComposite() {
		try {
			FxUtil.loadRoot(SkinPreviewViewComposite.class, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void initialize() {

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 5.
	 */
	private void loadButtonStyles() {
		tbBtnSkins.getItems().addAll(SkinManager.getInstance().getButtonSkinFiles());
	}

	private StringProperty selectedBtnStyleClassName = new SimpleStringProperty();

	@FxPostInitialize
	public void previewTabInit() {
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {

				Thread.sleep(5000L);
				Platform.runLater(() -> {
					//메뉴바 배경.
					{
						Background background = mbSample.getBackground();
						Color fill = (Color) background.getFills().get(0).getFill();
						colorMbSample.setValue(fill);

						//메뉴바 텍스트
						{
							Label lookup = (Label) mbSample.lookup(".label");
							Color textFill = (Color) lookup.getTextFill();
							colorMbLabelSample.setValue(textFill);
						}
					}

					//Hbox 배경.
					{
						Background background = hboxSample.getBackground();
						if(background != null)
						{
							Color fill = (Color) background.getFills().get(0).getFill();
							colorHboxSample.setValue(fill);	
						}
						
					}

					{
						//선택디지않는 탭 색상 처리.
						Set<Node> lookupAll = tabpaneSample.lookupAll(".tab:top");
						lookupAll.forEach(lookup -> {

							//Selected 여부에 따른 분리.
							Optional<PseudoClass> findFirst = lookup.getPseudoClassStates().stream().filter(v -> {
								return "selected".equals(v.getPseudoClassName());
							}).findFirst();

							//선택된 탭내 텍스트 색상
							if (findFirst.isPresent()) {
								Label selectedTabLabel = (Label) lookup.lookup(".tab-label");
								Color textFill = (Color) selectedTabLabel.getTextFill();
								colorSelectedTabText.setValue(textFill);
							} else
							//선택되지 않는 탭내 색상.
							{
								Label selectedTabLabel = (Label) lookup.lookup(".tab-label");
								Color textFill = (Color) selectedTabLabel.getTextFill();
								colorUnSelectedTabText.setValue(textFill);

							}
						});

						{
							lookupAll.stream().findFirst().ifPresent(n -> {
								Pane p = (Pane) n;
								Background background = p.getBackground();
								Color fill = (Color) background.getFills().get(0).getFill();
								colorTabSample1Selected.setValue(fill);
							});

						}
					}
				});

				return null;
			}
		};

		Window window = this.getScene().getWindow();
		if (window != null) {
			FxUtil.showLoading(window, task);
		} else
			FxUtil.showLoading(task);
	}

	@FxPostInitialize
	public void buttonTabInit() {
		loadButtonStyles();

		if (SkinManager.getInstance().isUsedCustomButton()) {
			tgbUseBtnDefault.setSelected(false);
			tbBtnSkins.setOpacity(1.0d);
		} else {
			tbBtnSkins.setOpacity(0.5d);
		}

		tgbUseBtnDefault.selectedProperty().addListener((oba, o, n) -> {
			if (n) {
				tbBtnSkins.setOpacity(0.5d);
			} else
				tbBtnSkins.setOpacity(1.0d);
		});

		colSkinName.setCellValueFactory(param -> {
			return new SimpleStringProperty(param.getValue().getName());
		});

		tbBtnSkins.getSelectionModel().selectedItemProperty().addListener((oba, o, n) -> {
			File selectedItem = n;

			if (selectedItem != null && selectedItem.exists()) {

				String readFile = FileUtil.readFile(selectedItem, true, null);
				txtStyle.setText(readFile);

				try {
					List<Node> findAllByNodes = FxUtil.findAllByNodes(borPreview, node -> node instanceof Button);
					String className = String.format("%s", selectedItem.getName().replaceAll(".css", ""));

					findAllByNodes.forEach(btn -> {
						btn.getStyleClass().add("button");
						btn.getStyleClass().add(className);
					});

					borPreview.getStylesheets().clear();
					borPreview.getStylesheets().add(selectedItem.toURI().toURL().toExternalForm());
					selectedBtnStyleClassName.set(className);
					borPreview.applyCss();
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				txtStyle.setText("");
				selectedBtnStyleClassName.set(null);
			}

		});
	}

	/**
	 * 스킨 적용 처리.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 2.
	 */
	@FXML
	public void btnApplyOnAction() {

		String complatedTemplate = getAppliedSkinText();

		try {
			File createUserCustomSkin = SkinManager.getInstance().createUserCustomSkin(complatedTemplate, false);
			if (createUserCustomSkin != null) {

				ObservableList<String> stylesheets = this.getScene().getStylesheets();
				stylesheets.clear();

				//기본 스킨
				stylesheets.add(createUserCustomSkin.toURI().toURL().toExternalForm());

				String btnStyleClass = null;
				//버튼스킨
				if (!tgbUseBtnDefault.isSelected()) {

					if (ValueUtil.isNotEmpty(selectedBtnStyleClassName.get())) {
						btnStyleClass = selectedBtnStyleClassName.get();
						SkinManager.getInstance().registButtonSyleClass(btnStyleClass);
//						SkinManager.getInstance().applyBtnSyleClass(btnStyleClass);
						stylesheets.add(SkinManager.getInstance().toButtonURL(btnStyleClass).toExternalForm());
					}
				}

				SkinManager.getInstance().applySkin(createUserCustomSkin , btnStyleClass);

			}
		} catch (IOException e) {
			LOGGER.error(ValueUtil.toString(e));
		}

	}

	/**
	 * 변수값으로 입력된 스킨을 리턴
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 2.
	 * @return
	 */
	private String getAppliedSkinText() {
		String skinTemplate = SkinTemplate.getSkinTemplate();
		//		colorMbSample, colorHboxSample, colorTabSample1Selected, colorSelectedTabText, colorUnSelectedTabText;
		Map<String, Object> param = new HashMap<>();

		param.put(SkinTemplate.MENU_BAR, FxUtil.toWebString(colorMbSample.getValue()));
		param.put(SkinTemplate.MENU_BAR_OPACITY, colorMbSample.getValue().getOpacity());

		param.put(SkinTemplate.MENU_BAR_LABEL, FxUtil.toWebString(colorMbLabelSample.getValue()));
		param.put(SkinTemplate.MENU_BAR_LABEL_OPACITY, colorMbLabelSample.getValue().getOpacity());

		param.put(SkinTemplate.KEY_HBOX, FxUtil.toWebString(colorHboxSample.getValue()));
		param.put(SkinTemplate.KEY_HBOX_OPACITY, colorHboxSample.getValue().getOpacity());

		param.put(SkinTemplate.KEY_TAB, FxUtil.toWebString(colorTabSample1Selected.getValue()));
		param.put(SkinTemplate.KEY_TAB_OPACITY, colorTabSample1Selected.getValue().getOpacity());

		param.put(SkinTemplate.MENU_TAB_SELECTED, FxUtil.toWebString(colorSelectedTabText.getValue()));
		param.put(SkinTemplate.MENU_TAB_SELECTED_OPACITY, colorSelectedTabText.getValue().getOpacity());

		param.put(SkinTemplate.MENU_TAB_UNSELECTED, FxUtil.toWebString(colorUnSelectedTabText.getValue()));
		param.put(SkinTemplate.MENU_TAB_UNSELECTED_OPACITY, colorUnSelectedTabText.getValue().getOpacity());

		String complatedTemplate = ValueUtil.getVelocityToText(skinTemplate, param, false, null, str -> str);
		return complatedTemplate;
	}

	/**
	 * Skin Reset.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 2.
	 */
	@FXML
	public void btnResetOnAction() {
		SkinManager.getInstance().resetSkin(this.getScene());
		SkinManager.getInstance().resetSkin();
		previewTabInit();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 2.
	 */
	public void btnSaveOnAction() {

		Optional<Pair<String, String>> showInputDialog = DialogUtil.showInputDialog(FxUtil.getWindow(this), "Skin Name",
				"Input Your Skin Name");
		showInputDialog.ifPresent(v -> {
			if ("OK".equalsIgnoreCase(v.getKey())) {

				String newSkinName = v.getValue();
				if (!newSkinName.endsWith(".css")) {
					newSkinName = newSkinName.concat(".css");
				}

				String complatedTemplate = getAppliedSkinText();
				try {
					File createUserCustomSkin = SkinManager.getInstance().createUserCustomSkin(complatedTemplate, false);
					File dest = new File(SkinManager.SKIN_BASE_DIR, newSkinName);
					if (dest.exists()) {
						DialogUtil.showMessageDialog("기존 파일이 존재합니다.");
						return;
					}
					if (createUserCustomSkin.renameTo(dest)) {
						SkinManager.getInstance().applySkin(dest);
						SkinManager.getInstance().registSkin(dest.getAbsolutePath());

						//버튼스킨
						if (!tgbUseBtnDefault.isSelected()) {

							String btnStyleClass = selectedBtnStyleClassName.get();
							if (ValueUtil.isNotEmpty(btnStyleClass)) {
								//SkinManager.getInstance().applyBtnSyleClass(btnStyleClass);
								SkinManager.getInstance().registButtonSyleClass(btnStyleClass);
							}
						}

					} else {
						DialogUtil.showMessageDialog("저장에 실패하였습니다.");
						return;
					}

					DialogUtil.showMessageDialog("저장에 성공하였습니다. 스킨 설정화면에서 확인하실 수 있습니다.");

					Stage window = (Stage) getScene().getWindow();
					if (window != null)
						window.close();

				} catch (Exception e) {
					DialogUtil.showExceptionDailog(e);
				}
			}
		});
		//		String complatedTemplate = getAppliedSkinText();
		//
		//		try {
		//			File createUserCustomSkin = SkinManager.getInstance().createUserCustomSkin(complatedTemplate, false);
		//			if (createUserCustomSkin != null) {
		//
		//				ObservableList<String> stylesheets = this.getScene().getStylesheets();
		//				stylesheets.clear();
		//				stylesheets.add(createUserCustomSkin.toURI().toURL().toExternalForm());
		//
		//			}
		//		} catch (IOException e) {
		//			LOGGER.error(ValueUtil.toString(e));
		//		}

	}
}
