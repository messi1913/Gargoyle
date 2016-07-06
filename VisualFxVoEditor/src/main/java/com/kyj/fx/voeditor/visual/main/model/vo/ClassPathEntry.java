/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.model.vo
 *	작성일   : 2015. 10. 26.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main.model.vo;

/**
 * ClassPathEntry정보를 담는 클래스
 *
 * @author KYJ
 *
 */
public class ClassPathEntry {
	private String kind;
	private String output;
	private String path;

	public ClassPathEntry(String kind, String output, String path) {
		this.kind = kind;
		this.output = output;
		this.path = path;
	}

	/**
	 * @return the kind
	 */
	public String getKind() {
		return kind;
	}

	/**
	 * @param kind
	 *            the kind to set
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}

	/**
	 * @return the output
	 */
	public String getOutput() {
		return output;
	}

	/**
	 * @param output
	 *            the output to set
	 */
	public void setOutput(String output) {
		this.output = output;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ClassPathEntry [kind=" + kind + ", output=" + output + ", path=" + path + "]";
	}

}
