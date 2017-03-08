/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.jdt.compiler
 *	작성일   : 2017. 3. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.jdt.compiler;

/**
 * @author KYJ
 *
 */
public interface JavaCompilerable extends Runnable {

	/**
	 * 컴파일이 되었는지 여부
	 * @작성자 : KYJ
	 * @작성일 : 2017. 3. 7.
	 * @return
	 */
	default boolean wasCompiled() {
		throw new RuntimeException("Not Yet impl.");
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	default void run() {
		throw new RuntimeException("Not Yet impl.");
	}

}
