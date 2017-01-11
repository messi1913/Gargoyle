/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 12. 27.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

/**
 * @author KYJ
 *
 */
public class ThreadUtil {

	/**
	 * 스레드 그룹.
	 * @최초생성일 2016. 12. 27.
	 */
	private static ThreadGroup threadGroup ;

	static {
		threadGroup = new ThreadGroup("Gargoyle-ThreadUtil");
		threadGroup.setDaemon(true);
	}

	public static void createNewThreadAndRun(Runnable r) {
		createNewThread("Gragoyle-ThreadUtil-Default", r).start();
	}

	public static void createNewThreadAndRun(String name, Runnable r) {
		createNewThread(name, r).start();
	}

	public static Thread createNewThread(String name, Runnable r) {
		Thread thread = new Thread(threadGroup, r, name);
		return thread;
	}

	public static void getcURRENT() {

	}

}
