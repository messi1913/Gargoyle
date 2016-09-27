/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.pdf.template
 *	작성일   : 2016. 9. 26.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.pdf.concreate;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.pdf.core.PDFHelperBuilder;

/**
 * @author KYJ
 *
 */
public class TablePDFHelpeBuilder extends PDFHelperBuilder<PDPage, PDPageContentStream> {

	/**
	 * @최초생성일 2016. 9. 27.
	 */
	private static final Color defaultFontColor = Color.BLACK;

	/**
	 * @최초생성일 2016. 9. 27.
	 */
	private static final Color headerFontColor = Color.RED;

	private static final Logger LOGGER = LoggerFactory.getLogger(TablePDFHelpeBuilder.class);

	private String[][] contents;
	private float y = 700, margin = 100;
	private int headerRow;
	private int headerFontSize = 16;
	private int defaultFontSize = 12;

	/**
	 * @param pdfUtil
	 * @param document
	 */
	public TablePDFHelpeBuilder(File f) {
		this(f, new String[][] {});
	}

	public TablePDFHelpeBuilder(File f, String[][] contents) {
		super(f);
		this.contents = contents;
	}

	//	public SimpleTextPDFConsumer(String text) {
	//		this.text = text;
	//	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.util.PDFUtil.PDFConsumer#accept(org.apache.pdfbox.pdmodel.PDPage, org.apache.pdfbox.pdmodel.PDPageContentStream)
	 */
	@Override
	public void accept(PDPage page, PDPageContentStream contentStream) throws Exception {
		drawTable(page, contentStream, this.contents);
	}

	public void drawTable(PDPage page, PDPageContentStream contentStream, String[][] content) throws IOException {
		final int rows = content.length;
		final int cols = content[0].length;
		final float rowHeight = 20f;
		final float tableWidth = page.getMediaBox().getWidth() - margin - margin;
		final float tableHeight = rowHeight * rows;
		final float colWidth = tableWidth / (float) cols;
		final float cellMargin = 5f;

		//draw the rows
		float nexty = y;
		for (int i = 0; i <= rows; i++) {

			contentStream.moveTo(margin, nexty);
			contentStream.lineTo(margin + tableWidth, nexty);
			//선색깔 바꾸기
			//			contentStream.setStrokingColor(255,50,5);
			contentStream.stroke();
			//			contentStream.fillAndStroke();

			nexty -= rowHeight;
		}

		//draw the columns
		float nextx = margin;
		for (int i = 0; i <= cols; i++) {

			//deprecated
			//			contentStream.drawLine(nextx, y, nextx, y - tableHeight);

			contentStream.moveTo(nextx, y);
			contentStream.lineTo(nextx, y - tableHeight);
			contentStream.stroke();

			nextx += colWidth;
		}

		//now add the text
		PDFont defaultFont = getDefaultFont();
		//		BoundingBox boundingBox = defaultFont.getBoundingBox();

		contentStream.setFont(defaultFont, defaultFontSize);

		float textx = margin + cellMargin;
		float texty = y - 15;

		//텍스트 색깔 바꾸기

		for (int i = 0; i < content.length; i++) {

			if (headerRow == i) {
				contentStream.setFont(defaultFont, headerFontSize);
				contentStream.setNonStrokingColor(headerFontColor);
			} else {
				contentStream.setFont(defaultFont, defaultFontSize);
				contentStream.setNonStrokingColor(defaultFontColor);
			}

			for (int j = 0; j < content[i].length; j++) {
				String text = content[i][j];

				contentStream.beginText();
				contentStream.newLineAtOffset(textx, texty);
				contentStream.showText(text);
				contentStream.endText();

				textx += colWidth;
			}
			texty -= rowHeight;
			textx = margin + cellMargin;

		}

	}

}
