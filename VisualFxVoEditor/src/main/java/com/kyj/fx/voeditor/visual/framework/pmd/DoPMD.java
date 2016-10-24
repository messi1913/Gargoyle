/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.pmd
 *	작성일   : 2016. 10. 5.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.pmd;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.kyj.fx.voeditor.visual.component.pmd.PMDGargoyleThreadProcessor;

import net.sourceforge.pmd.ReportListener;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.benchmark.Benchmark;
import net.sourceforge.pmd.benchmark.Benchmarker;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageFilenameFilter;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.stat.Metric;
import net.sourceforge.pmd.util.ClasspathClassLoader;
import net.sourceforge.pmd.util.FileUtil;
import net.sourceforge.pmd.util.IOUtil;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.ReaderDataSource;

/**
 *
 *  PMD 처리
 * @author KYJ
 *
 */
public class DoPMD implements Closeable {

	private StringWriter stringWriter = new StringWriter();

	private PMDGargoyleThreadProcessor pmdGargoyleThreadProcessor;

	private Writer getStringWriter() {
		return stringWriter;
	}

	public String getResultString() {
		return this.stringWriter.getBuffer().toString();
	}

	/**
	 * This method is the main entry point for command line usage.
	 *
	 * @param configuration
	 *            the configure to use
	 * @return number of violations found.
	 */
	public int doPMD(GargoylePMDConfiguration configuration) {
		return doPMD(configuration, (ReportListener) null);
	}

	public int doPMD(GargoylePMDConfiguration configuration, ReportListener... listeners) {
		return doPMD(configuration, Stream.of(listeners).collect(Collectors.toList()));

	}

	public int doPMD(GargoylePMDConfiguration configuration, List<ReportListener> listeners) {

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
			Renderer renderer = /*configuration.createRenderer();*/ new DatabaseXmlRenderer();
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

			if (listeners != null && !listeners.isEmpty()) {

				for (ReportListener l : listeners)
					ctx.getReport().addListener(l);

			}

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
			try {
				close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private Set<Language> getApplicableLanguages(GargoylePMDConfiguration configuration, RuleSets ruleSets) {
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
	public List<DataSource> getApplicableFiles(GargoylePMDConfiguration configuration, Set<Language> languages) {
		long startFiles = System.nanoTime();
		List<DataSource> files = internalGetApplicableFiles(configuration, languages);
		long endFiles = System.nanoTime();
		Benchmarker.mark(Benchmark.CollectFiles, endFiles - startFiles, 0);
		return files;
	}

	private List<DataSource> internalGetApplicableFiles(GargoylePMDConfiguration configuration, Set<Language> languages) {
		List<DataSource> files = new ArrayList<>();
		LanguageFilenameFilter fileSelector = new LanguageFilenameFilter(languages);

		if (null != configuration.getInputPaths()) {
			files.addAll(FileUtil.collectFiles(configuration.getInputPaths(), fileSelector));
		}

		if (null != configuration.getSourceText()) {

			String filePaths = "SourceBase";
			if (null != configuration.getSourceFileName()) {
				filePaths = configuration.getSourceFileName();
			}
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
	public void processFiles(final GargoylePMDConfiguration configuration, final RuleSetFactory ruleSetFactory,
			final List<DataSource> files, final RuleContext ctx, final List<Renderer> renderers) {

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
		pmdGargoyleThreadProcessor = new PMDGargoyleThreadProcessor(configuration);
		pmdGargoyleThreadProcessor.processFiles(ruleSetFactory, files, ctx, renderers);
		//			}

		if (configuration.getClassLoader() instanceof ClasspathClassLoader) {
			IOUtil.tryCloseClassLoader(configuration.getClassLoader());
		}
	}

	private void sortFiles(final GargoylePMDConfiguration configuration, final List<DataSource> files) {
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

	/* (non-Javadoc)
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		if (this.stringWriter != null)
			this.stringWriter.close();

	}

}
