/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.model
 *	작성일   : 2016. 3. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.model.proj;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * .classpath 파일 xml parser 클래스
 *
 * @author KYJ
 *
 */
@XmlRootElement(name = "project")
public class Project {

	private ProjectDescription projectDescription;

	@XmlElement(name = "projectDescription")
	public ProjectDescription getProjectDescription() {
		return projectDescription;
	}

	public void setProjectDescription(ProjectDescription projectDescription) {
		this.projectDescription = projectDescription;
	}

	@Override
	public String toString() {
		return "Project [projectDescription=" + projectDescription + "]";
	}

}
