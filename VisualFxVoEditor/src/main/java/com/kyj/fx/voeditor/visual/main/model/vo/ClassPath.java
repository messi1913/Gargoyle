/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.model.vo
 *	작성일   : 2015. 10. 26.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main.model.vo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * classPath에 대한 전체 정보를 담는 클래스
 *
 * @author KYJ
 *
 */
public class ClassPath {

	private List<ClassPathEntry> entrys;

	private String filePathName;
	private String charsetName;

	public ClassPath() {
		entrys = new ArrayList<>();
	}

	public void setFilePathName(String filePathName) {
		this.filePathName = filePathName;

	}

	public void setApplyedEncoding(String charsetName) {
		this.charsetName = charsetName;
	}

	public Iterator<ClassPathEntry> iteratorEntrys() {
		return entrys.iterator();
	}

	public void addEntry(ClassPathEntry entry) {
		if (entry == null)
			return;
		this.entrys.add(entry);
	}

	/**
	 * ClassPathEntry를 다른클래스형태로 변환하여 반환
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 26.
	 * @param func
	 * @return
	 */
	public <R> List<R> entryFunction(Function<ClassPathEntry, R> func) {
		return entrys.stream().map(func).collect(Collectors.toList());
	}

	/**
	 * ClassPathEntry의 요소를 필터링하여 특정값만 반환
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 26.
	 * @param filter
	 * @return
	 */
	public List<ClassPathEntry> entryFilter(Predicate<ClassPathEntry> filter) {
		return entrys.stream().filter(filter).collect(Collectors.toList());
	}

	/**
	 * ClassPathEntry -> Stream 형태로 변환후 반환
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 26.
	 * @return
	 */
	public Stream<ClassPathEntry> toStream() {
		return entrys.stream();
	}

	/**
	 * ClassPathEntry -> ParallelStream 형태로 변환후 반환
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 11.
	 * @return
	 */
	public Stream<ClassPathEntry> toParallelStream() {
		return entrys.parallelStream();
	}

	/**
	 * @return the filePathName
	 */
	public String getFilePathName() {
		return filePathName;
	}

	/**
	 * @return the charsetName
	 */
	public String getCharsetName() {
		return charsetName;
	}
}
