/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 4. 28.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.Test;

/**
 * @author KYJ
 *
 */
public class SqlFormatterTest {

	@Test
	public void simple() throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT\n");
		sb.append("    OP_SYS_CODE,\n");
		sb.append("    CD_ID,\n");
		sb.append("    INSP_STEP_CODE,\n");
		sb.append("    SRC_SEQ,\n");
		sb.append("    CI_ID,\n");
		sb.append("    CI_NM,\n");
		sb.append("    PREV_REV_NO,\n");
		sb.append("    CURR_REV_NO \n");
		sb.append("FROM\n");
		sb.append("    TBP_DEV_CODE_INSP_RSLT \n");
		sb.append("WHERE\n");
		sb.append("    1=1 \n");
		sb.append("    AND DEL_YN='N' \n");
		sb.append("    AND USE_YN='Y' \n");
		sb.append("    #if($opSysCode) \n");
		sb.append("    AND OP_SYS_CODE = :opSysCode \n");
		sb.append("    #end\n");
		sb.append("    #if($cdId) \n");
		sb.append("    AND CD_ID = :cdId \n");
		sb.append("    #end\n");
		sb.append("    #if($inspStepCode) \n");
		sb.append("    AND INSP_STEP_CODE = :inspStepCode \n");
		sb.append("    #end\n");
		sb.append("    #if($srcSeq) \n");
		sb.append("    SRC_SEQ = :srcSeq \n");
		sb.append("    #end\n");
		sb.toString();

		List<Map<String, Object>> select = DbUtil.select("select sql_body from tbp_sys_dao_methods");

//		OffScreenImage offScreenImage = new sun.awt.image.OffScreenImage(new Label("sss"), ColorModel.getRGBdefault(), null, false);
//		boolean writeImage = ImageIOUtil.writeImage(offScreenImage, "Sample.png", 720);


		SqlFormatter formatter = new SqlFormatter();
		String format = formatter.format(sb.toString());
		System.out.println(format);



//		PDDocument document = new PDDocument();
//		PDPage page = new PDPage();
//		document.addPage(page);
//
//
//		PDFont font = PDType1Font.HELVETICA_BOLD;
//
//		// Start a new content stream which will "hold" the to be created content
//		PDPageContentStream contentStream = new PDPageContentStream(document, page);
//
//		// Define a text content stream using the selected font, moving the cursor and drawing the text "Hello World"
//		contentStream.beginText();
//		contentStream.setFont( font, 12 );
//		contentStream.newLineAtOffset( 100, 700 );
//		contentStream.showText( "Hello World" );
//		contentStream.endText();
//
//		contentStream.close();
//
//		document.save(new File("Hello"));
//		document.close();
	}
}
