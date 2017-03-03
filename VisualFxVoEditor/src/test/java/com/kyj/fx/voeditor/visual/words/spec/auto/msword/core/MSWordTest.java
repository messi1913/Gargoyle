/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.words.spec.auto.msword.core
 *	작성일   : 2017. 3. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.words.spec.auto.msword.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Test;

import com.kyj.fx.voeditor.visual.util.FileUtil;

/**
 * @author KYJ
 *
 */
public class MSWordTest {

	@Test
	public final void toPdf() throws FileNotFoundException, IOException {
		File file = new File("C:\\Users\\KYJ\\Desktop\\학습\\Algorism.docx");
		XWPFDocument document = new XWPFDocument(new FileInputStream(file));

		File outFile = new File("Algorism.pdf");

		try (OutputStream out = new FileOutputStream(outFile)) {
			PdfOptions options = PdfOptions.getDefault();
			PdfConverter.getInstance().convert(document, out, options);
		}
		System.out.println("Sucess");


		FileUtil.openFile(outFile);


	}

}
