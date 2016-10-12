/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2016. 10. 6.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.fxmisc.richtext.CodeArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.pmd.PMDCheckedListComposite;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import net.sourceforge.pmd.benchmark.Benchmark;
import net.sourceforge.pmd.benchmark.Benchmarker;
import net.sourceforge.pmd.benchmark.TextReport;
import net.sourceforge.pmd.cli.PMDParameters;

/**
 *
 *  코드 처리 관련 Helper 클래스
 *
 *   CodeArea클래스와 연관된 모든 공통처리내용이 구현된다.
 *
 * @author KYJ
 *
 */
public class JavaCodeAreaHelper extends CodeAreaHelper implements EventHandler<ActionEvent> {

	private static Logger LOGGER = LoggerFactory.getLogger(JavaCodeAreaHelper.class);

	private Menu menuPmd;
	private MenuItem menuRunPmd;
	private MenuItem menuAutoComment;

	public JavaCodeAreaHelper(CodeArea codeArea) {
		super(codeArea);
		createContextMenu();
	}

	/**
	 * 메뉴 생성
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 6.
	 */
	private void createContextMenu() {
		menuPmd = new Menu("PMD");
		menuRunPmd = new MenuItem("Run PMD");
		menuAutoComment = new MenuItem("Auto Comment");

		menuRunPmd.setOnAction(this);
		menuAutoComment.setOnAction(this);
		menuPmd.getItems().addAll(menuRunPmd);
		ContextMenu contextMenu = codeArea.getContextMenu();
		if (contextMenu == null) {
			contextMenu = new ContextMenu();
			codeArea.setContextMenu(contextMenu);
		}
		contextMenu.getItems().addAll(menuPmd, menuAutoComment);
	}

	/* (non-Javadoc)
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(ActionEvent event) {
		if (event.getSource() == menuRunPmd) {
			doPmd();
		}

		else if (event.getSource() == menuAutoComment) {
			doAutoComment();
		}
	}

	/**
	 * @최초생성일 2016. 10. 12.
	 */
	public static final String appendLineKeyword = "+";
	/**
	 * 주석 자동화 처리
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 12.
	 */
	private void doAutoComment() {
		String javaCode = codeArea.getText();

		List<String> autoCommentedList = ValueUtil.toAutoCommentedList(javaCode, appendLineKeyword);

		JavaTextAreaForAutoComment area = new JavaTextAreaForAutoComment();
		area.setContent(autoCommentedList);

		FxUtil.createStageAndShow(area, stage -> {
			stage.setTitle(" Auto - Comment ");
			stage.initOwner(SharedMemory.getPrimaryStage());
			stage.setWidth(1200d);
			stage.setHeight(800d);
		});
	}

	/**
	 * PMD 처리
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 12.
	 */
	private void doPmd() {
		PMDCheckedListComposite pmdCheckedListComposite = new PMDCheckedListComposite(null) {

			@Override
			public void run() {
				simpleFilePmd(null);
			}

			@Override
			protected void simpleFilePmd(File file) {

				try {
					PMDParameters params = new PMDParameters();
					String sourceCode = codeArea.getText();
					params.setSourceFileName("Java");
					params.setSourceText(sourceCode);

					//						if (!FileUtil.isJavaFile(file)) {
					//							String fileExtension = FileUtil.getFileExtension(file);
					//							try {
					//								Field declaredField = PMDParameters.class.getDeclaredField("language");
					//								if (declaredField != null) {
					//									declaredField.setAccessible(true);
					//									declaredField.set(params, fileExtension);
					//								}
					//							} catch (Exception e) {
					//								e.printStackTrace();
					//							}
					//						}

					//			transformParametersIntoConfiguration(params);
					long start = System.nanoTime();

					doPMD.doPMD(transformParametersIntoConfiguration(params), reportListenerPropertyProperty().get());
					long end = System.nanoTime();
					Benchmarker.mark(Benchmark.TotalPMD, end - start, 0);

					TextReport report = new TextReport();
					try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
						report.generate(Benchmarker.values(), new PrintStream(out));
						out.flush();
						LOGGER.debug(out.toString("UTF-8"));
					}

					updateStatus(sourceCode);

				} catch (IOException e) {
					LOGGER.error(ValueUtil.toString(e));
				}
			}

		};

		pmdCheckedListComposite.run();

		FxUtil.createStageAndShow(pmdCheckedListComposite.getParent(), stage -> {
			stage.setTitle("PMD Check.");
			//Owner를 Root로 지정.
			stage.initOwner(SharedMemory.getPrimaryStage());
			stage.setWidth(1200d);
			stage.setHeight(800d);
		});
	}

}
