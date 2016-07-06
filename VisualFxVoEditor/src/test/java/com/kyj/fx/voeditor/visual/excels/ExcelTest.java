/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.excels
 *	작성일   : 2015. 11. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.excels;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import com.kyj.fx.voeditor.visual.excels.base.ExcelDataDVO;
import com.kyj.fx.voeditor.visual.excels.base.ExcelSVO;
import com.kyj.fx.voeditor.visual.util.ExcelUtil;

/**
 * @author KYJ
 *
 */
public class ExcelTest {

	@Test
	public void simpleWrite() throws Exception {
		ExcelSVO svo = new ExcelSVO();
		svo.addSheetExcelDVO("sampleExcel1", new ExcelDataDVO(0, 0, "0:0 data"));
		String excelFileName = "c:\\Users\\kyj\\desktop\\sampleFile.xlsx";
		Stream<String> lines = Files.lines(Paths.get("c:", "G-MES2.0","gmes20CodeTemplete.xml"));
		lines.forEach(System.out::println);
		ExcelUtil.createExcel(excelFileName, svo, false);

		File file = new File(excelFileName);
		Assert.assertEquals(file.exists(), true);
		file.delete();
	}
}
