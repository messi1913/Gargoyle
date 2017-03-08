/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 9. 26.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.junit.Assert;
import org.junit.Test;

import com.kyj.fx.voeditor.visual.framework.pdf.concreate.TablePDFHelpeBuilder;

/**
 * @author KYJ
 *
 */
public class PdfUtilTest {

	@Test
	public void test() {
		File newFile = new File("Hello.pdf");

		//		String text = "HelloWorld! \nhell abcdefghijklmnopqrstuvwxyz sadasdasdasasdas asdasdasdasdas \n\nzzzz";
		//		boolean createNew = PDFUtil.createNew(newFile, text);
		String[][] contents = { { "레벨", "강화확률", "소모아데나", "필요 주문서양" }, { "1", "100", "3800", "1" }, { "2", "100", "3800", "1" },
				{ "3", "100", "3800", "1" }, };

		TablePDFHelpeBuilder tablePDFHelpeBuilder = new TablePDFHelpeBuilder(newFile, contents) {

			/* (non-Javadoc)
			 * @see com.kyj.fx.voeditor.visual.framework.pdf.concreate.TablePDFHelpeBuilder#accept(org.apache.pdfbox.pdmodel.PDPage, org.apache.pdfbox.pdmodel.PDPageContentStream)
			 */
			@Override
			public void accept(PDPage page, PDPageContentStream contentStream) throws Exception {
				drawTable(page, contentStream, contents);
			}

		};
		boolean build = tablePDFHelpeBuilder.build();
		if (build)
			FileUtil.openFile(newFile);

		Assert.assertEquals(true, build);

	}

	@Test
	public void pdfToImageTest() throws IOException {
		File file = new File("PdfOut");
		file.mkdirs();
		PDFUtil.toImage(new File("Hello.pdf"), file);
	}

	@Test
	public void createPdFromImageTest() throws IOException {
		File imgFile = new File("C:\\Users\\KYJ\\Pictures\\2014022503792_0.jpg");
		File pdfFile = new File("Sample.pdf");
		PDFUtil.toPdf(pdfFile, imgFile);

		FileUtil.openFile(pdfFile);
	}

}
