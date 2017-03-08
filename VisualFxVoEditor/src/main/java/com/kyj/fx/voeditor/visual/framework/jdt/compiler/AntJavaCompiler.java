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

import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.listener.TimestampedLogger;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * @author KYJ
 *
 */
public class AntJavaCompiler implements JavaCompilerable {

	private File baseDir, buildFile;
	private Charset encoding = Charset.defaultCharset();

	/**
	 * 컴파일이 되었는지 여부를 리턴
	 * @최초생성일 2017. 3. 7.
	 */
	private boolean compiled;
	private boolean occurError;
	private Exception ex;

	private OutputStream out = System.out;
	private OutputStream err = System.err;
	private Project p;
	private ProjectHelper helper;
	/**
	 * 사용할 build.xml target명
	 * @최초생성일 2017. 3. 7.
	 */
	private String target;

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
		this.target = target;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 3. 7.
	 */
	private void init() {

		p = new Project();
		p.setUserProperty("ant.file", buildFile.getAbsolutePath());
		p.setUserProperty("-encoding", encoding.displayName());
		p.init();
		helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);

		if (baseDir != null && baseDir.exists())
			p.setBaseDir(baseDir);
		else
			p.setBaseDir(buildFile.getParentFile());

		//setting console
		DefaultLogger consoleLogger = new TimestampedLogger();

		consoleLogger.setOutputPrintStream(new PrintStream(out));
		consoleLogger.setErrorPrintStream(new PrintStream(err));

		consoleLogger.setMessageOutputLevel(Project.MSG_VERBOSE);
		p.addBuildListener(consoleLogger);

		//parse build.xml
		helper.parse(p, buildFile);

		System.out.println("base dir : " + p.getBaseDir());
		System.out.println("default target : " + p.getDefaultTarget());

		//append build debugger.
		BuildListener buildListener = getBuildListener();
		if (buildListener != null)
			p.addBuildListener(buildListener);

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

	private void write(String str) {
		write(out, str);
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public final void run() {
		init();

		try {
			if (ValueUtil.isEmpty(target))
				p.executeTarget(p.getDefaultTarget());
			else
				p.executeTarget(target);
		} catch (Exception e) {
			compiled = false;
			occurError = true;
			this.ex = e;
			write(err, ValueUtil.toString(e));
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

}
