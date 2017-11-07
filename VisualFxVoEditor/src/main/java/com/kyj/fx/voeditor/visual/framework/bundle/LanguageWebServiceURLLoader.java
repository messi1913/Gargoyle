/********************************
 *	프로젝트 : batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.core.init
 *	작성일   : 2016. 8. 12.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.bundle;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.stream.JsonReader;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 *
 * Opera에서 제공되는 webservice를 통해 다국어 정보를 리턴받는다.
 * @author KYJ
 *
 */
class LanguageWebServiceURLLoader implements LanguageLoadable {

	private static Logger LOGGER = LoggerFactory.getLogger(LanguageWebServiceURLLoader.class);

	private String langCode;

	/**
	 * @param langCode
	 */
	public LanguageWebServiceURLLoader(String langCode) {
		this.langCode = ValueUtil.decode(langCode, "ko").toString();
	}

	/* (non-Javadoc)
	 * @see com.samsung.sds.sos.schedule.core.init.LanguageLoadable#load()
	 */
	@Override
	public Properties load(URL url) throws Exception {
		JSONProperties properties = null;
		URLConnection openConnection = url.openConnection();
		//2초이내 접속이 안되면 에러.
		openConnection.setConnectTimeout(2000);
		//6초이내에 작업이 이루어지지않으면 에러
		openConnection.setReadTimeout(6000);

		HttpURLConnection http = (HttpURLConnection) openConnection;
		//Post방식으로 요청.  setDoOutput(true)경우 사실상 아래 코드는 필요없음.
		http.setRequestMethod("POST");

		//브라우저에서 사용될(?) 캐시부분은 제거.
		http.setUseCaches(false);
		http.setDefaultUseCaches(false);

		//헤더 정의.
		http.setRequestProperty("Content-Type", "application/json");
		http.setRequestProperty("charset", "UTF-8");

		//서버로 파라미터요청을 할 경우 true
		http.setDoOutput(true);
		//서버에서 응답받은 처리를 하는경우 true
		http.setDoInput(true);

		//서버로 요청할 정보를 보내는 처리.
		try (OutputStream outputStream = http.getOutputStream()) {
			outputStream.write("{}".getBytes());
			outputStream.flush();
		}

		//		http.connect();


		int responseCode = http.getResponseCode();

		//서버로부터 응답이 정상적이라면
		if (HttpURLConnection.HTTP_OK == responseCode) {

			//응답받은 내용을 JSON스트리밍 처리.
			try (InputStream openStream = http.getInputStream()) {
				properties = new JSONProperties(this.langCode);

				try (InputStreamReader reader = new InputStreamReader(openStream, "UTF-8")) {
					properties.loadJson(reader);
				}
			}
		} else {
			LOGGER.error("Acept Fail. Response Code : {} ", responseCode);
		}
		return properties;
	}

	/**
	 * JSON 스트리밍 처리.
	 * @author KYJ
	 */
	private static class JSONProperties extends Properties {

		/**
		 * @최초생성일 2016. 8. 12.
		 */
		private static final long serialVersionUID = -3544777954888710911L;

		private String langCode;

		/**
		 * @param langCode
		 */
		public JSONProperties(String langCode) {
			this.langCode = langCode;
		}

		public void loadJson(final Reader reader) throws IOException {
			JsonReader jsonReader = new JsonReader(reader);
			//JSON Object Tag의 시작점이라는것을 알린다.
			jsonReader.beginObject();
			while (jsonReader.hasNext()) {

				//핸들링할 특정 JSON 키값인경우 작업처리. 여기에선 internationalizationOutDVOList라는 키값인경우 처리한다.
				if ("internationalizationOutDVOList".equals(jsonReader.nextName())) {

					String key = "";
					String value = "";
					boolean catchedKey = false;
					boolean langCodeMatched = true;
					//start array[1]
					jsonReader.beginArray();
					while (jsonReader.hasNext()) {
						langCodeMatched = true;
						//start object [1]
						jsonReader.beginObject();
						while (jsonReader.hasNext()) {
							String nextName = jsonReader.nextName();
							if ("msgId".equals(nextName)) {
								key = jsonReader.nextString();
								catchedKey = true;
							} else if ("msgCont".equals(nextName)) {
								value = jsonReader.nextString();
							} else {

								if ("langCode".equals(nextName)) {
									value = jsonReader.nextString();

									if (!this.langCode.equals(value)) {
										langCodeMatched = false;
									}

								} else {
									jsonReader.skipValue();
								}

							}
						}
						if (catchedKey && langCodeMatched) {
							this.put(key, value);
						}
						//end object [1]
						jsonReader.endObject();
					}

					//end array[1]
					jsonReader.endArray();

				} else {
					jsonReader.skipValue();
				}

			}
			//JSON Object Tag의 종료점이라는것을 알린다.
			jsonReader.endObject();
			jsonReader.close();
		}
	}
}
