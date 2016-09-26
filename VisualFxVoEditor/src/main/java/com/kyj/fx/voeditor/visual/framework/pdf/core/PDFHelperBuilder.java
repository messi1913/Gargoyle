/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.pdf.core
 *	작성일   : 2016. 9. 26.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.pdf.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.PDFUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * Pdf write
 * @author KYJ
 *
 */
public abstract class PDFHelperBuilder<P extends PDPage, S extends PDPageContentStream> implements PDFMotionable<P, S>, PDFResourcea {

	private static final Logger LOGGER = LoggerFactory.getLogger(PDFUtil.class);

	private File newFile;
	private int newPageCount = 1;
	private PDDocument document;
	private PDFont defaultFont;
	private URL customFontUrl;

	private Consumer<Exception> errorHandler = e -> {
		throw new RuntimeException(e);
	};

	public PDFHelperBuilder(File newFile) {
		this.newFile = newFile;
	}

	public PDFHelperBuilder<P, S> setPageCount(int newPageCount) {
		this.newPageCount = newPageCount;
		return this;
	}

	/**
	 * 에러가 발생하면 후처리할 핸드러 setter
	 * @작성자 : KYJ
	 * @작성일 : 2016. 9. 26.
	 * @param errorHandler
	 */
	public PDFHelperBuilder<P, S> setExceptionHandler(Consumer<Exception> errorHandler) {
		this.errorHandler = errorHandler;
		return this;
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.framework.pdf.core.PDFMotionable#getPdDocument()
	 */
	@Override
	public PDDocument getPdDocument() {
		return this.document;
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.framework.pdf.core.PDFMotionable#accept(org.apache.pdfbox.pdmodel.PDPage, org.apache.pdfbox.pdmodel.PDPageContentStream)
	 */
	public abstract void accept(PDPage t, PDPageContentStream u) throws Exception;

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.framework.pdf.core.PDFResourcea#getDefaultFont()
	 */
	@Override
	public PDFont getDefaultFont() throws IOException {
		if (defaultFont == null)
			defaultFont = PDFResourcea.super.getDefaultFont();
		return defaultFont;
	}

	public PDFHelperBuilder<P, S> setCustomFont(URL customFontUrl) {
		this.customFontUrl = customFontUrl;
		return this;
	}

	public boolean build() {

		boolean flag = true;
		try {
			document = new PDDocument();

			//Font Load
			try {
				if (this.customFontUrl != null)
					defaultFont = PDFUtil.getFont(document, this.customFontUrl);
			} catch (IOException e) {
				errorHandler.accept(e);
				return false;
			}

			if (defaultFont == null)
				defaultFont = getDefaultFont();

			for (int i = 0; i < newPageCount; i++) {

				PDPage page = new PDPage();
				PDPageContentStream contentStream = new PDPageContentStream(document, page);
				try {
					accept(page, contentStream);
					document.addPage(page);
				} catch (Exception e) {
					errorHandler.accept(e);
					flag = false;
				}
				contentStream.close();
			}
			document.save(newFile);
		} catch (IOException e1) {
			flag = false;
			LOGGER.error(ValueUtil.toString(e1));
		} finally {

			if (this.document != null) {
				try {
					this.document.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		return flag;
	}

}