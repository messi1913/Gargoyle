/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.nrch.realtime
 *	작성일   : 2016. 12. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.nrch.realtime;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.kohlschutter.boilerpipe.document.TextDocument;
import com.kohlschutter.boilerpipe.extractors.ExtractorBase;
import com.kohlschutter.boilerpipe.extractors.KeepEverythingWithMinKWordsExtractor;
import com.kohlschutter.boilerpipe.sax.BoilerpipeSAXInput;
import com.kyj.fx.voeditor.visual.framework.KeyValue;
import com.kyj.fx.voeditor.visual.framework.RealtimeSearchItemVO;
import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.RequestUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "ArticleExtractorView.fxml", isSelfController = true)
public class ArticleExtractorComposite extends BorderPane {
	private static final Logger LOGGER = LoggerFactory.getLogger(ArticleExtractorComposite.class);

	public static final String TITLE = "기사 분석기 - Preview ver.";

	@FXML
	private JFXComboBox<Class<? extends ExtractorBase>> cbAlgorisms;
	@FXML
	private JFXTextArea txtResult, txtTfIdf;

	private RealtimeSearchItemVO userData;

	public ArticleExtractorComposite() {
		FxUtil.loadRoot(ArticleExtractorComposite.class, this, e -> LOGGER.error(ValueUtil.toString(e)));
	}

	public ArticleExtractorComposite(RealtimeSearchItemVO userData) {
		this();
		this.userData = userData;
	}

	ObjectProperty<TextDocument> cache = new SimpleObjectProperty<>();

	@FXML
	public void initialize() {

		cbAlgorisms.getItems().addAll(AlgorismExtractor.getAvaliables());
		cbAlgorisms.getSelectionModel().select(KeepEverythingWithMinKWordsExtractor.class);

		cbAlgorisms.valueProperty().addListener((oba, o, n) -> {

			// TextDocument textDocument = cache.get();
			// if (null != textDocument) {
			//
			// try {
			// ExtractorBase newInstance = n.newInstance();
			// txtResult.setText(newInstance.getText(textDocument));
			// } catch (InstantiationException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (IllegalAccessException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (BoilerpipeProcessingException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			//
			// } else {
			request(userData);
			// }

		});

		Platform.runLater(() -> {
			request(userData);
		});

	}

	public void request(RealtimeSearchItemVO userData) {
		SingleSelectionModel<Class<? extends ExtractorBase>> selectionModel = cbAlgorisms.getSelectionModel();
		Class<? extends ExtractorBase> selectedItem = selectionModel.getSelectedItem();
		if (selectedItem != null) {
			request(selectedItem, userData);
		}
	}

	BinaryOperator<String> accumulator = (v1, v2) -> {
		return String.format("%s\n%s", v1.toString(), v2.toString());
	};
	Function<? super KeyValue, String> mapper = v -> String.format("%s : %s", v.getKey(), v.getValue().toString());

	public void request(Class<? extends ExtractorBase> algorism, RealtimeSearchItemVO userData) {
		if (userData == null)
			return;
		try {
			URL url = new URL("https:" + userData.getLink());

			String str = RequestUtil.reqeustSSL(url, (st, code) -> {

				try {

					if (code == 200) {
						InputSource inputSource = new InputSource(st);
						inputSource.setEncoding("UTF-8");

						final BoilerpipeSAXInput in = new BoilerpipeSAXInput(inputSource);
						final TextDocument doc = in.getTextDocument();

						if (algorism == KeepEverythingWithMinKWordsExtractor.class) {
							return new KeepEverythingWithMinKWordsExtractor(10).getText(doc);
						}

						try {
							return algorism.newInstance().getText(doc);
						} catch (IllegalAccessException e) {
							Field declaredField = algorism.getDeclaredField("INSTANCE");
							ExtractorBase instance = (ExtractorBase) declaredField.get(null);
							return instance.getText(doc);
						}

					}

					// return KeepEverythingExtractor.INSTANCE.getText(doc);

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (st != null) {
						try {
							st.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				return "";
			}, false);

			txtResult.setText(str);

			// Function<String> mapper = v -> String.format("%s : %s",
			// v.getKey(), v.getValue());

			ValueUtil.toTF_IDF(new String[] { str }).stream().map(mapper).reduce(accumulator).ifPresent(txtTfIdf::setText);

		} catch (Exception e) {
			txtResult.setText(ValueUtil.toString(e));
			txtTfIdf.setText("");
		}

	}

	static class AlgorismExtractor {

		private static Reflections reflections;
		static final String PACKAGE_PREFIX = "com.kohlschutter.boilerpipe.extractors";
		static {

			reflections = new Reflections(
					new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(PACKAGE_PREFIX, ClassLoader.getSystemClassLoader())));

		}

		public static List<Class<? extends ExtractorBase>> getAvaliables() {
			return Stream.of(reflections.getSubTypesOf(ExtractorBase.class)).filter(v -> {

				if ((v.getClass().getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC) {
					Optional<Constructor<?>> findAny = Stream.of(v.getClass().getConstructors()).filter(c -> c.getParameterCount() == 0)
							.findFirst();
					return findAny.isPresent();
				}

				return false;
			}).flatMap(v -> v.stream()).collect(Collectors.toList());
		}

	}

}
