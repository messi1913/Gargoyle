/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.jdt
 *	작성일   : 2017. 3. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.jdt;

import com.kyj.fx.voeditor.visual.framework.jdt.compiler.JavaCompilerable;
import com.kyj.fx.voeditor.visual.framework.jdt.javaRun.RuntimeJavaRunner;

/**
 *
 * @author KYJ
 *
 */
public class DefaultJavaExecutor extends AbstractJavaExecutor {

	/**
	 * @param compiler
	 * @param runner
	 */
	public DefaultJavaExecutor(JavaCompilerable compiler, RuntimeJavaRunner runner) {
		super(compiler, runner);
	}

}
