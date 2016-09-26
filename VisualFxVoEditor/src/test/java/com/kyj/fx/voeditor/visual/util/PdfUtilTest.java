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

/**
 * @author KYJ
 *
 */
public class PdfUtilTest {

	@Test
	public void test() {
		File newFile = new File("Hello.pdf");

		String text = "HelloWorld! \nhell abcdefghijklmnopqrstuvwxyz sadasdasdasasdas asdasdasdasdas \n\nzzzz";
		boolean createNew = PDFUtil.createNew(newFile, text);

		if (createNew)
			FileUtil.openFile(newFile);

		Assert.assertEquals(true, createNew);

	}
}
