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
public abstract class UTF8EncodingProxyListener extends EncodingProxyListener {

	public UTF8EncodingProxyListener() {
		super(Charset.forName("UTF-8"));
	}

}
