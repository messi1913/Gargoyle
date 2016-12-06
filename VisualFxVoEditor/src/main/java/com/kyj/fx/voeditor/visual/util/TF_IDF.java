/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 12. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.tools.ant.util.regexp.RegexpUtil;

/**
 * @author KYJ
 *
 */

class TF_IDF {

	static class TextFilter {

		static final Set<String> filtered = new HashSet<>();

		static {
			filtered.add(" ");
			filtered.add("있다.");
			filtered.add("를");
			filtered.add("은");
			filtered.add("는");
			filtered.add("이");
			filtered.add("가");
			filtered.add("의");
			filtered.add("에");
			filtered.add("매우");
			filtered.add("하지만");
			filtered.add("나는");
			filtered.add("아니라");
			filtered.add("결국");
			filtered.add("에서");
			filtered.add("다양한");
			filtered.add("대해");
			filtered.add("아닌");
			filtered.add("이에");
			filtered.add("않는");
			filtered.add("있습니다.");

		}

		public static boolean isFiltering(String content) {
			if (isSingleWord(content))
				return true;
			return filtered.contains(content);
		}

		public static boolean isSingleWord(String content) {
			return content.length() == 1;
		}

	}

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
			String[] split = docSplit(doc);
			for (String word : split) {

				if (TextFilter.isFiltering(word))
					continue;

				word = ValueUtil.removeSpecialCharacter(word);

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
			String[] split = docSplit(doc);
			String[] words = split;
			for (String word : words) {

				if (TextFilter.isFiltering(word))
					continue;

				word = ValueUtil.removeSpecialCharacter(word);

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
			for (String word : docSplit(doc)) {
				if (TextFilter.isFiltering(word))
					continue;

				word = ValueUtil.removeSpecialCharacter(word);

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

	private String[] docSplit(String doc) {
		String[] split = doc.split("\\s+");
		return split;
	}

	public double[][] getTF_IDFMatrix() {
		return tfIdfMatrix;
	}

	public String[] getWordVector() {
		return wordVector;
	}

}