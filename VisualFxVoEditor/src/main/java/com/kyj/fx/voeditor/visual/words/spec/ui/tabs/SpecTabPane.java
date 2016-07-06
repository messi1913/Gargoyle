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
import javafx.scene.control.TabPane;

public class SpecTabPane extends TabPane {

	private ObjectProperty<ProgramSpecSVO> svoProperty = new SimpleObjectProperty<>();

	public SpecTabPane() {
		getTabs().add(new BaseInfoTab(this));
		getTabs().add(new TableInfoTab(this));
	}

	public SpecTabPane(ProgramSpecSVO svo) {
		this();
		this.svoProperty.set(svo);
	}

}
