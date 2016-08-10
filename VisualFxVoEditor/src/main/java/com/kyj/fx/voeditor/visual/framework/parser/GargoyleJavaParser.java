/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.parser
 *	작성일   : 2016. 7. 14.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Modifier;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.kyj.fx.voeditor.visual.framework.FileCheckConverter;

/***************************
 * 
 * @author KYJ
 *
 ***************************/
public class GargoyleJavaParser {

	/********************************
	 * 작성일 : 2016. 7. 14. 작성자 : KYJ
	 *
	 *
	 * @param javaFile
	 * @param converter
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 ********************************/
	public static String getPackageName(File javaFile, FileCheckConverter<String> converter)
			throws FileNotFoundException, IOException, ParseException {
		String packageName = null;

		if (javaFile == null) {
			packageName = converter.ifNull();
		} else if (!javaFile.exists())
			packageName = converter.notExists();
		else if (javaFile.isFile()) {

			CompilationUnit cu = getCompileUnit(javaFile);
			packageName = getPackageName(cu);

		}
		return packageName;

	}

	/********************************
	 * 작성일 : 2016. 7. 14. 작성자 : KYJ
	 *
	 *
	 * @param cu
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 ********************************/
	public static String getPackageName(CompilationUnit cu) throws FileNotFoundException, IOException, ParseException {
		PackageDeclaration packageDeclaration = cu.getPackage();
		return packageDeclaration.getPackageName();
	}

	/********************************
	 * 작성일 : 2016. 7. 14. 작성자 : KYJ
	 *
	 *
	 * @param f
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 ********************************/
	public static CompilationUnit getCompileUnit(File f) throws FileNotFoundException, IOException, ParseException {

		CompilationUnit cu = null;
		try (FileInputStream in = new FileInputStream(f)) {
			// parse the file
			cu = JavaParser.parse(in);
		}

		return cu;
	}

//	public static String toStringVisibility(int modifier) {
//		return Modifier.toString(modifier);
//	}
	
	public static String toStringVisibility(int modifier) {

		// 접근지정자 추출.
		String accessModifiers = "";

		switch (modifier) {
		case Modifier.PUBLIC:
			accessModifiers = "public";
			break;
		case Modifier.PRIVATE:
			accessModifiers = "private";
			break;
		case Modifier.PROTECTED:
			accessModifiers = "protected";
			break;
		default:
			accessModifiers = "default";
			break;
		}
		return accessModifiers;
	}

}
