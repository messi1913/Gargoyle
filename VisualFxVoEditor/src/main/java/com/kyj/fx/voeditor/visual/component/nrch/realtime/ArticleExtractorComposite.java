/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.nrch.realtime
 *	작성일   : 2016. 12. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.nrch.realtime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.kohlschutter.boilerpipe.document.TextDocument;
import com.kohlschutter.boilerpipe.extractors.ArticleExtractor;
import com.kohlschutter.boilerpipe.extractors.ExtractorBase;
import com.kohlschutter.boilerpipe.extractors.KeepEverythingExtractor;
import com.kohlschutter.boilerpipe.sax.BoilerpipeSAXInput;
import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.voeditor.visual.framework.KeyValue;
import com.kyj.fx.voeditor.visual.framework.RealtimeSearchItemVO;
import com.kyj.fx.voeditor.visual.framework.URLModel;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.RequestUtil;
import com.kyj.fx.voeditor.visual.util.ResponseHandler;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
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
	private JFXComboBox<URLModel> cbSmmy;

	@FXML
	private JFXTextArea txtResult, txtTfIdf, txtSummary;
	@FXML
	private TextField txtUrl;
	@FXML
	private WebView webPreview;

	private RealtimeSearchItemVO userData;

	/**
	 * 불필요한 URL을 제거하기 위해 URL정보를 저장관리하는 클래스.
	 *
	 * @최초생성일 2016. 12. 6.
	 */
	private URLFilterRepository filterRepository;

	public ArticleExtractorComposite() {
		filterRepository = new URLFilterRepository();
		FxUtil.loadRoot(ArticleExtractorComposite.class, this, e -> LOGGER.error(ValueUtil.toString(e)));
	}

	public ArticleExtractorComposite(RealtimeSearchItemVO userData) {
		this();
		this.userData = userData;
	}

	ObjectProperty<TextDocument> cache = new SimpleObjectProperty<>();

	@FXML
	public void initialize() {

		cbAlgorisms.getItems().addAll(ValueUtil.TFIDF.getAvaliablesExtractorBase());
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

			Class<? extends ExtractorBase> algorism = n;
			String baseURI = txtUrl.getText(); // webPreview.getEngine().getDocument().getBaseURI();

			if (ValueUtil.isEmpty(baseURI))
				return;

			RealtimeSearchItemVO vo = new RealtimeSearchItemVO();
			vo.setLink(baseURI);

			try {
				URLModel htmlContent = getHTMLContent(vo);
				if (!htmlContent.isEmpty()) {

					String boilderPipe = boilderPipe(algorism, htmlContent.getContent());
					txtResult.setText(boilderPipe);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		});

		cbSmmy.valueProperty().addListener((oba, o, n) -> {
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

		cbSmmy.setCellFactory(param -> {
			TextFieldListCell<URLModel> textFieldListCell = new TextFieldListCell<>(converter);
			textFieldListCell.setMaxWidth(600d);
			textFieldListCell.setPrefWidth(600d);
			return textFieldListCell;
		});

		/** Size the combo-box drop down list. */

		cbSmmy.setConverter(converter);
		Platform.runLater(() -> {
			request(userData);

			// Platform.runLater(() -> {
			// WebEngine engine = webPreview.getEngine();
			// engine.getLoadWorker().stateProperty().addListener((ChangeListener<State>)
			// (ov, oldState, newState) -> {
			// LOGGER.debug("{} - {}", newState.name(), engine.getLocation());
			//
			// if (newState == Worker.State.RUNNING) {
			// String location = engine.getLocation();
			// if (ValueUtil.isNotEmpty(location)) {
			//
			// Class<? extends ExtractorBase> algorism = cbAlgorisms.getValue();
			// RealtimeSearchItemVO vo = new RealtimeSearchItemVO();
			// vo.setLink(location);
			// try {
			// updateMainContent(algorism, getHTMLContent(vo));
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// }
			//
			// }
			// });

			// txtUrl.textProperty().addListener((oba, o, n) -> {
			//
			// if (ValueUtil.isNotEmpty(n)) {
			// RealtimeSearchItemVO realtimeSearchItemVO = new
			// RealtimeSearchItemVO();
			// realtimeSearchItemVO.setLink(n);
			// request(userData);
			// }
			//
			// });

		});

		// engine.load(url);
		// engine.getLoadWorker().messageProperty().addListener((oba, o, n) -> {
		// LOGGER.debug("Browser Message : {}", n);
		// });

		// engine.setJavaScriptEnabled(true);

		// HTML 코드를 engine에서 얻기위한 처리가 필요함.

		// org.w3c.dom.Document doc = engine.getDocument();
		// try {
		// Transformer transformer =
		// TransformerFactory.newInstance().newTransformer();
		// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		// transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		// transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		// transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		// transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
		// "4");
		//
		// try (ByteArrayOutputStream outputStream = new
		// ByteArrayOutputStream()) {
		//
		// try (OutputStreamWriter writer = new OutputStreamWriter(outputStream,
		// "UTF-8")) {
		// transformer.transform(new DOMSource(doc), new StreamResult(writer));
		// Class<? extends ExtractorBase> algorism = cbAlgorisms.getValue();
		// String boilderPipe = boilderPipe(algorism,
		// outputStream.toString("UTF-8"));
		// txtResult.setText(boilderPipe);
		// }
		// }
		//
		// } catch (Exception ex) {
		// txtResult.setText(
		// String.format("[%s] Something Problems Occured. \n\nStackTrace : {}",
		// newState.name(), ValueUtil.toString(ex)));
		// }
		// } else {
		// txtResult.setText("Waitings.... " + newState.name());
		// }
		// });
	}

	public void request(RealtimeSearchItemVO userData) {
		SingleSelectionModel<Class<? extends ExtractorBase>> selectionModel = cbAlgorisms.getSelectionModel();
		Class<? extends ExtractorBase> selectAlgorism = selectionModel.getSelectedItem();
		if (selectAlgorism != null) {
			request(selectAlgorism, userData);
		}
	}

	public void request(String url) {
		SingleSelectionModel<Class<? extends ExtractorBase>> selectionModel = cbAlgorisms.getSelectionModel();
		Class<? extends ExtractorBase> selectAlgorism = selectionModel.getSelectedItem();
		if (selectAlgorism != null) {

			RealtimeSearchItemVO vo = new RealtimeSearchItemVO();
			vo.setLink(url);
			request(selectAlgorism, vo);
		}
	}

	private BinaryOperator<String> accumulator = (v1, v2) -> {
		return String.format("%s\n%s", v1.toString(), v2.toString());
	};
	private Function<? super KeyValue, String> mapper = v -> String.format("[%s] : [%1.5f]", v.getKey(), v.getValue());

	// public void request(Class<? extends ExtractorBase> algorism,
	// RealtimeSearchItemVO userData) {
	// request(algorism, userData.getLink());
	// }

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 6.
	 * @param selectAlgorism
	 * @param url
	 */
	private void request(Class<? extends ExtractorBase> algorism, RealtimeSearchItemVO vo) {

		String link = vo.getLink();
		if (ValueUtil.isEmpty(link))
			return;

		try {
			URLModel model = getHTMLContent(link);

			if (!model.isEmpty()) {
				updateMainContent(algorism, model);

				String keyword = vo.getKeyword();
				if (ValueUtil.isNotEmpty(keyword)) {
					updateNewRealContent(algorism, model, OtherNews.getOtherNewsPages(keyword));
				} else {
					updateNewRealContent(algorism, model, Collections.emptyList());
				}
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
	private void updateNewRealContent(Class<? extends ExtractorBase> algorism, URLModel model, List<String> otherLinks) {
		Collection<String> findAllLink = findAllLink(model.getContent());
		List<String> arrayList = new ArrayList<>();
		arrayList.addAll(findAllLink);
		arrayList.addAll(otherLinks);
		updateNewRealContent(arrayList);
	}

	private void updateNewRealContent(Collection<String> links) {

		URLModel[] array = links.parallelStream().map(link -> {
			URLModel model = URLModel.empty();
			try {

				BiFunction<InputStream, Charset, URLModel> response = new BiFunction<InputStream, Charset, URLModel>() {
					@Override
					public URLModel apply(InputStream is, Charset charset) {

						URLModel urlModel = URLModel.empty();

						try {

							byte[] byteArray = IOUtils.toByteArray(is);
							String content = ValueUtil.toString(byteArray, charset);
							if (content == null)
								return URLModel.empty();

							Document parse = Jsoup.parse(content, "http");
							Element head = parse.head();
							Elements title = head.getElementsByTag("title");

							List<Node> childNodes = head.childNodes();

							//						Charset contentCharset = charset;
							for (Node n : childNodes) {
								if ("meta".equals(n.nodeName())) {

									String attr = n.attr("content");
									//text/html; charset=utf-8
									if (ValueUtil.isNotEmpty(attr)) {
										String[] split = attr.split(";");
										if (split != null && split.length == 2) {
											String[] split2 = split[1].split("=");
											if (split2 != null && split2.length == 2) {
												Charset forName = Charset.forName(split2[1]);
												if (!charset.equals(forName)) {
													content = new String(byteArray, forName);
													break;
												}
											}

										}
									}
								}
							}

							//						String content = ValueUtil.toString(is, charset);
							//						if (content == null)
							//							return URLModel.empty();
							//
							//						Document parse = Jsoup.parse(content, "http");
							//						Element head = parse.head();
							//						Elements title = head.getElementsByTag("title");
							//						Elements children = head.children();
							//
							//						List<Node> childNodes = head.childNodes();

							urlModel = new URLModel(link, content);
							urlModel.setTitle(title.text());

						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							try {
								is.close();
							} catch (Exception e) {
								LOGGER.error(ValueUtil.toString(e));
							}
						}

						return urlModel;

					}
				};

				model = RequestUtil.req200(new URL(link), response, false);

			} catch (Exception e) {
				return URLModel.empty();
			}

			return model;
		}).filter(v -> !v.isEmpty()).map(v ->

		{
			URLModel model = URLModel.empty();

			String url = v.getUrl();

			String content = v.getContent();

			ExtractorBase instance = null;
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
				content = ValueUtil.TFIDF.getNewsContent(instance, source);
				// v.setContent(content);

				model.setUrl(v.getUrl());
				model.setContent(content);
				model.setTitle(v.getTitle());

			} catch (Exception e) {
				model = URLModel.empty();
				e.printStackTrace();
			}

			return model;
		}).filter(v -> !v.isEmpty()).toArray(URLModel[]::new);

		cbSmmy.getItems().clear();
		cbSmmy.getItems().addAll(array);
		ValueUtil.toTF_IDF(array).stream().map(mapper).reduce(accumulator).ifPresent(txtTfIdf::setText);
		cbSmmy.getSelectionModel().select(0);
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
					.filter(filterRepository.getFilter()).collect(Collectors.toSet());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return collect;
	}

	private ResponseHandler<String> handler = new ResponseHandler<String>() {

		@Override
		public String apply(InputStream t, Integer u) {
			if (u == 200) {
				String contentEncoding = getContentEncoding();
				Charset charset = Charset.forName("UTF-8");
				if (ValueUtil.isNotEmpty(contentEncoding))
					charset = Charset.forName(contentEncoding);
				else {

					Map<String, List<String>> headers = getHeaders();
					List<String> list = headers.get("Content-Type");
					Optional<String> findAny = list.stream().filter(v -> {
						String[] expects = v.split(";");
						if (expects.length == 2) {
							expects = expects[1].split("=");
							if (expects.length == 2) {
								return Charset.isSupported(expects[1]);
							}
						}
						return false;
					}).findAny();

					if (findAny.isPresent()) {
						String wow = findAny.get().split(";")[1].split("=")[1];
						charset = Charset.forName(wow);
						LOGGER.debug("{}", charset);
					} else {
						LOGGER.debug("empty encoding.. ");
					}

				}
				return ValueUtil.toString(t, charset);

			}

			return null;
		}
	};

	private URLModel getHTMLContent(RealtimeSearchItemVO userData) throws Exception {
		if (userData == null || ValueUtil.isEmpty(userData.getLink()))
			return null;
		URL url = toURL(userData);

		String str = RequestUtil.requestSSL(url, handler);
		if (str == null)
			return URLModel.empty();

		return new URLModel(userData.getLink(), str);
	}

	private URLModel getHTMLContent(String url) throws Exception {
		if (ValueUtil.isEmpty(url))
			return null;
		URL url2 = toURL(url);
		String str = RequestUtil.req(url2, handler);

		if (str == null)
			return URLModel.empty();

		return new URLModel(url2.toString(), str);
	}

	//	public static String reqeustSSL_JSONString(URL url, RequestHandler<String> response) throws Exception {
	//		return RequestUtil.requestSSL(url, (is, code) -> {
	//			String dirtyConent = "";
	//			if (200 == code) {
	//				// 버퍼로 그냥 읽어봐도 되지만 인코딩 변환을 추후 쉽게 처리하기 위해 ByteArrayOutputStream을
	//				// 사용
	//
	//				try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
	//					// int read = -1;
	//					byte[] b = new byte[4096];
	//					while (is.read(b) != -1) {
	//						out.write(b);
	//					}
	//					out.flush();
	//					dirtyConent = out.toString("UTF-8");
	//				} catch (Exception e) {
	//					LOGGER.error(ValueUtil.toString(e));
	//				}
	//			} else {
	//				LOGGER.warn("not unnomal response code");
	//			}
	//			return dirtyConent;
	//
	//		});
	//	}

	private URL toURL(RealtimeSearchItemVO userData) throws MalformedURLException {
		return toURL(userData.getLink());
	}

	private URL toURL(URLModel userData) throws MalformedURLException {
		return toURL(userData.getUrl());
	}

	private URL toURL(String url) throws MalformedURLException {
		if (url.startsWith("https:") || url.startsWith("http:"))
			return new URL(url);
		return new URL("https:" + url);
	}

	private void updateMainContent(Class<? extends ExtractorBase> algorism, URLModel userData) {
		try {

			URL url = toURL(userData);
			txtUrl.setText(url.toString());
			webPreview.getEngine().load(txtUrl.getText());
			String content = userData.getContent();
			txtResult.setText(boilderPipe(algorism, content));
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

			content = ValueUtil.TFIDF.newInsntance(algorism).getText(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

}
