/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.pmd
 *	작성일   : 2016. 10. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.pmd;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.IndexedCheckModel;
import org.fxmisc.richtext.Paragraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.kyj.fx.voeditor.visual.component.pmd.chart.AbstractPMDViolationBarChartComposite;
import com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationbyBarChartComposite;
import com.kyj.fx.voeditor.visual.component.text.JavaTextArea;
import com.kyj.fx.voeditor.visual.component.text.JavaTextAreaForAutoComment;
import com.kyj.fx.voeditor.visual.component.text.MarkedLineNumberFactory;
import com.kyj.fx.voeditor.visual.component.text.MarkedLineNumberFactory.GraphicsMapper;
import com.kyj.fx.voeditor.visual.component.text.MarkedLineNumberFactory.LineMapper;
import com.kyj.fx.voeditor.visual.framework.pmd.DoPMD;
import com.kyj.fx.voeditor.visual.framework.pmd.GargoylePMDConfiguration;
import com.kyj.fx.voeditor.visual.framework.pmd.GargoylePMDParameters;
import com.kyj.fx.voeditor.visual.main.layout.CloseableParent;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.ListExpresion;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;
import net.sourceforge.pmd.ReportListener;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.benchmark.Benchmark;
import net.sourceforge.pmd.benchmark.Benchmarker;
import net.sourceforge.pmd.benchmark.TextReport;
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
	 * @Deprecated RULESETS_PROPERTIES_FILE_FORMAT 상수에 있는 rulesets.properties파일의 기술내용을 참조하여 기술할것.
	 * @최초생성일 2016. 10. 6.
	 */
	@Deprecated
	private static final String RULESETS_FILE_FORMAT = "rulesets/%s/basic.xml";

	private static final String RULESETS_PROPERTIES_FILE_FORMAT = "rulesets/%s/rulesets.properties";

	/**
	 * @param parent
	 */
	//	public PMDCheckedListComposite(BorderPane parent) {
	//		super(parent);
	//	}

	private static final Logger LOGGER = LoggerFactory.getLogger(PMDCheckedListComposite.class);

	private File sourceFile;

	private SplitPane splitPane;
	private JavaTextArea javaTextArea;

	private BorderPane borLvViolationRoot;
	private ListView<RuleViolation> lvViolation;
	private ObservableList<RuleViolation> violationList = FXCollections.observableArrayList();

	protected TextFlow violationLabel;
	private CheckComboBox<RulePriority> checkComboBox;

	/**
	 * PMD 처리에 대한 코어 로직.
	 *
	 * @최초생성일 2016. 10. 13.
	 */
	protected DoPMD doPMD = new DoPMD();

	protected static final String REPORT_FILE_FORMAT = "xml";

	AtomicInteger priorTotal = new AtomicInteger(0);
	AtomicInteger priorHigh = new AtomicInteger(0);
	AtomicInteger priorMediumHigh = new AtomicInteger(0);
	AtomicInteger priorMedium = new AtomicInteger(0);
	AtomicInteger priorEtc = new AtomicInteger(0);

	private AbstractPMDViolationBarChartComposite barchart;

	/**
	 * @param sourceFile
	 */
	public PMDCheckedListComposite(File sourceFile) {
		this(new BorderPane(), sourceFile);

	}

	/**
	 * @param root
	 * @param sourceFile
	 */
	public PMDCheckedListComposite(BorderPane root, File sourceFile) {
		super(root);
		this.sourceFile = sourceFile;
		barchart = new PMDViolationbyBarChartComposite() {

			/* (non-Javadoc)
			 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationChartVisualable#ruleViolationFilter()
			 */
			@Override
			public Predicate<RuleViolation> ruleViolationFilter() {

				return ruleViolation -> {
					ObservableList<RulePriority> checkedItems = checkComboBox.getCheckModel().getCheckedItems();
					Rule rule = ruleViolation.getRule();
					RulePriority priority = rule.getPriority();
					return checkedItems.stream().filter(c -> c == priority).findFirst().isPresent();

				};
			}

			/* (non-Javadoc)
			 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationbyBarChartComposite#accept(javafx.scene.chart.PieChart.Data, javafx.scene.Node)
			 */
			@Override
			public void chartGraphicsCustomAction(Data t, Node u) {
				super.chartGraphicsCustomAction(t, u);

				u.setOnMouseEntered(ev -> {
					u.setCursor(Cursor.HAND);
				});

				u.setOnMouseExited(ev -> {
					u.setCursor(Cursor.DEFAULT);
				});

				u.setOnMouseClicked(ev -> {

					ObservableList<RuleViolation> items = PMDCheckedListComposite.this.lvViolation.getItems();
					List<RuleViolation> collect = PMDCheckedListComposite.this.violationList.stream().filter(ruleViolationFilter())
							.filter(v -> ValueUtil.equals(t.getName(), ValueUtil.getSimpleFileName(v.getFilename())))
							.collect(Collectors.toList());
					items.setAll(collect);

				});

			}

			@Override
			public void seriesLegendLabelCustomAction(Data data, Node node) {
				super.seriesLegendLabelCustomAction(data, node);

				node.setOnMouseClicked(ev -> {

					ObservableList<RuleViolation> items = PMDCheckedListComposite.this.lvViolation.getItems();
					List<RuleViolation> collect = PMDCheckedListComposite.this.violationList.stream().filter(ruleViolationFilter())
							.filter(v -> ValueUtil.equals(data.getName(), ValueUtil.getSimpleFileName(v.getFilename())))
							.collect(Collectors.toList());
					items.setAll(collect);
				});
			}

		};

		javaTextArea = new JavaTextAreaForAutoComment() {

			/* (non-Javadoc)
			 * @see com.kyj.fx.voeditor.visual.component.text.JavaTextAreaForAutoComment#getLineFactory()
			 */
			@Override
			public IntFunction<Node> getLineFactory() {
				MarkedLineNumberFactory lineFactory = (MarkedLineNumberFactory) super.getLineFactory();

				//PMD에서 오류라고 측정한 값을 마킹하기 위해 값의 속성값을 리턴
				lineFactory.setLineMarkFactory(new LineMapper<Integer>() {

					@Override
					public Integer map(int row, Paragraph<?> pra) {

						Optional<RuleViolation> findFirst = violationList.stream().filter(v -> v.getBeginLine() == row).findFirst();
						if (findFirst.isPresent()) {
							RuleViolation ruleViolation = findFirst.get();

							RulePriority priority = ruleViolation.getRule().getPriority();
							//							LOGGER.debug("violation toString : {} ", priority.toString());
							return priority.getPriority();
						}
						return 0;
					}

				});

				//PMD에서 오류라고 측정한 값을 UI에 마킹하기 위해 체크.
				lineFactory.setGraphicsMapperFactory(new GraphicsMapper<Node>() {

					@Override
					public Node map(int row, Paragraph<?> pra, int typeValue) {

						Circle g = new Circle(5d);
						//						Rectangle rectangle = new Rectangle();
						//						rectangle.setWidth(10d);
						//						rectangle.setHeight(10d);
						g.setStyle("-fx-fill:transparent");
						if (typeValue >= 1) {

							ObservableList<RulePriority> checkedItems = checkComboBox.getCheckModel().getCheckedItems();
							checkedItems.stream().filter(c -> {
								return c.getPriority() == typeValue;
							}).findFirst().ifPresent(v -> {
								//								LOGGER.debug("priority value : {} ", typeValue);
								g.setStyle(PMDListCell.getPriorityStyle(typeValue));
							});
						}
						return g;
					}
				});
				return lineFactory;
			}

		};

		lvViolation = new ListView<>();

		lvViolation.setCellFactory(ruleCheckListener);

		borLvViolationRoot = new BorderPane(lvViolation);
		splitPane = new SplitPane(javaTextArea, borLvViolationRoot);
		splitPane.setOrientation(Orientation.VERTICAL);
		splitPane.setDividerPositions(0.7d, 0.3d);

		checkComboBox = new CheckComboBox<>();
		checkComboBox.getItems().addAll(RulePriority.values());

		IndexedCheckModel<RulePriority> checkModel = checkComboBox.getCheckModel();

		//초기값 선택.
		initSelectedRulePriorityValues(v -> checkModel.check(v));
		checkComboBox.getCheckModel().getCheckedItems().addListener(chkPriorityChangeListener);

		//		checkComboBox.
		HBox value = new HBox(5, new Label("위험도 : "), checkComboBox);
		value.setAlignment(Pos.CENTER_RIGHT);
		//		value.setStyle("-fx-background-color:transparent;");
		value.setPadding(new Insets(5));
		borLvViolationRoot.setTop(value);
		root.setCenter(splitPane);
		violationLabel = new TextFlow();

		root.setBottom(violationLabel);

	}

	private void initSelectedRulePriorityValues(Consumer<RulePriority> action) {
		List<String> values = ResourceLoader.getInstance().getValues(ResourceLoader.PMD_SELECTED_PRIORITY_VALUES, ",");
		ListExpresion.of(values).map(v -> RulePriority.valueOf(v)).forEach(action);
	}

	/**
	 * PMD 검사를 동기 처리
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 13.
	 */
	public void run() {
		if (this.sourceFile.isDirectory()) {
			dirFilePmd(this.sourceFile);
		} else {
			simpleFilePmd(this.sourceFile);
		}
	}

	/**
	 * PMD 검사를 비동기 처리
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 13.
	 */
	public void runAsynch() {

		new Thread(() -> {
			if (this.sourceFile.isDirectory()) {
				dirFilePmd(this.sourceFile);
			} else {
				simpleFilePmd(this.sourceFile);
			}
		}).start();

	}

	/**
	 * 파일 1개를 대상으로 PMD 체크하기 위한 처리.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 13.
	 * @param file
	 */
	protected void simpleFilePmd(File file) {
		try {
			String sourceCode = Files.toString(this.sourceFile, Charset.forName("UTF-8"));
			GargoylePMDParameters params = new GargoylePMDParameters();
			params.setSourceFileName(file.getAbsolutePath());
			params.setSourceText(sourceCode);

			if (!FileUtil.isJavaFile(file)) {
				String fileExtension = FileUtil.getFileExtension(file);
				try {
					Field declaredField = GargoylePMDParameters.class.getDeclaredField("language");
					if (declaredField != null) {
						declaredField.setAccessible(true);
						declaredField.set(params, fileExtension);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			//			transformParametersIntoConfiguration(params);
			long start = System.nanoTime();

			doPMD.doPMD(transformParametersIntoConfiguration(params), reportListenerProperty.get(), violationCountingListener.get());
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

	public TextFlow getViolationLabel() {
		return this.violationLabel;
	}

	public ListView<RuleViolation> getLvViolation() {
		return this.lvViolation;
	}

	protected void updateStatus(String sourceCode) {
		Platform.runLater(() -> {
			javaTextArea.setContent(sourceCode);

			updateViolationLabel();
			javaTextArea.moveToLine(1);
			javaTextArea.clearSelection();
		});
	}

	private void updateViolationLabel() {

		String VIOLATION_TEXT_FORMAT_PREFIIX = "Violation ";
		String VIOLATION_TEXT_FORMAT_TOTAL = "Total : %d ";
		String VIOLATION_TEXT_FORMAT_HIGH = "High : %d ";
		String VIOLATION_TEXT_FORMAT_MEDIUM_HIGH = "Medium High : %d ";
		String VIOLATION_TEXT_FORMAT_MEDIUM = "Medium : %d ";
		String VIOLATION_TEXT_FORMAT_ETC = "Etc : %d ";

		Text priffix = new Text(VIOLATION_TEXT_FORMAT_PREFIIX);
		priffix.setStyle("-fx-font-size:11pt; -fx-font-style:italic;");
		Text total = new Text(String.format(VIOLATION_TEXT_FORMAT_TOTAL, priorTotal.get()));
		Text high = new Text(String.format(VIOLATION_TEXT_FORMAT_HIGH, priorHigh.get()));
		high.setStyle(PMDListCell.getPriorityStyle(RulePriority.HIGH));
		Text mediumHigh = new Text(String.format(VIOLATION_TEXT_FORMAT_MEDIUM_HIGH, priorMediumHigh.get()));
		mediumHigh.setStyle(PMDListCell.getPriorityStyle(RulePriority.MEDIUM_HIGH));
		Text medium = new Text(String.format(VIOLATION_TEXT_FORMAT_MEDIUM, priorMedium.get()));
		medium.setStyle(PMDListCell.getPriorityStyle(RulePriority.MEDIUM));
		Text etc = new Text(String.format(VIOLATION_TEXT_FORMAT_ETC, priorEtc.get()));

		//		Text element = new Text(String.format(VIOLATION_TEXT_FORMAT, priorTotal.get(), priorHigh.get(), priorMediumHigh.get(),
		//				priorMedium.get(), priorEtc.get()));

		violationLabel.getChildren().add(priffix);
		violationLabel.getChildren().add(total);
		violationLabel.getChildren().add(high);
		violationLabel.getChildren().add(mediumHigh);
		violationLabel.getChildren().add(medium);
		violationLabel.getChildren().add(etc);

	}

	protected void dirFilePmd(File file) {
		try {
			GargoylePMDParameters params = new GargoylePMDParameters();

			splitPane.getItems().set(0, barchart);

			try {
				Field declaredField = GargoylePMDParameters.class.getDeclaredField("sourceDir");
				if (declaredField != null) {
					declaredField.setAccessible(true);
					declaredField.set(params, file.getAbsolutePath());
				}
			} catch (Exception e) {
				LOGGER.error(ValueUtil.toString(e));
			}

			long start = System.nanoTime();
			doPMD.doPMD(transformParametersIntoConfiguration(params), reportListenerProperty.get(), violationCountingListener.get(),
					barchart.getReportListener());
			long end = System.nanoTime();
			Benchmarker.mark(Benchmark.TotalPMD, end - start, 0);

			TextReport report = new TextReport();

			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				report.generate(Benchmarker.values(), new PrintStream(out));
				out.flush();
				LOGGER.debug(out.toString("UTF-8"));
			}

			Platform.runLater(() -> {
				updateViolationLabel();
				barchart.build();
			});

		} catch (IOException e) {
			LOGGER.error(ValueUtil.toString(e));
		}
	}

	/**
	 * 룰셋 파일 목록을 읽어옴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 13.
	 * @param language
	 * @return
	 */
	protected String getRuleSets(String language) {
		String result = "";
		try {
			File file = new File(String.format(RULESETS_PROPERTIES_FILE_FORMAT, "java"));
			Properties properties = new Properties();
			properties.load(new FileInputStream(file));
			Object object = properties.get("rulesets.filenames");
			result = object == null ? null : object.toString();
		} catch (IOException e) {

		}
		if (result == null)
			result = new File(String.format(RULESETS_FILE_FORMAT, language)).getAbsolutePath();

		return result;
	}

	public GargoylePMDConfiguration transformParametersIntoConfiguration(GargoylePMDParameters params) {
		//		if (null == params.getSourceDir() && null == params.getUri() && null == params.getFileListPath()
		//				&& null == params.getSourceText()) {
		//			throw new IllegalArgumentException(
		//					"Please provide a parameter for source root directory (-dir or -d), database URI (-uri or -u), or file list path (-filelist).");
		//		}
		GargoylePMDConfiguration configuration = new GargoylePMDConfiguration();
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

		String language = params.getLanguage();
		//		String RESULTSET = new File(String.format(RULESETS_FILE_FORMAT, "java")).getAbsolutePath();
		//		if (null != language) {
		//			RESULTSET = new File(String.format(RULESETS_FILE_FORMAT, language)).getAbsolutePath();
		//		}

		configuration.setRuleSets(getRuleSets(language));
		//		configuration.setRuleSetFactoryCompatibilityEnabled(true);
		configuration.setShowSuppressedViolations(params.isShowsuppressed());
		configuration.setSourceEncoding("UTF-8");
		//	        configuration.setStressTest(params.isStress());
		//		configuration.setSuppressMarker(params.getSuppressmarker());
		configuration.setThreads(1);
		//		configuration.setFailOnViolation(params.isFailOnViolation());

		LanguageVersion languageVersion = LanguageRegistry.findLanguageVersionByTerseName(language + " " + params.getVersion());
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
					int endLine = item.getEndLine();
					int beginColumn = item.getBeginColumn();
					int endColumn = item.getEndColumn();
					LOGGER.debug("pmd meta violation index : begineLine : {} , endLine : {} , begine col : {} , end col : {}", beginLine,
							endLine, beginColumn, endColumn);

					//					if (PMDCheckedListComposite.this.sourceFile.isFile()) {
					//					javaTextArea.moveToLine(beginLine);
					//					}
					//디렉토리 선택의 경우만..
					if (sourceFile != null && sourceFile.isDirectory()) {
						JavaTextArea javaTextArea = new JavaTextArea();
						String filename = item.getFilename();
						File file = new File(filename);

						//						File lastReadFile = lastSelectedFileProperty.get();

						String readFile = FileUtil.readFile(file, true, null);

						//						if ((lastReadFile == null && file.exists()) || !(lastReadFile.equals(file))) {

						javaTextArea.setContent(readFile);
						//							javaTextArea.moveToLine(beginLine);
						lastSelectedFileProperty.set(file);
						//						}

						SharedMemory.getSystemLayoutViewController().loadNewSystemTab(file.getName(), javaTextArea);
						javaTextArea.moveToLine(beginLine);

					} else {
						JavaTextArea javaTextArea = PMDCheckedListComposite.this.javaTextArea;
						javaTextArea.moveToLine(beginLine);
					}
				}

			});
			return pmdListCell;
		}
	};

	private final ReportListener defaultReportListener = new ReportListener() {

		@Override
		public void ruleViolationAdded(RuleViolation ruleViolation) {

			Rule rule = ruleViolation.getRule();
			RulePriority priority = rule.getPriority();
			String name = rule.getName();
			String ruleSetName = rule.getRuleSetName();
			Language language = rule.getLanguage();

			ObservableList<RulePriority> checkedItems = checkComboBox.getCheckModel().getCheckedItems();
			checkedItems.stream().filter(c -> c == priority).findFirst().ifPresent(v -> {
				//				LOGGER.debug("{}\n rulesetName : {}\nruleName :{}\nLang:{}", ruleViolation.toString(), ruleSetName, name,
				//						language.toString());

				Platform.runLater(() -> lvViolation.getItems().add(ruleViolation));

			});
			violationList.add(ruleViolation);

		}

		@Override
		public void metricAdded(Metric metric) {

		}

	};

	/**
	 * 위반 내용에대한 counting 리스너
	 * 
	 * @최초생성일 2016. 10. 18.
	 */
	private final ReportListener defaultViolationCountingListener = new ReportListener() {

		@Override
		public void ruleViolationAdded(RuleViolation r) {

			switch (r.getRule().getPriority()) {
			case HIGH:
				priorHigh.incrementAndGet();
				break;

			case MEDIUM_HIGH:

				priorMediumHigh.incrementAndGet();
				break;

			case MEDIUM:
				priorMedium.incrementAndGet();
				break;

			default:
				priorEtc.incrementAndGet();
				break;
			}
			priorTotal.incrementAndGet();
		}

		@Override
		public void metricAdded(Metric arg0) {
			// TODO Auto-generated method stub

		}
	};

	private ObjectProperty<ReportListener> violationCountingListener = new SimpleObjectProperty<ReportListener>(
			defaultViolationCountingListener);

	private ObjectProperty<ReportListener> reportListenerProperty = new SimpleObjectProperty<>(defaultReportListener);

	public final ReportListener getDefaultReportListener() {
		return defaultReportListener;
	}

	protected void setReportListener(ReportListener reportListener) {
		reportListenerProperty.set(reportListener);
	}

	/**
	 * 위험도 아이템 변경 리스너
	 *
	 * @최초생성일 2016. 10. 6.
	 */
	private ListChangeListener<RulePriority> chkPriorityChangeListener = (ListChangeListener<RulePriority>) c -> {
		if (c.next()) {
			lvViolation.getItems().clear();

			//선택된 필터정보
			ObservableList<? extends RulePriority> list = c.getList();

			barchart.clean();

			violationList.stream().filter(v1 -> list.contains(v1.getRule().getPriority())).forEach(v2 -> {
				lvViolation.getItems().add(v2);
				barchart.violationAdapter().ruleViolationAdded(v2);
			});

			//			Integer currentLine = javaTextArea.getCurrentLine() + 1;

			//			javaTextArea.lineStart(SelectionPolicy.CLEAR);
			//			javaTextArea.getCodeArea();

			//			javaTextArea.appendContent("");
			//			javaTextArea.getCodeArea().getParagraphs().forEach(s -> javaTextArea.getCodeArea().clearStyle(s));
			ObservableList<Paragraph<Collection<String>>> paragraphs = javaTextArea.getCodeArea().getParagraphs();
			int t = paragraphs.size();
			IntStream.range(0, t).forEach(v -> {
				javaTextArea.getCodeArea().clearStyle(v);
			});

			barchart.build();
			//			javaTextArea.moveToLine(currentLine);
		}
	};

	/* (non-Javadoc)
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {

		ObservableList<RulePriority> checkedItems = checkComboBox.getCheckModel().getCheckedItems();
		checkedItems.stream().map(v -> v.name()).reduce((t, u) -> t.concat(",").concat(u)).ifPresent(v -> {
			ResourceLoader.getInstance().put(ResourceLoader.PMD_SELECTED_PRIORITY_VALUES, v);
		});

		doPMD.close();
	}

	public final ObjectProperty<ReportListener> reportListenerPropertyProperty() {
		return this.reportListenerProperty;
	}

	public final net.sourceforge.pmd.ReportListener getReportListenerProperty() {
		return this.reportListenerPropertyProperty().get();
	}

	public final void setReportListenerProperty(final net.sourceforge.pmd.ReportListener reportListenerProperty) {
		this.reportListenerPropertyProperty().set(reportListenerProperty);
	}

	public final ObjectProperty<ReportListener> violationCountingListenerProperty() {
		return this.violationCountingListener;
	}

	public final net.sourceforge.pmd.ReportListener getViolationCountingListener() {
		return this.violationCountingListenerProperty().get();
	}

	public final void setViolationCountingListener(final net.sourceforge.pmd.ReportListener violationCountingListener) {
		this.violationCountingListenerProperty().set(violationCountingListener);
	}

}
