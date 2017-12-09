/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2017. 12. 8.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * @author KYJ
 *
 */
public class BehaviorReferenceMenuItemActionEvent implements EventHandler<ActionEvent> {

	private BehaviorTextComposite composite;
	private BehaviorReferenceMenuItem e;

	public BehaviorReferenceMenuItemActionEvent(BehaviorTextComposite composite, BehaviorReferenceMenuItem e) {
		this.composite = composite;
		this.e = e;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(ActionEvent event) {
		composite.smbReferencesOnAction(e);
	}

}
