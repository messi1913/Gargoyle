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
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.kyj.fx.voeditor.visual.component.text.JavaTextArea;
import com.kyj.fx.voeditor.visual.component.text.XMLEditor;
import com.kyj.fx.voeditor.visual.framework.PrimaryStageCloseable;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import kyj.Fx.dao.wizard.core.util.ValueUtil;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.ReportListener;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.RulesetsFactoryUtils;
import net.sourceforge.pmd.benchmark.Benchmark;
import net.sourceforge.pmd.benchmark.Benchmarker;
import net.sourceforge.pmd.benchmark.TextReport;
import net.sourceforge.pmd.cli.PMDParameters;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageFilenameFilter;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.renderers.DatabaseXmlRenderer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.stat.Metric;
import net.sourceforge.pmd.util.ClasspathClassLoader;
import net.sourceforge.pmd.util.FileUtil;
import net.sourceforge.pmd.util.IOUtil;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.ReaderDataSource;

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

	private StringWriter stringWriter = new StringWriter();

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
			stringWriter.getBuffer().setLength(0);
			String sourceCode = Files.toString(this.sourceFile, Charset.forName("UTF-8"));
			javaTextArea.setContent(sourceCode);

			PMDParameters params = new PMDParameters();
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
				xmlEditor.setContent(stringWriter.getBuffer().toString());
				violationLabel.setText(String.format(VIOLATION_TEXT_FORMAT, violations));
			});

		} catch (IOException e) {
			LOGGER.error(ValueUtil.toString(e));
		}
	}

	private void dirFilePmd(File file) {
		try {
			stringWriter.getBuffer().setLength(0);
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
				xmlEditor.setContent(stringWriter.getBuffer().toString());
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
		configuration.setThreads(1);
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

	private Writer getStringWriter() {
		return stringWriter;
	}

	/***************************
	 * PMD 처리
	 *
	 * @author KYJ
	 *
	 ***************************/
	class DoPMD {

		/**
		 * This method is the main entry point for command line usage.
		 *
		 * @param configuration
		 *            the configure to use
		 * @return number of violations found.
		 */
		public int doPMD(PMDConfiguration configuration) {

			// Load the RuleSets
			RuleSetFactory ruleSetFactory = RulesetsFactoryUtils.getRulesetFactory(configuration);
			RuleSets ruleSets = RulesetsFactoryUtils.getRuleSetsWithBenchmark(configuration.getRuleSets(), ruleSetFactory);
			if (ruleSets == null) {
				return 0;
			}

			Set<Language> languages = getApplicableLanguages(configuration, ruleSets);
			List<DataSource> files = getApplicableFiles(configuration, languages);

			long reportStart = System.nanoTime();
			try {
				Renderer renderer = new DatabaseXmlRenderer();
				//RendererFactory.createRenderer(configuration.getReportFormat(), configuration.getReportProperties()); //configuration.createRenderer();//createDefaultRenderer();
				List<Renderer> renderers = new LinkedList<>();
				renderers.add(renderer);

				renderer.setWriter(getStringWriter());
				renderer.start();

				Benchmarker.mark(Benchmark.Reporting, System.nanoTime() - reportStart, 0);

				RuleContext ctx = new RuleContext();
				final AtomicInteger violations = new AtomicInteger(0);
				ctx.getReport().addListener(new ReportListener() {
					@Override
					public void ruleViolationAdded(RuleViolation ruleViolation) {
						violations.incrementAndGet();
					}

					@Override
					public void metricAdded(Metric metric) {
					}
				});

				processFiles(configuration, ruleSetFactory, files, ctx, renderers);

				reportStart = System.nanoTime();
				//				renderer.renderFileReport();
				renderer.end();
				renderer.flush();
				return violations.get();
			} catch (Exception e) {
				e.printStackTrace();
				String message = e.getMessage();
				return 0;
			} finally {
				Benchmarker.mark(Benchmark.Reporting, System.nanoTime() - reportStart, 0);
			}
		}

		private Set<Language> getApplicableLanguages(PMDConfiguration configuration, RuleSets ruleSets) {
			Set<Language> languages = new HashSet<>();
			LanguageVersionDiscoverer discoverer = configuration.getLanguageVersionDiscoverer();

			for (Rule rule : ruleSets.getAllRules()) {
				Language language = rule.getLanguage();
				if (languages.contains(language)) {
					continue;
				}
				LanguageVersion version = discoverer.getDefaultLanguageVersion(language);
				if (RuleSet.applies(rule, version)) {
					languages.add(language);
				}
			}
			return languages;
		}

		/**
		 * Determines all the files, that should be analyzed by PMD.
		 *
		 * @param configuration
		 *            contains either the file path or the DB URI, from where to load the files
		 * @param languages
		 *            used to filter by file extension
		 * @return List<DataSource> of files
		 */
		public List<DataSource> getApplicableFiles(PMDConfiguration configuration, Set<Language> languages) {
			long startFiles = System.nanoTime();
			List<DataSource> files = internalGetApplicableFiles(configuration, languages);
			long endFiles = System.nanoTime();
			Benchmarker.mark(Benchmark.CollectFiles, endFiles - startFiles, 0);
			return files;
		}

		private List<DataSource> internalGetApplicableFiles(PMDConfiguration configuration, Set<Language> languages) {
			List<DataSource> files = new ArrayList<>();
			LanguageFilenameFilter fileSelector = new LanguageFilenameFilter(languages);

			if (null != configuration.getInputPaths()) {
				files.addAll(FileUtil.collectFiles(configuration.getInputPaths(), fileSelector));
			}

			if (null != configuration.getSourceText()) {

				String filePaths = "SourceBase";
				filePaths = filePaths.replaceAll("\\r?\\n", ",");
				filePaths = filePaths.replaceAll(",+", ",");

				String sourceText = configuration.getSourceText();
				try {
					Reader reader = new StringReader(sourceText);
					files.addAll(Arrays.asList(new ReaderDataSource(reader, filePaths)));
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			return files;
		}

		/**
		 * Run PMD on a list of files using multiple threads - if more than one is available
		 *
		 * @param configuration
		 *            Configuration
		 * @param ruleSetFactory
		 *            RuleSetFactory
		 * @param files
		 *            List<DataSource>
		 * @param ctx
		 *            RuleContext
		 * @param renderers
		 *            List<Renderer>
		 */
		public void processFiles(final PMDConfiguration configuration, final RuleSetFactory ruleSetFactory, final List<DataSource> files,
				final RuleContext ctx, final List<Renderer> renderers) {

			sortFiles(configuration, files);

			/*
			 * Check if multithreaded support is available. ExecutorService can also
			 * be disabled if threadCount is not positive, e.g. using the
			 * "-threads 0" command line option.
			 */
			//			if (SystemUtils.MT_SUPPORTED && configuration.getThreads() > 0) {
			//				MultiThreadProcessor multiThreadProcessor = new MultiThreadProcessor(configuration);
			//				multiThreadProcessor.processFiles(ruleSetFactory, files, ctx, renderers);
			//			} else {
			new PMDGargoyleThreadProcessor(configuration).processFiles(ruleSetFactory, files, ctx, renderers);
			//			}

			if (configuration.getClassLoader() instanceof ClasspathClassLoader) {
				IOUtil.tryCloseClassLoader(configuration.getClassLoader());
			}
		}

		private void sortFiles(final PMDConfiguration configuration, final List<DataSource> files) {
			if (configuration.isStressTest()) {
				// randomize processing order
				Collections.shuffle(files);
			} else {
				final boolean useShortNames = configuration.isReportShortNames();
				final String inputPaths = configuration.getInputPaths();
				Collections.sort(files, new Comparator<DataSource>() {
					@Override
					public int compare(DataSource left, DataSource right) {
						String leftString = left.getNiceFileName(useShortNames, inputPaths);
						String rightString = right.getNiceFileName(useShortNames, inputPaths);
						return leftString.compareTo(rightString);
					}
				});
			}
		}
	}

	@Override
	public void closeRequest() {
		try {
			this.stringWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void close() throws IOException {
		closeRequest();
	}

}
