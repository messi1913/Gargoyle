/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2017. 12. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import org.fxmisc.richtext.CodeArea;

import javafx.scene.control.ContextMenu;

/**
 * @author KYJ
 *
 */
public class BehaviorCodeAreaHelper extends CodeAreaHelper<CodeArea> {

	public BehaviorCodeAreaHelper(CodeArea codeArea) {
		super(codeArea);
	}

	protected void init() {
		defaultSelectionHandler = new CodeAreaDefaultSelectionHandler(codeArea);
		this.codeArea.setOnMouseClicked(defaultSelectionHandler);
		this.codeMoveDeligator = new CodeAreaMoveLineHelper(codeArea);
		//사용하지않음 별도 구현.
//		this.dragDropHelper = new BehaviorCodeAreaFileDragDropHelper(codeArea);
		this.findAndReplaceHelper = new CodeAreaFindAndReplaceHelper<>(codeArea);
		// this.codeArea.addEventHandler(MouseDragEvent.MOUSE_DRAG_OVER,
		// this::codeAreaDagOver);
		// this.codeArea.addEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED_TARGET,
		// this::codeAreaDagEnteredTarget);

		contextMenu = codeArea.getContextMenu();
		if (contextMenu == null) {
			contextMenu = new ContextMenu();
			codeArea.setContextMenu(contextMenu);
		}
		createMenus();
	}
}
