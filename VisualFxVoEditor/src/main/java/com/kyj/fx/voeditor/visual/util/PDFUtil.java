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
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.tools.imageio.ImageIOUtil;

public class PDFUtil {

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
