/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.words.spec.ui.controls
 *	작성일   : 2016. 2. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.words.spec.ui.tabs;

import com.kyj.fx.voeditor.visual.exceptions.GargoyleResourceException;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.vo.ProgramSpecSVO;
import com.kyj.fx.voeditor.visual.words.spec.ui.model.SpecResource;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/***************************
 *
 * 프로그램 사양서 처리를 위한 tabPane
 *
 * @author KYJ
 *
 ***************************/
public class SpecTabPane extends TabPane {

	/**
	 * 샤양서 생성시 필요한 resource 집합.
	 *
	 * @최초생성일 2016. 8. 5.
	 */
	private final SpecResource resource;

	/**
	 * 기본기능외 추가적인 tab을 더함.
	 *
	 * @param tabs
	 * @throws GargoyleResourceException
	 */
	public SpecTabPane(SpecResource resource, Tab... tabs) throws Exception {
		this(resource);
		getTabs().addAll(tabs);
	}

	public SpecTabPane(SpecResource resource) throws Exception {
		this.resource = resource;
		if (resource == null)
			throw new GargoyleResourceException("resource cannot be null.");

		getTabs().add(new ProjectInfoBaseInfoTab("사양서 기본 정보", this));
		getTabs().add(new TableInfoTab("테이블 정의", this));
	}

	public SpecTabPane(ProgramSpecSVO svo) throws Exception {
		this(new SpecResource(svo));
	}

	/**
	 * @return the resource
	 */
	public final SpecResource getResource() {
		return resource;
	}

}
