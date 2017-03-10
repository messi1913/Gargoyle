/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.compiler
 *	작성일   : 2017. 3. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.jdt.compiler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.listener.TimestampedLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.sun.star.uno.RuntimeException;

/**
 *
 * Ant기반으로 build.xml을 파싱하고 기술된 내용대로
 * 파싱처리를 하는 코어를 사용하기 쉽게 wapper 처리함
 * @author KYJ
 *
 */
public class AntJavaCompiler implements JavaCompilerable {

	private static final Logger LOGGER = LoggerFactory.getLogger(AntJavaCompiler.class);

	private File baseDir, buildFile;
	private Charset encoding = Charset.defaultCharset();

	/**
	 * 컴파일이 되었는지 여부를 리턴
	 * @최초생성일 2017. 3. 7.
	 */
	private boolean compiled;
	private boolean occurError;
	private Exception ex;

	//표준 출력
	private OutputStream out = System.out;
	//표준 에러 출력
	private OutputStream err = System.err;
	private Project p;
	private ProjectHelper helper;
	//build.xml 파일이 파싱되어 빌드처리 준비가 되었는지 확인
	private boolean wasParse;
	/**
	 * 사용할 build.xml target명
	 * @최초생성일 2017. 3. 7.
	 */
//	private String target;
	private Vector<String> targets = new Vector<>();

	public AntJavaCompiler(File buildFile) {
		this(null, buildFile);
	}

	/**
	 * @param value
	 */
	public AntJavaCompiler(File baseDir, File buildFile) {
		this.baseDir = baseDir;
		this.buildFile = buildFile;

	}

	public void setTarget(String target) {
		setTargets(target);
	}

	public void setTargets(String ... targets) {
		this.targets.clear();
		this.targets.addAll(Arrays.asList(targets));
	}

	/**
	 *
	 * build.xml파일을 파싱처리하여
	 * 빌드처리 준비상태로 처리한다.
	 * 이후 run 함수를 호출하여 빌드를 실행할 수 있다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2017. 3. 7.
	 */
	public void parse() {

		p = new Project();
		p.setUserProperty("ant.file", buildFile.getAbsolutePath());
		p.setUserProperty("encoding", encoding.displayName());

		p.init();
		helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);

		if (baseDir != null && baseDir.exists())
			p.setBaseDir(baseDir);
		else
			p.setBaseDir(buildFile.getParentFile());

		//setting console
		DefaultLogger consoleLogger = getLogger();

		consoleLogger.setOutputPrintStream(new PrintStream(out));
		consoleLogger.setErrorPrintStream(new PrintStream(err));

		consoleLogger.setMessageOutputLevel(Project.MSG_VERBOSE);
		p.addBuildListener(consoleLogger);

		//parse build.xml
		helper.parse(p, buildFile);

		LOGGER.debug("  ##### base dir : " + p.getBaseDir());
		LOGGER.debug("  ##### default target : " + p.getDefaultTarget());

		//append build debugger.
		BuildListener buildListener = getBuildListener();
		if (buildListener != null)
			p.addBuildListener(buildListener);

		wasParse = true;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 3. 9.
	 * @return
	 */
	private DefaultLogger getLogger() {
		return new TimestampedLogger();
	}

	/**
	 * 프로젝트명을 리턴
	 * @작성자 : KYJ
	 * @작성일 : 2017. 3. 10.
	 * @return
	 */
	public String getProjectName() {
		return p.getName();
	}

	/**
	 * build.xml에 기술된 target 목록을 리턴한다.
	 * @작성자 : KYJ
	 * @작성일 : 2017. 3. 9.
	 * @return
	 */
	public Hashtable<String, Target> getTargets() {
		validateParse();

		return p.getTargets();
	}

	/**
	 * build.,xml에 기술된 디폴트 target을 리턴한다.
	 * @작성자 : KYJ
	 * @작성일 : 2017. 3. 9.
	 * @return
	 */
	public String getDefaultTarget() {
		validateParse();
		return p.getDefaultTarget();
	}

	private void validateParse() {
		if (p == null)
			throw new RuntimeException("was not parse.");
	}

	protected BuildListener getBuildListener() {
		return null;
		/* debug.
		return new BuildListener() {

			@Override
			public void taskStarted(BuildEvent arg0) {
			}

			@Override
			public void taskFinished(BuildEvent arg0) {
			}

			@Override
			public void targetStarted(BuildEvent arg0) {
				write(String.format("### Target : %s [%s]\n", arg0.getTarget().getName(), "started"));
			}

			@Override
			public void targetFinished(BuildEvent arg0) {
				write(String.format("### Target : %s [%s]\n", arg0.getTarget().getName(), "finished"));
			}

			@Override
			public void messageLogged(BuildEvent arg0) {
			}

			@Override
			public void buildStarted(BuildEvent arg0) {
				write("build start\n");
			}

			@Override
			public void buildFinished(BuildEvent arg0) {
				write("build finished\n");
				compiled = true;
			}
		};
		*/
	}

	private void write(OutputStream out, String str) {
		if (str != null) {
			try {
				if (out != null)
					out.write(str.getBytes());
			} catch (IOException e) {
				/*Nothing*/}
		}

	}

	@SuppressWarnings("unused")
	private void write(String str) {
		write(out, str);
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public final void run() {

		if (!wasParse)
			throw new RuntimeException("was not parse..");

		try {
			parse();
			if (ValueUtil.isEmpty(targets))
				p.executeTarget(p.getDefaultTarget());
			else
			{
				p.executeTargets( targets);
			}
		} catch (Exception e) {
			compiled = false;
			occurError = true;
			this.ex = e;
			write(err, ValueUtil.toString(e));
		} finally {
			//			wasParse = false;
		}

	}

	/**
	 * @return the encoding
	 */
	public final Charset getEncoding() {
		return encoding;
	}

	/**
	 * @return the compiled
	 */
	public final boolean isCompiled() {
		return compiled;
	}

	/**
	 * @return the occurError
	 */
	public final boolean isOccurError() {
		return occurError;
	}

	/**
	 * @return the ex
	 */
	public final Exception getEx() {
		return ex;
	}

	/**
	 * @return the out
	 */
	public final OutputStream getOut() {
		return out;
	}

	/**
	 * @return the err
	 */
	public final OutputStream getErr() {
		return err;
	}

	/**
	 * @param encoding the encoding to set
	 */
	public final void setEncoding(Charset encoding) {
		this.encoding = encoding;
	}

	/**
	 * @param out the out to set
	 */
	public final void setOut(OutputStream out) {
		this.out = out;
	}

	/**
	 * @param err the err to set
	 */
	public final void setErr(OutputStream err) {
		this.err = err;
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.framework.jdt.compiler.JavaCompilerable#wasCompiled()
	 */
	@Override
	public boolean wasCompiled() {
		return compiled;
	}

	/**
	 * @return the buildFile
	 */
	public final File getBuildFile() {
		return buildFile;
	}

	/**
	 * @param buildFile the buildFile to set
	 */
	public final void setBuildFile(File buildFile) {
		this.buildFile = buildFile;
	}

}
