/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2017. 12. 8.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import javafx.scene.control.MenuItem;

/**
 * @author KYJ
 *
 */
public class BehaviorReferenceMenuItem extends MenuItem {

	private BehaviorReferenceVO behaviorReferenceVO;

	public BehaviorReferenceMenuItem(BehaviorReferenceVO behaviorReferenceVO) {
		this.behaviorReferenceVO = behaviorReferenceVO;
		setText(behaviorReferenceVO.getTxtFileName());
	}

	public BehaviorReferenceVO getValue() {
		return behaviorReferenceVO;
	}

	public void setBehaviorReferenceVO(BehaviorReferenceVO behaviorReferenceVO) {
		this.behaviorReferenceVO = behaviorReferenceVO;
	}

}
