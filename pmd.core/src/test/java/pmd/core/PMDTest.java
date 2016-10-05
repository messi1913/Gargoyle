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
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import net.sourceforge.pmd.PMD;

/***************************
 *
 * @author KYJ
 *
 ***************************/
public class PMDTest {

	@Test
	public void test() throws IOException, InterruptedException {
		String fileString = "C:\\Users\\KYJ\\git\\Gargoyle\\VisualFxVoEditor\\src\\main\\java\\com\\kyj\\fx\\voeditor\\visual\\util\\ValueUtil.java";

		File javaFile = new File(fileString);
		String ruleSetFile = /*"java-basic";*/new File("rulesets/java/basic.xml").getAbsolutePath();
		File resultXml = new File("result.xml");
		String pmdReportFileName = resultXml.getAbsolutePath();
		System.out.println(javaFile.getAbsolutePath());
		System.out.println(pmdReportFileName);
		//		String[] args = new String[] { "-d ", javaFile.getAbsolutePath(), "-f", "xml", "-R", ruleSetFile, "-r", pmdReportFileName , "-version" , "1.8", "-language", "java" , "-debug" };

		String string = FileUtils.readFileToString(javaFile, Charset.forName("UTF-8"));
		//		System.out.println(string);
		String[] args = new String[] { "-sourceText ", string, "-f", "xml", "-R", ruleSetFile, "-r", pmdReportFileName, "-version", "1.8",
				"-language", "java", "-P", "driver=jdbc.oracle,url=10.20.2.2,id=ccc,password=ccc" };

		PMD.main(args);
		//		Thread.sleep(1000L);

		//		System.out.println(Files.toString(resultXml, Charset.forName("UTF-8")));
		//		System.out.println(Files.readLines(resultXml, Charset.forName("UTF-8")));
	}

	@Test
	public void testDir() throws IOException, InterruptedException {
		String relativePath = "src\\main\\java\\net\\sourceforge\\pmd";
		String fileString = new File(relativePath).getAbsolutePath();

		//		File javaFile = new File(fileString);
		String ruleSetFile = /*"java-basic";*/new File("rulesets/java/basic.xml").getAbsolutePath();
		File resultXml = new File("result.xml");
		String pmdReportFileName = resultXml.getAbsolutePath();
		System.out.println(fileString);
		System.out.println(pmdReportFileName);
		//		String[] args = new String[] { "-d ", javaFile.getAbsolutePath(), "-f", "xml", "-R", ruleSetFile, "-r", pmdReportFileName , "-version" , "1.8", "-language", "java" , "-debug" };

		//		String string = Files.toString(javaFile, Charset.forName("UTF-8"));
		//		System.out.println(string);
		String[] args = new String[] { "-d", fileString, "-f", "xml", "-R", ruleSetFile, "-r", pmdReportFileName, "-version", "1.8",
				"-language", "java", "-P", "driver=jdbc.oracle,url=10.20.2.2,id=ccc,password=ccc", "-debug" };

		PMD.main(args);
		//		Thread.sleep(1000L);

		//		System.out.println(Files.toString(resultXml, Charset.forName("UTF-8")));
		//		System.out.println(Files.readLines(resultXml, Charset.forName("UTF-8")));
	}

}
