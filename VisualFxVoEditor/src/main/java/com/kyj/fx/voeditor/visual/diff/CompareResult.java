/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.file
 *	작성일   : 2016. 1. 25.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.diff;

import java.util.List;

import difflib.Chunk;
import difflib.Delta;

/**
 * 비교결과 정보를 리턴
 * 
 * @author KYJ
 *
 */
public class CompareResult {
	List<Chunk> listOfOriginChanges;
	List<Chunk> listOfReviceChanges;
	List<String> originalFileLines;
	List<String> revisedFileLines;
	List<Delta> deltas;

	/**
	 * @return the deltas
	 */
	public List<Delta> getDeltas() {
		return deltas;
	}

	/**
	 * @param deltas
	 *            the deltas to set
	 */
	public void setDeltas(List<Delta> deltas) {
		this.deltas = deltas;
	}

	/**
	 * @return the listOfOriginChanges
	 */
	public List<Chunk> getListOfOriginChanges() {
		return listOfOriginChanges;
	}

	/**
	 * @param listOfOriginChanges
	 *            the listOfOriginChanges to set
	 */
	public void setListOfOriginChanges(List<Chunk> listOfOriginChanges) {
		this.listOfOriginChanges = listOfOriginChanges;
	}

	/**
	 * @return the listOfReviceChanges
	 */
	public List<Chunk> getListOfReviceChanges() {
		return listOfReviceChanges;
	}

	/**
	 * @param listOfReviceChanges
	 *            the listOfReviceChanges to set
	 */
	public void setListOfReviceChanges(List<Chunk> listOfReviceChanges) {
		this.listOfReviceChanges = listOfReviceChanges;
	}

	/**
	 * @return the originalFileLines
	 */
	public List<String> getOriginalFileLines() {
		return originalFileLines;
	}

	/**
	 * @param originalFileLines
	 *            the originalFileLines to set
	 */
	public void setOriginalFileLines(List<String> originalFileLines) {
		this.originalFileLines = originalFileLines;
	}

	/**
	 * @return the revisedFileLines
	 */
	public List<String> getRevisedFileLines() {
		return revisedFileLines;
	}

	/**
	 * @param revisedFileLines
	 *            the revisedFileLines to set
	 */
	public void setRevisedFileLines(List<String> revisedFileLines) {
		this.revisedFileLines = revisedFileLines;
	}

}
