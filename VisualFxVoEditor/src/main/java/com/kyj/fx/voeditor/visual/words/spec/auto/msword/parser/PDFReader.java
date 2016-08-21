/**
 *
 */
package com.kyj.fx.voeditor.visual.words.spec.auto.msword.parser;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;

import com.kyj.fx.voeditor.visual.component.ImageViewPane;

/**
 * 1.8 버젼 pdf박스
 *
 * @author KYJ
 *
 */
public class PDFReader {

//	public static void main(String[] args) throws IOException {
//		PDDocument doc = new PDDocument();
//		try {
//			doc = PDDocument.load("구글 원격 데스크톱 사용기.pdf");
//			PDDocumentCatalog catal = doc.getDocumentCatalog();
//			List<PDPage> allPages = catal.getAllPages();
//			PDPage pdPage = allPages.get(0);
//
//			BufferedImage convertToImage = pdPage.convertToImage();
//
//			ByteArrayInputStream inputStream = toInputStream(convertToImage);
//
//			new ImageViewPane(inputStream);
//			// ImageIO.write(convertToImage, "png", new File("구글원격데스크톱사용기이미지"));
//
//		} finally {
//			if (doc != null) {
//				doc.close();
//			}
//		}
//	}
//
//	public static ByteArrayInputStream toInputStream(BufferedImage bufferedImage) throws IOException {
//		final ByteArrayOutputStream output = new ByteArrayOutputStream() {
//			@Override
//			public synchronized byte[] toByteArray() {
//				return this.buf;
//			}
//		};
//		ImageIO.write(bufferedImage, "png", output);
//		return new ByteArrayInputStream(output.toByteArray(), 0, output.size());
//	}
}
