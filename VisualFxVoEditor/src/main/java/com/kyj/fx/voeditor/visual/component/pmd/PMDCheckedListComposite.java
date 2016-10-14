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
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.IndexedCheckModel;
import org.fxmisc.richtext.Paragraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.kyj.fx.voeditor.visual.component.text.JavaTextArea;
import com.kyj.fx.voeditor.visual.component.text.JavaTextAreaForAutoComment;
import com.kyj.fx.voeditor.visual.component.text.MarkedLineNumberFactory;
import com.kyj.fx.voeditor.visual.component.text.MarkedLineNumberFactory.GraphicsMapper;
import com.kyj.fx.voeditor.visual.component.text.MarkedLineNumberFactory.LineMapper;
import com.kyj.fx.voeditor.visual.framework.pmd.DoPMD;
import com.kyj.fx.voeditor.visual.main.layout.CloseableParent;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.ListExpresion;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.util.Callback;
import kyj.Fx.dao.wizard.core.util.ValueUtil;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.ReportListener;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
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

	private JavaTextArea javaTextArea;

	private ListView<RuleViolation> lvViolation;
	private ObservableList<RuleViolation> violationList = FXCollections.observableArrayList();

	protected Label violationLabel;
	private CheckComboBox<RulePriority> checkComboBox;

	/**
	 * PMD 처리에 대한 코어 로직.
	 * @최초생성일 2016. 10. 13.
	 */
	protected DoPMD doPMD = new DoPMD();

	protected static final String REPORT_FILE_FORMAT = "xml";
	protected static final String VIOLATION_TEXT_FORMAT = "Violation : %d";

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
		BorderPane borderPane = new BorderPane(lvViolation);
		SplitPane splitPane = new SplitPane(javaTextArea, borderPane);
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
		borderPane.setTop(value);
		root.setCenter(splitPane);
		violationLabel = new Label();
		root.setBottom(violationLabel);

	}

	private void initSelectedRulePriorityValues(Consumer<RulePriority> action) {
		List<String> values = ResourceLoader.getInstance().getValues(ResourceLoader.PMD_SELECTED_PRIORITY_VALUES, ",");
		ListExpresion.of(values).map(v -> RulePriority.valueOf(v)).forEach(action);
	}

	/**
	 * PMD 검사를 동기 처리
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
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 13.
	 * @param file
	 */
	protected void simpleFilePmd(File file) {
		try {
			String sourceCode = Files.toString(this.sourceFile, Charset.forName("UTF-8"));
			PMDParameters params = new PMDParameters();
			params.setSourceFileName(file.getAbsolutePath());
			params.setSourceText(sourceCode);

			if (!FileUtil.isJavaFile(file)) {
				String fileExtension = FileUtil.getFileExtension(file);
				try {
					Field declaredField = PMDParameters.class.getDeclaredField("language");
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

			doPMD.doPMD(transformParametersIntoConfiguration(params), reportListenerProperty.get());
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

	public Label getViolationLabel() {
		return this.violationLabel;
	}

	public ListView<RuleViolation> getLvViolation() {
		return this.lvViolation;
	}

	protected void updateStatus(String sourceCode) {
		Platform.runLater(() -> {
			javaTextArea.setContent(sourceCode);
			violationLabel.setText(String.format(VIOLATION_TEXT_FORMAT, lvViolation.getItems().size()));
			javaTextArea.moveToLine(1);
			javaTextArea.clearSelection();
		});
	}

	protected void dirFilePmd(File file) {
		try {
			PMDParameters params = new PMDParameters();
			try {
				Field declaredField = PMDParameters.class.getDeclaredField("sourceDir");
				if (declaredField != null) {
					declaredField.setAccessible(true);
					declaredField.set(params, file.getAbsolutePath());
				}
			} catch (Exception e) {
				LOGGER.error(ValueUtil.toString(e));
			}

			//			if (!FileUtil.isJavaFile(file)) {
			//				String fileExtension = FileUtil.getFileExtension(file);
			//				try {
			//					Field declaredField = PMDParameters.class.getDeclaredField("language");
			//					if (declaredField != null) {
			//						declaredField.setAccessible(true);
			//						declaredField.set(params, fileExtension);
			//					}
			//				} catch (Exception e) {
			//					e.printStackTrace();
			//				}
			//			}

			//			transformParametersIntoConfiguration(params);
			long start = System.nanoTime();
			doPMD.doPMD(transformParametersIntoConfiguration(params), reportListenerProperty.get());
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

	/**
	 * 룰셋 파일 목록을 읽어옴.
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
					JavaTextArea javaTextArea = PMDCheckedListComposite.this.javaTextArea;

					//					if (PMDCheckedListComposite.this.sourceFile.isFile()) {
					//					javaTextArea.moveToLine(beginLine);
					//					}
					//디렉토리 선택의 경우만..
					if (sourceFile != null && sourceFile.isDirectory()) {
						String filename = item.getFilename();
						File file = new File(filename);

						File lastReadFile = lastSelectedFileProperty.get();

						if ((lastReadFile == null && file.exists()) || !(lastReadFile.equals(file))) {
							LOGGER.debug("Read File :: {}", file);
							javaTextArea.setContent(FileUtil.readFile(file, null));
							//							javaTextArea.moveToLine(beginLine);
							lastSelectedFileProperty.set(file);
						}
						javaTextArea.moveToLine(beginLine);
					} else {
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
				LOGGER.debug("{}\n rulesetName : {}\nruleName :{}\nLang:{}", ruleViolation.toString(), ruleSetName, name,
						language.toString());
				lvViolation.getItems().add(ruleViolation);
			});
			violationList.add(ruleViolation);

		}

		@Override
		public void metricAdded(Metric metric) {

		}

	};

	private ObjectProperty<ReportListener> reportListenerProperty = new SimpleObjectProperty<>(defaultReportListener);

	public final ReportListener getDefaultReportListener() {
		return defaultReportListener;
	}

	protected void setReportListener(ReportListener reportListener) {
		reportListenerProperty.set(reportListener);
	}

	/**
	 * 위험도 아이템 변경 리스너
	 * @최초생성일 2016. 10. 6.
	 */
	private ListChangeListener<RulePriority> chkPriorityChangeListener = (ListChangeListener<RulePriority>) c -> {
		if (c.next()) {
			lvViolation.getItems().clear();

			//선택된 필터정보
			ObservableList<? extends RulePriority> list = c.getList();

			violationList.stream().filter(v1 -> list.contains(v1.getRule().getPriority())).forEach(v2 -> {
				lvViolation.getItems().add(v2);
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

}
