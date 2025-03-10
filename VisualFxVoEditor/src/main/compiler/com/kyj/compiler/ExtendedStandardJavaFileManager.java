package com.kyj.compiler;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;

/**
 * Created by trung on 5/3/15.
 */
public class ExtendedStandardJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

	// private CompiledCode compiledCode;
	private DynamicClassLoader cl;

	/**
	 * Creates a new instance of ForwardingJavaFileManager.
	 *
	 * @param fileManager
	 *            delegate to this file manager
	 * @param cl
	 */
	protected ExtendedStandardJavaFileManager(JavaFileManager fileManager, CompiledCode compiledCode, DynamicClassLoader cl) {
		super(fileManager);
		// this.compiledCode = compiledCode;
		this.cl = cl;
		if (compiledCode != null)
			this.cl.addCode(compiledCode);
	}

	protected ExtendedStandardJavaFileManager(JavaFileManager fileManager, DynamicClassLoader cl) {
		super(fileManager);
		this.cl = cl;
		// this.cl.addCode(compiledCode);
	}

	@Override
	public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind,
			FileObject sibling) throws IOException {
		return this.cl.getCompiledCode(className);
	}

	@Override
	public ClassLoader getClassLoader(JavaFileManager.Location location) {
		return cl;
	}

	public void addCode(CompiledCode compiledCode) {
		this.cl.addCode(compiledCode);
	}
}
