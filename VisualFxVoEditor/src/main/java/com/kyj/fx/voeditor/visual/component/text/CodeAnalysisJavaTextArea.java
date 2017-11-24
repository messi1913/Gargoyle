package com.kyj.fx.voeditor.visual.component.text;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.fxloader.FxPostInitialize;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.biz.SourceCodeAnalysis;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.model.BizFile;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.vo.SourceAnalysisDVO;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "CodeAnalysisJavaTextAreaView.fxml", isSelfController = true)
public class CodeAnalysisJavaTextArea extends BorderPane {
	private static Logger LOGGER = LoggerFactory.getLogger(CodeAnalysisJavaTextArea.class);

	private ChoiceBox<SourceAnalysisDVO> choMethod;
	private File javaFile;
	private BizFile newInstance;
	// private ProgramSpecSVO svo = new ProgramSpecSVO();
	private JavaTextArea txtJavaTextArea;

	public CodeAnalysisJavaTextArea(File javaFile) {
		this.javaFile = javaFile;
		txtJavaTextArea = new JavaTextArea();

		FxUtil.loadRoot(CodeAnalysisJavaTextArea.class, this, ex -> {
			LOGGER.error(ValueUtil.toString(ex));
		});
		//bug fix.
		this.setCenter(txtJavaTextArea);
	}

	@FXML
	public void initialize() {
		choMethod = new ChoiceBox<>();
		choMethod.setConverter(new StringConverter<SourceAnalysisDVO>() {
			@Override
			public String toString(SourceAnalysisDVO d) {
				return d.getMethodName();
			}

			@Override
			public SourceAnalysisDVO fromString(String string) {

				return null;
			}
		});
		// jdk1.8.73이후버젼부터 사용가능
		choMethod.setOnAction(event -> {

			/*
			 * select라인처리하는데 빡치는줄...
			 *
			 * 물론 코드 분석 기능을 가져오는 api도 잘못했지만..
			 *
			 * 정말 빡친건.. select라인을 구하는게 어렵다.
			 */
			SourceAnalysisDVO selectedItem = choMethod.getSelectionModel().getSelectedItem();
			String methodName = selectedItem.getMethodName();
			int methodBlockStart = selectedItem.getMethodBlockStart();

			int finallyBlockIdx = methodBlockStart - 1;
			/*
			 * 기본적으로 methodBlockStart- 1값에 위치해있으나 공백등의 이유로 인해 다른경우가 있음. 정확한 메소드
			 * 인덱스를 구하기위해 methodBlockStart- 1 ~ methodBlockStart +1 까지 범위내에서 메소드
			 * 명이 있는 블록을 찾음.
			 */
			String string = newInstance.getReadLines().get(finallyBlockIdx);
			if (!string.contains(methodName)) {

				for (int i = methodBlockStart - 2; i < methodBlockStart + 1; i++) {
					if (i < 0)
						continue;
					if (i == (methodBlockStart - 1))
						continue;

					string = newInstance.getReadLines().get(i);

					if (string.contains(methodName)) {
						finallyBlockIdx = i;
						break;
					}
				}
			}

			IntStream mapToInt = Stream.of(txtJavaTextArea.getText().split("\n")).limit(finallyBlockIdx).mapToInt(mapper -> {
				// 여기서 1을 더하는 이유는 \n때문임. 갯수를 더해줘야함.
				return mapper.length() + 1;
			});

			// 공백만큼 상대값을 구한후....
			int length = string.split("\t").length - 1;

			int sum = mapToInt.sum() + length;

			System.out.println(string);
			txtJavaTextArea.selectRange(sum, sum + methodName.length());

		});

		this.setTop(new HBox(5, new Label("Method Name : "), choMethod));
	}

	@FxPostInitialize
	public void init() {
		List<SourceAnalysisDVO> methodList = Collections.emptyList();
		try {
			newInstance = new BizFile(javaFile);
			List<String> readLines = newInstance.getReadLines();
			txtJavaTextArea.setContent(readLines);
			// svo = ProgramSpecUtil.doJavaFile("sampleJavaProject",
			// javaFile.getName(), (AbstractJavaProgramSpecFile) newInstance);
			// this.setContent(svo.getFile().getInspectorSourceMeta().getSourceCode());
			SourceCodeAnalysis sa = newInstance.getJavaSourceAnalysis();
			methodList = sa.findStatement();
			choMethod.getItems().addAll(methodList);

		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}

	}

}
