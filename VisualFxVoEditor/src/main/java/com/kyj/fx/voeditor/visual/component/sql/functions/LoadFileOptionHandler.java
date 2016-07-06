/**
 * package : com.kyj.fx.voeditor.visual.component.sql.functions
 *	fileName : LoadFileOption.java
 *	date      : 2015. 12. 24.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.functions;

import java.util.List;

/**
 * 파일을 로드할시에 처리할 옵션.
 * 
 * @author KYJ
 *
 */
public class LoadFileOptionHandler {

	/**
	 * 인코딩
	 */
	private String encoding = "UTF-8";

	/**
	 * 예외발생여부
	 */
	private boolean isOccurException;

	/**
	 * 예외가 발생한경우 입력됨.
	 */
	private Exception exception;

	/**
	 * 확장자 필터
	 */
	private List<String> fileNameLikeFilter;

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean isOccurException() {
		return isOccurException;
	}

	public void setOccurException(boolean isOccurException) {
		this.isOccurException = isOccurException;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		if (exception != null)
			this.isOccurException = true;

		this.exception = exception;
	}

	public List<String> getFileNameLikeFilter() {
		return fileNameLikeFilter;
	}

	public void setFileNameLikeFilter(List<String> fileNameLikeFilter) {
		this.fileNameLikeFilter = fileNameLikeFilter;
	}

}
