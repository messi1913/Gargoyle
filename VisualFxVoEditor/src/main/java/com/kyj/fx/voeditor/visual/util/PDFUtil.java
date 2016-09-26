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
import java.util.function.Consumer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.pdf.concreate.SimpleTextPDFHelper;
import com.kyj.fx.voeditor.visual.framework.pdf.core.PDFHelper;

public class PDFUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(PDFUtil.class);

	/**
	 * 새로운 문서 작성.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 9. 23.
	 * @param newFile
	 * @param newPageCount pdf에서 생성되는 페이지 수
	 * @param helper
	 * @return 처리 성공여부
	 * @throws Exception
	 */
	public static boolean createNew(File f, String text, Consumer<Exception> errorHandler) {

		//		boolean flag = true;
		//		try (PDDocument document = new PDDocument()) {
		//			for (int i = 0; i < newPageCount; i++) {
		//				PDPage page = new PDPage();
		//				PDPageContentStream contentStream = new PDPageContentStream(document, page);
		//				try {
		//					helper.accept(page, contentStream);
		//					document.addPage(page);
		//				} catch (Exception e) {
		//					errorHandler.accept(e);
		//					flag = false;
		//				}
		//				contentStream.close();
		//			}
		//			document.save(newFile);
		//		} catch (IOException e1) {
		//			flag = false;
		//			LOGGER.error(ValueUtil.toString(e1));
		//		}
		PDFHelper<PDPage, PDPageContentStream> helper = new SimpleTextPDFHelper(f, text);
		helper.setPageCount(1);
		helper.setExceptionHandler(errorHandler);

		return helper.build();
	}

	/**
	 * 새로운 문서 작성.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 9. 26.
	 * @param newFile
	 * @param newPageCount pdf에서 생성되는 페이지 수
	 * @param consumer
	 * @return
	 */
	public static boolean createNew(File newFile, String text) {
		return createNew(newFile, text, e -> LOGGER.error(ValueUtil.toString(e)));

	}

	/**
	 * 기본 폰트 리턴.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 9. 23.
	 * @return
	 * @throws IOException
	 */
	public static PDFont getFont(PDDocument doc) throws IOException {
//		GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
//		Font[] allFonts = localGraphicsEnvironment.getAllFonts();
		PDFont font = PDType0Font.load(doc, ClassLoader.getSystemClassLoader().getResourceAsStream("fonts/NANUMBARUNGOTHIC.TTF"));
		return font;
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
