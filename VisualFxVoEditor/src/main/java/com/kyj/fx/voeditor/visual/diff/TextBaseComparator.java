/********************************
 *	프로젝트 : system.plan
 *	패키지   : com.kyj.plan
 *	작성일   : 2016. 1. 25.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.diff;

import java.util.List;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * 두개의 텍스트 비교 결과를 리턴한다.
 *
 * @author KYJ
 *
 */
public class TextBaseComparator implements DiffComparable<String> {

	private String original;

	private String revised;

	private IReadModel<String, String> model;

	public TextBaseComparator() {
		model = new DefaultTextReadModel();
	}

	public TextBaseComparator(String original, String revised) {
		this();
		this.original = original;
		this.revised = revised;

	}

	public TextBaseComparator(String original, String revised, IReadModel<String, String> model) {
		this.original = original;
		this.revised = revised;
		this.model = model;
	}

	/**
	 * 비교결과리턴
	 *
	 * @return
	 * @throws Exception
	 */
	public CompareResult getChunkResult() throws Exception {
		return getDeltas();
	}

	/**
	 * 비교결과 리턴
	 *
	 * @return
	 * @throws Exception
	 */
	private CompareResult getDeltas() throws Exception {
		CompareResult result = new CompareResult();

		List<String> originalFileLines = fileToLines(original);
		List<String> revisedFileLines = fileToLines(revised);

		result.setOriginalFileLines(originalFileLines);
		result.setRevisedFileLines(revisedFileLines);

		final Patch patch = DiffUtils.diff(originalFileLines, revisedFileLines);
		List<Delta> deltas = patch.getDeltas();
		result.setDeltas(deltas);
		return result;
	}

	private List<String> fileToLines(String text) throws Exception {
		return model.readLines(text);
	}

	/**
	 * @return the original
	 */
	public String getOriginal() {
		return original;
	}

	/**
	 * @return the revised
	 */
	public String getRevised() {
		return revised;
	}

	/**
	 * @param original
	 *            the original to set
	 */
	public void setOriginal(String original) {
		this.original = original;
	}

	/**
	 * @param revised
	 *            the revised to set
	 */
	public void setRevised(String revised) {
		this.revised = revised;
	}

}