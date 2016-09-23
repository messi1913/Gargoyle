/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 02. 22.
 *	프로젝트 : VisualFxVoEditor
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiConsumer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

public class PDFUtil {

	/**
	 * 새로운 문서 작성.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 9. 23.
	 * @param newFile
	 * @param newPageCount
	 * @param consume
	 * @return
	 * @throws IOException
	 */
	public static File createNew(File newFile, int newPageCount, BiConsumer<PDPage, PDPageContentStream> consume) throws IOException {
		try (PDDocument document = new PDDocument()) {
			for (int i = 0; i < newPageCount; i++) {
				PDPage page = new PDPage();
				PDPageContentStream contentStream = new PDPageContentStream(document, page);
				consume.accept(page, contentStream);
				contentStream.close();
			}
			document.save(newFile);
		}
		return newFile;
	}

	/**
	 * 기본 폰트 리턴.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 9. 23.
	 * @return
	 */
	public static PDFont getDefaultFont() {
		return PDType1Font.HELVETICA_BOLD;
	}
	/**
	 * 비효율적이므로 deprecated
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 16.
	 * @param pdfFile
	 * @param page
	 * @return
	 * @throws IOException
	 */

	// public static InputStream getInputStream(File pdfFile, int page) throws
	// IOException {
	// PDDocument doc = PDDocument.load(pdfFile);
	// PDDocumentCatalog catal = doc.getDocumentCatalog();
	// List<PDPage> allPages = catal.getAllPages();
	// PDPage pdPage = allPages.get(page);
	// BufferedImage convertToImage = pdPage.convertToImage();
	// return toInputStream(convertToImage);
	// }

	/**
	 * inputStream으로 변환
	 *
	 * @param bufferedImage
	 * @return
	 * @throws IOException
	 */
	public static InputStream toInputStream(BufferedImage bufferedImage) throws IOException {
		final ByteArrayOutputStream output = new ByteArrayOutputStream() {
			@Override
			public byte[] toByteArray() {
				return this.buf;
			}
		};

		ImageIOUtil.writeImage(bufferedImage, "png", output);
		return new ByteArrayInputStream(output.toByteArray()/*, 0, output.size()*/);
	}
}
