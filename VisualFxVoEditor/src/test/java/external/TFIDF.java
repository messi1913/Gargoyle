/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : external
 *	작성일   : 2016. 11. 9.
 *	작성자   : KYJ
 *******************************/
package external;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.kohlschutter.boilerpipe.document.TextDocument;
import com.kohlschutter.boilerpipe.extractors.KeepEverythingExtractor;
import com.kohlschutter.boilerpipe.sax.BoilerpipeSAXInput;
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
	public void boilerpipeTest() throws Exception {
		URL url;
		url = new URL(
				"https://search.naver.com/search.naver?where=nexearch&sm=tab_lvf&ie=utf8&query=%EB%B0%95%EA%B7%BC%ED%95%B5%EB%8B%B7%EC%BB%B4");

		String str = RequestUtil.reqeustSSL(url, (st, code) -> {
			try {

				InputSource inputSource = new InputSource(st);
				inputSource.setEncoding("UTF-8");

				final BoilerpipeSAXInput in = new BoilerpipeSAXInput(inputSource);
				final TextDocument doc = in.getTextDocument();
				return KeepEverythingExtractor.INSTANCE.getText(doc);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return "";
		}, false);

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

		String reqeustSSL = RequestUtil.reqeustSSL(
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