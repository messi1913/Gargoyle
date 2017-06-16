/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   :  2016. 02. 22.
 *	프로젝트 : VisualFxVoEditor
 *	작성자   :  KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.main.layout.CloseableParent;

/**
 * PDF 조회처리를 위한 패널
 *
 * @author KYJ
 *
 */
public class PDFImageBasePaneWrapper extends CloseableParent<PDFImageBasePane> {
	private static Logger LOGGER = LoggerFactory.getLogger(PDFImageBasePaneWrapper.class);

	public PDFImageBasePaneWrapper(PDFImageBasePane parent) {
		super(parent);
	}

	public PDFImageBasePaneWrapper(File pdfFile) throws IOException {
		super(new PDFImageBasePane(pdfFile));
	}

	@Override
	public void close() throws IOException {
		LOGGER.debug("Close doc . reuqest ");
		getParent().close();
	}
}
