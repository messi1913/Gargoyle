/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.loder
 *	작성일   : 2015. 10. 27.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.loder;

import java.util.List;

/**
 * Project 관련 정보를 관리하기 위한 데이터셋 <br/>
 * 
 * <ol>
 * <li>17.11.22 entry 항목 추가</li>
 * </ol>
 * 
 * @author KYJ
 *
 */
public class ProjectInfo {

	private String projectName;
	private String projectDir;
	private List<String> classes;
	private List<String> javaSources;
	/**
	 * Entry 항목 추가.<br/>
	 * example) <br/>
	 * <ol>
	 * <li>src/main/java</li>
	 * <li>src/test/java</li>
	 * </ol>
	 * 
	 * @최초생성일 2017. 11. 22.
	 */
	private String entry;

	public List<String> getJavaSources() {
		return javaSources;
	}

	public void setJavaSources(List<String> javaSources) {
		this.javaSources = javaSources;
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName
	 *            the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return the projectDir
	 */
	public String getProjectDir() {
		return projectDir;
	}

	/**
	 * @param projectDir
	 *            the projectDir to set
	 */
	public void setProjectDir(String projectDir) {
		this.projectDir = projectDir;
	}

	/**
	 * @return the classes
	 */
	public List<String> getClasses() {
		return classes;
	}

	/**
	 * @param classes
	 *            the classes to set
	 */
	public void setClasses(List<String> classes) {
		this.classes = classes;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 22.
	 * @param entry
	 */
	public void setEntry(String entry) {
		this.entry = entry;
	}

	public String getEntry() {
		return entry;
	}

}
