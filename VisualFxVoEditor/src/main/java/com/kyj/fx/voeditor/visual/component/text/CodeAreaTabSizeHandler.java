/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2017. 6. 28.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import org.fxmisc.richtext.CodeArea;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * 
 * CodeArea에 존재하는 Tab키 size 불편한 상태임
 * 하여 Tab키를 좀 더 사용하기 편하게 상태를 수정하는 역할을 처리함.
 * 
 *  참조 사이트  
 *  https://github.com/TomasMikula/RichTextFX/issues/86
 *  
 * @author KYJ
 *
 */
public class CodeAreaTabSizeHandler implements EventHandler<KeyEvent> {

	private CodeArea codeArea;

	/**
	 * 
	 */
	public CodeAreaTabSizeHandler(CodeArea codeArea) {
		this.codeArea = codeArea;
	}

	@Override
	public void handle(KeyEvent event) {

		
		if (event.getCode() == KeyCode.TAB) {

			/*
			 * 2017-07-02
			 *  bug fix
			 *  shift + tab , tab기능과 중복되기 때문에 tab키만 눌렀을때 
			 *  기능이 처리되게 한다. 
			 */
			if (event.isAltDown() || event.isControlDown() || event.isShiftDown() || event.isShortcutDown()) {
				return;
			}

			if (event.isConsumed())
				return;
			codeArea.replaceSelection("   ");
			event.consume();
		}

	}

}
