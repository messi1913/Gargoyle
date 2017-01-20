/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.proxy
 *	작성일   : 2017. 1. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.proxy;

import java.nio.charset.Charset;

/**
 * @author KYJ
 *
 */
public interface UTF8EncodingProxyListener extends ProxyListener<String> {

	static final Charset FOR_NAME = Charset.forName("UTF-8");

	public void onAction(String str);

	@Override
	public default String convert(byte[] bytes) {
		return new String(bytes, FOR_NAME);
	}

}
