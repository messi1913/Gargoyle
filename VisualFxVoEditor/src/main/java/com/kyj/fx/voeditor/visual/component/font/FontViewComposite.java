/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.font
 *	작성일   : 2016. 12. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.font;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.voeditor.visual.framework.thread.ExecutorDemons;
import com.kyj.fx.voeditor.visual.momory.SkinManager;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "FontView.fxml", isSelfController = true)
public class FontViewComposite extends BorderPane {

	private static final Logger LOGGER = LoggerFactory.getLogger(FontViewComposite.class);

	@FXML
	private JFXComboBox<String> cbFontNames, cbFontStyles, cbFontWeight;

	@FXML
	private JFXSlider sliderFontSize, sliderFontWeight;

	@FXML
	private Label lblPreviewText, lblFontInfo;

	private static ExecutorService newFixedThreadExecutor = ExecutorDemons.newFixedThreadExecutor(1);
	public FontViewComposite() {
		FxUtil.loadRoot(FontViewComposite.class, this, err -> LOGGER.error(ValueUtil.toString(err)));
		
	}

	@FXML
	public void initialize() {

		Font defaultFont = FxUtil.FONTUtil.getDefaultFont();
		String name = defaultFont.getName();
		double size = defaultFont.getSize();
		String style = defaultFont.getStyle();

		FontStyle fontStyle = new FontStyle(defaultFont);
		FontWeight fontWeight = fontStyle.getWeight();
		int weight = fontWeight.getWeight();

		cbFontNames.setValue(name);
		cbFontStyles.setValue(style);
		sliderFontSize.setValue(size);
		sliderFontWeight.setValue(weight);

		loadFontNames();
		loadFontStyles();
		loadFontWeights();

		printSelectFontInfo(defaultFont);

		cbFontNames.valueProperty().addListener((oba, o, n) -> {
			printSelectFontInfo(Font.font(n));
			loadFont();
		});

		cbFontStyles.valueProperty().addListener((oba, o, n) -> {
			loadFont();
		});

		sliderFontSize.valueProperty().addListener((oba, o, n) -> {

			loadFont();

		});

		sliderFontWeight.valueProperty().addListener((oba, o, n) -> {
			loadFont();
		});

		cbFontWeight.valueProperty().addListener((oba, o, n) -> {
			updateFontWeightByName();
		});

		cbFontNames.setDisable(true);
	}

	@FXML
	Label lblFontName, lblFontSize, lblFontStyle, lblFontFamily;

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 2.
	 */
	private void printSelectFontInfo(Font font) {
		if (ValueUtil.isNotEmpty(font)) {
			double size = font.getSize();
			String name = font.getName();
			String style2 = font.getStyle();
			String family = font.getFamily();

			lblFontName.setText(name);
			lblFontSize.setText(size + "");
			lblFontStyle.setText(style2);
			lblFontFamily.setText(family);

		}

	}

	/**
	 * FontWeight이름의 값을 이용, weight값으로 변환후 slider에 값을 update처리.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 2.
	 */
	private void updateFontWeightByName() {

		if (ValueUtil.isNotEmpty(cbFontWeight.getValue())) {
			String fontWeight = cbFontWeight.getValue();

			/*
			 *  this code is bug  jdk 1.8.11- > FontWeight.findByName(fontWeight);
			 *  2016-12-02 by kyj.
			 */

			FontWeight findByName = findByName(fontWeight);

			int weight = findByName.getWeight();
			sliderFontWeight.setValue(weight);
		}
	}

	public static FontWeight findByName(String name) {
		if (name == null)
			return null;

		for (FontWeight w : FontWeight.values()) {
			if (w.name().equalsIgnoreCase(name))
				return w;
		}

		return null;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 2.
	 */
	private void loadFont() {
		FontWeight fontWeight = FontWeight.findByWeight((int) sliderFontWeight.getValue());
		FontPosture fontStyle = FontPosture.findByName(cbFontStyles.getValue());
		double fontSize = sliderFontSize.getValue();
		String fontName = cbFontNames.getValue();
		LOGGER.debug("{} {} {} {}", fontName, fontWeight, fontStyle, fontSize);
		Font font = Font.font(fontName, fontWeight, fontStyle, fontSize);
		if (font != null)
			lblPreviewText.setFont(font);

	}

	/**
	 * 폰트명 로딩
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 2.
	 */
	private void loadFontNames() {

		Service<List<String>> createFxService = new Service<List<String>>() {
			@Override
			protected Task<List<String>> createTask() {
				return new Task<List<String>>() {
					@Override
					protected List<String> call() throws Exception {
						List<String> avaliableFontNames = FxUtil.FONTUtil.getAvaliableFontNames();
						return avaliableFontNames;
					}
				};
			}
		};

		createFxService.setOnSucceeded(w -> {
			List<String> value = createFxService.getValue();
			cbFontNames.getItems().addAll(value);
			LOGGER.debug("Font Load Completed.");

			cbFontNames.setDisable(false);
		});
		
		createFxService.setExecutor(newFixedThreadExecutor);
		createFxService.start();

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 2.
	 */
	private void loadFontWeights() {

		Service<List<String>> createFxService = new Service<List<String>>() {
			@Override
			protected Task<List<String>> createTask() {
				return new Task<List<String>>() {
					@Override
					protected List<String> call() throws Exception {
						return FxUtil.FONTUtil.getFontWeights();
					}
				};
			}
		};

		createFxService.setOnSucceeded(w -> {
			List<String> value = createFxService.getValue();
			cbFontWeight.getItems().addAll(value);
		});

		createFxService.start();

	}

	private void loadFontStyles() {
		Service<List<String>> createFxService = new Service<List<String>>() {
			@Override
			protected Task<List<String>> createTask() {
				return new Task<List<String>>() {
					@Override
					protected List<String> call() throws Exception {
						return FxUtil.FONTUtil.getFontStyles();
					}
				};
			}
		};

		createFxService.setOnSucceeded(w -> {
			List<String> value = createFxService.getValue();
			cbFontStyles.getItems().addAll(value);
		});
		
		

		createFxService.start();

	}

	private static class FontStyle implements Comparable<FontStyle> {

		private FontPosture posture;
		private FontWeight weight;

		public FontStyle(FontWeight weight, FontPosture posture) {
			this.posture = posture == null ? FontPosture.REGULAR : posture;
			this.weight = weight;
		}

		public FontStyle() {
			this(null, null);
		}

		public FontStyle(String styles) {
			this();
			String[] fontStyles = (styles == null ? "" : styles.trim().toUpperCase()).split(" "); //$NON-NLS-1$ //$NON-NLS-2$
			for (String style : fontStyles) {
				FontWeight w = FontWeight.findByName(style);
				if (w != null) {
					weight = w;
				} else {
					FontPosture p = FontPosture.findByName(style);
					if (p != null)
						posture = p;
				}
			}
		}

		public FontStyle(Font font) {
			this(font.getStyle());
		}

		public FontPosture getPosture() {
			return posture;
		}

		public FontWeight getWeight() {
			return weight;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((posture == null) ? 0 : posture.hashCode());
			result = prime * result + ((weight == null) ? 0 : weight.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object that) {
			if (this == that)
				return true;
			if (that == null)
				return false;
			if (getClass() != that.getClass())
				return false;
			FontStyle other = (FontStyle) that;
			if (posture != other.posture)
				return false;
			if (weight != other.weight)
				return false;
			return true;
		}

		private static String makePretty(Object o) {
			String s = o == null ? "" : o.toString(); //$NON-NLS-1$
			if (!s.isEmpty()) {
				s = s.replace("_", " "); //$NON-NLS-1$ //$NON-NLS-2$
				s = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
			}
			return s;
		}

		@Override
		public String toString() {
			return String.format("%s %s", makePretty(weight), makePretty(posture)).trim(); //$NON-NLS-1$
		}

		private <T extends Enum<T>> int compareEnums(T e1, T e2) {
			if (e1 == e2)
				return 0;
			if (e1 == null)
				return -1;
			if (e2 == null)
				return 1;
			return e1.compareTo(e2);
		}

		@Override
		public int compareTo(FontStyle fs) {
			int result = compareEnums(weight, fs.weight);
			return (result != 0) ? result : compareEnums(posture, fs.posture);
		}

	}
	
	@FXML
	public void btnApplyOnAction(){
		
		String fontName = cbFontNames.getSelectionModel().getSelectedItem();
		String fontStyle = cbFontStyles.getSelectionModel().getSelectedItem();
		double fontSize = sliderFontSize.getValue();
		double fontWeight = sliderFontWeight.getValue();
		
		SkinManager.getInstance().getRootSkinTemplate();
		
//		SkinManager.getInstance().registRootSkin();
		
		
	}
}
