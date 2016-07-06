/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.words
 *	작성일   : 2016. 1. 25.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.words;

/**
 * DefaultDocumentFormatRegistry가 오래전에 작성된 프로그램이라서 
 * 지원되는 포멧형태가 부족한부분을 커버함.
 *  
 * @author KYJ
 *
 */

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentFamily;
import com.artofsolving.jodconverter.DocumentFormat;

public class CustomDocFormatRegistry extends DefaultDocumentFormatRegistry {

	public CustomDocFormatRegistry() {

		final DocumentFormat doc = new DocumentFormat("Microsoft Word", DocumentFamily.TEXT, "application/msword", "docx");
		doc.setExportFilter(DocumentFamily.TEXT, "MS Word 2007");
		addDocumentFormat(doc);

		final DocumentFormat xlsx = new DocumentFormat("Microsoft Excel", DocumentFamily.SPREADSHEET, "application/vnd.ms-excel", "xlsx");
		xlsx.setExportFilter(DocumentFamily.SPREADSHEET, "MS Excel 2007");
		addDocumentFormat(xlsx);

		final DocumentFormat pptx = new DocumentFormat("Microsoft PowerPoint", DocumentFamily.PRESENTATION,
				"application/vnd.ms-powerpoint", "pptx");
		pptx.setExportFilter(DocumentFamily.PRESENTATION, "MS PowerPoint 2007");
		addDocumentFormat(pptx);
	}
}
