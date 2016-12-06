/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.nrch.realtime
 *	작성일   : 2016. 12. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.nrch.realtime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.kohlschutter.boilerpipe.document.TextDocument;
import com.kohlschutter.boilerpipe.extractors.ArticleExtractor;
import com.kohlschutter.boilerpipe.extractors.ArticleSentencesExtractor;
import com.kohlschutter.boilerpipe.extractors.ExtractorBase;
import com.kohlschutter.boilerpipe.extractors.KeepEverythingExtractor;
import com.kohlschutter.boilerpipe.extractors.KeepEverythingWithMinKWordsExtractor;
import com.kohlschutter.boilerpipe.sax.BoilerpipeSAXInput;
import com.kyj.fx.voeditor.visual.framework.KeyValue;
import com.kyj.fx.voeditor.visual.framework.RealtimeSearchItemVO;
import com.kyj.fx.voeditor.visual.framework.URLModel;
import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.RequestUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import javafx.util.StringConverter;

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
	private JFXComboBox<URLModel> cbURLSmmy;

	@FXML
	private JFXTextArea txtResult, txtTfIdf, txtSummary;
	@FXML
	private TextField txtUrl;
	@FXML
	private WebView webPreview;

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

		cbAlgorisms.getItems().addAll(ValueUtil.HTML.getAvaliablesExtractorBase());
		cbAlgorisms.getSelectionModel().select(ArticleExtractor.class);
		cbAlgorisms.setCellFactory(new Callback<ListView<Class<? extends ExtractorBase>>, ListCell<Class<? extends ExtractorBase>>>() {

			@Override
			public ListCell<Class<? extends ExtractorBase>> call(ListView<Class<? extends ExtractorBase>> param) {
				return new TextFieldListCell<>(new StringConverter<Class<? extends ExtractorBase>>() {

					@Override
					public String toString(Class<? extends ExtractorBase> object) {
						return object.getSimpleName();
					}

					@Override
					public Class<? extends ExtractorBase> fromString(String string) {
						// TODO Auto-generated method stub
						return null;
					}
				});
			}
		});
		cbAlgorisms.setConverter(new StringConverter<Class<? extends ExtractorBase>>() {

			@Override
			public String toString(Class<? extends ExtractorBase> object) {
				return object.getSimpleName();
			}

			@Override
			public Class<? extends ExtractorBase> fromString(String string) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		cbAlgorisms.valueProperty().addListener((oba, o, n) -> {
			request(userData);
		});

		cbURLSmmy.valueProperty().addListener((oba, o, n) -> {
			txtSummary.setText(n.getUrl());
			txtSummary.nextWord();
			txtSummary.appendText(n.getContent());
		});

		StringConverter<URLModel> converter = new StringConverter<URLModel>() {
			@Override
			public String toString(URLModel object) {
				return String.format("[%s] - %s", object.getTitle(), object.getUrl());
			}

			@Override
			public URLModel fromString(String string) {
				return null;
			}
		};

		cbURLSmmy.setCellFactory(param -> {
			TextFieldListCell<URLModel> textFieldListCell = new TextFieldListCell<>(converter);
			textFieldListCell.setMaxWidth(600d);
			textFieldListCell.setPrefWidth(600d);
			return textFieldListCell;
		});

		/** Size the combo-box drop down list. */

		cbURLSmmy.setConverter(converter);
		Platform.runLater(() -> {
			request(userData);
		});

		WebEngine engine = webPreview.getEngine();
		engine.getLoadWorker().messageProperty().addListener((oba, o, n) -> {
			LOGGER.debug("Browser Message : {}", n);
		});

		//HTML 코드를 engine에서 얻기위한 처리가 필요함.
		engine.getLoadWorker().stateProperty().addListener((ChangeListener<State>) (ov, oldState, newState) -> {
			if (newState == Worker.State.SUCCEEDED) {
				org.w3c.dom.Document doc = engine.getDocument();
				try {
					Transformer transformer = TransformerFactory.newInstance().newTransformer();
					transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
					transformer.setOutputProperty(OutputKeys.METHOD, "xml");
					transformer.setOutputProperty(OutputKeys.INDENT, "yes");
					transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
					transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

					try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

						try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8")) {
							transformer.transform(new DOMSource(doc), new StreamResult(writer));
							Class<? extends ExtractorBase> algorism = cbAlgorisms.getValue();
							String boilderPipe = boilderPipe(algorism, outputStream.toString("UTF-8"));
							txtResult.setText(boilderPipe);
						}
					}

				} catch (Exception ex) {
					LOGGER.error(ValueUtil.toString(ex));
				}
			}
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
			URLModel model = getHTMLContent(userData);

			if (!model.isEmpty()) {
				updateMainContent(algorism, model);
				updateNewRealContent(algorism, model);
			}

		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 5.
	 * @param algorism
	 * @param userData2
	 */
	private void updateNewRealContent(Class<? extends ExtractorBase> algorism, URLModel model) {
		Collection<String> findAllLink = findAllLink(model.getContent());
		updateNewRealContent(findAllLink);
	}

	private void updateNewRealContent(Collection<String> links) {

		URLModel[] array = links.parallelStream().map(link -> {
			URLModel model = URLModel.empty();
			try {

				BiFunction<InputStream, Charset, URLModel> response = (is, charset) -> {

					URLModel urlModel = URLModel.empty();
					try {

						String content = ValueUtil.toString(is, charset);

						Document parse = Jsoup.parse(content, "http");
						Element head = parse.head();
						Elements title = head.getElementsByTag("title");

						urlModel = new URLModel(link, content);
						urlModel.setTitle(title.text());
					} catch (IOException e) {
						return URLModel.empty();
					} finally {
						try {
							is.close();
						} catch (Exception e) {
							LOGGER.error(ValueUtil.toString(e));
						}
					}

					return urlModel;

				};

				if (link.startsWith("https")) {
					model = RequestUtil.reqeustSSL200(new URL(link), response, false);
				} else {
					model = RequestUtil.request200(new URL(link), response, false);
				}
			} catch (Exception e) {
				return URLModel.empty();
			}

			return model;
		}).filter(v -> !v.isEmpty()).map(v ->

		{
			String url = v.getUrl();

			String content = v.getContent();

			ExtractorBase instance = ArticleSentencesExtractor.getInstance();
			// 트위터의경우 특별한 알고리즘으로 텍스트 불러옴.
			if (url.contains("twitter.com")) {
				instance = KeepEverythingExtractor.INSTANCE;
			} else {
				instance = ArticleExtractor.getInstance();// ArticleSentencesExtractor.getInstance();
			}

			// ArticleExtractor.getInstance();

			InputSource source = new InputSource(new StringReader(content));
			source.setEncoding("UTF-8");
			try {
				content = ValueUtil.HTML.getNewsContent(instance, source);
				v.setContent(content);
			} catch (Exception e) {
				v = URLModel.empty();
				e.printStackTrace();
			}

			return v;
		}).filter(v -> !v.isEmpty()).toArray(URLModel[]::new);

		cbURLSmmy.getItems().clear();
		cbURLSmmy.getItems().addAll(array);

		ValueUtil.toTF_IDF(array).stream().map(mapper).reduce(accumulator).ifPresent(txtTfIdf::setText);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 5.
	 * @param content
	 * @return
	 */
	private Collection<String> findAllLink(String content) {

		Set<String> collect = Collections.emptySet();
		try (ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes())) {
			Document parse = Jsoup.parse(in, "UTF-8", "https");

			// Repository로 저장 관리할 필요성이 있음.
			/* a 태그 만 추출. */
			Elements elementsByTag = parse.getElementsByTag("a");// parse.getElementsByTag("a");

			collect = elementsByTag.stream().filter(e -> e.hasAttr("href")).map(e -> e.attr("href").trim())
					/* http or https인 링크만 추출. */
					.filter(e -> e.startsWith("http") || e.startsWith("https"))

					/* 검색에 불필요한 URL */
					.filter(v -> {

						if ("https://submit.naver.com/".equals(v))
							return false;

						else if ("http://www.naver.com".equals(v))
							return false;

						else if (v.startsWith("https://nid.naver.com"))
							return false;

						else if (v.startsWith("http://searchad.naver.com"))
							return false;

						else if (v.contains("namu.wiki"))
							return false;

						else if (v.contains("wikipedia.org"))
							return false;

						else if (v.startsWith("http://music.naver.com"))
							return false;

						else if (v.startsWith("http://m.post.naver.com"))
							return false;

						else if (v.startsWith("http://tvcast.naver.com"))
							return false;

						else if (v.startsWith("http://shopping.naver.com"))
							return false;

						else if (v.startsWith("https://help.naver"))
							return false;

						else if (v.startsWith("http://www.navercorp.com"))
							return false;

						else if (v.startsWith("http://book.naver.com"))
							return false;

						else if (v.startsWith("http://www.cwpyo.com"))
							return false;

						else if (v.startsWith("http://navercast.naver.com"))
							return false;

						else if (v.startsWith("http://localad.naver.com"))
							return false;

						else if (v.startsWith("http://map.naver.com/"))
							return false;

						else if (v.startsWith("http://pay.naver.com"))
							return false;

						return v.contains("news");
					}).collect(Collectors.toSet());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return collect;
	}

	private URLModel getHTMLContent(RealtimeSearchItemVO userData) throws Exception {
		if (userData == null || ValueUtil.isEmpty(userData.getLink()))
			return null;
		URL url = toURL(userData);
		String str = RequestUtil.reqeustSSL(url, (st, code) -> {
			if (code == 200) {
				try {
					return ValueUtil.toString(st);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		});
		if (str == null)
			return URLModel.empty();

		return new URLModel(userData.getLink(), str);
	}

	private URL toURL(RealtimeSearchItemVO userData) throws MalformedURLException {
		return new URL("https:" + userData.getLink());
	}

	private URL toURL(URLModel userData) throws MalformedURLException {
		return new URL("https:" + userData.getUrl());
	}

	private void updateMainContent(Class<? extends ExtractorBase> algorism, URLModel userData) {
		try {

			txtUrl.setText(toURL(userData).toString());
			webPreview.getEngine().load(txtUrl.getText());

		} catch (Exception e) {
			txtResult.setText(ValueUtil.toString(e));
			txtTfIdf.setText("");
		}
	}

	private String boilderPipe(Class<? extends ExtractorBase> algorism, String content) {
		try (StringReader characterStream = new StringReader(content)) {
			InputSource inputSource = new InputSource(characterStream);
			inputSource.setEncoding("UTF-8");

			final BoilerpipeSAXInput in = new BoilerpipeSAXInput(inputSource);
			final TextDocument doc = in.getTextDocument();

			if (algorism == KeepEverythingWithMinKWordsExtractor.class) {
				content = new KeepEverythingWithMinKWordsExtractor(10).getText(doc);
			}

			try {
				content = algorism.newInstance().getText(doc);
			} catch (IllegalAccessException e) {
				Field declaredField = algorism.getDeclaredField("INSTANCE");
				ExtractorBase instance = (ExtractorBase) declaredField.get(null);
				content = instance.getText(doc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

}
