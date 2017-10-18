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
	private static final String DEAMON_THREAD_NAME = "[Gargoye-Excutor-Deamon-Thread]";

	/**
	 * 데몬스레드 그룹을 만드는 스레드 팩토리
	 * @최초생성일 2016. 10. 5.
	 */
	//	private static final ThreadFactory deamonThreadFactory = new GargoyleThreadFactory();

	/**
	 * 분석시 이름을 확인할 수 있는 구조로 수정.
	 * @author KYJ
	 *
	 */
	static final class GargoyleThreadFactory implements ThreadFactory {
		private String name;

		GargoyleThreadFactory(String name) {
			this.name = name;
		}

		/* (non-Javadoc)
		 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
		 */
		@Override
		public Thread newThread(Runnable r) {
			Thread t = Executors.defaultThreadFactory().newThread(r);
			t.setDaemon(true);
			t.setName(name);
			return t;
		}

	}

	public static ExecutorService newSingleThreadExecutor() {
		return newSingleThreadExecutor(DEAMON_THREAD_NAME);
	}

	public static ExecutorService newSingleThreadExecutor(String name) {
		return Executors.newSingleThreadExecutor(new GargoyleThreadFactory(name));
	}

	public static ExecutorService newFixedThreadExecutor(int count) {
		return newFixedThreadExecutor(DEAMON_THREAD_NAME, count);
	}

	public static ExecutorService newFixedThreadExecutor(String name, int count) {
		return Executors.newFixedThreadPool(count, new GargoyleThreadFactory(name));
	}

	private static ExecutorService gargoyleSystemExecutorService;

	static {
		gargoyleSystemExecutorService = newFixedThreadExecutor(2);
	}

	public static ExecutorService getGargoyleSystemExecutorSerivce() {
		return gargoyleSystemExecutorService;
	}
}
