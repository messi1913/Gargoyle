/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.thread
 *	작성일   : 2016. 11. 26.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.thread;

import java.util.function.Consumer;

import com.kyj.fx.voeditor.visual.framework.handler.ExceptionHandler;

/**
 * @author KYJ
 *
 */
public class DemonThreadFactory<R> {

	private CallableThreadFactory<R> defaultCallableThreadFactory;

	public DemonThreadFactory() {
		defaultCallableThreadFactory = new DefaultCallableThreadFactory<R>();
	}

	public DemonThreadFactory(CallableThreadFactory<R> factory) {
		defaultCallableThreadFactory = factory;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 3. 
	 * @return
	 */
	public static <R> DemonThreadFactory<R> newInstance() {
		return new DemonThreadFactory<R>();
	}

	/**
	 * 새로운 데몬 스레드 생성
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 26.
	 * @param run
	 * @return
	 * @return
	 */
	public Thread newThread(CloseableCallable<R> run, Consumer<R> onSuccess, ExceptionHandler onError) {
		Thread newThread = defaultCallableThreadFactory.newThread(run, onSuccess, onError);
		newThread.setDaemon(true);
		return newThread;
	}

}
