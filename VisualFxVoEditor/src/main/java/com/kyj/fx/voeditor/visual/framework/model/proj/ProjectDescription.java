/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.model.proj
 *	작성일   : 2016. 3. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.model.proj;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * .classpath 파일 xml parser 클래스
 * 
 * @author KYJ
 *
 */
@XmlRootElement(name = "projectDescription")
public class ProjectDescription {

	private String name;
	private String comment;
	private List<Project> projects;
	private List<BuildSpec> buildSpec;
	private List<Natures> natures;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	public List<BuildSpec> getBuildSpec() {
		return buildSpec;
	}

	public void setBuildSpec(List<BuildSpec> buildSpec) {
		this.buildSpec = buildSpec;
	}

	public List<Natures> getNatures() {
		return natures;
	}

	public void setNatures(List<Natures> natures) {
		this.natures = natures;
	}

	@Override
	public String toString() {
		return "ProjectDescription [name=" + name + ", comment=" + comment + ", projects=" + projects + ", buildSpec=" + buildSpec
				+ ", natures=" + natures + "]";
	}

}
