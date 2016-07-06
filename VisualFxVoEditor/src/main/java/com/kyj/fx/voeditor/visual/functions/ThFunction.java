/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.functions
 *	작성일   : 2016. 2. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.functions;

/**
 * 파라미터가 3개인 Function 인터페이스
 * 
 * @author KYJ
 *
 */
@FunctionalInterface
public interface ThFunction<T, U, V, R> {

	public R apply(T t, U u, V v);
}
