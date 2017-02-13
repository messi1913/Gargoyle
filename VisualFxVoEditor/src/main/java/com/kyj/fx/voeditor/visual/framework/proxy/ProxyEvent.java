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
@SuppressWarnings("rawtypes")
public class ProxyEvent {

	private int seq;
	private List<ProxyListener> onRequests;
	private byte[] bytes;

	/**
	 * @param onRequests
	 * @param bytes
	 */
	public ProxyEvent(int seq, List<ProxyListener> onRequests, byte[] bytes) {
		this.onRequests = onRequests;
		this.bytes = bytes;
		this.seq = seq;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 19.
	 */
	@SuppressWarnings("unchecked")
	public void fire() {
		this.onRequests.forEach(listener -> {
			listener.onAction( seq, listener.convert(bytes));
		});
	}

}
