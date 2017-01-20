/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.proxy
 *	작성일   : 2017. 1. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.proxy;

import java.util.List;

/**
 * @author KYJ
 *
 */
public class ProxyEvent {

	private List<ProxyListener> onRequests;
	private byte[] bytes;

	/**
	 * @param onRequests
	 * @param bytes
	 */
	public ProxyEvent(List<ProxyListener> onRequests, byte[] bytes) {
		this.onRequests = onRequests;
		this.bytes = bytes;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 19.
	 */
	public void fire() {
		this.onRequests.forEach(listener -> {
			listener.onAction(listener.convert(bytes));
		});
	}

}
