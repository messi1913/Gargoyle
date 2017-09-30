/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2017. 2. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import javafx.scene.input.Clipboard;

/**
 * @author KYJ
 *
 */
public class IntegerField extends NumberField {

	public IntegerField() {
	}

	@Override
	public void replaceText(int start, int end, String text) {
		String newText = getNewText(start, end, text);
		if (!text.isEmpty() && // Always allow text deletion
				!partOfConstants(newText)) {
			try {
				Integer.parseInt(newText);
			} catch (NumberFormatException e) {
				return;
			}
		}
		super.replaceText(start, end, text);
	}

	@Override
	public void paste() {
		String strToPaste = Clipboard.getSystemClipboard().getString();
		try {
			Integer.parseInt(strToPaste);
		} catch (NumberFormatException e) {
			return;
		}
		super.paste();
	}
}
