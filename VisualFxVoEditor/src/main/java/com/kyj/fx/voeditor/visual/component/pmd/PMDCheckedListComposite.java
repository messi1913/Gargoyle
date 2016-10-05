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
import com.kyj.fx.voeditor.visual.framework.pmd.DoPMD;
import com.kyj.fx.voeditor.visual.main.layout.CloseableParent;
import com.kyj.fx.voeditor.visual.util.FileUtil;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import kyj.Fx.dao.wizard.core.util.ValueUtil;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.ReportListener;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.benchmark.Benchmark;
import net.sourceforge.pmd.benchmark.Benchmarker;
import net.sourceforge.pmd.benchmark.TextReport;
import net.sourceforge.pmd.cli.PMDParameters;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.stat.Metric;

/***************************
 *
 * @author KYJ
 *
 ***************************/

public class PMDCheckedListComposite extends CloseableParent<BorderPane> {

	/**
	 * @param parent
	 */
	//	public PMDCheckedListComposite(BorderPane parent) {
	//		super(parent);
	//	}

	private static final Logger LOGGER = LoggerFactory.getLogger(PMDCheckedListComposite.class);

	private File sourceFile;

	private JavaTextArea javaTextArea;

	private ListView<RuleViolation> lvViolation;

	private Label violationLabel;

	private DoPMD doPMD = new DoPMD();

	private static final String RESULTSET = new File("rulesets/java/basic.xml").getAbsolutePath();
	private static final String REPORT_FILE_FORMAT = "xml";
	private static final String VIOLATION_TEXT_FORMAT = "Violation : %d";

	public PMDCheckedListComposite(File sourceFile) {
		this(new BorderPane(), sourceFile);
	}

	public PMDCheckedListComposite(BorderPane root, File sourceFile) {
		super(root);
		this.sourceFile = sourceFile;

		javaTextArea = new JavaTextArea();
		lvViolation = new ListView<>();

		lvViolation.setCellFactory(ruleCheckListener);
		SplitPane splitPane = new SplitPane(javaTextArea, lvViolation);
		splitPane.setOrientation(Orientation.VERTICAL);
		splitPane.setDividerPositions(0.7d, 0.3d);

		root.setCenter(splitPane);
		violationLabel = new Label();
		root.setBottom(violationLabel);

	}

	public void run() {
		if (this.sourceFile.isDirectory()) {
			dirFilePmd(this.sourceFile);
		} else {
			simpleFilePmd(this.sourceFile);
		}
	}

	public void runAsynch() {

		new Thread(() -> {
			if (this.sourceFile.isDirectory()) {
				dirFilePmd(this.sourceFile);
			} else {
				simpleFilePmd(this.sourceFile);
			}
		}).start();

	}

	private void simpleFilePmd(File file) {
		try {
			String sourceCode = Files.toString(this.sourceFile, Charset.forName("UTF-8"));
			PMDParameters params = new PMDParameters();
			params.setSourceText(sourceCode);

			//			transformParametersIntoConfiguration(params);
			long start = System.nanoTime();

			doPMD.doPMD(transformParametersIntoConfiguration(params), reportListener);
			long end = System.nanoTime();
			Benchmarker.mark(Benchmark.TotalPMD, end - start, 0);

			TextReport report = new TextReport();
			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				report.generate(Benchmarker.values(), new PrintStream(out));
				out.flush();
				LOGGER.debug(out.toString("UTF-8"));
			}

			Platform.runLater(() -> {
				javaTextArea.setContent(sourceCode);
				violationLabel.setText(String.format(VIOLATION_TEXT_FORMAT, lvViolation.getItems().size()));
			});

		} catch (IOException e) {
			LOGGER.error(ValueUtil.toString(e));
		}
	}

	private void dirFilePmd(File file) {
		try {
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
			doPMD.doPMD(transformParametersIntoConfiguration(params), reportListener);
			long end = System.nanoTime();
			Benchmarker.mark(Benchmark.TotalPMD, end - start, 0);

			TextReport report = new TextReport();

			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				report.generate(Benchmarker.values(), new PrintStream(out));
				out.flush();
				LOGGER.debug(out.toString("UTF-8"));
			}

			Platform.runLater(() -> {
				violationLabel.setText(String.format(VIOLATION_TEXT_FORMAT, lvViolation.getItems().size()));
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
		configuration.setSourceText(params.getSourceText());
		//		configuration.setBenchmark(params.isBenchmark());
		configuration.setDebug(params.isDebug());
		//		configuration.setMinimumPriority(params.getMinimumPriority());

		configuration.setReportFile(params.getReportfile());
		configuration.setReportProperties(params.getProperties());
		//		configuration.setReportShortNames(params.isShortnames());
		configuration.setRuleSets(RESULTSET);
//		configuration.setRuleSetFactoryCompatibilityEnabled(true);
		configuration.setShowSuppressedViolations(params.isShowsuppressed());
		configuration.setSourceEncoding("UTF-8");
		//	        configuration.setStressTest(params.isStress());
//		configuration.setSuppressMarker(params.getSuppressmarker());
		configuration.setThreads(1);
//		configuration.setFailOnViolation(params.isFailOnViolation());

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

	private SimpleObjectProperty<File> lastSelectedFileProperty = new SimpleObjectProperty<>();

	private Callback<ListView<RuleViolation>, ListCell<RuleViolation>> ruleCheckListener = new Callback<ListView<RuleViolation>, ListCell<RuleViolation>>() {
		/* (non-Javadoc)
		 * @see javafx.util.Callback#call(java.lang.Object)
		 */
		@Override
		public ListCell<RuleViolation> call(ListView<RuleViolation> param) {
			PMDListCell pmdListCell = new PMDListCell();
			pmdListCell.setOnMouseClicked(ev -> {

				if (ev.getButton() == MouseButton.PRIMARY && ev.getClickCount() == 2) {
					RuleViolation item = pmdListCell.getItem();
					int beginLine = item.getBeginLine();

					JavaTextArea javaTextArea = PMDCheckedListComposite.this.javaTextArea;

					if (PMDCheckedListComposite.this.sourceFile.isFile()) {
						javaTextArea.moveToLine(beginLine);
					}
					//디렉토리 선택의 경우만..
					else {
						String filename = item.getFilename();
						File file = new File(filename);

						File lastReadFile = lastSelectedFileProperty.get();

						if ((lastReadFile == null && file.exists()) || !(lastReadFile.equals(file))) {
							LOGGER.debug("Read File :: {}", file);
							javaTextArea.setContent(FileUtil.readFile(file, null));
							javaTextArea.moveToLine(beginLine);
							lastSelectedFileProperty.set(file);
						}
					}
				}

			});
			return pmdListCell;
		}
	};

	private ReportListener reportListener = new ReportListener() {

		@Override
		public void ruleViolationAdded(RuleViolation ruleViolation) {
			Rule rule = ruleViolation.getRule();
			String name = rule.getName();
			String ruleSetName = rule.getRuleSetName();
			Language language = rule.getLanguage();


			LOGGER.debug("{}\n rulesetName : {}\nruleName :{}\nLang:{}", ruleViolation.toString(), ruleSetName, name, language.toString());
			lvViolation.getItems().add(ruleViolation);
		}

		@Override
		public void metricAdded(Metric metric) {

		}

	};

	/* (non-Javadoc)
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		doPMD.close();
	}

}
