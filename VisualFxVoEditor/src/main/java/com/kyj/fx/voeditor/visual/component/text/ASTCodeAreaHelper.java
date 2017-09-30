/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2016. 12. 9.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import java.util.Collection;
import java.util.Map;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.Paragraph;

import com.kyj.fx.voeditor.visual.component.popup.ResourceView;

/**
 * @author KYJ
 *
 */
public abstract class ASTCodeAreaHelper {

	/**
	 * 다이얼로그를 보여줄 뷰를 정의.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 9.
	 * @return
	 */
	public abstract ResourceView<Map<String, Object>> createResourceView();

	protected int getIndexOfValideWhiteSpace(String string) {
		for (int i = string.length() - 1; i >= 0; i--) {
			if (Character.isWhitespace(string.charAt(i))) {
				return i + 1;
			}
		}
		return 0;
	}

	protected Paragraph<Collection<String>, Collection<String>> currentParagraphRange(CodeArea textArea) {
		int currentParagraph = textArea.getCurrentParagraph();
		return textArea.getDocument().getParagraphs().get(currentParagraph);
	}

	protected String currentPragraph(CodeArea textArea) {
		Paragraph<Collection<String>, Collection<String>> x = currentParagraphRange(textArea);
		return x.toString();
	}

	protected String markText(CodeArea textArea) {
		String string = currentPragraph(textArea);
		int markTextColumnIndex = getIndexOfValideWhiteSpace(string);
		string = string.substring(markTextColumnIndex);
		return string;
	}
}
