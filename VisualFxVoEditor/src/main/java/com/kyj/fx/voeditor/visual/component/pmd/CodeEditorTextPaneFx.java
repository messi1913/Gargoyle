/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package com.kyj.fx.voeditor.visual.component.pmd;

import javax.swing.SwingUtilities;

import javafx.scene.control.TextArea;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.designer.LineGetter;

public class CodeEditorTextPaneFx extends TextArea implements LineGetter {

	private String[] getLines() {
		// Support files with line separators from various platforms
		return getText().split("\r\n|\r|\n");
	}

	public String getLine(int number) {

		String[] lines = getLines();
		if (number < lines.length) {
			return lines[number];
		}
		throw new RuntimeException("Line number " + number + " not found");
	}

	private int getPosition(String[] lines, int line, int column) {
		int pos = 0;
		for (int count = 0; count < lines.length;) {
			String tok = lines[count++];
			if (count == line) {
				int linePos = 0;
				int i;
				for (i = 0; linePos < column && linePos < tok.length(); i++) {
					linePos++;
					if (tok.charAt(i) == '\t') {
						linePos--;
						linePos += 8 - (linePos & 07);
					}
				}

				return pos + i - 1;
			}
			pos += tok.length() + 1;
		}
		throw new RuntimeException("Line " + line + " not found");
	}

	public void select(Node node) {

		SwingUtilities.invokeLater(() -> {

			String[] lines = getLines();
			if (node.getBeginLine() >= 0) {

				selectRange(getPosition(lines, node.getBeginLine(), node.getBeginColumn()),
						getPosition(lines, node.getEndLine(), node.getEndColumn()) + 1);
				//				setSelectionStart(getPosition(lines, node.getBeginLine(), node.getBeginColumn()));
				//				setSelectionEnd(getPosition(lines, node.getEndLine(), node.getEndColumn()) + 1);
			}

//			requestFocus();
		});

	}
}
