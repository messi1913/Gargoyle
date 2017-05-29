/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : external
 *	작성일   : 2016. 11. 9.
 *	작성자   : KYJ
 *******************************/
package external;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.kohlschutter.boilerpipe.document.TextDocument;
import com.kohlschutter.boilerpipe.extractors.ArticleExtractor;
import com.kohlschutter.boilerpipe.extractors.ArticleSentencesExtractor;
import com.kohlschutter.boilerpipe.extractors.ExtractorBase;
import com.kohlschutter.boilerpipe.extractors.KeepEverythingExtractor;
import com.kohlschutter.boilerpipe.sax.BoilerpipeSAXInput;
import com.kyj.fx.voeditor.visual.framework.KeyValue;
import com.kyj.fx.voeditor.visual.framework.URLModel;
import com.kyj.fx.voeditor.visual.main.initalize.ProxyInitializable;
import com.kyj.fx.voeditor.visual.util.RequestUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * @author KYJ
 *
 */
public class TFIDF {

	private static final Logger LOGGER = LoggerFactory.getLogger(TFIDF.class);

	@Before
	public void setting() throws Exception {
		new ProxyInitializable().initialize();
	}

	@Test
	public void findAllLinks() throws Exception {
		URL url;
		url = new URL("https://search.naver.com/search.naver?where=nexearch&query=%ED%91%9C%EC%B0%BD%EC%9B%90&sm=top_hty&fbm=1&ie=utf8");

		Set<String> reqeustSSL = RequestUtil.requestSSL(url, (is, code) -> {

			Set<String> collect = Collections.emptySet();
			try {
				Document parse = Jsoup.parse(is, "UTF-8", "http");

				/*a 태그 만 추출.*/
				Elements elementsByTag = parse.getElementsByTag("a");// parse.getElementsByTag("a");

				collect = elementsByTag.stream().filter(e -> e.hasAttr("href")).map(e -> e.attr("href").trim())
						/*http or https인 링크만 추출.*/
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

					return true;
				}).collect(Collectors.toSet());

			} catch (IOException e) {
				e.printStackTrace();
			}

			return collect;
		});

		// reqeustSSL.forEach(System.out::println);
		getString(reqeustSSL);

	}

	public void getString(Collection<String> links) {

		URLModel[] array = links.parallelStream().map(link -> {
			URLModel model = URLModel.empty();
			try {

				if (link.startsWith("https")) {
					model = RequestUtil.requestSSL(new URL(link), (is, code) -> {
						if (code == 200) {
							return new URLModel(link, ValueUtil.toString(is));
						}
						return URLModel.empty();

					});

				} else {
					model = RequestUtil.request(new URL(link), (is, code) -> {
						if (code == 200) {
							return new URLModel(link, ValueUtil.toString(is));
						}

						return URLModel.empty();

					});
				}
			} catch (Exception e) {
				return URLModel.empty();
			}

			return model;
		}).filter(v -> !v.isEmpty()).map(v -> {
			String content = v.getContent();
			ExtractorBase instance = ArticleExtractor.getInstance();
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

		List<KeyValue> tf_IDF = ValueUtil.toTF_IDF(array);
		tf_IDF.forEach(v -> {
			System.out.println(v.toString());
		});
	}

	@Test
	public void boilerpipeTest() throws Exception {
		URL url;
		url = new URL("https://twitter.com/intent/favorite?tweet_id=805627131061374976");

		String str = RequestUtil.request(url, (st, code) -> {
			try {

				InputSource inputSource = new InputSource(st);
				inputSource.setEncoding("UTF-8");

				final BoilerpipeSAXInput in = new BoilerpipeSAXInput(inputSource);
				final TextDocument doc = in.getTextDocument();

				System.out.println("HH");
				LOGGER.debug("HH");

				System.out.println(KeepEverythingExtractor.INSTANCE.getText(doc));
				return ArticleSentencesExtractor.INSTANCE.getText(doc);
				//				return ArticleExtractor.INSTANCE.getText(doc);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		} , true);

		LOGGER.debug(str);
		System.out.println(str);

	}

	@Test
	public void removeTags() throws MalformedURLException, Exception {
		String reqeustSSL = RequestUtil.request(new URL("http://view.asiae.co.kr/news/view.htm?idxno=2016120314584743535"),
				(is, resCode) -> {

					try {
						return ValueUtil.toString(is);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					return "";
				});

		System.out.println(ValueUtil.HTML.escapeHtml(reqeustSSL));

	}

	@Test
	public void simple() throws MalformedURLException, Exception {

		String reqeustSSL = RequestUtil.requestSSL(
				new URL("https://search.naver.com/search.naver?where=nexearch&sm=tab_htf&ie=utf8&query=%EA%B9%80%EB%AC%B4%EC%84%B1+%EB%8C%80%EC%84%A0+%EB%B6%88%EC%B6%9C%EB%A7%88"),
				(is, resCode) -> {

					try {
						return ValueUtil.toString(is);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					return "";
				});

		System.out.println(reqeustSSL);

		// File file = new
		// File("C:\\Users\\KYJ\\.git\\Gargoyle\\VisualFxVoEditor\\log\\fxeditor-2016-11-07.log");
		// String str1 = sb.toString();//FileUtil.readFile(file, null);
		TF_IDF tf_IDF = new TF_IDF(new String[] { reqeustSSL });
		String[] words = tf_IDF.getWordVector();

		double[][] tf_IDFMatrix = tf_IDF.getTF_IDFMatrix();

		for (double[] tfIdf : tf_IDFMatrix) {

			// sort
			double arraySize = tfIdf.length;
			for (int i = 0; i < arraySize - 1; i++) {
				for (int k = i + 1; k < arraySize; k++) {
					if (tfIdf[i] < tfIdf[k]) {
						String temp = words[i];
						double dTemp = tfIdf[i];

						words[i] = words[k];
						tfIdf[i] = tfIdf[k];

						words[k] = temp;
						tfIdf[k] = dTemp;
					}
				}
			}

			for (int i = 0; i < tfIdf.length; i++) {
				System.out.printf("%s - %f\n", words[i], tfIdf[i]);
			}
		}

	}

}

class TF_IDF {

	int numOfWords;
	double[] idfVector;
	double[][] tfIdfMatrix;
	double[][] tfMatrix;
	String[] wordVector;
	int docLength[];

	public TF_IDF(String[] docs) {
		// STEP 1, scan all words and count the number of different words
		// mapWordToIdx maps word to its vector index
		HashMap<String, Integer> mapWordToIdx = new HashMap<>();
		int nextIdx = 0;
		for (String doc : docs) {
			// 공백분리를 정규식으로 바꿈. - 공백이 분석에 영향을 끼치는듯보이므로..
			for (String word : doc.split("\\s+")) {
				// for (String word : doc.split(" ")) {
				if (!mapWordToIdx.containsKey(word)) {
					mapWordToIdx.put(word, nextIdx);
					nextIdx++;
				}
			}
		}

		numOfWords = mapWordToIdx.size();

		// STEP 2, create word vector where wordVector[i] is the actual word
		wordVector = new String[numOfWords];
		for (String word : mapWordToIdx.keySet()) {
			int wordIdx = mapWordToIdx.get(word);
			wordVector[wordIdx] = word;
		}

		// STEP 3, create doc word vector where docCountVector[i] is number of
		// docs containing word of index i
		// and doc length vector
		int[] docCountVector = new int[numOfWords];
		docLength = new int[docs.length];
		// lastDocWordVector is auxilary vector keeping track of last doc index
		// containing the word
		int[] lastDocWordVector = new int[numOfWords];
		for (int wordIdx = 0; wordIdx < numOfWords; wordIdx++) {
			lastDocWordVector[wordIdx] = -1;
		}
		for (int docIdx = 0; docIdx < docs.length; docIdx++) {
			String doc = docs[docIdx];
			String[] words = doc.split("\\s+");
			for (String word : words) {
				docLength[docIdx] = words.length;
				int wordIdx = mapWordToIdx.get(word);
				if (lastDocWordVector[wordIdx] < docIdx) {
					lastDocWordVector[wordIdx] = docIdx;
					docCountVector[wordIdx]++;
				}
			}
		}

		// STEP 4, compute IDF vector based on docCountVector
		idfVector = new double[numOfWords];
		for (int wordIdx = 0; wordIdx < numOfWords; wordIdx++) {
			idfVector[wordIdx] = Math.log10(1 + (double) docs.length / (docCountVector[wordIdx]));
		}

		// STEP 5, compute term frequency matrix, tfMatrix[docIdx][wordIdx]
		tfMatrix = new double[docs.length][];
		for (int docIdx = 0; docIdx < docs.length; docIdx++) {
			tfMatrix[docIdx] = new double[numOfWords];
		}
		for (int docIdx = 0; docIdx < docs.length; docIdx++) {
			String doc = docs[docIdx];
			for (String word : doc.split("\\s+")) {
				int wordIdx = mapWordToIdx.get(word);
				tfMatrix[docIdx][wordIdx] = tfMatrix[docIdx][wordIdx] + 1;
			}
		}
		// normalize idfMatrix by deviding corresponding doc length
		for (int docIdx = 0; docIdx < docs.length; docIdx++) {
			for (int wordIdx = 0; wordIdx < numOfWords; wordIdx++) {
				tfMatrix[docIdx][wordIdx] = tfMatrix[docIdx][wordIdx] / docLength[docIdx];
			}
		}

		// STEP 6, compute final TF-IDF matrix
		// tfIdfMatrix[docIdx][wordIdx] = tfMatrix[docIdx][wordIdx] *
		// idfVector[wordIdx]
		tfIdfMatrix = new double[docs.length][];
		for (int docIdx = 0; docIdx < docs.length; docIdx++) {
			tfIdfMatrix[docIdx] = new double[numOfWords];
		}

		for (int docIdx = 0; docIdx < docs.length; docIdx++) {
			for (int wordIdx = 0; wordIdx < numOfWords; wordIdx++) {
				tfIdfMatrix[docIdx][wordIdx] = tfMatrix[docIdx][wordIdx] * idfVector[wordIdx];
			}
		}

	}

	public double[][] getTF_IDFMatrix() {
		return tfIdfMatrix;
	}

	public String[] getWordVector() {
		return wordVector;
	}

}