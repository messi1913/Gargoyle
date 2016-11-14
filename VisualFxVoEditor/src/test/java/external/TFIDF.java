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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public void simple() throws MalformedURLException, Exception {

		String reqeustSSL = RequestUtil.reqeustSSL(
				new URL("https://www.google.co.kr/search?q=%EA%B3%BC%ED%95%99&oq=%EA%B3%BC%ED%95%99&aqs=chrome..69i57j69i65j69i60j69i61l2.1579j0j4&sourceid=chrome&ie=UTF-8#q=%EA%B3%BC%ED%95%99%EC%9E%A1%EC%A7%80"),
				(is, resCode) -> {

					try {
						Document parse = Jsoup.parse(ValueUtil.toString(is));
						Elements select = parse.select("#center_col");
						LOGGER.debug("########################################");
						select.forEach(ele -> {
							LOGGER.debug(ele.toString());
						});

						LOGGER.debug("########################################");

					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//					if (resCode == 200) {
					//
					//						try {
					//							return ValueUtil.toString(is);
					//						} catch (Exception e) {
					//							e.printStackTrace();
					//						}
					//					}

					return "";
				});

//				File file = new File("C:\\Users\\KYJ\\.git\\Gargoyle\\VisualFxVoEditor\\log\\fxeditor-2016-11-07.log");
		//		String str1 = sb.toString();//FileUtil.readFile(file, null);
		TF_IDF tf_IDF = new TF_IDF(new String[] { reqeustSSL });
		String[] words = tf_IDF.getWordVector();

		double[][] tf_IDFMatrix = tf_IDF.getTF_IDFMatrix();

		for (double[] tfIdf : tf_IDFMatrix) {

			//sort
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

			//			for (int i = 0; i < tfIdf.length; i++) {
			//				System.out.printf("%s - %f\n", words[i], tfIdf[i]);
			//			}
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
			//공백분리를 정규식으로 바꿈. - 공백이 분석에 영향을 끼치는듯보이므로..
			for (String word : doc.split("\\s+")) {
				//			for (String word : doc.split(" ")) {
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
		// tfIdfMatrix[docIdx][wordIdx] = tfMatrix[docIdx][wordIdx] * idfVector[wordIdx]
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