/********************************
 *	프로젝트 : pmd.core
 *	패키지   : pmd.core
 *	작성일   : 2016. 9. 30.
 *	작성자   : KYJ
 *******************************/
package pmd.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Test;
import org.ky.pmd.core.PMD;

import com.google.common.io.Files;

/***************************
 * 
 * @author KYJ
 *
 ***************************/
public class PMDTest {

	@Test
	public void test() throws IOException{
		String fileString = "C:\\Users\\KYJ\\git\\Gargoyle\\VisualFxVoEditor\\src\\main\\java\\com\\kyj\\fx\\voeditor\\visual\\util\\ValueUtil.java";
		
		File javaFile = new File(fileString);
		String ruleSetFile = /*"java-basic";*/new File("rulesets/java/basic.xml").getAbsolutePath();
		String pmdReportFileName = new File("result.xml").getAbsolutePath();
		System.out.println(javaFile.getAbsolutePath());
		System.out.println(pmdReportFileName);
//		String[] args = new String[] { "-d ", javaFile.getAbsolutePath(), "-f", "xml", "-R", ruleSetFile, "-r", pmdReportFileName , "-version" , "1.8", "-language", "java" , "-debug" };
		
		String string = Files.toString(javaFile, Charset.forName("UTF-8"));
//		System.out.println(string);
		String[] args = new String[] { "-sourceText ",string, "-f", "xml", "-R", ruleSetFile, "-r", pmdReportFileName , "-version" , "1.8", "-language", "java" , "-debug" };
		
		PMD.main(args);
	}
}
