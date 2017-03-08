/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.jdt
 *	작성일   : 2017. 3. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.jdt;

import com.kyj.fx.voeditor.visual.framework.jdt.compiler.JavaCompilerable;
import com.kyj.fx.voeditor.visual.framework.jdt.javaRun.RuntimeJavaRunner;

/**
 * 비동기형태로 컴파일 및 실행처리를 지원한다.
 *
 * @author KYJ
 *
 */
public class AsynchJavaExecutor extends AbstractJavaExecutor {

	private AsychTrhead deligator;
	/**
	 * @param compiler
	 * @param runner
	 */
	public AsynchJavaExecutor(JavaCompilerable compiler, RuntimeJavaRunner runner) {
		super(compiler, runner);

		deligator = new AsychTrhead(this.compiler, () -> {
			this.runner.run();
		});

	}

	public void execute() {
		deligator.start();
	}

	interface Action {
		public void action();
	}

	class AsychTrhead extends Thread {

		private Action action;

		/**
		 * @param compiler
		 */
		public AsychTrhead(Runnable r, Action action) {
			super(r);
			this.action = action;
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			super.run();
			if (action != null)
				action.action();
		}

	}

}
