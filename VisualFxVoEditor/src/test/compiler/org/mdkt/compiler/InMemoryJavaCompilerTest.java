package org.mdkt.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.Assert;
import org.junit.Test;

import com.kyj.compiler.CompiledCode;
import com.kyj.compiler.DynamicClassLoader;
import com.kyj.compiler.InMemoryJavaCompiler;
import com.kyj.compiler.InMemoryJavaCompiler.Code;
import com.kyj.compiler.SourceCode;
import com.kyj.fx.voeditor.visual.util.FileUtil;

import javafx.scene.layout.HBox;

/**
 * Created by trung on 5/3/15.
 */
public class InMemoryJavaCompilerTest {

	@Test
	public void simple() throws Exception {
		// StringBuffer sourceCode = new StringBuffer();
		//
		// sourceCode.append("package org.mdkt;\n");
		// sourceCode.append("public class HelloClass {\n");
		// sourceCode.append(" public String hello() { return \"hello\"; }");
		// sourceCode.append("}");

		File file = new File("C:\\Users\\KYJ\\git\\GargoylePlugins\\gargoyle-soap\\gen\\com\\samsungbiologics\\TEST");
		File[] listFiles = file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".java");
			}
		});
		Code[] array = Stream.of(listFiles).map(f -> {
			InMemoryJavaCompiler.Code code = new InMemoryJavaCompiler.Code();
			code.setClassName(file.getName().replace(".java", ""));
			try {
				code.setSourceCode(FileUtil.readToString(new FileInputStream(f)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return code;
		}).toArray(InMemoryJavaCompiler.Code[]::new);

		Class<?> helloClass = InMemoryJavaCompiler.compile("com.samsungbiologics.TEST.MbrInfomationTransferApp_SOBindingImpl", array);

		Method declaredMethod = helloClass.getDeclaredMethod("mbrInfomationTransferApp_SO");
		Object invoke = declaredMethod.invoke(helloClass.newInstance());
		System.out.println(invoke);
	}

	@Test
	public void simple2() throws Exception {
		Class helloClass = ExampleRef.class;

		Method method = helloClass.getDeclaredMethod("main", String[].class);
		method.setAccessible(true);

		Stream.of(helloClass.getDeclaredMethods()).forEach(System.out::println);

		new Thread(() -> {
			try {
				method.invoke(helloClass, Array.newInstance(String.class, 1));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
		;
		Thread.currentThread().join(5000);
	}

	@Test
	public void simple3() throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("package org.mdkt.exam;\n");
		sb.append("public class ExampleParent {");
		sb.append("");
		sb.append("}");

		StringBuffer sb2 = new StringBuffer();
		sb2.append("package org.mdkt.exam;\n");
		sb2.append("\n");
		sb2.append("/**\n");
		sb2.append(" * @author KYJ\n");
		sb2.append(" *\n");
		sb2.append(" */\n");
		sb2.append("\n");
		sb2.append("\n");
		sb2.append("public class ExampleRef extends ExampleParent {\n");
		sb2.append("\n");
		sb2.append("	public void hello() {\n");
		sb2.append("		System.out.println(\"ExampleRef www hello\");\n");
		sb2.append("	}\n");
		sb2.append("\n");
		sb2.append("	public static void main(String[] args) {\n");
		sb2.append("		new ExampleRef().hello();\n");
		sb2.append("	}\n");
		sb2.append("}\n");
		sb2.toString();

		SourceCode sourceCode = new SourceCode("org.mdkt.exam.ExampleParent", sb.toString());
		SourceCode sourceCode2 = new SourceCode("org.mdkt.exam.ExampleRef", sb2.toString());

		// InMemoryJavaCompiler.compile("org.mdkt.exam.ExampleParent",
		// sb.toString());
		Class<?> helloClass = InMemoryJavaCompiler.compile(Arrays.asList(sourceCode, sourceCode2), "org.mdkt.exam.ExampleRef");
		Method method = helloClass.getDeclaredMethod("main", String[].class);

		Class<?>[] classes = helloClass.getClasses();
		Stream.of(classes).forEach(System.out::println);

		method.setAccessible(true);

		Object newInstance = helloClass.newInstance();
		System.out.println(newInstance);
		// try {
		// method.invoke(helloClass);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		try {
			method.invoke(helloClass, Array.newInstance(String.class, 1));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void compile_whenTypical() throws Exception {
		// Simple
		{
			StringBuffer sourceCode = new StringBuffer();

			sourceCode.append("package org.mdkt;\n");
			sourceCode.append("public class HelloClass {\n");
			sourceCode.append("   public String hello() { return \"hello\"; }");
			sourceCode.append("}");

			Class<?> helloClass = InMemoryJavaCompiler.compile("org.mdkt.HelloClass", sourceCode.toString());

			Assert.assertNotNull(helloClass);
			Assert.assertEquals(1, helloClass.getDeclaredMethods().length);
		}

		{
			StringBuffer sb = new StringBuffer();
			sb.append("/********************************\n");
			sb.append(" *	프로젝트 : VisualFxVoEditor\n");
			sb.append(" *	패키지   : pak\n");
			sb.append(" *	작성일   : 2016. 4. 22.\n");
			sb.append(" *	작성자   : KYJ\n");
			sb.append(" *******************************/\n");
			sb.append("package pak;\n");
			sb.append("\n");
			sb.append("\n");
			sb.append("/**\n");
			sb.append(" * @author KYJ\n");
			sb.append(" *\n");
			sb.append(" */\n");
			sb.append("public class ExampleRef  {\n");
			sb.append("\n");
			sb.append("	public void hello() {\n");
			sb.append("		System.out.println(\"ExampleRef www hello\");\n");
			sb.append("	}\n");
			sb.append("}\n");
			sb.toString();

			StringBuffer sb2 = new StringBuffer();
			sb2.append("package pak;\n");
			sb2.append("\n");
			sb2.append("/**\n");
			sb2.append(" * @author KYJ\n");
			sb2.append(" *\n");
			sb2.append(" */\n");
			sb2.append("public class ExampleMain {\n");
			sb2.append("	public static void main(String[] args) {\n");
			sb2.append("		new ExampleRef().hello();\n");
			sb2.append("	}\n");
			sb2.append("}\n");

			// StringBuffer sb3 = new StringBuffer();
			// sb3.append("/********************************\n");
			// sb3.append(" * 프로젝트 : VisualFxVoEditor\n");
			// sb3.append(" * 패키지 : pak\n");
			// sb3.append(" * 작성일 : 2016. 4. 22.\n");
			// sb3.append(" * 작성자 : KYJ\n");
			// sb3.append(" *******************************/\n");
			// sb3.append("package pak;\n");
			// sb3.append("\n");
			// sb3.append("import javafx.application.Application;\n");
			// sb3.append("import javafx.scene.Scene;\n");
			// sb3.append("import javafx.scene.layout.BorderPane;\n");
			// sb3.append("import javafx.scene.text.Text;\n");
			// sb3.append("import javafx.stage.Stage;\n");
			// sb3.append("\n");
			// sb3.append("/**\n");
			// sb3.append(" * @author KYJ\n");
			// sb3.append(" *\n");
			// sb3.append(" */\n");
			// sb3.append("public class ExampleRef2 extends Application {\n");
			// sb3.append("\n");
			// sb3.append(" public void hello() {\n");
			// sb3.append(" System.out.println(\"ExampleRef www hello\");\n");
			// sb3.append(" }\n");
			// sb3.append("\n");
			// sb3.append(" @Override\n");
			// sb3.append(" public void start(Stage primaryStage) throws
			// Exception {\n");
			// sb3.append(" primaryStage.setScene(new Scene(new BorderPane(new
			// Text(\"hello world!\"))));\n");
			// sb3.append(" primaryStage.show();\n");
			// sb3.append(" }\n");
			// sb3.append("\n");
			// sb3.append(" public static void main(String[] args) {\n");
			// sb3.append(" launch(args);\n");
			// sb3.append(" }\n");
			// sb3.append("}\n");
			// sb3.toString();

			List<SourceCode> asList = new ArrayList<>();

			asList.add(new SourceCode("pak.ExampleRef", sb.toString()));
			asList.add(new SourceCode("pak.ExampleMain", sb2.toString()));
			// asList.add(new SourceCode("pak.ExampleRef2", sb3.toString()));

			Class<?> helloClass = InMemoryJavaCompiler.compile(asList, "pak.ExampleMain");

			// Assert.assertNotNull(helloClass);
			// Assert.assertEquals(1, helloClass.getDeclaredMethods().length);

			// Class<?> helloClass =
			// InMemoryJavaCompiler.compile("pak.ExampleMain", sb.toString());

			Method method = helloClass.getDeclaredMethod("main", String[].class);
			method.setAccessible(true);

			Stream.of(helloClass.getDeclaredMethods()).forEach(System.out::println);

			method.invoke(helloClass, Array.newInstance(String.class, 1));

		}

		// ref
		// {
		// StringBuffer sb = new StringBuffer();
		// sb.append("package pak;\n");
		// sb.append("\n");
		// sb.append(" /**\n");
		// sb.append(" * @author KYJ\n");
		// sb.append(" *\n");
		// sb.append(" */\n");
		// sb.append(" public class ExampleRef {\n");
		// sb.append("\n");
		// sb.append(" public void hello(){\n");
		// sb.append(" System.out.println(\"ExampleRef ww hello\");\n");
		// sb.append(" }\n");
		// sb.append(" }\n");
		// sb.toString();
		//
		// Class<?> helloClass = InMemoryJavaCompiler.compile("pak.ExampleRef",
		// sb.toString());
		//
		// Assert.assertNotNull(helloClass);
		// Assert.assertEquals(1, helloClass.getDeclaredMethods().length);
		// }

		// ref
		// {
		// StringBuffer sb = new StringBuffer();
		// sb.append("package pak;\n");
		// sb.append("\n");
		// sb.append("/**\n");
		// sb.append(" * @author KYJ\n");
		// sb.append(" *\n");
		// sb.append(" */\n");
		// sb.append("public class ExampleMain {\n");
		// sb.append(" public static void main(String[] args) {\n");
		// sb.append(" new ExampleRef().hello();\n");
		// sb.append(" }\n");
		// sb.append("}\n");
		//
		// Class<?> helloClass = InMemoryJavaCompiler.compile("pak.ExampleMain",
		// sb.toString());
		//
		// Method method = helloClass.getDeclaredMethod("main", String[].class);
		// method.setAccessible(true);
		//
		// Stream.of(helloClass.getDeclaredMethods()).forEach(System.out::println);
		//
		// method.invoke(helloClass.newInstance(),
		// Array.newInstance(String.class, 1));
		// }
	}

	@Test
	public void reverse() throws Exception {
		// String name =
		// "com\\kyj\\fx\\voeditor\\visual\\main\\model\\vo\\ClassTypeCodeDVO.class";
		// InputStream resourceAsStream =
		// ClassLoader.getSystemClassLoader().getResourceAsStream(name);
		// if (resourceAsStream == null)
		// throw new Exception("File not found!");
		// String readToString = FileUtil.readToString(resourceAsStream);
		// System.out.println(readToString);

		CompiledCode compiledCode = new CompiledCode("com.kyj.fx.voeditor.visual.main.model.vo.ClassTypeCodeDVO");

		DynamicClassLoader cl = new DynamicClassLoader(ClassLoader.getSystemClassLoader());

		cl.addCode(compiledCode);

		Class<?> loadClass = cl.loadClass("com.kyj.fx.voeditor.visual.main.model.vo.ClassTypeCodeDVO");

		Stream.of(loadClass.getMethods()).forEach(m -> {

			System.out.println(m.getName());
		});
	}

	@Test
	public void dummyControllerMaker() throws Exception {

		StringBuffer sb = new StringBuffer();

		sb.append("/********************************\n");
		sb.append(" *	프로젝트 : auto.intern.for.pass\n");
		sb.append(" *	패키지   : com.kyj.intern.controller.dummy\n");
		sb.append(" *	작성일   : 2016. 7. 14.\n");
		sb.append(" *	작성자   : KYJ\n");
		sb.append(" *******************************/\n");
		sb.append("package com.kyj.intern.controller.dummy;\n");
		sb.append("\n");
		sb.append("\n");
		sb.append("/**\n");
		sb.append(" * @author KYJ\n");
		sb.append(" *\n");
		sb.append(" */\n");
		sb.append("public class HBoxDummyController extends javafx.scene.layout.HBox {\n");
		sb.append("\n");
		sb.append("}\n");
		sb.toString();

		List<SourceCode> asList = new ArrayList<>();

		asList.add(new SourceCode("com.kyj.intern.controller.dummy.HBoxDummyController", sb.toString()));
		// asList.add(new SourceCode("pak.ExampleRef2", sb3.toString()));

		Class<?> helloClass = InMemoryJavaCompiler.compile(asList, "com.kyj.intern.controller.dummy.HBoxDummyController");

		Assert.assertNotNull(helloClass);
		Assert.assertNotNull(helloClass.newInstance());
	}
}
