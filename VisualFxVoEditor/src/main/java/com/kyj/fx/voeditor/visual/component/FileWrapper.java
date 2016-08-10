/**
 * KYJ
 * 2015. 10. 13.
 */
package com.kyj.fx.voeditor.visual.component;

import java.io.File;
import java.io.Serializable;

/**
 * 파일트리를 표현하기 위한 기본단위 래퍼클래스
 *
 * @author KYJ
 *
 */
public class FileWrapper implements Serializable{
	/**
	 * @최초생성일 2016. 7. 18.
	 */
	private static final long serialVersionUID = -6959924416500023356L;
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

	/**
	 * 자바 프로젝트인지 유무
	 * 
	 * @최초생성일 2016. 7. 10.
	 */
	private boolean isJavaProjectFile;
	/**
	 * svn 연결이 되었는지 유무
	 * 
	 * @최초생성일 2016. 7. 10.
	 */
	private boolean isSVNConnected;

	/**
	 * SVN 연결시 연계되는 SQLIte DB 파일.
	 * @최초생성일 2016. 7. 18.
	 */
	private File wcDbFile;

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
		/*2016.07.10 set empty toString*/
		return "";
		//		return file.getName();
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

	public boolean isJavaProjectFile() {
		return isJavaProjectFile;
	}

	public void setJavaProjectFile(boolean isJavaProjectFile) {
		this.isJavaProjectFile = isJavaProjectFile;
	}

	public boolean isSVNConnected() {
		return isSVNConnected;
	}

	public void setSVNConnected(boolean isSVNConnected) {
		this.isSVNConnected = isSVNConnected;
	}

	public void setWcDbFile(File wcDbFile) {
		this.wcDbFile = wcDbFile;
	}

	public File getWcDbFile() {
		return wcDbFile;
	}

}
