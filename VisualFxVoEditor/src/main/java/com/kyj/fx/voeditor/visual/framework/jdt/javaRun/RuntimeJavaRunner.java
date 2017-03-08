/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.compiler
 *	작성일   : 2017. 3. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.jdt.javaRun;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import com.kyj.fx.voeditor.visual.util.RuntimeClassUtil;

/**
 * @author KYJ
 *
 */
public class RuntimeJavaRunner implements JavaRunnable {

	private File cp;
	private File classFile;

	private Charset encoding = Charset.defaultCharset();

	private OutputStream out = System.out;
	private OutputStream err = System.err;

	/**
	 * @param file
	 * @param value
	 */
	public RuntimeJavaRunner(File cp, File classFile) {
		this.cp = cp;
		this.classFile = classFile;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {

		try {

			String javaHome = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java.exe";

			String parent = this.cp.getAbsolutePath();
			String replace = this.classFile.getName().replace(".class", "");

			List<String> asList = Arrays.asList(javaHome, "-cp", parent, replace, "-encoding", encoding.displayName());

			RuntimeClassUtil.exe(asList, out, err);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
