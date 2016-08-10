/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2016. 7. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.scm;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

import com.kyj.fx.voeditor.visual.component.MasterSlaveChartComposite;
import com.kyj.fx.voeditor.visual.framework.model.GagoyleDate;
import com.kyj.fx.voeditor.visual.util.DateUtil;
import com.kyj.fx.voeditor.visual.util.FxCollectors;
import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import kyj.Fx.dao.wizard.core.util.ValueUtil;

/**
 *
 * SCM 커밋 이력정보를 차트화화여 분석.
 *
 * @author KYJ
 *
 */
public class ScmCommitComposite extends MasterSlaveChartComposite {
	private static final Logger LOGGER = LoggerFactory.getLogger(ScmCommitComposite.class);
	//	private JavaSVNManager manager;
	private FxSVNHistoryDataSupplier supplier;
	private static final String SERIES_LABEL = "커밋수";

	/**
	 * @throws Exception
	 */
	public ScmCommitComposite(FxSVNHistoryDataSupplier supplier) throws Exception {
		super();
		this.supplier = supplier;
		load();
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.MasterSlaveChartComposite#init()
	 */
	@Override
	public void init() {

	}

	public void load() {
		try {
			scmHistoryWalk();

			getBarChartDayOfMonthCategory().setLabel("기간별 커밋 통계");
			getLineChartDayOfWeekCategory().setLabel("요일별 커밋 통계");

			getBarChartDayOfMonthCategory().getCategories().add(SERIES_LABEL);
			getLineChartDayOfWeekCategory().getCategories().add(SERIES_LABEL);

			Color lineColor = Color.GREEN;
			double strokeWidth = 1;
			String cssStyleClassName = ".chart-series-line";

			{
				/*start Desing css.*/
				BarChart<String, Long> barChartDayOfMonth = getBarChartDayOfMonth();
				String style = "-fx-bar-fill: ".concat(FxUtil.toRGBCode(lineColor)).concat(";");
				for (Node n : barChartDayOfMonth.lookupAll(".default-color0.chart-bar")) {
					n.setStyle(style);
				}
				barChartDayOfMonth.setStyle(
						".chart-legend-item-syCmbol chart-bar series0 bar-legend-symbol default-color0{- fx-background-color:green;}");
						/*end Desing css.*/

				/*start Popover*/
				//무조건 1개의 시리즈만 처리하므로 인덱스에서 바로 가져옴.
				ObservableList<Data<String, Long>> dataArr = getBarChartDayOfMonth().getData().get(0).getData();

				for (Data<String, Long> d : dataArr) {
					d.getNode().setOnMouseClicked(ev -> {
						if (ev.getClickCount() == 1 && ev.getButton() == MouseButton.PRIMARY)
							createPopOver(d);
					});

				}
				barChartDayOfMonth.setMinWidth(BarChart.USE_COMPUTED_SIZE);
				barChartDayOfMonth.requestLayout();

				/*end Popover*/
			}
			{
				LineChart<String, Long> lineChartDayOfWeek = getLineChartDayOfWeek();
				Set<Node> lookupAll = lineChartDayOfWeek.lookupAll(".chart-line-symbol");
				StringBuffer sb = new StringBuffer();
				sb.append("-fx-background-color: " + FxUtil.toRGBCode(lineColor) + ", white;");
				sb.append(" -fx-background-insets: 0, 2;");
				sb.append("-fx-background-radius: 5px;");
				sb.append("-fx-padding: 5px;");
				for (Node n : lookupAll) {
					n.setStyle(sb.toString());
				}
				Node seriesLine = lineChartDayOfWeek.lookup(cssStyleClassName);
				String style = "-fx-stroke: " + FxUtil.toRGBCode(lineColor) + "; -fx-stroke-width: " + strokeWidth + ";";
				seriesLine.setStyle(style);
			}

		} catch (SVNException e) {
			LOGGER.error(ValueUtil.toString(e));
		}
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 19.
	 * @param n
	 */
	private void createPopOver(Data<String, Long> data) {
		String xValue = data.getXValue();

		//			Date parse = FxSVNHistoryDataSupplier.YYYYMMDD_EEE_PATTERN.parse(xValue);

		Collection<SVNLogEntry> list = supplier.getAllLogs();//supplier.list(parse);
		//			Stream<GargoyleSVNLogEntryPath> createStream = supplier.createStream(list);

		ObservableList<SVNLogEntry> collect = list.stream().filter(v -> {
			return xValue.equals(FxSVNHistoryDataSupplier.YYYYMMDD_EEE_PATTERN.format(v.getDate()));
		}).distinct().collect(FxCollectors.toObservableList());

		ListView<SVNLogEntry> createHistoryListView = supplier.createEntryListView(collect);

		BorderPane borderPane = new BorderPane(createHistoryListView);
		borderPane.setTop(new Label(xValue));
		borderPane.setBottom(new Label(String.valueOf(collect.size())));

		FxUtil.showPopOver(data.getNode(), borderPane);

	}

	private void scmHistoryWalk() throws SVNException {
		List<GagoyleDate> periodDaysByWeek = DateUtil.getPeriodDaysByWeek(supplier.getWeekSize());

		Collection<SVNLogEntry> allLogs = supplier.getAllLogs();
		//		supplier.createStream(allLogs);

		TreeMap<String, Long> dayOfMonths = allLogs.stream().collect(Collectors.groupingBy(
				v -> FxSVNHistoryDataSupplier.YYYYMMDD_EEE_PATTERN.format(v.getDate()), () -> new TreeMap<>(), Collectors.counting()));

		Map<String, Long> dayOfWeeks = new LinkedHashMap<>();

		//초기값 세팅. [중요한건 정렬순서를 유지해아하므로. 초기값을 넣어준것.]
		for (GagoyleDate d : DateUtil.getPeriodDaysByWeek()) {
			String eee = FxSVNHistoryDataSupplier.EEE_PATTERN.format(d.toDate());
			dayOfWeeks.put(eee, new Long(0));
		}
		//실제값 add
		dayOfWeeks.putAll(allLogs.stream()
				.collect(Collectors.groupingBy(v -> FxSVNHistoryDataSupplier.EEE_PATTERN.format(v.getDate()), Collectors.counting())));

		{
			BarChart<String, Long> barChartDayOfMonth = getBarChartDayOfMonth();

			ObservableList<Data<String, Long>> convert = convert(FxSVNHistoryDataSupplier.YYYYMMDD_EEE_PATTERN, periodDaysByWeek,
					dayOfMonths, true);
			Series<String, Long> series = new Series<>(SERIES_LABEL, convert);
			barChartDayOfMonth.getData().add(series);

		}

		{
			LineChart<String, Long> lineChartDayOfWeek = getLineChartDayOfWeek();
			ObservableList<Data<String, Long>> convert = convert(FxSVNHistoryDataSupplier.EEE_PATTERN, DateUtil.getPeriodDaysByWeek(),
					dayOfWeeks, false);
			Series<String, Long> series = new Series<>(SERIES_LABEL, convert);
			lineChartDayOfWeek.getData().add(series);

		}

	}

	private ObservableList<Data<String, Long>> convert(SimpleDateFormat format, List<GagoyleDate> dateList, Map<String, Long> appendedMap,
			boolean initAdd) {

		if (initAdd) {
			for (GagoyleDate d : dateList) {

				String key = format.format(d.toDate());
				if (!appendedMap.containsKey(key)) {
					appendedMap.put(key, 0L);
				}
			}
		}

		return appendedMap.entrySet().stream().map(ent -> {
			String key = ent.getKey();
			Long value = ent.getValue();
			return new XYChart.Data<>(key, value);
		}).collect(FxCollectors.toObservableList());

	}

	//	private Map<String, Long> createMapBy(List<GagoyleDate> periodList, SimpleDateFormat format) {
	//
	//		Supplier<TreeMap<String, Long>> mapFactory = TreeMap::new;
	//		Collector<GagoyleDate, ?, Long> mapping = Collectors.mapping(v -> v, Collectors.summingLong(value -> 0L));
	//		Collector<GagoyleDate, ?, TreeMap<String, Long>> groupingBy = Collectors.groupingBy(v -> format.format(v.toDate()), mapFactory,
	//				mapping);
	//		TreeMap<String, Long> collect = periodList.stream().collect(groupingBy);
	//		return collect;
	//
	//	}

	//	List<String> createNames(List<GagoyleDate> periodList, SimpleDateFormat format) {
	//		return periodList.stream().map(v -> format.format(v.toDate())).collect(Collectors.toList());
	//	}
}
