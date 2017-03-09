/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.jdt
 *	작성일   : 2017. 3. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.jdt;

import java.io.File;

import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.junit.Test;

import com.kyj.fx.voeditor.visual.framework.jdt.compiler.AntJavaCompiler;
import com.kyj.fx.voeditor.visual.framework.jdt.compiler.EclipseJavaCompiler;
import com.kyj.fx.voeditor.visual.framework.jdt.compiler.JavaCompilerable;
import com.kyj.fx.voeditor.visual.framework.jdt.javaRun.RuntimeJavaRunner;

/**
 * @author KYJ
 *
 */
public class DefaultJavaExecutorTest {



	/**
	 * src 디렉토리를 이용한 컴파일 테스트
	 *
	 *  + 비동기 처리 테스트
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2017. 3. 7.
	 * @throws InterruptedException
	 */
	@Test
	public final void srcDirBuildTest() throws InterruptedException {
		String _srcDir = "C:\\Users\\KYJ\\eclipse\\jee-neon\\workspace\\Algorism";


		String _srcSimpleName = "OriginalA";
		String _srcJavaFile = _srcSimpleName + ".java";
		String _compiledFile = _srcSimpleName + ".class";

		//		String _src = _srcDir + _srcJavaFile;
		String _dst = "c:\\ttt\\";

		File[] classpath = new File[] {

				new File("C:\\Users\\KYJ\\eclipse\\jee-neon\\workspace\\Algorism\\ojdbc14.jar")

		};

		//		System.err.println(JavaCompilerable.class.getProtectionDomain().getCodeSource().getLocation());
		JavaCompilerable c = new EclipseJavaCompiler(new File(_srcDir), new File(_dst), classpath) {

			/* (non-Javadoc)
			 * @see com.kyj.fx.voeditor.visual.framework.jdt.compiler.EclipseJavaCompiler#getProgress()
			 */
			@Override
			protected CompilationProgress getProgress() {
				return new CompilationProgress() {

					@Override
					public void begin(int remainingWork) {
						System.out.println("## Job Remaining Count : " + remainingWork);
					}

					@Override
					public void done() {
						System.out.println("## Complted...");
					}

					@Override
					public boolean isCanceled() {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public void setTaskName(String name) {
						System.out.println("## TaskName :  " + name);
					}

					@Override
					public void worked(int workIncrement, int remainingWork) {
						System.out.println("## working workedCount : " + workIncrement + " remainingWork : " + remainingWork);

					}

				};

			}

		};
		c.run();
		RuntimeJavaRunner r = new RuntimeJavaRunner(new File(_dst), new File(_compiledFile));
		AbstractJavaExecutor defaultJavaExecutor = new AsynchJavaExecutor(c, r) {

		};
		defaultJavaExecutor.execute();

		Thread.sleep(5000);
	}

	/**
	 * 소스파일에 참고라이브러리를 포함시켜 컴파일하는 테스트
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2017. 3. 7.
	 * @throws InterruptedException
	 */
	@Test
	public void referencesPackageCompileTest() throws InterruptedException {

		String _srcDir = "C:\\Users\\KYJ\\eclipse\\jee-neon\\workspace\\Algorism\\src\\";
		String _srcSimpleName = "OriginalA";
		String _srcJavaFile = _srcSimpleName + ".java";
		String _compiledFile = _srcSimpleName + ".class";
		String _src = _srcDir + _srcJavaFile;
		String _dst = "c:\\ttt\\";

		File[] classpath = new File[] {

				new File("C:\\Users\\KYJ\\eclipse\\jee-neon\\workspace\\Algorism\\ojdbc14.jar")

		};

		JavaCompilerable c = new EclipseJavaCompiler(new File(_src), new File(_dst), classpath) {

			/* (non-Javadoc)
			 * @see com.kyj.fx.voeditor.visual.framework.jdt.compiler.EclipseJavaCompiler#getProgress()
			 */
			@Override
			protected CompilationProgress getProgress() {
				return new CompilationProgress() {

					@Override
					public void begin(int remainingWork) {
						System.out.println("## Job Remaining Count : " + remainingWork);
					}

					@Override
					public void done() {
						System.out.println("## Complted...");
					}

					@Override
					public boolean isCanceled() {
						return false;
					}

					@Override
					public void setTaskName(String name) {
						System.out.println("## TaskName :  " + name);
					}

					@Override
					public void worked(int workIncrement, int remainingWork) {
						System.out.println("## working workedCount : " + workIncrement);

					}

				};

			}

		};

		//		c.run();
		//		System.out.println(c.wasCompiled());
		RuntimeJavaRunner r = new RuntimeJavaRunner(new File(_dst), new File(_compiledFile));
		AbstractJavaExecutor defaultJavaExecutor = new AsynchJavaExecutor(c, r) {

		};
		defaultJavaExecutor.execute();

		Thread.sleep(10000);
	}
}
