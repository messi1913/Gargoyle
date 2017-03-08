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
 * @author KYJ
 *
 */
public abstract class AbstractJavaExecutor {

	protected JavaCompilerable compiler;
	protected RuntimeJavaRunner runner;
	private ResourceManager manager;

	public AbstractJavaExecutor(JavaCompilerable compiler, RuntimeJavaRunner runner) {
		manager = new ResourceManager(compiler, runner);
		this.compiler = compiler;
		this.runner = runner;
	}

	/**
	 * 컴파일이 되었는지 여부
	 * @작성자 : KYJ
	 * @작성일 : 2017. 3. 7.
	 * @return
	 */
	public boolean wasCompiled() {
		return this.compiler.wasCompiled();
	}

	public void execute() {
		this.compiler.run();
		this.runner.run();
	}
}
