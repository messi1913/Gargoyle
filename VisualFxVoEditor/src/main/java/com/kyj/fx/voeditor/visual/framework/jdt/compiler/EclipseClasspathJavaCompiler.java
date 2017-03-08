/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.compiler
 *	작성일   : 2017. 3. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.jdt.compiler;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.core.compiler.batch.BatchCompiler;

/**
 * @author KYJ
 *
 */
public class EclipseClasspathJavaCompiler implements JavaCompilerable {

	private File classpath, src, dst;
	private Charset encoding = Charset.defaultCharset();
	private String compiledVersion = "1.8";
	private boolean debuging = true;
	private boolean nowarn = true;

	/**
	 * 컴파일이 되었는지 여부를 리턴
	 * @최초생성일 2017. 3. 7.
	 */
	private boolean compiled;

	private OutputStream out = System.out;
	private OutputStream err = System.err;;
	private File[] classpaths;

	/**
	 * @param value
	 */
	public EclipseClasspathJavaCompiler(File classpath) {
		this.classpath = classpath;
	}


	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public final void run() {
		String command = getCommand();
		CompilationProgress progress = getProgress(); // instantiate your subclass
		compiled = BatchCompiler.compile(command, new PrintWriter(out), new PrintWriter(err), progress);

	}

	protected CompilationProgress getProgress() {
		return null;
	}

	protected String getCommand() {
		StringBuilder sb = new StringBuilder();
		sb.append("-classpath rt.jar;");
		if (classpaths != null) {
			for (File classpath : classpaths) {
				sb.append(classpath.getAbsolutePath()).append(";");
			}
		}
		sb.append(" ");
		sb.append(src.getAbsolutePath()).append(" ");
		sb.append("-d ").append(dst.getAbsolutePath()).append(" ");
		if (debuging)
			sb.append("-verbose ");
		if (nowarn)
			sb.append(" -nowarn ");

		sb.append("-encoding").append(" ").append(encoding.displayName()).append(" ");
		sb.append("-").append(compiledVersion);
		return sb.toString();
	}

	/**
	 * @return the encoding
	 */
	public final Charset getEncoding() {
		return encoding;
	}

	/**
	 * @param encoding the encoding to set
	 */
	public final void setEncoding(Charset encoding) {
		this.encoding = encoding;
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

	/**
	 * @return the src
	 */
	public File getSrc() {
		return src;
	}

	/**
	 * @return the dst
	 */
	public File getDst() {
		return dst;
	}

	/**
	 * @return the compiledVersion
	 */
	public final String getCompiledVersion() {
		return compiledVersion;
	}

	/**
	 * @return the debuging
	 */
	public final boolean isDebuging() {
		return debuging;
	}

	/**
	 * @return the nowarn
	 */
	public final boolean isNowarn() {
		return nowarn;
	}

	/**
	 * @param compiledVersion the compiledVersion to set
	 */
	public final void setCompiledVersion(String compiledVersion) {
		this.compiledVersion = compiledVersion;
	}

	/**
	 * @param debuging the debuging to set
	 */
	public final void setDebuging(boolean debuging) {
		this.debuging = debuging;
	}

	/**
	 * @param nowarn the nowarn to set
	 */
	public final void setNowarn(boolean nowarn) {
		this.nowarn = nowarn;
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.framework.jdt.compiler.JavaCompilerable#wasCompiled()
	 */
	@Override
	public boolean wasCompiled() {
		return compiled;
	}

}
