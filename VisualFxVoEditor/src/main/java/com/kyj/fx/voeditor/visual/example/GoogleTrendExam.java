/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 11. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.chart.service.BaseGoogleTrendChart;
import com.kyj.fx.voeditor.visual.component.chart.service.GoogleTrendChartEvent;
import com.kyj.fx.voeditor.visual.main.initalize.ProxyInitializable;
import com.kyj.fx.voeditor.visual.util.RequestUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.stage.Stage;

/**
 * 샘플1
 * @author KYJ
 *
 */
public class GoogleTrendExam extends Application {

	private static final Logger LOGGER = LoggerFactory.getLogger(GoogleTrendExam.class);

	public static void main(String[] args) throws Exception {

		//프록시 관련 설정을  세팅함.
		new ProxyInitializable().initialize();

		launch(args);
	}

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		URL url = new URL(createUrl());
		String jsonString = RequestUtil.requestSSL(url, (is, code) -> {
			String result = "";
			if (200 == code) {

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

					System.out.printf("dirty content %s \n", dirtyConent);

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
					System.out.printf("result string : %s \n", result);
					//					jsonObject = ValueUtil.toJSONObject(regexMatch);

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				LOGGER.warn("not unnomal response code");
				try {
					result = ValueUtil.toString(GoogleTrendExam.class.getResourceAsStream("GoogleTrendSample.json"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return result;
		});

		//		String source = ValueUtil.toString(GoogleTrendExam.class.getResourceAsStream("GoogleTrendSample.json"));
		BaseGoogleTrendChart root = new BaseGoogleTrendChart(jsonString, new CategoryAxis(), new NumberAxis(0, 100, 10));
		root.setVerticalGridLinesVisible(false);
		root.addEventFilter(GoogleTrendChartEvent.GOOGLE_CHART_INTERSECT_NODE_CLICK, ev -> {
			System.out.println(ev.getContents());
		});

		root.addEventHandler(GoogleTrendChartEvent.GOOGLE_CHART_INTERSECT_NODE_CLICK, ev -> {
			System.out.println(ev.getContents());
		});
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}

	String createUrl() throws UnsupportedEncodingException {
		String geo = "KR";
		String date = "all";
		//		String keywords = "%EB%B0%95%EA%B7%BC%ED%98%9C,%EC%B5%9C%EC%88%9C%EC%8B%A4";
		String origin_keywords = "galaxy,iphone"; //"박근혜,최순실";
		String converted_keywords = URLEncoder.encode(origin_keywords, "UTF-8");

		//핫 트랜드 https://www.google.com/trends/hottrends/atom
		String URL_FORMAT = "https://www.google.com/trends/fetchComponent?cid=TIMESERIES_GRAPH_0&export=3&q=:keywords #if($geo)&geo=:geo#end";

		Map<String, Object> hashMap = new HashMap<>();
		//		hashMap.put("geo", geo);
		hashMap.put("date", date);
		hashMap.put("keywords", converted_keywords);

		String formattedURL = ValueUtil.getVelocityToText(URL_FORMAT, hashMap, true, null, str -> str);
		System.out.println(formattedURL);
		return formattedURL;
	}
}
