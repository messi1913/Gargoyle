/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 9. 26.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.kyj.fx.voeditor.visual.framework.pdf.concreate.TablePDFHelpeBuilder;

/**
 * @author KYJ
 *
 */
public class PdfUtilTest {

	@Test
	public void test() {
		File newFile = new File("Hello.pdf");

//		String text = "HelloWorld! \nhell abcdefghijklmnopqrstuvwxyz sadasdasdasasdas asdasdasdasdas \n\nzzzz";
//		boolean createNew = PDFUtil.createNew(newFile, text);
		String[][] contents = {{"a","b", "1"},
                {"c","d", "2"},
                {"e","f", "3"},
                {"g","h", "4"},
                {"i","j", "5"}} ;

		TablePDFHelpeBuilder tablePDFHelpeBuilder = new TablePDFHelpeBuilder(newFile, contents);
		boolean build = tablePDFHelpeBuilder.build();
		if (build)
			FileUtil.openFile(newFile);

		Assert.assertEquals(true, build);

	}
}
