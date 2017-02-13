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
public abstract class EncodingProxyListener implements ProxyListener<String> {

	private Charset encoding;

	public EncodingProxyListener(Charset encoding) {

		if(encoding == null)
			this.encoding = Charset.forName("UTF-8");
		else
			this.encoding = encoding;

	}

	public abstract void onAction(int seq, String str);

	@Override
	public String convert(byte[] bytes) {
		return new String(bytes, this.encoding);
	}

}
