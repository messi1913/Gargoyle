/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.file
 *	작성일   : 2016. 1. 27.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.diff;

import java.io.IOException;

/**
 * 비교처리 알고리즘 구현
 *
 * @author KYJ
 *
 */
public interface DiffComparable<T> {

	/**
	 * 비교결과 구현
	 *
	 * @return
	 * @throws IOException
	 */
	public CompareResult getChunkResult() throws Exception;

	/**
	 * 비교원본파일
	 *
	 * @param original
	 *            the original to set
	 */
	public void setOriginal(T original);

	/**
	 * 비교대상파일
	 *
	 * @param revised
	 *            the revised to set
	 */
	public void setRevised(T revised);
}
