/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.jdt.compiler
 *	작성일   : 2017. 3. 9.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.jdt.compiler;

import java.io.File;
import java.io.PrintStream;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;
import org.junit.Test;

/**
 * @author KYJ
 *
 */
public class AntJavaCompilerTest {

	/**
	 * ant의 build.xml 파일을 이용한 컴파일 테스트O
	 * @작성자 : KYJ
	 * @작성일 : 2017. 3. 7.
	 */
	@Test
	public void antBuildTest() {

		//		String buildFile = "C:\\Users\\KYJ\\eclipse\\jee-neon\\workspace\\Algorism\\build.xml";
		String buildFile = "C:\\Users\\KYJ\\.git\\Gargoyle\\VisualFxVoEditor\\build\\build.xml";

		AntJavaCompiler c = new AntJavaCompiler(new File(buildFile));
		c.setTarget("notifyBuildForVersion");

		c.parse();

		System.out.println("######################## ");
		System.out.println("target ");
		System.out.println("######################## ");
		c.getTargets().entrySet().forEach(ent -> {
			System.out.printf("  %s : %s \n", ent.getKey(), ent.getValue().getName());
		});

		c.run();

	}

	@Test
	public void runtimeAddingTarget() {
		Project p = new Project();
		p.init();
		p.addTarget(extracted(p));
		p.addBuildListener(extracted());
		p.executeTarget("Hello build");
	}

	private DefaultLogger extracted() {
		DefaultLogger defaultLogger = new DefaultLogger();
		defaultLogger.setMessageOutputLevel(Project.MSG_VERBOSE);
		defaultLogger.setOutputPrintStream(new PrintStream(System.out));
		return defaultLogger;
	}

	private Target extracted(Project p) {

		Target target = new Target() {
			//			@Override
			//			public Project getProject() {
			//				return p;
			//			}
		};
		target.setProject(p);

		target.setName("Hello build");
		target.setDescription("Runtime Adding Target");

		Echo echo = new Echo();
		echo.setMessage("Hello ant build");
		echo.setProject(p);
		target.addTask(echo);
		return target;
	}
}
