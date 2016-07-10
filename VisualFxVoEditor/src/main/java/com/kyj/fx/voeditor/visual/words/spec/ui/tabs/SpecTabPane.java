/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.words.spec.ui.controls
 *	작성일   : 2016. 2. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.words.spec.ui.tabs;

import com.kyj.fx.voeditor.visual.words.spec.auto.msword.vo.ProgramSpecSVO;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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

	private ObjectProperty<ProgramSpecSVO> svoProperty = new SimpleObjectProperty<>();

	/**
	 * 기본기능외 추가적인 tab을 더함.
	 * 
	 * @param tabs
	 */
	public SpecTabPane(Tab... tabs) {
		this();
		getTabs().addAll(tabs);
	}

	public SpecTabPane() {
		getTabs().add(new BaseInfoTab(this));
		getTabs().add(new TableInfoTab(this));
	}

	public SpecTabPane(ProgramSpecSVO svo) {
		this();
		this.svoProperty.set(svo);
	}

}
