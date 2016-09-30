/********************************
 *	프로젝트 : pmd.core
 *	패키지   : org.ky.pmd.core
 *	작성일   : 2016. 9. 28.
 *	작성자   : KYJ
 *******************************/
package org.ky.pmd.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.function.Consumer;

/***************************
 * 
 * @author KYJ
 *
 ***************************/
public class PMDExec {

	public PMDExec() {

	}

	public void checkPMD(File reportFile, String version, File javaFile, String ruleSetFile, Consumer<String[]> exec) throws Exception {

		if (javaFile == null || !javaFile.exists()) {
			throw new FileNotFoundException("File Not Found " + javaFile);
		}

		/* 결과 레포트를 생성할 파일명 */
		String pmdReportFileName = reportFile.getAbsolutePath();

		String[] args = null;

		if ("425".equals(version)) {
			args = new String[] { javaFile.getAbsolutePath(), "xml", ruleSetFile, "-reportfile", pmdReportFileName };
		} else if ("514".equals(version)) {
			args = new String[] { "-d ", javaFile.getAbsolutePath(), "-f", "xml", "-R", ruleSetFile, "-reportfile", pmdReportFileName };
		} else {
			throw new RuntimeException("지원되지않는 PMD 버젼입니다.");
		}

		File file = new File(pmdReportFileName);
		if (!file.exists()) {
			file.createNewFile();
		}

		exec.accept(args);
	}

}
