/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2016. 7. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.scm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Observable;
import java.util.Properties;
import java.util.TreeMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

import com.kyj.fx.voeditor.visual.component.MasterSlaveChartComposite;
import com.kyj.fx.voeditor.visual.framework.model.GagoyleDate;
import com.kyj.fx.voeditor.visual.util.DateUtil;
import com.kyj.scm.manager.svn.java.JavaSVNManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

/**
 * @author KYJ
 *
 */
public class ScmCommitComposite extends MasterSlaveChartComposite {

	private JavaSVNManager manager;
	private static final SimpleDateFormat YYYYMMDD_PATTERN = new SimpleDateFormat(DateUtil.SYSTEM_DATEFORMAT_YYYY_MM_DD);
	private static final SimpleDateFormat EEE_PATTERN = new SimpleDateFormat(DateUtil.SYSTEM_DATEFORMAT_EEE);
	private static final String SERIES_LABEL = "커밋수";

	/**
	 * @throws Exception
	 */
	public ScmCommitComposite(Properties properties) throws Exception {
		super();
		manager = new JavaSVNManager(properties);
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
		} catch (SVNException e) {
			e.printStackTrace();
		}
	}

	void scmHistoryWalk() throws SVNException {
		List<GagoyleDate> periodDaysByWeek = DateUtil.getPeriodDaysByWeek(3);
		System.out.println(periodDaysByWeek.size());
		GagoyleDate start = periodDaysByWeek.get(0);
		GagoyleDate end = periodDaysByWeek.get(periodDaysByWeek.size() - 1);

		//		Calendar instance = Calendar.getInstance();
		//		instance.set(2016, 6, 1);
		//		Date time = instance.getTime();

		long startRevision = manager.getRevision(start.toDate());
		long endRevision = manager.getRevision(end.toDate());

		//		System.out.println("start " + start.toDateString() + " end : " + end.toDateString());
		//		System.out.println("startRevision " + startRevision + " endRevision : " + endRevision);
		Collection<SVNLogEntry> allLogs = manager.getAllLogs(startRevision, endRevision);
		//		allLogs.

		Collector<SVNLogEntry, ?, Long> mapping = Collectors.mapping(v -> v, Collectors.counting());
		//		() -> createMapBy(periodDaysByWeek, YYYYMMDD_PATTERN)
		LinkedHashMap<String, Long> dayOfMonths = allLogs.stream()
				.collect(Collectors.groupingBy(v -> YYYYMMDD_PATTERN.format(v.getDate()), LinkedHashMap::new, mapping));

		//		dayOfMonths.entrySet().stream().map(v -> {
		//
		//		})

		LinkedHashMap<String, Long> dayOfWeeks = allLogs.stream()
				.collect(Collectors.groupingBy(v -> EEE_PATTERN.format(v.getDate()), LinkedHashMap::new, mapping));

		//		getBarChartDayOfMonthCategory().getCategories().addAll(dayOfMonths.keySet());
		//		getLineChartDayOfWeekCategory().getCategories().addAll(dayOfWeeks.keySet());

		{
			BarChart<String, Long> barChartDayOfMonth = getBarChartDayOfMonth();

			ObservableList<Data<String, Long>> convert = convert(YYYYMMDD_PATTERN, periodDaysByWeek, dayOfMonths);
			Series<String, Long> series = new Series<>(SERIES_LABEL, convert);
			barChartDayOfMonth.getData().add(series);

			//			series.getData().addAll(convert);
		}

		{
			LineChart<String, Long> lineChartDayOfWeek = getLineChartDayOfWeek();
			ObservableList<Data<String, Long>> convert = convert(EEE_PATTERN, DateUtil.getPeriodDaysByWeek(), dayOfWeeks);
			Series<String, Long> series = new Series<>(SERIES_LABEL, convert);
			lineChartDayOfWeek.getData().add(series);

			//			series.getData().addAll(convert);
		}

	}

	ObservableList<Data<String, Long>> convert(SimpleDateFormat format, List<GagoyleDate> dateList,
			LinkedHashMap<String, Long> appendedMap) {

		for (GagoyleDate d : dateList) {
			String key = format.format(d.toDate());
			if (!appendedMap.containsKey(key)) {
				System.out.println("append : " + key);
				appendedMap.put(key, 0L);
			}

		}

		return appendedMap.entrySet().stream().map(ent -> {

			String key = ent.getKey();
			Long value = ent.getValue();

			return new XYChart.Data<>(key, value);
		}).collect(() -> FXCollections.observableArrayList(), (a, b) -> {

			int g = a.size() / 7;
			b.setXValue("[" + g + "]    ".concat(b.getXValue()));
			a.add(b);
		} , (a, b) -> a.addAll(b));

	}

	TreeMap<String, Long> createMapBy(List<GagoyleDate> periodList, SimpleDateFormat format) {

		TreeMap<String, Long> collect = periodList.stream().collect(Collectors.groupingBy(v -> format.format(v.toDate()), TreeMap::new,
				Collectors.mapping(v -> v, Collectors.summingLong(value -> 0L))));

		return collect;

	}

	//	List<String> createNames(List<GagoyleDate> periodList, SimpleDateFormat format) {
	//		return periodList.stream().map(v -> format.format(v.toDate())).collect(Collectors.toList());
	//	}

}
