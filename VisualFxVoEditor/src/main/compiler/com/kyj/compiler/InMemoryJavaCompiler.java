package com.kyj.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

/**
 * Created by trung on 5/3/15.
 */
public class InMemoryJavaCompiler {

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
