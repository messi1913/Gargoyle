/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework
 *	작성일   : 2016. 6. 29.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework;

/***************************
 * 
 * 영역처리
 * 
 * @author KYJ
 *
 ***************************/
public interface Scope<T> {

	/********************************
	 * 작성일 : 2016. 6. 29. 작성자 : KYJ
	 *
	 * 영역처리.
	 ********************************/
	public void doScope(T t);

}
