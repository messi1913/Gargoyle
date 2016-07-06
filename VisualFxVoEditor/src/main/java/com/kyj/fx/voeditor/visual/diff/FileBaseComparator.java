/********************************
 *	프로젝트 : system.plan
 *	패키지   : com.kyj.plan
 *	작성일   : 2016. 1. 25.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.diff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * 두개의 파일 비교 결과를 리턴한다.
 *
 * @author KYJ
 *
 */
public class FileBaseComparator implements DiffComparable<File> {

	private File original;

	private File revised;

	private IFileReadModel model;

	public FileBaseComparator() {
		model = new DefaultFileReadModel();
	}

	public FileBaseComparator(File original, File revised) {
		this();
		this.original = original;
		this.revised = revised;

	}

	public FileBaseComparator(File original, File revised, IFileReadModel model) {
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

		if (this.original == null || !this.original.exists())
			throw new FileNotFoundException("original file is null");
		if (this.revised == null || !this.original.exists())
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

	private List<String> fileToLines(File file) throws IOException {
		return model.readLines(file);
	}

	/**
	 * @return the original
	 */
	public File getOriginal() {
		return original;
	}

	/**
	 * @return the revised
	 */
	public File getRevised() {
		return revised;
	}

	/**
	 * @param original
	 *            the original to set
	 */
	public void setOriginal(File original) {
		this.original = original;
	}

	/**
	 * @param revised
	 *            the revised to set
	 */
	public void setRevised(File revised) {
		this.revised = revised;
	}

}