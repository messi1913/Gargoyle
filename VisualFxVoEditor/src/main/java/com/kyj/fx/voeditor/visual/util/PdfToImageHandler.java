/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2017. 2. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 *
 * PDF 파일의 내용을 이미지로 변환시키는 처리를
 * 지원하기 위한 핸들러객체.
 * @author KYJ
 *
 */
public abstract class PdfToImageHandler {

	private File pdfFile;
	private int startPage = -1;
	private int endPage = -1;
	private File outputDir;

	public PdfToImageHandler(File pdfFile, File outputDir) {
		this.pdfFile = pdfFile;
		this.outputDir = outputDir;
	}

	/**
	 * @return the startPage
	 */
	public final int getStartPage() {
		return startPage;
	}

	/**
	 * @return the endPage
	 */
	public final int getEndPage() {
		return endPage;
	}

	/**
	 * @param startPage the startPage to set
	 */
	public final void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	/**
	 * @param endPage the endPage to set
	 */
	public final void setEndPage(int endPage) {
		this.endPage = endPage;
	}

	/**
	 * @return the outputDir
	 */
	public final File getOutputDir() {
		return outputDir;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 21.
	 * @param dir
	 * @param page
	 * @param img
	 */
	public abstract void write(File dir, int page, BufferedImage img);

	/**
	 * @return
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 21.
	 */
	public File getPdfFile() {
		return this.pdfFile;
	}

}
