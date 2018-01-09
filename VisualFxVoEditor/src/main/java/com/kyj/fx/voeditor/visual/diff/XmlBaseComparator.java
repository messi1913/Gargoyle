/********************************
 *	프로젝트 : system.plan
 *	패키지   : com.kyj.plan
 *	작성일   : 2016. 1. 25.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.diff;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * 두개의 파일 비교 결과를 리턴한다.
 *
 * @author KYJ
 *
 */
public class XmlBaseComparator implements DiffComparable<URL> {

	private URL original;

	private URL revised;

	private IURLReadModel model;

	public XmlBaseComparator() {
		model = new XmlFileReadModel();
	}

	public XmlBaseComparator(URL original, URL revised) {
		this();
		this.original = original;
		this.revised = revised;

	}

	public XmlBaseComparator(URL original, URL revised, IURLReadModel model) {
		this.original = original;
		this.revised = revised;
		this.model = model;
	}

	/**
	 * 비교결과리턴
	 *
	 * @return
	 * @throws IOException
	 */
	public CompareResult getChunkResult() throws IOException {

		if (ValueUtil.isEmpty(this.original))
			throw new FileNotFoundException("original file is null");
		if (ValueUtil.isEmpty(this.revised))
			throw new FileNotFoundException("original file is null");

		return getDeltas();
	}

	/**
	 * 비교결과 리턴
	 *
	 * @return
	 * @throws IOException
	 */
	private CompareResult getDeltas() throws IOException {
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

	private List<String> fileToLines(URL file) throws IOException {
		return model.readLines(file);
	}

	/**
	 * @return the original
	 */
	public URL getOriginal() {
		return original;
	}

	/**
	 * @return the revised
	 */
	public URL getRevised() {
		return revised;
	}

	/**
	 * @param original
	 *            the original to set
	 */
	public void setOriginal(URL original) {
		this.original = original;
	}

	/**
	 * @param revised
	 *            the revised to set
	 */
	public void setRevised(URL revised) {
		this.revised = revised;
	}

}