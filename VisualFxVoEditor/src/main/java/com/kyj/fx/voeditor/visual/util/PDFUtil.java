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
import java.net.URL;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.pdf.concreate.SimpleTextPDFHelpeBuilder;
import com.kyj.fx.voeditor.visual.framework.pdf.core.PDFHelperBuilder;

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
		PDFHelperBuilder<PDPage, PDPageContentStream> helper = new SimpleTextPDFHelpeBuilder(f, text);
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
		PDFont font = PDType0Font.load(doc, ClassLoader.getSystemClassLoader().getResourceAsStream(FxUtil.FONTS_NANUMBARUNGOTHIC_TTF));
		return font;
	}

	public static PDFont getFont(PDDocument doc, URL fontURL) throws IOException {
		PDFont font = PDType0Font.load(doc, fontURL.openStream());
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

	/*************************************************************************************************************************************/
	// PDF 페이지내 컨텐츠를 페이지별로  파일로 변환하는 API 작성
	/*************************************************************************************************************************************/

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 20.
	 * @param pdfFile
	 * @throws IOException
	 */
	public static void toImage(File pdfFile, File outDir) throws IOException {
		toImage(pdfFile, outDir, -1);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 20.
	 * @param pdfFile
	 * @param pageIndex
	 * @throws IOException
	 */
	public static void toImage(File pdfFile, File outDir, int pageIndex) throws IOException {
		PdfToImageHandler handler = new PdfToImageHandler(pdfFile, outDir) {

			@Override
			public void write(File dir, int page, BufferedImage img) {
				try {
					ImageIO.write(img, "png", new File(dir, page + ".png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		handler.setStartPage(pageIndex);
		handler.setEndPage(pageIndex);
		PDFUtil.toImage(handler);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 20.
	 * @param pdfFile
	 * @param handler
	 * @throws IOException
	 */
	public static void toImage(PdfToImageHandler handler) throws IOException {
		File pdfFile = handler.getPdfFile();
		try (PDDocument doc = PDDocument.load(pdfFile)) {
			PDFRenderer pdfRenderer = new PDFRenderer(doc);
			PDDocumentCatalog catal = doc.getDocumentCatalog();
			PDPageTree pages = catal.getPages();
			int totalPageCount = pages.getCount();
			int start = handler.getStartPage();
			int end = handler.getEndPage();

			//페이지 유효성 검증
			if (start > end) {
				throw new RuntimeException(String.format("Invalide page index start : %d end : %d", start, end));
			}

			if (start == -1)
				start = 0;
			if (end == -1)
				end = totalPageCount;

			if (end > totalPageCount) {
				end = totalPageCount;
			}

			// 파일 디렉토리 검증
			File outputDir = handler.getOutputDir();
			if (!outputDir.isDirectory()) {
				throw new RuntimeException("OutputDir is not Directory.");
			}

			//디렉토리가 없으면 생성
			if (!outputDir.exists())
				outputDir.mkdirs();

			for (int currentPage = start; currentPage < totalPageCount; currentPage++) {
				if (currentPage > end)
					break;
				BufferedImage renderImage = pdfRenderer.renderImage(currentPage);
				handler.write(outputDir, currentPage, renderImage);
			}
		}

	}

	/*************************************************************************************************************************************/
	/// 이미지 파일의 내용을 PDF 파일로 만드는 API 작성
	/*************************************************************************************************************************************/

}
