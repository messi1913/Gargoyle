/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.pdf.core
 *	작성일   : 2016. 9. 26.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.pdf.core;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.kyj.fx.voeditor.visual.util.PDFUtil;

/**
 * PDF처리에 대한 자원관리
 * @author KYJ
 *
 */
public interface PDFResourcea {

	/**
	 * API내에서 사용할 기본 폰트를 리턴한다.
	 *
	 * 기본폰트를 가져오는 방법에 대한 정책을 기술한다.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 9. 26.
	 * @return
	 * @throws IOException
	 */
	public default PDFont getDefaultFont() throws IOException {
		return PDFUtil.getFont(getPdDocument());
	}

	/**
	 * PDF 문서 getter
	 * PDFDocument를 어떻게 가져올것인가에 대한 정책을 기술.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 9. 26.
	 * @return
	 */
	public PDDocument getPdDocument();

}
