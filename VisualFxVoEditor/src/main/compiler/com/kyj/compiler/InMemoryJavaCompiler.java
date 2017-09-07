package com.kyj.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

/**
 * Created by trung on 5/3/15.
 */
public class InMemoryJavaCompiler {

	public static class Code {
		private String className;
		private String sourceCode;

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public String getSourceCode() {
			return sourceCode;
		}

		public void setSourceCode(String sourceCode) {
			this.sourceCode = sourceCode;
		}

	}

	public static Class<?> compile(String mainClass, Code... codes) throws Exception {
		JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
		DynamicClassLoader cl = new DynamicClassLoader(ClassLoader.getSystemClassLoader());

		ExtendedStandardJavaFileManager fileManager = new ExtendedStandardJavaFileManager(javac.getStandardFileManager(null, null, null),
				null, cl);

		JavaFileObject[] sCodes = new JavaFileObject[codes.length];
		// List<CompiledCode> cCodes = new ArrayList<>();
		for (int i = 0; i < codes.length; i++) {
			SourceCode sourceCode = new SourceCode(codes[i].getClassName(), codes[i].getSourceCode());
			CompiledCode compiledCode = new CompiledCode(codes[i].getClassName());
			sCodes[i] = sourceCode;
			fileManager.addCode(compiledCode);
		}

		Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(sCodes);

		CompilationTask task = javac.getTask(null, fileManager, null, null, null, compilationUnits);
		task.call();
		return cl.loadClass(mainClass);
	}

	public static Class<?> compile(String className, String sourceCodeInText) throws Exception {
		JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
		DynamicClassLoader cl = new DynamicClassLoader(ClassLoader.getSystemClassLoader());
		SourceCode sourceCode = new SourceCode(className, sourceCodeInText);
		CompiledCode compiledCode = new CompiledCode(className);
		Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(sourceCode);

		ExtendedStandardJavaFileManager fileManager = new ExtendedStandardJavaFileManager(javac.getStandardFileManager(null, null, null),
				compiledCode, cl);

		JavaCompiler.CompilationTask task = javac.getTask(null, fileManager, null, null, null, compilationUnits);
		task.call();
		return cl.loadClass(className);
	}

	public static Class<?> compile(List<SourceCode> codeList, String className) throws Exception {
		JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
		DynamicClassLoader cl = new DynamicClassLoader(ClassLoader.getSystemClassLoader());
		ExtendedStandardJavaFileManager fileManager = new ExtendedStandardJavaFileManager(javac.getStandardFileManager(null, null, null),
				cl);

		for (SourceCode sourceCode : codeList) {
			// SourceCode sourceCode = new SourceCode(className,
			// sourceCodeInText);
			CompiledCode compiledCode = new CompiledCode(sourceCode.getClassName());
			fileManager.addCode(compiledCode);
		}

		Iterable<? extends JavaFileObject> compilationUnits = codeList;// Arrays.asList(sourceCode);

		JavaCompiler.CompilationTask task = javac.getTask(null, fileManager, null, getDefaultOption(), null, compilationUnits);
		boolean result = task.call();
		return cl.loadClass(className);
	}

	public static List<String> getDefaultOption() {
		List<String> optionList = new ArrayList<String>();
		// set compiler's classpath to be same as the runtime's
		optionList.addAll(Arrays.asList("-classpath", System.getProperty("java.class.path") /* + ";" + System.getProperty("java.home") */));

		optionList.stream().flatMap(m -> Stream.of(m.split(";"))).forEach(System.out::println);
		return optionList;
	}
	// public static Class<?> compile(SourceCode[] codes) throws Exception {
	//
	// for (SourceCode sourceCode : codes) {
	//
	// }
	//
	// SourceCode sourceCode = new SourceCode(className, sourceCodeInText);
	// CompiledCode compiledCode = new CompiledCode(className);
	//
	// Iterable<? extends JavaFileObject> compilationUnits =
	// Arrays.asList(sourceCode);
	//
	// ExtendedStandardJavaFileManager fileManager = new
	// ExtendedStandardJavaFileManager(javac.getStandardFileManager(null, null,
	// null),
	// compiledCode, cl);
	// JavaCompiler.CompilationTask task = javac.getTask(null, fileManager,
	// null, null, null, compilationUnits);
	// boolean result = task.call();
	// return cl.loadClass(className);
	// }
}
