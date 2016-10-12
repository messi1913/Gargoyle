/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.thread
 *	작성일   : 2016. 10. 5.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 데몬스레드를 생성
 *
 * @author KYJ
 *
 */
public class ExecutorDemons {

	/**
	 * @최초생성일 2016. 10. 12.
	 */
	private static final String DEAMON_THREAD_NAME = "Gargoye-Excutor-Deamon-Thread";

	/**
	 * 데몬스레드 그룹을 만드는 스레드 팩토리
	 * @최초생성일 2016. 10. 5.
	 */
	private static final ThreadFactory deamonThreadFactory = r -> {
		Thread t = Executors.defaultThreadFactory().newThread(r);
		t.setDaemon(true);
		t.setName(DEAMON_THREAD_NAME);
		return t;
	};

	public static ExecutorService newSingleThreadExecutor() {
		return Executors.newSingleThreadExecutor(deamonThreadFactory);
	}
}
