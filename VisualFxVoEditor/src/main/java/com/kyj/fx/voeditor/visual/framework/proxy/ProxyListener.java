/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.proxy
 *	작성일   : 2017. 1. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.proxy;

/**
 * @author KYJ
 *
 */
public interface ProxyListener<T> {

	public void onAction(T t);

	public T convert(byte[] bytes);

}
