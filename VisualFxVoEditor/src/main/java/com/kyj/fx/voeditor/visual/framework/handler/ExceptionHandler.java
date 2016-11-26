/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2015. 10. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.handler;

/**
 * 
 * 예외처리로직 구현.
 * 
 * @author KYJ
 *
 */
@FunctionalInterface
public interface ExceptionHandler {

	/**
	 * 예외 발생시 처리할 내용
	 *
	 * @Date 2015. 10. 18.
	 * @param t
	 * @User KYJ
	 */
	public void handle(Exception t);

}
