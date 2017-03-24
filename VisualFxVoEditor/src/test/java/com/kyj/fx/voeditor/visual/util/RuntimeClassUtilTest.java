/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2017. 3. 22.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

/**
 * @author KYJ
 *
 */
public class RuntimeClassUtilTest {

	@Test
	public final void pmdTest() throws Exception {

		File srcFile = new File("src/main/java/com/kyj/fx/voeditor/visual/example");
		/*4.25 버젼 실행*/
		{

			File ruleSetFile = new File("lib/pmd/PMD_425/rulesets/basic.xml");
			File reportFile = new File("reporsets/report425.txt");
			run425(srcFile, ruleSetFile, reportFile);
		}

		/* 5.51 버젼 실행*/
		{
			File ruleSetFile = new File("lib/pmd/PMD_551/rulesets/basic.xml");
			File reportFile = new File("reporsets/report551.txt");
			run551(srcFile, ruleSetFile, reportFile);
		}

		/*4.25 버젼 실행*/
		{

			File ruleSetFile = new File("lib/pmd/PMD_425/rulesets/basic.xml");
			File reportFile = new File("reporsets/report425_2.txt");
			run425(srcFile, ruleSetFile, reportFile);
		}

	}

	private void run425(File srcFile, File ruleSetFile, File reportFile) throws Exception {
		String[] mainArguments = { "java", "-classpath", "lib/pmd/PMD_425/lib/*", "net.sourceforge.pmd.PMD" };
		String[] args = {
				/* */
				srcFile.getAbsolutePath(), /* */
				"text", ruleSetFile.getAbsolutePath(), /* */
				"-reportfile", reportFile.getAbsolutePath() /* */ };

		List<String> arguments = Stream.of(mainArguments, args).flatMap(arr -> {
			return Stream.of(arr);
		}).collect(Collectors.toList());

		RuntimeClassUtil.exe(arguments, Charset.forName("EUC-KR"), str -> {
			System.out.println(str);
		});
	}

	private void run551(File srcFile, File ruleSetFile, File reportFile) throws Exception {
		String[] mainArguments = { "java", "-classpath", "lib/pmd/PMD_551/lib/*", "net.sourceforge.pmd.PMD" };
		String[] args = { "-d", srcFile.getAbsolutePath() /*  */
		, "-f", "text" /*  */
				, "-R", ruleSetFile.getAbsolutePath() /*  Comma separated list of ruleset names to use */
				, "-reportfile", reportFile.getAbsolutePath() /*   */
				, "-v", "1.8" /* version */
				, "-l", "java"/* language, Specify a language PMD should use. */
				, "-encoding", "UTF-8", "-verbose" };

		List<String> arguments = Stream.of(mainArguments, args).flatMap(arr -> {
			return Stream.of(arr);
		}).collect(Collectors.toList());

		RuntimeClassUtil.exe(arguments, Charset.forName("EUC-KR"), str -> {
			System.out.println(str);
		});
	}

}
