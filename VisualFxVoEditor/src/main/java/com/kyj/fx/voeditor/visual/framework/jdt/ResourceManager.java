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
 *  소스파일과 컴파일된 파일에 대한 연결되 리소스에 대한 메타데이터를
 *  제공하기 위한 클래스
 * @author KYJ
 *
 */
class ResourceManager {

	private JavaCompilerable compiler;
	private RuntimeJavaRunner runner;

	/**
	 * @param compiler
	 * @param runner
	 */
	public ResourceManager(JavaCompilerable compiler, RuntimeJavaRunner runner) {
		this.compiler = compiler;
		this.runner = runner;
	}

}
