/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.pdf.template
 *	작성일   : 2016. 9. 26.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.pdf.concreate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.pdf.core.PDFHelper;

/**
 * @author KYJ
 *
 */
public class SimpleTextPDFHelper extends PDFHelper<PDPage, PDPageContentStream> {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTextPDFHelper.class);

	private String content;

	private List<String> lines = new ArrayList<String>();

	/**
	 * @param pdfUtil
	 * @param document
	 */
	public SimpleTextPDFHelper(File f) {
		this(f, "");
	}

	public SimpleTextPDFHelper(File f, String content) {
		super(f);
		this.content = content;
	}

	//	public SimpleTextPDFConsumer(String text) {
	//		this.text = text;
	//	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.util.PDFUtil.PDFConsumer#accept(org.apache.pdfbox.pdmodel.PDPage, org.apache.pdfbox.pdmodel.PDPageContentStream)
	 */
	@Override
	public void accept(PDPage page, PDPageContentStream contentStream) throws Exception {

		String[] texts = this.content.split("\n");
		for (String text : texts) {
			drawText(page, contentStream, text);
		}

	}

	public void drawText(PDPage page, PDPageContentStream contentStream, String _text) throws IOException {

		String text = _text;
		//		contentStream.beginText();
		//		contentStream.setFont(getDefaultFont(), 12);
		//		contentStream.newLine();
		//		contentStream.newLineAtOffset(100, 700);
		//		contentStream.showText(this.text);
		//		contentStream.endText();

		float fontSize = 25;
		float leading = 1.5f * fontSize;

		PDRectangle mediabox = page.getMediaBox();
		float margin = 72;
		float width = mediabox.getWidth() - 2 * margin;
		float startX = mediabox.getLowerLeftX() + margin;
		float startY = mediabox.getUpperRightY() - margin;

		int lastSpace = -1;
		while (text.length() > 0) {

			int spaceIndex = text.indexOf(' ', lastSpace + 1);//text.indexOf("\n", lastSpace + 1);
			if (spaceIndex < 0) {
				spaceIndex = text.length();
			}

			String subString = text.substring(0, spaceIndex);
			float size = fontSize * getDefaultFont().getStringWidth(subString) / 1000;
			LOGGER.debug("'{}' - {} of {}\n", subString, size, width);
			if (size > width) {

				if (lastSpace < 0)
					lastSpace = spaceIndex;

				subString = text.substring(0, lastSpace);
				lines.add(subString);
				text = text.substring(lastSpace).trim();
				LOGGER.debug("'{}' is line\n", subString);
				lastSpace = -1;
			} else if (spaceIndex == text.length()) {
				lines.add(text);
				LOGGER.debug("'{}' is line\n", text);
				text = "";
			} else {
				lastSpace = spaceIndex;
			}
		}

		contentStream.beginText();
		contentStream.setFont(getDefaultFont(), fontSize);
		contentStream.newLineAtOffset(startX, startY);
		for (String line : lines) {
			contentStream.showText(line);
			contentStream.newLineAtOffset(0, -leading);
		}
		contentStream.endText();

	}

}
