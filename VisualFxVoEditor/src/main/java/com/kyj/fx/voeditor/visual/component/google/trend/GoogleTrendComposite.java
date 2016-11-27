/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.google.trend
 *	작성일   : 2016. 11. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.google.trend;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.bar.GargoyleSynchLoadBar;
import com.kyj.fx.voeditor.visual.component.chart.service.BaseGoogleTrendChart;
import com.kyj.fx.voeditor.visual.component.chart.service.ChartOverTooltip;
import com.kyj.fx.voeditor.visual.component.chart.service.GoogleTrendChartEvent;
import com.kyj.fx.voeditor.visual.util.DateUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.RequestUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.web.WebView;
import jfxtras.scene.layout.HBox;

/**
 * 구글 트랜드를 표현하기 위한 컴포넌트
 *
 * @author KYJ
 *
 */
public class GoogleTrendComposite extends BorderPane {

	public static final String TITLE = "Google Trend";

	private static final Logger LOGGER = LoggerFactory.getLogger(GoogleTrendComposite.class);
	/**
	 * 검색 URL
	 *
	 * @최초생성일 2016. 11. 6.
	 */
	final String GOOGLE_SEARCH_URL_TEMPLATE = "https://www.google.co.kr/search?q=:keyword&biw=1920&bih=990&tbas=0&source=lnt&tbs=cdr%3A1,cd_min%3A:startDate,cd_max%3A:endDate&tbm=";
	/**
	 * 구글 트랜드 차트 URL
	 *
	 * @최초생성일 2016. 11. 6.
	 */
	final String GOOGLE_TREND_URL_TEMPLATE = "https://www.google.com/trends/fetchComponent?cid=TIMESERIES_GRAPH_0&export=3&q=:keywords#if($gprop)&gprop=:gprop#end#if($geo)&geo=:geo#end";

	//	private BorderPane borRoot;
	private SplitPane spMainContent;

	private TextField[] srchItems;
	private BaseGoogleTrendChart chart;
	private Button chkShowBrowser;
	private TilePane browserParent;
	private WebView[] webViews = new WebView[] { new WebView(), new WebView(), new WebView(), new WebView() };

	private TextField txtSrch1 = new TextField();
	private TextField txtSrch2 = new TextField();
	private TextField txtSrch3 = new TextField();
	private TextField txtSrch4 = new TextField();

	public GoogleTrendComposite() {
		//텍스트 필드 정의

		srchItems = new TextField[] { txtSrch1, txtSrch2, txtSrch3, txtSrch4 };
		//텍스트 필드 이벤트 정의
		Stream.of(srchItems).forEach(t -> {
			t.addEventHandler(KeyEvent.KEY_PRESSED, this::txtSrchItemOnKeyPress);
		});

		//검색버튼
		Button btnSrch = new Button("Srch");
		//이벤트 정의
		btnSrch.setOnAction(this::btnSrchOnAction);
		//브라우져 보임여부 정의
		chkShowBrowser = new Button("show browser");
		chkShowBrowser.setOnAction(this::chkShowBrowserOnAction);

		HBox hboxSrchTextFieldsPane = new HBox(5);
		hboxSrchTextFieldsPane.getChildren().addAll(srchItems);
		HBox hboxFilter = new HBox(5, hboxSrchTextFieldsPane, btnSrch, chkShowBrowser);

		hboxSrchTextFieldsPane.setPadding(new Insets(10, 5, 5, 5));
		hboxSrchTextFieldsPane.setAlignment(Pos.CENTER);
		hboxFilter.setAlignment(Pos.CENTER);

		chart = new BaseGoogleTrendChart(new CategoryAxis(), new NumberAxis(0, 100, 25));
		chart.setVerticalGridLinesVisible(false);

		//webview 초기화
		Stream.of(webViews).forEach(webView -> {
			webView.getEngine().setJavaScriptEnabled(true);
		});

		browserParent = new TilePane(2, 2);
		//		browserParent.setPrefColumns(2);
		//		browserParent.setPrefRows(2);
		browserParent.setAlignment(Pos.CENTER);

		chart.addEventHandler(GoogleTrendChartEvent.GOOGLE_CHART_INTERSECT_NODE_CLICK, ev -> {
			if (ev.getClickCount() == 2) {

				List<Node> contents = ev.getContents();

				List<ChartOverTooltip> collect = contents.stream().map(v -> v.getUserData()).filter(v -> v != null)
						.map(v -> (ChartOverTooltip) v).sorted((a1, a2) -> {
					try {
						String xValue = a1.getData().getXValue();
						Date date = DateUtil.toDate(xValue, "yyyy년 MM월");
						String xValue2 = a2.getData().getXValue();
						Date date2 = DateUtil.toDate(xValue2, "yyyy년 MM월");
						return date.compareTo(date2);
					} catch (Exception e) {
					}
					return 0;
				}).limit(4).collect(Collectors.toList());

				browserParent.getChildren().clear();

				IntStream.iterate(0, v -> v + 1).limit(4).forEach(i -> {

					if (collect.size() <= i) {
						webViews[i].getEngine().loadContent("", "text/html");
					} else {
						browserParent.getChildren().add(webViews[i]);
						ChartOverTooltip tip = collect.get(i);

						String googoleSearchUrl = null;

						LOGGER.debug("search url : {}", googoleSearchUrl);
						//						if (googoleSearchUrl != null) {

						GargoyleSynchLoadBar<Integer> gargoyleSynchLoadBar = new GargoyleSynchLoadBar<>(FxUtil.getWindow(this),
								new Task<Integer>() {

							@Override
							protected Integer call() throws Exception {
								try {
									try {
										String xValue = tip.getData().getXValue();
										Date date = DateUtil.toDate(xValue, "yyyy년 MM월");
										Date start = DateUtil.getFirstDateOfMonth(date);
										Date end = DateUtil.getLastDateOfMonth(date);
										//											googoleSearchUrl =
										String googoleSearchUrl2 = getGoogoleSearchUrl(tip.getColumnName(), start, end);
										String content = loadContent(new URL(googoleSearchUrl2));
										updateValue(i);
										updateMessage(content);
										//										updateMessage(googoleSearchUrl2);

									} catch (Exception e) {
										e.printStackTrace();
									}

								} catch (Exception e) {
									e.printStackTrace();
								}
								return i;
							}

						});

						gargoyleSynchLoadBar.setOnSucceeded(work -> {

							String message = work.getSource().getMessage();
							Integer value = gargoyleSynchLoadBar.getValue();
							webViews[value].getEngine().loadContent(message, "text/html");
							//							webViews[value].getEngine().load(message);
							work.getSource().getWorkDone();
						});
						gargoyleSynchLoadBar.start();
						//						}

					}
				});

			}

		});

		spMainContent = new SplitPane(chart);
		spMainContent.setOrientation(Orientation.VERTICAL);

		TitledPane titledPane = new TitledPane("Filter", hboxFilter);

		setTop(titledPane);
		setCenter(spMainContent);
	}

	public void searchKeywords(String keyword1) {
		searchKeywords(keyword1, null);
	}

	public void searchKeywords(String keyword1, String keyword2) {
		searchKeywords(keyword1, keyword2, null);
	}

	public void searchKeywords(String keyword1, String keyword2, String keyword3) {
		searchKeywords(keyword1, keyword2, keyword3, null);
	}

	public void searchKeywords(String keyword1, String keyword2, String keyword3, String keyword4) {
		List<String> keywords = new ArrayList<>();
		if (ValueUtil.isNotEmpty(keyword1)) {
			keywords.add(keyword1);
		}
		if (ValueUtil.isNotEmpty(keyword2)) {
			keywords.add(keyword2);
		}
		if (ValueUtil.isNotEmpty(keyword3)) {
			keywords.add(keyword3);
		}
		if (ValueUtil.isNotEmpty(keyword4)) {
			keywords.add(keyword4);
		}

		IntStream.iterate(0, v -> v + 1).limit(keywords.size()).forEach(idx -> {
			srchItems[idx].setText(keywords.get(idx));
		});

		Platform.runLater(() -> {
			btnSrchOnAction(new ActionEvent());
		});
		//		btnSrchOnAction(new ActionEvent());

	}

	public void chkShowBrowserOnAction(ActionEvent e) {

		//		if (chkShowBrowser.isSelected()) {

		FxUtil.createStageAndShow(new BorderPane(browserParent), stage -> {

			chkShowBrowser.setDisable(true);
			stage.setOnCloseRequest(ev -> {
				chkShowBrowser.setDisable(false);
			});
		});
		//		}

		//		if (chkShowBrowser.isSelected()) {
		//			spMainContent.getItems().add(spBrowserContent);
		//		} else {
		//			spMainContent.getItems().removeIf(v -> {
		//				return v == spBrowserContent;
		//			});
		//		}

	}

	/**
	 * 검색 버튼 클릭 이벤트
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 4.
	 * @param e
	 */
	public void btnSrchOnAction(ActionEvent e) {
		txtSrchItemOnKeyPress(
				new KeyEvent(srchItems[0], null, KeyEvent.KEY_PRESSED, null, null, KeyCode.ENTER, false, false, false, false));
	}

	/**
	 * 텍스트필드 키 이벤트
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 4.
	 * @param e
	 */
	public void txtSrchItemOnKeyPress(KeyEvent e) {

		//Enter
		if (e.getCode() == KeyCode.ENTER) {
			Optional<String> reduce = Stream.of(srchItems).map(field -> field.getText()).filter(ValueUtil::isNotEmpty)
					.reduce((t1, t2) -> t1.concat(",").concat(t2));
			reduce.ifPresent(t -> {
				try {
					String jsonString = request(t);
					chart.setSource(jsonString);
				} catch (Exception e1) {
					LOGGER.error(ValueUtil.toString(e1));
				}

			});
		}

	}

	private String loadContent(URL url) throws IOException, Exception {
		return RequestUtil.reqeustSSL(url, (is, code) -> {
			if (code == 200) {
				try {
					return ValueUtil.toString(is);
				} catch (Exception e) {
					LOGGER.error(ValueUtil.toString(e));
				}
			}
			return "";
		});
	}

	private String getGoogoleSearchUrl(String _keyword, Date start, Date end) throws UnsupportedEncodingException {
		String keyword = _keyword;

		String startDate = DateUtil.getDateAsStr(start, "yyyy.MM.dd."); //"2016.10.31.";
		String endDate = DateUtil.getDateAsStr(end, "yyyy.MM.dd."); //"2016.11.02.";

		keyword = URLEncoder.encode(keyword, "UTF-8");
		startDate = URLEncoder.encode(startDate, "UTF-8");
		endDate = URLEncoder.encode(endDate, "UTF-8");

		//%3A -> :

		Map<String, Object> hashMap = new HashMap<>();
		hashMap.put("keyword", keyword);
		hashMap.put("startDate", startDate);
		hashMap.put("endDate", endDate);

		String formattedURL = ValueUtil.getVelocityToText(GOOGLE_SEARCH_URL_TEMPLATE, hashMap, true, null, str -> str);

		LOGGER.debug("decode : {}", URLDecoder.decode(formattedURL, "UTF8"));
		return formattedURL;

	}

	private String request(String keywords) throws Exception {
		URL url = new URL(createUrl(keywords));
		String jsonString = RequestUtil.reqeustSSL(url, (is, code) -> {
			String result = "";
			if (200 == code || 203 == code) {

				if (code == 203) {
					LOGGER.warn("not unnomal response code {}", code);
				}

				try {

					String dirtyConent = "";
					//버퍼로 그냥 읽어봐도 되지만 인코딩 변환을 추후 쉽게 처리하기 위해 ByteArrayOutputStream을 사용

					ByteArrayOutputStream out = new ByteArrayOutputStream();
					int read = -1;
					while ((read = is.read()) != -1) {
						out.write(read);
					}
					out.flush();
					dirtyConent = out.toString();

					LOGGER.debug("dirty content {} \n", dirtyConent);

					//원본 데이터에서 불필요한 부분 삭제
					String regexMatch = ValueUtil.regexMatch("\\(.*\\)", dirtyConent, str -> {
						return str.substring(1, str.length() - 1);
					});

					//데이터중 new Date(xxx,x,x) 라고 JSON이 인식못하는 텍스트를 제거함.
					regexMatch = ValueUtil.regexReplaceMatchs("new Date\\([0-9,]+\\)", regexMatch, str -> {
						int startIdx = str.indexOf('(');
						int endIdx = str.indexOf(')', startIdx);

						if (startIdx == -1 || endIdx == -1)
							return str;

						startIdx++;
						return String.format("\"%s\"", str.substring(startIdx, endIdx).replaceAll(",", "-"));
					});

					result = regexMatch;
					//결과출력
					LOGGER.debug("result string : {} \n", result);
					//					jsonObject = ValueUtil.toJSONObject(regexMatch);

				} catch (Exception e) {
					LOGGER.error(ValueUtil.toString(e));
				}

			} else {
				throw new RuntimeException("Connect Fail.");
			}
			//			else {
			//
			//				try {
			//					result = ValueUtil.toString(GoogleTrendExam2.class.getResourceAsStream("GoogleTrendSample.json"));
			//				} catch (Exception e) {
			//					LOGGER.error(ValueUtil.toString(e));
			//				}
			//			}

			return result;
		});
		return jsonString;
	}

	String createUrl(String keywords) throws UnsupportedEncodingException {
		String geo = null;//"KR";
		String date = "all";
		String gprop = null;//"news"; //news youtube, froogle, images
		//		String keywords = "%EB%B0%95%EA%B7%BC%ED%98%9C,%EC%B5%9C%EC%88%9C%EC%8B%A4";
		String converted_keywords = URLEncoder.encode(keywords, "UTF-8");

		//핫 트랜드 https://www.google.com/trends/hottrends/atom

		Map<String, Object> hashMap = new HashMap<>();
		hashMap.put("geo", geo);
		hashMap.put("date", date);
		hashMap.put("gprop", gprop);
		hashMap.put("keywords", converted_keywords);

		String formattedURL = ValueUtil.getVelocityToText(GOOGLE_TREND_URL_TEMPLATE, hashMap, true, null, str -> str);
		return formattedURL;
	}

}
