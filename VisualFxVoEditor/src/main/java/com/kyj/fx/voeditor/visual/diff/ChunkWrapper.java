/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.file
 *	작성일   : 2016. 1. 26.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.diff;

import java.util.List;

import difflib.Chunk;
import difflib.Delta;
import difflib.Delta.TYPE;

/**
 * Chunk데이터 래핑 UI처리에 쉽게 도움을 주기위한 파일임.
 *
 * Diff라이브러리가 비교결과 차이점만 리턴하지,
 *
 * 전체적인 원본데이터값을 제대로 리턴해주지않음.
 *
 * 원본데이터형태를 UI에 표시하면서 그 라인에 대한 메타정보를 한라인에 표시해주기 위한 목적으로 사용하기위해
 *
 * 데이터를 가공후 래핑처리한다.
 *
 * @author KYJ
 *
 */
public class ChunkWrapper {

	/**
	 * 데이터 문자열
	 */
	private String str;
	/**
	 * 비교처리결과
	 */
	private Chunk chunk;
	/**
	 * 비교 결과 라인수(차이점데이터)
	 */
	private List<String> lines;
	/**
	 * 차이점 타입
	 */
	private TYPE type;
	/**
	 * 차이점 발생 위치
	 */
	private int position;

	/**
	 * 변경에대한 종합정보
	 * @최초생성일 2016. 1. 27.
	 */
	private Delta delta;

	/**
	 * @return the delta
	 */
	public Delta getDelta() {
		return delta;
	}

	/**
	 * @param delta
	 *            the delta to set
	 */
	public void setDelta(Delta delta) {
		this.delta = delta;
	}

	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * @return the str
	 */
	public String getStr() {
		return str;
	}

	/**
	 * @param str
	 *            the str to set
	 */
	public void setStr(String str) {
		this.str = str;
	}

	/**
	 * @return the chunk
	 */
	public Chunk getChunk() {
		return chunk;
	}

	/**
	 * @param chunk
	 *            the chunk to set
	 */
	public void setChunk(Chunk chunk) {
		this.chunk = chunk;
	}

	/**
	 * @return the lines
	 */
	public List<String> getLines() {
		return lines;
	}

	/**
	 * @param lines
	 *            the lines to set
	 */
	public void setLines(List<String> lines) {
		this.lines = lines;
	}

	/**
	 * @return the type
	 */
	public TYPE getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(TYPE type) {
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return str;
	}

}
