/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.pdf.core
 *	작성일   : 2016. 9. 26.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.pdf.core;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

/**
 * PDF 처리에 대한 구체적인 처리 로직이 기술됨.
 * @author KYJ
 *
 */
public interface PDFMotionable<P extends PDPage, S extends PDPageContentStream> {

	/**
	 * PDF처리의 실제 코드를 작성.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 9. 26.
	 * @param t
	 * @param u
	 * @throws Exception
	 */
	public abstract void accept(PDPage t, PDPageContentStream u) throws Exception;

}
