/********************************
 *	프로젝트 : FxVoEditor
 *	패키지   : com.kyj.fx.voeditor.core.parser
 *	작성일   : 2016. 7. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.kyj.fx.voeditor.core.VoEditor;
import com.kyj.fx.voeditor.core.model.meta.ClassMeta;
import com.kyj.fx.voeditor.core.model.meta.FieldMeta;
import com.kyj.fx.voeditor.visual.momory.ClassTypeResourceLoader;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/***************************
 *
 * @author KYJ
 *
 ***************************/
public class VOEditorParser2 {

	public static void main(String[] args) throws ParseException, IOException {
		String fileName = "C:\\Users\\KYJ\\JAVA_FX\\gagoyleWorkspace\\VisualFxVoEditor\\src\\main\\java\\com\\kyj\\fx\\voeditor\\visual\\main\\model\\vo\\ClassPathEntry.java";
		FileInputStream in = new FileInputStream(fileName);

		CompilationUnit cu;
		try {
			// parse the file
			cu = JavaParser.parse(in);
		} finally {
			in.close();
		}

		PackageDeclaration packageDeclaration = cu.getPackage();

		// System.out.println(packageDeclaration.getName().toString());
		// System.out.println();
		// System.out.println(String.format("package name : %s",
		// packageDeclaration.getName().getName()));

		ClassMeta classMeta = new ClassMeta("");
		classMeta.setPackageName(packageDeclaration.getName().toString());
		ArrayList<FieldMeta> fields = new ArrayList<FieldMeta>();

		VoEditor voEditor = new VoEditor(classMeta, fields);
		List<Node> childrenNodes = cu.getChildrenNodes();
		for (Node n : childrenNodes) {

		}

		new MethodVisitor().visit(cu, null);

	}

	/**
	 * Simple visitor implementation for visiting MethodDeclaration nodes.
	 */
	private static class MethodVisitor extends VoidVisitorAdapter<Object> {

		private Set<FieldMeta> fieldMes = new HashSet<>();

		public void visit(MethodDeclaration n, Object arg) {

			String name = n.getName();

			if (name.startsWith("set") || name.startsWith("get")) {
				String fieldName = ValueUtil.getIndexLowercase(name.substring(3, name.length()), 0);
				System.err.println(fieldName);
			}

			super.visit(n, arg);
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public void visit(VoidType n, Object arg) {
			System.out.println("VoidType ::" + n);
			super.visit(n, arg);
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public void visit(MethodCallExpr n, Object arg) {
			System.out.println("methodCallexpr ::" + n);
			super.visit(n, arg);
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public void visit(MethodReferenceExpr n, Object arg) {
			System.out.println("MethodReferenceExpr ::" + n);
			super.visit(n, arg);
		}

		@Override
		public void visit(FieldDeclaration n, Object arg) {

			Type type = n.getType();

			if (type instanceof ReferenceType) {
				ReferenceType refType = (ReferenceType) type;
				if (refType.getType() instanceof ClassOrInterfaceType) {
					ClassOrInterfaceType cOrInterfaceType = (ClassOrInterfaceType) refType.getType();
					if (cOrInterfaceType == null)
						return;
					int modifiers = n.getModifiers();
					List<VariableDeclarator> variables = n.getVariables();
					String fieldName = null;
					if (variables != null && variables.size() == 1) {
						fieldName = variables.get(0).toString();
					}

					if (fieldName == null)
						return;

					try {
						FieldMeta fieldMeta = ClassTypeResourceLoader.getInstance().get(cOrInterfaceType.getName());
						fieldMeta.setName(fieldName);
						fieldMeta.setModifier(modifiers);
						fieldMes.add(fieldMeta);
					} catch (ClassNotFoundException e) {
						// Nothing..
					}
				}
			}
			super.visit(n, arg);
		}
	}
}
