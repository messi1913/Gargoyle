/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : external.sqlconvert
 *	작성일   : 2016. 2. 29.
 *	작성자   : KYJ
 *******************************/
package external.sqlconvert;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.kyj.fx.voeditor.visual.util.RuntimeClassUtil;

/**
 * @author KYJ
 *
 */
public class Convert {

	@Test
	public void convert() throws Exception {
		String sourceSqlFile = "testSql.sql";

		String sourceType = "mysql";
		String targetType = "oracle";

		File exeFile = new File("sqlines307", "sqlines.exe");
		File parentFile = exeFile.getParentFile();
		File sourceFile = new File(parentFile, sourceSqlFile);
		String[] options = new String[] { exeFile.getAbsolutePath(), "-s=" + sourceType, "-t=" + targetType,
				"-in=" + sourceFile.getAbsolutePath() };

		List<String> exe = RuntimeClassUtil.exe(Arrays.asList(options));

		System.out.println("#######################################");
		System.out.println("Status :::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
		exe.forEach(System.out::println);
		System.out.println("#######################################");

		System.out.println("#######################################");
		System.out.println("Original ::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
		String result = FileUtils.readFileToString(sourceFile);
		System.out.println(result);
		System.out.println("#######################################");

		System.out.println("#######################################");
		System.out.println("Result :::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
		result = FileUtils.readFileToString(sourceFile);
		System.out.println(result);
		System.out.println("#######################################");
	}
}
