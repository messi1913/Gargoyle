/********************************
 *	프로젝트 : system.plan
 *	패키지   : com.kyj.plan
 *	작성일   : 2016. 1. 25.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.diff;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * 두개의 파일 비교 결과를 리턴한다.
 *
 * @author KYJ
 *
 */
public class RelocateFileComparator implements DiffComparable<File> {

	private File original;

	private File revised;

	private IReadModel<File, String> model;

	public RelocateFileComparator() {
		model = new DefaultFileReadModel();
	}

	public RelocateFileComparator(File original, File revised) {
		this();
		this.original = original;
		this.revised = revised;

	}

	public RelocateFileComparator(File original, File revised, IFileReadModel model) {
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
	 * @throws Exception
	 */
	private CompareResult getDeltas() throws Exception {
		CompareResult result = new CompareResult();

		List<String> originalFileLines = fileToLines(original);
		List<String> revisedFileLines = fileToLines(revised);

		int max = Math.max(originalFileLines.size(), revisedFileLines.size());

		// array copy
		ArrayList<String> tmpOriginalFileLines = new ArrayList<String>();
		originalFileLines.forEach(str -> {
			tmpOriginalFileLines.add(str);
		});
		while (tmpOriginalFileLines.size() <= max) {
			tmpOriginalFileLines.add("");
		}

		ArrayList<String> tmpReviceFileLines = new ArrayList<String>();
		revisedFileLines.forEach(str -> {
			tmpReviceFileLines.add(str);
		});
		while (tmpReviceFileLines.size() <= max) {
			tmpReviceFileLines.add("");
		}
		// occur .. exception..
		// Collections.copy(originalFileLines, tmpOriginalFileLines);

		result.setOriginalFileLines(tmpOriginalFileLines);
		result.setRevisedFileLines(tmpReviceFileLines);

		final Patch patch = DiffUtils.diff(originalFileLines, revisedFileLines);

		List<Chunk> listOfOriginChanges = new ArrayList<Chunk>();
		List<Chunk> listOfReviceChanges = new ArrayList<Chunk>();

		List<Delta> deltas = patch.getDeltas();

		Collections.reverse(deltas);

		for (Delta delta : deltas) {
			Chunk original = delta.getOriginal();
			Chunk revised = delta.getRevised();
			int position = original.getPosition();

			@SuppressWarnings("unchecked")
			List<String> lines = (List<String>) original.getLines();
			@SuppressWarnings("unchecked")
			List<String> lines2 = (List<String>) revised.getLines();

			switch (delta.getType()) {
			case DELETE:
				// tmpOriginalFileLines.addAll(position, lines2);
				// tmpReviceFileLines.addAll(position, lines);
				System.err.println("DELETE");
				break;
			case INSERT: {
				// tmpOriginalFileLines.addAll(position, lines2);
				tmpReviceFileLines.addAll(position, lines);
				break;
			}
			case CHANGE: {
				LinkedList<String> src = new LinkedList<>();

				List<String> tmp1 = null;
				List<String> tmp2 = null;
				int compare = Integer.compare(lines.size(), lines2.size());
				if (compare >= 0) {
					tmp1 = lines;
					tmp2 = lines2;
				} else {
					tmp1 = lines2;
					tmp2 = lines;
				}
				tmp1.forEach(str -> {
					src.add(str);
				});
				try {
					for (int i = 0; i < tmp2.size(); i++) {
						src.set(i, tmp2.get(i));
					}
					for (int i = tmp2.size(); i < src.size(); i++)
						src.set(i, "");
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
				}
				if (compare == 1) {
					tmpReviceFileLines.addAll(position, src);
					tmpReviceFileLines.remove(position + src.size());
				} else {
					tmpOriginalFileLines.addAll(position, src);
					tmpOriginalFileLines.remove(position + src.size());
				}

			}
				break;
			default:
				break;
			}
			listOfOriginChanges.add(delta.getOriginal());
			listOfReviceChanges.add(delta.getRevised());

		}

		result.setListOfOriginChanges(listOfOriginChanges);
		result.setListOfReviceChanges(listOfReviceChanges);

		return result;
	}

	private List<String> fileToLines(File file) throws Exception {
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