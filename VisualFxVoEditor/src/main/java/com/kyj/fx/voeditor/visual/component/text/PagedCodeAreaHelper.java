/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2016. 10. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import org.fxmisc.richtext.CodeArea;

import com.kyj.fx.voeditor.visual.component.popup.BigTextView;
import com.sun.star.uno.RuntimeException;

import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * 코드 처리 관련 Helper 클래스
 *
 * CodeArea클래스와 연관된 모든 공통처리내용이 구현된다.
 *
 *  2017.01.13 FindAndReplace를 별도의 Helper 클래스로 변경처리. by kyj.
 * @author KYJ
 *
 */
public class PagedCodeAreaHelper<T extends CodeArea> extends CodeAreaHelper<T> {

	private BigTextView bigTextView;

	public PagedCodeAreaHelper(BigTextView bigTextView, T codeArea) {
		super(codeArea);
		this.bigTextView = bigTextView;
	}

	PagedCodeAreaFindAndReplaceHelper findAndReplaceHelper;

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.text.CodeAreaHelper#init()
	 */
	@Override
	protected void init() {

		if (codeArea == null)
			throw new RuntimeException("TextView is null ");
		defaultSelectionHandler = new CodeAreaDefaultSelectionHandler(codeArea);
		this.codeArea.setOnMouseClicked(defaultSelectionHandler);
		this.codeMoveDeligator = new CodeAreaMoveLineHelper(codeArea);
		this.dragDropHelper = new CodeAreaFileDragDropHelper(codeArea);
		this.findAndReplaceHelper = new PagedCodeAreaFindAndReplaceHelper(bigTextView);

		contextMenu = codeArea.getContextMenu();
		if (contextMenu == null) {
			contextMenu = new ContextMenu();
			codeArea.setContextMenu(contextMenu);
		}
		createMenus();
	}

	/**
	 * 키클릭 이벤트 처리
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 14.
	 * @param e
	 */
	public void codeAreaKeyClick(KeyEvent e) {

		if (KeyCode.F == e.getCode() && e.isControlDown() && !e.isShiftDown() && !e.isAltDown()) {
			if (!e.isConsumed()) {
				findAndReplaceHelper.findReplaceEvent(new ActionEvent());
				e.consume();
			}
		}

		else if (KeyCode.L == e.getCode() && e.isControlDown() && !e.isShiftDown() && !e.isAltDown()) {
			if (!e.isConsumed()) {
				moveToLineEvent(new ActionEvent());
				e.consume();
			}
		}

		else if (KeyCode.U == e.getCode() && e.isControlDown() && e.isShiftDown() && !e.isAltDown()) {
			if (!e.isConsumed()) {
				toUppercaseEvent(new ActionEvent());
				e.consume();
			}
		} else if (KeyCode.L == e.getCode() && e.isControlDown() && e.isShiftDown() && !e.isAltDown()) {
			if (!e.isConsumed()) {
				toLowercaseEvent(new ActionEvent());
				e.consume();
			}
		} else {
			codeArea.getUndoManager().mark();
		}
	}

}
