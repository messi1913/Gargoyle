/**
 * KYJ
 * 2015. 10. 13.
 */
package com.kyj.fx.voeditor.visual.component;

import java.io.File;

/**
 * 파일트리를 표현하기 위한 기본단위 래퍼클래스
 *
 * @author KYJ
 *
 */
public class FileWrapper {
	/**
	 * @최초생성일 2016. 3. 15.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 파일
	 *
	 * @최초생성일 2015. 10. 21.
	 */
	private File file;
	/**
	 * 숨김파일을 보여줄지 유무. (숨김파일의 기준은 파일명이 '.'으로 시작하는경우 숨김파일이라 가정한다.)
	 *
	 * @최초생성일 2015. 10. 21.
	 */
	private boolean showHiddenFile;

	public FileWrapper(File file) {
		super();
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public boolean isFile() {
		return this.file.isFile();
	}

	@Override
	public String toString() {
		return file.getName();
	}

	/**
	 * @return the showHiddenFile
	 */
	public boolean isShowHiddenFile() {
		return showHiddenFile;
	}

	/**
	 * @param showHiddenFile
	 *            the showHiddenFile to set
	 */
	public void setShowHiddenFile(boolean showHiddenFile) {
		this.showHiddenFile = showHiddenFile;
	}

	public File[] listFiles() {

		return this.file.listFiles((dir, name) -> {
			if (!isShowHiddenFile()) {
				return !name.startsWith(".");
			}
			return true;
		});

	}

}
