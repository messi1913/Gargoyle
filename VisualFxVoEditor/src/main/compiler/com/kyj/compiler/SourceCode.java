package com.kyj.compiler;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.net.URI;

/**
 *
 * 소스코드 오브젝트 객체
 * 
 *
 * @author KYJ
 *
 */
public class SourceCode extends SimpleJavaFileObject {
	private String contents, className;

	public SourceCode(String className, String contents) throws Exception {
		super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
		this.contents = contents;
		this.className = className;
	}

	public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
		System.out.println("######## " + getName() + " ##########");
		System.out.println(contents);
		return contents;
	}

	public String getClassName() {
		return this.className;
	}
}
