/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2016. 10. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import java.util.Collection;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.Paragraph;

/**
 * @author KYJ
 *
 */
public class CodeAreaMoveLineHelper {

	private CodeArea codeArea;

	public CodeAreaMoveLineHelper(CodeArea codeArea) {
		this.codeArea = codeArea;
	}

	/**
	 * 특정라인으로 이동처리하는 메소드
	 *
	 * 특정라인블록 전체를 선택처리함.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 4.
	 * @param moveToLine
	 */
	@SuppressWarnings("rawtypes")
	public void moveToLine(int moveToLine) {

		int position = 0;
		int row = moveToLine - 1;
		for (Paragraph par : codeArea.getParagraphs().subList(0, row)) {
			position += par.length() + 1; // account for line terminators
		}
		Paragraph<Collection<String>> paragraph = codeArea.getParagraphs().get(row);
		int length = paragraph.length();
		codeArea.selectRange(position, (position + length));
	}

	@SuppressWarnings("rawtypes")
	public void moveToLine(int moveToLine, int startCol) {
		int position = 0;
		int row = moveToLine - 1;
		for (Paragraph par : codeArea.getParagraphs().subList(0, row)) {
			position += par.length() + 1; // account for line terminators
		}
		Paragraph<Collection<String>> paragraph = codeArea.getParagraphs().get(row);
		int length = paragraph.length();

		int lineStartLength = position + startCol;
		int lineEndLength = position + length;
		codeArea.selectRange(lineStartLength, lineEndLength);
	}

	@SuppressWarnings("rawtypes")
	public void moveToLine(int moveToLine, int startCol, int endCol) {

		int position = 0;
		int row = moveToLine - 1;
		for (Paragraph par : codeArea.getParagraphs().subList(0, row)) {
			position += par.length() + 1; // account for line terminators
		}
		//		Paragraph<Collection<String>> paragraph = codeArea.getParagraphs().get(row);
		//		int length = paragraph.length();

		int lineStartLength = position + startCol;
		int lineEndLength = position + endCol;
		codeArea.selectRange(lineStartLength, lineEndLength);
	}

}
