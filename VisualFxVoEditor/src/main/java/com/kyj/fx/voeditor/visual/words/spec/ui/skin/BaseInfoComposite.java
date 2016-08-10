/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.words.spec.ui.skin
 *	작성일   : 2016. 2. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.words.spec.ui.skin;

import java.io.File;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang.SystemUtils;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.kyj.fx.voeditor.visual.component.FileWrapper;
import com.kyj.fx.voeditor.visual.component.JavaProjectFileTreeItem;
import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.framework.parser.GargoyleJavaParser;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.vo.MethodDVO;
import com.kyj.fx.voeditor.visual.words.spec.resources.SpecResource;
import com.kyj.fx.voeditor.visual.words.spec.ui.tabs.AbstractSpecTab;
import com.sun.btrace.org.objectweb.asm.MethodVisitor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "BaseInfoApp.fxml", isSelfController = true)
public class BaseInfoComposite extends BorderPane {

	@FXML
	private TextField lblProjectName, lblRealPath, lblFileName, lblPackage, lblImports, lblUserName;

	private ObservableList<MethodDVO> methodData = FXCollections.observableArrayList();

	private AbstractSpecTab projectInfoBaseInfoTab;

	/**
	 * Construnctor.
	 * @param projectInfoBaseInfoTab
	 *
	 * @param projectDir
	 * @param targetFile
	 * @throws Exception
	 */
	public BaseInfoComposite(AbstractSpecTab projectInfoBaseInfoTab) throws Exception {
		this.projectInfoBaseInfoTab = projectInfoBaseInfoTab;
		FxUtil.loadRoot(getClass(), this);
	}

	@FXML
	public void initialize() {



		SpecResource specResource = projectInfoBaseInfoTab.getSpecResource();
		//TODO SVO형태로 받은경우는 없을 수 있음 처리해야함.
		
		File projectFile = specResource.getProjectFile();
		File targetFile = specResource.getTargetFile();

		String projectFileName = projectFile.getName();

		lblProjectName.setText(projectFileName);
		lblRealPath.setText(targetFile.getAbsolutePath());
		lblFileName.setText(targetFile.getName());
		lblUserName.setText(SystemUtils.USER_NAME);

		FileUtil.consumeJavaParser(targetFile, cu -> {

			NameExpr name = cu.getPackage().getName();
			lblPackage.setText(name.toString());
			String importStatement = cu.getImports().stream().map(im -> im.getName().toString()).collect(Collectors.joining(","));
			lblImports.setText(importStatement);

			Service<Void> service = new Service<Void>() {
				@Override
				protected Task<Void> createTask() {

					return new Task<Void>() {

						@Override
						protected Void call() throws Exception {
							new MethodVisitor(v -> {
								methodData.add(v);
							}).visit(cu, null);
							return null;
						}
					};
				}
			};
			service.start();

		} , System.err::println);

	}
	//	}

	/********************************
	 * 작성일 : 2016. 7. 14. 작성자 : KYJ
	 *
	 * JavaProjectTree 반환
	 *
	 * @param treeItem
	 * @return
	 ********************************/
	public JavaProjectFileTreeItem getJavaProjectFileTreeItem(TreeItem<FileWrapper> treeItem) {

		if (treeItem != null) {

			if (treeItem instanceof JavaProjectFileTreeItem) {
				return (JavaProjectFileTreeItem) treeItem;
			}

			return getJavaProjectFileTreeItem(treeItem.getParent());
		}

		return null;
	}

	/**
	 * 메타정보를 콜렉팅
	 *
	 * @author KYJ
	 *
	 */
	private static class MethodVisitor extends VoidVisitorAdapter<Object> {

		private Consumer<MethodDVO> onMethodDVOVisite;

		public MethodVisitor(Consumer<MethodDVO> onMethodDVOVisite) {
			this.onMethodDVOVisite = onMethodDVOVisite;
		}

		public void visit(MethodDeclaration n, Object arg) {

			MethodDVO methodDVO = new MethodDVO();
			methodDVO.setMethodName(n.getName());
			methodDVO.setVisivility(GargoyleJavaParser.toStringVisibility(n.getModifiers()));
			methodDVO.setDescription(n.getComment() != null ? n.getComment().toString() : "");

			onMethodDVOVisite.accept(methodDVO);

			super.visit(n, arg);
		}

	}

	public ObservableList<MethodDVO> getMethodData() {
		return methodData;
	}

}
