/********************************
 *	프로젝트 : FxVoEditor
 *	패키지   : com.kyj.fx.voeditor.core.parser
 *	작성일   : 2016. 7. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.parser;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import org.junit.experimental.categories.Categories.ExcludeCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/***************************
 *
 * @author KYJ
 *
 ***************************/
@Deprecated
public class VOEditorParser implements Closeable {

	private static final Logger LOGGER = LoggerFactory.getLogger(VOEditorParser.class);
	
	private InputStream is;

	public VOEditorParser(InputStream is) throws IOException {
		this.is = is;
	}

	public void parse() throws ParseException {

		// FileInputStream in = new FileInputStream(fileName);

		CompilationUnit cu = JavaParser.parse(is);

		PackageDeclaration package1 = cu.getPackage();

		
		LOGGER.debug(package1.getName().toString());
		
		LOGGER.debug(String.format("package name : %s", package1.getName().getName()));
		// prints the resulting compilation unit to default system output
		LOGGER.debug(cu.toString());

		new MethodVisitor().visit(cu, null);

	}

	@Override
	public void close() throws IOException {
		if (is != null)
			is.close();
	}

	/**
	 * Simple visitor implementation for visiting MethodDeclaration nodes.
	 */
	private static class MethodVisitor extends VoidVisitorAdapter<Object> {

		@Override
		public void visit(MethodDeclaration n, Object arg) {
//			LOGGER.debug(n.getName());
			super.visit(n, arg);
		}

		@Override
		public void visit(FieldDeclaration n, Object arg) {

//			LOGGER.debug(n.getVariables());
			super.visit(n, arg);
		}


	}

}
