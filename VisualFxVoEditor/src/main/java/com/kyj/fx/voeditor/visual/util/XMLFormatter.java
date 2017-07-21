/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2017. 7. 18.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

/**
 * @author KYJ
 *
 */
public class XMLFormatter implements Formatter {

	/**
	 * 
	 */
	public XMLFormatter() {

	}

	@Override
	public String format(String str) {

		int length = str.length();
		int depth = -1;
		boolean tagStart = false;
		boolean tagEnd = true;
		boolean isContentArea = false;

		boolean isMetaTag = false;

		boolean isDoubleQuoteBlockStart = false;
		boolean isDoubleQuoteBlockEnd = false;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {

			char currentC = str.charAt(i);

			if ('<' == currentC) {
				tagStart = true;
				tagEnd = false;

				if (i != length) {
					char nextChar = str.charAt(i + 1);
					if ('/' == nextChar) {
						if (!isDoubleQuoteBlockStart) {
							depth--;
						}
					} else if ('?' == nextChar) {
						depth--;
					}
				}
				sb.append(System.lineSeparator());
				sb.append(getEmptyString(depth));

				sb.append(currentC);

				depth++;
				continue;
			}

			if ('>' == currentC) {
				tagStart = false;
				tagEnd = true;
				sb.append(currentC);

				continue;
			}

			if (tagStart) {

				if ('/' == currentC) {

					if (!isDoubleQuoteBlockStart) {
						depth--;
						sb.append(currentC);
						continue;
					}

					sb.append(currentC);
					continue;
				}

			}

			if (!isDoubleQuoteBlockStart && tagStart && '"' == currentC) {
				isDoubleQuoteBlockStart = true;
				isDoubleQuoteBlockEnd = false;
				sb.append(currentC);
				continue;
			}

			if (isDoubleQuoteBlockStart && tagStart && '"' == currentC) {
				isDoubleQuoteBlockStart = false;
				isDoubleQuoteBlockEnd = true;
				sb.append(currentC);
				continue;
			}

			//data block start
			if (tagEnd && isContentArea) {
				sb.append(System.lineSeparator());
				sb.append("\t" + getEmptyString(depth));
			}

			// 태그 시작부터 내용이 기입
			if (tagStart && !tagEnd) {
				isContentArea = true;
				sb.append(currentC);
				continue;
				//태그 끝
			} else if (!tagStart && tagEnd) {
				isContentArea = false;
				sb.append(currentC);
				continue;
			}

		}

		return sb.toString();
	}

	public String getEmptyString(int depth) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < depth; i++)
			sb.append(" ");
		return sb.toString();
	}

	@Override
	public String toUpperCase(String source) {

		return null;
	}

	@Override
	public String toLowerCase(String source) {

		return null;
	}

	@Override
	public String split(String source, int position) {

		return null;
	}

}
