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
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.kyj.fx.voeditor.visual.framework.pdf.core.PDFHelper;

/**
 * @author KYJ
 *
 */
public class SimpleTextPDFHelper extends PDFHelper<PDPage, PDPageContentStream> {

	private String text = "";
	int offsetRow = 0;

	/**
	 * @param pdfUtil
	 * @param document
	 */
	public SimpleTextPDFHelper(File f) {
		super(f);
	}

	public SimpleTextPDFHelper(File f, String text) {
		super(f);
		this.text = text;
	}

	//	public SimpleTextPDFConsumer(String text) {
	//		this.text = text;
	//	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.util.PDFUtil.PDFConsumer#accept(org.apache.pdfbox.pdmodel.PDPage, org.apache.pdfbox.pdmodel.PDPageContentStream)
	 */
	@Override
	public void accept(PDPage page, PDPageContentStream contentStream) throws Exception {

		drawText(page, contentStream, text);

	}

	public void drawText(PDPage page, PDPageContentStream contentStream, String text) throws IOException {
		//		contentStream.beginText();
		//		contentStream.setFont(getDefaultFont(), 12);
		//		contentStream.newLine();
		//		contentStream.newLineAtOffset(100, 700);
		//		contentStream.showText(this.text);
		//		contentStream.endText();

		PDFont defaultFont = getDefaultFont();
		float fontSize = 25;
		float leading = 1.5f * fontSize;

		PDRectangle mediabox = page.getMediaBox(); //page.findMediaBox();
		float margin = 72;
		float width = mediabox.getWidth() - 2 * margin;
		float startX = mediabox.getLowerLeftX() + margin;
		float startY = mediabox.getUpperRightY() - margin;

		//		String text = "I am trying to create a PDF file with a lot of text contents in the document. I am using PDFBox";
		List<String> lines = new ArrayList<String>();
		int lastSpace = -1;
		while (text.length() > 0) {
			int spaceIndex = text.indexOf(' ', lastSpace + 1);
			if (spaceIndex < 0)
				spaceIndex = text.length();
			String subString = text.substring(0, spaceIndex);
			float size = fontSize * defaultFont.getStringWidth(subString) / 1000;
			System.out.printf("'%s' - %f of %f\n", subString, size, width);
			if (size > width) {
				if (lastSpace < 0)
					lastSpace = spaceIndex;
				subString = text.substring(0, lastSpace);
				lines.add(subString);
				text = text.substring(lastSpace).trim();
				System.out.printf("'%s' is line\n", subString);
				lastSpace = -1;
			} else if (spaceIndex == text.length()) {
				lines.add(text);
				System.out.printf("'%s' is line\n", text);
				text = "";
			} else {
				lastSpace = spaceIndex;
			}
		}

		contentStream.beginText();
		contentStream.setFont(defaultFont, fontSize);
		contentStream.newLineAtOffset(startX, startY);
		for (String line : lines) {
			contentStream.showText(line);
			contentStream.newLineAtOffset(0, -leading);
		}
		contentStream.endText();

	}

}
