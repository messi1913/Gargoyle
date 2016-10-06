/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.pmd
 *	작성일   : 2016. 10. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.pmd;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.kyj.fx.voeditor.visual.component.text.JavaTextArea;
import com.kyj.fx.voeditor.visual.component.text.XMLEditor;
import com.kyj.fx.voeditor.visual.framework.PrimaryStageCloseable;
import com.kyj.fx.voeditor.visual.framework.pmd.DoPMD;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import kyj.Fx.dao.wizard.core.util.ValueUtil;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.benchmark.Benchmark;
import net.sourceforge.pmd.benchmark.Benchmarker;
import net.sourceforge.pmd.benchmark.TextReport;
import net.sourceforge.pmd.cli.PMDParameters;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;

/***************************
 *
 * @author KYJ
 *
 ***************************/

public class PMDCheckComposite extends BorderPane implements PrimaryStageCloseable, Closeable {

	private static final Logger LOGGER = LoggerFactory.getLogger(PMDCheckComposite.class);

	private File sourceFile;

	private JavaTextArea javaTextArea;

	private XMLEditor xmlEditor;

	private Label violationLabel;

	private DoPMD doPMD = new DoPMD();

	private static final String RESULTSET = new File("rulesets/java/basic.xml").getAbsolutePath();
	private static final String REPORT_FILE_FORMAT = "xml";
	private static final String VIOLATION_TEXT_FORMAT = "Violation : %d";

	public PMDCheckComposite(File sourceFile) {
		this.sourceFile = sourceFile;

		javaTextArea = new JavaTextArea();
		xmlEditor = new XMLEditor();
		SplitPane splitPane = new SplitPane(javaTextArea, xmlEditor);
		splitPane.setOrientation(Orientation.VERTICAL);
		splitPane.setDividerPositions(0.7d, 0.3d);

		setCenter(splitPane);
		violationLabel = new Label();
		setBottom(violationLabel);

		if (this.sourceFile.isDirectory()) {
			dirFilePmd(this.sourceFile);
		} else {
			simpleFilePmd(this.sourceFile);
		}

	}

	private void simpleFilePmd(File file) {
		try {

			String sourceCode = Files.toString(this.sourceFile, Charset.forName("UTF-8"));
			javaTextArea.setContent(sourceCode);

			PMDParameters params = new PMDParameters();
			params.setSourceFileName(file.getAbsolutePath());
			params.setSourceText(sourceCode);

			//			transformParametersIntoConfiguration(params);
			long start = System.nanoTime();

			int violations = doPMD.doPMD(transformParametersIntoConfiguration(params));
			long end = System.nanoTime();
			Benchmarker.mark(Benchmark.TotalPMD, end - start, 0);

			TextReport report = new TextReport();

			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				report.generate(Benchmarker.values(), new PrintStream(out));
				out.flush();
				LOGGER.debug(out.toString("UTF-8"));
			}

			Platform.runLater(() -> {
				xmlEditor.setContent(doPMD.getResultString());
				violationLabel.setText(String.format(VIOLATION_TEXT_FORMAT, violations));
			});

		} catch (IOException e) {
			LOGGER.error(ValueUtil.toString(e));
		}
	}

	private void dirFilePmd(File file) {
		try {
			//			String sourceCode = Files.toString(this.sourceFile, Charset.forName("UTF-8"));
			//			javaTextArea.setContent(sourceCode);

			PMDParameters params = new PMDParameters();
			try {
				Field declaredField = PMDParameters.class.getDeclaredField("sourceDir");
				if (declaredField != null) {
					declaredField.setAccessible(true);
					declaredField.set(params, file.getAbsolutePath());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			//			transformParametersIntoConfiguration(params);
			long start = System.nanoTime();

			int violations = doPMD.doPMD(transformParametersIntoConfiguration(params));
			long end = System.nanoTime();
			Benchmarker.mark(Benchmark.TotalPMD, end - start, 0);

			TextReport report = new TextReport();

			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				report.generate(Benchmarker.values(), new PrintStream(out));
				out.flush();
				LOGGER.debug(out.toString("UTF-8"));
			}

			Platform.runLater(() -> {
				xmlEditor.setContent(doPMD.getResultString());
				violationLabel.setText(String.format(VIOLATION_TEXT_FORMAT, violations));
			});

		} catch (IOException e) {
			LOGGER.error(ValueUtil.toString(e));
		}
	}

	public PMDConfiguration transformParametersIntoConfiguration(PMDParameters params) {
		//		if (null == params.getSourceDir() && null == params.getUri() && null == params.getFileListPath()
		//				&& null == params.getSourceText()) {
		//			throw new IllegalArgumentException(
		//					"Please provide a parameter for source root directory (-dir or -d), database URI (-uri or -u), or file list path (-filelist).");
		//		}
		PMDConfiguration configuration = new PMDConfiguration();
		configuration.setInputPaths(params.getSourceDir());
		//		configuration.setInputFilePath(params.getFileListPath());
		//		configuration.setInputUri(params.getUri());
		configuration.setReportFormat(REPORT_FILE_FORMAT);
		configuration.setSourceFileName(params.getSourceFileName());
		configuration.setSourceText(params.getSourceText());
		//		configuration.setBenchmark(params.isBenchmark());
		configuration.setDebug(params.isDebug());
		//		configuration.setMinimumPriority(params.getMinimumPriority());

		configuration.setReportFile(params.getReportfile());
		configuration.setReportProperties(params.getProperties());
		//		configuration.setReportShortNames(params.isShortnames());
		configuration.setRuleSets(RESULTSET);
		configuration.setRuleSetFactoryCompatibilityEnabled(true);
		configuration.setShowSuppressedViolations(params.isShowsuppressed());
		configuration.setSourceEncoding("UTF-8");
		//	        configuration.setStressTest(params.isStress());
		configuration.setSuppressMarker(params.getSuppressmarker());
//		configuration.setThreads(1);
		configuration.setFailOnViolation(params.isFailOnViolation());

		LanguageVersion languageVersion = LanguageRegistry.findLanguageVersionByTerseName(params.getLanguage() + " " + params.getVersion());
		if (languageVersion != null) {
			configuration.getLanguageVersionDiscoverer().setDefaultLanguageVersion(languageVersion);
		}
		try {
			configuration.prependClasspath(params.getAuxclasspath());
		} catch (IOException e) {
			throw new IllegalArgumentException("Invalid auxiliary classpath: " + e.getMessage(), e);
		}
		return configuration;
	}

	@Override
	public void closeRequest() {
		try {
			doPMD.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void close() throws IOException {
		closeRequest();
	}

}
